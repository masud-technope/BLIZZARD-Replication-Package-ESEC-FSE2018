/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.clients;

import java.util.Map;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;

public class XMPPChatClient {

    IContainer container = null;

    IPresenceContainerAdapter presence = null;

    IChatMessageSender sender = null;

    ID userID = null;

    // Interface for receiving messages
    IMessageReceiver receiver = null;

    IPresenceListener presenceListener = null;

    public  XMPPChatClient() {
        this(null);
    }

    public  XMPPChatClient(IMessageReceiver receiver) {
        super();
        this.receiver = receiver;
    }

    public  XMPPChatClient(IMessageReceiver receiver, IPresenceListener presenceListener) {
        this(receiver);
        this.presenceListener = presenceListener;
    }

    public IContainer setupContainer() throws ECFException {
        if (container == null) {
            container = ContainerFactory.getDefault().createContainer("ecf.xmpp.smack");
        }
        return container;
    }

    public IContainer getContainer() {
        return container;
    }

    public void setupPresence() throws ECFException {
        if (presence == null) {
            presence = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);
            if (presence == null)
                throw new ECFException("adapter is null");
            sender = presence.getChatManager().getChatMessageSender();
            presence.getChatManager().addMessageListener(new IIMMessageListener() {

                public void handleMessageEvent(IIMMessageEvent messageEvent) {
                    if (messageEvent instanceof IChatMessageEvent) {
                        IChatMessage m = ((IChatMessageEvent) messageEvent).getChatMessage();
                        if (receiver != null) {
                            receiver.handleMessage(m);
                        }
                    }
                }
            });
            if (presenceListener != null) {
                presence.getRosterManager().addPresenceListener(presenceListener);
            }
        }
    }

    public void connect(String account, String password) throws ECFException {
        setupContainer();
        setupPresence();
        doConnect(account, password);
    }

    public void doConnect(String account, String password) throws ECFException {
        // Now connect
        userID = createID(account);
        container.connect(userID, ConnectContextFactory.createPasswordConnectContext(password));
    }

    public ID createID(String name) {
        return IDFactory.getDefault().createID(container.getConnectNamespace(), name);
    }

    public void sendChat(String jid, String msg) {
        if (sender != null) {
            try {
                sender.sendChatMessage(createID(jid), msg);
            } catch (ECFException e) {
                e.printStackTrace();
            }
        }
    }

    /**
	 * @since 2.0
	 */
    public void sendChat(ID targetID, String msg) {
        if (sender != null) {
            try {
                sender.sendChatMessage(targetID, msg);
            } catch (ECFException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendProperties(String jid, Map properties) {
        if (sender != null) {
            try {
                sender.sendChatMessage(createID(jid), null, IChatMessage.Type.CHAT, null, null, properties);
            } catch (ECFException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isConnected() {
        if (container == null)
            return false;
        return (container.getConnectedID() != null);
    }

    public synchronized void close() {
        if (container != null) {
            container.dispose();
            container = null;
            presence = null;
            sender = null;
            receiver = null;
            userID = null;
        }
    }
}
