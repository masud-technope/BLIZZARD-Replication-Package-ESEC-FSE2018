/*******************************************************************************
* Copyright (c) 2015 University of York. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Antonio Garcia-Dominguez - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.filetransfer.httpclient4;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.HttpContext;

/**
 * @since 1.1
 */
final class SNIAwareHttpClient extends DefaultHttpClient {

    public  SNIAwareHttpClient() {
    // default constructor
    }

    public  SNIAwareHttpClient(SingleClientConnManager singleClientConnManager) {
        super(singleClientConnManager);
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SSLSocketFactory factory = new SSLSocketFactory(SSLContexts.createSystemDefault(), SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER) {

            @Override
            public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException, ConnectTimeoutException {
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=478655
                if (socket instanceof SSLSocket) {
                    try {
                        final Method mSetHost = socket.getClass().getMethod("setHost", String.class);
                        mSetHost.setAccessible(true);
                        mSetHost.invoke(socket, host.getHostName());
                    } catch (NoSuchMethodException ex) {
                    } catch (IllegalAccessException ex) {
                    } catch (InvocationTargetException ex) {
                    }
                }
                return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
            }
        };
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, factory));
        return new BasicClientConnectionManager(registry);
    }
}
