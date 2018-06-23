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
package org.eclipse.ecf.internal.provider.rss.container;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;
import org.eclipse.ecf.internal.provider.rss.http.HttpRequest;
import org.eclipse.ecf.internal.provider.rss.http.HttpResponse;
import org.eclipse.ecf.provider.comm.tcp.ISocketAcceptHandler;
import org.eclipse.ecf.provider.comm.tcp.Server;
import org.eclipse.ecf.provider.generic.SOContainerGroup;

/**
 * 
 */
public class RssServerSOContainerGroup extends SOContainerGroup implements ISocketAcceptHandler {

    private static final String PRODUCT_NAME = "ECF RSS Server/0.0.1";

    private static final String DEFAULT_GROUP_NAME = RssServerSOContainerGroup.class.getName();

    protected int port;

    protected Server listener;

    protected ThreadGroup threadGroup;

    private boolean isOnTheAir = false;

    /**
	 * The constructors
	 * @param port 
	 */
    public  RssServerSOContainerGroup(int port) {
        this(DEFAULT_GROUP_NAME, null, port);
    }

    public  RssServerSOContainerGroup(String name, int port) {
        this(name, null, port);
    }

    public  RssServerSOContainerGroup(String name, ThreadGroup group, int port) {
        super(name);
        threadGroup = group;
        this.port = port;
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    public synchronized void putOnTheAir() throws IOException {
        trace("Group at port " + port + " on the air");
        listener = new Server(threadGroup, port, this);
        port = listener.getLocalPort();
        isOnTheAir = true;
    }

    public synchronized boolean isOnTheAir() {
        return isOnTheAir;
    }

    public void handleAccept(Socket socket) throws Exception {
        final HttpRequest request = new HttpRequest(socket.getInputStream());
        final RssServerSOContainer con = (RssServerSOContainer) get(String.valueOf(port));
        if (con == null) {
            throw new IOException("Container for port " + port + " not found!");
        }
        trace("Found container: " + con.getID().getName() + " for target " + request.getURLString());
        String body = "";
        int code = HttpResponse.SERVER_ERROR;
        String path = request.getPath();
        // Tests whether this abstract pathname is absolute.
        path = path.startsWith("/") ? path.substring(1) : path;
        final File feedFile = new File(path);
        Date lastModified = new Date();
        if (feedFile.exists() && feedFile.isFile()) {
            trace("Found feed file: " + feedFile.getAbsolutePath());
            code = HttpResponse.OK;
            body = readFileToString(feedFile);
            lastModified = new Date(feedFile.lastModified());
        } else {
            code = HttpResponse.NOT_FOUND;
        }
        // Create connect response and send it back
        final HttpResponse response = new HttpResponse(code, body);
        if (body != null && body.length() > 0) {
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", "text/xml");
            response.setHeader("Server", PRODUCT_NAME);
            response.setDateHeader("Last-Modified", lastModified);
        }
        response.writeToStream(socket.getOutputStream());
    }

    private String readFileToString(File file) throws IOException {
        final FileInputStream in = new FileInputStream(file);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int n;
        final byte buffer[] = new byte[8192];
        while ((n = in.read(buffer, 0, buffer.length)) > -1) {
            out.write(buffer, 0, n);
            out.flush();
        }
        return out.toString();
    }

    public synchronized void takeOffTheAir() {
        if (listener != null) {
            trace("Taking " + getName() + " on the air.");
            try {
                listener.close();
            } catch (final IOException e) {
                dumpStack("Exception in closeListener", e);
            }
            listener = null;
        }
        isOnTheAir = false;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return super.toString() + ";port:" + port;
    }
}
