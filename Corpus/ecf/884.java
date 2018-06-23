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
package org.eclipse.ecf.internal.provider.phpbb;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IForum;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IPoll;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.bulletinboard.IllegalWriteException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadID;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadMessageID;

public class Thread extends PHPBBObject implements IThread {

    // private static final Logger log = Logger.getLogger(Thread.class);
    private static final String E_READ_ONLY = "This thread is read only.";

    private ThreadID id;

    protected Forum forum;

    private ThreadMessage prePostMessage;

    private IMember author;

    public ThreadMessageID lastReadMessageId;

    public  Thread() {
        super(null, READ_WRITE);
        this.prePostMessage = new ThreadMessage();
        prePostMessage.setThread(this);
    }

    public  Thread(ThreadID id, String name) {
        super(name, READ_WRITE);
        this.id = id;
    }

    public IPoll getPoll() {
        return null;
    }

    public IForum getForum() {
        return forum;
    }

    public List<IThreadMessage> getMessages() throws BBException {
        // TODO Auto-generated method stub
        return getNewMessages();
    }

    public List<IThreadMessage> getNewMessages() throws BBException {
        ThreadBrowser2 browser = new ThreadBrowser2((AbstractBulletinBoard) bb, this);
        List<IThreadMessage> msgs = browser.fetchNewMessages();
        if (msgs.size() > 0) {
            lastReadMessageId = (ThreadMessageID) msgs.get(msgs.size() - 1).getID();
            for (IThreadMessage message : msgs) {
                ThreadMessage msg = (ThreadMessage) message;
                msg.setBulletinBoard(bb);
                msg.thread = this;
                IMember author = msg.author;
                ((Member) author).setBulletinBoard(bb);
            }
        }
        return msgs;
    }

    public List<IThreadMessage> getNewMessages(ID lastReadId) throws BBException {
        if (lastReadId != null) {
            lastReadMessageId = (ThreadMessageID) lastReadId;
        }
        return getNewMessages();
    }

    public ID getID() {
        return id;
    }

    public int getType() {
        return 0;
    }

    public IThreadMessage createReplyMessage() throws IllegalWriteException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        ThreadMessage msg = new ThreadMessage();
        msg.setBulletinBoard(bb);
        msg.setThread(this);
        return msg;
    }

    public IThreadMessage createReplyMessage(IThreadMessage replyTo) throws IllegalWriteException {
        ThreadMessage msg = (ThreadMessage) createReplyMessage();
        msg.setReplyTo(replyTo);
        return msg;
    }

    public ID postReply(IThreadMessage message) throws IllegalWriteException, BBException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        ThreadMessage msg = (ThreadMessage) message;
        // FIXME assert msg.bb == bb;
        assert msg.getThread() == this;
        PostRequest request = new PostRequest(bb.getHttpClient(), bb.getURL(), "posting.php");
        NameValuePair[] params = new NameValuePair[] { new NameValuePair("subject", msg.getName()), new NameValuePair("message", msg.getMessage()), new NameValuePair("t", String.valueOf(id.getLongValue())), new NameValuePair("mode", "reply"), // checkbox : disabled new NameValuePair("notify", "on"),
        new NameValuePair("post", "Submit") };
        request.addParameters(params);
        String resp = null;
        try {
            request.execute();
            resp = request.getResponseBodyAsString();
            String info = ((PHPBBParser) bb.getParser()).parseInformationMessage(resp);
            Matcher m = Pattern.compile("<a href=\"viewtopic.php\\?p=([0-9]+)(?:.*?)\">").matcher(info);
            if (m.find()) {
                synchronized (this) {
                    try {
                        lastReadMessageId = (ThreadMessageID) new ThreadMessageFactory().createBBObjectId(bb.getNamespace(), bb.getURL(), m.group(1));
                        return lastReadMessageId;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (IDCreateException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new BBException("The message was not posted.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSubscriptionStatus() {
        return -1;
    }

    public boolean updateSubscription(int newSubscriptionStatus) throws BBException {
        return false;
    }

    public IThreadMessage getPrePostMessage() throws IllegalWriteException {
        return prePostMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Thread) {
            Thread grp = (Thread) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public IMember getAuthor() {
        return author;
    }

    protected void setAuthor(IMember author) {
        this.author = author;
    }

    public int getNumberOfMessages() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Date getTimePosted() {
        // TODO Auto-generated method stub
        return null;
    }

    public Date getTimeUpdated() {
        // TODO Auto-generated method stub
        return null;
    }
}
