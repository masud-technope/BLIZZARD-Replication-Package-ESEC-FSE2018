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

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatMessageSender;

/**
 * A container representing a specific chat room.
 */
public interface IChatRoomContainer extends IContainer {

    /**
	 * Add message listener.
	 * 
	 * @param listener
	 *            the listener to add. Must not be <code>null</code>.
	 */
    public void addMessageListener(IIMMessageListener listener);

    /**
	 * Remove message listener.
	 * 
	 * @param listener
	 *            the listener to remove. Must not be <code>null</code>.
	 */
    public void removeMessageListener(IIMMessageListener listener);

    /**
	 * Retrieve a chat message sender to send private chat messages to other
	 * participants. If sending private chat messages is not supported by this
	 * provider then <code>null</code> will be returned.
	 * 
	 * @return IChatMessageSender to use for sending chat message. If
	 *         <code>null</code>, sending chat messages not supported by this
	 *         provider.
	 */
    public IChatMessageSender getPrivateMessageSender();

    /**
	 * Get interface for sending messages
	 * 
	 * @return IChatRoomMessageSender. Will be <code>null</code> if no message
	 *         sender available for the provider implementation.
	 */
    public IChatRoomMessageSender getChatRoomMessageSender();

    /**
	 * Add chat room participant listener. The given listener will be notified
	 * if/when participants are added or removed from given room.
	 * 
	 * @param participantListener
	 *            to add. Must not be <code>null</code>.
	 */
    public void addChatRoomParticipantListener(IChatRoomParticipantListener participantListener);

    /**
	 * Remove chat room participant listener.
	 * 
	 * @param participantListener
	 *            the participant listener to remove. Must not be
	 *            <code>null</code>.
	 */
    public void removeChatRoomParticipantListener(IChatRoomParticipantListener participantListener);

    /**
	 * Add chat room admin listener to listen for room admin changes.
	 * 
	 * @param adminListener
	 *            the listener to add. Must not be <code>null</code>.
	 */
    public void addChatRoomAdminListener(IChatRoomAdminListener adminListener);

    /**
	 * Remove chat room admin listener.
	 * 
	 * @param adminListener
	 *            the listener to remove. Must not be <code>null</code>.
	 */
    public void removeChatRoomAdminListener(IChatRoomAdminListener adminListener);

    /**
	 * Get chat room admin sender.  
	 * @return IChatRoomAdminSender for sending admin update messages.  Will return
	 * <code>null</code> if there is no sender available for provider.
	 */
    public IChatRoomAdminSender getChatRoomAdminSender();

    /**
	 * Returns an array of IDs of current chatroom participants.  
	 * 
	 * @return ID[] of chatroom participants.  If no participants in chat room, will return
	 * an empty array.  Will not return <code>null</code>
	 * @since 1.1
	 */
    public ID[] getChatRoomParticipants();
}
