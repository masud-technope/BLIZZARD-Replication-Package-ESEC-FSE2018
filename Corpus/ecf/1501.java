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

import org.eclipse.ecf.internal.sync.resources.core.ResourcesShare;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterListener;

public class RosterListener implements IRosterListener {

    private ResourcesShare share;

    private String projectName;

    private IRosterEntry entry;

    public  RosterListener(ResourcesShare share, String projectName, IRosterEntry entry) {
        this.share = share;
        this.projectName = projectName;
        this.entry = entry;
    }

    public void handleRosterEntryAdd(IRosterEntry entry) {
    }

    public void handleRosterEntryRemove(IRosterEntry entry) {
        if (this.entry.equals(entry)) {
            share.stopSharing(projectName);
        }
    }

    public void handleRosterUpdate(IRoster roster, IRosterItem changedValue) {
    }
}
