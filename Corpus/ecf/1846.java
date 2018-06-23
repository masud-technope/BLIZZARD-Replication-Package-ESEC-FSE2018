/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/
package org.eclipse.ecf.provider.comm.tcp;

import java.io.IOException;
import java.net.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;

public class Server extends ServerSocket {

    public static final int DEFAULT_BACKLOG = 50;

    ISocketAcceptHandler acceptHandler;

    Thread listenerThread;

    ThreadGroup threadGroup;

    protected void debug(String msg) {
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.CONNECTION, msg);
    }

    protected void traceStack(String msg, Throwable e) {
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, Server.class, msg, e);
    }

    /**
	 * @param group thread group for created threads
	 * @param port port
	 * @param backlog backlog
	 * @param bindAddress a bindAddress
	 * @param handler the socket accept handler
	 * @throws IOException if server setup cannot be done
	 * @since 4.4
	 */
    public  Server(ThreadGroup group, int port, int backlog, InetAddress bindAddress, ISocketAcceptHandler handler) throws IOException {
        super(port, backlog, bindAddress);
        if (handler == null)
            //$NON-NLS-1$
            throw new NullPointerException("Socket accept handler cannot be null");
        acceptHandler = handler;
        threadGroup = group;
        listenerThread = setupListener();
        listenerThread.start();
    }

    /**
	 * @param group thread group for created threads
	 * @param port port
	 * @param bindAddress a bindAddress
	 * @param handler the socket accept handler
	 * @throws IOException if server setup cannot be done
	 * @since 4.4
	 */
    public  Server(ThreadGroup group, int port, InetAddress bindAddress, ISocketAcceptHandler handler) throws IOException {
        this(group, port, DEFAULT_BACKLOG, bindAddress, handler);
    }

    /**
	 * @param group thread group for created threads
	 * @param port port
	 * @param backlog backlog
	 * @param handler the socket accept handler
	 * @throws IOException if server setup cannot be done
	 * @since 4.4
	 */
    public  Server(ThreadGroup group, int port, int backlog, ISocketAcceptHandler handler) throws IOException {
        this(null, port, backlog, null, handler);
    }

    public  Server(ThreadGroup group, int port, ISocketAcceptHandler handler) throws IOException {
        this(group, port, DEFAULT_BACKLOG, handler);
    }

    public  Server(int port, ISocketAcceptHandler handler) throws IOException {
        this(null, port, handler);
    }

    protected Thread setupListener() {
        return new Thread(threadGroup, new Runnable() {

            public void run() {
                while (true) {
                    try {
                        handleAccept(accept());
                    } catch (Exception e) {
                        traceStack("Exception in accept", e);
                        break;
                    }
                }
                debug(//$NON-NLS-1$
                "Closing listener normally.");
            }
        }, //$NON-NLS-1$ //$NON-NLS-2$
        "ServerApplication(" + getLocalPort() + ")");
    }

    protected void handleAccept(final Socket aSocket) {
        new Thread(threadGroup, new Runnable() {

            public void run() {
                try {
                    debug("accept:" + //$NON-NLS-1$
                    aSocket.getInetAddress());
                    acceptHandler.handleAccept(aSocket);
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

    public synchronized void close() throws IOException {
        super.close();
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
        if (threadGroup != null) {
            threadGroup.interrupt();
            threadGroup = null;
        }
        acceptHandler = null;
    }
}
