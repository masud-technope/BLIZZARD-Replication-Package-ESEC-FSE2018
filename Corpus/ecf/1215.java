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
import org.eclipse.ecf.core.util.ECFException;

/**
 * Represents ability to send a chat room invitation to a given target.
 */
public interface IChatRoomInvitationSender {

    /**
	 * Send invitation to join in chat room to target user.
	 * 
	 * @param room
	 *            the ID of the room that the invitation is for. Must not be
	 *            <code>null</code>.
	 * @param targetUser
	 *            the ID of the targetUser to send the invitation to. Must not
	 *            be <code>null</code>.
	 * @param subject
	 *            an optional subject for the invitation. May be
	 *            <code>null</code>.
	 * @param body
	 *            an optional body for the invitation message. May be
	 *            <code>null</code>.
	 * @throws ECFException
	 *             if connection disconnected or some other error preventing
	 *             sending of the invitation.
	 */
    public void sendInvitation(ID room, ID targetUser, String subject, String body) throws ECFException;
}
