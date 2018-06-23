/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.outgoing;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.ISendFileTransfer;
import org.eclipse.ecf.internal.provider.filetransfer.Activator;
import org.eclipse.ecf.internal.provider.filetransfer.Messages;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferNamespace;
import org.eclipse.osgi.util.NLS;

/**
 * Multi protocol handler for outgoing file transfer. Multiplexes between Apache
 * httpclient 3.0.1-based file retriever and the URLConnection-based file
 * retriever.
 */
public class MultiProtocolOutgoingAdapter implements ISendFileTransfer {

    IConnectContext connectContext = null;

    Proxy proxy = null;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#getOutgoingNamespace()
	 */
    public Namespace getOutgoingNamespace() {
        return IDFactory.getDefault().getNamespaceByName(FileTransferNamespace.PROTOCOL);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter#setConnectContextForAuthentication(org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter#setProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void sendOutgoingRequest(IFileID targetID, File outgoingFile, IFileTransferListener transferListener, Map options) throws SendFileTransferException {
        Assert.isNotNull(targetID);
        Assert.isNotNull(outgoingFile);
        Assert.isNotNull(transferListener);
        String protocol = null;
        try {
            protocol = targetID.getURI().getScheme();
        } catch (URISyntaxException e) {
            try {
                protocol = targetID.getURL().getProtocol();
            } catch (final MalformedURLException e1) {
                throw new SendFileTransferException(Messages.AbstractRetrieveFileTransfer_MalformedURLException);
            }
        }
        ISendFileTransferContainerAdapter fileTransfer = Activator.getDefault().getSendFileTransfer(protocol);
        // If no handler setup for this protocol then throw
        if (fileTransfer == null) {
            if (//$NON-NLS-1$
            protocol.equalsIgnoreCase("file")) {
                fileTransfer = new LocalFileOutgoingFileTransfer();
            }
        }
        if (fileTransfer == null) {
            throw new SendFileTransferException(NLS.bind(Messages.MultiProtocolOutgoingAdapter_EXCEPTION_NO_PROTOCOL_HANDER, targetID));
        }
        fileTransfer.setConnectContextForAuthentication(connectContext);
        fileTransfer.setProxy(proxy);
        fileTransfer.sendOutgoingRequest(targetID, outgoingFile, transferListener, options);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#addListener(org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener)
	 */
    public void addListener(IIncomingFileTransferRequestListener listener) {
    // We don't have any listeners
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#removeListener(org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener)
	 */
    public boolean removeListener(IIncomingFileTransferRequestListener listener) {
        return false;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#sendOutgoingRequest(org.eclipse.ecf.filetransfer.identity.IFileID, org.eclipse.ecf.filetransfer.IFileTransferInfo, org.eclipse.ecf.filetransfer.IFileTransferListener, java.util.Map)
	 */
    public void sendOutgoingRequest(IFileID targetID, IFileTransferInfo localFileToSend, IFileTransferListener transferListener, Map options) throws SendFileTransferException {
        Assert.isNotNull(targetID);
        Assert.isNotNull(localFileToSend);
        Assert.isNotNull(transferListener);
        String protocol = null;
        try {
            protocol = targetID.getURI().getScheme();
        } catch (URISyntaxException e) {
            try {
                protocol = targetID.getURL().getProtocol();
            } catch (final MalformedURLException e1) {
                throw new SendFileTransferException(Messages.AbstractRetrieveFileTransfer_MalformedURLException);
            }
        }
        ISendFileTransferContainerAdapter fileTransfer = Activator.getDefault().getSendFileTransfer(protocol);
        // If no handler setup for this protocol then throw
        if (fileTransfer == null) {
            throw new SendFileTransferException(NLS.bind(Messages.MultiProtocolOutgoingAdapter_EXCEPTION_NO_PROTOCOL_HANDER, targetID));
        }
        fileTransfer.setConnectContextForAuthentication(connectContext);
        fileTransfer.setProxy(proxy);
        fileTransfer.sendOutgoingRequest(targetID, localFileToSend, transferListener, options);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }
}
