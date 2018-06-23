/****************************************************************************
 * Copyright (c) 2006, 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.msn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.protocol.msn.Contact;
import org.eclipse.ecf.protocol.msn.Group;

final class MSNRosterGroup implements IRosterGroup {

    private final IRoster roster;

    private final List entries;

    private final Group group;

     MSNRosterGroup(IRoster roster, Group group) {
        this.roster = roster;
        this.group = group;
        entries = new ArrayList();
    }

    public Collection getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    void add(MSNRosterEntry entry) {
        entries.add(entry);
        entry.setParent(this);
    }

    Group getGroup() {
        return group;
    }

    MSNRosterEntry getEntryFor(Contact contact) {
        for (int i = 0; i < entries.size(); i++) {
            MSNRosterEntry entry = (MSNRosterEntry) entries.get(i);
            if (entry.getContact().equals(contact)) {
                return entry;
            }
        }
        return null;
    }

    public String getName() {
        return group.getName();
    }

    public IRosterItem getParent() {
        return roster;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IRosterItem#getRoster()
	 */
    public IRoster getRoster() {
        return roster;
    }
}
