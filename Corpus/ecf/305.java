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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;

/**
 * XHTML chat message.
 * 
 */
public class XHTMLChatMessage extends ChatMessage implements IXHTMLChatMessage {

    private static final long serialVersionUID = -1322099958260366438L;

    protected List xhtmlbodies;

    public  XHTMLChatMessage(ID fromID, ID threadID, Type type, String subject, String body, Map properties, List xhtmlbodies) {
        super(fromID, threadID, type, subject, body, properties);
        this.xhtmlbodies = (xhtmlbodies == null) ? new ArrayList() : xhtmlbodies;
    }

    public  XHTMLChatMessage(ID fromID, ID threadID, String subject, String body, Map properties, List xhtmlbodies) {
        this(fromID, threadID, IChatMessage.Type.CHAT, subject, body, properties, xhtmlbodies);
    }

    public  XHTMLChatMessage(ID fromID, Type type, String subject, String body, Map properties, List xhtmlbodies) {
        this(fromID, null, type, subject, body, properties, xhtmlbodies);
    }

    public  XHTMLChatMessage(ID fromID, String subject, String body, Map properties, List xhtmlbodies) {
        this(fromID, (ID) null, subject, body, properties, xhtmlbodies);
    }

    public  XHTMLChatMessage(ID fromID, String body, Map properties, List xhtmlbodies) {
        this(fromID, (String) null, body, properties, xhtmlbodies);
    }

    public  XHTMLChatMessage(ID fromID, Map properties, List xhtmlbodies) {
        this(fromID, (String) null, properties, xhtmlbodies);
    }

    public  XHTMLChatMessage(ID fromID, List xhtmlbodies) {
        this(fromID, (Map) null, xhtmlbodies);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IXHTMLChatMessage#getXTHMLBodies()
	 */
    public List getXTHMLBodies() {
        return xhtmlbodies;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ChatMessage[");
        //$NON-NLS-1$
        buf.append("fromID=").append(getFromID());
        //$NON-NLS-1$
        buf.append(";threadID=").append(getThreadID());
        //$NON-NLS-1$
        buf.append(";type=").append(getType());
        //$NON-NLS-1$
        buf.append(";subject=").append(getSubject());
        //$NON-NLS-1$
        buf.append(";body=").append(getBody());
        //$NON-NLS-1$
        buf.append(";props=").append(getProperties());
        //$NON-NLS-1$
        buf.append(";xhtmlbodies=").append(getXTHMLBodies());
        //$NON-NLS-1$
        buf.append("]");
        return buf.toString();
    }
}
