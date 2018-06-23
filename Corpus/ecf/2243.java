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

import java.util.*;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;

/**
 * Base class implementation of {@link IRoster}. Subclasses may be created as
 * appropriate.
 * 
 */
public class Roster extends RosterItem implements IRoster {

    protected List rosteritems;

    protected IUser rosterUser;

    protected IPresenceContainerAdapter presenceContainer;

    public  Roster(IPresenceContainerAdapter pc, IUser user) {
        super(null, //$NON-NLS-1$
        (user == null) ? //$NON-NLS-1$
        "<unknown>" : ((user.getName() == null) ? user.getID().getName() : user.getName()));
        this.presenceContainer = pc;
        this.rosterUser = user;
        this.rosteritems = Collections.synchronizedList(new ArrayList());
    }

    public  Roster(IPresenceContainerAdapter presenceContainer) {
        this(presenceContainer, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRoster#getItems()
	 */
    public Collection getItems() {
        return rosteritems;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterItem#getName()
	 */
    public String getName() {
        return getUser().getName();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRoster#getUser()
	 */
    public IUser getUser() {
        return rosterUser;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRoster#addItem(org.eclipse.ecf.presence.roster.IRosterItem)
	 */
    public boolean addItem(IRosterItem item) {
        if (item == null)
            return false;
        return rosteritems.add(item);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRoster#removeItem(org.eclipse.ecf.presence.roster.IRosterItem)
	 */
    public boolean removeItem(IRosterItem item) {
        if (item == null)
            return false;
        return rosteritems.remove(item);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRoster#setUser(org.eclipse.ecf.core.user.IUser)
	 */
    public void setUser(IUser user) {
        this.rosterUser = user;
    }

    /**
	 * @return String this object as String
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("Roster[");
        //$NON-NLS-1$
        buf.append("pc=").append(getPresenceContainerAdapter());
        //$NON-NLS-1$
        buf.append(";user=").append(getUser());
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";items=").append(getItems()).append("]");
        return buf.toString();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.roster.IRoster#getPresenceContainerAdapter()
	 */
    public IPresenceContainerAdapter getPresenceContainerAdapter() {
        return presenceContainer;
    }
}
