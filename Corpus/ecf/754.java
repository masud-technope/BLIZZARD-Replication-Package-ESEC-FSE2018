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
package org.eclipse.ecf.docshare;

import org.eclipse.ecf.internal.docshare.Messages;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.events.IChannelDisconnectEvent;
import org.eclipse.ecf.docshare.messages.*;
import org.eclipse.ecf.internal.docshare.*;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.ecf.sync.*;
import org.eclipse.ecf.sync.doc.DocumentChangeMessage;
import org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Represents a document sharing session between two participants.
 * @since 2.1
 */
public class DocShare extends AbstractShare {

    public static class SelectionReceiver {

        //$NON-NLS-1$
        private static final String SELECTION_ANNOTATION_ID = "org.eclipse.ecf.docshare.annotations.RemoteSelection";

        //$NON-NLS-1$
        private static final String CURSOR_ANNOTATION_ID = "org.eclipse.ecf.docshare.annotations.RemoteCursor";

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

        public  SelectionReceiver(ITextEditor editor) {
            if (editor == null) {
                return;
            }
            IDocumentProvider documentProvider = editor.getDocumentProvider();
            if (documentProvider != null) {
                this.annotationModel = documentProvider.getAnnotationModel(editor.getEditorInput());
                if (this.annotationModel != null) {
                    if (this.annotationModel instanceof ISynchronizable) {
                        this.annotationModelLock = ((ISynchronizable) this.annotationModel).getLockObject();
                    }
                    if (this.annotationModelLock == null) {
                        this.annotationModelLock = this;
                    }
                }
            }
        }

        public void handleMessage(final SelectionMessage remoteMsg) {
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

    /**
	 * The ID of the initiator 
	 */
    private ID initiatorID;

    /**
	 * The ID of the receiver.
	 */
    private ID receiverID;

    /**
	 * Our ID
	 */
    private ID ourID;

    /**
	 * Text editor
	 */
    private ITextEditor editor;

    /**
	 * Content that we have received via start message, before user has
	 * responded to question about whether or not to display in editor. Should
	 * be null at all other times.
	 */
    String startContent = null;

    /**
	 * Object to use as lock for changing connected state of this docshare
	 * instance
	 */
    Object stateLock = new Object();

    /**
	 * Strategy for maintaining consistency among session participants'
	 * documents.
	 */
    IModelSynchronizationStrategy syncStrategy;

    /**
	 * Factory to returns the possible strategies
	 */
    IDocumentSynchronizationStrategyFactory factory;

    /**
	 * Handler for SelectionMessage (painting remote selection)
	 */
    SelectionReceiver selectionReceiver;

    /**
	 * Create a document sharing session instance.
	 * 
	 * @param adapter
	 *            the {@link IChannelContainerAdapter} to use to create this
	 *            document sharing session.
	 * @throws ECFException
	 *             if the channel cannot be created.
	 */
    public  DocShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
        factory = Activator.getDefault().getColaSynchronizationStrategyFactory();
    }

    IPartListener partListener = new IPartListener() {

        public void partActivated(IWorkbenchPart part) {
        // nothing to do
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        // nothing to do
        }

        public void partClosed(IWorkbenchPart part) {
            ITextEditor textEditor = getTextEditor();
            if (textEditor != null && part.equals(textEditor.getSite().getPart())) {
                stopShare();
            }
        }

        public void partDeactivated(IWorkbenchPart part) {
        // nothing to do
        }

        public void partOpened(IWorkbenchPart part) {
        // nothing to do
        }
    };

    IRosterManager rosterManager;

