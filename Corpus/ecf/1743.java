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
package org.eclipse.ecf.example.collab.share.io;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class EclipseFileTransfer extends FileTransferSharedObject implements FileTransferListener {

    private static final long serialVersionUID = -4496151870561737078L;

    FileSenderUI senderUI;

    ID sharedObjectID;

    protected File localFile = null;

    protected FileReceiverUI receiverUI = null;

    protected EclipseCollabSharedObject receiver = null;

    /**
	 * @since 2.0
	 */
    public  EclipseFileTransfer(FileSenderUI view, ID target, InputStream ins, FileTransferParams params, ID sharedObjectID) {
        super(target, ins, params);
        this.senderUI = view;
        this.progressListener = this;
        this.sharedObjectID = sharedObjectID;
    }

    protected void addRemoteParticipants(ID ids[]) {
        final ID groupID = getContext().getConnectedID();
        if (ids != null && participantIDs != null) {
            for (int i = 0; i < ids.length; i++) {
                if (groupID != null && groupID.equals(ids[i]))
                    continue;
                if (targetReceiver == null) {
                    if (!getHomeContainerID().equals(ids[i]))
                        participantIDs.add(ids[i]);
                } else {
                    if (targetReceiver.equals(ids[i]))
                        participantIDs.add(ids[i]);
                }
            }
        }
    }

    protected ReplicaSharedObjectDescription getReplicaDescription(ID remoteMember) {
        final HashMap map = new HashMap();
        map.put(ARGS_PROPERTY_NAME, new Object[] { transferParams, sharedObjectID });
        return new ReplicaSharedObjectDescription(getClass(), getID(), getConfig().getHomeContainerID(), map, getNextReplicateID());
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        super.init(config);
        final Map props = config.getProperties();
        final Object[] args = (Object[]) props.get(ARGS_PROPERTY_NAME);
        if (args != null && args.length == 2) {
            transferParams = (FileTransferParams) args[0];
            sharedObjectID = (ID) args[1];
            progressListener = this;
        }
        if (args != null && args.length == 5) {
            senderUI = (FileSenderUI) args[0];
            targetReceiver = (ID) args[1];
            setInputStream((InputStream) args[2]);
            transferParams = (FileTransferParams) args[3];
            sharedObjectID = (ID) args[4];
            progressListener = this;
        }
    }

    /**
	 * Client constructor
	 * 
	 * @param params
	 *            the file send parameters associated with this file delivery
	 * @param receiverID 
	 */
    public  EclipseFileTransfer(FileTransferParams params, ID receiverID) {
        super(params);
        this.progressListener = this;
        this.sharedObjectID = receiverID;
    }

    public  EclipseFileTransfer() {
        super();
    }

    public void sendStart(FileTransferSharedObject obj, long length, float rate) {
        if (senderUI != null)
            senderUI.sendStart(transferParams.getRemoteFile(), length, rate);
    }

    public void sendData(FileTransferSharedObject obj, int dataLength) {
        if (senderUI != null)
            senderUI.sendData(transferParams.getRemoteFile(), dataLength);
    }

    public void sendDone(FileTransferSharedObject obj, Exception e) {
        if (senderUI != null)
            senderUI.sendDone(transferParams.getRemoteFile(), e);
    }

    protected File createPath(EclipseCollabSharedObject sharedObject, boolean server, File file, long length, float rate) {
        final File downloadDir = new File(sharedObject.getLocalFullDownloadPath());
        // create directories if we need them
        downloadDir.mkdirs();
        // Then create new file
        final File retFile = new File(downloadDir, file.getName());
        return retFile;
    }

    public void receiveStart(FileTransferSharedObject obj, File aFile, long length, float rate) {
        final FileReceiver r = new FileReceiver(aFile, length, rate);
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                r.run();
            }
        });
    }

    public void receiveData(FileTransferSharedObject obj, int dataLength) {
        if (receiverUI != null)
            receiverUI.receiveData(getHomeContainerID(), localFile, dataLength);
    }

    public void receiveDone(FileTransferSharedObject obj, Exception e) {
        if (receiverUI != null)
            receiverUI.receiveDone(getHomeContainerID(), localFile, e);
    }

    protected boolean votingCompleted() throws SharedObjectAddAbortException {
        if (failedParticipants != null && failedParticipants.size() > 0) {
            // In this case, we're going to go ahead and continue anyway
            return true;
        // Abort!
        // If no problems, and the number of participants to here from is 0,
        // then we're done
        } else if (state == ISharedObjectContainerTransaction.VOTING && participantIDs.size() == 0) {
            // Success!
            return true;
        }
        // Else continue waiting
        return false;
    }

    private class FileReceiver implements Runnable {

        private File aFile = null;

        private final long length;

        private final float rate;

        public  FileReceiver(File aFile, long length, float rate) {
            this.aFile = aFile;
            this.length = length;
            this.rate = rate;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
        public void run() {
            if (ClientPlugin.getDefault().getPluginPreferences().getBoolean(ClientPlugin.PREF_CONFIRM_FILE_RECEIVE)) {
                final MessageDialog dialog = new MessageDialog(ClientPlugin.getDefault().getActiveShell(), Messages.EclipseFileTransfer_DIALOG_RECEIVE_CONF_TITLE, null, Messages.EclipseFileTransfer_DIALOG_RECEIVE_CONF_TEXT, MessageDialog.QUESTION, null, 0);
                dialog.setBlockOnOpen(true);
                final int response = dialog.open();
                if (response == MessageDialog.CANCEL)
                    return;
            }
            receiver = (EclipseCollabSharedObject) getContext().getSharedObjectManager().getSharedObject(sharedObjectID);
            receiverUI = receiver.getFileReceiverUI(EclipseFileTransfer.this, transferParams);
            localFile = createPath(receiver, getContext().isGroupManager(), aFile, length, rate);
            // Our superclass depends upon the transferParams.getRemoteFile()
            // call
            // to give a valid file.
            // We modify this to the new local file we've decided upon
            transferParams.setRemoteFile(localFile);
            if (receiverUI != null)
                receiverUI.receiveStart(getHomeContainerID(), localFile, length, rate);
        }
    }
}
