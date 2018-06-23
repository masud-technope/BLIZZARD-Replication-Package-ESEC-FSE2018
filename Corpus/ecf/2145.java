/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.chatroom;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.history.IHistoryManager;

/**
 * Chat room manager. Entry point for getting access to chat rooms managed by
 * this manager. Access to objects implementing this interface is provided by
 * {@link IPresenceContainerAdapter#getChatRoomManager()}
 * 
 */
public interface IChatRoomManager extends IAdaptable {

    /**
	 * Add invitation listener
	 * 
	 * @param listener
	 *            the invitation listener to add. Must not be <code>null</code>.
	 */
    public void addInvitationListener(IChatRoomInvitationListener listener);

    /**
	 * Remove invitation listener
	 * 
	 * @param listener
	 *            the invitation listener to remove. Must not be
	 *            <code>null</code>.
	 */
    public void removeInvitationListener(IChatRoomInvitationListener listener);

    /**
	 * Get chat room invitation sender.  If <code>null</code>, this chat room
	 * manager does not support the ability to send invitations.
	 * 
	 * @return IChatRoomInvitationSender to use to send invitations.  May be 
	 * <code>null</code>, and in that case no invitation sending ability
	 * is available from this chat manager.
	 */
    public IChatRoomInvitationSender getInvitationSender();

    /**
	 * Get parent IChatRoomManager. If this manager is the root, then this
	 * method returns <code>null</code>.
	 * 
	 * @return IChatRoomManager instance if this manager has a parent. Returns
	 *         <code>null</code> if this manager is the root of the hierarchy.
	 */
    public IChatRoomManager getParent();

    /**
	 * Get any children managers of this IChatRoomManager. If this chat room
	 * manager has children chat room managers, then the returned array will
	 * have more than zero elements. If this IChatRoomManager has no children,
	 * then a zero-length array will be returned.
	 * 
	 * @return IChatRoomManager[] of children for this chat room manager. If no
	 *         children, a zero-length array will be returned. <code>null</code>
	 *         will not be returned.
	 */
    public IChatRoomManager[] getChildren();

    /**
	 * Get detailed room info for given room name
	 * 
	 * @param roomName
	 *            the name of the room to get detailed info for. If null, the
	 *            room info is assumed to be a room associated with the chat
	 *            room manager instance itself. For example, for IRC, the chat
	 *            room manager is also a chat room where message can be
	 *            sent/received
	 * @return IChatRoomInfo an instance that provides the given info. Returns
	 *         <code>null</code> if no chat room info associated with given
	 *         name or null
	 */
    public IChatRoomInfo getChatRoomInfo(String roomName);

    /**
	 * Get detailed room info for all chat rooms associated with this manager
	 * 
	 * @return IChatRoomInfo an array of instances that provide info for all
	 *         chat rooms. Will return empty array if there are no available
	 *         chat rooms. Will not return <code>null</code>.
	 */
    public IChatRoomInfo[] getChatRoomInfos();

    /**
	 * 
	 * Create a chat room with the given room name and properties.
	 * 
	 * @param roomName
	 *            the name of the room. Must not be <code>null</code>.
	 * @param properties
	 *            properties associated with the room's creation. May be
	 *            <code>null</code>.
	 * @return IChatRoomInfo room info suitable for creating a chat room
	 *         container and connecting. Will not be <code>null</code>.
	 * @throws ChatRoomCreateException
	 *             if roomName is <code>null</code>, or if chat room creation
	 *             cannot occur (e.g. server refuses, name collision occurs,
	 *             user has insufficient rights to perform creation operation,
	 *             etc).
	 */
    public IChatRoomInfo createChatRoom(String roomName, Map properties) throws ChatRoomCreateException;

    /**
	 * Get chat room history manager. Will not return <code>null</code>.
	 * 
	 * @return IHistoryManager. Will not return <code>null</code>.
	 */
    public IHistoryManager getHistoryManager();
}
