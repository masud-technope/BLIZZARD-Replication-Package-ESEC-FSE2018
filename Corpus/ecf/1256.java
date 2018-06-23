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
import org.eclipse.ecf.presence.IPresenceContainerAdapter;

/**
 * Roster (aka contacts list or buddy list)
 * 
 */
public interface IRoster extends IRosterItem {

    /**
	 * Get local user for this roster. This is the user that owns this roster.
	 * If <code>null</code>, this means that the user is not yet logged in.
	 * 
	 * @return IUser local user associated with this roster. If
	 *         <code>null</code>, this means that the user is not yet logged
	 *         in.
	 */
    public IUser getUser();

    /**
	 * Get the IRosterItems for this roster. The collection returned will not be
	 * null, and will contain IRosterItems. The IRosterItems may be either
	 * IRosterGroups and/or IRosterEntries.
	 * 
	 * @return Collection of IRosterItems. Will not be <code>null</code>. May
	 *         return an empty collection of items.
	 */
    public Collection getItems();

    /**
	 * Get the IPresenceContainerAdapter associated with this roster.  The
	 * result will not be <code>null</code>.
	 * 
	 * @return IPresenceContainerAdapter the presence container adapter associated
	 * with this roster.
	 */
    public IPresenceContainerAdapter getPresenceContainerAdapter();
}
