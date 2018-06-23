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
package org.eclipse.ecf.presence.roster;

import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.presence.PresencePlugin;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceSender;

public abstract class AbstractRosterManager implements IRosterManager {

    protected IRoster roster;

    private List rosterSubscriptionListeners = new ArrayList();

    private List rosterUpdateListeners = new ArrayList();

    public  AbstractRosterManager() {
    // null constructor
    }

    public  AbstractRosterManager(IRoster roster) {
        this.roster = roster;
    }

    public synchronized void addRosterSubscriptionListener(IRosterSubscriptionListener listener) {
        if (listener != null) {
            synchronized (rosterSubscriptionListeners) {
                rosterSubscriptionListeners.add(listener);
            }
        }
    }

    public synchronized void addRosterListener(IRosterListener listener) {
        if (listener != null) {
            synchronized (rosterUpdateListeners) {
                rosterUpdateListeners.add(listener);
            }
        }
    }

    protected void fireRosterUpdate(IRosterItem changedItem) {
        List toNotify = null;
        synchronized (rosterUpdateListeners) {
            toNotify = new ArrayList(rosterUpdateListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) ((IRosterListener) i.next()).handleRosterUpdate(roster, changedItem);
    }

    protected void fireRosterAdd(IRosterEntry entry) {
        List toNotify = null;
        synchronized (rosterUpdateListeners) {
            toNotify = new ArrayList(rosterUpdateListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) ((IRosterListener) i.next()).handleRosterEntryAdd(entry);
    }

    protected void fireRosterRemove(IRosterEntry entry) {
        List toNotify = null;
        synchronized (rosterUpdateListeners) {
            toNotify = new ArrayList(rosterUpdateListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) ((IRosterListener) i.next()).handleRosterEntryRemove(entry);
    }

    protected void fireSubscriptionListener(ID fromID, IPresence.Type presencetype) {
        List toNotify = null;
        synchronized (rosterSubscriptionListeners) {
            toNotify = new ArrayList(rosterSubscriptionListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            IRosterSubscriptionListener l = (IRosterSubscriptionListener) i.next();
            if (presencetype.equals(IPresence.Type.SUBSCRIBE)) {
                l.handleSubscribeRequest(fromID);
            } else if (presencetype.equals(IPresence.Type.SUBSCRIBED)) {
                l.handleSubscribed(fromID);
            } else if (presencetype.equals(IPresence.Type.UNSUBSCRIBED)) {
                l.handleUnsubscribed(fromID);
            }
        }
    }

    public abstract IPresenceSender getPresenceSender();

    public IRoster getRoster() {
        return roster;
    }

    public abstract IRosterSubscriptionSender getRosterSubscriptionSender();

    public synchronized void removeRosterSubscriptionListener(IRosterSubscriptionListener listener) {
        if (listener != null) {
            synchronized (rosterSubscriptionListeners) {
                rosterSubscriptionListeners.remove(listener);
            }
        }
    }

    public synchronized void removeRosterListener(IRosterListener listener) {
        if (listener != null) {
            synchronized (rosterUpdateListeners) {
                rosterUpdateListeners.remove(listener);
            }
        }
    }

    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager != null) {
            return adapterManager.loadAdapter(this, adapter.getName());
        }
        return null;
    }

    public void disconnect() {
        roster.getItems().clear();
        fireRosterUpdate(roster);
        synchronized (rosterUpdateListeners) {
            rosterUpdateListeners.clear();
        }
        synchronized (rosterSubscriptionListeners) {
            rosterSubscriptionListeners.clear();
        }
    }
}
