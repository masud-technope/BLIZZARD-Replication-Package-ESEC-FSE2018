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

public class XHTMLChatMessageEvent extends ChatMessageEvent implements IXHTMLChatMessageEvent {

    /**
	 * @param fromID
	 * @param message
	 */
    public  XHTMLChatMessageEvent(ID fromID, IXHTMLChatMessage message) {
        super(fromID, message);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IXHTMLChatMessageEvent#getXHTMLChatMessage()
	 */
    public IXHTMLChatMessage getXHTMLChatMessage() {
        return (IXHTMLChatMessage) super.getChatMessage();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("XHTMLChatMessageEvent[");
        //$NON-NLS-1$
        buf.append("fromID=").append(getFromID());
        //$NON-NLS-1$
        buf.append(";xhtmlchatmessage=").append(getXHTMLChatMessage()).append(//$NON-NLS-1$
        "]");
        return buf.toString();
    }
}
