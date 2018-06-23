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

public class TypingMessageEvent implements ITypingMessageEvent {

    protected ID fromID;

    protected ITypingMessage typingMessage;

    public  TypingMessageEvent(ID fromID, ITypingMessage message) {
        this.fromID = fromID;
        this.typingMessage = message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.ITypingMessageEvent#getTypingMessage()
	 */
    public ITypingMessage getTypingMessage() {
        return typingMessage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IIMMessageEvent#getFromID()
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
        StringBuffer buf = new StringBuffer("TypingMessage[");
        //$NON-NLS-1$
        buf.append("fromID=").append(getFromID());
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";typingMessage=").append(getTypingMessage()).append("]");
        return buf.toString();
    }
}
