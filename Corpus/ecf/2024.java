/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import java.io.IOException;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;
import org.eclipse.ecf.core.sharedobject.events.*;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.sharedobject.util.QueueException;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.internal.core.sharedobject.Activator;
import org.eclipse.ecf.internal.core.sharedobject.SharedObjectDebugOptions;

/**
 * Base class for shared object classes. This base class provides a number of
 * utility method for subclasses to use for tracing (e.g.
 * {@link #traceCatching(String, Throwable)}, {@link #traceEntering(String)},
 * {@link #traceExiting(String)}) logging (e.g.
 * {@link #log(int, String, Throwable)}), as well as methods to access the
 * {@link ISharedObjectContext} for the shared object instance (e.g.
 * {@link #getID()}, {@link #getHomeContainerID()}, {@link #getContext()},
 * {@link #getConfig()}, {@link #getProperties()}, {@link #isConnected()},
 * {@link #isPrimary()}, etc). Also provided are a number of methods for
 * sending messages to remote replica shared objects (e.g.
 * {@link #sendSharedObjectMsgTo(ID, SharedObjectMsg)},
 * {@link #sendSharedObjectMsgToPrimary(SharedObjectMsg)},
 * {@link #sendSharedObjectMsgToSelf(SharedObjectMsg)}) and methods for
 * replicating oneself to remote containers (e.g.
 * {@link #replicateToRemoteContainers(ID[])}). Finally, object lifecycle
 * methods are also provided (e.g. {@link #initialize()},
 * {@link #creationCompleted()}, {@link #dispose(ID)}).
 * 
 * Subclasses may use and override these methods as appropriate.
 * 
 */
public class BaseSharedObject implements ISharedObject, IIdentifiable {

    protected static final int DESTROYREMOTE_CODE = 8001;

    protected static final int DESTROYSELFLOCAL_CODE = 8002;

    private ISharedObjectConfig config = null;

    private List eventProcessors = new Vector();

