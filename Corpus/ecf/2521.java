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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.FileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.provider.xmpp.XmppPlugin;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.ecf.provider.xmpp.identity.XMPPFileID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPFileNamespace;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;

public class XMPPOutgoingFileTransferHelper implements ISendFileTransferContainerAdapter {

    /**
	 * 
	 */
    private static final String OUTGOING_REQUEST_TIMEOUT = "outgoingRequestTimeout";

    List transferListeners = new ArrayList();

    List incomingListeners = new ArrayList();

    XMPPContainer container = null;

    FileTransferManager manager = null;

    public  XMPPOutgoingFileTransferHelper(XMPPContainer container) {
        this.container = container;
    }

    public void dispose() {
        transferListeners.clear();
        incomingListeners.clear();
        manager = null;
    }

    protected void addFileTransferListener(IFileTransferListener listener) {
        synchronized (transferListeners) {
            transferListeners.add(listener);
        }
    }

    protected void removeFileTransferListener(IFileTransferListener listener) {
        synchronized (transferListeners) {
            transferListeners.remove(listener);
        }
    }

    public void addListener(IIncomingFileTransferRequestListener listener) {
        if (listener == null)
            return;
        final XMPPFileTransferRequestListener xmppListener = new XMPPFileTransferRequestListener(container, listener);
        incomingListeners.add(xmppListener);
        if (this.manager != null)
            this.manager.addFileTransferListener(xmppListener);
    }

    public void sendOutgoingRequest(IFileID targetReceiver, IFileTransferInfo localFileToSend, IFileTransferListener progressListener, Map options) throws SendFileTransferException {
        if (manager == null)
            throw new SendFileTransferException("not connected");
        if (!(targetReceiver instanceof XMPPFileID))
            throw new SendFileTransferException("target receiver not XMPPFileID type.");
        final XMPPFileID fileID = (XMPPFileID) targetReceiver;
        int requestTimeout = -1;
        if (options != null) {
            final Object option = options.get(OUTGOING_REQUEST_TIMEOUT);
            if (option != null) {
                if (option instanceof String) {
                    try {
                        requestTimeout = Integer.valueOf((String) option).intValue();
                    } catch (final NumberFormatException e) {
                    }
                } else if (option instanceof Integer) {
                    requestTimeout = ((Integer) option).intValue();
                }
            }
        }
        final XMPPOutgoingFileTransfer fileTransfer = new XMPPOutgoingFileTransfer(manager, fileID.getXMPPID(), localFileToSend, progressListener, requestTimeout);
        try {
            fileTransfer.startSend(localFileToSend.getFile(), localFileToSend.getDescription());
        } catch (final XMPPException e) {
            throw new SendFileTransferException("Exception sending outgoing file transfer request", e);
        }
    }

    protected void fireFileTransferEvent(IFileTransferEvent event) {
        synchronized (transferListeners) {
            for (final Iterator i = transferListeners.iterator(); i.hasNext(); ) {
                final IFileTransferListener l = (IFileTransferListener) i.next();
                l.handleTransferEvent(event);
            }
        }
    }

    public Namespace getOutgoingFileTransferNamespace() {
        return container.getConnectNamespace();
    }

    public boolean removeListener(IIncomingFileTransferRequestListener listener) {
        if (listener == null)
            return false;
        synchronized (incomingListeners) {
            for (final Iterator i = incomingListeners.iterator(); i.hasNext(); ) {
                final XMPPFileTransferRequestListener ftl = (XMPPFileTransferRequestListener) i.next();
                if (ftl.hasListener(listener)) {
                    this.manager.removeFileTransferListener(ftl);
                    i.remove();
                }
            }
        }
        return true;
    }

    public void sendOutgoingRequest(IFileID targetReceiver, File localFileToSend, IFileTransferListener transferListener, Map options) throws SendFileTransferException {
        sendOutgoingRequest(targetReceiver, new FileTransferInfo(localFileToSend), transferListener, options);
    }

    /**
	 * @param connection
	 */
    public void setConnection(XMPPConnection connection) {
        if (connection != null) {
            synchronized (incomingListeners) {
                this.manager = new FileTransferManager(connection);
                for (final Iterator i = incomingListeners.iterator(); i.hasNext(); ) {
                    final XMPPFileTransferRequestListener ftl = (XMPPFileTransferRequestListener) i.next();
                    this.manager.addFileTransferListener(ftl);
                }
            }
        } else {
            if (this.manager != null) {
                synchronized (incomingListeners) {
                    for (final Iterator i = incomingListeners.iterator(); i.hasNext(); ) {
                        final XMPPFileTransferRequestListener ftl = (XMPPFileTransferRequestListener) i.next();
                        this.manager.removeFileTransferListener(ftl);
                    }
                    this.manager = null;
                }
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#setConnectContextForAuthentication(org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void setConnectContextForAuthentication(IConnectContext connectContext) {
    // XXX no authentication for XMPP file transfer...ignore calls
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter#setProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    public void setProxy(Proxy proxy) {
    // TODO Auto-generated method stub
    }

    public Namespace getOutgoingNamespace() {
        return IDFactory.getDefault().getNamespaceByName(XMPPFileNamespace.NAME);
    }

    /* (non-Javadoc)
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
}
