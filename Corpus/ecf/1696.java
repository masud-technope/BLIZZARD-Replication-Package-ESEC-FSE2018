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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.presence.PresencePlugin;

/**
 * Base class implmentation of {@link IRosterItem} super interface. This class
 * is a superclass for the {@link RosterEntry} and {@link RosterGroup} classes.
 */
public class RosterItem implements IRosterItem {

    protected String name;

    protected IRosterItem parent;

    protected  RosterItem() {
    // protected root constructor
    }

    public  RosterItem(IRosterItem parent, String name) {
        Assert.isNotNull(name);
        this.parent = parent;
        this.name = name;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterItem#getName()
	 */
    public String getName() {
        return name;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterItem#getParent()
	 */
    public IRosterItem getParent() {
        return parent;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    public IRoster getRoster() {
        if (this instanceof IRoster)
            return (IRoster) this;
        IRosterItem p = getParent();
        while (p != null && !(p instanceof IRoster)) p = p.getParent();
        return (IRoster) p;
    }
}