    public  BaseSharedObject() {
        super();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.ISharedObject#init(org.eclipse.ecf.core.sharedobject.ISharedObjectConfig)
	 */
    public final void init(ISharedObjectConfig initData) throws SharedObjectInitException {
        this.config = initData;
        //$NON-NLS-1$
        traceEntering("init", initData);
        addEventProcessor(new SharedObjectMsgEventProcessor(this));
        initialize();
        //$NON-NLS-1$
        traceExiting("init");
    }

    /**
	 * Initialize this shared object. Subclasses may override as appropriate to
	 * define custom initialization behavior. If initialization should fail,
	 * then a SharedObjectInitException should be thrown by implementing code.
	 * Also, subclasses overriding this method should call super.initialize()
	 * before running their own code.
	 * 
	 * @throws SharedObjectInitException
	 *             if initialization should throw
	 */
    protected void initialize() throws SharedObjectInitException {
        //$NON-NLS-1$
        traceEntering("initialize");
    }

    /**
	 * Called by replication strategy code (e.g. two phase commit) when creation
	 * is completed (i.e. when transactional replication completed
	 * successfully). Subclasses that need to be notified when creation is
	 * completed should override this method.
	 * 
	 */
    protected void creationCompleted() {
        //$NON-NLS-1$
        traceEntering("creationCompleted", null);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.ISharedObject#dispose(org.eclipse.ecf.core.identity.ID)
	 */
    public void dispose(ID containerID) {
        //$NON-NLS-1$
        traceEntering("dispose", containerID);
        eventProcessors.clear();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        Activator activator = Activator.getDefault();
        if (activator == null)
            return null;
        final IAdapterManager adapterManager = activator.getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.ISharedObject#handleEvent(org.eclipse.ecf.core.util.Event)
	 */
    public void handleEvent(Event event) {
        //$NON-NLS-1$
        traceEntering("handleEvent", event);
        fireEventProcessors(event);
        //$NON-NLS-1$
        traceExiting("handleEvent");
    }

    /**
	 * Add an event processor to the set of event processors available.
	 * @param proc the event processor to add.  Must not be <code>null</code>.
	 * @return <code>true</code> if actually added, <code>false</code> otherwise.
	 */
    @SuppressWarnings("unchecked")
    public boolean addEventProcessor(IEventProcessor proc) {
        Assert.isNotNull(proc);
        synchronized (eventProcessors) {
            return eventProcessors.add(proc);
        }
    }

    /**
	 * Remove an event processor from the set of event processors available to this object.
	 * @param proc the event processor to remove.  Must not be <code>null</code>.
	 * @return <code>true</code> if actually removed, <code>false</code> otherwise.
	 */
    public boolean removeEventProcessor(IEventProcessor proc) {
        Assert.isNotNull(proc);
        synchronized (eventProcessors) {
            return eventProcessors.remove(proc);
        }
    }

    /**
	 *  Clear event processors.
	 */
    public void clearEventProcessors() {
        synchronized (eventProcessors) {
            eventProcessors.clear();
        }
    }

    /**
	 * Method called when an event is not handled by any event processor.
	 * @param event the event that was not handled.
	 */
    protected void handleUnhandledEvent(Event event) {
        // By default, simply log as warning
        //$NON-NLS-1$
        Activator.getDefault().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, "handleUnhandledEvent=" + event, null));
    }

    /**
	 * Fire the current set of event processors with given event.
	 * @param event the event to deliver to event processors.
	 */
    @SuppressWarnings("unchecked")
    protected void fireEventProcessors(Event event) {
        if (event == null)
            return;
        Event evt = event;
        List notify = null;
        synchronized (eventProcessors) {
            notify = new ArrayList(eventProcessors);
        }
        if (notify.size() == 0) {
            handleUnhandledEvent(evt);
            return;
        }
        for (Iterator i = notify.iterator(); i.hasNext(); ) {
            IEventProcessor ep = (IEventProcessor) i.next();
            if (ep.processEvent(evt))
                break;
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.ISharedObject#handleEvents(org.eclipse.ecf.core.util.Event[])
	 */
    public void handleEvents(Event[] events) {
        //$NON-NLS-1$
        traceEntering("handleEvents", events);
        if (events == null)
            return;
        for (int i = 0; i < events.length; i++) {
            handleEvent(events[i]);
        }
        //$NON-NLS-1$
        traceExiting("handleEvents");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return getConfig().getSharedObjectID();
    }

    /**
	 * Get the config for this shared object.
	 * 
	 * @return ISharedObjectConfig for this object.  The ISharedObjectConfig is 
	 * set within {@link #init(ISharedObjectConfig)}.  Will not be <code>null</code> as long as the init method
	 * is called prior to this method being called.
	 */
    protected final ISharedObjectConfig getConfig() {
        return config;
    }

    /**
	 * Get the shared object context for this object.
	 * 
	 * @return ISharedObjectContext the context.  Will not be <code>null</code>.
	 */
    protected final ISharedObjectContext getContext() {
        return getConfig().getContext();
    }

    /**
	 * @return ID that is the home container ID (primary) for this shared object.  Will not be <code>null</code> 
	 * as long as the {@link #init(ISharedObjectConfig)} method has been called (by container) as a result
	 * of {@link ISharedObjectManager#addSharedObject(ID, ISharedObject, Map)}.
	 */
    protected ID getHomeContainerID() {
        return getConfig().getHomeContainerID();
    }

    /**
	 * @return ID that is the local container ID for this shared object.  Will be <code>null</code> if
	 * the shared object is *not* in a local container (i.e. has been removed from the container).
	 */
    protected ID getLocalContainerID() {
        ISharedObjectContext context = getContext();
        return (context == null) ? null : context.getLocalContainerID();
    }

    /**
	 * @return ID the connected ID for the container that contains this shared object.  Will be non-<code>null</code>
	 * if the surrounding container is not currently connected.
	 */
    protected ID getConnectedID() {
        ISharedObjectContext context = getContext();
        return (context == null) ? null : context.getConnectedID();
    }

    /**
	 * @return <code>true</code> if the surrounding container is currently connected, <code>false</code> otherwise.
	 */
    protected final boolean isConnected() {
        return (getConnectedID() != null);
    }

    /**
	 * @return <code>true</code> if this shared object replica is the <b>primary</b>.  The definition of primary
	 * is whether the {@link #getLocalContainerID()} and {@link #getHomeContainerID()} values are equal.
	 */
    protected final boolean isPrimary() {
        ID local = getLocalContainerID();
        ID home = getHomeContainerID();
        if (local == null || home == null) {
            return false;
        }
        return (local.equals(home));
    }

    /**
	 * @return Map any properties associated with this shared object via the ISharedObjectConfig provided
	 * upon {@link #init(ISharedObjectConfig)}.
	 */
    protected final Map<String, ?> getProperties() {
        return getConfig().getProperties();
    }

    /**
	 * Destroy this shared object in the context of the current container.  Destroys both local copy and
	 * any replicas present in remote containers.
	 */
    protected void destroySelf() {
        //$NON-NLS-1$
        traceEntering("destroySelf");
        if (isPrimary()) {
            try {
                // Send destroy message to all known remotes
                destroyRemote(null);
            } catch (IOException e) {
                traceCatching("destroySelf", e);
                log(DESTROYREMOTE_CODE, "destroySelf", e);
            }
        }
        // Now destroy self locally
        destroySelfLocal();
        //$NON-NLS-1$
        traceExiting("destroySelf");
    }

    /**
	 * Destroy the local copy of this shared object in the current container.
	 */
    protected void destroySelfLocal() {
        //$NON-NLS-1$
        traceEntering("destroySelfLocal");
        try {
            ISharedObjectManager manager = getContext().getSharedObjectManager();
            if (manager != null) {
                manager.removeSharedObject(getID());
            }
        } catch (Exception e) {
            traceCatching("destroySelfLocal", e);
            log(DESTROYSELFLOCAL_CODE, "destroySelfLocal", e);
        }
        //$NON-NLS-1$
        traceExiting("destroySelfLocal");
    }

    /**
	 * @param remoteID the ID of the remote container where the replica should be destroyed.
	 * @throws IOException if the destroy message cannot be sent (i.e. due to disconnection, etc).
	 */
    protected void destroyRemote(ID remoteID) throws IOException {
        ISharedObjectContext context = getContext();
        if (context != null)
            context.sendDispose(remoteID);
    }

    /**
	 * Send SharedObjectMessage to container with given ID. The toID parameter
	 * may be null, and if null the message will be delivered to <b>all</b>
	 * containers in group. The second parameter may not be null.
	 * 
	 * @param toID
	 *            the target container ID for the SharedObjectMsg. If null, the
	 *            given message is sent to all other containers currently in
	 *            group
	 * @param msg
	 *            the message instance to send
	 * @throws IOException
	 *             thrown if the local container is not connected or unable to
	 *             send for other reason
	 */
    protected void sendSharedObjectMsgTo(ID toID, SharedObjectMsg msg) throws IOException {
        ISharedObjectContext context = getContext();
        //$NON-NLS-1$
        String method = "sendSharedObjectMsgTo";
        traceEntering(method, new Object[] { toID, msg });
        if (context != null) {
            //$NON-NLS-1$
            Assert.isNotNull(msg, "SharedObjectMsg cannot be null");
            context.sendMessage(toID, new SharedObjectMsgEvent(getID(), toID, msg));
        } else {
            //$NON-NLS-1$
            trace(method, "No shared object context available, so no message sent");
        }
        traceExiting(method);
    }

    /**
	 * Send SharedObjectMsg to this shared object's primary instance.
	 * 
	 * @param msg
	 *            the message instance to send
	 * @throws IOException
	 *             throws if the local container is not connect or unable to
	 *             send for other reason
	 */
    protected void sendSharedObjectMsgToPrimary(SharedObjectMsg msg) throws IOException {
        sendSharedObjectMsgTo(getHomeContainerID(), msg);
    }

    /**
	 * Send SharedObjectMsg to local shared object. This places the given
	 * message at the end of this shared object's message queue for processing.
	 * 
	 * @param msg
	 *            the message instance to send.
	 */
    protected void sendSharedObjectMsgToSelf(SharedObjectMsg msg) {
        if (msg == null)
            //$NON-NLS-1$
            throw new NullPointerException("SharedObjectMsg cannot be null");
        ISharedObjectContext context = getContext();
        if (context == null)
            return;
        IQueueEnqueue queue = context.getQueue();
        try {
            queue.enqueue(new SharedObjectMsgEvent(getID(), getContext().getLocalContainerID(), msg));
        } catch (QueueException e) {
            traceCatching("sendSharedObjectMsgToSelf", e);
            log(DESTROYREMOTE_CODE, "sendSharedObjectMsgToSelf", e);
        }
    }

    /**
	 * Get SharedObjectMsg from ISharedObjectMessageEvent.
	 * ISharedObjectMessageEvents can come from both local and remote sources.
	 * In the remote case, the SharedObjectMsg has to be retrieved from the
	 * RemoteSharedObjectEvent rather than the
	 * ISharedObjectMessageEvent.getData() directly. This method will provide a
	 * non-null SharedObjectMsg if it's provided either via remotely or locally.
	 * Returns null if the given event does not provide a valid SharedObjectMsg.
	 * 
	 * @param event
	 * @return SharedObjectMsg the SharedObjectMsg delivered by the given event
	 */
    protected SharedObjectMsg getSharedObjectMsgFromEvent(ISharedObjectMessageEvent event) {
        //$NON-NLS-1$
        traceEntering("getSharedObjectMsgFromEvent", event);
        Object eventData = event.getData();
        Object msgData = null;
        // contains and get it's data
        if (eventData != null && eventData instanceof RemoteSharedObjectEvent) {
            // It's a remote event
            Object rsoeData = ((RemoteSharedObjectEvent) event).getData();
            if (rsoeData != null && rsoeData instanceof SharedObjectMsgEvent)
                msgData = ((SharedObjectMsgEvent) rsoeData).getData();
        } else
            msgData = eventData;
        if (msgData != null && msgData instanceof SharedObjectMsg) {
            //$NON-NLS-1$
            traceExiting("getSharedObjectMsgFromEvent", msgData);
            return (SharedObjectMsg) msgData;
        }
        //$NON-NLS-1$
        traceExiting("getSharedObjectMsgFromEvent", null);
        return null;
    }

    /**
	 * Handle a ISharedObjectMessageEvent. This method will be automatically
	 * called by the SharedObjectMsgEventProcessor when a
	 * ISharedObjectMessageEvent is received. The SharedObjectMsgEventProcessor
	 * is associated with this object via the initialize() method
	 * 
	 * @param event
	 *            the event to handle
	 * @return true if the provided event should receive no further processing.
	 *         If false the provided Event should be passed to subsequent event
	 *         processors.
	 */
    protected boolean handleSharedObjectMsgEvent(ISharedObjectMessageEvent event) {
        //$NON-NLS-1$
        traceEntering("handleSharedObjectMsgEvent", event);
        boolean result = false;
        if (event instanceof ISharedObjectCreateResponseEvent)
            result = handleSharedObjectCreateResponseEvent((ISharedObjectCreateResponseEvent) event);
        else {
            SharedObjectMsg msg = getSharedObjectMsgFromEvent(event);
            if (msg != null)
                result = handleSharedObjectMsg(event.getRemoteContainerID(), msg);
        }
        //$NON-NLS-1$
        traceExiting("handleSharedObjectMsgEvent", result ? Boolean.TRUE : Boolean.FALSE);
        return result;
    }

    /**
	 * @since 2.4
	 */
    protected boolean handleSharedObjectMsg(ID fromID, SharedObjectMsg msg) {
        return handleSharedObjectMsg(msg);
    }

    /**
	 * Handle a ISharedObjectCreateResponseEvent. This handler is called by
	 * handleSharedObjectMsgEvent when the ISharedObjectMessageEvent is of type
	 * ISharedObjectCreateResponseEvent. This default implementation simply
	 * returns false. Subclasses may override as appropriate. Note that if
	 * return value is true, it will prevent subsequent event processors from
	 * having a chance to process event
	 * 
	 * @param createResponseEvent
	 *            the ISharedObjectCreateResponseEvent received
	 * @return true if the provided event should receive no further processing.
	 *         If false the provided Event should be passed to subsequent event
	 *         processors.
	 */
    protected boolean handleSharedObjectCreateResponseEvent(ISharedObjectCreateResponseEvent createResponseEvent) {
        return false;
    }

    /**
	 * SharedObjectMsg handler method. This method will be called by
	 * {@link #handleSharedObjectMsgEvent(ISharedObjectMessageEvent)} when a
	 * SharedObjectMsg is received either from a local source or a remote
	 * source. This default implementation simply returns false so that other
	 * processing of of the given msg can occur. Subclasses should override this
	 * behavior to define custom logic for handling SharedObjectMsgs.
	 * 
	 * @param msg
	 *            the SharedObjectMsg received
	 * @return true if the msg has been completely handled and subsequent
	 *         processing should stop. False if processing should continue
	 */
    protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
        return false;
    }

    /**
	 * Get a ReplicaSharedObjectDescription for a replica to be created on a
	 * given receiver.
	 * 
	 * @param receiver
	 *            the receiver the ReplicaSharedObjectDescription is for
	 * @return ReplicaSharedObjectDescription to be associated with given
	 *         receiver. A non-null ReplicaSharedObjectDescription <b>must</b>
	 *         be returned.
	 */
    protected ReplicaSharedObjectDescription getReplicaDescription(ID receiver) {
        //$NON-NLS-1$
        traceEntering("getReplicaDescription", receiver);
        ReplicaSharedObjectDescription result = new ReplicaSharedObjectDescription(getClass(), getID(), getConfig().getHomeContainerID(), getConfig().getProperties());
        //$NON-NLS-1$
        traceExiting("getReplicaDescription", result);
        return result;
    }

    /**
	 * This method is called by replicateToRemoteContainers to determine the
	 * ReplicaSharedObjectDescriptions associated with the given receivers.
	 * Receivers may be null (meaning that all in group are to be receivers),
	 * and if so then this method should return a ReplicaSharedObjectDescription []
	 * of length 1 with a single ReplicaSharedObjectDescription that will be
	 * used for all receivers. If receivers is non-null, then the
	 * ReplicaSharedObjectDescription [] result must be of <b>same length</b>
	 * as the receivers array. This method calls the getReplicaDescription
	 * method to create a replica description for each receiver. If this method
	 * returns null, <b>null replication is done</b>.
	 * 
	 * @param receivers
	 *            an ID[] of the intended receivers for the resulting
	 *            ReplicaSharedObjectDescriptions. If null, then the <b>entire
	 *            current group</b> is assumed to be the target, and this
	 *            method should return a ReplicaSharedObjectDescriptions array
	 *            of length 1, with a single ReplicaSharedObjectDescriptions for
	 *            all target receivers.
	 * 
	 * @return ReplicaSharedObjectDescription[] to determine replica
	 *         descriptions for each receiver. A null return value indicates
	 *         that no replicas are to be created. If the returned array is not
	 *         null, then it <b>must</b> be of same length as the receivers
	 *         parameter.
	 * 
	 */
    protected ReplicaSharedObjectDescription[] getReplicaDescriptions(ID[] receivers) {
        //$NON-NLS-1$
        traceEntering("getReplicaDescriptions", receivers);
        ReplicaSharedObjectDescription[] descriptions = null;
        if (receivers == null || receivers.length == 1) {
            descriptions = new ReplicaSharedObjectDescription[1];
            descriptions[0] = getReplicaDescription((receivers == null) ? null : receivers[0]);
        } else {
            descriptions = new ReplicaSharedObjectDescription[receivers.length];
            for (int i = 0; i < receivers.length; i++) {
                descriptions[i] = getReplicaDescription(receivers[i]);
            }
        }
        //$NON-NLS-1$
        traceExiting("getReplicaDescriptions", descriptions);
        return descriptions;
    }

    /**
	 * Get IDs of remote containers currently in this group. This method
	 * consults the current container context to retrieve the current group
	 * membership
	 * 
	 * @return ID[] of current group membership. Will not return null;
	 * 
	 * @see ISharedObjectContext#getGroupMemberIDs()
	 */
    protected ID[] getGroupMemberIDs() {
        ISharedObjectContext context = getContext();
        return (context == null) ? new ID[] {} : context.getGroupMemberIDs();
    }

    /**
	 * Replicate this shared object to a given set of remote containers. This
	 * method will invoke the method getReplicaDescriptions in order to
	 * determine the set of ReplicaSharedObjectDescriptions to send to remote
	 * containers.
	 * 
	 * @param remoteContainers
	 *            the set of remote containers to replicate to. If null, <b>all</b>
	 *            containers in the current group are sent a message to create a
	 *            replica of this shared object.
	 */
    protected void replicateToRemoteContainers(ID[] remoteContainers) {
        ISharedObjectContext context = getContext();
        if (context != null) {
            //$NON-NLS-1$
            traceEntering("replicateToRemoteContainers", remoteContainers);
            try {
                // Get current group membership
                ReplicaSharedObjectDescription[] createInfos = getReplicaDescriptions(remoteContainers);
                if (createInfos != null) {
                    if (createInfos.length == 1) {
                        context.sendCreate((remoteContainers == null) ? null : remoteContainers[0], createInfos[0]);
                    } else {
                        for (int i = 0; i < remoteContainers.length; i++) {
                            context.sendCreate(remoteContainers[i], createInfos[i]);
                        }
                    }
                }
            } catch (IOException e) {
                traceCatching("replicateToRemoteContainers." + DESTROYREMOTE_CODE, e);
                log(DESTROYREMOTE_CODE, "replicateToRemoteContainers", e);
            }
        }
    }

    protected void log(int code, String method, Throwable e) {
        Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, code, getSharedObjectAsString(method), e));
    }

