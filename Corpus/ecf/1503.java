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
import org.eclipse.ecf.presence.roster.IRoster;

/**
 * Abstract contribution item class for creating menu contribution items for
 * roster entries. Subclasses should be created as appropriate.
 */
public abstract class AbstractRosterContributionItem extends AbstractPresenceContributionItem {

    public  AbstractRosterContributionItem() {
        super(null);
    }

    public  AbstractRosterContributionItem(String id) {
        super(id);
    }

    /**
	 * Get the currently selected IRoster.
	 * 
	 * @return IRoster that is current workbenchwindow selection. Returns
	 *         <code>null</code> if nothing is selected or if something other
	 *         than IRoster is selected.
	 */
    protected IRoster getSelectedRoster() {
        Object selection = getSelection();
        if (selection instanceof IRoster)
            return (IRoster) selection;
        return null;
    }

    /**
	 * Get container for the given IRoster.
	 * 
	 * @param roster
	 *            the IRoster. May be <code>null</code>.
	 * 
	 * @return IContainer associated with currently selected IRosterEntry.
	 *         Returns <code>null</code> if the given <code>entry</code> is
	 *         null, or if the container associated with the <code>entry</code>
	 *         cannot be accessed.
	 */
    protected IContainer getContainerForRoster(IRoster roster) {
        if (roster == null)
            return null;
        IPresenceContainerAdapter pca = roster.getPresenceContainerAdapter();
        if (pca != null)
            return (IContainer) pca.getAdapter(IContainer.class);
        return null;
    }
}
