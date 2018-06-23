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

import org.eclipse.ecf.protocol.msn.Contact;
import org.eclipse.ecf.protocol.msn.Group;

/**
 * <p>
 * The IContactListListener monitors events pertaining to the addition and
 * removal of contacts on the user's client.
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
public interface IContactListListener {

    /**
	 * This method is invoked when a contact has been added to the user's list.
	 * 
	 * @param contact
	 *            the contact that has been added
	 */
    public void contactAdded(Contact contact);

    /**
	 * This method is invoked when a contact has been removed from the user's
	 * list.
	 * 
	 * @param contact
	 *            the contact that has been added
	 */
    public void contactRemoved(Contact contact);

    /**
	 * This method is invoked when a contact has added the user to his or her
	 * contact list.
	 * 
	 * @param email
	 *            the email of the contact
	 */
    public void contactAddedUser(String email);

    /**
	 * This method is invoked when a contact has removed the user from his or
	 * her contact list.
	 * 
	 * @param email
	 *            the email of the contact
	 */
    public void contactRemovedUser(String email);

    /**
	 * This method is invoked when a group has been added to the contact list.
	 * 
	 * @param group
	 *            the group that has been added
	 */
    public void groupAdded(Group group);
}
