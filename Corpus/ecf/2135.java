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

import org.eclipse.ecf.presence.im.IChatMessage;

/**
 * Message handler for receiving a IM message.
 */
public interface IIMMessageHandler extends IContainerAdvisor {

    /**
	 * Initialize robot with robot entry data.
	 * 
	 * @param robot
	 *            the robot to initialize. Will not be <code>null</code>.
	 */
    public void init(IIMBotEntry robot);

    /**
	 * This method is called when a {@link IChatMessage} is received.
	 * 
	 * @param message
	 *            the {@link IChatMessage} received. Will not be
	 *            <code>null</code>. Implementers should not block the
	 *            calling thread. Any methods on the given <code>message</code>
	 *            parameter may be called.
	 */
    public void handleIMMessage(IChatMessage message);
}
