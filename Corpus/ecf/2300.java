/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.provider.irc.bot;

import java.util.List;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomInfo;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessage;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageEvent;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.ecf.provider.irc.internal.bot.IBotEntry;
import org.eclipse.ecf.provider.irc.internal.bot.ICommandEntry;

public class Bot implements IIMMessageListener {

    private IBotEntry bot;

    private IContainer container;

    private IChatRoomManager manager;

    private Namespace namespace;

    private IChatRoomMessageSender sender;

    private static String CONTAINER_TYPE = "ecf.irc.irclib";

    public  Bot(IBotEntry bot) {
        this.bot = bot;
        start();
    }

    private void start() {
        try {
            setup();
        } catch (ECFException e) {
            e.printStackTrace();
        }
    }

    protected void setup() throws ECFException {
        if (container == null) {
            container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);
            namespace = container.getConnectNamespace();
        }
        manager = (IChatRoomManager) container.getAdapter(IChatRoomManager.class);
        ID targetID = IDFactory.getDefault().createID(namespace, "irc://" + bot.getName() + "@" + bot.getServer());
        container.connect(targetID, null);
        IChatRoomInfo room = manager.getChatRoomInfo(bot.getChannel());
        IChatRoomContainer roomContainer = room.createChatRoomContainer();
        roomContainer.connect(room.getRoomID(), null);
        roomContainer.addMessageListener(this);
        sender = roomContainer.getChatRoomMessageSender();
    }

    public void handleMessageEvent(IIMMessageEvent event) {
        if (event instanceof IChatRoomMessageEvent) {
            IChatRoomMessageEvent roomEvent = (IChatRoomMessageEvent) event;
            IChatRoomMessage message = roomEvent.getChatRoomMessage();
            List commands = bot.getCommands();
            for (int i = 0; i < commands.size(); i++) {
                ICommandEntry entry = (ICommandEntry) commands.get(i);
                entry.execute(message.getMessage(), sender);
            }
        }
    }
}