    IRosterListener rosterListener = new IRosterListener() {

        public void handleRosterEntryAdd(IRosterEntry entry) {
        // nothing to do
        }

        public void handleRosterEntryRemove(IRosterEntry entry) {
        // nothing to do
        }

        public void handleRosterUpdate(IRoster roster, IRosterItem changedValue) {
            if (changedValue instanceof IRosterEntry) {
                ID changedID = ((IRosterEntry) changedValue).getUser().getID();
                ID oID = null;
                ID otherID = null;
                Shell shell = null;
                synchronized (stateLock) {
                    oID = getOurID();
                    otherID = getOtherID();
                    IWorkbenchPartSite wps = getTextEditor().getSite();
                    shell = wps.getShell();
                }
                if (oID != null && changedID.equals(oID)) {
                    localStopShare();
                    showStopShareMessage(shell, Messages.DocShare_STOP_SHARED_EDITOR_US);
                } else if (otherID != null && changedID.equals(otherID)) {
                    localStopShare();
                    showStopShareMessage(shell, Messages.DocShare_STOP_SHARED_EDITOR_REMOTE);
                }
            }
        }
    };

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
            // If the channel is gone, then no reason to handle this.
            if (getChannel() == null || !Activator.getDefault().isListenerActive() || !isSharing()) {
                return;
            }
            // If the listener is not active, ignore input
            if (!Activator.getDefault().isListenerActive()) {
                // wish to echo this change.
                return;
            }
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, NLS.bind("{0}.documentChanged[{1}]", DocShare.this, event));
            // SYNC API.  Here is entry point usage of sync API.  When a local document is changed by an editor,
            // this method will be called and the following code executed.  This code registers a DocumentChange
            // with the local syncStrategy instance via syncStrategy.registerLocalChange(IModelChange).
            // Model change messages returned from the registerLocalChange call are then sent (via ECF datashare channel)
            // to remote participant.
            IModelChangeMessage changeMessages[] = syncStrategy.registerLocalChange(new DocumentChangeMessage(event.getOffset(), event.getLength(), event.getText()));
            for (int i = 0; i < changeMessages.length; i++) {
                try {
                    sendMessage(getOtherID(), changeMessages[i].serialize());
                } catch (final Exception e) {
                    logError(Messages.DocShare_EXCEPTION_SEND_MESSAGE, e);
                }
            }
        }
    };

    ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

        public void selectionChanged(final SelectionChangedEvent event) {
            // If the channel is gone, then no reason to handle this.
            if (getChannel() == null || !Activator.getDefault().isListenerActive()) {
                return;
            }
            // If the listener is not active, ignore input
            if (!Activator.getDefault().isListenerActive()) {
                // wish to echo this change.
                return;
            }
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, NLS.bind("{0}.selectionChanged[{1}]", DocShare.this, event));
            if (!(event.getSelection() instanceof ITextSelection)) {
                return;
            }
            final ITextSelection textSelection = (ITextSelection) event.getSelection();
            final SelectionMessage msg = new SelectionMessage(textSelection.getOffset(), textSelection.getLength());
            try {
                sendMessage(getOtherID(), msg.serialize());
            } catch (final Exception e) {
                logError(Messages.DocShare_EXCEPTION_SEND_MESSAGE, e);
            }
        }
    };

    /**
	 * Start sharing an editor's contents between two participants. This will
	 * send a request to start sharing with the target identified by the
	 * <code>toID</code> parameter. The remote receiver will be displayed a
	 * message dialog, and given the option to start editor sharing, or not.
	 * 
	 * @param our
	 *            the ID associated with the initiator. Must not be
	 *            <code>null</code>.
	 * @param fromName
	 *            a name to present to the receiver. If
	 *            <code>null, our.getName() will be used.
	 * @param toID the ID of the intended receiver.  Must not be <code>null</code>.
	 * @param fileName the file name of the file to be shared (with suffix type extension).  Must not be <code>null</code>.
	 * @param editorPart the text editor currently showing the contents of this editor.  Must not be <code>null</code>.
	 */
    public void startShare(final ID our, final String fromName, final ID toID, final String fileName, final ITextEditor editorPart) {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_ENTERING, DocShare.class, "startShare", new Object[] { our, fromName, toID, fileName, editorPart });
        Assert.isNotNull(our);
        final String fName = (fromName == null) ? our.getName() : fromName;
        Assert.isNotNull(toID);
        Assert.isNotNull(fName);
        Assert.isNotNull(editorPart);
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                try {
                    // SYNC API.  Create the synchronization strategy instance
                    syncStrategy = createSynchronizationStrategy(true);
                    Assert.isNotNull(syncStrategy);
                    // Get content from local document
                    final String content = editorPart.getDocumentProvider().getDocument(editorPart.getEditorInput()).get();
                    // Send start message with current content
                    sendMessage(toID, new StartMessage(our, fName, toID, content, fileName).serialize());
                    // Set local sharing start (to setup doc listener)
                    localStartShare(getLocalRosterManager(), our, our, toID, editorPart);
                } catch (final Exception e) {
                    logError(Messages.DocShare_ERROR_STARTING_EDITOR_TITLE, e);
                    showErrorToUser(Messages.DocShare_ERROR_STARTING_EDITOR_TITLE, NLS.bind(Messages.DocShare_ERROR_STARTING_EDITOR_MESSAGE, e.getLocalizedMessage()));
                }
            }
        });
        //$NON-NLS-1$
        Trace.exiting(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_ENTERING, DocShare.class, "startShare");
    }

    /**
	 * Stop editor sharing. Message only sent if we are currently engaged in an
	 * editor sharing session ({@link #isSharing()} returns <code>true</code>.
	 */
    public void stopShare() {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_ENTERING, this.getClass(), "stopShare");
        if (isSharing()) {
            // send stop message to other
            sendStopMessage();
            syncStrategy = null;
        }
        localStopShare();
        //$NON-NLS-1$
        Trace.exiting(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_EXITING, this.getClass(), "stopShare");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.AbstractShare#handleMessage(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
    protected void handleMessage(ID fromContainerID, byte[] data) {
        try {
            final IModelChangeMessage message = Message.deserialize(data);
            Assert.isNotNull(message);
            if (message instanceof DocumentChangeMessage) {
                handleUpdateMessage((DocumentChangeMessage) message);
            } else if (message instanceof SelectionMessage) {
                SelectionReceiver receiver = selectionReceiver;
                if (receiver != null) {
                    receiver.handleMessage((SelectionMessage) message);
                }
            } else if (message instanceof StartMessage) {
                handleStartMessage((StartMessage) message);
            } else if (message instanceof StopMessage) {
                handleStopMessage((StopMessage) message);
            } else {
                throw new InvalidObjectException(NLS.bind(Messages.DocShare_EXCEPTION_INVALID_MESSAGE, message.getClass().getName()));
            }
        } catch (final Exception e) {
            logError(Messages.DocShare_EXCEPTION_HANDLE_MESSAGE, e);
        }
    }

    /**
	 * This method called by the {@link #handleMessage(ID, byte[])} method if
	 * the type of the message received is a start message (sent by remote party
	 * via {@link #startShare(ID, String, ID, String, ITextEditor)}.
	 * 
	 * @param message
	 *            the UpdateMessage received.
	 * @throws IDCreateException 
	 */
    protected void handleStartMessage(final StartMessage message) throws IDCreateException {
        final ID senderID = message.getSenderID();
        Assert.isNotNull(senderID);
        final String senderUsername = message.getSenderUsername();
        Assert.isNotNull(senderUsername);
        final ID our = message.getReceiverID();
        Assert.isNotNull(our);
        final String filename = message.getFilename();
        Assert.isNotNull(filename);
        final String documentContent = message.getDocumentContent();
        Assert.isNotNull(documentContent);
        //SYNC API. Create an instance of the synchronization strategy on the receiver
        syncStrategy = createSynchronizationStrategy(false);
        Assert.isNotNull(syncStrategy);
        // First synchronize on any state changes by getting stateLock
        synchronized (stateLock) {
            // If we are already sharing, or have non-null start content
            if (isSharing() || startContent != null) {
                sendStopMessage(senderID);
                // And we're done
                return;
            }
            // Otherwise set start content to the message-provided
            // documentContent
            startContent = documentContent;
        }
        // Then open UI and show text editor if appropriate
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    // First, ask user if they want to receive the doc
                    if (openReceiverDialog(senderID, senderUsername, filename)) {
                        // If so, then we create a new DocShareEditorInput
                        final DocShareEditorInput dsei = new DocShareEditorInput(getTempFileStore(senderUsername, filename, startContent), senderUsername, filename);
                        // Then open up text editor
                        final ITextEditor ep;
                        IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(dsei, getEditorIdForFileName(filename));
                        // if it's not a text editor, offer it a chance to give us one
                        if (editorPart != null && !(editorPart instanceof ITextEditor))
                            ep = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
                        else
                            ep = (ITextEditor) editorPart;
                        // Then change our local state
                        localStartShare(getLocalRosterManager(), our, senderID, our, ep);
                    } else {
                        // Send stop message to initiator
                        sendStopMessage();
                        // Then we stop the local share
                        localStopShare();
                    }
                } catch (final Exception e) {
                    logError(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE, e);
                    showErrorToUser(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE, NLS.bind(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_MESSAGE, e.getLocalizedMessage()));
                }
            }
        });
    }

    IRosterManager getLocalRosterManager() {
        IContainer container = (IContainer) this.adapter.getAdapter(IContainer.class);
        if (container != null) {
            IPresenceContainerAdapter presenceContainerAdapter = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);
            if (presenceContainerAdapter != null) {
                return presenceContainerAdapter.getRosterManager();
            }
        }
        return null;
    }

    void modifyStartContent(int offset, int length, String text) {
        final StringBuffer result = new StringBuffer(startContent.substring(0, offset));
        result.append(text);
        result.append(startContent.substring(offset + length));
        startContent = result.toString();
    }

    /**
	 * This method called by the {@link #handleMessage(ID, byte[])} method if
	 * the type of the message received is an update message.
	 * 
	 * @param documentChangeMessage
	 *            the UpdateMessage received.
	 */
    protected void handleUpdateMessage(final DocumentChangeMessage documentChangeMessage) {
        synchronized (stateLock) {
            // directly
            if (startContent != null) {
                modifyStartContent(documentChangeMessage.getOffset(), documentChangeMessage.getLengthOfReplacedText(), documentChangeMessage.getText());
                // And we're done
                return;
            }
        }
        // Else replace in document directly
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    Trace.entering(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_ENTERING, this.getClass(), "handleUpdateMessage", documentChangeMessage//$NON-NLS-1$
                    );
                    final IDocument document = getDocumentFromEditor();
                    if (document != null) {
                        Trace.trace(Activator.PLUGIN_ID, //$NON-NLS-1$
                        NLS.bind(//$NON-NLS-1$
                        "{0}.handleUpdateMessage calling transformIncomingMessage", //$NON-NLS-1$
                        DocShare.this));
                        // SYNC API.  Here a document change message has been received from remote via channel,
                        // and is now passed to the syncStrategy for transformation.  The returned IModelChange[]
                        // are then applied to the local document (after the synchronization strategy as transformed
                        // them as necessary).
                        IModelChange modelChanges[] = syncStrategy.transformRemoteChange(documentChangeMessage);
                        // Make editor refuse input while we are applying changes
                        setEditorToRefuseInput();
                        for (int i = 0; i < modelChanges.length; i++) {
                            // Apply each change to a model.  Clients may use this method
                            // to apply the change to a model of appropriate type
                            modelChanges[i].applyToModel(document);
                        }
                    }
                } catch (final Exception e) {
                    logError(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE, e);
                    showErrorToUser(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE, NLS.bind(Messages.DocShare_EXCEPTION_RECEIVING_MESSAGE_MESSAGE, e.getLocalizedMessage()));
                } finally {
                    // Have editor accept input
                    setEditorToAcceptInput();
                    Trace.exiting(Activator.PLUGIN_ID, DocshareDebugOptions.METHODS_EXITING, this.getClass(), "handleUpdateMessage");
                }
            }
        });
    }

    /**
	 * @param message
	 */
    protected void handleStopMessage(StopMessage message) {
        if (isSharing()) {
            Shell s = editor.getSite().getShell();
            localStopShare();
            syncStrategy = null;
            showStopShareMessage(s, Messages.DocShare_REMOTE_USER_STOPPED);
        }
    }

    void setEditorToRefuseInput() {
        setEditorEditable(false);
        Activator.getDefault().setListenerActive(false);
    }

    void setEditorToAcceptInput() {
        setEditorEditable(true);
        Activator.getDefault().setListenerActive(true);
    }

    ITextEditor getEditor() {
        return editor;
    }

    IEditorInput getEditorInput() {
        synchronized (stateLock) {
            if (editor == null)
                return null;
            return editor.getEditorInput();
        }
    }

    boolean openReceiverDialog(ID fromID, String fromUsername, String fileName) {
        return MessageDialog.openQuestion(null, Messages.DocShare_EDITOR_SHARE_POPUP_TITLE, NLS.bind(Messages.DocShare_EDITOR_SHARE_POPUP_MESSAGE, fromUsername, fileName));
    }

    protected void handleDisconnectEvent(IChannelDisconnectEvent cde) {
        boolean weDisconnected = (ourID != null && ourID.equals(cde.getTargetID()));
        Shell shell = null;
        if (isSharing()) {
            shell = editor.getSite().getShell();
        }
        // Stop things and *then* notify user
        localStopShare();
        if (shell != null) {
            if (weDisconnected)
                showStopShareMessage(shell, Messages.DocShare_STOP_SHARED_EDITOR_US);
            else
                showStopShareMessage(shell, Messages.DocShare_STOP_SHARED_EDITOR_REMOTE);
        }
    }

    /**
	 * @param shell must not be <code>null</code>
	 * @param message message content for message dialog
	 */
    void showStopShareMessage(final Shell shell, final String message) {
        Display display = shell.getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                MessageDialog.openInformation(shell, Messages.DocShare_STOP_SHARED_EDITOR_TITLE, message);
            }
        });
    }

    IFileStore getTempFileStore(String fromUsername, String fileName, String content) throws IOException, CoreException {
        final IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(File.createTempFile(fromUsername, fileName));
        final OutputStream outs = fileStore.openOutputStream(EFS.OVERWRITE, null);
        outs.write(content.getBytes());
        outs.close();
        return fileStore;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.AbstractShare#dispose()
	 */
    public synchronized void dispose() {
        localStopShare();
        super.dispose();
    }

    void logError(IStatus status) {
        Activator.getDefault().getLog().log(status);
    }

    void showErrorToUser(String title, String message) {
        MessageDialog.openError(null, title, message);
    }

    void logError(String exceptionString, Throwable e) {
        Trace.catching(Activator.PLUGIN_ID, DocshareDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), exceptionString, e);
        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, exceptionString, e));
    }

    StyledText getTextControl() {
        synchronized (stateLock) {
            if (editor == null)
                return null;
            return (StyledText) editor.getAdapter(Control.class);
        }
    }

    void setEditorEditable(final boolean editable) {
        final StyledText textControl = getTextControl();
        if (textControl != null && !textControl.isDisposed()) {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    textControl.setEditable(editable);
                }
            });
        }
    }

    String getEditorIdForFileName(String fileName) {
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IEditorRegistry er = wb.getEditorRegistry();
        final IEditorDescriptor desc = er.getDefaultEditor(fileName);
        if (desc != null)
            return desc.getId();
        return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
    }

    IDocument getDocumentFromEditor() {
        synchronized (stateLock) {
            ITextEditor textEditor = getTextEditor();
            if (textEditor == null)
                return null;
            final IDocumentProvider documentProvider = textEditor.getDocumentProvider();
            if (documentProvider == null)
                return null;
            return documentProvider.getDocument(textEditor.getEditorInput());
        }
    }

    void localStartShare(final IRosterManager rm, ID our, ID initiator, ID receiver, ITextEditor edt) {
        synchronized (stateLock) {
            localStopShare();
            this.rosterManager = rm;
            if (this.rosterManager != null) {
                this.rosterManager.addRosterListener(rosterListener);
            }
            this.ourID = our;
            this.initiatorID = initiator;
            this.receiverID = receiver;
            this.editor = edt;
            this.editor.getSite().getPage().addPartListener(partListener);
            final IDocument doc = getDocumentFromEditor();
            if (doc != null)
                doc.addDocumentListener(documentListener);
            if (this.editor != null) {
                ISelectionProvider selectionProvider = this.editor.getSelectionProvider();
                if (selectionProvider instanceof IPostSelectionProvider) {
                    ((IPostSelectionProvider) selectionProvider).addPostSelectionChangedListener(selectionListener);
                }
                selectionReceiver = new SelectionReceiver(this.editor);
            }
        }
    }

    void localStopShare() {
        SelectionReceiver oldSelectionReceiver;
        synchronized (stateLock) {
            if (rosterManager != null)
                rosterManager.removeRosterListener(rosterListener);
            this.rosterManager = null;
            this.ourID = null;
            this.initiatorID = null;
            this.receiverID = null;
            this.startContent = null;
            final IDocument doc = getDocumentFromEditor();
            if (doc != null)
                doc.removeDocumentListener(documentListener);
            if (this.editor != null) {
                this.editor.getSite().getPage().removePartListener(partListener);
                ISelectionProvider selectionProvider = this.editor.getSelectionProvider();
                if (selectionProvider instanceof IPostSelectionProvider) {
                    ((IPostSelectionProvider) selectionProvider).removePostSelectionChangedListener(selectionListener);
                }
            }
            oldSelectionReceiver = this.selectionReceiver;
            this.selectionReceiver = null;
            this.editor = null;
        }
        if (oldSelectionReceiver != null) {
            oldSelectionReceiver.dispose();
        }
    }

    void sendStopMessage() {
        sendStopMessage(getOtherID());
    }

    void sendStopMessage(ID other) {
        if (isSharing()) {
            try {
                super.sendMessage(other, new StopMessage().serialize());
            } catch (final Exception e) {
                logError(Messages.DocShare_EXCEPTION_SEND_MESSAGE, e);
            }
        }
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("DocShare[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buf.append("ourID=").append(ourID).append(";initiatorID=").append(initiatorID).append(";receiverID=").append(receiverID);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";strategy=").append(syncStrategy).append("]");
        return buf.toString();
    }

    IModelSynchronizationStrategy createSynchronizationStrategy(boolean isInitiator) {
        //Instantiate the service
        Assert.isNotNull(factory);
        return factory.createDocumentSynchronizationStrategy(getChannel().getID(), isInitiator);
    }

    public ID getInitiatorID() {
        return initiatorID;
    }

    public ID getReceiverID() {
        return receiverID;
    }

    public ID getOurID() {
        return ourID;
    }

    public ITextEditor getTextEditor() {
        return this.editor;
    }

    public boolean isSharing() {
        synchronized (stateLock) {
            return (this.editor != null);
        }
    }

    public ID getOtherID() {
        synchronized (stateLock) {
            if (isInitiator())
                return receiverID;
            return initiatorID;
        }
    }

    public boolean isInitiator() {
        synchronized (stateLock) {
            if (ourID == null || initiatorID == null || receiverID == null)
                return false;
            return ourID.equals(initiatorID);
        }
    }
}
