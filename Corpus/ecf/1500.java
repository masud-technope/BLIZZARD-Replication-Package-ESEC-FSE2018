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
package org.eclipse.ecf.presence.roster;

import java.util.Collection;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;

/**
 * Roster entry object. Instances implementing this interface provide
 * information about roster entryies.
 * 
 */
public interface IRosterEntry extends IRosterItem {

    /**
	 * Get user for this roster entry. Will not be <code>null</code>.
	 * 
	 * @return IUser that represents user associated with this roster entry.
	 *         Will not be <code>null</code>.
	 */
    public IUser getUser();

    /**
	 * Get groups associated with this roster entry. Instance in list are of
	 * type {@link IRosterGroup}
	 * 
	 * @return Collection of IRosterGroup instances entry belongs to. Will not
	 *         return <code>null</code>.
	 */
    public Collection getGroups();

    /**
	 * Get presence state for this roster entry.
	 * 
	 * @return IPresence information for this roster entry. May be
	 *         <code>null</code>.
	 */
    public IPresence getPresence();
}
