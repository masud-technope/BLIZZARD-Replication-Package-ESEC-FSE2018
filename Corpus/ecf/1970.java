/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.vbulletin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMessageBase;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.bulletinboard.IllegalWriteException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.provider.vbulletin.identity.ThreadMessageID;

public class ThreadMessage extends VBObject implements IThreadMessage {

    private static final String E_READ_ONLY = "This message is read only.";

    protected String message;

    protected IMember author;

    private ThreadMessageID id;

    protected Thread thread;

    protected int number;

    protected Date timePosted;

    public  ThreadMessage(ThreadMessageID id, String name) {
        super(name, READ_ONLY);
        this.id = id;
        this.number = -1;
    }

    public  ThreadMessage() {
        super(null, READ_WRITE);
        this.number = -1;
    }

    public int getMessageNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public IMember getFrom() {
        return author;
    }

    public IMessageBase getReplyTo() {
        // TODO Implement reply relationships
        return null;
    }

    public void setReplyTo(IMessageBase message) throws IllegalWriteException {
    // TODO Auto-generated method stub
    }

    public ID getID() {
        return id;
    }

    public URL getURL() {
        try {
            return new URL(bb.getURL(), "showpost.php?p=" + id.getLongValue());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public IThread getThread() {
        return thread;
    }

    /**
	 * @param thread
	 *            The thread to set.
	 */
    protected void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setName(String name) throws IllegalWriteException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        this.name = name;
    }

    public void setMessage(String message) throws IllegalWriteException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThreadMessage) {
            ThreadMessage grp = (ThreadMessage) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Date getTimePosted() {
        return timePosted;
    }
}
