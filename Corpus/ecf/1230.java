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
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.filetransfer.IRemoteFileAttributes;
import org.eclipse.ecf.filetransfer.IRemoteFileInfo;

/**
 *
 */
public class EFSRemoteFileInfo implements IRemoteFileInfo {

    IFileInfo efsFileInfo;

    IRemoteFileAttributes fileAttributes;

    /**
	 * @param remoteFileInfo 
	 */
    public  EFSRemoteFileInfo(IFileInfo remoteFileInfo) {
        Assert.isNotNull(remoteFileInfo);
        this.efsFileInfo = remoteFileInfo;
        this.fileAttributes = new EFSFileAttributes(efsFileInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#getAttributes()
	 */
    public IRemoteFileAttributes getAttributes() {
        return fileAttributes;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#getLastModified()
	 */
    public long getLastModified() {
        return efsFileInfo.getLastModified();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#getLength()
	 */
    public long getLength() {
        return efsFileInfo.getLength();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#getName()
	 */
    public String getName() {
        return efsFileInfo.getName();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#isDirectory()
	 */
    public boolean isDirectory() {
        return efsFileInfo.isDirectory();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#setAttributes(org.eclipse.ecf.filetransfer.IRemoteFileAttributes)
	 */
    public void setAttributes(IRemoteFileAttributes attributes) {
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#setLastModified(long)
	 */
    public void setLastModified(long time) {
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileInfo#setName(java.lang.String)
	 */
    public void setName(String name) {
    }
}
