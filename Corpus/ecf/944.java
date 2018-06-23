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
package org.eclipse.ecf.internal.examples.webinar.util.rosterview;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.examples.webinar.Activator;
import org.eclipse.ecf.internal.examples.webinar.util.RosterWriterHelper;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 *
 */
public class ShowAllRostersContribution extends CompoundContributionItem {

    /* (non-Javadoc)
	 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
	 */
    protected IContributionItem[] getContributionItems() {
        IAction action = new Action() {

            public void run() {
                // Get containers from container manager
                IContainer[] containers = Activator.getDefault().getContainers();
                List l = new ArrayList();
                for (int i = 0; i < containers.length; i++) {
                    // Make sure the container is connected
                    if (containers[i].getConnectedID() != null) {
                        // Make sure container implements IPresenceContainerAdapter API
                        Object o = containers[i].getAdapter(IPresenceContainerAdapter.class);
                        if (o != null)
                            l.add(o);
                    }
                }
                IPresenceContainerAdapter[] adapters = (IPresenceContainerAdapter[]) l.toArray(new IPresenceContainerAdapter[] {});
                for (int i = 0; i < adapters.length; i++) {
                    // Show all rosters on console...or do other things with it here.
                    new RosterWriterHelper().writeRosterToConsole(adapters[i].getRosterManager().getRoster());
                }
            }
        };
        action.setText("show all rosters on console");
        return new IContributionItem[] { new ActionContributionItem(action) };
    }
}
