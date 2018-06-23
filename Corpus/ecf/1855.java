/*******************************************************************************
 * Copyright (c) 2012 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.generic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.comm.tcp.ISocketAcceptHandler;
import org.eclipse.ecf.provider.comm.tcp.Server;

/**
 * @since 4.3
 */
public class SSLServerSOContainerGroup extends SOContainerGroup implements ISocketAcceptHandler {

    public static final int DEFAULT_BACKLOG = 50;

    public static final String DEFAULT_GROUP_NAME = SSLServerSOContainerGroup.class.getName();

    private int port = 0;

    private int backlog = DEFAULT_BACKLOG;

    SSLServerSocket serverSocket;

    private boolean isOnTheAir = false;

    private ThreadGroup threadGroup;

    private InetAddress inetAddress;

    private Thread listenerThread;

    public  SSLServerSOContainerGroup(String name, ThreadGroup group, int port, int backlog, InetAddress inetAddress) {
        super(name);
        this.threadGroup = group;
        this.port = port;
        this.backlog = backlog;
        this.inetAddress = inetAddress;
        listenerThread = setupListener();
    }

    public  SSLServerSOContainerGroup(String name, ThreadGroup group, int port, int backlog) {
        this(name, group, port, backlog, null);
    }

    /**
	 * @param name name
	 * @param group thread group to use to create thread
	 * @param port port
	 * @param bindAddress bind address
	 * @since 4.4
	 */
    public  SSLServerSOContainerGroup(String name, ThreadGroup group, int port, InetAddress bindAddress) {
        this(name, group, port, Server.DEFAULT_BACKLOG, bindAddress);
    }

    public  SSLServerSOContainerGroup(String name, ThreadGroup group, int port) {
        this(name, group, port, DEFAULT_BACKLOG);
    }

    public  SSLServerSOContainerGroup(String name, int port) {
        this(name, null, port);
    }

    public  SSLServerSOContainerGroup(int port) {
        this(DEFAULT_GROUP_NAME, null, port);
    }

    /**
	 * @param name name
	 * @param group thread group to use
	 * @param sslServerSocket the ssl server socket
	 * @since 4.6
	 */
    public  SSLServerSOContainerGroup(String name, ThreadGroup group, SSLServerSocket sslServerSocket) {
        super(name);
        this.threadGroup = group;
        this.serverSocket = sslServerSocket;
        this.port = serverSocket.getLocalPort();
        this.listenerThread = setupListener();
    }

    protected void trace(String msg) {
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.DEBUG, msg);
    }

    protected void traceStack(String msg, Throwable e) {
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SSLServerSOContainerGroup.class, msg, e);
    }

    public synchronized void putOnTheAir() throws IOException {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("SSLServerSOContainerGroup at port " + port + " on the air");
        if (this.serverSocket == null) {
            SSLServerSocketFactory socketFactory = ProviderPlugin.getDefault().getSSLServerSocketFactory();
            if (socketFactory == null)
                throw new //$NON-NLS-1$
                IOException(//$NON-NLS-1$
                "Cannot get SSLServerSocketFactory to create SSLServerSocket");
            serverSocket = (SSLServerSocket) ((this.inetAddress == null) ? socketFactory.createServerSocket(this.port, this.backlog) : socketFactory.createServerSocket(this.port, this.backlog, this.inetAddress));
        }
        port = serverSocket.getLocalPort();
        isOnTheAir = true;
        listenerThread.start();
    }

    public synchronized boolean isOnTheAir() {
        return isOnTheAir;
    }

    public synchronized void takeOffTheAir() {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("Taking " + getName() + " off the air.");
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
        if (threadGroup != null) {
            threadGroup.interrupt();
            threadGroup = null;
        }
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                Trace.catching("org.eclipse.ecf.provider", ECFProviderDebugOptions.CONNECTION, SSLServerSOContainerGroup.class, "takeOffTheAir", e);
            }
            this.serverSocket = null;
        }
        isOnTheAir = false;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized String toString() {
        //$NON-NLS-1$
        return super.toString() + ";port:" + port;
    }

    protected Thread setupListener() {
        return new Thread(threadGroup, new Runnable() {

            public void run() {
                while (true) {
                    try {
                        handleAccept(serverSocket.accept());
                    } catch (Exception e) {
                        traceStack("Exception in accept", e);
                        break;
                    }
                }
                debug(//$NON-NLS-1$
                "SSLServerSOContaienrGroup closing listener normally.");
            }
        }, //$NON-NLS-1$ //$NON-NLS-2$
        "SSLServerSOContainerGroup(" + port + ")");
    }

    /**
	 * @param aSocket socket
	 * @throws Exception if some problem with handling accept
	 * @since 4.7
	 */
    protected void handleSyncAccept(final Socket aSocket) throws Exception {
        super.handleAccept(aSocket);
    }

    public void handleAccept(final Socket aSocket) {
        new Thread(threadGroup, new Runnable() {

            public void run() {
                try {
                    debug("accept:" + //$NON-NLS-1$
                    aSocket.getInetAddress());
                    handleSyncAccept(aSocket);
                } catch (Exception e) {
                    traceStack("Unexpected exception in handleAccept...closing", e);
                    try {
                        aSocket.close();
                    } catch (IOException e1) {
                        ProviderPlugin.getDefault().log(new Status(IStatus.ERROR, ProviderPlugin.PLUGIN_ID, IStatus.ERROR, "accept.close", e1));
                    }
                }
            }
        }).start();
    }

    protected void debug(String msg) {
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.CONNECTION, msg);
    }
}
