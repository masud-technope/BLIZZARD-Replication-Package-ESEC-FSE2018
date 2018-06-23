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

public class SocketFactory implements IClientSocketFactory, IServerSocketFactory {

    protected static SocketFactory defaultFactory;

    protected static SocketFactory factory = null;

    public Socket createSocket(String name, int port, int timeout) throws IOException {
        if (factory != null) {
            return factory.createSocket(name, port, timeout);
        }
        Socket s = new Socket();
        s.connect(new InetSocketAddress(name, port), timeout);
        return s;
    }

    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        if (factory != null) {
            return factory.createServerSocket(port, backlog);
        }
        return new ServerSocket(port, backlog);
    }

    /**
	 * @param port port
	 * @param backlog backlog
	 * @param bindAddress bindAddress
	 * @return ServerSocket server socket created
	 * @throws IOException if server socket cannot be created
	 * @since 4.4
	 */
    public ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddress) throws IOException {
        if (factory != null) {
            return factory.createServerSocket(port, backlog, bindAddress);
        }
        return new ServerSocket(port, backlog, bindAddress);
    }

    public static synchronized SocketFactory getSocketFactory() {
        return factory;
    }

    public static synchronized SocketFactory getDefaultSocketFactory() {
        if (defaultFactory == null) {
            defaultFactory = new SocketFactory();
        }
        return defaultFactory;
    }

    public static synchronized void setSocketFactory(SocketFactory fact) {
        if (!fact.equals(defaultFactory)) {
            factory = fact;
        }
    }
}
