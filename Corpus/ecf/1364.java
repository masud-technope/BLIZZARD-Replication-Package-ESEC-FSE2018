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
package org.eclipse.ecf.presence.ui.menu;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ecf.presence.roster.IRosterEntry;

/**
 *
 */
public class NoopRosterMenuContributionItem extends AbstractRosterMenuContributionItem {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem#createRosterEntryHandler(org.eclipse.ecf.presence.roster.IRosterEntry)
	 */
    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new AbstractRosterMenuHandler(rosterEntry) {

            public Object execute(ExecutionEvent arg0) {
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("execute(" + arg0 + ") on rosterEntry=" + getRosterEntry());
                return null;
            }
        };
    }
}
