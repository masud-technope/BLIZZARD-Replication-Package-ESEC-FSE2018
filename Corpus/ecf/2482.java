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

import java.util.List;

/**
 * Models a message sent using Private Messaging of a Bulletin Board.
 * 
 * Extends the IMessageBase interface with additional properties that are
 * specific to private messages.
 * 
 * @author Erkki
 */
public interface IPrivateMessage extends IMessageBase {

    /**
	 * Returns the recipients of this message.
	 * 
	 * @return the recipients
	 */
    public List getRecipients();

    /**
	 * Sets the recipient of this message.
	 * 
	 * @param recipient
	 *            the recipient to set
	 * @throws IllegalWriteException
	 *             if the message is read-only
	 */
    public void setRecipient(IMember recipient) throws IllegalWriteException;

    /**
	 * Sets multiple recipients of this message.
	 * 
	 * @param recipients
	 *            the recipients to set
	 * @throws IllegalWriteException
	 *             if the message is read-only
	 */
    public void setRecipients(List recipients) throws IllegalWriteException;
}
