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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.internal.provider.xmpp.Messages;
import org.eclipse.ecf.internal.provider.xmpp.XmppPlugin;

public class XMPPIncomingFileTransfer implements IIncomingFileTransfer {

    public static final int DEFAULT_BUF_LENGTH = 4096;

    protected int buff_length = DEFAULT_BUF_LENGTH;

    ID threadID = null;

    InputStream remoteFileContents = null;

    OutputStream localFileContents = null;

    IFileTransferListener listener = null;

    Exception exception;

    long bytesReceived = 0;

    long fileLength = -1;

    Job job = null;

    boolean done = false;

    String fileName;

    class FileTransferJob extends Job {

        public  FileTransferJob(String name) {
            super(name);
        }

        protected IStatus run(IProgressMonitor monitor) {
            final byte[] buf = new byte[buff_length];
            final int totalWork = ((fileLength == -1) ? 100 : (int) fileLength);
            monitor.beginTask(getID().getName() + Messages.XMPPIncomingFileTransfer_Progress_Data, totalWork);
            try {
                while (!isDone()) {
                    if (monitor.isCanceled())
                        throw new UserCancelledException(Messages.XMPPIncomingFileTransfer_Exception_User_Cancelled);
                    final int bytes = remoteFileContents.read(buf);
                    if (bytes != -1) {
                        bytesReceived += bytes;
                        localFileContents.write(buf, 0, bytes);
                        fireTransferReceiveDataEvent();
                        monitor.worked(bytes);
                    } else {
                        done = true;
                    }
                }
            } catch (final Exception e) {
                exception = e;
                done = true;
            } finally {
                hardClose();
                monitor.done();
                fireTransferReceiveDoneEvent();
            }
            return getFinalStatus(exception);
        }
    }

    protected IStatus getFinalStatus(Throwable exception) {
        return (exception == null) ? new Status(IStatus.OK, XmppPlugin.PLUGIN_ID, 0, Messages.XMPPIncomingFileTransfer_Status_Transfer_Completed_OK, null) : new Status(IStatus.ERROR, XmppPlugin.PLUGIN_ID, IStatus.ERROR, Messages.XMPPIncomingFileTransfer_Status_Transfer_Exception, exception);
    }

    protected void hardClose() {
        try {
            if (remoteFileContents != null) {
                remoteFileContents.close();
            }
        } catch (final IOException e) {
        }
        job = null;
        remoteFileContents = null;
        localFileContents = null;
    }

    protected void fireTransferReceiveDoneEvent() {
        if (listener != null)
            listener.handleTransferEvent(new IIncomingFileTransferReceiveDoneEvent() {

                public IIncomingFileTransfer getSource() {
                    return XMPPIncomingFileTransfer.this;
                }

                public Exception getException() {
                    return XMPPIncomingFileTransfer.this.getException();
                }

                public String toString() {
                    final StringBuffer sb = new StringBuffer("IIncomingFileTransferReceiveDoneEvent[");
                    //$NON-NLS-1$ //$NON-NLS-2$
                    sb.append("isDone=").append(done).append(";");
                    //$NON-NLS-1$
                    sb.append("bytesReceived=").append(//$NON-NLS-1$
                    bytesReceived).append(//$NON-NLS-1$
                    "]");
                    return sb.toString();
                }
            });
    }

    protected void fireTransferReceiveDataEvent() {
        if (listener != null)
            listener.handleTransferEvent(new IIncomingFileTransferReceiveDataEvent() {

                public IIncomingFileTransfer getSource() {
                    return XMPPIncomingFileTransfer.this;
                }

                public String toString() {
                    final StringBuffer sb = new StringBuffer("IIncomingFileTransferReceiveDataEvent[");
                    //$NON-NLS-1$ //$NON-NLS-2$
                    sb.append("isDone=").append(done).append(";");
                    //$NON-NLS-1$
                    sb.append("bytesReceived=").append(//$NON-NLS-1$
                    bytesReceived).append(//$NON-NLS-1$
                    ";");
                    //$NON-NLS-1$
                    sb.append("percentComplete=").append(//$NON-NLS-1$
                    getPercentComplete() * 100).append("]");
                    return sb.toString();
                }
            });
    }

    /**
	 * @param threadID
	 * @param fileName
	 * @param inputStream
	 * @param outputStream
	 * @param fileSize
	 * @param listener
	 */
    public  XMPPIncomingFileTransfer(ID threadID, String fileName, InputStream inputStream, OutputStream outputStream, long fileSize, IFileTransferListener listener) {
        this.threadID = threadID;
        this.fileName = fileName;
        this.remoteFileContents = inputStream;
        this.localFileContents = outputStream;
        this.fileLength = fileSize;
        this.listener = listener;
        this.job = new FileTransferJob(threadID.getName());
        this.job.schedule();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getBytesReceived()
	 */
    public long getBytesReceived() {
        return bytesReceived;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getListener()
	 */
    public IFileTransferListener getListener() {
        return listener;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#cancel()
	 */
    public void cancel() {
        if (job != null)
            job.cancel();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#getException()
	 */
    public Exception getException() {
        return exception;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#getPercentComplete()
	 */
    public double getPercentComplete() {
        return ((double) bytesReceived / (double) fileLength);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#isDone()
	 */
    public boolean isDone() {
        return done;
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return threadID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getFileRangeSpecification
	 * ()
	 */
    public IFileRangeSpecification getFileRangeSpecification() {
        return null;
    }

    public long getFileLength() {
        return fileLength;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getRemoteFileName()
	 */
    public String getRemoteFileName() {
        return fileName;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getRemoteLastModified
	 * ()
	 */
    public Date getRemoteLastModified() {
        // Not supported
        return null;
    }
}
