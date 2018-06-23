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

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.IRemoteFileInfo;
import org.eclipse.ecf.filetransfer.identity.IFileID;

/**
 *
 */
public class EFSRemoteFile implements IRemoteFile {

    private final IFileID fileID;

    private final EFSRemoteFileInfo remoteFileInfo;

    /**
	 * @param fileID
	 * @param remoteFile
	 */
    public  EFSRemoteFile(IFileID fileID, IFileInfo remoteFile) {
        this.fileID = fileID;
        this.remoteFileInfo = new EFSRemoteFileInfo(remoteFile);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFile#getID()
	 */
    public IFileID getID() {
        return fileID;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFile#getInfo()
	 */
    public IRemoteFileInfo getInfo() {
        return remoteFileInfo;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        return null;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("EFSRemoteFile[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("name=").append(getInfo().getName()).append("]");
        return buf.toString();
    }
}
