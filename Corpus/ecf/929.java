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

import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Chat message sender.
 */
public interface IChatMessageSender {

    /**
	 * Send chat message to given ID.
	 * 
	 * @param toID
	 *            the target receiver to receive the chat message. Must not be
	 *            <code>null</code>.
	 * 
	 * @param threadID
	 *            the threadID for the message. May be <code>null</code>.
	 * 
	 * @param type
	 *            the IChatMessage.Type of the message. May not be null.
	 * 
	 * @param subject
	 *            the subject of the message. May be null.
	 * 
	 * @param body
	 *            the body of the message to send. May be null.
	 * 
	 * @param properties
	 *            properties associated with message. May be null.
	 * 
	 * @throws ECFException
	 *             thrown if toID is null, or currently disconnected
	 */
    public void sendChatMessage(ID toID, ID threadID, IChatMessage.Type type, String subject, String body, Map properties) throws ECFException;

    /**
	 * Send chat message to given ID.
	 * 
	 * @param toID
	 *            the target receiver to receive the chat message. Must not be
	 *            null.
	 * 
	 * @param body
	 *            the body of the message to send. May be null.
	 * 
	 * @throws ECFException
	 *             thrown if toID is null, or currently disconnected
	 */
    public void sendChatMessage(ID toID, String body) throws ECFException;
}
