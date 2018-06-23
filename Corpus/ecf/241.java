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
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;

/**
 * Listener interface for receiving participant arrive and departed
 * notifications
 * 
 * <p>
 * </p>
 * Note these methods will be called asynchronously when notifications of remote
 * changes are received by the provider implementation code. The provider is
 * free to call the methods below with an arbitrary thread, so the
 * implementation of these methods must be appropriately prepared.
 * <p>
 * </p>
 * For example, if the code implementing any of these methods must interact with
 * user interface code, then it should use code such as the following to execute
 * on the SWT UI thread:
 * 
 * <pre>
 * 	Display.getDefault().asyncExec(new Runnable() {
 * 		public void run() {
 * 		... UI code here
 * 		}
 * 	});
 * </pre>
 * 
 * Further, the code in the implementations of these methods should <b>not block</b> via 
 * I/O operations or blocking UI calls.
 */
public interface IChatRoomParticipantListener {

    /**
	 * Notification that participant arrived in associated chat room
	 * 
	 * @param participant
	 *            Will not be <code>null</code>. The IUser of the arrived
	 *            participant
	 */
    public void handleArrived(IUser participant);

    /**
	 * Notification that user information (e.g. name, nickname, or properties)
	 * have changed for chat participant. The ID of the changedParticipant (via
	 * changedParticipant.getID()) will match the ID of the previous
	 * notification {@link #handleArrived(IUser)}.
	 * 
	 * @param updatedParticipant
	 *            Will not be <code>null</code>. The ID of the
	 *            updatedParticipant will be the same as the ID previously
	 *            specified via handleArrived, but the name
	 *            {@link IUser#getName()} and/or the nickname
	 *            {@link IUser#getNickname()} and/or the properties
	 *            {@link IUser#getProperties()} may be different.
	 */
    public void handleUpdated(IUser updatedParticipant);

    /**
	 * Notification that participant departed the associated chat room
	 * 
	 * @param participant
	 *            Will not be <code>null</code>. the ID of the departed
	 *            participant
	 */
    public void handleDeparted(IUser participant);

    /**
	 * Notification that a presence update has been received
	 * 
	 * @param fromID
	 *            the ID of the sender of the presence update. Will not be
	 *            <code>null</code>.
	 * @param presence
	 *            the presence information for the sender. Will not be
	 *            <code>null</code>.
	 */
    public void handlePresenceUpdated(ID fromID, IPresence presence);
}
