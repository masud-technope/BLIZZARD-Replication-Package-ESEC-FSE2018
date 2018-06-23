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

/**
 * This interface contains accessors for properties common to Private and Thread
 * Messages.
 * 
 * @author Erkki
 */
public interface IMessageBase extends IBBObject {

    /**
	 * Returns the moment in time when this message was posted.
	 * 
	 * TODO Design timezone handling.
	 * 
	 * @return date that the message was posted.
	 */
    public Date getTimePosted();

    /**
	 * Sets the name (title) of the message.
	 * 
	 * @param name
	 *            the name to set
	 * @throws IllegalWriteException
	 *             if the message is read-only
	 */
    public void setName(String name) throws IllegalWriteException;

    /**
	 * Returns the contents of the message.
	 * 
	 * @return contents
	 */
    public String getMessage();

    /**
	 * Sets the contents of the message.
	 * 
	 * @param message
	 *            the contents to set
	 * @throws IllegalWriteException
	 *             if the message is read-only
	 */
    public void setMessage(String message) throws IllegalWriteException;

    /**
	 * Returns the author (sender) of the message.
	 * 
	 * @return author
	 */
    public IMember getFrom();

    /**
	 * Returns the message that this one is a reply to or null if it's not a
	 * reply.
	 * 
	 * @return the message that this is a reply to
	 */
    public IMessageBase getReplyTo();

    /**
	 * Sets this message as a reply to another message.
	 * 
	 * @param message
	 *            the message that this will be set as a reply to.
	 * @throws IllegalWriteException
	 *             if the message is read-only
	 */
    public void setReplyTo(IMessageBase message) throws IllegalWriteException;
}
