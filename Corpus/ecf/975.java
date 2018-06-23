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
package org.eclipse.ecf.bulletinboard;

import java.util.Date;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;

/**
 * Models a thread in a forum.
 * 
 * @author Erkki
 */
public interface IThread extends IBBObject {

    /**
	 * You are subscribed to this thread.
	 */
    public static final int SUBSCRIBED = 1;

    /**
	 * You are not subscribed to this thread.
	 */
    public static final int UNSUBSCRIBED = 0;

    /**
	 * This thread is "sticky".
	 */
    public static final int STICKY = 1;

    /**
	 * Returns the type of this thread, STICKY or 0.
	 * 
	 * @return the type of this thread
	 */
    public int getType();

    /**
	 * Returns the author of the thread.
	 * 
	 * @return the author of the thread.
	 */
    public IMember getAuthor();

    public Date getTimePosted();

    public Date getTimeUpdated();

    public int getNumberOfMessages();

    /**
	 * Returns the poll associated with this thread, or null if none.
	 * 
	 * @return poll associated with this thread
	 */
    public IPoll getPoll();

    /**
	 * Returns the forum that this thread belongs to or null if unknown.
	 * 
	 * @return the forum
	 */
    public IForum getForum();

    /**
	 * Returns all the messages in this thread.
	 * 
	 * Note: this method may not return all messages in the thread.
	 * 
	 * @return messages in this thread
	 * @throws BBException
	 *             if fetching the messages failed
	 */
    public List getMessages() throws BBException;

    /**
	 * Returns all new messages in this thread. The exact meaning of when a
	 * message is new may differ across implementations.
	 * 
	 * Equivalent to calling getNewMessages(null).
	 * 
	 * @return new messages in this thread
	 * @throws BBException
	 *             if fetching the messages failed
	 */
    public List getNewMessages() throws BBException;

    /**
	 * Returns all new messages since the last read message ID given in the
	 * parameter. If the parameter is null, then getNewMessages() is called and
	 * the Bulletin Board itself may decide which messages are new and which are
	 * not.
	 * 
	 * @param lastPostId
	 *            the message with this id and any older messages must not be
	 *            returned.
	 * @return new messages since the last read one
	 * @throws BBException
	 *             if fetching the messages failed
	 */
    public List getNewMessages(ID lastPostId) throws BBException;

    /**
	 * Returns your subscription status of this thread. SUBSCRIBED or -1 if the
	 * status is unknown.
	 * 
	 * @return subscription status
	 */
    public int getSubscriptionStatus();

    /**
	 * Updates your subscription status of this thread.
	 * 
	 * @param newSubscriptionStatus
	 *            the new subscription status
	 * @return true if the subscription update was successful
	 * @throws BBException
	 *             if the status update failed
	 */
    public boolean updateSubscription(int newSubscriptionStatus) throws BBException;

    /**
	 * Creates a message that can be filled with contents and posted in this
	 * thread using the postReply(IThreadMessage) method.
	 * 
	 * @return a newly created message that should be filled and then posted in
	 *         this thread
	 * @throws IllegalWriteException
	 *             if the thread is read-only
	 */
    public IThreadMessage createReplyMessage() throws IllegalWriteException;

    /**
	 * Similar to createReplyMessage(), but creates the message in such way that
	 * it contains a reference to an existing message in this thread.
	 * 
	 * @param replyTo
	 *            the message that the new one is a reply to.
	 * @return a newly created message that should be filled and then posted in
	 *         this thread
	 * @throws IllegalWriteException
	 *             if the thread is read-only
	 */
    public IThreadMessage createReplyMessage(IThreadMessage replyTo) throws IllegalWriteException;

    /**
	 * Returns the message object that will be used to post this thread.
	 * 
	 * @return the message to be filled for posting
	 * @throws IllegalWriteException
	 *             if this thread has already been created
	 */
    public IThreadMessage getPrePostMessage() throws IllegalWriteException;

    /**
	 * Posts the given message as a reply in this thread.
	 * 
	 * @param message
	 *            the message to post
	 * @return the ID of the message that was posted, or null if it was not
	 *         possible to obtain the ID
	 * @throws IllegalWriteException
	 *             if the thread is read-only
	 * @throws BBException
	 *             if posting failed
	 */
    public ID postReply(IThreadMessage message) throws IllegalWriteException, BBException;
}
