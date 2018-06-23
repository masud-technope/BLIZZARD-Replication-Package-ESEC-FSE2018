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
package org.eclipse.ecf.internal.provider.xmpp;

import java.net.URISyntaxException;
import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.internal.provider.xmpp.events.*;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.chatroom.*;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPRoomID;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

public class XMPPChatRoomContainerHelper implements ISharedObject {

    ISharedObjectConfig config = null;

    private final List messageListeners = new ArrayList();

    private Namespace usernamespace = null;

    private XMPPConnection connection = null;

    private final List participantListeners = new ArrayList();

    private ID roomID = null;

    private final List chatRoomContainerParticipants = Collections.synchronizedList(new ArrayList());

    protected void trace(String message) {
    }

    protected void addChatParticipantListener(IChatRoomParticipantListener listener) {
        synchronized (participantListeners) {
            participantListeners.add(listener);
        }
    }

    protected void removeChatParticipantListener(IChatRoomParticipantListener listener) {
        synchronized (participantListeners) {
            participantListeners.remove(listener);
        }
    }

    public  XMPPChatRoomContainerHelper(Namespace usernamespace, XMPPConnection conn) {
        super();
        this.usernamespace = usernamespace;
        this.connection = conn;
    }

    protected ISharedObjectContext getContext() {
        return config.getContext();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#init(org.eclipse.ecf.core.ISharedObjectConfig)
	 */
    public void init(ISharedObjectConfig initData) throws SharedObjectInitException {
        this.config = initData;
    }

    protected ID createUserIDFromName(String name) {
        ID result = null;
        try {
            result = new XMPPID(usernamespace, name);
            return result;
        } catch (final Exception e) {
            return null;
        }
    }

    protected Message.Type[] ALLOWED_MESSAGES = { Message.Type.groupchat };

    protected Message filterMessageType(Message msg) {
        for (int i = 0; i < ALLOWED_MESSAGES.length; i++) {
            if (ALLOWED_MESSAGES[i].equals(msg.getType())) {
                return msg;
            }
        }
        return null;
    }

    protected String canonicalizeRoomFrom(String from) {
        if (from == null)
            return null;
        final int atIndex = from.indexOf('@');
        String hostname = null;
        String username = null;
        final int index = from.indexOf("/");
        if (atIndex > 0 && index > 0) {
            hostname = from.substring(atIndex + 1, index);
            username = from.substring(index + 1);
            return username + "@" + hostname;
        }
        return from;
    }

    protected void fireMessageListeners(ID from, String body) {
        List toNotify = null;
        synchronized (messageListeners) {
            toNotify = new ArrayList(messageListeners);
        }
        for (final Iterator i = toNotify.iterator(); i.hasNext(); ) {
            final IIMMessageListener l = (IIMMessageListener) i.next();
            l.handleMessageEvent(new ChatRoomMessageEvent(from, new ChatRoomMessage(from, roomID, body)));
        }
    }

    protected String canonicalizeRoomTo(String to) {
        if (to == null)
            return null;
        final int index = to.indexOf("/");
        if (index > 0) {
            return to.substring(0, index);
        } else
            return to;
    }

    protected ID createRoomIDFromName(String from) {
        try {
            return new XMPPRoomID(usernamespace, connection, from);
        } catch (final URISyntaxException e) {
            return null;
        }
    }

    protected void handleMessageEvent(MessageEvent evt) {
        final Message msg = filterMessageType(evt.getMessage());
        if (msg != null)
            fireMessageListeners(createUserIDFromName(canonicalizeRoomFrom(msg.getFrom())), msg.getBody());
    }

    protected IPresence.Type createIPresenceType(Presence xmppPresence) {
        if (xmppPresence == null)
            return IPresence.Type.AVAILABLE;
        final Type type = xmppPresence.getType();
        if (type == Presence.Type.available) {
            return IPresence.Type.AVAILABLE;
        } else if (type == Presence.Type.error) {
            return IPresence.Type.ERROR;
        } else if (type == Presence.Type.subscribe) {
            return IPresence.Type.SUBSCRIBE;
        } else if (type == Presence.Type.subscribed) {
            return IPresence.Type.SUBSCRIBED;
        } else if (type == Presence.Type.unsubscribe) {
            return IPresence.Type.UNSUBSCRIBE;
        } else if (type == Presence.Type.unsubscribed) {
            return IPresence.Type.UNSUBSCRIBED;
        } else if (type == Presence.Type.unavailable) {
            return IPresence.Type.UNAVAILABLE;
        }
        return IPresence.Type.AVAILABLE;
    }

    protected IPresence.Mode createIPresenceMode(Presence xmppPresence) {
        if (xmppPresence == null)
            return IPresence.Mode.AVAILABLE;
        final Mode mode = xmppPresence.getMode();
        if (mode == Presence.Mode.available) {
            return IPresence.Mode.AVAILABLE;
        } else if (mode == Presence.Mode.away) {
            return IPresence.Mode.AWAY;
        } else if (mode == Presence.Mode.chat) {
            return IPresence.Mode.CHAT;
        } else if (mode == Presence.Mode.dnd) {
            return IPresence.Mode.DND;
        } else if (mode == Presence.Mode.xa) {
            return IPresence.Mode.EXTENDED_AWAY;
        }
        /*else if (mode == Presence.Mode.invisible) {
			return IPresence.Mode.INVISIBLE;
			}*/
        return IPresence.Mode.AVAILABLE;
    }

    protected IPresence createIPresence(Presence xmppPresence) {
        final String status = xmppPresence.getStatus();
        final IPresence newPresence = new org.eclipse.ecf.presence.Presence(createIPresenceType(xmppPresence), status, createIPresenceMode(xmppPresence));
        return newPresence;
    }

    protected void disconnect() {
        chatRoomContainerParticipants.clear();
        setRoomID(null);
    }

    protected void handlePresenceEvent(PresenceEvent evt) {
        final Presence xmppPresence = evt.getPresence();
        final String from = canonicalizeRoomFrom(xmppPresence.getFrom());
        final IPresence newPresence = createIPresence(xmppPresence);
        final ID fromID = createUserIDFromName(from);
        if (newPresence.getType().equals(IPresence.Type.AVAILABLE)) {
            if (!chatRoomContainerParticipants.contains(fromID))
                chatRoomContainerParticipants.add(fromID);
        } else
            chatRoomContainerParticipants.remove(fromID);
        fireParticipant(fromID, newPresence);
    }

    protected void handleChatMembershipEvent(ChatMembershipEvent evt) {
        final String from = canonicalizeRoomFrom(evt.getFrom());
        final ID fromID = createUserIDFromName(from);
        fireChatParticipant(fromID, evt.isAdd());
    }

    protected void fireParticipant(ID fromID, IPresence presence) {
        List toNotify = null;
        synchronized (participantListeners) {
            toNotify = new ArrayList(participantListeners);
        }
        for (final Iterator i = toNotify.iterator(); i.hasNext(); ) {
            final IChatRoomParticipantListener l = (IChatRoomParticipantListener) i.next();
            l.handlePresenceUpdated(fromID, presence);
        }
    }

    protected void fireChatParticipant(ID fromID, boolean join) {
        List toNotify = null;
        synchronized (participantListeners) {
            toNotify = new ArrayList(participantListeners);
        }
        for (final Iterator i = toNotify.iterator(); i.hasNext(); ) {
            final IChatRoomParticipantListener l = (IChatRoomParticipantListener) i.next();
            if (join) {
                l.handleArrived(new User(fromID));
            } else {
                l.handleDeparted(new User(fromID));
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#handleEvent(org.eclipse.ecf.core.util.Event)
	 */
    public void handleEvent(Event event) {
        trace("handleEvent(" + event + ")");
        if (event instanceof MessageEvent) {
            handleMessageEvent((MessageEvent) event);
        } else if (event instanceof PresenceEvent) {
            handlePresenceEvent((PresenceEvent) event);
        } else if (event instanceof ChatMembershipEvent) {
            handleChatMembershipEvent((ChatMembershipEvent) event);
        } else
            trace("unrecognized event " + event);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#handleEvents(org.eclipse.ecf.core.util.Event[])
	 */
    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; i++) {
            this.handleEvent(events[i]);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#dispose(org.eclipse.ecf.core.identity.ID)
	 */
    public void dispose(ID containerID) {
        synchronized (messageListeners) {
            messageListeners.clear();
        }
        synchronized (participantListeners) {
            participantListeners.clear();
        }
        synchronized (chatRoomContainerParticipants) {
            chatRoomContainerParticipants.clear();
        }
        this.config = null;
        this.connection = null;
        this.usernamespace = null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.isInstance(this))
            return this;
        final IAdapterManager adapterManager = XmppPlugin.getDefault().getAdapterManager();
        return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
    }

    /**
	 * @param msgListener
	 */
    protected void addChatRoomMessageListener(IIMMessageListener msgListener) {
        synchronized (messageListeners) {
            messageListeners.add(msgListener);
        }
    }

    /**
	 * @param msgListener
	 */
    public void removeChatRoomMessageListener(IIMMessageListener msgListener) {
        synchronized (messageListeners) {
            messageListeners.remove(msgListener);
        }
    }

    /**
	 * @param remoteServerID
	 */
    protected void setRoomID(ID roomID) {
        this.roomID = roomID;
    }

    /**
	 * @return array of IDs of chat room participants.
	 */
    public ID[] getChatRoomParticipants() {
        return (ID[]) chatRoomContainerParticipants.toArray(new ID[] {});
    }
}
