/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.sync.ui.resources;

import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;

public class ResourcesShareMenuContributionItem extends AbstractRosterMenuContributionItem {

    public  ResourcesShareMenuContributionItem() {
        setTopMenuName("Share Project");
    }

    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new ResourcesShareHandler(rosterEntry);
    }
}
