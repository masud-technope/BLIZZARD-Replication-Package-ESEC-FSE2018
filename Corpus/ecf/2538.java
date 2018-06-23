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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Exception class thrown by
 * {@link IChatRoomManager#createChatRoom(String, java.util.Map)}
 */
public class ChatRoomCreateException extends ECFException {

    private static final long serialVersionUID = -2605728854430323369L;

    protected String roomname;

    public  ChatRoomCreateException() {
    // null constructor
    }

    public String getRoomName() {
        return roomname;
    }

    /**
	 * @param roomname
	 * @param message
	 * @param cause
	 */
    public  ChatRoomCreateException(String roomname, String message, Throwable cause) {
        super(message, cause);
        this.roomname = roomname;
    }

    /**
	 * @param roomname
	 * @param message
	 */
    public  ChatRoomCreateException(String roomname, String message) {
        this(roomname, message, null);
    }

    /**
	 * @param roomname
	 * @param cause
	 */
    public  ChatRoomCreateException(String roomname, Throwable cause) {
        this(roomname, null, cause);
    }

    /**
	 * @param status
	 */
    public  ChatRoomCreateException(IStatus status) {
        super(status);
    }
}
