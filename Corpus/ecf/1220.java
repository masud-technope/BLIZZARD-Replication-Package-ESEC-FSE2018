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
package org.eclipse.ecf.protocol.msn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A Group is a collection of {@link Contact}s within a {@link ContactList}. A
 * <tt>Contact</tt> can be in zero or more <tt>Group</tt>s.
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
public final class Group {

    private final List contacts;

    /**
	 * The name of this group.
	 */
    private final String name;

    /**
	 * Create a new group with the specified name.
	 * 
	 * @param name
	 *            the name of the group
	 */
     Group(String name) {
        this.name = name;
        contacts = new ArrayList();
    }

    void add(Contact contact) {
        contacts.add(contact);
        contact.add(this);
    }

    void remove(Contact contact) {
        contacts.remove(contact);
    }

    /**
	 * Returns whether the specified contact is in this group.
	 * @param contact 
	 * 
	 * @return <tt>true</tt> if the contact is in this group, <tt>false</tt>
	 *         otherwise
	 */
    public boolean contains(Contact contact) {
        return contacts.contains(contact);
    }

    public Collection getContacts() {
        return Collections.unmodifiableCollection(contacts);
    }

    /**
	 * Returns the name of this group.
	 * 
	 * @return this group's name
	 */
    public String getName() {
        return name;
    }
}
