/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn.events;

import org.eclipse.ecf.protocol.msn.ChatSession;

/**
 * <p>
 * A listener that can be used to monitor the creation of {@link ChatSession}s.
 * This is used for listening for incoming instant messages from a party that
 * the current user has not already established a chat session with.
 * </p>
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public interface ISessionListener {

    /**
	 * A method that is called when a chat session has been created because of a
	 * request from an external host.
	 * 
	 * @param chatSession
	 *            the created chat session
	 */
    public void sessionConnected(ChatSession chatSession);
}
