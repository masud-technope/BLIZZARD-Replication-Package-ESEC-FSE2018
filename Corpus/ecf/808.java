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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;

/**
 * Chat room information. Instances implementing this interface are returned by
 * {@link IChatRoomManager#getChatRoomInfo(String)} or
 * {@link IChatRoomManager#getChatRoomInfos()}
 * 
 * @see IChatRoomManager#getChatRoomInfo(String)
 */
public interface IChatRoomInfo extends IAdaptable {

    /**
	 * Get a description for this room
	 * 
	 * @return String description for this chat room. May be <code>null</code>.
	 */
    public String getDescription();

    /**
	 * Get subject/topic for room
	 * 
	 * @return String topic. May be <code>null</code>.
	 */
    public String getSubject();

    /**
	 * Get the ID associated with this room
	 * 
	 * @return ID for room. Will not be <code>null</code>.
	 */
    public ID getRoomID();

    /**
	 * Get a count of the current number of room participants
	 * 
	 * @return int number of participants
	 */
    public int getParticipantsCount();

    /**
	 * Get a long name for room
	 * 
	 * @return String name. May be <code>null</code>.
	 */
    public String getName();

    /**
	 * 
	 * @return true if room is persistent, false otherwise
	 */
    public boolean isPersistent();

    /**
	 * 
	 * @return true if room connect requires password, false otherwise
	 */
    public boolean requiresPassword();

    /**
	 * 
	 * @return true if room is moderated, false otherwise
	 */
    public boolean isModerated();

    /**
	 * 
	 * @return ID of connected user
	 */
    public ID getConnectedID();

    /**
	 * Create a new IChatRoomContainer instance. This method can be used to
	 * create and connect a container to the chat room identified by this chat
	 * room info object.
	 * 
	 * @return non-null IChatRoomContainer instance. Will not return
	 *         <code>null</code>.
	 * @throws ContainerCreateException
	 *             if chat room container cannot be made
	 */
    public IChatRoomContainer createChatRoomContainer() throws ContainerCreateException;
}
