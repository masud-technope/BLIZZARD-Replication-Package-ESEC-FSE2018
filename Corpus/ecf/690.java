/****************************************************************************
 * Copyright (c) 2006, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.identity;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.provider.filetransfer.Messages;

public class FileTransferID extends BaseID implements IFileID {

    private static final long serialVersionUID = 1274308869502156992L;

    URL fileURL;

    URI fileURI;

    public  FileTransferID(Namespace namespace, URL url) {
        super(namespace);
        Assert.isNotNull(url, Messages.FileTransferID_Exception_Url_Not_Null);
        this.fileURL = url;
    }

    /**
	 * @param namespace namespace
	 * @param uri uri
	 * @since 3.2
	 */
    public  FileTransferID(Namespace namespace, URI uri) {
        super(namespace);
        //$NON-NLS-1$
        Assert.isNotNull(uri, "FileTransferID URI cannot be null");
        this.fileURI = uri;
    }

    protected int namespaceCompareTo(BaseID o) {
        if (o == null)
            return 1;
        if (!(o instanceof FileTransferID))
            return 1;
        return (fileURI != null) ? fileURI.compareTo(((FileTransferID) o).fileURI) : fileURL.toExternalForm().compareTo(((FileTransferID) o).toExternalForm());
    }

    protected boolean namespaceEquals(BaseID o) {
        if (o == null)
            return false;
        if (!(o instanceof FileTransferID))
            return false;
        return (fileURI != null) ? fileURI.equals(((FileTransferID) o).fileURI) : fileURL.equals(((FileTransferID) o).fileURL);
    }

    protected String namespaceGetName() {
        return (fileURI != null) ? fileURI.toASCIIString() : fileURL.toExternalForm();
    }

    protected int namespaceHashCode() {
        return (fileURI != null) ? fileURI.hashCode() : this.fileURL.hashCode();
    }

    public String getFilename() {
        return getFileNameOnly();
    }

    public URL getURL() throws MalformedURLException {
        return (fileURI != null) ? fileURI.toURL() : fileURL;
    }

    protected String getFileNameOnly() {
        String path = (fileURI != null) ? fileURI.getPath() : fileURL.getPath();
        //$NON-NLS-1$;
        return (path == null) ? null : path.substring(path.lastIndexOf("/") + 1);
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer b = new StringBuffer("FileTransferID[");
        b.append(toExternalForm());
        //$NON-NLS-1$
        b.append("]");
        return b.toString();
    }

    /**
	 * @since 3.2
	 */
    public URI getURI() throws URISyntaxException {
        return (fileURI != null) ? fileURI : new URI(fileURL.toExternalForm());
    }
}
