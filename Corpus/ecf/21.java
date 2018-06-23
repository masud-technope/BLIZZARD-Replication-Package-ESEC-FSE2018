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

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * 
 */
public abstract class AbstractPresenceContributionItem extends CompoundContributionItem {

    protected static final IContributionItem[] EMPTY_ARRAY = new IContributionItem[0];

    public  AbstractPresenceContributionItem() {
        super(null);
    }

    public  AbstractPresenceContributionItem(String id) {
        super(id);
    }

    /**
	 * Get the currently selected model object.
	 * 
	 * @return Object that is current workbenchwindow selection. Returns
	 *         <code>null</code> if nothing is selected.
	 */
    protected Object getSelection() {
        IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (ww != null) {
            IWorkbenchPage p = ww.getActivePage();
            if (p != null) {
                ISelection selection = p.getSelection();
                if (selection != null && selection instanceof IStructuredSelection)
                    return ((IStructuredSelection) selection).getFirstElement();
            }
        }
        return null;
    }

    /**
	 * Make IAction instances to return as contribution items.
	 * 
	 * @return IAction [] of new actions for contribution to menu.  If <code>null</code> then
	 * no contributions are made.
	 */
    protected abstract IAction[] makeActions();

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.ui.roster.AbstractPresenceContributionItem#getContributionItems()
	 */
    protected IContributionItem[] getContributionItems() {
        IAction[] actions = makeActions();
        if (actions == null)
            return EMPTY_ARRAY;
        // One extra for separator
        IContributionItem[] items = new IContributionItem[actions.length];
        for (int i = 0; i < actions.length; i++) items[i] = new ActionContributionItem(actions[i]);
        return items;
    }
}
