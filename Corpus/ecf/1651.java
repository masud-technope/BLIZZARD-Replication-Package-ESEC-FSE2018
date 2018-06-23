/*******************************************************************************
* Copyright (c) 2009 IBM, and others. 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM Corporation - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.httpclient4;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.filetransfer.events.socket.ISocketClosedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketConnectedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketCreatedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketListener;

public class ConnectingSocketMonitor implements ISocketListener {

    private Map connectingSockets;

    public  ConnectingSocketMonitor(int initialCapacity) {
        connectingSockets = Collections.synchronizedMap(new HashMap(initialCapacity));
    }

    public  ConnectingSocketMonitor() {
        connectingSockets = Collections.synchronizedMap(new HashMap());
    }

    /**
	 * Callers of this method should not iterate through the returned
	 * Collection, as a CME is possible...as reported by bug
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=430704
	 * Rather than call this method and iterate through the Collection,
	 * to close the connecting sockets call closeConnectingSockets
	 * instead.
	 * @return Collection the existing collection of underlying connecting
	 * Socket instances
	 */
    public Collection getConnectingSockets() {
        return Collections.unmodifiableCollection(connectingSockets.keySet());
    }

    public void clear() {
        connectingSockets.clear();
    }

    /**
	 * Method added to synchronize access to underlying keySet
	 * to prevent CME as reported in bug
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=430704
	 */
    public void closeSockets() {
        // are prevented via synchronized Map
        synchronized (connectingSockets) {
            for (Iterator iterator = connectingSockets.keySet().iterator(); iterator.hasNext(); ) {
                Socket socket = (Socket) iterator.next();
                try {
                    Trace.trace(Activator.PLUGIN_ID, "Call socket.close() for socket=" + //$NON-NLS-1$
                    socket.toString());
                    socket.close();
                } catch (IOException e) {
                    Trace.catching(Activator.PLUGIN_ID, DebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "cancel", e);
                }
            }
        }
    }

    public void handleSocketEvent(ISocketEvent event) {
        if (event instanceof ISocketCreatedEvent) {
            connectingSockets.put(event.getFactorySocket(), event);
        } else if (event instanceof ISocketConnectedEvent) {
            connectingSockets.remove(event.getFactorySocket());
        } else if (event instanceof ISocketClosedEvent) {
            connectingSockets.remove(event.getFactorySocket());
        }
    }
}
