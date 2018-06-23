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

import org.eclipse.ecf.core.identity.ID;

/**
 * Invitation listener for handling asynchronous chat room invitation events.
 * Instances implementing this interface must be registered via the
 * {@link IChatRoomManager#addInvitationListener(IChatRoomInvitationListener)}
 * 
 * @see IChatRoomManager#addInvitationListener(IChatRoomInvitationListener)
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
public interface IChatRoomInvitationListener {

    /**
	 * Handle notification of a received invitation to join a chat room. This
	 * method will be called by some thread when an invitation is received by
	 * this user account to join a chat room
	 * 
	 * @param roomID
	 *            the room id associated with the invitation. Will not be
	 *            <code>null</code>.
	 * @param from
	 *            the id of the sender. Will not be <code>null</code>.
	 * @param subject
	 *            a subject for the invitation. May be <code>null</code>.
	 * @param body
	 *            a message body for the invitation. May be <code>null</code>.
	 */
    public void handleInvitationReceived(ID roomID, ID from, String subject, String body);
}
