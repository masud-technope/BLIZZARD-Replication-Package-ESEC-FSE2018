/*******************************************************************************
 * Copyright (c) 2006, 2009 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.ContainerDisposeEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.InvalidFileRangeSpecificationException;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.protocol.bittorrent.Torrent;
import org.eclipse.ecf.protocol.bittorrent.TorrentFactory;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;

public final class BitTorrentContainer implements IContainer, IRetrieveFileTransfer {

    private final List containerListeners;

    private final ID id;

    public  BitTorrentContainer() throws IDCreateException {
        id = IDFactory.getDefault().createGUID();
        containerListeners = new ArrayList();
        BitTorrentProviderPlugin.getDefault().setConfigurationPath();
    }

    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
    // do nothing since BitTorrent does not have a central network
    }

    public void disconnect() {
    // do nothing since BitTorrent does not have a central network
    }

    public void dispose() {
        fireContainerEvent(new ContainerDisposeEvent(id));
    }

    private void fireContainerEvent(IContainerEvent event) {
        synchronized (containerListeners) {
            for (int i = 0; i < containerListeners.size(); i++) {
                final IContainerListener icl = (IContainerListener) containerListeners.get(i);
                icl.handleEvent(event);
            }
        }
    }

    public Object getAdapter(Class serviceType) {
        return serviceType.isInstance(this) ? this : null;
    }

    public Namespace getConnectNamespace() {
        return null;
    }

    public ID getConnectedID() {
        return null;
    }

    public ID getID() {
        return id;
    }

    public void addListener(IContainerListener listener) {
        synchronized (containerListeners) {
            if (!containerListeners.contains(listener)) {
                containerListeners.add(listener);
            }
        }
    }

    public void removeListener(IContainerListener listener) {
        synchronized (containerListeners) {
            containerListeners.remove(listener);
        }
    }

    public void sendRetrieveRequest(final IFileID remoteFileReference, final IFileTransferListener transferListener, Map options) throws IncomingFileTransferException {
        //$NON-NLS-1$
        Assert.isNotNull(remoteFileReference, "remoteFileReference cannot be null");
        //$NON-NLS-1$
        Assert.isLegal(remoteFileReference instanceof TorrentID, "remoteFileReference must be instanceof TorrentID");
        //$NON-NLS-1$
        Assert.isNotNull(transferListener, "transferListener cannot be null");
        transferListener.handleTransferEvent(new IIncomingFileTransferReceiveStartEvent() {

            private IIncomingFileTransfer transfer;

            private boolean cancelled = false;

            public IIncomingFileTransfer receive(File localFileToSave) throws IOException {
                if (cancelled) {
                    throw new RuntimeException(new UserCancelledException());
                }
                //$NON-NLS-1$
                Assert.isNotNull(//$NON-NLS-1$
                localFileToSave, //$NON-NLS-1$
                "localFileToSave cannot be null");
                if (localFileToSave.exists() && !localFileToSave.canWrite()) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    throw new IOException("file=" + localFileToSave.getAbsolutePath() + " does not exist or cannot be written to");
                }
                final TorrentFile file = new TorrentFile(((TorrentID) remoteFileReference).getFile());
                file.setTargetFile(localFileToSave);
                final Torrent torrent = TorrentFactory.createTorrent(file);
                transfer = new TorrentFileTransfer(remoteFileReference, transferListener, torrent);
                return transfer;
            }

            public IIncomingFileTransfer receive(File localFileToSave, FileTransferJob fileTransferJob) throws IOException {
                if (cancelled) {
                    throw new RuntimeException(new UserCancelledException());
                }
                //$NON-NLS-1$
                Assert.isNotNull(//$NON-NLS-1$
                localFileToSave, //$NON-NLS-1$
                "localFileToSave must not be null");
                if (localFileToSave.exists() && !localFileToSave.canWrite()) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    throw new IOException("file=" + localFileToSave.getAbsolutePath() + " does not exist or cannot be written to");
                }
                final TorrentFile file = new TorrentFile(((TorrentID) remoteFileReference).getFile());
                file.setTargetFile(localFileToSave);
                final Torrent torrent = TorrentFactory.createTorrent(file);
                transfer = new TorrentFileTransfer(remoteFileReference, transferListener, torrent);
                return transfer;
            }

            public void cancel() {
                if (transfer != null) {
                    transfer.cancel();
                    cancelled = true;
                }
            }

            public IFileID getFileID() {
                return remoteFileReference;
            }

            public IIncomingFileTransfer receive(OutputStream streamToStore) throws IOException {
                throw new UnsupportedOperationException("receive(OutputStream) not supported by this provider");
            }

            public IIncomingFileTransfer receive(OutputStream streamToStore, FileTransferJob fileTransferJob) throws IOException {
                throw new UnsupportedOperationException("receive(OutputStream,FileTransferJob) not supported by this provider");
            }

            public IIncomingFileTransfer getSource() {
                return transfer;
            }

            public Map getResponseHeaders() {
                return null;
            }
        });
    }

    public Namespace getRetrieveNamespace() {
        return IDFactory.getDefault().getNamespaceByName(BitTorrentProviderPlugin.NAMESPACE_ID);
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
    }

    public void setProxy(Proxy proxy) {
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter#sendRetrieveRequest(org.eclipse.ecf.filetransfer.identity.IFileID, org.eclipse.ecf.filetransfer.IFileRangeSpecification, org.eclipse.ecf.filetransfer.IFileTransferListener, java.util.Map)
	 */
    public void sendRetrieveRequest(IFileID remoteFileID, IFileRangeSpecification rangeSpecification, IFileTransferListener transferListener, Map options) throws IncomingFileTransferException {
        // TODO Auto-generated method stub
        throw new InvalidFileRangeSpecificationException("not supported", rangeSpecification);
    }
}
