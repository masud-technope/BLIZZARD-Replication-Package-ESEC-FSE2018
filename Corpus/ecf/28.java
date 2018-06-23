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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.filebuffers.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.docshare2.messages.*;
import org.eclipse.ecf.internal.docshare2.DocShareActivator;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.SerializationException;
import org.eclipse.ecf.sync.doc.DocumentChangeMessage;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;

public class DocShare extends AbstractShare {

    public static final ISynchronizationContext DEFAULT_CONTEXT = new ISynchronizationContext() {

        public void run(Runnable runnable) {
            runnable.run();
        }
    };

    private ITextFileBufferManager manager = ITextFileBufferManager.DEFAULT;

    /**
	 * A map of all the documents that are currently shared, keyed based on their workspace-relative paths.
	 */
    private Map sharedDocuments;

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
        sharedDocuments = new HashMap();
    }

    public synchronized void lock(String[] paths) {
        // only lock files that needs to be locked
        for (int i = 0; i < paths.length; i++) {
            DocumentShare share = (DocumentShare) sharedDocuments.get(paths[i]);
            if (share != null) {
                share.lock();
            }
        }
    }

    public synchronized void unlock(String[] paths) {
        for (int i = 0; i < paths.length; i++) {
            DocumentShare share = (DocumentShare) sharedDocuments.get(paths[i]);
            if (share != null) {
                // revert the content, we lock to change the underlying file, now that we're done, we should revert so that the document matches the underlying the file
                revert(paths[i], manager.getTextFileBuffer(new Path(paths[i]), LocationKind.IFILE));
                share.unlock();
            }
        }
    //		for (Iterator it = sharedDocuments.entrySet().iterator(); it.hasNext();) {
    //			Map.Entry entry = (Map.Entry) it.next();
    //			String path = (String) entry.getKey();
    //			DocumentShare share = (DocumentShare) entry.getValue();
    //			share.isLocallyActive();
    //			revert(path, manager.getTextFileBuffer(new Path(path), LocationKind.IFILE));
    //			share.isLocallyActive();
    //			share.unlock();
    //		}
    }

    public void startSharing(ID localID, ID targetID, String path, IAnnotationModel annotationModel) throws CoreException, ECFException {
        DocumentShare share = (DocumentShare) sharedDocuments.get(path);
        IDocument document = connect(new Path(path), LocationKind.IFILE);
        if (share == null) {
            share = new DocumentShare(getChannel(), targetID, path, document, annotationModel);
            sharedDocuments.put(path, share);
        } else {
            // we're starting to share a file the remote peer already had up, hook up our own listeners
            share.connect(annotationModel);
            share.addDocumentListener();
        }
        // starting to share this file, locally active
        share.setLocallyActive(true);
        sendStartMessage(localID, targetID, document.get(), path);
    }

    private void sendStartMessage(ID localID, ID targetID, String content, String path) throws ECFException {
        StartMessage message = new StartMessage(localID, content, path);
        sendMessage(targetID, message.serialize());
    }

    public void sendSelection(ID targetID, String path, int offset, int length) throws ECFException {
        DocumentShare share = (DocumentShare) sharedDocuments.get(path);
        // only send selection messages if the other side is active, no point otherwise as the selection isn't shown anyway
        if (share.isRemotelyActive()) {
            sendMessage(targetID, new SelectionMessage(path, offset, length).serialize());
        }
    }

    /**
	 * Stops sharing the document and the specified path and notify the target of this.
	 * @param targetID the target to notify that the document at the path is no longer being shared
	 * @param path the path to the document, must not be <code>null</code>
	 * @throws ECFException
	 */
    public void stopSharing(ID targetID, String path) throws ECFException {
        DocumentShare share = (DocumentShare) sharedDocuments.get(path);
        if (share.isRemotelyActive()) {
            share.setLocallyActive(false);
        } else {
            sharedDocuments.remove(path);
            share.removeDocumentListener();
            share.disconnect();
            disconnect(path);
        }
        sendStopMessage(targetID, path);
    }

    private void sendStopMessage(ID targetID, String path) throws ECFException {
        sendMessage(targetID, new StopMessage(path).serialize());
    }

    protected void handleMessage(ID fromContainerID, byte[] data) {
        try {
            IModelChangeMessage message = Message.deserialize(data);
            Assert.isNotNull(message);
            if (message instanceof SelectionMessage) {
                handleSelectionMessage((SelectionMessage) message);
            } else if (message instanceof FileSystemDocumentChangeMessage) {
                handleFileSystemDocumentChangeMessage((FileSystemDocumentChangeMessage) message);
            } else if (message instanceof StartMessage) {
                handleStartMessage((StartMessage) message);
            } else if (message instanceof StopMessage) {
                handleStopMessage((StopMessage) message);
            }
        } catch (SerializationException e) {
            DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Could not deserialize message from " + fromContainerID, e));
        } catch (CoreException e) {
            DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Could not connect to file buffer", e));
        } catch (RuntimeException e) {
            DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Runtime exception has occurred while handling message from " + fromContainerID, e));
        }
    }

    /**
	 * Reverts the content of the file buffer back to what's on the underlying file. 
	 * @param path the path of the file
	 * @param buffer the buffer to revert
	 */
    private void revert(String path, final IFileBuffer buffer) {
        // revert within a synchronization context, this is important because an editor may be open on the buffer, in that case, the revert must be done on a UI thread
        getSynchronizationContext(path).run(new Runnable() {

            public void run() {
                try {
                    buffer.revert(null);
                } catch (CoreException e) {
                    DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Could not connect to revert buffer for " + buffer.getLocation(), e));
                }
            }
        });
    }

    protected ISynchronizationContext getSynchronizationContext(String path) {
        return DEFAULT_CONTEXT;
    }

    private void handleFileSystemDocumentChangeMessage(FileSystemDocumentChangeMessage message) {
        String path = message.getPath();
        DocumentShare share = (DocumentShare) sharedDocuments.get(path);
        if (share != null) {
            try {
                documentAboutToBeChanged(path);
                share.handleUpdateMessage(getSynchronizationContext(path), (DocumentChangeMessage) message.getMessage());
            } finally {
                documentChanged(path);
            }
        }
    }

    /**
	 * Method that will be called prior to a document being modified by the changes of a remote peer. This is used for performing any preparation work that needs to be invoked prior to the document being modified.
	 * 
	 * @param path the path of the document that will be modified
	 */
    protected void documentAboutToBeChanged(String path) {
    // subclasses to override
    }

    /**
	 * Method that will be called after a document has been modified by a remote peer's changes. Note that the document may not actually have changed.
	 * 
	 * @param path the path of the document that has been modified
	 */
    protected void documentChanged(String path) {
    // subclasses to override
    }

    protected void handleStartMessage(StartMessage message) throws CoreException {
        String sentPath = message.getPath();
        DocumentShare share = (DocumentShare) sharedDocuments.get(sentPath);
        if (share == null) {
            IPath path = new Path(message.getPath());
            IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            IFile file = workspaceRoot.getFile(path);
            // FIXME: if the file doesn't exist shouldn't we be creating it?
            LocationKind kind = file.exists() ? LocationKind.IFILE : LocationKind.LOCATION;
            IDocument document = connect(path, kind);
            share = new DocumentShare(getChannel(), message.getPeerID(), message.getPath(), document);
            sharedDocuments.put(message.getPath(), share);
        }
        share.setRemotelyActive(true);
    }

    private void handleSelectionMessage(final SelectionMessage message) {
        final DocumentShare share = (DocumentShare) sharedDocuments.get(message.getPath());
        if (share != null) {
            getSynchronizationContext(message.getPath()).run(new Runnable() {

                public void run() {
                    share.handleSelectionMessage(message);
                }
            });
        }
    }

    protected void handleStopMessage(StopMessage message) {
        String path = message.getPath();
        DocumentShare share = (DocumentShare) sharedDocuments.get(path);
        if (share != null) {
            // revert the content, this ensures that unsaved changes are discarded on the other end
            // TODO: does this make sense?
            revert(path, manager.getTextFileBuffer(new Path(path), LocationKind.IFILE));
            if (share.isLocallyActive()) {
                // if it's still active locally, just note that it's not remotely active
                share.setRemotelyActive(false);
            } else {
                // not active anywhere, remove it completely
                sharedDocuments.remove(path);
                // perform clean-up
                share.removeDocumentListener();
                share.disconnect();
                disconnect(path);
            }
        }
    }

    private IDocument connect(IPath path, LocationKind kind) throws CoreException {
        manager.connect(path, kind, null);
        return manager.getTextFileBuffer(path, LocationKind.IFILE).getDocument();
    }

    private void disconnect(String path) {
        try {
            manager.disconnect(new Path(path), LocationKind.IFILE, null);
        } catch (CoreException e) {
            DocShareActivator.log(new Status(IStatus.ERROR, DocShareActivator.PLUGIN_ID, "Could not disconnect file buffer for path: " + path, e));
        }
    }
}
