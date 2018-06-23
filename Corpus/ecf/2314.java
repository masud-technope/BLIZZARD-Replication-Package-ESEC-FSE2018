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
import org.eclipse.team.internal.ecf.ui.Messages;

public class SynchronizeWithMenuContributionItem extends AbstractRosterMenuContributionItem {

    public  SynchronizeWithMenuContributionItem() {
        setTopMenuName(Messages.SynchronizeWithMenuContributionItem_MenuTitle);
    }

    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new SynchronizeWithHandler(rosterEntry);
    }
}