    protected void log(String method, Throwable e) {
        log(IStatus.ERROR, method, e);
    }

    private String getSharedObjectAsString(String suffix) {
        StringBuffer buf = new StringBuffer(String.valueOf(getID()));
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(((isPrimary()) ? ".p." : ".r."));
        buf.append(suffix);
        return buf.toString();
    }

    protected void traceEntering(String methodName) {
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_ENTERING, this.getClass(), getSharedObjectAsString(methodName));
    }

    protected void traceEntering(String methodName, Object[] params) {
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_ENTERING, this.getClass(), getSharedObjectAsString(methodName));
    }

    protected void traceEntering(String methodName, Object param) {
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_ENTERING, this.getClass(), getSharedObjectAsString(methodName));
    }

    protected void traceExiting(String methodName) {
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_EXITING, this.getClass(), getSharedObjectAsString(methodName));
    }

    protected void traceExiting(String methodName, Object result) {
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_EXITING, this.getClass(), getSharedObjectAsString(methodName));
    }

    protected void traceCatching(String method, Throwable t) {
        Trace.catching(Activator.PLUGIN_ID, SharedObjectDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), getSharedObjectAsString(method), t);
    }

    /**
	 * @since 2.2
	 */
    protected void trace(String method, String message) {
        //$NON-NLS-1$
        Trace.trace(Activator.PLUGIN_ID, SharedObjectDebugOptions.DEBUG, this.getClass(), method, getSharedObjectAsString(method) + ": " + message);
    }
}
