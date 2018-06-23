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
import java.net.ConnectException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.*;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.comm.*;
import org.eclipse.ecf.provider.generic.gmm.Member;

public abstract class ClientSOContainer extends SOContainer implements ISharedObjectContainerClient {

    public static final int DEFAULT_CONNECT_TIMEOUT = 30000;

    protected ISynchAsynchConnection connection;

    protected ID remoteServerID;

    protected byte connectionState;

    protected IConnectInitiatorPolicy connectPolicy = null;

    public static final byte DISCONNECTED = 0;

    public static final byte CONNECTING = 1;

    public static final byte CONNECTED = 2;

    static final class Lock {
        //
    }

    protected Lock connectLock;

    public  ClientSOContainer(ISharedObjectContainerConfig config) {
        super(config);
        connection = null;
        connectionState = DISCONNECTED;
        connectLock = new Lock();
    }

    protected Lock getConnectLock() {
        return connectLock;
    }

    protected ISynchAsynchConnection getConnection() {
        return connection;
    }

    public void setConnectInitiatorPolicy(IConnectInitiatorPolicy policy) {
        this.connectPolicy = policy;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#dispose()
	 */
    public void dispose() {
        synchronized (connectLock) {
            isClosing = true;
            if (isConnected()) {
                this.disconnect();
            } else {
                setStateDisconnected(connection);
            }
        }
        super.dispose();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#isGroupManager()
	 */
    public final boolean isGroupManager() {
        return false;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return remoteServerID;
    }

    private void setStateDisconnected(ISynchAsynchConnection conn) {
        disconnect(conn);
        connectionState = DISCONNECTED;
        connection = null;
        remoteServerID = null;
    }

    private void setStateConnecting(ISynchAsynchConnection conn) {
        connectionState = CONNECTING;
        connection = conn;
    }

    private void setStateConnected(ID serverID, ISynchAsynchConnection conn) {
        connectionState = CONNECTED;
        connection = conn;
        remoteServerID = serverID;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(ID targetID, IConnectContext joinContext) throws ContainerConnectException {
        try {
            if (isClosing)
                throw new //$NON-NLS-1$
                IllegalStateException(//$NON-NLS-1$
                "Container closing");
            if (targetID == null)
                throw new //$NON-NLS-1$
                ContainerConnectException(//$NON-NLS-1$
                "targetID cannot be null");
            Object response = null;
            synchronized (getConnectLock()) {
                // Throw if already connected
                if (isConnected())
                    throw new IllegalStateException(//$NON-NLS-1$
                    "Container already connected connectedID=" + //$NON-NLS-1$
                    getConnectedID());
                // Throw if connecting
                if (isConnecting())
                    throw new IllegalStateException("Container connecting");
                // else we're entering connecting state
                // first notify synchonously
                final ISynchAsynchConnection aConnection = createConnection(targetID, joinContext);
                setStateConnecting(aConnection);
                fireContainerEvent(new ContainerConnectingEvent(this.getID(), targetID, joinContext));
                final Object connectData = getConnectData(targetID, joinContext);
                final int connectTimeout = getConnectTimeout();
                synchronized (aConnection) {
                    try {
                        // Make connect call
                        response = aConnection.connect(targetID, connectData, connectTimeout);
                    } catch (final ECFException e) {
                        if (getConnection() != aConnection)
                            disconnect(aConnection);
                        else
                            setStateDisconnected(aConnection);
                        throw e;
                    }
                    // If not in correct state, disconnect and return
                    if (getConnection() != aConnection) {
                        disconnect(aConnection);
                        throw new IllegalStateException("Container connect failed because not in correct state");
                    }
                    ID serverID = null;
                    try {
                        serverID = handleConnectResponse(targetID, response);
                    } catch (final Exception e) {
                        setStateDisconnected(aConnection);
                        throw e;
                    }
                    setStateConnected(serverID, aConnection);
                    // notify listeners
                    fireContainerEvent(new ContainerConnectedEvent(this.getID(), remoteServerID));
                    aConnection.start();
                }
            }
        } catch (final ContainerConnectException e) {
            throw e;
        } catch (final ECFException e) {
            final IStatus s = e.getStatus();
            throw new ContainerConnectException(s.getMessage(), s.getException());
        } catch (final Exception e) {
            throw new ContainerConnectException(e.getLocalizedMessage(), e);
        }
    }

    protected Callback[] createAuthorizationCallbacks() {
        return null;
    }

    protected Object getConnectData(ID remote, IConnectContext joinContext) throws IOException, UnsupportedCallbackException {
        Object connectData = null;
        if (connectPolicy != null)
            connectData = connectPolicy.createConnectData(this, remote, joinContext);
        else {
            final Callback[] callbacks = createAuthorizationCallbacks();
            if (joinContext != null) {
                final CallbackHandler handler = joinContext.getCallbackHandler();
                if (handler != null)
                    handler.handle(callbacks);
            }
        }
        return ContainerMessage.createJoinGroupMessage(getID(), remote, getNextSequenceNumber(), (Serializable) connectData);
    }

    protected int getConnectTimeout() {
        if (connectPolicy != null)
            return connectPolicy.getConnectTimeout();
        return DEFAULT_CONNECT_TIMEOUT;
    }

    protected void handleLeaveGroupMessage(ContainerMessage mess) {
        if (!isConnected())
            return;
        final ContainerMessage.LeaveGroupMessage lgm = (ContainerMessage.LeaveGroupMessage) mess.getData();
        final ID fromID = mess.getFromContainerID();
        if (fromID == null || !fromID.equals(remoteServerID)) {
            // we ignore anything not from our server
            return;
        }
        synchronized (getGroupMembershipLock()) {
            handleLeave(fromID, connection);
        }
        // Now notify that we've been ejected
        fireContainerEvent(new ContainerEjectedEvent(getID(), fromID, lgm.getData()));
    }

    protected void handleViewChangeMessage(ContainerMessage mess) throws IOException {
        if (!isConnected())
            return;
        final ContainerMessage.ViewChangeMessage vc = (ContainerMessage.ViewChangeMessage) mess.getData();
        if (vc == null)
            //$NON-NLS-1$
            throw new IOException("view change message cannot be null");
        final ID fromID = mess.getFromContainerID();
        if (fromID == null || !fromID.equals(remoteServerID)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            throw new IOException("view change message fromID=" + fromID + " is not from remoteServerID=" + remoteServerID);
        }
        final ID[] changeIDs = vc.getChangeIDs();
        if (changeIDs == null) {
        // do nothing if we've got no changes
        } else {
            for (int i = 0; i < changeIDs.length; i++) {
                if (vc.isAdd()) {
                    boolean wasAdded = false;
                    synchronized (getGroupMembershipLock()) {
                        // known
                        if (groupManager.getMemberForID(changeIDs[i]) == null) {
                            wasAdded = true;
                            groupManager.addMember(new Member(changeIDs[i]));
                        }
                    }
                    // accomplished
                    if (wasAdded)
                        fireContainerEvent(new ContainerConnectedEvent(getID(), changeIDs[i]));
                } else {
                    if (changeIDs[i].equals(getID())) {
                        // We've been ejected.
                        final ID serverID = remoteServerID;
                        synchronized (getGroupMembershipLock()) {
                            handleLeave(remoteServerID, connection);
                        }
                        // Notify listeners that we've been ejected
                        fireContainerEvent(new ContainerEjectedEvent(getID(), serverID, vc.getData()));
                    } else {
                        synchronized (getGroupMembershipLock()) {
                            groupManager.removeMember(changeIDs[i]);
                        }
                        // Notify listeners that another remote has gone away
                        fireContainerEvent(new ContainerDisconnectedEvent(getID(), changeIDs[i]));
                    }
                }
            }
        }
    }

    /**
	 * @param from from
	 * @param excluding excluding
	 * @param data data
	 * @throws IOException not thrown by this implementation.
	 */
    protected void forwardExcluding(ID from, ID excluding, ContainerMessage data) throws IOException {
    // NOP
    }

    protected Serializable getLeaveData(ID target) {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#disconnect()
	 */
    public void disconnect() {
        disconnect((Throwable) null);
    }

    /**
	 * Create connection instance.  This method is called by {@link ClientSOContainer#connect(ID, IConnectContext)}.  Following
	 * a return from this method, the new ISynchAsynchConnection instance's {@link IConnection#connect(ID, Object, int)} method is subsequently 
	 * called.  
	 * @param targetID the targetID to connect to.  The value is the same as that passed into {@link ClientSOContainer#connect(ID, IConnectContext)}.
	 * @param data and data provided to the connection via the IConnectContext passed into the {@link #connect(ID, IConnectContext)}
	 * call.
	 * @return {@link ISynchAsynchConnection} a connection instance.  Will not be <code>null</code>.
	 * @throws ConnectionCreateException thrown if the connection cannot be created.
	 */
    protected abstract ISynchAsynchConnection createConnection(ID targetID, Object data) throws ConnectionCreateException;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#queueContainerMessage(org.eclipse.ecf.provider.generic.ContainerMessage)
	 */
    protected void queueContainerMessage(ContainerMessage message) throws IOException {
        // Do it
        connection.sendAsynch(message.getToContainerID(), serialize(message));
    }

    /**
	 * @param from from 
	 * @param excluding excluding
	 * @param msg msg
	 * @param data data
	 * @throws IOException not thrown by this implementation.
	 */
    protected void forwardExcluding(ID from, ID excluding, byte msg, Serializable data) throws IOException {
    // NOP 
    }

    /**
	 * @param from from 
	 * @param to to 
	 * @param message message
	 * @throws IOException not thrown by this implementation.
	 */
    protected void forwardToRemote(ID from, ID to, ContainerMessage message) throws IOException {
    // NOP
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#getIDForConnection(org.eclipse.ecf.provider.comm.IAsynchConnection)
	 */
    protected ID getIDForConnection(IAsynchConnection conn) {
        return remoteServerID;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#handleLeave(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.provider.comm.IConnection)
	 */
    protected void handleLeave(ID fromID, IConnection conn) {
        // If it's the remote server then we're completely disconnected
        if (fromID.equals(remoteServerID)) {
            groupManager.removeNonLocalMembers();
            super.handleLeave(fromID, conn);
            setStateDisconnected(null);
        // Otherwise it's some other group member
        } else if (fromID.equals(getID()))
            super.handleLeave(fromID, conn);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#sendMessage(org.eclipse.ecf.provider.generic.ContainerMessage)
	 */
    protected void sendMessage(ContainerMessage data) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendMessage(data);
        }
    }

    protected ID[] sendCreateMsg(ID toID, SharedObjectDescription createInfo) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            return super.sendCreateSharedObjectMessage(toID, createInfo);
        }
    }

    protected void sendCreate(ID sharedObjectId, ID toContainerId, SharedObjectDescription sd) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendCreate(sharedObjectId, toContainerId, sd);
        }
    }

    protected void sendCreateResponse(ID homeId, ID sharedObjectId, Throwable t, long identifier) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendCreateResponse(homeId, sharedObjectId, t, identifier);
        }
    }

    protected void sendCreateResponseSharedObjectMessage(ID toContainerId, ID fromSharedObject, Throwable t, long ident) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendCreateResponseSharedObjectMessage(toContainerId, fromSharedObject, t, ident);
        }
    }

    protected ID[] sendCreateSharedObjectMessage(ID toContainerId, SharedObjectDescription sd) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            return super.sendCreateSharedObjectMessage(toContainerId, sd);
        }
    }

    protected void sendDispose(ID toContainerId, ID sharedObjectId) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendDispose(toContainerId, sharedObjectId);
        }
    }

    protected void sendDisposeSharedObjectMessage(ID toContainerId, ID fromSharedObject) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendDisposeSharedObjectMessage(toContainerId, fromSharedObject);
        }
    }

    protected void sendMessage(ID toContainerId, ID sharedObjectId, Object message) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendMessage(toContainerId, sharedObjectId, message);
        }
    }

    protected void sendSharedObjectMessage(ID toContainerId, ID fromSharedObject, Serializable data) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.sendSharedObjectMessage(toContainerId, fromSharedObject, data);
        }
    }

    protected void disconnect(Throwable exception) {
        synchronized (getConnectLock()) {
            // disconnect message
            if (isConnected()) {
                final ID groupID = getConnectedID();
                if (exception == null)
                    fireContainerEvent(new ContainerDisconnectingEvent(this.getID(), groupID));
                synchronized (connection) {
                    try {
                        connection.sendSynch(groupID, serialize(ContainerMessage.createLeaveGroupMessage(getID(), groupID, getNextSequenceNumber(), getLeaveData(groupID))));
                    } catch (final Exception e) {
                    }
                    synchronized (getGroupMembershipLock()) {
                        handleLeave(groupID, connection);
                    }
                }
                // notify listeners
                if (exception == null)
                    fireContainerEvent(new ContainerDisconnectedEvent(this.getID(), groupID));
                else
                    fireContainerEvent(new ContainerEjectedEvent(this.getID(), groupID, exception));
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#processDisconnect(org.eclipse.ecf.provider.comm.DisconnectEvent)
	 */
    protected void processDisconnect(DisconnectEvent evt) {
        // Get connect lock, and just return if this connection has been
        // terminated
        disconnect(evt.getException());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#processAsynch(org.eclipse.ecf.provider.comm.AsynchEvent)
	 */
    protected void processAsynch(AsynchEvent evt) throws IOException {
        // Get connect lock, then call super version
        synchronized (connectLock) {
            checkConnected();
            super.processAsynch(evt);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.generic.SOContainer#processSynch(org.eclipse.ecf.provider.comm.SynchEvent)
	 */
    protected Serializable processSynch(SynchEvent evt) throws IOException {
        synchronized (connectLock) {
            checkConnected();
            final IConnection conn = evt.getConnection();
            if (connection != conn)
                throw new //$NON-NLS-1$
                ConnectException(//$NON-NLS-1$
                "Container not connected");
            return super.processSynch(evt);
        }
    }

    protected boolean isConnected() {
        return (connectionState == CONNECTED);
    }

    protected boolean isConnecting() {
        return (connectionState == CONNECTING);
    }

    private void checkConnected() throws ConnectException {
        if (!isConnected())
            //$NON-NLS-1$
            throw new ConnectException("Container not connected");
    }

    protected ID handleConnectResponse(ID orginalTarget, Object serverData) throws Exception {
        final ContainerMessage aPacket = (ContainerMessage) serverData;
        final ID fromID = aPacket.getFromContainerID();
        //$NON-NLS-1$
        Assert.isNotNull(fromID, "fromID cannot be null");
        final ContainerMessage.ViewChangeMessage viewChangeMessage = (ContainerMessage.ViewChangeMessage) aPacket.getData();
        // throw if there
        if (!viewChangeMessage.isAdd()) {
            // We were refused by server...so we retrieve data and throw
            final Object data = viewChangeMessage.getData();
            if (data != null && data instanceof Exception)
                throw (Exception) data;
            //$NON-NLS-1$
            throw new InvalidObjectException("Invalid server response");
        }
        // Otherwise everything is OK to this point and we get the group member
        // IDs from server
        final ID[] ids = viewChangeMessage.getChangeIDs();
        //$NON-NLS-1$
        Assert.isNotNull(ids, "view change ids cannot be null");
        for (int i = 0; i < ids.length; i++) {
            final ID id = ids[i];
            if (id != null && !id.equals(getID()))
                addNewRemoteMember(id, null);
        }
        return fromID;
    }
}
