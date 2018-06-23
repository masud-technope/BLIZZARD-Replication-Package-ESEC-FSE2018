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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.ecf.filetransfer.IRemoteFileAttributes;

/**
 *
 */
public class EFSFileAttributes implements IRemoteFileAttributes {

    static String[] fileAttributes = { IRemoteFileAttributes.READ_ATTRIBUTE, IRemoteFileAttributes.WRITE_ATTRIBUTE, IRemoteFileAttributes.HIDDEN_ATTRIBUTE, IRemoteFileAttributes.EXEC_ATTRIBUTE, IRemoteFileAttributes.ARCHIVE_ATTRIBUTE };

    static List attributeKeys = new ArrayList(Arrays.asList(fileAttributes));

    IFileInfo fileInfo;

    public  EFSFileAttributes(IFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileAttributes#getAttribute(java.lang.String)
	 */
    public String getAttribute(String key) {
        if (key == null)
            return null;
        if (key.equalsIgnoreCase(IRemoteFileAttributes.READ_ATTRIBUTE)) {
            return Boolean.TRUE.toString();
        } else if (key.equalsIgnoreCase(IRemoteFileAttributes.WRITE_ATTRIBUTE)) {
            if (!fileInfo.getAttribute(EFS.ATTRIBUTE_READ_ONLY))
                return Boolean.TRUE.toString();
            else
                return Boolean.FALSE.toString();
        } else if (key.equals(IRemoteFileAttributes.HIDDEN_ATTRIBUTE)) {
            if (fileInfo.getAttribute(EFS.ATTRIBUTE_HIDDEN))
                return Boolean.TRUE.toString();
            else
                return Boolean.FALSE.toString();
        } else if (key.equals(IRemoteFileAttributes.EXEC_ATTRIBUTE)) {
            if (fileInfo.getAttribute(EFS.ATTRIBUTE_EXECUTABLE))
                return Boolean.TRUE.toString();
            else
                return Boolean.FALSE.toString();
        } else if (key.equals(IRemoteFileAttributes.ARCHIVE_ATTRIBUTE)) {
            if (fileInfo.getAttribute(EFS.ATTRIBUTE_ARCHIVE))
                return Boolean.TRUE.toString();
            else
                return Boolean.FALSE.toString();
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileAttributes#getAttributeKeys()
	 */
    public Iterator getAttributeKeys() {
        return attributeKeys.iterator();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IRemoteFileAttributes#setAttribute(java.lang.String, java.lang.String)
	 */
    public void setAttribute(String key, String value) {
    }
}
