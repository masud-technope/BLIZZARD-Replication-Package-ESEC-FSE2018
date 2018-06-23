/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.chatroom;

import org.eclipse.ecf.core.util.ECFException;

/**
 * Perform administrative functions for an IChatRoomContainer.
 * 
 * @since 1.1
 */
public interface IChatRoomAdminSender {

    /**
	 * Send chat room subject change.
	 * 
	 * @param newsubject the new subject for the chat room.
	 * @throws ECFException exception thrown if some problem sending message (e.g. disconnect).
	 */
    public void sendSubjectChange(String newsubject) throws ECFException;
}
