/*******************************************************************************
 * Copyright (c) 2014 CohesionForce Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CohesionForce Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.scp;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRemoteFileSystemBrowser;
import org.eclipse.ecf.filetransfer.service.IRemoteFileSystemBrowserFactory;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferNamespace;
import org.eclipse.osgi.util.NLS;

/**
 * Factory for creating a remote file browser. This class is registered in the
 * plugin to handle the "scp://" protocol.
 *
 */
public class ScpBrowseFileTransferFactory implements IRemoteFileSystemBrowserFactory {

    public IRemoteFileSystemBrowser newInstance() {
        return new IRemoteFileSystemBrowser() {

            private Proxy proxy;

            private IConnectContext connectContext;

            public Namespace getBrowseNamespace() {
                return IDFactory.getDefault().getNamespaceByName(FileTransferNamespace.PROTOCOL);
            }

            public IRemoteFileSystemRequest sendBrowseRequest(IFileID directoryOrFileId, IRemoteFileSystemListener listener) throws RemoteFileSystemException {
                Assert.isNotNull(directoryOrFileId);
                Assert.isNotNull(listener);
                URL url;
                try {
                    url = directoryOrFileId.getURL();
                } catch (final MalformedURLException e) {
                    throw new RemoteFileSystemException(NLS.bind("Exception creating URL for {0}", directoryOrFileId));
                }
                ScpFileSystemBrowser browser = new ScpFileSystemBrowser(directoryOrFileId, listener, url, connectContext, proxy);
                return browser.sendBrowseRequest();
            }

            public void setConnectContextForAuthentication(IConnectContext connectContext) {
                this.connectContext = connectContext;
            }

            public void setProxy(Proxy proxy) {
                this.proxy = proxy;
            }

            public Object getAdapter(Class adapter) {
                return null;
            }
        };
    }
}
