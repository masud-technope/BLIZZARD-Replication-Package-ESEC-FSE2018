/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.events;

import java.util.Iterator;
import org.eclipse.ecf.core.util.Event;
import org.jivesoftware.smack.packet.Message;

public class MessageEvent implements Event {

    protected Message message = null;

    protected Iterator xhtmlbodies = null;

    public  MessageEvent(Message message) {
        this(message, null);
    }

    public  MessageEvent(Message message, Iterator xhtmlbodies) {
        this.message = message;
        this.xhtmlbodies = xhtmlbodies;
    }

    public Message getMessage() {
        return message;
    }

    public Iterator getXHTMLBodies() {
        return xhtmlbodies;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("MessageEvent[");
        buf.append(message).append(";").append((message == null) ? "" : message.toXML()).append("]");
        return buf.toString();
    }
}
