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
package org.eclipse.ecf.example.collab.share;

import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerTransaction;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddAbortException;
import org.eclipse.ecf.internal.example.collab.Messages;

public class TransactionSharedObject extends GenericSharedObject implements ISharedObjectContainerTransaction {

    //$NON-NLS-1$
    public static final String REPLICA_COMMIT_MSG = "replicaCommit";

    public static int DEFAULT_TIMEOUT = 30000;

    // Dummy inner class to provide lock
    static final class Lock {
    }

    // Timeout value associated with this object's replication
    protected int timeout;

    // Replication state this object is currently in.
    protected byte state;

    // A lock variable
    protected Lock lock;

    protected Vector participantIDs;

    protected Hashtable failedParticipants;

    public  TransactionSharedObject(int timeout) {
        this.timeout = timeout;
        init();
    }

    public  TransactionSharedObject() {
        this(DEFAULT_TIMEOUT);
    }

    protected void init() {
        state = ISharedObjectContainerTransaction.ACTIVE;
        lock = new Lock();
        participantIDs = new Vector();
        failedParticipants = new Hashtable();
    }

    public void activated(ID[] others) {
        // No other state changes while this is going on
        synchronized (lock) {
            if (isHost()) {
                replicate(null);
                addRemoteParticipants(getContext().getGroupMemberIDs());
                state = ISharedObjectContainerTransaction.VOTING;
            // Clients
            } else {
                try {
                    // Try to respond with create success message back to host
                    getContext().sendCreateResponse(getHomeContainerID(), null, getNextReplicateID());
                    // If above succeeds, we're now in prepared state
                    state = ISharedObjectContainerTransaction.PREPARED;
                } catch (Exception e) {
                    state = ISharedObjectContainerTransaction.ABORTED;
                    log("unable to send create response to " + getHomeContainerID(), e);
                }
            }
            // Notify any threads waiting on state change
            lock.notifyAll();
        }
    }

    public void memberAdded(ID member) {
        if (isHost()) {
            // replicate message
            synchronized (lock) {
                replicate(member);
                if (getTransactionState() == ISharedObjectContainerTransaction.VOTING)
                    addRemoteParticipants(new ID[] { member });
                else
                    replicate(member);
            }
        }
    }

    protected void addRemoteParticipants(ID ids[]) {
        if (ids != null && participantIDs != null) {
            for (int i = 0; i < ids.length; i++) {
                if (!getHomeContainerID().equals(ids[i]))
                    participantIDs.addElement(ids[i]);
            }
        }
    }

    protected void removeRemoteParticipant(ID id) {
        if (id != null && participantIDs != null) {
            int index = participantIDs.indexOf(id);
            if (index != -1)
                participantIDs.removeElementAt(index);
        }
    }

    protected void addRemoteParticipantFailed(ID remote, Throwable failure) {
        if (remote != null && failure != null && failedParticipants != null) {
            failedParticipants.put(remote, failure);
        }
    }

    public void handleCreateResponse(ID fromID, Throwable e, Long identifier) {
        // If no exception, remove
        synchronized (lock) {
            if (state == ISharedObjectContainerTransaction.VOTING) {
                if (e == null) {
                    removeRemoteParticipant(fromID);
                } else {
                    addRemoteParticipantFailed(fromID, e);
                }
            } else {
                handleVotingCompletedCreateResponse(fromID, e, identifier);
            }
            lock.notifyAll();
        }
    }

    protected void handleVotingCompletedCreateResponse(ID fromID, Throwable e, Long identifier) {
        // If remote creation was successful, simply send commit message back.
        if (e == null) {
            // send commit message right back.
            try {
                forwardMsgTo(fromID, SharedObjectMsg.createMsg((String) null, REPLICA_COMMIT_MSG));
            } catch (Exception except) {
                log("Exception sending commit message to " + fromID, except);
            }
        }
    }

