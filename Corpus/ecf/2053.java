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
package org.eclipse.ecf.example.collab.share;

import org.eclipse.ecf.internal.example.collab.ui.LineChatClientView;
import org.eclipse.ecf.presence.roster.*;

/**
 * 
 * @since 2.0
 */
public class RosterListener implements IRosterListener {

    private final EclipseCollabSharedObject sharedObject;

    private final LineChatClientView view;

     RosterListener(EclipseCollabSharedObject sharedObject, LineChatClientView view) {
        this.sharedObject = sharedObject;
        this.view = view;
    }

    public void handleRosterEntryAdd(IRosterEntry entry) {
        boolean addUserResult = view.addUser(entry.getUser());
        // And we need to report our own existence to them
        if (addUserResult)
            sharedObject.sendNotifyUserAdded();
    }

    public void handleRosterEntryRemove(IRosterEntry entry) {
        view.removeUser(entry.getUser().getID());
    }

    public void handleRosterUpdate(IRoster roster, IRosterItem changedValue) {
    // unimplemented, update code has been removed at the moment
    }
}
