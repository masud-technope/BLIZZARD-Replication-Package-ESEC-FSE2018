/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.clients;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomInfo;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;

public class XMPPChatRoomClient {

    protected static String CONTAINER_TYPE = "ecf.xmpp.smack";

    Namespace namespace = null;

    IContainer container = null;

    IPresenceContainerAdapter presence = null;

    IChatMessageSender sender = null;

    ID userID = null;

    IChatRoomManager chatmanager = null;

    IChatRoomContainer chatroom = null;

    IChatRoomInfo roomInfo = null;

    // Interface for receiving messages
    IMessageReceiver receiver = null;

    public  XMPPChatRoomClient() {
        this(null);
    }

    public  XMPPChatRoomClient(IMessageReceiver receiver) {
        super();
        this.receiver = receiver;
    }

    protected IContainer createContainer() throws ECFException {
        // Create container
        container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);
        namespace = container.getConnectNamespace();
        return container;
    }

    protected IContainer getContainer() {
        return container;
    }

    protected Namespace getNamespace() {
        return namespace;
    }

    protected void setupPresenceAdapter() {
        // Get presence adapter off of container
        presence = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);
        // Get sender interface
        sender = presence.getChatManager().getChatMessageSender();
        // Setup message requestListener to handle incoming messages
        presence.getChatManager().addMessageListener(new IIMMessageListener() {

            public void handleMessageEvent(IIMMessageEvent messageEvent) {
                if (messageEvent instanceof IChatMessageEvent) {
                    final IChatMessage m = ((IChatMessageEvent) messageEvent).getChatMessage();
                    receiver.handleMessage(m);
                }
            }
        });
    }

    protected IPresenceContainerAdapter getPresenceContainer() {
        return presence;
    }

    public void connect(String account, String password) throws ECFException {
        createContainer();
        setupPresenceAdapter();
        // create target id
        final ID targetID = IDFactory.getDefault().createID(getNamespace(), account);
        // Now connect
        getContainer().connect(targetID, ConnectContextFactory.createPasswordConnectContext(password));
        // Get a local ID for user account
        userID = getID(account);
    }

    public IChatRoomContainer createChatRoom(String chatRoomName) throws Exception {
        // Create chat room container from manager
        roomInfo = presence.getChatRoomManager().getChatRoomInfo(chatRoomName);
        chatroom = roomInfo.createChatRoomContainer();
        return chatroom;
    }

    public IChatRoomInfo getChatRoomInfo() {
        return roomInfo;
    }

    private ID getID(String name) {
        try {
            return IDFactory.getDefault().createID(namespace, name);
        } catch (final IDCreateException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMessage(String jid, String msg) {
        if (sender != null) {
            try {
                sender.sendChatMessage(getID(jid), msg);
            } catch (final ECFException e) {
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