    public void memberRemoved(ID member) {
        // We only care about this if we are the host.
        if (isHost()) {
            synchronized (lock) {
                if (state == ISharedObjectContainerTransaction.VOTING) {
                    addRemoteParticipantFailed(member, new //$NON-NLS-1$
                    Exception("Member " + //$NON-NLS-1$
                    member + " left"));
                }
                lock.notifyAll();
            }
        }
    }

    public void waitToCommit() throws SharedObjectAddAbortException {
        synchronized (lock) {
            long end = System.currentTimeMillis() + timeout;
            try {
                while (!votingCompleted()) {
                    long wait = end - System.currentTimeMillis();
                    if (wait <= 0L)
                        throw new SharedObjectAddAbortException(Messages.TransactionSharedObject_EXCEPTION_TIMEOUT);
                    // Actually wait right here
                    lock.wait(wait);
                }
            } catch (InterruptedException e) {
                throw new SharedObjectAddAbortException(Messages.TransactionSharedObject_EXCEPTION_INTERUPTED);
            } catch (SharedObjectAddAbortException e1) {
                doAbort(e1);
            }
            // Success. Send commit to remotes and clean up before returning.
            doCommit();
        }
    }

    public byte getTransactionState() {
        synchronized (lock) {
            return state;
        }
    }

    protected void doAbort(SharedObjectAddAbortException e) throws SharedObjectAddAbortException {
        // Send destroy message here so all remotes get destroyed, and we remove
        // ourselves from local space as well.
        destroySelf();
        // Set our own state variable to ABORTED
        state = ISharedObjectContainerTransaction.ABORTED;
        // throw so caller gets exception and can deal with it
        throw e;
    }

    public void doCommit() throws SharedObjectAddAbortException {
        // Get current membership
        int others = 0;
        others = getContext().getGroupMemberIDs().length;
        // and the current membership is > 0 (we're connected to something)
        if (participantIDs != null && others > 0) {
            // Send replicaCommit message to all remote clients
            try {
                forwardMsgTo(null, SharedObjectMsg.createMsg((String) null, REPLICA_COMMIT_MSG));
            } catch (Exception e2) {
                doAbort(new SharedObjectAddAbortException(Messages.TransactionSharedObject_EXCEPTION_ON_COMMIT_MESSAGE, e2));
            }
        }
        // Set state variable to committed.
        state = ISharedObjectContainerTransaction.COMMITTED;
        // Call local committed message
        committed();
        participantIDs = null;
        failedParticipants = null;
    }

    protected void execMsgInvoke(SharedObjectMsg msg, ID fromID, Object o) throws Exception {
        if (o == this) {
            // Object[] args = msg.getArgs();
            String name = msg.getMethodName();
            if (name.equals(REPLICA_COMMIT_MSG)) {
                replicaCommit();
                return;
            }
        }
        super.execMsgInvoke(msg, fromID, o);
    }

    public final void replicaCommit() {
        synchronized (lock) {
            state = COMMITTED;
            lock.notifyAll();
            participantIDs = null;
            failedParticipants = null;
        }
        // Call subclass overrideable method
        committed();
    }

    protected void committed() {
    // Subclasses may override as appropriate
    }

    protected boolean votingCompleted() throws SharedObjectAddAbortException {
        // the transaction. If so, we throw.
        if (failedParticipants != null && failedParticipants.size() > 0) {
            ID remoteID = (ID) failedParticipants.keys().nextElement();
            Exception e = (Exception) failedParticipants.get(remoteID);
            // Abort!
            throw new SharedObjectAddAbortException(Messages.TransactionSharedObject_EXCEPTION_FROM_ABORT, e);
        // If no problems, and the number of participants to here from is 0,
        // then we're done
        } else if (state == ISharedObjectContainerTransaction.VOTING && participantIDs.size() == 0) {
            // Success!
            return true;
        }
        // Else continue waiting
        return false;
    }
}
