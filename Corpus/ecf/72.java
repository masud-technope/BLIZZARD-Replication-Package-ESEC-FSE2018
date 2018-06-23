/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 *               Cloudsmith, Inc. - additional API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.filetransfer.outgoing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URLConnection;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.SendFileTransferException;
import org.eclipse.ecf.filetransfer.service.ISendFileTransfer;
import org.eclipse.ecf.internal.provider.filetransfer.Messages;
import org.eclipse.ecf.provider.filetransfer.util.JREProxyHelper;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractUrlConnectionOutgoingFileTransfer extends AbstractOutgoingFileTransfer implements ISendFileTransfer {

    private static final int OK_RESPONSE_CODE = 200;

    protected URLConnection urlConnection;

    protected long lastModifiedTime = 0L;

    protected int httpVersion = 1;

    protected int responseCode = -1;

    protected String responseMessage = null;

    private JREProxyHelper proxyHelper = null;

    public  AbstractUrlConnectionOutgoingFileTransfer() {
        super();
        proxyHelper = new JREProxyHelper();
    }

    /**
	 * Setup and connect.  Subclasses should override as appropriate.  After calling is complete,
	 * the <code>urlConnection</code> member variable should be non-null, and ready to have it's
	 * getInputStream() method called.
	 * 
	 * @throws IOException if the connection cannot be opened.
	 */
    protected abstract void connect() throws IOException;

    protected boolean isConnected() {
        return (urlConnection != null);
    }

    public int getResponseCode() {
        if (responseCode != -1)
            return responseCode;
        if (isHTTP()) {
            final String response = urlConnection.getHeaderField(0);
            if (response == null) {
                responseCode = -1;
                httpVersion = 1;
                return responseCode;
            }
            if (//$NON-NLS-1$
            response == null || !response.startsWith("HTTP/"))
                return -1;
            response.trim();
            //$NON-NLS-1$
            final int mark = response.indexOf(" ") + 1;
            if (mark == 0)
                return -1;
            if (response.charAt(mark - 2) != '1')
                httpVersion = 0;
            int last = mark + 3;
            if (last > response.length())
                last = response.length();
            responseCode = Integer.parseInt(response.substring(mark, last));
            if (last + 1 <= response.length())
                responseMessage = response.substring(last + 1);
        } else {
            responseCode = OK_RESPONSE_CODE;
            //$NON-NLS-1$
            responseMessage = "OK";
        }
        return responseCode;
    }

    private boolean isHTTP() {
        final String protocol = getRemoteFileURL().getProtocol();
        if (//$NON-NLS-1$ //$NON-NLS-2$
        protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https"))
            return true;
        return false;
    }

    /**
	 * @param proxy2 the ECF proxy to setup
	 */
    protected void setupProxy(final Proxy proxy2) {
        proxyHelper.setupProxy(proxy2);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer#openStreams()
	 */
    protected void openStreams() throws SendFileTransferException {
        try {
            File localFile = getFileTransferInfo().getFile();
            // Set input stream from local file
            setInputStream(new BufferedInputStream(new FileInputStream(localFile)));
            // Then connect
            connect();
            // Make PUT request
            setOutputStream(urlConnection.getOutputStream());
        } catch (final Exception e) {
            throw new SendFileTransferException(NLS.bind(Messages.UrlConnectionOutgoingFileTransfer_EXCEPTION_COULD_NOT_CONNECT, getRemoteFileURL().toString()), e);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer#hardClose()
	 */
    protected void hardClose() {
        super.hardClose();
        int rCode = getResponseCode();
        if (rCode != OK_RESPONSE_CODE) {
            //$NON-NLS-1$
            exception = new ProtocolException(NLS.bind("{0} {1}", new Integer(rCode), responseMessage));
        }
        urlConnection = null;
        if (proxyHelper != null) {
            proxyHelper.dispose();
            proxyHelper = null;
        }
    }
}
