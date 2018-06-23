/****************************************************************************
 * Copyright (c) 2012 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.generic;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.comm.tcp.SSLClient;

/**
 * @since 4.3
 */
public class SSLClientSOContainer extends ClientSOContainer {

    int keepAlive = 0;

    public static final int DEFAULT_TCP_CONNECT_TIMEOUT = 30000;

    public static final String DEFAULT_COMM_NAME = org.eclipse.ecf.provider.comm.tcp.SSLClient.class.getName();

    public  SSLClientSOContainer(ISharedObjectContainerConfig config) {
        super(config);
    }

    public  SSLClientSOContainer(ISharedObjectContainerConfig config, int ka) {
        super(config);
        keepAlive = ka;
    }

    protected int getConnectTimeout() {
        return DEFAULT_TCP_CONNECT_TIMEOUT;
    }

    /**
	 * @param remoteSpace remote space
	 * @param data data
	 * @return ISynchAsynchConnection a non-<code>null</code> instance.
	 * @throws ConnectionCreateException not thrown by this implementation.
	 */
    protected ISynchAsynchConnection createConnection(ID remoteSpace, Object data) throws ConnectionCreateException {
        //$NON-NLS-1$ //$NON-NLS-2$
        debug("createClientConnection:" + remoteSpace + ":" + data);
        ISynchAsynchConnection conn = new SSLClient(receiver, keepAlive);
        return conn;
    }

    public static final void main(String[] args) throws Exception {
        ISharedObjectContainerConfig config = new SOContainerConfig(IDFactory.getDefault().createGUID());
        SSLClientSOContainer container = new SSLClientSOContainer(config);
        // now join group
        ID serverID = IDFactory.getDefault().createStringID(SSLServerSOContainer.getDefaultServerURL());
        container.connect(serverID, null);
        Thread.sleep(200000);
    }
}
