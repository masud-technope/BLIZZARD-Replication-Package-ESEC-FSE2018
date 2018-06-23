/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Mustafa K. Isik - conflict resolution via operational transformations
 *    Marcelo Mayworm - Adding sync API dependence
 *    IBM Corporation - support for certain non-text editors
 *****************************************************************************/
package org.eclipse.ecf.docshare2;

import java.util.Collections;
import java.util.Map;
import org.eclipse.core.filebuffers.ISynchronizationContext;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.docshare2.messages.FileSystemDocumentChangeMessage;
import org.eclipse.ecf.docshare2.messages.SelectionMessage;
import org.eclipse.ecf.internal.docshare2.DocShareActivator;
import org.eclipse.ecf.internal.docshare2.Messages;
import org.eclipse.ecf.sync.*;
import org.eclipse.ecf.sync.doc.DocumentChangeMessage;
import org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.osgi.util.NLS;

public class DocumentShare {

    public static class SelectionReceiver {

        //$NON-NLS-1$
        private static final String SELECTION_ANNOTATION_ID = "org.eclipse.ecf.docshare2.annotations.RemoteSelection";

        //$NON-NLS-1$
        private static final String CURSOR_ANNOTATION_ID = "org.eclipse.ecf.docshare2.annotations.RemoteCursor";

        /**
		 * Annotation model of current document
		 */
        private IAnnotationModel annotationModel;

        /**
		 * Object to use as lock for changing in annotation model,
		 * <code>null</code> if no model is provided.
		 */
        private Object annotationModelLock;

        /**
		 * Annotation for remote selection in annotationModel
		 */
        private Annotation currentAnnotation;

        public void setAnnotationModel(IAnnotationModel annotationModel) {
            this.annotationModel = annotationModel;
            if (this.annotationModel != null) {
                if (this.annotationModel instanceof ISynchronizable) {
                    this.annotationModelLock = ((ISynchronizable) this.annotationModel).getLockObject();
                }
                if (this.annotationModelLock == null) {
                    this.annotationModelLock = this;
                }
            }
        }

        public void handleMessage(SelectionMessage remoteMsg) {
            if (this.annotationModelLock == null) {
                return;
            }
            final Position newPosition = new Position(remoteMsg.getOffset(), remoteMsg.getLength());
            final Annotation newAnnotation = new Annotation(newPosition.getLength() > 0 ? SELECTION_ANNOTATION_ID : CURSOR_ANNOTATION_ID, false, Messages.DocShare_RemoteSelection);
            synchronized (this.annotationModelLock) {
                if (this.annotationModel != null) {
                    // initial selection, create new
                    if (this.currentAnnotation == null) {
                        this.currentAnnotation = newAnnotation;
                        this.annotationModel.addAnnotation(newAnnotation, newPosition);
                        return;
                    }
                    // selection not changed, skip
                    if (this.currentAnnotation.getType() == newAnnotation.getType()) {
                        Position oldPosition = this.annotationModel.getPosition(this.currentAnnotation);
                        if (oldPosition == null || newPosition.equals(oldPosition)) {
                            return;
                        }
                    }
                    // selection changed, replace annotation
                    if (this.annotationModel instanceof IAnnotationModelExtension) {
                        Annotation[] oldAnnotations = new Annotation[] { this.currentAnnotation };
                        this.currentAnnotation = newAnnotation;
                        Map newAnnotations = Collections.singletonMap(newAnnotation, newPosition);
                        ((IAnnotationModelExtension) this.annotationModel).replaceAnnotations(oldAnnotations, newAnnotations);
                    } else {
                        this.annotationModel.removeAnnotation(this.currentAnnotation);
                        this.annotationModel.addAnnotation(newAnnotation, newPosition);
                    }
                }
            }
        }

        public void dispose() {
            if (this.annotationModelLock == null) {
                return;
            }
            synchronized (this.annotationModelLock) {
                if (this.annotationModel != null) {
                    if (this.currentAnnotation != null) {
                        this.annotationModel.removeAnnotation(this.currentAnnotation);
                        this.currentAnnotation = null;
                    }
                    this.annotationModel = null;
                }
            }
        }
    }

    private SelectionReceiver selectionReceiver;

    private IChannel channel;

    private IDocument document;

    private String path;

    private ID targetID;

    private boolean locked = false;

    private boolean locallyActive = false;

    private boolean remotelyActive = false;

    /**
	 * Strategy for maintaining consistency among session participants'
	 * documents.
	 */
    private IModelSynchronizationStrategy syncStrategy;

    /**
	 * Factory to returns the possible strategies
	 */
    private IDocumentSynchronizationStrategyFactory factory;

    /**
	 * Create a document sharing session instance.
	 */
    public  DocumentShare(IChannel channel, ID targetID, String path, IDocument document) {
        Assert.isNotNull(channel);
        this.channel = channel;
        this.targetID = targetID;
        this.path = path;
        this.document = document;
        factory = DocShareActivator.getDefault().getColaSynchronizationStrategyFactory();
        selectionReceiver = new SelectionReceiver();
        document.addDocumentListener(documentListener);
        //SYNC API. Create an instance of the synchronization strategy on the receiver
        syncStrategy = createSynchronizationStrategy(false);
        Assert.isNotNull(syncStrategy);
    }

