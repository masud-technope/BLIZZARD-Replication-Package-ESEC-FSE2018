/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.generic;

import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.events.*;
import org.eclipse.ecf.core.sharedobject.security.ISharedObjectPolicy;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.sharedobject.util.ISharedObjectMessageSerializer;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.comm.*;
import org.eclipse.ecf.provider.generic.ContainerMessage.SharedObjectMessage;
import org.eclipse.ecf.provider.generic.gmm.Member;
import org.eclipse.ecf.provider.util.*;

public abstract class SOContainer extends AbstractContainer implements ISharedObjectContainer {

    class LoadingSharedObject implements ISharedObject {

        final ReplicaSharedObjectDescription description;

        private Thread runner = null;

        ID fromID = null;

         LoadingSharedObject(ID fromID, ReplicaSharedObjectDescription sd) {
            this.fromID = fromID;
            this.description = sd;
        }

        public void dispose(ID containerID) {
        // nothing to do
        }

        public Object getAdapter(Class clazz) {
            return null;
        }

        ID getHomeID() {
            final ID homeID = description.getHomeID();
            if (homeID == null)
                return getID();
            return homeID;
        }

        ID getID() {
            return description.getID();
        }

        public void handleEvent(Event event) {
        // nothing to do
        }

        public void handleEvents(Event[] events) {
        // nothing to do
        }

        /**
		 * @param initData
		 * @throws SharedObjectInitException not thrown in this implementation.
		 */
        public void init(ISharedObjectConfig initData) throws SharedObjectInitException {
        // nothing to do
        }

        void start() {
            if (runner == null) {
                runner = (Thread) AccessController.doPrivileged(new PrivilegedAction() {

                    public Object run() {
                        return new Thread(loadingThreadGroup, new Runnable() {

                            public void run() {
                                try {
                                    if (Thread.currentThread().isInterrupted() || isClosing())
                                        throw new InterruptedException("loading interrupted for " + getID().getName());
                                    // First load object
                                    final ISharedObject obj = load(description);
                                    // Create wrapper object and
                                    // move from loading to
                                    // active
                                    // list.
                                    final SOWrapper wrap = createRemoteSharedObjectWrapper(fromID, description, obj);
                                    wrap.init();
                                    // throw
                                    if (Thread.currentThread().isInterrupted() || isClosing())
                                        throw new InterruptedException("loading interrupted for " + getID().getName());
                                    // Finally, we move from
                                    // loading to active, and
                                    // then the
                                    // object is done
                                    SOContainer.this.moveFromLoadingToActive(wrap);
                                } catch (final Exception e) {
                                    traceStack("Exception loading:" + description, e);
                                    SOContainer.this.removeFromLoading(getID());
                                    try {
                                        sendCreateResponse(getHomeID(), getID(), e, description.getIdentifier());
                                    } catch (final Exception e1) {
                                        traceStack("Exception sending create response from LoadingSharedObject.run:" + description, e1);
                                    }
                                } catch (final NoClassDefFoundError e) {
                                    traceStack("Exception loading:" + description, e);
                                    SOContainer.this.removeFromLoading(getID());
                                    try {
                                        sendCreateResponse(getHomeID(), getID(), e, description.getIdentifier());
                                    } catch (final Exception e1) {
                                        traceStack("Exception sending create response from LoadingSharedObject.run:" + description, e1);
                                    }
                                }
                            }
                        }, //$NON-NLS-1$
                        getID().getName() + //$NON-NLS-1$
                        ":loading");
                    }
                });
                runner.setDaemon(true);
                runner.start();
            }
        }
    }

    //$NON-NLS-1$
    public static final String DEFAULT_OBJECT_ARG_KEY = SOContainer.class.getName() + ".sharedobjectargs";

    //$NON-NLS-1$
    public static final String DEFAULT_OBJECT_ARGTYPES_KEY = SOContainer.class.getName() + ".sharedobjectargtypes";

    private long sequenceNumber = 0L;

    protected ISharedObjectContainerConfig config = null;

    protected SOContainerGMM groupManager = null;

    protected boolean isClosing = false;

    protected ThreadGroup loadingThreadGroup = null;

    protected SOManager sharedObjectManager = null;

    protected ISharedObjectPolicy policy = null;

    protected ThreadGroup sharedObjectThreadGroup = null;

    /**
	 * @since 2.0
	 */
    protected ISharedObjectMessageSerializer sharedObjectMessageSerializer = new ISharedObjectMessageSerializer() {

        public Object deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
            return defaultDeserializeSharedObjectMessage(data);
        }

