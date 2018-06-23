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
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;

/**
 * Roster entry base class implementing {@link IRosterEntry}. Subclasses may be
 * created as appropriate.
 * 
 */
public class RosterEntry extends RosterItem implements IRosterEntry, IMultiResourceRosterEntry {

    protected IUser user;

    protected IPresence presence;

    protected List groups;

    /**
	 * @since 2.1
	 */
    protected List resources;

    public  RosterEntry(IRosterItem parent, IUser user, IPresence presenceState) {
        Assert.isNotNull(parent);
        Assert.isNotNull(user);
        this.parent = parent;
        this.user = user;
        this.presence = presenceState;
        this.groups = Collections.synchronizedList(new ArrayList());
        if (parent instanceof RosterGroup) {
            groups.add(parent);
            ((RosterGroup) parent).add(this);
        }
        this.resources = new ArrayList();
    }

    public void setPresence(IPresence newPresence) {
        this.presence = newPresence;
    }

    public String getName() {
        return user.getName();
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof RosterEntry) {
            RosterEntry re = (RosterEntry) obj;
            return re.getUser().getID().equals(getUser().getID());
        }
        return false;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return getUser().getID().hashCode();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterEntry#add(org.eclipse.ecf.presence.roster.IRosterGroup)
	 */
    public boolean add(IRosterGroup group) {
        if (group == null)
            return false;
        return groups.add(group);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterEntry#remove(org.eclipse.ecf.presence.roster.IRosterGroup)
	 */
    public boolean remove(IRosterGroup group) {
        if (group == null)
            return false;
        return groups.remove(group);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterEntry#getUser()
	 */
    public IUser getUser() {
        return user;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.presence.IRosterEntry#getGroups()
	 */
    public Collection getGroups() {
        return groups;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.presence.IRosterEntry#getPresenceState()
	 */
    public IPresence getPresence() {
        return presence;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("RosterEntry[");
        synchronized (sb) {
            //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("userid=" + getUser().getID().getName()).append(";");
            //$NON-NLS-1$
            sb.append("name=").append(getName()).append(';');
            //$NON-NLS-1$
            sb.append("presence=").append(presence).append(';');
            //$NON-NLS-1$
            sb.append("groups=");
            if (!groups.isEmpty()) {
                for (int i = 0; i < groups.size(); i++) {
                    sb.append(((IRosterGroup) groups.get(i)).getName());
                    if (i < (groups.size() - 1))
                        sb.append(',');
                }
            }
            sb.append(']');
        }
        return sb.toString();
    }

    /**
	 * @since 2.1
	 */
    public boolean updateResource(String resourceName, IPresence p) {
        if (resourceName == null)
            return false;
        synchronized (resources) {
            for (Iterator i = resources.iterator(); i.hasNext(); ) {
                RosterResource r = (RosterResource) i.next();
                if (r.getName().equals(resourceName)) {
                    r.setPresence(p);
                    return true;
                }
            }
            resources.add(new RosterResource(this, resourceName, p));
            return false;
        }
    }

    /**
	 * @since 2.1
	 */
    public RosterResource removeResource(String resourceName) {
        if (resourceName == null)
            return null;
        RosterResource result = null;
        synchronized (resources) {
            for (Iterator i = resources.iterator(); i.hasNext(); ) {
                RosterResource r = (RosterResource) i.next();
                if (r.getName().equals(resourceName)) {
                    i.remove();
                    result = r;
                }
            }
        }
        return result;
    }

    /**
	 * @since 2.1
	 */
    public IRosterResource[] getResources() {
        List result = null;
        synchronized (resources) {
            result = new ArrayList(resources);
        }
        return (IRosterResource[]) result.toArray(new IRosterResource[result.size()]);
    }
}
