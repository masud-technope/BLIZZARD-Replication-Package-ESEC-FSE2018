/**
 * Copyright (c) 2006 Parity Communications, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Yakovlev - initial API and implementation
 */
package org.eclipse.ecf.internal.provider.rss.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.ISynchAsynchEventHandler;

/**
 * A ISynchAsynchConnection with support for HTTP-specific features. See the <a
 * href='http://www.w3.org/pub/WWW/Protocols/'>spec</a> for details.
 * 
 * 
 */
public class HttpClient implements ISynchAsynchConnection {

    private static final String PRODUCT_NAME = "ECF RSS Client/0.0.1";

    protected static final int DEFAULT_PORT = 80;

    protected Socket socket;

    protected ISynchAsynchEventHandler handler;

    protected boolean started = false;

    /**
	 * The constructors
	 * @param handler 
	 */
    public  HttpClient(ISynchAsynchEventHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Event handler cannot be null");
        }
        this.handler = handler;
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    private URL getURL(String path) throws MalformedURLException {
        if (socket != null) {
            return new URL("http", socket.getInetAddress().getHostName(), socket.getPort(), path);
        }
        return null;
    }

    public Object connect(ID remote, Object data, int timeout) throws ECFException {
        try {
            trace("connect(" + remote + "," + data + "," + timeout + ")");
            if (socket != null) {
                throw new ECFException("Already connected to " + getURL(null));
            }
            final URL url = new URL(remote.getName());
            /*
			 * // Get socket factory and create/connect socket SocketFactory fact =
			 * SocketFactory.getSocketFactory(); if(fact == null) { fact =
			 * SocketFactory.getDefaultSocketFactory(); }
			 */
            final int port = url.getPort() != -1 ? url.getPort() : DEFAULT_PORT;
            // socket = fact.createSocket(url.getHost(), port, timeout);
            socket = new Socket(url.getHost(), port);
        } catch (final IOException e) {
            throw new ECFException(e);
        }
        return null;
    }

    public void disconnect() {
        trace("disconnect()");
        if (socket != null) {
            try {
                socket.close();
            } catch (final IOException e) {
            }
            socket = null;
        }
    }

    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public ID getLocalID() {
        if (handler != null) {
            return handler.getEventHandlerID();
        }
        if (socket == null) {
            return null;
        }
        ID retID = null;
        try {
            retID = IDFactory.getDefault().createStringID(getURL(null).toString());
        } catch (final Exception e) {
            dumpStack("Exception in getLocalID()", e);
            return null;
        }
        return retID;
    }

    public void start() {
        trace("start()");
        started = true;
    }

    public void stop() {
        trace("stop()");
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public Map getProperties() {
        return null;
    }

    public void addListener(IConnectionListener listener) {
    // TODO Auto-generated method stub
    }

    public void removeListener(IConnectionListener listener) {
    // TODO Auto-generated method stub
    }

    public Object getAdapter(Class clazz) {
        return null;
    }

    public void sendAsynch(ID receiver, byte[] data) throws IOException {
    // TODO Auto-generated method stub
    }

    public Object sendSynch(ID receiver, byte[] data) throws IOException {
        trace("sendSynch(" + receiver + ", " + data + ")");
        // data should be a string
        final HttpRequest request = new HttpRequest("GET", getURL(new String(data)));
        request.setHeader("User-Agent", PRODUCT_NAME);
        request.setHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
        request.setHeader("Connection", "keep-alive");
        request.writeToStream(request.getStartLine(), socket.getOutputStream());
        final HttpResponse response = new HttpResponse(socket.getInputStream());
        return response.getBody();
    }
}