    /**
	 * Create a document sharing session instance.
	 */
    public  DocumentShare(IChannel channel, ID targetID, String path, IDocument document, IAnnotationModel annotationModel) {
        this(channel, targetID, path, document);
        selectionReceiver.setAnnotationModel(annotationModel);
    }

    void lock() {
        locked = true;
    }

    void unlock() {
        locked = false;
    }

    boolean isLocked() {
        return locked;
    }

    /**
	 * The document listener is the listener for changes to the *local* copy of
	 * the IDocument. This listener is responsible for sending document update
	 * messages when notified.
	 */
    IDocumentListener documentListener = new IDocumentListener() {

        public void documentAboutToBeChanged(DocumentEvent event) {
        // nothing to do
        }

        // handling of LOCAL OPERATION application
        public void documentChanged(DocumentEvent event) {
            if (isLocked()) {
                return;
            }
            //$NON-NLS-1$
            Trace.trace(DocShareActivator.PLUGIN_ID, NLS.bind("{0}.documentChanged[{1}]", DocumentShare.this, event));
            // SYNC API.  Here is entry point usage of sync API.  When a local document is changed by an editor,
            // this method will be called and the following code executed.  This code registers a DocumentChange
            // with the local syncStrategy instance via syncStrategy.registerLocalChange(IModelChange).
            // Model change messages returned from the registerLocalChange call are then sent (via ECF datashare channel)
            // to remote participant.
            IModelChangeMessage changeMessages[] = registerLocalChange(new DocumentChangeMessage(event.getOffset(), event.getLength(), event.getText()));
            for (int i = 0; i < changeMessages.length; i++) {
                sendMessage(changeMessages[i]);
            }
        }
    };

    void sendMessage(IModelChangeMessage changeMessage) {
        try {
            channel.sendMessage(targetID, new FileSystemDocumentChangeMessage(path, changeMessage).serialize());
        } catch (ECFException e) {
            DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Could not send message to " + targetID, e));
        }
    }

    IModelChangeMessage[] registerLocalChange(IModelChange localChange) {
        return syncStrategy.registerLocalChange(localChange);
    }

    void connect(IAnnotationModel annotationModel) {
        selectionReceiver.setAnnotationModel(annotationModel);
    }

    void disconnect() {
        selectionReceiver.dispose();
    }

    void addDocumentListener() {
        document.addDocumentListener(documentListener);
    }

    void removeDocumentListener() {
        document.removeDocumentListener(documentListener);
    }

    void handleSelectionMessage(SelectionMessage message) {
        selectionReceiver.handleMessage(message);
    }

    /**
	 * This method called by the {@link #handleMessage(ID, byte[])} method if
	 * the type of the message received is an update message.
	 * 
	 * @param documentChangeMessage
	 *            the UpdateMessage received.
	 */
    protected synchronized void handleUpdateMessage(ISynchronizationContext context, final DocumentChangeMessage documentChangeMessage) {
        try {
            lock();
            // SYNC API.  Here a document change message has been received from remote via channel,
            // and is now passed to the syncStrategy for transformation.  The returned IModelChange[]
            // are then applied to the local document (after the synchronization strategy as transformed
            // them as necessary).
            IModelChange modelChanges[] = syncStrategy.transformRemoteChange(documentChangeMessage);
            final ModelUpdateException[] exception = new ModelUpdateException[1];
            for (int i = 0; i < modelChanges.length; i++) {
                if (exception[0] != null) {
                    DocShareActivator.getDefault().getLog().log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, IStatus.ERROR, Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE, exception[0]));
                    return;
                }
                final IModelChange modelChange = modelChanges[i];
                context.run(new Runnable() {

                    public void run() {
                        // to apply the change to a model of appropriate type
                        try {
                            modelChange.applyToModel(getDocument());
                        } catch (ModelUpdateException e) {
                            exception[0] = e;
                        }
                    }
                });
            }
        } finally {
            unlock();
        }
    }

    IDocument getDocument() {
        return document;
    }

    private IModelSynchronizationStrategy createSynchronizationStrategy(boolean isInitiator) {
        //Instantiate the service
        Assert.isNotNull(factory);
        return factory.createDocumentSynchronizationStrategy(channel.getID(), isInitiator);
    }

    /**
	 * Returns <code>true</code> if the document being backed is locally active, that is, it is being actively edited. For example, this could mean that there is an editor open for the backed document. 
	 * @return <code>true</code> if the backed document is being actively modified, <code>false</code> otherwise
	 */
    boolean isLocallyActive() {
        return locallyActive;
    }

    void setLocallyActive(boolean locallyActive) {
        this.locallyActive = locallyActive;
    }

    /**
	 * Returns <code>true</code> if the document being backed is remotely active, that is, it is being actively edited by a remote peer. For example, this could mean that a peer has an editor open up for the backed document. 
	 * @return <code>true</code> if the backed document is being actively modified remotely, <code>false</code> otherwise
	 */
    boolean isRemotelyActive() {
        return remotelyActive;
    }

    void setRemotelyActive(boolean remotelyActive) {
        this.remotelyActive = remotelyActive;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("DocumentShare[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("path=").append(path).append(";channel=").append(channel);
        //$NON-NLS-1$
        buf.append("targetID=").append(targetID);
        //$NON-NLS-1$
        buf.append(";strategy=").append(syncStrategy).append(']');
        return buf.toString();
    }
}
