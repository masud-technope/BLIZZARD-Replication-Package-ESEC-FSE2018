/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.provider.irc.bot.handler;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;

public interface ICommandHandler {

    public void execute(String command, IChatRoomMessageSender sender) throws ECFException;
}
