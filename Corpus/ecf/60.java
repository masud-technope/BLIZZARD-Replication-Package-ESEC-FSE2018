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
package org.eclipse.ecf.presence.ui.roster;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;

/**
 * Abstract contribution item class for creating menu contribution items for
 * roster entries. Subclasses should be created as appropriate.
 */
public abstract class AbstractRosterGroupContributionItem extends AbstractPresenceContributionItem {

    public  AbstractRosterGroupContributionItem() {
        super(null);
    }

    public  AbstractRosterGroupContributionItem(String id) {
        super(id);
    }

    /**
	 * Get the currently selected IRosterGroup.
	 * 
	 * @return IRosterGroup that is current workbenchwindow selection. Returns
	 *         <code>null</code> if nothing is selected or if something other
	 *         than IRosterGroup is selected.
	 */
    protected IRosterGroup getSelectedRosterGroup() {
        Object selection = getSelection();
        if (selection instanceof IRosterEntry)
            return (IRosterGroup) selection;
        return null;
    }

    /**
	 * Get container for the given IRosterGroup.
	 * 
	 * @param group
	 *            the IRosterGroup. May be <code>null</code>.
	 * 
	 * @return IContainer associated with currently selected IRosterGroup.
	 *         Returns <code>null</code> if the given <code>entry</code> is
	 *         null, or if the container associated with the <code>group</code>
	 *         cannot be accessed.
	 */
    protected IContainer getContainerForRosterEntry(IRosterEntry group) {
        if (group == null)
            return null;
        IPresenceContainerAdapter pca = group.getRoster().getPresenceContainerAdapter();
        if (pca != null)
            return (IContainer) pca.getAdapter(IContainer.class);
        return null;
    }
}
