/*******************************************************************************
 * Copyright (c) 2004, 2010 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 *               Cloudsmith, Inc. - additional API and implementation
 *               Henrich Kraemer - bug 295030, Update Manager doesn't work with SOCKS proxy  
 ******************************************************************************/
package org.eclipse.ecf.provider.filetransfer.outgoing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.FileTransferInfo;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IFileTransferRunnable;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDataEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDoneEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.ISendFileTransfer;
import org.eclipse.ecf.internal.provider.filetransfer.Activator;
import org.eclipse.ecf.internal.provider.filetransfer.Messages;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferNamespace;
import org.eclipse.ecf.provider.filetransfer.util.ProxySetupHelper;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public abstract class AbstractOutgoingFileTransfer implements IOutgoingFileTransfer, ISendFileTransfer {

    public static final int DEFAULT_BUF_LENGTH = 4096;

    protected Job job;

    protected URL remoteFileURL;

    protected IFileID remoteFileID;

    protected IFileTransferListener listener;

    protected int buff_length = DEFAULT_BUF_LENGTH;

    protected boolean done = false;

    protected long bytesSent = 0;

    protected InputStream localFileContents;

    protected OutputStream remoteFileContents;

    protected Exception exception;

    protected IFileTransferInfo fileTransferInfo;

    protected Map options = null;

    protected IConnectContext connectContext;

    protected Proxy proxy;

    private final IFileTransferRunnable fileTransferRunnable = new IFileTransferRunnable() {

        public IStatus performFileTransfer(IProgressMonitor monitor) {
            final byte[] buf = new byte[buff_length];
            final long totalWork = ((fileTransferInfo.getFileSize() == -1) ? 100 : fileTransferInfo.getFileSize());
            double factor = (totalWork > Integer.MAX_VALUE) ? (((double) Integer.MAX_VALUE) / ((double) totalWork)) : 1.0;
            int work = (totalWork > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) totalWork;
            monitor.beginTask(getRemoteFileURL().toString() + Messages.AbstractOutgoingFileTransfer_Progress_Data, work);
            try {
                while (!isDone()) {
                    if (monitor.isCanceled())
                        throw new UserCancelledException(Messages.AbstractOutgoingFileTransfer_Exception_User_Cancelled);
                    final int bytes = localFileContents.read(buf);
                    if (bytes != -1) {
                        bytesSent += bytes;
                        remoteFileContents.write(buf, 0, bytes);
                        fireTransferSendDataEvent();
                        monitor.worked((int) Math.round(factor * bytes));
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
                try {
                    fireTransferSendDoneEvent();
                } catch (Exception e) {
                    Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.AbstractOutgoingFileTransfer_EXCEPTION_IN_FINALLY, e));
                }
            }
            return getFinalStatus(exception);
        }
    };

    FileTransferJob fileTransferJob;

    protected URL getRemoteFileURL() {
        return remoteFileURL;
    }

    protected void setInputStream(InputStream ins) {
        localFileContents = ins;
    }

    protected void setOutputStream(OutputStream outs) {
        remoteFileContents = outs;
    }

    protected IFileTransferInfo getFileTransferInfo() {
        return fileTransferInfo;
    }

    protected Map getOptions() {
        return options;
    }

    public  AbstractOutgoingFileTransfer() {
    //
    }

    protected IStatus getFinalStatus(Throwable exception1) {
        return Status.OK_STATUS;
    }

    protected void hardClose() {
        try {
            if (remoteFileContents != null)
                remoteFileContents.close();
        } catch (final IOException e) {
            Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "hardClose", e));
        }
        try {
            if (localFileContents != null)
                localFileContents.close();
        } catch (final IOException e) {
            Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "hardClose", e));
        }
        job = null;
        remoteFileContents = null;
        localFileContents = null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return remoteFileID;
    }

    protected void fireTransferSendDoneEvent() {
        listener.handleTransferEvent(new IOutgoingFileTransferSendDoneEvent() {

            public IOutgoingFileTransfer getSource() {
                return AbstractOutgoingFileTransfer.this;
            }

            public Exception getException() {
                return AbstractOutgoingFileTransfer.this.getException();
            }

            public String toString() {
                final StringBuffer sb = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "IOutgoingFileTransferSendDoneEvent[");
                //$NON-NLS-1$
                sb.append("bytesSent=").append(//$NON-NLS-1$
                bytesSent).append(";fileLength=").append(fileTransferInfo.getFileSize()).append(";exception=").append(//$NON-NLS-1$ //$NON-NLS-2$
                getException()).append(//$NON-NLS-1$
                "]");
                return sb.toString();
            }
        });
    }

    protected void fireTransferSendDataEvent() {
        listener.handleTransferEvent(new IOutgoingFileTransferSendDataEvent() {

            public IOutgoingFileTransfer getSource() {
                return AbstractOutgoingFileTransfer.this;
            }

            public String toString() {
                final StringBuffer sb = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "IOutgoingFileTransferSendDataEvent[");
                //$NON-NLS-1$
                sb.append("bytesSent=").append(//$NON-NLS-1$
                bytesSent).append(";fileLength=").append(//$NON-NLS-1$ 
                fileTransferInfo.getFileSize()).append(//$NON-NLS-1$
                "]");
                return sb.toString();
            }
        });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IOutgoingFileTransfer#getBytesSent()
	 */
    public long getBytesSent() {
        return bytesSent;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#cancel()
	 */
    public void cancel() {
        if (job != null)
            job.cancel();
    }

    /* (non-Javadoc)
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
        long fileLength = getFileLength();
        if (fileLength == -1 || fileLength == 0)
            return fileLength;
        return ((double) bytesSent / (double) fileLength);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#getFileLength()
	 */
    public long getFileLength() {
        return fileTransferInfo.getFileSize();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IFileTransfer#isDone()
	 */
    public boolean isDone() {
        return done;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.isInstance(this)) {
            return this;
        }
        final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
        return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
    }

    /**
	 * Open incoming and outgoing streams associated with this file transfer.
	 * Subclasses must implement this method to open input and output streams.
	 * The <code>remoteFileContents</code> and <code>localFileContent</code>
	 * must be non-<code>null</code> after successful completion of the
	 * implementation of this method.
	 * 
	 * @throws SendFileTransferException if some problem
	 */
    protected abstract void openStreams() throws SendFileTransferException;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#getOutgoingNamespace()
	 */
    public Namespace getOutgoingNamespace() {
        return IDFactory.getDefault().getNamespaceByName(FileTransferNamespace.PROTOCOL);
    }

    public IFileTransferListener getListener() {
        return listener;
    }

    protected String createJobName() {
        return getRemoteFileURL().toString();
    }

    protected void setupAndScheduleJob() {
        if (fileTransferJob == null)
            fileTransferJob = new FileTransferJob(createJobName());
        fileTransferJob.setFileTransferRunnable(fileTransferRunnable);
        fileTransferJob.setFileTransfer(this);
        job = fileTransferJob;
        job.schedule();
    }

    protected void fireSendStartEvent() {
        listener.handleTransferEvent(new IOutgoingFileTransferResponseEvent() {

            public String toString() {
                final StringBuffer sb = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "IOutgoingFileTransferResponseEvent[");
                //$NON-NLS-1$ //$NON-NLS-2$
                sb.append("isdone=").append(done).append(";");
                //$NON-NLS-1$
                sb.append("bytesSent=").append(//$NON-NLS-1$
                bytesSent).append(//$NON-NLS-1$
                "]");
                return sb.toString();
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent#requestAccepted()
			 */
            public boolean requestAccepted() {
                return true;
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferEvent#getSource()
			 */
            public IOutgoingFileTransfer getSource() {
                return AbstractOutgoingFileTransfer.this;
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent#setFileTransferJob(org.eclipse.ecf.filetransfer.FileTransferJob)
			 */
            public void setFileTransferJob(org.eclipse.ecf.filetransfer.FileTransferJob ftj) {
                AbstractOutgoingFileTransfer.this.fileTransferJob = ftj;
            }
        });
    }

    protected abstract void setupProxy(Proxy proxy);

    protected void setupProxies() {
        // If it's been set directly (via ECF API) then this overrides platform settings
        if (proxy == null) {
            try {
                proxy = ProxySetupHelper.getProxy(getRemoteFileURL().toExternalForm());
            } catch (NoClassDefFoundError e) {
                Activator.logNoProxyWarning(e);
            }
        }
        if (proxy != null)
            setupProxy(proxy);
    }

    /**
	 * Select a single proxy from a set of proxies available for the given host.  This implementation
	 * selects in the following manner:  1) If proxies provided is null or array of 0 length, null 
	 * is returned.  If only one proxy is available (array of length 1) then the entry is returned.
	 * If proxies provided is length greater than 1, then if the type of a proxy in the array matches the given
	 * protocol (e.g. http, https), then the first matching proxy is returned.  If the protocol does
	 * not match any of the proxies, then the *first* proxy (i.e. proxies[0]) is returned.  Subclasses may
	 * override if desired.
	 * 
	 * @param protocol the target protocol (e.g. http, https, scp, etc).  Will not be <code>null</code>.
	 * @param proxies the proxies to select from.  May be <code>null</code> or array of length 0.
	 * @return proxy data selected from the proxies provided.  
	 */
    protected IProxyData selectProxyFromProxies(String protocol, IProxyData[] proxies) {
        if (proxies == null || proxies.length == 0)
            return null;
        // If only one proxy is available, then use that
        if (proxies.length == 1)
            return proxies[0];
        // one...if not found then use first
        if (//$NON-NLS-1$
        protocol.equalsIgnoreCase("http")) {
            for (int i = 0; i < proxies.length; i++) {
                if (proxies[i].getType().equals(IProxyData.HTTP_PROXY_TYPE))
                    return proxies[i];
            }
        } else if (//$NON-NLS-1$
        protocol.equalsIgnoreCase("https")) {
            for (int i = 0; i < proxies.length; i++) {
                if (proxies[i].getType().equals(IProxyData.HTTPS_PROXY_TYPE))
                    return proxies[i];
            }
        }
        // If we haven't found it yet, then return the first one.
        return proxies[0];
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#sendOutgoingRequest(org.eclipse.ecf.filetransfer.identity.IFileID, org.eclipse.ecf.filetransfer.IFileTransferInfo, org.eclipse.ecf.filetransfer.IFileTransferListener, java.util.Map)
	 */
    public void sendOutgoingRequest(IFileID targetReceiver, IFileTransferInfo localFileToSend, IFileTransferListener transferListener, Map ops) throws SendFileTransferException {
        Assert.isNotNull(targetReceiver, Messages.AbstractOutgoingFileTransfer_RemoteFileID_Not_Null);
        Assert.isNotNull(transferListener, Messages.AbstractOutgoingFileTransfer_TransferListener_Not_Null);
        Assert.isNotNull(localFileToSend, Messages.AbstractOutgoingFileTransfer_EXCEPTION_FILE_TRANSFER_INFO_NOT_NULL);
        this.done = false;
        this.bytesSent = 0;
        this.exception = null;
        this.fileTransferInfo = localFileToSend;
        this.remoteFileID = targetReceiver;
        this.options = ops;
        try {
            this.remoteFileURL = targetReceiver.getURL();
        } catch (final MalformedURLException e) {
            throw new SendFileTransferException(NLS.bind(Messages.AbstractOutgoingFileTransfer_MalformedURLException, targetReceiver), e);
        }
        this.listener = transferListener;
        setupProxies();
        openStreams();
        fireSendStartEvent();
        setupAndScheduleJob();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#sendOutgoingRequest(org.eclipse.ecf.filetransfer.identity.IFileID, java.io.File, org.eclipse.ecf.filetransfer.IFileTransferListener, java.util.Map)
	 */
    public void sendOutgoingRequest(IFileID targetReceiver, final File localFileToSend, IFileTransferListener transferListener, Map ops) throws SendFileTransferException {
        sendOutgoingRequest(targetReceiver, new FileTransferInfo(localFileToSend, null, null), transferListener, ops);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#addListener(org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener)
	 */
    public void addListener(IIncomingFileTransferRequestListener l) {
    // Not needed
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#removeListener(org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener)
	 */
    public boolean removeListener(IIncomingFileTransferRequestListener l) {
        return false;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#setConnectContextForAuthentication(org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#setProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
