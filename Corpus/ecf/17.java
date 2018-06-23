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

import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.history.IHistory;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.*;
import org.eclipse.ecf.presence.search.message.IMessageSearchManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;

/**
 * Chat manager for XMPP container
 */
public class XMPPChatManager implements IChatManager {

    private final List messageListeners = new ArrayList();

    private final XMPPContainerPresenceHelper presenceHelper;

    private final IChatMessageSender chatMessageSender = new IChatMessageSender() {

        /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ecf.presence.im.IChatMessageSender#sendChatMessage(org
		 * .eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.identity.ID,
		 * org.eclipse.ecf.presence.im.IChatMessage.Type, java.lang.String,
		 * java.lang.String)
		 */
        public void sendChatMessage(ID toID, ID threadID, org.eclipse.ecf.presence.im.IChatMessage.Type type, String subject, String body, Map properties) throws ECFException {
            if (toID == null)
                throw new ECFException("receiver cannot be null");
            try {
                presenceHelper.getConnectionOrThrowIfNull().sendMessage(toID, threadID, XMPPChatManager.this.createMessageType(type), subject, body, properties);
            } catch (final Exception e) {
                throw new ECFException("sendChatMessage exception", e);
            }
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ecf.presence.im.IChatMessageSender#sendChatMessage(org
		 * .eclipse.ecf.core.identity.ID, java.lang.String)
		 */
        public void sendChatMessage(ID toID, String body) throws ECFException {
            sendChatMessage(toID, null, IChatMessage.Type.CHAT, null, body, null);
        }
    };

    protected ITypingMessageSender typingMessageSender = new ITypingMessageSender() {

        public void sendTypingMessage(ID toID, boolean isTyping, String body) throws ECFException {
            if (toID == null)
                throw new ECFException("receiver cannot be null");
            try {
                presenceHelper.sendTypingMessage(toID, isTyping, body);
            } catch (final Exception e) {
                throw new ECFException("sendChatMessage exception", e);
            }
        }
    };

    protected IHistoryManager historyManager = new IHistoryManager() {

        /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ecf.presence.im.IChatHistoryManager#getHistory(org.eclipse
		 * .ecf.core.identity.ID, java.util.Map)
		 */
        public IHistory getHistory(ID partnerID, Map options) {
            // XXX TODO provide local storage (with some
            return null;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
        public Object getAdapter(Class adapter) {
            if (adapter == null)
                return null;
            if (adapter.isInstance(this))
                return this;
            final IAdapterManager adapterManager = XmppPlugin.getDefault().getAdapterManager();
            return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
        }

        public boolean isActive() {
            return false;
        }

        public void setActive(boolean active) {
        // TODO Auto-generated method stub
        }
    };

    public  XMPPChatManager(XMPPContainerPresenceHelper presenceHelper) {
        this.presenceHelper = presenceHelper;
    }

    protected IChatMessage.Type createMessageType(Message.Type type) {
        if (type == null)
            return IChatMessage.Type.CHAT;
        if (type == Message.Type.chat) {
            return IChatMessage.Type.CHAT;
        } else if (type == Message.Type.headline) {
            return IChatMessage.Type.SYSTEM;
        } else
            return IChatMessage.Type.CHAT;
    }

    protected Message.Type createMessageType(IChatMessage.Type type) {
        if (type == null)
            return Message.Type.normal;
        if (type == IChatMessage.Type.CHAT) {
            return Message.Type.chat;
        } else if (type == IChatMessage.Type.SYSTEM) {
            return Message.Type.headline;
        } else
            return Message.Type.normal;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.presence.im.IChatManager#addChatMessageListener(org.eclipse
	 * .ecf.presence.im.IIMMessageListener)
	 */
    public void addMessageListener(IIMMessageListener listener) {
        synchronized (messageListeners) {
            messageListeners.add(listener);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatManager#getChatMessageSender()
	 */
    public IChatMessageSender getChatMessageSender() {
        return chatMessageSender;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.presence.im.IChatManager#removeChatMessageListener(org
	 * .eclipse.ecf.presence.im.IIMMessageListener)
	 */
    public void removeMessageListener(IIMMessageListener listener) {
        synchronized (messageListeners) {
            messageListeners.remove(listener);
        }
    }

    private void fireMessageEvent(IIMMessageEvent event) {
        List toNotify = null;
        synchronized (messageListeners) {
            toNotify = new ArrayList(messageListeners);
        }
        for (final Iterator i = toNotify.iterator(); i.hasNext(); ) {
            final IIMMessageListener l = (IIMMessageListener) i.next();
            l.handleMessageEvent(event);
        }
    }

    protected void fireChatMessage(ID fromID, ID threadID, Type type, String subject, String body, Map properties) {
        fireMessageEvent(new ChatMessageEvent(fromID, new ChatMessage(fromID, threadID, createMessageType(type), subject, body, properties)));
    }

    protected void fireTypingMessage(ID fromID, ITypingMessage typingMessage) {
        fireMessageEvent(new TypingMessageEvent(fromID, typingMessage));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatManager#getTypingMessageSender()
	 */
    public ITypingMessageSender getTypingMessageSender() {
        return typingMessageSender;
    }

    protected void fireXHTMLChatMessage(ID fromID, ID threadID, Type type, String subject, String body, Map properties, List xhtmlbodylist) {
        fireMessageEvent(new XHTMLChatMessageEvent(fromID, new XHTMLChatMessage(fromID, threadID, createMessageType(type), subject, body, properties, xhtmlbodylist)));
    }

    public IHistoryManager getHistoryManager() {
        return historyManager;
    }

    public void disconnect() {
        synchronized (messageListeners) {
            messageListeners.clear();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.presence.im.IChatManager#createChat(org.eclipse.ecf.core
	 * .identity.ID)
	 */
    public IChat createChat(ID targetUser, IIMMessageListener messageListener) throws ECFException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatManager#getMessageSearchManager()
	 */
    public IMessageSearchManager getMessageSearchManager() {
        return null;
    }
}
