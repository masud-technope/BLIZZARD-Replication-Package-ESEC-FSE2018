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

/**
 * Listener for chat room subject changes
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
 * Further, the code in the implementations of these methods should <b>not block</b>
 * via I/O operations or blocking UI calls.
 */
public interface IChatRoomAdminListener {

    /**
	 * Handle notification of new subject set for the associated chat room.
	 * 
	 * @param from
	 *            the ID of the user the subject change is from. May be
	 *            <code>null</code> if user is not known, or change is not
	 *            from any particular user (i.e. the system).
	 * 
	 * @param newSubject
	 *            the new subject for the chat room. Will not be
	 *            <code>null</code>, but may be empty String.
	 */
    public void handleSubjectChange(ID from, String newSubject);
}
