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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IMMessage;

/**
 * Chat message concrete class. Implements IChatMessage.
 */
public class ChatMessage extends IMMessage implements IChatMessage {

    private static final long serialVersionUID = 483032454041915204L;

    protected ID threadID;

    protected IChatMessage.Type type;

    protected String subject;

    protected String body;

    protected Map properties;

    public  ChatMessage(ID fromID, ID threadID, IChatMessage.Type type, String subject, String body, Map properties) {
        super(fromID);
        this.threadID = threadID;
        this.type = type;
        this.subject = subject;
        //$NON-NLS-1$
        this.body = (body == null) ? "" : body;
        this.properties = (properties == null) ? new HashMap() : properties;
    }

    public  ChatMessage(ID fromID, ID threadID, String subject, String body, Map properties) {
        this(fromID, threadID, IChatMessage.Type.CHAT, subject, body, properties);
    }

    public  ChatMessage(ID fromID, IChatMessage.Type type, String subject, String body, Map properties) {
        this(fromID, null, type, subject, body, properties);
    }

    public  ChatMessage(ID fromID, String subject, String body, Map properties) {
        this(fromID, (ID) null, subject, body, properties);
    }

    public  ChatMessage(ID fromID, String body, Map properties) {
        this(fromID, null, body, properties);
    }

    public  ChatMessage(ID fromID, String body) {
        this(fromID, body, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessage#getBody()
	 */
    public String getBody() {
        return body;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessage#getSubject()
	 */
    public String getSubject() {
        return subject;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessage#getThreadID()
	 */
    public ID getThreadID() {
        return threadID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IChatMessage#getType()
	 */
    public Type getType() {
        return type;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.im.IIMMessage#getProperties()
	 */
    public Map getProperties() {
        return properties;
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
        buf.append("]");
        return buf.toString();
    }
}
