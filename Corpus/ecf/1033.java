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

import org.eclipse.ecf.protocol.msn.Status;

/**
 * <p>
 * The IContactListener interface defines methods that developers can listen for
 * which pertains to the {@link org.eclipse.ecf.protocol.msn.Contact} class.
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
public interface IContactListener {

    /**
	 * This method is called when contact has changed his or her user name.
	 * 
	 * @param name
	 *            the new name that the contact is using
	 */
    public void nameChanged(String name);

    /**
	 * This method is called when the user changes his or her personal message.
	 * 
	 * @param personalMessage
	 *            the new message that the contact is displaying
	 */
    public void personalMessageChanged(String personalMessage);

    /**
	 * This method is called when the contact has changed his or her status.
	 * 
	 * @param status
	 *            the status that the contact has now switched to
	 */
    public void statusChanged(Status status);
}
