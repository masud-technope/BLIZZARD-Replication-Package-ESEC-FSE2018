package org.eclipse.ecf.internal.provider.filetransfer.efs;

import java.net.URI;
import java.net.URL;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemListener;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemRequest;
import org.eclipse.ecf.filetransfer.RemoteFileSystemException;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRemoteFileSystemBrowser;
import org.eclipse.ecf.provider.filetransfer.identity.FileTransferNamespace;

/**
 *
 */
public class EFSRemoteFileSystemBrowser implements IRemoteFileSystemBrowser {

    IConnectContext connectContext;

    Proxy proxy;

    public Namespace getBrowseNamespace() {
        return IDFactory.getDefault().getNamespaceByName(FileTransferNamespace.PROTOCOL);
    }

    public IRemoteFileSystemRequest sendBrowseRequest(IFileID directoryOrFileID, IRemoteFileSystemListener listener) throws RemoteFileSystemException {
        Assert.isNotNull(directoryOrFileID);
        Assert.isNotNull(listener);
        URL efsDirectory = null;
        FileStoreBrowser fsb = null;
        try {
            efsDirectory = directoryOrFileID.getURL();
            //$NON-NLS-1$ //$NON-NLS-2$
            final String path = StringUtils.replaceAll(efsDirectory.getPath(), " ", "%20");
            fsb = new FileStoreBrowser(EFS.getStore(new URI(path)), efsDirectory, directoryOrFileID, listener);
        } catch (final Exception e) {
            throw new RemoteFileSystemException(e);
        }
        return fsb.sendBrowseRequest();
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
