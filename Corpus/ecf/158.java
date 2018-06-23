/****************************************************************************
 * Copyright (c) 2008 Mustafa K. Isik and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mustafa K. Isik - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.sync.doc.cola;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.sync.Activator;
import org.eclipse.ecf.internal.sync.SyncDebugOptions;
import org.eclipse.ecf.sync.*;
import org.eclipse.ecf.sync.doc.DocumentChangeMessage;
import org.eclipse.ecf.sync.doc.IDocumentChange;
import org.eclipse.osgi.util.NLS;

public class ColaSynchronizationStrategy implements IModelSynchronizationStrategy {

    // <ColaDocumentChangeMessage>
    private final List unacknowledgedLocalOperations;

    private final boolean isInitiator;

    private long localOperationsCount;

    private long remoteOperationsCount;

    // <ID, ColaSynchronizationStrategy>
    private static Map sessionStrategies = new HashMap();

    private  ColaSynchronizationStrategy(boolean isInitiator) {
        this.isInitiator = isInitiator;
        unacknowledgedLocalOperations = new LinkedList();
        localOperationsCount = 0;
        remoteOperationsCount = 0;
    }

    public static ColaSynchronizationStrategy getInstanceFor(ID client, boolean isInitiator) {
        ColaSynchronizationStrategy existingStrategy = (ColaSynchronizationStrategy) sessionStrategies.get(client);
        if (existingStrategy != null && existingStrategy.isInitiator == isInitiator)
            return existingStrategy;
        existingStrategy = new ColaSynchronizationStrategy(isInitiator);
        sessionStrategies.put(client, existingStrategy);
        return existingStrategy;
    }

    public static void cleanUpFor(ID client) {
        sessionStrategies.remove(client);
    }

    public static void dispose() {
        sessionStrategies.clear();
    }

    /**
	 * Handles proper transformation of incoming
	 * <code>ColaDocumentChangeMessage</code>s. Returned
	 * <code>DocumentChangeMessage</code>s can be applied directly to the shared
	 * document. The method implements the concurrency algorithm described in
	 * <code>http://wiki.eclipse.org/RT_Shared_Editing</code>
	 * 
	 * @param remoteMsg
	 * @return List contains <code>DocumentChangeMessage</code>s ready for
	 *         sequential application to document
	 */
    public List transformIncomingMessage(final DocumentChangeMessage remoteMsg) {
        if (!(remoteMsg instanceof ColaDocumentChangeMessage)) {
            throw new IllegalArgumentException("DocumentChangeMessage is incompatible with Cola SynchronizationStrategy");
        }
        Trace.entering(Activator.PLUGIN_ID, SyncDebugOptions.METHODS_ENTERING, //$NON-NLS-1$
        this.getClass(), //$NON-NLS-1$
        "transformIncomingMessage", //$NON-NLS-1$
        remoteMsg);
        ColaDocumentChangeMessage transformedRemote = (ColaDocumentChangeMessage) remoteMsg;
        final List transformedRemotes = new LinkedList();
        transformedRemotes.add(transformedRemote);
        remoteOperationsCount++;
        Trace.trace(Activator.PLUGIN_ID, "unacknowledgedLocalOperations=" + unacknowledgedLocalOperations);
        // this is where the concurrency algorithm is executed
        if (// Do not remove this. It
        !unacknowledgedLocalOperations.isEmpty()) {
            // this message
            for (final Iterator it = unacknowledgedLocalOperations.iterator(); it.hasNext(); ) {
                final ColaDocumentChangeMessage unackedLocalOp = (ColaDocumentChangeMessage) it.next();
                if (transformedRemote.getRemoteOperationsCount() > unackedLocalOp.getLocalOperationsCount()) {
                    Trace.trace(Activator.PLUGIN_ID, NLS.bind("transformIncomingMessage.removing {0}", unackedLocalOp));
                    it.remove();
                } else {
                    // the unackowledgedLocalOperations queue is ordered and
                    // sorted
                    // due to sequential insertion of local ops, thus once a
                    // local op with a higher
                    // or equal local op count (i.e. remote op count from the
                    // remote operation's view)
                    // is reached, we can abandon the check for the remaining
                    // queue items
                    Trace.trace(Activator.PLUGIN_ID, "breaking out of unackedLocalOperations loop");
                    // exits for-loop
                    break;
                }
            }
            if (!unacknowledgedLocalOperations.isEmpty()) {
                ColaDocumentChangeMessage localOp = (ColaDocumentChangeMessage) unacknowledgedLocalOperations.get(0);
                Assert.isTrue(transformedRemote.getRemoteOperationsCount() == localOp.getLocalOperationsCount());
                for (final ListIterator unackOpsListIt = unacknowledgedLocalOperations.listIterator(); unackOpsListIt.hasNext(); ) {
                    for (final ListIterator trafoRemotesIt = transformedRemotes.listIterator(); trafoRemotesIt.hasNext(); ) {
                        // returns new instance
                        // clarify operation preference, owner/docshare
                        // initiator
                        // consistently comes first
                        localOp = (ColaDocumentChangeMessage) unackOpsListIt.next();
                        transformedRemote = (ColaDocumentChangeMessage) trafoRemotesIt.next();
                        transformedRemote = transformedRemote.transformAgainst(localOp, isInitiator);
                        if (transformedRemote.isSplitUp()) {
                            // currently this only happens for a remote deletion
                            // that needs to be transformed against a locally
                            // applied insertion
                            // attention: before applying a list of deletions to
                            // docshare, the indices need to be
                            // updated/finalized one last time
                            // since deletions can only be applied sequentially
                            // and every deletion is going to change the
                            // underlying document and the
                            // respective indices!
                            trafoRemotesIt.remove();
                            for (final Iterator splitUpIterator = transformedRemote.getSplitUpRepresentation().iterator(); splitUpIterator.hasNext(); ) {
                                trafoRemotesIt.add(splitUpIterator.next());
                            }
                        // according to the ListIterator documentation it
                        // seems so as if the following line is unnecessary,
                        // as a call to next() after the last removal and
                        // additions will return what it would have returned
                        // anyway
                        // trafoRemotesIt.next();//TODO not sure about the
                        // need for this - I want to jump over the two
                        // inserted ops and reach the end of this iterator
                        }
                        // instead
                        if (localOp.isSplitUp()) {
                            // local operation has been split up during
                            // operational transform --> remove current version
                            // and add new versions plus jump over those
                            unackOpsListIt.remove();
                            for (final Iterator splitUpOpIterator = localOp.getSplitUpRepresentation().iterator(); splitUpOpIterator.hasNext(); ) {
                                unackOpsListIt.add(splitUpOpIterator.next());
                            }
                        // according to the ListIterator documentation it
                        // seems so as if the following line is unnecessary,
                        // as a call to next() after the last removal and
                        // additions will return what it would have returned
                        // anyway
                        // unackOpsListIt.next();//TODO check whether or not
                        // this does jump over both inserted operations that
                        // replaced the current unack op
                        }
                    // end split up localop handling
                    }
                // transformedRemotes List iteration
                }
            }
        }
        Trace.exiting(Activator.PLUGIN_ID, SyncDebugOptions.METHODS_EXITING, //$NON-NLS-1$
        this.getClass(), //$NON-NLS-1$
        "transformIncomingMessage", //$NON-NLS-1$
        transformedRemote);
        // contains multiple deletions:
        if (transformedRemotes.size() > 1) {
            final ColaDocumentChangeMessage firstOp = (ColaDocumentChangeMessage) transformedRemotes.get(0);
            if (firstOp.isDeletion()) {
                // this means all operations in the return list must also be
                // deletions, i.e. modify virtual/optimistic offset for
                // sequential application to document
                final ListIterator deletionFinalizerIt = transformedRemotes.listIterator();
                ColaDocumentChangeMessage previousDel = (ColaDocumentChangeMessage) deletionFinalizerIt.next();
                // jump over first del-op does not need
                // modification, we know this is OK because of
                // previous size check;
                ColaDocumentChangeMessage currentDel;
                for (; deletionFinalizerIt.hasNext(); ) {
                    currentDel = (ColaDocumentChangeMessage) deletionFinalizerIt.next();
                    currentDel.setOffset(currentDel.getOffset() - previousDel.getLengthOfReplacedText());
                    previousDel = currentDel;
                }
            }
        }
        return transformedRemotes;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ColaSynchronizationStrategy");
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategy#registerLocalChange
	 * (org.eclipse.ecf.sync.doc.IModelChange)
	 */
    public IModelChangeMessage[] registerLocalChange(IModelChange localChange) {
        List results = new ArrayList();
        Trace.entering(Activator.PLUGIN_ID, SyncDebugOptions.METHODS_ENTERING, //$NON-NLS-1$
        this.getClass(), //$NON-NLS-1$
        "registerLocalChange", //$NON-NLS-1$
        localChange);
        if (localChange instanceof IDocumentChange) {
            final IDocumentChange docChange = (IDocumentChange) localChange;
            final ColaDocumentChangeMessage colaMsg = new ColaDocumentChangeMessage(new DocumentChangeMessage(docChange.getOffset(), docChange.getLengthOfReplacedText(), docChange.getText()), localOperationsCount, remoteOperationsCount);
            // to results
            if (!colaMsg.isReplacement()) {
                unacknowledgedLocalOperations.add(colaMsg);
                localOperationsCount++;
                results.add(colaMsg);
            } else {
                // It *is a replacement message, so we add both a delete and an
                // insert message
                // First create/add a delete message (text set to "")...
                ColaDocumentChangeMessage delMsg = new ColaDocumentChangeMessage(new DocumentChangeMessage(docChange.getOffset(), docChange.getLengthOfReplacedText(), ""), localOperationsCount, remoteOperationsCount);
                unacknowledgedLocalOperations.add(delMsg);
                localOperationsCount++;
                results.add(delMsg);
                // Then create/add the insert message (length set to 0)
                ColaDocumentChangeMessage insMsg = new ColaDocumentChangeMessage(new DocumentChangeMessage(docChange.getOffset(), 0, docChange.getText()), localOperationsCount, remoteOperationsCount);
                unacknowledgedLocalOperations.add(insMsg);
                localOperationsCount++;
                results.add(insMsg);
            }
            Trace.exiting(Activator.PLUGIN_ID, SyncDebugOptions.METHODS_EXITING, this.getClass(), //$NON-NLS-1$
            "registerLocalChange", //$NON-NLS-1$
            colaMsg);
        }
        return (IModelChangeMessage[]) results.toArray(new IModelChangeMessage[] {});
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategy#
	 * toDocumentChangeMessage(byte[])
	 */
    public IModelChange deserializeRemoteChange(byte[] bytes) throws SerializationException {
        return DocumentChangeMessage.deserialize(bytes);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategy#
	 * transformRemoteChange(org.eclipse.ecf.sync.doc.IModelChangeMessage)
	 */
    public IModelChange[] transformRemoteChange(IModelChange remoteChange) {
        if (!(remoteChange instanceof DocumentChangeMessage))
            return new IDocumentChange[0];
        final DocumentChangeMessage m = (DocumentChangeMessage) remoteChange;
        final List l = this.transformIncomingMessage(m);
        return (IDocumentChange[]) l.toArray(new IDocumentChange[] {});
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        IAdapterManager manager = Activator.getDefault().getAdapterManager();
        if (manager == null)
            return null;
        return manager.loadAdapter(this, adapter.getName());
    }
}
