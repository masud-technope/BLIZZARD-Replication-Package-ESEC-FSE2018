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
import org.eclipse.ecf.presence.IMMessage;

/**
 * Typing Message implementation class
 */
public class TypingMessage extends IMMessage implements ITypingMessage {

    private static final long serialVersionUID = 6534377119279656830L;

    protected boolean typing = false;

    protected String body = null;

    public  TypingMessage(ID fromID, boolean typing, String body) {
        super(fromID);
        this.typing = typing;
        this.body = body;
    }

    public  TypingMessage(ID fromID, String body) {
        super(fromID);
        if (body != null) {
            this.typing = true;
            this.body = body;
        } else {
            this.typing = false;
        }
    }

    public  TypingMessage(ID fromID) {
        //$NON-NLS-1$
        this(fromID, "");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.ITypingMessage#getBody()
	 */
    public String getBody() {
        return body;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.ITypingMessage#isTyping()
	 */
    public boolean isTyping() {
        return typing;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        return null;
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
        buf.append(";body=").append(getBody()).append("]");
        return buf.toString();
    }
}
