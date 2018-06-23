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
package org.eclipse.ecf.provider.generic;

import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.generic.gmm.*;

class SOContainerGMM implements Observer {

    SOContainer container;

    Member localMember;

    GMMImpl groupManager;

    // Maximum number of members. Default is -1 (no maximum).
    int maxMembers = -1;

    TreeMap loading, active;

     SOContainerGMM(SOContainer cont, Member local) {
        container = cont;
        groupManager = new GMMImpl();
        groupManager.addObserver(this);
        loading = new TreeMap();
        active = new TreeMap();
        localMember = local;
        addMember(local);
    }

    protected void debug(String msg) {
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.SOCONTAINERGMM, msg + ";container=" + container.getID() + ";existingmembers=" + groupManager);
    }

    protected void traceStack(String msg, Throwable e) {
        //$NON-NLS-1$
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SOContainerGMM.class, container.getID() + ":" + msg, e);
    }

    ID[] getSharedObjectIDs() {
        return getActiveKeys();
    }

    synchronized boolean addMember(Member m) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("addMember(" + m.getID() + ")");
        if (maxMembers > 0 && getSize() > maxMembers) {
            return false;
        }
        return groupManager.addMember(m);
    }

    synchronized int setMaxMembers(int max) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("setMaxMembers(" + max + ")");
        final int old = maxMembers;
        maxMembers = max;
        return old;
    }

    synchronized int getMaxMembers() {
        return maxMembers;
    }

    synchronized boolean removeMember(Member m) {
        final boolean res = groupManager.removeMember(m);
        if (res) {
            removeSharedObjects(m);
        }
        return res;
    }

    synchronized boolean removeMember(ID id) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("removeMember(" + id + ")");
        final Member m = getMemberForID(id);
        if (m == null)
            return false;
        return removeMember(m);
    }

    void removeAllMembers() {
        removeAllMembers(null);
    }

    void removeNonLocalMembers() {
        removeAllMembers(localMember);
    }

    synchronized void removeAllMembers(Member exception) {
        if (exception == null) {
            //$NON-NLS-1$
            debug("removeAllMembers()");
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("removeAllMembers(" + exception.getID() + ")");
        }
        final Object m[] = getMembers();
        for (int i = 0; i < m.length; i++) {
            final Member mem = (Member) m[i];
            if (exception == null || !exception.equals(mem))
                removeMember(mem);
        }
    }

    synchronized Object[] getMembers() {
        return groupManager.getMembers();
    }

    synchronized ID[] getOtherMemberIDs() {
        return groupManager.getMemberIDs(localMember.getID());
    }

    synchronized ID[] getMemberIDs() {
        return groupManager.getMemberIDs(null);
    }

    synchronized Member getMemberForID(ID id) {
        final Member newMem = new Member(id);
        for (final Iterator i = iterator(); i.hasNext(); ) {
            final Member oldMem = (Member) i.next();
            if (newMem.equals(oldMem))
                return oldMem;
        }
        return null;
    }

    synchronized int getSize() {
        return groupManager.getSize();
    }

    synchronized boolean containsMember(Member m) {
        if (m != null) {
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("containsMember(" + m.getID() + ")");
        }
        return groupManager.containsMember(m);
    }

    synchronized Iterator iterator() {
        return groupManager.iterator();
    }

    // End group membership change methods
    synchronized boolean addSharedObject(SOWrapper ro) {
        if (ro != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("addSharedObject(" + ro.getObjID() + ")");
        if (getFromAny(ro.getObjID()) != null)
            return false;
        addSharedObjectToActive(ro);
        return true;
    }

    synchronized boolean addLoadingSharedObject(SOContainer.LoadingSharedObject lso) {
        if (lso != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("addLoadingSharedObject(" + lso.getID() + ")");
        if (getFromAny(lso.getID()) != null)
            return false;
        loading.put(lso.getID(), new SOWrapper(lso, container));
        // And start the thing
        lso.start();
        return true;
    }

    synchronized void moveSharedObjectFromLoadingToActive(SOWrapper ro) {
        if (ro != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("moveSharedObjectFromLoadingToActive(" + ro.getObjID() + ")");
        if (removeSharedObjectFromLoading(ro.getObjID()))
            addSharedObjectToActive(ro);
    }

    boolean removeSharedObjectFromLoading(ID id) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("removeSharedObjectFromLoading(" + id + ")");
        if (loading.remove(id) != null) {
            return true;
        }
        return false;
    }

    synchronized ID[] getActiveKeys() {
        return (ID[]) active.keySet().toArray(new ID[0]);
    }

    void addSharedObjectToActive(SOWrapper so) {
        if (so != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            debug("addSharedObjectToActive(" + so.getObjID() + ")");
        active.put(so.getObjID(), so);
        so.activated();
    }

    synchronized void notifyOthersActivated(ID id) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("notifyOthersActivated(" + id + ")");
        notifyOtherChanged(id, active, true);
    }

    synchronized void notifyOthersDeactivated(ID id) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("notifyOthersDeactivated(" + id + ")");
        notifyOtherChanged(id, active, false);
    }

    void notifyOtherChanged(ID id, TreeMap aMap, boolean activated) {
        for (final Iterator i = aMap.values().iterator(); i.hasNext(); ) {
            final SOWrapper other = (SOWrapper) i.next();
            if (!id.equals(other.getObjID())) {
                other.otherChanged(id, activated);
            }
        }
    }

    synchronized boolean removeSharedObject(ID id) {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("removeSharedObject(" + id + ")");
        final SOWrapper ro = removeFromMap(id, active);
        if (ro == null)
            return false;
        ro.deactivated();
        return true;
    }

    synchronized SOWrapper getFromMap(ID objID, TreeMap aMap) {
        return (SOWrapper) aMap.get(objID);
    }

    synchronized SOWrapper removeFromMap(ID objID, TreeMap aMap) {
        return (SOWrapper) aMap.remove(objID);
    }

    SOWrapper getFromLoading(ID objID) {
        return getFromMap(objID, loading);
    }

    SOWrapper getFromActive(ID objID) {
        return getFromMap(objID, active);
    }

    synchronized SOWrapper getFromAny(ID objID) {
        SOWrapper ro = getFromMap(objID, active);
        if (ro != null)
            return ro;
        ro = getFromMap(objID, loading);
        return ro;
    }

    // Notification methods
    void notifyAllOfMemberChange(Member m, TreeMap map, boolean add) {
        for (final Iterator i = map.values().iterator(); i.hasNext(); ) {
            final SOWrapper ro = (SOWrapper) i.next();
            ro.memberChanged(m, add);
        }
    }

    public void update(Observable o, Object arg) {
        final MemberChanged mc = (MemberChanged) arg;
        notifyAllOfMemberChange(mc.getMember(), active, mc.getAdded());
    }

    synchronized void removeSharedObjects(Member m) {
        removeSharedObjects(m, true);
    }

    synchronized void clear() {
        //$NON-NLS-1$
        debug("clear()");
        removeSharedObjects(null, true);
    }

    void removeSharedObjects(Member m, boolean match) {
        final HashSet set = getRemoveIDs(m.getID(), match);
        final Iterator i = set.iterator();
        while (i.hasNext()) {
            final ID removeID = (ID) i.next();
            if (isLoading(removeID)) {
                removeSharedObjectFromLoading(removeID);
            } else {
                container.destroySharedObject(removeID);
            }
        }
    }

    HashSet getRemoveIDs(ID homeID, boolean match) {
        final HashSet aSet = new HashSet();
        for (final Iterator i = new DestroyIterator(loading, homeID, match); i.hasNext(); ) {
            aSet.add(i.next());
        }
        for (final Iterator i = new DestroyIterator(active, homeID, match); i.hasNext(); ) {
            aSet.add(i.next());
        }
        return aSet;
    }

    synchronized boolean isActive(ID id) {
        return active.containsKey(id);
    }

    synchronized boolean isLoading(ID id) {
        return loading.containsKey(id);
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        //$NON-NLS-1$
        sb.append("SOContainerGMM[");
        sb.append(groupManager);
        //$NON-NLS-1$
        sb.append(";load:").append(loading);
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(";active:").append(active).append("]");
        return sb.toString();
    }
}

class DestroyIterator implements Iterator {

    ID next;

    ID homeID;

    Iterator i;

    boolean match;

    public  DestroyIterator(TreeMap map, ID hID, boolean m) {
        i = map.values().iterator();
        homeID = hID;
        next = null;
        match = m;
    }

    public boolean hasNext() {
        if (next == null)
            next = getNext();
        return (next != null);
    }

    public Object next() {
        if (hasNext()) {
            final ID value = next;
            next = null;
            return value;
        }
        throw new java.util.NoSuchElementException();
    }

    ID getNext() {
        while (i.hasNext()) {
            final SOWrapper ro = (SOWrapper) i.next();
            if (homeID == null || (match ^ !ro.getHomeID().equals(homeID))) {
                return ro.getObjID();
            }
        }
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
