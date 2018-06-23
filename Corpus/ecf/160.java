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

import java.io.*;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.example.collab.share.SharedObjectMsg;
import org.eclipse.ecf.example.collab.share.TransactionSharedObject;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.osgi.util.NLS;

public class FileTransferSharedObject extends TransactionSharedObject {

    public static final int DEFAULT_START_WAIT_INTERVAL = 5000;

    //$NON-NLS-1$
    private static final String HANDLEDATA_MSG = "handleData";

    //$NON-NLS-1$
    private static final String HANDLEDONE_MSG = "handleDone";

    //$NON-NLS-1$
    private static final String STARTSENDTOALL_MSG = "startSendToAll";

    //$NON-NLS-1$
    private static final String START_MSG = "start";

    // Both host and container
    protected FileTransferParams transferParams;

    protected FileTransferListener progressListener;

    // Host only
    protected ID targetReceiver;

    protected InputStream inputStream;

    // Client only
    protected OutputStream outputStream;

    protected long dataWritten = -1;

    public  FileTransferSharedObject(InputStream ins, FileTransferParams params) {
        this(null, ins, params);
    }

    public  FileTransferSharedObject(InputStream ins) {
        this(null, ins, null);
    }

    public  FileTransferSharedObject(FileTransferParams params) {
        if (params == null) {
            transferParams = new FileTransferParams();
        } else
            transferParams = params;
        progressListener = transferParams.getProgressListener();
    }

    public  FileTransferSharedObject(ID receiver, InputStream ins, FileTransferParams params) {
        targetReceiver = receiver;
        Assert.isNotNull(ins);
        if (ins == null)
            throw new NullPointerException(Messages.FileTransferSharedObject_EXCEPTION_INPUTSTREAM_NOT_NULL);
        setInputStream(ins);
        transferParams = (params == null) ? new FileTransferParams() : params;
        progressListener = transferParams.getProgressListener();
    }

    public  FileTransferSharedObject() {
        super();
    }

