/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Thomas Joiner - HttpClient 4 implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.httpclient4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;
import org.eclipse.ecf.filetransfer.events.socket.ISocketListener;
import org.eclipse.ecf.filetransfer.events.socketfactory.INonconnectedSocketFactory;
import org.eclipse.ecf.provider.filetransfer.events.socket.SocketClosedEvent;
import org.eclipse.ecf.provider.filetransfer.events.socket.SocketConnectedEvent;
import org.eclipse.ecf.provider.filetransfer.events.socket.SocketCreatedEvent;

public class ECFHttpClientProtocolSocketFactory implements SchemeSocketFactory {

    protected ISocketEventSource source;

    private INonconnectedSocketFactory unconnectedFactory;

    private ISocketListener socketConnectListener;

    private static final ISocketListener NULL_SOCKET_EVENT_LISTENER = new ISocketListener() {

        public void handleSocketEvent(ISocketEvent event) {
        //empty
        }
    };

    public  ECFHttpClientProtocolSocketFactory(INonconnectedSocketFactory unconnectedFactory, ISocketEventSource source, ISocketListener socketConnectListener) {
        super();
        Assert.isNotNull(unconnectedFactory);
        Assert.isNotNull(source);
        this.unconnectedFactory = unconnectedFactory;
        this.source = source;
        this.socketConnectListener = socketConnectListener != null ? socketConnectListener : NULL_SOCKET_EVENT_LISTENER;
    }

    public  ECFHttpClientProtocolSocketFactory(final SocketFactory socketFactory, ISocketEventSource source, ISocketListener socketConnectListener) {
        this(new INonconnectedSocketFactory() {

            public Socket createSocket() throws IOException {
                return socketFactory.createSocket();
            }
        }, source, socketConnectListener);
    }

    public Socket createSocket(HttpParams params) throws IOException {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, DebugOptions.METHODS_ENTERING, ECFHttpClientProtocolSocketFactory.class, "createSocket");
        final Socket factorySocket = unconnectedFactory.createSocket();
        fireEvent(socketConnectListener, new SocketCreatedEvent(source, factorySocket));
        //$NON-NLS-1$
        Trace.exiting(Activator.PLUGIN_ID, DebugOptions.METHODS_EXITING, ECFHttpClientProtocolSocketFactory.class, "socketCreated " + factorySocket);
        return factorySocket;
    }

    public Socket connectSocket(final Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        int timeout = params.getIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        Trace.entering(Activator.PLUGIN_ID, DebugOptions.METHODS_ENTERING, ECFHttpClientProtocolSocketFactory.class, "connectSocket " + remoteAddress.toString() + " timeout=" + timeout);
        try {
            if (localAddress != null) {
                // only explicitly bind if a local address is actually provided (bug 444377)
                //$NON-NLS-1$//$NON-NLS-2$
                Trace.trace(Activator.PLUGIN_ID, "bind(" + localAddress + ")");
                sock.bind(localAddress);
            }
            //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            Trace.trace(Activator.PLUGIN_ID, "connect(" + remoteAddress.toString() + ", " + timeout + ")");
            sock.connect(remoteAddress, timeout);
            //$NON-NLS-1$
            Trace.trace(Activator.PLUGIN_ID, "connected");
        } catch (IOException e) {
            Trace.catching(Activator.PLUGIN_ID, DebugOptions.EXCEPTIONS_CATCHING, ECFHttpClientProtocolSocketFactory.class, "createSocket", e);
            fireEvent(socketConnectListener, new SocketClosedEvent(source, sock, sock));
            Trace.throwing(Activator.PLUGIN_ID, DebugOptions.EXCEPTIONS_THROWING, ECFHttpClientProtocolSocketFactory.class, "createSocket", e);
            throw e;
        }
        Socket toReturn;
        Socket wrapped = new CloseMonitoringSocket(sock, socketConnectListener, source);
        SocketConnectedEvent connectedEvent = new SocketConnectedEvent(source, sock, wrapped);
        fireEvent(socketConnectListener, connectedEvent);
        // Change the wrapped socket if one of the receivers of the SocketConnectedEvent changed it
        if (connectedEvent.getSocket() != wrapped) {
            toReturn = connectedEvent.getSocket();
            ((CloseMonitoringSocket) wrapped).setWrappedSocket(toReturn);
        } else {
            toReturn = wrapped;
        }
        return toReturn;
    }

    private static void fireEvent(final ISocketListener spyListener, ISocketEvent event) {
        if (spyListener != null) {
            spyListener.handleSocketEvent(event);
        }
        event.getSource().fireEvent(event);
    }

    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        if (sock instanceof SSLSocket) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("Socket not created by this factory.");
        }
        return false;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ECFHttpClientProtocolSocketFactory);
    }

    public int hashCode() {
        return ECFHttpClientProtocolSocketFactory.class.hashCode();
    }
}
