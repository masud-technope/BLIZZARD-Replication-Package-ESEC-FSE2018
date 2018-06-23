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
package org.eclipse.ecf.internal.examples.webinar.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.examples.webinar.Activator;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;

/**
 * Chat with me about this at:  xmpp://fliwatuet@ecf.eclipse.org
 */
public class RosterWriterHelper {

    private IContainer[] getContainers() {
        return Activator.getDefault().getContainers();
    }

    private Object[] getConnectedContainerAdapters(Class adapterType) {
        IContainer[] containers = getContainers();
        List l = new ArrayList();
        for (int i = 0; i < containers.length; i++) {
            // Make sure connected
            if (containers[i].getConnectedID() != null) {
                Object o = containers[i].getAdapter(adapterType);
                if (o != null)
                    l.add(o);
            }
        }
        return (Object[]) l.toArray();
    }

    public void writeAllRostersToConsole() {
        Object[] adapters = getConnectedContainerAdapters(IPresenceContainerAdapter.class);
        for (int i = 0; i < adapters.length; i++) {
            IPresenceContainerAdapter presenceAdapter = (IPresenceContainerAdapter) adapters[i];
            showRosterItems(presenceAdapter.getRosterManager().getRoster());
        }
    }

    public void writeRosterToConsole(IRoster roster) {
        showRosterItems(roster);
    }

    private void showRosterItems(IRosterItem rosterItem) {
        if (rosterItem == null)
            return;
        Collection children = null;
        if (rosterItem instanceof IRoster) {
            System.out.println("Roster: " + rosterItem.getName());
            children = ((IRoster) rosterItem).getItems();
        } else if (rosterItem instanceof IRosterGroup) {
            System.out.println("  Group: " + rosterItem.getName());
            children = ((IRosterGroup) rosterItem).getEntries();
        } else if (rosterItem instanceof IRosterEntry) {
            System.out.println("    Entry: " + rosterItem.getName());
            System.out.println("     Type: " + ((IRosterEntry) rosterItem).getPresence().getType());
            children = null;
        }
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext(); ) showRosterItems((IRosterItem) i.next());
        }
    }
}