    protected void setInputStream(InputStream src) {
        inputStream = src;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.example.collab.share.TransactionSharedObject#addRemoteParticipants(org.eclipse.ecf.core.identity.ID[])
	 */
    protected void addRemoteParticipants(ID ids[]) {
        if (ids != null && participantIDs != null) {
            for (int i = 0; i < ids.length; i++) {
                if (targetReceiver == null) {
                    if (!getHomeContainerID().equals(ids[i]))
                        participantIDs.addElement(ids[i]);
                } else {
                    if (targetReceiver.equals(ids[i]))
                        participantIDs.addElement(ids[i]);
                }
            }
        }
    }

    protected void setOutputStream(OutputStream src) {
        outputStream = src;
    }

    public void activated(ID[] others) {
        try {
            // Only try to open output file if this is not the host instance
            if (!isHost() && !getContext().isGroupManager()) {
                // Then notify listener about starting the receive
                if (progressListener != null)
                    progressListener.receiveStart(this, transferParams.getRemoteFile(), transferParams.getLength(), transferParams.getRate());
                // Open output file
                openOutputFile();
                if (transferParams.getLength() != -1)
                    dataWritten = 0;
            } else {
                // Just notify listener (if any) about the sending
                if (progressListener != null)
                    progressListener.sendStart(this, transferParams.getLength(), transferParams.getRate());
            }
        } catch (final Exception e) {
            try {
                getContext().sendCreateResponse(getHomeContainerID(), e, getNextReplicateID());
            } catch (final Exception e1) {
                log("Exception sending failure back to host", e1);
            }
            return;
        }
        // Finally, call activated to report success
        super.activated(others);
    }

    protected void openOutputFile() throws IOException {
        final File aFile = transferParams.getRemoteFile();
        if (aFile == null)
            throw new IOException(Messages.FileTransferSharedObject_EXCEPTION_REMOTE_FILE_NOT_NULL);
        // then we skip the file creation totally
        if (getContext().isGroupManager() && !transferParams.getIncludeServer()) {
            // Set myFile to null and outputStream to null
            setOutputStream(null);
        } else {
            try {
                final String parent = aFile.getParent();
                if (parent != null) {
                    new File(parent).mkdirs();
                }
            } catch (final Exception ex) {
                log(NLS.bind("Exception creating local directory for ", aFile), ex);
            }
            setOutputStream(new BufferedOutputStream(new FileOutputStream(aFile)));
        }
    }

    protected void replicate(ID remoteMember) {
        // handle replication.
        if (targetReceiver == null) {
            super.replicate(remoteMember);
            return;
        } else // if we're replicating on activation
        if (remoteMember == null) {
            try {
                final ReplicaSharedObjectDescription createInfo = getReplicaDescription(targetReceiver);
                if (createInfo != null) {
                    getContext().sendCreate(targetReceiver, createInfo);
                    return;
                }
            } catch (final IOException e) {
                log("Could not send createFail message", e);
            }
        }
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        super.init(config);
        final Map map = config.getProperties();
        final Object[] args = (Object[]) map.get(ARGS_PROPERTY_NAME);
        if (args != null && args.length == 1) {
            transferParams = (FileTransferParams) args[0];
            progressListener = transferParams.getProgressListener();
        }
    }

    protected ReplicaSharedObjectDescription getReplicaDescription(ID remoteMember) {
        final HashMap map = new HashMap();
        map.put(ARGS_PROPERTY_NAME, new Object[] { transferParams });
        return new ReplicaSharedObjectDescription(getClass(), getID(), getConfig().getHomeContainerID(), map, getNextReplicateID());
    }

    protected boolean sendData(ID rcvr, FileData data) throws IOException {
        // Send it. This does all data delivery.
        forwardMsgTo(rcvr, SharedObjectMsg.createMsg(HANDLEDATA_MSG, data));
        return data.isDone();
    }

    protected boolean sendChunk(ID rcvr) throws IOException {
        final FileData data = new FileData(inputStream, transferParams.getChunkSize());
        final int size = data.getDataSize();
        if (progressListener != null && size != -1)
            progressListener.sendData(this, size);
        return sendData(rcvr, data);
    }

    protected void handleData(FileData data) {
        preSaveData(data);
        // Then save the file data.
        final int size = data.getDataSize();
        if (progressListener != null && size != -1)
            progressListener.receiveData(this, size);
        saveData(data);
    }

    protected void preSaveData(FileData data) {
    }

    /**
	 * Save data to File. This method is called by handleData to actually save
	 * data received (on the clients only) to the appropriate file.
	 * 
	 * @param data
	 *            the FileData to save
	 */
    protected void saveData(FileData data) {
        // Save data locally...if we have an output stream
        try {
            if (outputStream != null) {
                final long len = transferParams.getLength();
                dataWritten += data.getDataSize();
                if (len != -1 && dataWritten > len)
                    throw new IOException(NLS.bind(Messages.FileTransferSharedObject_EXCEPTION_FILE_LARGER_THAN_LEN, String.valueOf(len)));
                data.saveData(outputStream);
                // Flush to verify that data was saved.
                outputStream.flush();
            }
        } catch (final Exception e) {
            notifyExceptionOnSave(e);
            try {
                forwardMsgHome(SharedObjectMsg.createMsg(HANDLEDONE_MSG, e));
                hardClose();
            } catch (final Exception e1) {
                log("Exception sending done msg back to host", e1);
            }
            if (progressListener != null)
                progressListener.receiveDone(this, e);
            return;
        }
        Exception except = null;
        // and report success.
        if (data.isDone()) {
            try {
                // Make sure everything is cleaned up
                hardClose();
            } catch (final Exception e1) {
                except = e1;
                notifyExceptionOnClose(except);
            }
            // object
            if (progressListener != null)
                progressListener.receiveDone(this, except);
            try {
                forwardMsgHome(SharedObjectMsg.createMsg(HANDLEDONE_MSG, except));
            } catch (final Exception e) {
                log("Exception sending done message home", e);
            }
            // Now call doneReceiving...which may destroy us
            doneReceiving();
        }
    }

    protected void notifyExceptionOnSave(Throwable t) {
    // By default, do nothing
    }

    protected void notifyExceptionOnClose(Throwable t) {
    // By default, do nothing
    }

    public void doneReceiving() {
    }

    /**
	 * Handler for done msg. NOTE: If this method name is changed, then the
	 * static variable 'HANDLEDONE_MSG' should be changed to match.
	 * 
	 * @param e
	 *            the Exception involved in the failure
	 */
    public void handleDone(Exception e) {
    }

    protected void preStartWaiting() {
    }

    protected void preStartSending() {
    }

    protected void preChunkSent() {
    }

    protected void chunkSent() {
    }

    protected void doneSending(Exception e) {
        if (progressListener != null)
            progressListener.sendDone(this, e);
    }

    protected void committed() {
        preStartWaiting();
        start();
    }

    protected void preWait() {
    }

    protected void start() {
        if (isHost()) {
            final Date start = transferParams.getStartDate();
            if (start != null && start.after(new Date())) {
                try {
                    preWait();
                    synchronized (this) {
                        wait(DEFAULT_START_WAIT_INTERVAL);
                    }
                    // Asynchronous tail recursion.
                    sendSelf(SharedObjectMsg.createMsg(START_MSG));
                } catch (final Exception e) {
                }
            } else {
                preStartSending();
                // Actually begin
                startSendToAll();
            }
        }
    }

    /**
	 * Start sending data to all clients. This is the entry point method for
	 * sending the desired data to remotes.
	 */
    protected void startSendToAll() {
        // Send chunks to all remotes until done.
        try {
            // Call subclass overrideable method before sending chunk
            preChunkSent();
            // Send chunk
            final boolean res = sendChunk(targetReceiver);
            // Call subclass overrideable method after sending chunk
            chunkSent();
            if (!res) {
                synchronized (this) {
                    int waittime = transferParams.getWaitTime();
                    if (waittime <= 0)
                        waittime = 10;
                    wait(waittime);
                }
                // If all data not sent, send message to self. This results
                // in this method iterating until entire file is sent.
                sendSelf(SharedObjectMsg.createMsg(STARTSENDTOALL_MSG));
            } else {
                // Close input stream.
                hardClose();
                doneSending(null);
            }
        } catch (final Exception e) {
            doneSending(e);
        }
    }

    /**
	 * @throws IOException  
	 */
    protected void hardClose() throws IOException {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (final Exception e) {
            }
            inputStream = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (final Exception e) {
            }
            outputStream = null;
        }
    }

    public void deactivated() {
        super.deactivated();
        // Make sure things are cleaned up properly in case of wrong trousers
        try {
            hardClose();
        } catch (final Exception e) {
        }
    }
}
