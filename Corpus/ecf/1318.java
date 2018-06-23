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

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;

/**
 * Event received via {@link IIMMessageListener} when remote
 * chat has been converted into a chat room.
 */
public interface IChatRoomCreationEvent extends IIMMessageEvent {

    /**
	 * Get the chat room container associated with the new chat room
	 * created by remote.
	 * 
	 * @return IChatRoomContainer created by remote.  Will not be <code>null</code>.
	 * 
	 * @throws ECFException if chatroom container cannot be created...e.g. due
	 * to container disconnect.
	 */
    public IChatRoomContainer getChatRoomContainer() throws ECFException;
}
