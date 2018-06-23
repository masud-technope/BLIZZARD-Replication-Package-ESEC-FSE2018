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
package org.eclipse.ecf.internal.provider.xmpp.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDoneEvent;
import org.eclipse.ecf.internal.provider.xmpp.XmppPlugin;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.osgi.util.NLS;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

public class XMPPOutgoingFileTransfer implements IOutgoingFileTransfer {

    private final ID sessionID;

    private final XMPPID remoteTarget;

    private final IFileTransferListener listener;

    private File localFile;

    private long fileSize;

    private final OutgoingFileTransfer outgoingFileTransfer;

    private Status status;

    private Exception exception;

    private int originalOutputRequestTimeout = -1;

    private boolean localCancelled = false;

    public  XMPPOutgoingFileTransfer(FileTransferManager manager, XMPPID remoteTarget, IFileTransferInfo fileTransferInfo, IFileTransferListener listener, int outgoingRequestTimeout) {
        this.remoteTarget = remoteTarget;
        this.listener = listener;
        this.sessionID = createSessionID();
        final String fullyQualifiedName = remoteTarget.getFQName();
        // Set request timeout if we have a new value
        if (outgoingRequestTimeout != -1) {
            originalOutputRequestTimeout = OutgoingFileTransfer.getResponseTimeout();
            OutgoingFileTransfer.setResponseTimeout(outgoingRequestTimeout);
        }
        outgoingFileTransfer = manager.createOutgoingFileTransfer(fullyQualifiedName);
    }

    private ID createSessionID() {
        try {
            return IDFactory.getDefault().createGUID();
        } catch (final IDCreateException e) {
            throw new NullPointerException("cannot create id for XMPPOutgoingFileTransfer");
        }
    }

    public synchronized ID getRemoteTargetID() {
        return remoteTarget;
    }

    public ID getID() {
        return sessionID;
    }

    private void fireTransferListenerEvent(IFileTransferEvent event) {
        listener.handleTransferEvent(event);
    }

    private void setStatus(Status s) {
        this.status = s;
    }

    private void setException(Exception e) {
        this.exception = e;
    }

    private Status getStatus() {
        return this.status;
    }

    private void setErrorStatus(Exception e) {
        setStatus(FileTransfer.Status.error);
        setException(e);
    }

    public synchronized void startSend(File localFile, String description) throws XMPPException {
        this.localFile = localFile;
        this.fileSize = localFile.length();
        setStatus(Status.initial);
        outgoingFileTransfer.sendFile(localFile, description);
        final Thread transferThread = new Thread(new Runnable() {

            public void run() {
                setStatus(outgoingFileTransfer.getStatus());
                boolean negotiation = true;
                try {
                    while (negotiation && !localCancelled) {
                        // check the state of the progress
                        try {
                            Thread.sleep(300);
                        } catch (final InterruptedException e) {
                            setErrorStatus(e);
                            return;
                        }
                        final Status s = outgoingFileTransfer.getStatus();
                        setStatus(s);
                        final boolean negotiated = getStatus().equals(Status.negotiated);
                        if (s.equals(Status.negotiated) || s.equals(Status.cancelled) || s.equals(Status.complete) || s.equals(Status.error) || s.equals(Status.refused)) {
                            fireTransferListenerEvent(new IOutgoingFileTransferResponseEvent() {

                                public boolean requestAccepted() {
                                    return negotiated;
                                }

                                public IOutgoingFileTransfer getSource() {
                                    return XMPPOutgoingFileTransfer.this;
                                }

                                public String toString() {
                                    final StringBuffer buf = new StringBuffer(//$NON-NLS-1$
                                    "OutgoingFileTransferResponseEvent[");
                                    //$NON-NLS-1$ //$NON-NLS-2$
                                    buf.append("requestAccepted=").append(requestAccepted()).append(//$NON-NLS-1$ //$NON-NLS-2$
                                    "]");
                                    return buf.toString();
                                }

                                public void setFileTransferJob(FileTransferJob job) {
                                // does nothing with this implementation
                                }
                            });
                            // And negotiation is over
                            negotiation = false;
                        }
                    }
                    if (localCancelled) {
                        setErrorStatus(new UserCancelledException("Transfer cancelled by sender"));
                        return;
                    }
                    outgoingFileTransfer.sendStream(new FileInputStream(XMPPOutgoingFileTransfer.this.localFile), XMPPOutgoingFileTransfer.this.localFile.getName(), fileSize, "Ein File");
                    setStatus(Status.complete);
                } catch (final Exception e) {
                    setStatus(FileTransfer.Status.error);
                    setException(e);
                } finally {
                    // Reset request timeout
                    if (originalOutputRequestTimeout != -1) {
                        OutgoingFileTransfer.setResponseTimeout(originalOutputRequestTimeout);
                    }
                    // Then notify that the sending is done
                    fireTransferListenerEvent(new IOutgoingFileTransferSendDoneEvent() {

                        public IOutgoingFileTransfer getSource() {
                            return XMPPOutgoingFileTransfer.this;
                        }

                        public String toString() {
                            final StringBuffer buf = new StringBuffer("IOutgoingFileTransferSendDoneEvent[");
                            buf.append(//$NON-NLS-1$
                            "isDone=" + //$NON-NLS-1$
                            getSource().isDone());
                            buf.append(";bytesSent=").append(//$NON-NLS-1$
                            getSource().getBytesSent());
                            //$NON-NLS-1$ //$NON-NLS-2$
                            buf.append(";exception=").append(getException()).append("]");
                            return buf.toString();
                        }
                    });
                }
            }
        }, //$NON-NLS-1$
        NLS.bind("XMPP send {0}", remoteTarget.toExternalForm()));
        transferThread.start();
    }

    public synchronized void cancel() {
        localCancelled = true;
    }

    public synchronized File getLocalFile() {
        return localFile;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.isInstance(this))
            return this;
        final IAdapterManager adapterManager = XmppPlugin.getDefault().getAdapterManager();
        return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
    }

    public long getBytesSent() {
        return outgoingFileTransfer.getBytesSent();
    }

    public Exception getException() {
        return exception;
    }

    public double getPercentComplete() {
        return (fileSize <= 0) ? 1.0 : (((double) outgoingFileTransfer.getAmountWritten()) / ((double) fileSize));
    }

    public boolean isDone() {
        return status == Status.cancelled || status == Status.error || status == Status.complete;
    }

    public ID getSessionID() {
        return sessionID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#getFileLength()
	 */
    public long getFileLength() {
        return fileSize;
    }
}
