/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *               Cloudsmith, Inc. - additional API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.outgoing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileTransferInfo;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.internal.provider.filetransfer.Messages;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class LocalFileOutgoingFileTransfer extends AbstractOutgoingFileTransfer {

    public  LocalFileOutgoingFileTransfer() {
    // not needed
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer#openStreams()
	 */
    protected void openStreams() throws SendFileTransferException {
        IFileTransferInfo localFileTransferInfo = getFileTransferInfo();
        Assert.isNotNull(localFileTransferInfo);
        // Setup input file
        File inputFile = localFileTransferInfo.getFile();
        try {
            setInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
        } catch (Exception e) {
            hardClose();
            throw new SendFileTransferException(NLS.bind(Messages.LocalFileOutgoingFileTransfer_EXCEPTION_OPENING_FOR_INPUT, inputFile));
        }
        URL url = getRemoteFileURL();
        Assert.isNotNull(url);
        try {
            File outputFile = new File(url.getPath());
            setOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        } catch (Exception e) {
            hardClose();
            throw new SendFileTransferException(NLS.bind(Messages.LocalFileOutgoingFileTransfer_EXCEPTION_OPENING_FOR_OUTPUT, url));
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.filetransfer.outgoing.AbstractOutgoingFileTransfer#setupProxy(org.eclipse.ecf.core.util.Proxy)
	 */
    protected void setupProxy(Proxy proxy) {
    // No proxy for local file system
    }
}
