/*******************************************************************************
 * Copyright (c) 2008 Abner Ballardo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abner Ballardo <modlost@modlost.net> - initial API and implementation via bug 197745
 ******************************************************************************/
package org.eclipse.ecf.internal.irc.ui.hyperlink;

import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.ui.chatroom.ChatRoomManagerView;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class IRCChannelHyperlink implements IHyperlink {

    private final Region region;

    private final String channel;

    private String typeLabel;

    private String hyperlinkText;

    private final ChatRoomManagerView view;

    public  IRCChannelHyperlink(ChatRoomManagerView view, String channel, Region region) {
        this.channel = channel;
        this.region = region;
        this.view = view;
    }

    public IRegion getHyperlinkRegion() {
        return this.region;
    }

    public String getHyperlinkText() {
        return this.hyperlinkText;
    }

    public String getTypeLabel() {
        return this.typeLabel;
    }

    public void open() {
        IChatRoomContainer container = view.getRootChatRoomContainer();
        final IChatRoomManager manager = (IChatRoomManager) container.getAdapter(IChatRoomManager.class);
        view.joinRoom(manager.getChatRoomInfo(channel), "");
    }
}
