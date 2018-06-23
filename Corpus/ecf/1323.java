/****************************************************************************
 * Copyright (c) 2007 IBM, Composent Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Thomas Joiner - HttpClient 4 implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.httpclient4;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;

public class HttpClientRetrieveFileTransferFactory implements IRetrieveFileTransferFactory {

    public IRetrieveFileTransfer newInstance() {
        SSLSocketFactory factory = new SSLSocketFactory(SSLContexts.createSystemDefault(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, factory));
        return new HttpClientRetrieveFileTransfer(new SNIAwareHttpClient(new SingleClientConnManager(registry)));
    }
}
