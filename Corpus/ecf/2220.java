/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.NameCallback;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddException;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.xmpp.events.ChatMembershipEvent;
import org.eclipse.ecf.internal.provider.xmpp.events.IQEvent;
import org.eclipse.ecf.internal.provider.xmpp.events.MessageEvent;
import org.eclipse.ecf.internal.provider.xmpp.events.PresenceEvent;
import org.eclipse.ecf.internal.provider.xmpp.smack.ECFConnection;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomAdminListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomAdminSender;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.ecf.presence.chatroom.IChatRoomParticipantListener;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.IChatMessage.Type;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.ContainerMessage;
import org.eclipse.ecf.provider.generic.SOConfig;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.SOContext;
import org.eclipse.ecf.provider.generic.SOWrapper;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.provider.xmpp.identity.XMPPRoomID;
import org.eclipse.osgi.util.NLS;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

public class XMPPChatRoomContainer extends ClientSOContainer implements IChatRoomContainer {

    //$NON-NLS-1$
    private static final String CONTAINER_HELPER_ID = XMPPContainer.class.getName() + ".xmppgroupchathandler";

    private ID containerHelperID;

    private XMPPChatRoomContainerHelper containerHelper;

    private MultiUserChat multiuserchat;

    private List chatRoomAdminListeners;

    private IChatRoomAdminSender chatRoomAdminSender;

    public  XMPPChatRoomContainer(ISharedObjectContainerConfig config, ECFConnection conn, Namespace usernamespace) throws IDCreateException {
        super(config);
        this.connection = conn;
        this.config = config;
        this.containerHelperID = IDFactory.getDefault().createStringID(CONTAINER_HELPER_ID);
        this.containerHelper = new XMPPChatRoomContainerHelper(usernamespace, getXMPPConnection());
        this.chatRoomAdminListeners = new ArrayList();
    }

    protected void sendInvitation(ID toUser, String subject, String body) throws ECFException {
        if (toUser == null)
            throw new ECFException(Messages.XMPPChatRoomContainer_EXCEPTION_TARGET_USER_NOT_NULL);
        synchronized (getConnectLock()) {
            if (multiuserchat == null)
                throw new ContainerConnectException(Messages.XMPPChatRoomContainer_EXCEPTION_NOT_CONNECTED);
            //$NON-NLS-1$
            multiuserchat.invite(toUser.getName(), (body == null) ? "" : body);
        }
    }

    public  XMPPChatRoomContainer(ECFConnection conn, Namespace usernamespace) throws IDCreateException {
        this(new SOContainerConfig(IDFactory.getDefault().createGUID()), conn, usernamespace);
    }

    public void dispose() {
        disconnect();
        if (containerHelperID != null) {
            getSharedObjectManager().removeSharedObject(containerHelperID);
            containerHelperID = null;
        }
        if (containerHelper != null)
            containerHelper.dispose(getID());
        containerHelper = null;
        if (chatRoomAdminListeners != null)
            chatRoomAdminListeners.clear();
        chatRoomAdminListeners = null;
        super.dispose();
    }

    protected void sendMessage(ContainerMessage data) throws IOException {
        synchronized (getConnectLock()) {
            final ID toID = data.getToContainerID();
            if (toID == null) {
                data.setToContainerID(remoteServerID);
            }
            super.sendMessage(data);
        }
    }

    protected void handleChatMessage(Message mess) throws IOException {
        final SOWrapper wrap = getSharedObjectWrapper(containerHelperID);
        if (wrap != null) {
            wrap.deliverEvent(new MessageEvent(mess));
        }
    }

    protected boolean verifyToIDForSharedObjectMessage(ID toID) {
        return true;
    }

    public void handleContainerMessage(ContainerMessage mess) throws IOException {
        if (mess == null) {
            //$NON-NLS-1$
            debug("got null container message...ignoring");
            return;
        }
        final Object data = mess.getData();
        if (data instanceof ContainerMessage.CreateMessage) {
            handleCreateMessage(mess);
        } else if (data instanceof ContainerMessage.CreateResponseMessage) {
            handleCreateResponseMessage(mess);
        } else if (data instanceof ContainerMessage.SharedObjectMessage) {
            handleSharedObjectMessage(mess);
        } else if (data instanceof ContainerMessage.SharedObjectDisposeMessage) {
            handleSharedObjectDisposeMessage(mess);
        } else {
            //$NON-NLS-1$
            debug("got unrecognized container message...ignoring: " + mess);
        }
    }

