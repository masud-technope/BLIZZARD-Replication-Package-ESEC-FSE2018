/*******************************************************************************
 * Copyright (c)2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.ssl;

import java.io.IOException;
import java.net.*;
import java.security.SecureRandom;
import javax.net.ssl.*;

public class ECFSSLSocketFactory extends SSLSocketFactory {

    //$NON-NLS-1$
    public static final String DEFAULT_SSL_PROTOCOL = "https.protocols";

    private SSLContext sslContext = null;

    private String defaultProtocolNames = System.getProperty(DEFAULT_SSL_PROTOCOL);

    private SSLSocketFactory getSSLSocketFactory() throws IOException {
        if (null == sslContext) {
            try {
                sslContext = getSSLContext(defaultProtocolNames);
            } catch (Exception e) {
                IOException ioe = new IOException();
                ioe.initCause(e);
                throw ioe;
            }
        }
        return (sslContext == null) ? (SSLSocketFactory) SSLSocketFactory.getDefault() : sslContext.getSocketFactory();
    }

    public SSLContext getSSLContext(String protocols) {
        SSLContext rtvContext = null;
        if (protocols != null) {
            //$NON-NLS-1$
            String protocolNames[] = protocols.split(",");
            for (int i = 0; i < protocolNames.length; i++) {
                try {
                    rtvContext = SSLContext.getInstance(protocolNames[i]);
                    rtvContext.init(null, new TrustManager[] { new ECFTrustManager() }, new SecureRandom());
                    break;
                } catch (Exception e) {
                }
            }
        }
        return rtvContext;
    }

    public Socket createSocket() throws IOException {
        return getSSLSocketFactory().createSocket();
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return getSSLSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public String[] getDefaultCipherSuites() {
        try {
            return getSSLSocketFactory().getDefaultCipherSuites();
        } catch (IOException e) {
            return new String[] {};
        }
    }

    public String[] getSupportedCipherSuites() {
        try {
            return getSSLSocketFactory().getSupportedCipherSuites();
        } catch (IOException e) {
            return new String[] {};
        }
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLSocketFactory().createSocket(host, port);
    }

    public Socket createSocket(InetAddress address, int port) throws IOException {
        return getSSLSocketFactory().createSocket(address, port);
    }

    public Socket createSocket(InetAddress address, int port, InetAddress arg2, int arg3) throws IOException {
        return getSSLSocketFactory().createSocket(address, port);
    }

    public Socket createSocket(String host, int port, InetAddress address, int localPort) throws IOException, UnknownHostException {
        return getSSLSocketFactory().createSocket(host, port, address, localPort);
    }
}
