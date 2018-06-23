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
package org.eclipse.ecf.presence.chatroom;

import org.eclipse.ecf.core.identity.ID;

/**
 * Chat room message event. This event class is used to deliver an
 * {@link IChatRoomMessage} as an event.
 */
public class ChatRoomMessageEvent implements IChatRoomMessageEvent {

    protected ID fromID;

    protected IChatRoomMessage chatMessage;

    public  ChatRoomMessageEvent(ID fromID, IChatRoomMessage message) {
        this.fromID = fromID;
        this.chatMessage = message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.chatroom.IChatRoomMessageEvent#getChatRoomMessage()
	 */
    public IChatRoomMessage getChatRoomMessage() {
        return chatMessage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.IIMMessageEvent#getFromID()
	 */
    public ID getFromID() {
        return fromID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ChatRoomMessageEvent[");
        //$NON-NLS-1$
        buf.append("fromID=").append(getFromID());
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";chatMessage=").append(chatMessage).append("]");
        return buf.toString();
    }
}