    protected void handleIQMessage(IQ mess) throws IOException {
        final SOWrapper wrap = getSharedObjectWrapper(containerHelperID);
        if (wrap != null) {
            wrap.deliverEvent(new IQEvent(mess));
        }
    }

    protected void handlePresenceMessage(Presence mess) throws IOException {
        final SOWrapper wrap = getSharedObjectWrapper(containerHelperID);
        if (wrap != null) {
            wrap.deliverEvent(new PresenceEvent(mess));
        }
    }

    protected void handleChatMembershipEvent(String from, boolean add) {
        final SOWrapper wrap = getSharedObjectWrapper(containerHelperID);
        if (wrap != null) {
            wrap.deliverEvent(new ChatMembershipEvent(from, add));
        }
    }

    protected void handleXMPPMessage(Packet aPacket) {
        try {
            if (aPacket instanceof IQ) {
                handleIQMessage((IQ) aPacket);
            } else if (aPacket instanceof Message) {
                handleChatMessage((Message) aPacket);
            } else if (aPacket instanceof Presence) {
                handlePresenceMessage((Presence) aPacket);
            } else {
                // unexpected message
                debug(//$NON-NLS-1$
                "got unexpected packet " + aPacket);
            }
        } catch (final IOException e) {
            traceStack("Exception in handleXMPPMessage", e);
        }
    }

    protected XMPPConnection getXMPPConnection() {
        return ((ECFConnection) getConnection()).getXMPPConnection();
    }

    protected void addSharedObjectToContainer(ID remote) throws SharedObjectAddException {
        getSharedObjectManager().addSharedObject(containerHelperID, containerHelper, new HashMap());
    }

    protected void cleanUpConnectFail() {
        if (containerHelper != null) {
            getSharedObjectManager().removeSharedObject(containerHelperID);
            containerHelper = null;
            containerHelperID = null;
        }
        connectionState = DISCONNECTED;
        remoteServerID = null;
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(XmppPlugin.getDefault().getRoomNamespaceIdentifier());
    }

