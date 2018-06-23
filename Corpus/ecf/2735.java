/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.filetransfer;

import java.io.File;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;

/**
 * File transfer information delivered to
 * {@link IIncomingFileTransferRequestListener} via an event implementing
 * {@link IFileTransferRequestEvent#getFileTransferInfo()}
 * 
 */
public class FileTransferInfo implements IFileTransferInfo, Serializable {

    private static final long serialVersionUID = 8354226751625912190L;

    protected File file;

    protected Map properties;

    protected String description;

    protected String mimeType = null;

    public  FileTransferInfo(File file) {
        this(file, null);
    }

    public  FileTransferInfo(File file, Map properties) {
        this(file, properties, null);
    }

    public  FileTransferInfo(File file, Map properties, String description) {
        this(file, properties, description, null);
    }

    public  FileTransferInfo(File file, Map properties, String description, String mimeType) {
        //$NON-NLS-1$
        Assert.isNotNull(file, "file must not be null");
        this.file = file;
        this.properties = (properties == null) ? new HashMap() : properties;
        this.description = description;
        this.mimeType = mimeType;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransferInfo#getFile()
	 */
    public File getFile() {
        return file;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransferInfo#getProperties()
	 */
    public Map getProperties() {
        return properties;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransferInfo#getDescription()
	 */
    public String getDescription() {
        return description;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransferInfo#getFileSize()
	 */
    public long getFileSize() {
        return file.length();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.IFileTransferInfo#getMimeType()
	 */
    public String getMimeType() {
        if (mimeType == null)
            return URLConnection.getFileNameMap().getContentTypeFor(file.getAbsolutePath());
        return mimeType;
    }
}
