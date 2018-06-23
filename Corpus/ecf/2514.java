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
package org.eclipse.ecf.presence.chatroom;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IIMMessage;

/**
 * Chat room message.
 */
public interface IChatRoomMessage extends IIMMessage {

    /** 
	 * Get the room ID for the room of this message.
	 * 
	 * @return ID of chat room associated with this message.
	 */
    public ID getChatRoomID();

    /**
	 * Get the actual message sent to the chat room
	 * 
	 * @return String message sent to chat room. Will not be <code>null</code>.
	 */
    public String getMessage();
}