    public void connect(ID remote, IConnectContext connectContext) throws ContainerConnectException {
        if (!(remote instanceof XMPPRoomID)) {
            throw new ContainerConnectException(NLS.bind(Messages.XMPPChatRoomContainer_Exception_Connect_Wrong_Type, remote));
        }
        final XMPPRoomID roomID = (XMPPRoomID) remote;
        fireContainerEvent(new ContainerConnectingEvent(this.getID(), remote, connectContext));
        synchronized (getConnectLock()) {
            try {
                connectionState = CONNECTING;
                remoteServerID = null;
                addSharedObjectToContainer(remote);
                multiuserchat = new MultiUserChat(getXMPPConnection(), roomID.getMucString());
                // Get nickname from join context
                String nick = null;
                try {
                    final Callback[] callbacks = new Callback[1];
                    callbacks[0] = new NameCallback(Messages.XMPPChatRoomContainer_NAME_CALLBACK_NICK, roomID.getNickname());
                    if (connectContext != null) {
                        final CallbackHandler handler = connectContext.getCallbackHandler();
                        if (handler != null) {
                            handler.handle(callbacks);
                        }
                    }
                    if (callbacks[0] instanceof NameCallback) {
                        final NameCallback cb = (NameCallback) callbacks[0];
                        nick = cb.getName();
                    }
                } catch (final Exception e) {
                    throw new ContainerConnectException(Messages.XMPPChatRoomContainer_EXCEPTION_CALLBACKHANDLER, e);
                }
                String nickname = null;
                if (nick == null || //$NON-NLS-1$
                nick.equals(//$NON-NLS-1$
                ""))
                    nickname = roomID.getNickname();
                else
                    nickname = nick;
                multiuserchat.addSubjectUpdatedListener(new SubjectUpdatedListener() {

                    public void subjectUpdated(String subject, String from) {
                        fireSubjectUpdated(subject, from);
                    }
                });
                multiuserchat.addMessageListener(new PacketListener() {

                    public void processPacket(Packet arg0) {
                        handleXMPPMessage(arg0);
                    }
                });
                multiuserchat.addParticipantListener(new PacketListener() {

                    public void processPacket(Packet arg0) {
                        handleXMPPMessage(arg0);
                    }
                });
                multiuserchat.addParticipantStatusListener(new ParticipantStatusListener() {

                    public void joined(String arg0) {
                        handleChatMembershipEvent(arg0, true);
                    }

                    public void left(String arg0) {
                        handleChatMembershipEvent(arg0, false);
                    }

                    public void voiceGranted(String arg0) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("voiceGranted(" + arg0 + ")");
                    }

                    public void voiceRevoked(String arg0) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("voiceRevoked(" + arg0 + ")");
                    }

                    public void membershipGranted(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("membershipGranted(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void membershipRevoked(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("membershipRevoked(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void moderatorGranted(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("moderatorGranted(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void moderatorRevoked(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("moderatorRevoked(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void ownershipGranted(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("ownershipGranted(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void ownershipRevoked(String arg0) {
                        // TODO Auto-generated method stub
                        System.out.println("ownershipRevoked(" + arg0 + //$NON-NLS-1$
                        ")");
                    }

                    public void adminGranted(String arg0) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("adminGranted(" + arg0 + ")");
                    }

                    public void adminRevoked(String arg0) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("adminRevoked(" + arg0 + ")");
                    }

                    public void kicked(String arg0, String arg1, String arg2) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("kicked(" + arg0 + "," + arg1 + "," + //$NON-NLS-1$ //$NON-NLS-2$
                        arg2 + //$NON-NLS-1$ //$NON-NLS-2$
                        ")");
                    }

                    public void banned(String arg0, String arg1, String arg2) {
                        // TODO Auto-generated method stub
                        //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println("banned(" + arg0 + "," + arg1 + "," + //$NON-NLS-1$ //$NON-NLS-2$
                        arg2 + //$NON-NLS-1$ //$NON-NLS-2$
                        ")");
                    }

                    public void nicknameChanged(String arg0, String arg1) {
                        // TODO Auto-generated method stub
                        System.out.println("nicknameChanged(" + arg0 + "," + //$NON-NLS-1$ //$NON-NLS-2$
                        arg1 + //$NON-NLS-1$ //$NON-NLS-2$
                        ")");
                    }
                });
                multiuserchat.addInvitationRejectionListener(new InvitationRejectionListener() {

                    public void invitationDeclined(String arg0, String arg1) {
                        // TODO Auto-generated method stub
                        System.out.println("invitationDeclined(" + arg0 + "," + //$NON-NLS-1$ //$NON-NLS-2$
                        arg1 + //$NON-NLS-1$ //$NON-NLS-2$
                        ")");
                    }
                });
                multiuserchat.join(nickname);
                connectionState = CONNECTED;
                remoteServerID = roomID;
                containerHelper.setRoomID(remoteServerID);
                fireContainerEvent(new ContainerConnectedEvent(this.getID(), roomID));
            } catch (final Exception e) {
                cleanUpConnectFail();
                final ContainerConnectException ce = new ContainerConnectException(NLS.bind(Messages.XMPPChatRoomContainer_EXCEPTION_JOINING_ROOM, roomID));
                ce.setStackTrace(e.getStackTrace());
                throw ce;
            }
        }
    }

    /**
	 * @param subject
	 * @param from
	 */
    protected void fireSubjectUpdated(String subject, String from) {
        List notify = null;
        synchronized (chatRoomAdminListeners) {
            notify = new ArrayList(chatRoomAdminListeners);
        }
        for (final Iterator i = notify.iterator(); i.hasNext(); ) {
            final IChatRoomAdminListener l = (IChatRoomAdminListener) i.next();
            l.handleSubjectChange(containerHelper.createUserIDFromName(from), subject);
        }
    }

    public void disconnect() {
        final ID groupID = getConnectedID();
        fireContainerEvent(new ContainerDisconnectingEvent(this.getID(), groupID));
        synchronized (getConnectLock()) {
            // If we are currently connected
            if (isConnected()) {
                try {
                    multiuserchat.leave();
                } catch (final Exception e) {
                    traceStack("Exception in multi user chat.leave", e);
                }
            }
            connectionState = DISCONNECTED;
            remoteServerID = null;
            if (containerHelper != null)
                containerHelper.disconnect();
            this.connection = null;
        }
        // notify listeners
        fireContainerEvent(new ContainerDisconnectedEvent(this.getID(), groupID));
    }

    protected SOContext createSharedObjectContext(SOConfig soconfig, IQueueEnqueue queue) {
        return new XMPPContainerContext(soconfig.getSharedObjectID(), soconfig.getHomeContainerID(), this, soconfig.getProperties(), queue);
    }

    protected ID createChatRoomID(String groupName) throws IDCreateException {
        String username = getXMPPConnection().getUser();
        final int atIndex = username.indexOf('@');
        if (atIndex > 0)
            username = username.substring(0, atIndex);
        final String host = getXMPPConnection().getHost();
        final Namespace ns = getConnectNamespace();
        final ID targetID = IDFactory.getDefault().createID(ns, new Object[] { username, host, null, groupName, username });
        return targetID;
    }

    protected ISynchAsynchConnection createConnection(ID remoteSpace, Object data) throws ConnectionCreateException {
        return null;
    }

    IChatMessageSender privateSender = new IChatMessageSender() {

        public void sendChatMessage(ID toID, ID threadID, Type type, String subject, String body, Map properties) throws ECFException {
        // TODO Auto-generated method stub
        }

        public void sendChatMessage(ID toID, String body) throws ECFException {
        // TODO Auto-generated method stub
        }
    };

    public IChatMessageSender getPrivateMessageSender() {
        return privateSender;
    }

    public IChatRoomMessageSender getChatRoomMessageSender() {
        return new IChatRoomMessageSender() {

            public void sendMessage(String messageBody) throws ECFException {
                if (multiuserchat != null) {
                    try {
                        multiuserchat.sendMessage(messageBody);
                    } catch (final Exception e) {
                        final ECFException except = new ECFException(Messages.XMPPChatRoomContainer_EXCEPTION_SEND_MESSAGE, e);
                        throw except;
                    }
                }
            }
        };
    }

    public void connect(String groupName) throws ContainerConnectException {
        ID targetID = null;
        try {
            targetID = createChatRoomID(groupName);
        } catch (final IDCreateException e) {
            throw new ContainerConnectException(Messages.XMPPChatRoomContainer_EXCEPTION_CREATING_ROOM_ID, e);
        }
        this.connect(targetID, null);
    }

    public void addChatRoomParticipantListener(IChatRoomParticipantListener participantListener) {
        if (containerHelper != null) {
            containerHelper.addChatParticipantListener(participantListener);
        }
    }

    public void removeChatRoomParticipantListener(IChatRoomParticipantListener participantListener) {
        if (containerHelper != null) {
            containerHelper.removeChatParticipantListener(participantListener);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#addMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
    public void addMessageListener(IIMMessageListener listener) {
        containerHelper.addChatRoomMessageListener(listener);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#removeMessageListener(org.eclipse.ecf.presence.IIMMessageListener)
	 */
    public void removeMessageListener(IIMMessageListener listener) {
        containerHelper.removeChatRoomMessageListener(listener);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#addChatRoomSubjectListener(org.eclipse.ecf.presence.chatroom.IChatRoomAdminListener)
	 */
    public void addChatRoomAdminListener(IChatRoomAdminListener subjectListener) {
        if (subjectListener == null)
            return;
        chatRoomAdminListeners.add(subjectListener);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#removeChatRoomSubjectListener(org.eclipse.ecf.presence.chatroom.IChatRoomAdminListener)
	 */
    public void removeChatRoomAdminListener(IChatRoomAdminListener subjectListener) {
        if (subjectListener == null)
            return;
        chatRoomAdminListeners.remove(subjectListener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#getChatRoomParticipants()
	 */
    public ID[] getChatRoomParticipants() {
        return containerHelper.getChatRoomParticipants();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomContainer#getChatRoomAdminSender()
	 */
    public IChatRoomAdminSender getChatRoomAdminSender() {
        synchronized (this) {
            if (chatRoomAdminSender == null) {
                chatRoomAdminSender = new IChatRoomAdminSender() {

                    public void sendSubjectChange(String newsubject) throws ECFException {
                        if (multiuserchat == null)
                            throw new ECFException(Messages.XMPPChatRoomContainer_EXCEPTION_NOT_CONNECTED);
                        try {
                            multiuserchat.changeSubject(newsubject);
                        } catch (final XMPPException e) {
                            throw new ECFException(e);
                        }
                    }
                };
            }
        }
        return chatRoomAdminSender;
    }
}
