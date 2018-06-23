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

import java.util.EventListener;
import org.eclipse.ecf.protocol.msn.ChatSession;
import org.eclipse.ecf.protocol.msn.Contact;

/**
 * <p>
 * The IChatSessionListener monitors the events that are occurring within a
 * {@link ChatSession}.
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
public interface IChatSessionListener extends EventListener {

    /**
	 * This method is called when a contact joins the session.
	 * 
	 * @param contact
	 *            the contact that has joined
	 */
    public void contactJoined(Contact contact);

    /**
	 * This method is called when a contact leaves the session.
	 * 
	 * @param contact
	 *            the contact that has left
	 */
    public void contactLeft(Contact contact);

    /**
	 * This method is called when a contact begins typing.
	 * 
	 * @param contact
	 *            the contact that is currently typing
	 */
    public void contactIsTyping(Contact contact);

    /**
	 * This method is called when a message has been received from a contact.
	 * 
	 * @param contact
	 *            the contact that has sent out a message
	 * @param message
	 *            the message that has been sent
	 */
    public void messageReceived(Contact contact, String message);

    /**
	 * This method is called when the session has timed out.
	 */
    public void sessionTimedOut();
}
