/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui.handlers;

import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

public class CompareWithMenuContributionItem extends AbstractRosterMenuContributionItem implements IWorkbenchContribution {

    private ISelectionService selectionService;

    public  CompareWithMenuContributionItem() {
        setTopMenuName(Messages.CompareWithMenuContributionItem_MenuTitle);
    }

    public void initialize(IServiceLocator serviceLocator) {
        selectionService = (ISelectionService) serviceLocator.getService(ISelectionService.class);
    }

    protected IContributionItem[] getContributionItems() {
        //FIXME: interim hack to only be enabled for single selections
        if (selectionService != null) {
            ISelection selection = selectionService.getSelection();
            if (selection instanceof IStructuredSelection) {
                if (((IStructuredSelection) selection).size() != 1) {
                    return NO_CONTRIBUTIONS;
                }
            }
        }
        return super.getContributionItems();
    }

    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new CompareWithHandler(rosterEntry);
    }
}
