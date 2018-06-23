/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.efs;

import java.net.URL;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemListener;
import org.eclipse.ecf.filetransfer.RemoteFileSystemException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.provider.filetransfer.browse.AbstractFileSystemBrowser;

/**
 *
 */
public class FileStoreBrowser extends AbstractFileSystemBrowser {

    IFileStore fileStore;

    /**
	 * @param store 
	 * @param efsDirectory 
	 * @param directoryID2 
	 * @param listener 
	 * @throws RemoteFileSystemException 
	 * 
	 */
    public  FileStoreBrowser(IFileStore store, URL efsDirectory, IFileID directoryID2, IRemoteFileSystemListener listener) throws RemoteFileSystemException {
        super(directoryID2, listener, efsDirectory, null, null);
        Assert.isNotNull(store);
        this.fileStore = store;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.filetransfer.browse.LocalFileSystemBrowser#runDirectoryRequest()
	 */
    protected void runRequest() throws Exception {
        final IFileInfo fileStoreInfo = fileStore.fetchInfo();
        if (fileStoreInfo.isDirectory()) {
            final IFileInfo[] fileInfos = fileStore.childInfos(EFS.NONE, null);
            remoteFiles = new IRemoteFile[fileInfos.length];
            for (int i = 0; i < fileInfos.length; i++) {
                remoteFiles[i] = new //$NON-NLS-1$
                EFSRemoteFile(//$NON-NLS-1$
                FileIDFactory.getDefault().createFileID(fileID.getNamespace(), new URL(directoryOrFile + "/" + fileInfos[i].getName())), //$NON-NLS-1$
                fileInfos[i]);
            }
        } else {
            remoteFiles = new IRemoteFile[1];
            remoteFiles[0] = new EFSRemoteFile(fileID, fileStoreInfo);
        }
    }

    protected void setupProxy(Proxy proxy) {
    // Not implemented
    }
}
