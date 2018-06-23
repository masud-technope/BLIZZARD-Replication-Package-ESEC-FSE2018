/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 * Boris Bokowski, IBM
 ******************************************************************************/
package org.eclipse.ecf.example.clients.applications;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.example.clients.IMessageReceiver;
import org.eclipse.ecf.example.clients.XMPPChatRoomClient;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessage;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageEvent;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * To be started as an application. Go to Run->Run..., create a new Eclipse
 * Application, select org.eclipse.ecf.example.clients.robot as the application
 * and make sure you have all required plug-ins.
 * 
 */
public class ChatRoomRobotApplication implements IApplication, IMessageReceiver, IIMMessageListener {

    private String senderAccount;

    private Object lock = new Object();

    private boolean done = false;

    private IChatRoomMessageSender sender;

    public Object start(IApplicationContext context) throws Exception {
        // process program arguments
        String[] originalArgs = (String[]) context.getArguments().get("application.args");
        if (originalArgs.length < 4) {
            System.out.println("Parameters:  <senderAccount> <senderPassword> <chatroomname>.  e.g. sender@gmail.com senderpassword mychatroom");
            return new Integer(-1);
        }
        senderAccount = originalArgs[0];
        // Create client
        final XMPPChatRoomClient client = new XMPPChatRoomClient(this);
        // connect to senderAccount using senderPassword
        client.connect(senderAccount, originalArgs[1]);
        // get chat room
        final IChatRoomContainer chatRoomContainer = client.createChatRoom(originalArgs[2]);
        // join/connect to chat room
        chatRoomContainer.connect(client.getChatRoomInfo().getRoomID(), null);
        System.out.println("ECF chat room robot sender=" + senderAccount + "  Connected to room: " + client.getChatRoomInfo().getRoomID().getName());
        // Add message listener to chat room
        chatRoomContainer.addMessageListener(this);
        // Get chat room message sender
        sender = chatRoomContainer.getChatRoomMessageSender();
        sender.sendMessage("Hi, I'm a robot. To get rid of me, send me a direct message.");
        synchronized (lock) {
            while (!done) {
                lock.wait();
            }
        }
        return IApplication.EXIT_OK;
    }

    public void handleMessage(IChatMessage chatMessage) {
        // direct message
        try {
            sender.sendMessage("gotta run");
        } catch (final ECFException e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            done = true;
            lock.notify();
        }
    }

    public void handleMessageEvent(IIMMessageEvent messageEvent) {
        if (messageEvent instanceof IChatRoomMessageEvent) {
            final IChatRoomMessage m = ((IChatRoomMessageEvent) messageEvent).getChatRoomMessage();
            handleChatRoomMessage(m.getFromID(), m.getMessage());
        }
    }

    private void handleChatRoomMessage(ID fromID, String messageBody) {
        // message in chat room
        if (fromID.getName().indexOf(senderAccount) != -1) {
            // my own message, don't respond
            return;
        }
        try {
            if (messageBody.indexOf("e") != -1) {
                sender.sendMessage("kewl");
            } else if (messageBody.indexOf("s") != -1) {
                sender.sendMessage(";-)");
            } else {
                sender.sendMessage("'s up?");
            }
        } catch (final ECFException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        synchronized (lock) {
            done = true;
            lock.notify();
        }
    }
}
