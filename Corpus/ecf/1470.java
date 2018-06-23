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
package org.eclipse.ecf.presence.im;

import org.eclipse.ecf.core.identity.ID;

/**
 * Chat message event class
 */
public class ChatMessageEvent implements IChatMessageEvent {

    protected ID fromID;

    protected IChatMessage message;

    protected IChat chat;

    public  ChatMessageEvent(ID fromID, IChatMessage message, IChat chat) {
        this.fromID = fromID;
        this.message = message;
        this.chat = chat;
    }

    public  ChatMessageEvent(ID fromID, IChatMessage message) {
        this(fromID, message, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessageEvent#getFromID()
	 */
    public ID getFromID() {
        return fromID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessageEvent#getMessage()
	 */
    public IChatMessage getChatMessage() {
        return message;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatMessageEvent#getChat()
	 */
    public IChat getChat() {
        return chat;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ChatMessageEvent[");
        //$NON-NLS-1$
        buf.append("fromID=").append(getFromID());
        //$NON-NLS-1$
        buf.append(";message=").append(message);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";chat=").append(chat).append("]");
        return buf.toString();
    }
}