        public byte[] serializeMessage(ID sharedObjectId, Object message) throws IOException {
            return defaultSerializeSharedObjectMessage(sharedObjectId, message);
        }
    };

    /**
	 * @since 2.0
	 */
    public void setSharedObjectMessageSerializer(ISharedObjectMessageSerializer serializer) {
        if (serializer == null)
            return;
        this.sharedObjectMessageSerializer = serializer;
    }

    /**
	 * @return ISharedObjectMessageSerializer the shared object message serializer
	 * @since 2.0
	 */
    protected ISharedObjectMessageSerializer getSharedObjectMessageSerializer() {
        return this.sharedObjectMessageSerializer;
    }

    protected ISynchAsynchEventHandler receiver = new ISynchAsynchEventHandler() {

        public Object handleSynchEvent(SynchEvent event) throws IOException {
            return processSynch(event);
        }

        public ID getEventHandlerID() {
            return getID();
        }

        public void handleConnectEvent(ConnectionEvent event) {
        // nothing to do
        }

        public void handleDisconnectEvent(DisconnectEvent event) {
            processDisconnect(event);
        }

        public void handleAsynchEvent(AsynchEvent event) throws IOException {
            processAsynch(event);
        }
    };

    public  SOContainer(ISharedObjectContainerConfig config) {
        //$NON-NLS-1$
        Assert.isNotNull(config, "container config cannot be null");
        this.config = config;
        groupManager = new SOContainerGMM(this, new Member(config.getID()));
        sharedObjectManager = new SOManager(this);
        //$NON-NLS-1$
        loadingThreadGroup = new ThreadGroup(getID() + ":loading");
        //$NON-NLS-1$
        sharedObjectThreadGroup = new ThreadGroup(getID() + ":SOs");
    }

    // Implementation of IIdentifiable
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return config.getID();
    }

    // Implementation of IContainer
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#connect(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.security.IConnectContext)
	 */
    public abstract void connect(ID groupID, IConnectContext connectContext) throws ContainerConnectException;

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#getGroupID()
	 */
    public abstract ID getConnectedID();

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#leaveGroup()
	 */
    public abstract void disconnect();

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
    public Namespace getConnectNamespace() {
        // We expect StringIDs for the generic server
        return IDFactory.getDefault().getNamespaceByName(ProviderPlugin.getDefault().getNamespaceIdentifier());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#dispose(long)
	 */
    public void dispose() {
        isClosing = true;
        // Clear group manager
        if (groupManager != null)
            groupManager.removeAllMembers();
        // Clear shared object manager
        if (sharedObjectManager != null) {
            sharedObjectManager.dispose();
            sharedObjectManager = null;
        }
        if (loadingThreadGroup != null) {
            loadingThreadGroup.interrupt();
            loadingThreadGroup = null;
        }
        super.dispose();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        final IAdapterManager adapterManager = ProviderPlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    // Impl of ISharedObjectContainer
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#getSharedObjectManager()
	 */
    public ISharedObjectManager getSharedObjectManager() {
        return sharedObjectManager;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#getGroupMemberIDs()
	 */
    public ID[] getGroupMemberIDs() {
        return groupManager.getMemberIDs();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#getConfig()
	 */
    public ISharedObjectContainerConfig getConfig() {
        return config;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainer#isGroupManager()
	 */
    public abstract boolean isGroupManager();

    // End of ISharedObjectContainer
    protected void setRemoteAddPolicy(ISharedObjectPolicy policy) {
        synchronized (getGroupMembershipLock()) {
            this.policy = policy;
        }
    }

    protected boolean addNewRemoteMember(ID memberID, Object data) {
        return groupManager.addMember(new Member(memberID, data));
    }

    protected ISharedObjectContainerTransaction addSharedObject0(ID id, ISharedObject s, Map props) throws Exception {
        return addSharedObjectWrapper(createSharedObjectWrapper(id, s, props));
    }

    protected void addSharedObjectAndWait(ID id, ISharedObject s, Map properties) throws Exception {
        if (id == null || s == null)
            //$NON-NLS-1$
            throw new SharedObjectAddException("shared object or id cannot be null");
        final ISharedObjectContainerTransaction t = addSharedObject0(id, s, properties);
        // Wait right here until committed
        if (t != null)
            t.waitToCommit();
    }

    protected ISharedObjectContainerTransaction addSharedObjectWrapper(SOWrapper wrapper) throws Exception {
        if (wrapper == null)
            return null;
        final ID id = wrapper.getObjID();
        ISharedObjectContainerTransaction transaction = null;
        synchronized (getGroupMembershipLock()) {
            final Object obj = groupManager.getFromAny(id);
            if (obj != null)
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new SharedObjectAddException("shared object id=" + id.getName() + " already in container");
            // Call initialize. If this throws it halts everything
            wrapper.init();
            // Call getAdapter(ISharedObjectContainerTransaction)
            transaction = (ISharedObjectContainerTransaction) wrapper.sharedObject.getAdapter(ISharedObjectContainerTransaction.class);
            // Put in table
            groupManager.addSharedObjectToActive(wrapper);
        }
        return transaction;
    }

    protected boolean addToLoading(LoadingSharedObject lso) {
        return groupManager.addLoadingSharedObject(lso);
    }

    /**
	 * Check remote creation of shared objects. This method is called by the
	 * remote shared object creation message handler, to verify that the shared
	 * object from container 'fromID' to container 'toID' with description
	 * 'desc' is to be allowed to be created within the current container. If
	 * this method throws, a failure (and exception will be sent back to caller
	 * If this method returns null, the create message is ignored. If this
	 * method returns a non-null object, the creation is allowed to proceed. The
	 * default implementation is to return a non-null object
	 * 
	 * @param fromID
	 *            the ID of the container sending us this create request
	 * @param toID
	 *            the ID (or null) of the container intended to receive this
	 *            request
	 * @param desc
	 *            the SharedObjectDescription that describes the shared object
	 *            to be created
	 * 
	 * @return Object null if the create message is to be ignored, non-null if
	 *         the creation should continue
	 * 
	 * @throws Exception
	 *             may throw any Exception to communicate back (via
	 *             sendCreateResponse) to the sender that the creation has
	 *             failed
	 */
    protected Object checkRemoteCreate(ID fromID, ID toID, ReplicaSharedObjectDescription desc) throws Exception {
        if (policy != null)
            return policy.checkAddSharedObject(fromID, toID, getID(), desc);
        return desc;
    }

    protected void debug(String msg) {
        //$NON-NLS-1$
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.CONTAINER, msg + ":" + config.getID());
    }

    protected void traceStack(String msg, Throwable e) {
        //$NON-NLS-1$
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SOContainer.class, config.getID() + ":" + msg, e);
    }

    protected boolean destroySharedObject(ID sharedObjectID) {
        return groupManager.removeSharedObject(sharedObjectID);
    }

    protected final void forward(ID fromID, ID toID, ContainerMessage data) throws IOException {
        if (toID == null)
            forwardExcluding(fromID, fromID, data);
        else
            forwardToRemote(fromID, toID, data);
    }

    protected abstract void forwardExcluding(ID from, ID excluding, ContainerMessage data) throws IOException;

    protected abstract void forwardToRemote(ID from, ID to, ContainerMessage data) throws IOException;

    /**
	 * @param sd shared object description
	 * @return Object[] arguments from the shared object description properties
	 */
    protected Object[] getArgsFromProperties(SharedObjectDescription sd) {
        if (sd == null)
            return null;
        final Map aMap = sd.getProperties();
        if (aMap == null)
            return null;
        final Object obj = aMap.get(DEFAULT_OBJECT_ARG_KEY);
        if (obj == null)
            return null;
        if (obj instanceof Object[]) {
            final Object[] ret = (Object[]) obj;
            aMap.remove(DEFAULT_OBJECT_ARG_KEY);
            return ret;
        }
        return null;
    }

    /**
	 * @param sd shared object description
	 * @return String[] arguments types from shared object description properties
	 */
    protected String[] getArgTypesFromProperties(SharedObjectDescription sd) {
        if (sd == null)
            return null;
        final Map aMap = sd.getProperties();
        if (aMap == null)
            return null;
        final Object obj = aMap.get(DEFAULT_OBJECT_ARGTYPES_KEY);
        if (obj == null)
            return null;
        if (obj instanceof String[]) {
            final String[] ret = (String[]) obj;
            aMap.remove(DEFAULT_OBJECT_ARGTYPES_KEY);
            return ret;
        }
        return null;
    }

    public static byte[] serialize(Serializable obj) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    protected ClassLoader getClassLoaderForContainer() {
        // policy (currently set to 'global').
        return this.getClass().getClassLoader();
    }

    /**
	 * @param sd shared object description
	 * @return ClassLoader classloader to used for given shared object description
	 */
    protected ClassLoader getClassLoaderForSharedObject(SharedObjectDescription sd) {
        return getClassLoaderForContainer();
    }

    protected Object getGroupMembershipLock() {
        return groupManager;
    }

    protected int getMaxGroupMembers() {
        return groupManager.getMaxMembers();
    }

    protected Thread getNewSharedObjectThread(ID sharedObjectID, Runnable runnable) {
        //$NON-NLS-1$
        return new Thread(sharedObjectThreadGroup, runnable, sharedObjectID.getName() + ":run");
    }

    protected long getNextSequenceNumber() {
        if (sequenceNumber == Long.MAX_VALUE) {
            sequenceNumber = 0;
            return sequenceNumber;
        }
        return sequenceNumber++;
    }

    public static ContainerMessage deserializeContainerMessage(byte[] bytes) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = ProviderPlugin.getDefault().createObjectInputStream(bis);
        Object obj = null;
        try {
            obj = ois.readObject();
        } catch (final ClassNotFoundException e) {
            ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, "class not found on deserialize", e));
            printToSystemError("deserializeContainerMessage class not found", e);
            return null;
        } catch (final InvalidClassException e) {
            ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, "invalid class on deserialize", e));
            printToSystemError("deserializeContainerMessage invalid class", e);
            return null;
        }
        if (obj instanceof ContainerMessage)
            return (ContainerMessage) obj;
        //$NON-NLS-1$
        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, "invalid container message", null));
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        printToSystemError("deserializeContainerMessage invalid container message ", new InvalidObjectException("object " + obj + " not appropriate type"));
        return null;
    }

    /**
	 * @param message message
	 * @param t exception to print to system error
	 * @since 2.0
	 */
    protected static void printToSystemError(String message, Throwable t) {
        System.err.println(message);
        t.printStackTrace(System.err);
    }

    protected ID[] getOtherMemberIDs() {
        return groupManager.getOtherMemberIDs();
    }

    protected ISynchAsynchEventHandler getReceiver() {
        return receiver;
    }

    /**
	 * @return ISynchAsyncEventHandler message receiver
	 * @since 4.7
	 */
    public ISynchAsynchEventHandler getMessageReceiver() {
        return getReceiver();
    }

    protected ISharedObject getSharedObject(ID id) {
        final SOWrapper wrap = getSharedObjectWrapper(id);
        return (wrap == null) ? null : wrap.getSharedObject();
    }

    protected ID[] getSharedObjectIDs() {
        return groupManager.getSharedObjectIDs();
    }

    protected SOWrapper getSharedObjectWrapper(ID id) {
        return groupManager.getFromActive(id);
    }

    protected void handleAsynchIOException(IOException except, AsynchEvent e) {
        // If we get IO Exception, we'll disconnect...if we can
        disconnect(e.getConnection());
    }

    protected void handleCreateMessage(ContainerMessage mess) throws IOException {
        final ContainerMessage.CreateMessage create = (ContainerMessage.CreateMessage) mess.getData();
        if (create == null)
            //$NON-NLS-1$
            throw new IOException("container create message cannot be null");
        final ReplicaSharedObjectDescription desc = (ReplicaSharedObjectDescription) create.getData();
        if (desc == null)
            //$NON-NLS-1$
            throw new IOException("shared object description cannot be null");
        final ID fromID = mess.getFromContainerID();
        final ID toID = mess.getToContainerID();
        Object checkCreateResult = null;
        final ID sharedObjectID = desc.getID();
        if (sharedObjectID == null)
            //$NON-NLS-1$
            throw new IOException("shared object id cannot be null");
        if (verifySharedObjectMessageTarget(toID)) {
            try {
                // Check to make sure that the remote creation is allowed.
                // If this method throws, a failure (and exception will be sent back to
                // caller
                // If this method returns null, the create message is ignored. If this
                // method
                // returns a non-null object, the creation is allowed to proceed
                checkCreateResult = checkRemoteCreate(fromID, toID, desc);
            } catch (final Exception e) {
                final SharedObjectAddException addException = new SharedObjectAddException("shared object=" + sharedObjectID + " could not be added to container=" + getID(), e);
                traceStack("Exception in checkRemoteCreate:" + desc, addException);
                try {
                    sendCreateResponse(fromID, sharedObjectID, addException, desc.getIdentifier());
                } catch (final IOException except) {
                    traceStack("Exception from sendCreateResponse in handleCreateResponse", except);
                }
                return;
            }
            // ignore
            if (checkCreateResult != null) {
                final LoadingSharedObject lso = new LoadingSharedObject(fromID, desc);
                synchronized (getGroupMembershipLock()) {
                    if (!addToLoading(lso)) {
                        try {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            sendCreateResponse(fromID, sharedObjectID, new SharedObjectAddException("shared object=" + sharedObjectID + " already exists in container=" + getID()), desc.getIdentifier());
                        } catch (final IOException e) {
                            traceStack("Exception in handleCreateMessage.sendCreateResponse", e);
                        }
                    }
                    forward(fromID, toID, mess);
                    return;
                }
            }
        } else {
            synchronized (getGroupMembershipLock()) {
                forward(fromID, toID, mess);
            }
        }
    }

    protected void handleCreateResponseMessage(ContainerMessage mess) throws IOException {
        final ID fromID = mess.getFromContainerID();
        final ID toID = mess.getToContainerID();
        final ContainerMessage.CreateResponseMessage resp = (ContainerMessage.CreateResponseMessage) mess.getData();
        synchronized (getGroupMembershipLock()) {
            if (verifySharedObjectMessageTarget(toID)) {
                final ID sharedObjectID = resp.getSharedObjectID();
                final SOWrapper sow = getSharedObjectWrapper(sharedObjectID);
                if (sow != null) {
                    sow.deliverCreateResponse(fromID, resp);
                }
            } else
                forward(fromID, toID, mess);
        }
    }

    /**
	 * @param mess leave group message
	 */
    protected abstract void handleLeaveGroupMessage(ContainerMessage mess);

    /**
	 * @param containerID containerID
	 * @return boolean true if verified, false otherwise
	 * @since 4.0
	 */
    protected boolean verifySharedObjectMessageTarget(ID containerID) {
        return (containerID == null || containerID.equals(getID()));
    }

    protected void handleSharedObjectDisposeMessage(ContainerMessage mess) throws IOException {
        final ID fromID = mess.getFromContainerID();
        final ID toID = mess.getToContainerID();
        final ContainerMessage.SharedObjectDisposeMessage resp = (ContainerMessage.SharedObjectDisposeMessage) mess.getData();
        final ID sharedObjectID = resp.getSharedObjectID();
        synchronized (getGroupMembershipLock()) {
            if (verifySharedObjectMessageTarget(toID)) {
                if (groupManager.isLoading(sharedObjectID)) {
                    groupManager.removeSharedObjectFromLoading(sharedObjectID);
                } else {
                    groupManager.removeSharedObject(sharedObjectID);
                }
            }
            forward(fromID, toID, mess);
        }
    }

    protected boolean verifyToIDForSharedObjectMessage(ID toID) {
        if (toID == null || toID.equals(getID()))
            return true;
        return false;
    }

    protected void handleSharedObjectMessage(ContainerMessage mess) throws IOException {
        final ID fromID = mess.getFromContainerID();
        final ID toID = mess.getToContainerID();
        final ContainerMessage.SharedObjectMessage resp = (ContainerMessage.SharedObjectMessage) mess.getData();
        final ID sharedObjectID = resp.getFromSharedObjectID();
        SOWrapper sow = null;
        Serializable obj = null;
        synchronized (getGroupMembershipLock()) {
            // We only deliver to local copy if the toID equals null (all), or it equals ours
            if (verifySharedObjectMessageTarget(toID)) {
                sow = getSharedObjectWrapper(sharedObjectID);
                if (sow != null) {
                    try {
                        obj = (Serializable) deserializeSharedObjectMessage((byte[]) resp.getData());
                        // Actually deliver event to shared object asynchronously
                        sow.deliverSharedObjectMessage(fromID, obj);
                    } catch (final ClassNotFoundException e) {
                        String message = "shared object message ClassNotFoundException.  sharedObjectID=" + sharedObjectID + " fromContainerID=" + fromID;
                        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, message, e));
                        printToSystemError(message, e);
                    } catch (final IOException e) {
                        String message = "shared object message IOException.  sharedObjectID=" + sharedObjectID + " fromContainerID=" + fromID;
                        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, message, e));
                        printToSystemError(message, e);
                    } catch (final NoClassDefFoundError e) {
                        String message = "shared object message NoClassDefFoundError.  sharedObjectID=" + sharedObjectID + " fromContainerID=" + fromID;
                        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, message, e));
                        printToSystemError(message, e);
                    }
                } else
                    handleUndeliveredSharedObjectMessage(resp);
            }
            // forward in any case
            forward(fromID, toID, mess);
        }
        // receiving event.
        if (sow != null)
            fireContainerEvent(new ContainerSharedObjectMessageReceivingEvent(getID(), fromID, sharedObjectID, obj));
    }

    /**
	 * @param resp response message
	 * @since 4.0
	 */
    protected void handleUndeliveredSharedObjectMessage(SharedObjectMessage resp) {
    // by default do nothing.  Subclasses may override
    }

    /**
	 * @param mess message
	 * @throws IOException not thrown by this implementation.
	 */
    protected void handleUnidentifiedMessage(ContainerMessage mess) throws IOException {
        // do nothing
        //$NON-NLS-1$
        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, IStatus.ERROR, "unidentified message " + mess, null));
        //$NON-NLS-1$
        debug("received unidentified message: " + mess);
    }

    protected abstract void handleViewChangeMessage(ContainerMessage mess) throws IOException;

    protected boolean isClosing() {
        return isClosing;
    }

    protected void disconnect(IConnection conn) {
        if (conn != null && conn.isConnected())
            conn.disconnect();
    }

    protected ISharedObject load(SharedObjectDescription sd) throws Exception {
        return sharedObjectManager.loadSharedObject(sd);
    }

    /**
	 * @param id id 
	 * @param obj obj
	 * @param props props
	 * @return SOConfig a non-<code>null</code> instance.
	 * @throws ECFException not thrown by this implementation.
	 */
    protected SOConfig createSharedObjectConfig(ID id, ISharedObject obj, Map props) throws ECFException {
        return new SOConfig(id, getID(), this, props);
    }

    protected SOConfig createRemoteSharedObjectConfig(ID fromID, ReplicaSharedObjectDescription sd, ISharedObject obj) {
        ID homeID = sd.getHomeID();
        if (homeID == null)
            homeID = fromID;
        return new SOConfig(sd.getID(), homeID, this, sd.getProperties());
    }

    protected SOContext createSharedObjectContext(SOConfig soconfig, IQueueEnqueue queue) {
        return new SOContext(soconfig.getSharedObjectID(), soconfig.getHomeContainerID(), this, soconfig.getProperties(), queue);
    }

    protected SOContext createRemoteSharedObjectContext(SOConfig soconfig, IQueueEnqueue queue) {
        return new SOContext(soconfig.getSharedObjectID(), soconfig.getHomeContainerID(), this, soconfig.getProperties(), queue);
    }

    protected SOWrapper createSharedObjectWrapper(ID id, ISharedObject s, Map props) throws ECFException {
        final SOConfig newConfig = createSharedObjectConfig(id, s, props);
        return new SOWrapper(newConfig, s, this);
    }

    protected SOWrapper createRemoteSharedObjectWrapper(ID fromID, ReplicaSharedObjectDescription sd, ISharedObject s) {
        final SOConfig newConfig = createRemoteSharedObjectConfig(fromID, sd, s);
        return new SOWrapper(newConfig, s, this);
    }

    protected void handleLeave(ID leftID, IConnection conn) {
        if (leftID == null)
            return;
        if (groupManager.removeMember(leftID)) {
            try {
                forwardExcluding(getID(), leftID, ContainerMessage.createViewChangeMessage(getID(), null, getNextSequenceNumber(), new ID[] { leftID }, false, null));
            } catch (final IOException e) {
                traceStack("Exception in memberLeave.forwardExcluding", e);
            }
        }
        if (conn != null)
            disconnect(conn);
    }

    protected void moveFromLoadingToActive(SOWrapper wrap) {
        groupManager.moveSharedObjectFromLoadingToActive(wrap);
    }

    protected void notifySharedObjectActivated(ID sharedObjectID) {
        synchronized (getGroupMembershipLock()) {
            groupManager.notifyOthersActivated(sharedObjectID);
            fireContainerEvent(new SharedObjectActivatedEvent(getID(), sharedObjectID));
        }
    }

    protected void notifySharedObjectDeactivated(ID sharedObjectID) {
        synchronized (getGroupMembershipLock()) {
            groupManager.notifyOthersDeactivated(sharedObjectID);
            fireContainerEvent(new SharedObjectDeactivatedEvent(getID(), sharedObjectID));
        }
    }

    protected ContainerMessage validateContainerMessage(Object mess) {
        // Message must not be null
        if (mess == null)
            return null;
        if (mess instanceof ContainerMessage) {
            final ContainerMessage contmess = (ContainerMessage) mess;
            final ID fromID = contmess.getFromContainerID();
            if (fromID == null)
                return null;
            // OK..let it continue on it's journey
            return contmess;
        }
        return null;
    }

    /**
	 * @param event event
	 * @throws IOException not thrown by this implementation.
	 */
    protected void processAsynch(AsynchEvent event) throws IOException {
        try {
            final Object obj = event.getData();
            if (obj == null) {
                debug(//$NON-NLS-1$
                "Ignoring null data in event " + event);
                return;
            }
            if (!(obj instanceof byte[])) {
                debug(//$NON-NLS-1$
                "Ignoring event without valid data " + event);
                return;
            }
            final ContainerMessage mess = validateContainerMessage(deserializeContainerMessage((byte[]) obj));
            if (mess == null) {
                debug(//$NON-NLS-1$
                "event not validated: " + event);
                return;
            }
            final Serializable submess = mess.getData();
            if (submess == null) {
                debug(//$NON-NLS-1$
                "submess is null: " + event);
                return;
            }
            if (submess instanceof ContainerMessage.CreateMessage)
                handleCreateMessage(mess);
            else if (submess instanceof ContainerMessage.CreateResponseMessage)
                handleCreateResponseMessage(mess);
            else if (submess instanceof ContainerMessage.SharedObjectDisposeMessage)
                handleSharedObjectDisposeMessage(mess);
            else if (submess instanceof ContainerMessage.SharedObjectMessage)
                handleSharedObjectMessage(mess);
            else if (submess instanceof ContainerMessage.ViewChangeMessage)
                handleViewChangeMessage(mess);
            else
                handleUnidentifiedMessage(mess);
        } catch (final IOException except) {
            handleAsynchIOException(except, event);
        }
    }

    protected abstract ID getIDForConnection(IAsynchConnection connection);

    protected abstract void processDisconnect(DisconnectEvent event);

    protected Serializable processSynch(SynchEvent e) throws IOException {
        final ContainerMessage mess = deserializeContainerMessage((byte[]) e.getData());
        final Serializable data = mess.getData();
        // Must be non null
        if (data != null && data instanceof ContainerMessage.LeaveGroupMessage)
            handleLeaveGroupMessage(mess);
        return null;
    }

    protected abstract void queueContainerMessage(ContainerMessage mess) throws IOException;

    protected void removeFromLoading(ID id) {
        groupManager.removeSharedObjectFromLoading(id);
    }

    protected boolean removeRemoteMember(ID remoteMember) {
        return groupManager.removeMember(remoteMember);
    }

    protected ISharedObject removeSharedObject(ID id) {
        synchronized (getGroupMembershipLock()) {
            final SOWrapper wrap = groupManager.getFromActive(id);
            if (wrap == null)
                return null;
            groupManager.removeSharedObject(id);
            return wrap.getSharedObject();
        }
    }

    protected void sendCreate(ID sharedObjectID, ID toContainerID, SharedObjectDescription sd) throws IOException {
        sendCreateSharedObjectMessage(toContainerID, sd);
    }

    protected void sendCreateResponse(ID homeID, ID sharedObjectID, Throwable t, long identifier) throws IOException {
        sendCreateResponseSharedObjectMessage(homeID, sharedObjectID, t, identifier);
    }

    protected void sendCreateResponseSharedObjectMessage(ID toContainerID, ID fromSharedObject, Throwable t, long ident) throws IOException {
        sendMessage(ContainerMessage.createSharedObjectCreateResponseMessage(getID(), toContainerID, getNextSequenceNumber(), fromSharedObject, t, ident));
    }

    protected ID[] sendCreateSharedObjectMessage(ID toContainerID, SharedObjectDescription sd) throws IOException {
        ID[] returnIDs = null;
        if (toContainerID == null) {
            synchronized (getGroupMembershipLock()) {
                // Send message to all
                sendMessage(ContainerMessage.createSharedObjectCreateMessage(getID(), toContainerID, getNextSequenceNumber(), sd));
                returnIDs = getOtherMemberIDs();
            }
        } else {
            // If the create msg is directed to this space, no msg will be sent
            if (getID().equals(toContainerID)) {
                returnIDs = new ID[0];
            } else {
                sendMessage(ContainerMessage.createSharedObjectCreateMessage(getID(), toContainerID, getNextSequenceNumber(), sd));
                returnIDs = new ID[1];
                returnIDs[0] = toContainerID;
            }
        }
        return returnIDs;
    }

    protected Map createContainerPropertiesForSharedObject(ID sharedObjectID) {
        return new HashMap();
    }

    protected void sendDispose(ID toContainerID, ID sharedObjectID) throws IOException {
        sendDisposeSharedObjectMessage(toContainerID, sharedObjectID);
    }

    protected void sendDisposeSharedObjectMessage(ID toContainerID, ID fromSharedObject) throws IOException {
        sendMessage(ContainerMessage.createSharedObjectDisposeMessage(getID(), toContainerID, getNextSequenceNumber(), fromSharedObject));
    }

    protected void sendMessage(ContainerMessage data) throws IOException {
        synchronized (getGroupMembershipLock()) {
            final ID ourID = getID();
            // We don't send to ourselves
            if (!ourID.equals(data.getToContainerID()))
                queueContainerMessage(data);
        }
    }

    protected byte[] serializeSharedObjectMessage(ID sharedObjectID, Object message) throws IOException {
        // If there is a serializer set, use it
        return getSharedObjectMessageSerializer().serializeMessage(sharedObjectID, message);
    }

    /**
	 * @param sharedObjectID shared object ID
	 * @param message message
	 * @return byte[] serialized message
	 * @throws IOException if some problem serializing 
	 * @since 2.0
	 */
    protected byte[] defaultSerializeSharedObjectMessage(ID sharedObjectID, Object message) throws IOException {
        if (!(message instanceof Serializable))
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new NotSerializableException("shared object=" + sharedObjectID + " message=" + message + " not serializable");
        final ByteArrayOutputStream bouts = new ByteArrayOutputStream();
        final IdentifiableObjectOutputStream ioos = new IdentifiableObjectOutputStream(sharedObjectID.getName(), bouts);
        ioos.writeObject(message);
        ioos.close();
        return bouts.toByteArray();
    }

    /**
	 * @param bytes data to deserialized
	 * @return Object the deserialized shared object message
	 * @throws IOException if deserialization cannot be done
	 * @throws ClassNotFoundException if deserialization cannot be done
	 * @since 2.0
	 */
    protected Object defaultDeserializeSharedObjectMessage(byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
        Object obj = null;
        try {
            final ObjectInputStream oins = ProviderPlugin.getDefault().createObjectInputStream(bins);
            obj = oins.readObject();
        } catch (final ClassNotFoundException e) {
            bins.reset();
            final IdentifiableObjectInputStream iins = new IdentifiableObjectInputStream(new IClassLoaderMapper() {

                public ClassLoader mapNameToClassLoader(String name) {
                    ISharedObjectManager manager = getSharedObjectManager();
                    ID[] ids = manager.getSharedObjectIDs();
                    ID found = null;
                    for (int i = 0; i < ids.length; i++) {
                        ID id = ids[i];
                        if (name.equals(id.getName())) {
                            found = id;
                            break;
                        }
                    }
                    if (found == null)
                        return null;
                    ISharedObject obj1 = manager.getSharedObject(found);
                    if (obj1 == null)
                        return null;
                    return obj1.getClass().getClassLoader();
                }
            }, bins);
            obj = iins.readObject();
            iins.close();
        }
        return obj;
    }

    protected Object deserializeSharedObjectMessage(byte[] bytes) throws IOException, ClassNotFoundException {
        return getSharedObjectMessageSerializer().deserializeMessage(bytes);
    }

    protected void sendMessage(ID toContainerID, ID sharedObjectID, Object message) throws IOException {
        if (message == null)
            return;
        // fire IContainerSharedObjectMessageSendingEvent
        fireContainerEvent(new ContainerSharedObjectMessageSendingEvent(getID(), toContainerID, sharedObjectID, message));
        final byte[] sendData = serializeSharedObjectMessage(sharedObjectID, message);
        sendSharedObjectMessage(toContainerID, sharedObjectID, sendData);
    }

    protected void sendSharedObjectMessage(ID toContainerID, ID fromSharedObject, Serializable data) throws IOException {
        sendMessage(ContainerMessage.createSharedObjectMessage(getID(), toContainerID, getNextSequenceNumber(), fromSharedObject, data));
    }

    protected void setMaxGroupMembers(int max) {
        groupManager.setMaxMembers(max);
    }

    /**
	 * @param containerEvent container event
	 */
    protected void fireDelegateContainerEvent(IContainerEvent containerEvent) {
        super.fireContainerEvent(containerEvent);
    }
}
