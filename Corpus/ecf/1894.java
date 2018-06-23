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

import org.eclipse.core.commands.*;
import org.eclipse.ecf.presence.roster.IRosterEntry;

public abstract class AbstractRosterMenuHandler extends AbstractHandler {

    private IRosterEntry rosterEntry;

    public  AbstractRosterMenuHandler(IRosterEntry entry) {
        this.rosterEntry = entry;
    }

    protected void fireHandlerChangeEvent() {
        super.fireHandlerChanged(new HandlerEvent(this, false, true));
    }

    public IRosterEntry getRosterEntry() {
        return rosterEntry;
    }

    /* (non-Javadoc)
	* @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	*/
    public abstract Object execute(final ExecutionEvent arg0) throws ExecutionException;

    /* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#dispose()
	 */
    public void dispose() {
        super.dispose();
        rosterEntry = null;
    }
}
