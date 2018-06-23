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
package org.eclipse.ecf.presence.bot;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;

/**
 * Advisor instance for receiving pre connect events for chat rooms.
 */
public interface IChatRoomContainerAdvisor extends IContainerAdvisor {

    /**
	 * This method will be called prior to connecting to the
	 * <code>roomContainer</code>. The given <code>roomContainer</code> and
	 * <code>roomID</code> will not be <code>null</code>.
	 * 
	 * @param roomContainer
	 *            the {@link IChatRoomContainer} that will be connected to. Will
	 *            not be <code>null</code>.
	 * 
	 * @param roomID
	 *            the {@link ID} of the room that will be connected to. Will not
	 *            be <code>null</code>.
	 */
    public void preChatRoomConnect(IChatRoomContainer roomContainer, ID roomID);
}
