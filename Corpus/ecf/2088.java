/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui.console;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ConsoleRosterMenuContributionItem extends AbstractRosterMenuContributionItem {

    public  ConsoleRosterMenuContributionItem() {
    // do nothing
    }

    public  ConsoleRosterMenuContributionItem(String id) {
        super(id);
    }

    /**
	 * Get the currently selected model object.
	 * 
	 * @return Object that is current workbenchwindow selection. Returns
	 *         <code>null</code> if nothing is selected.
	 */
    protected Object getSelection() {
        final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (ww != null) {
            final IWorkbenchPage p = ww.getActivePage();
            if (p != null) {
                final ISelection selection = p.getSelection();
                if (selection != null && selection instanceof IStructuredSelection)
                    return ((IStructuredSelection) selection).getFirstElement();
            }
        }
        return null;
    }

    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new AbstractRosterMenuHandler(rosterEntry) {

            /**
			 * @throws ExecutionException  
			 */
            public Object execute(ExecutionEvent arg0) throws ExecutionException {
                final Object s = getSelection();
                System.out.println(s);
                System.out.println(getRosterEntry());
                System.out.println(arg0);
                return null;
            }
        };
    }
}
