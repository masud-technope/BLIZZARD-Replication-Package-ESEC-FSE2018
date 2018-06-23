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
package org.eclipse.ecf.provider.filetransfer.events.socket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;
import org.eclipse.ecf.filetransfer.events.socket.ISocketListener;

public abstract class SocketEventSource implements ISocketEventSource {

    private final List listeners = new ArrayList();

    public void addListener(ISocketListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void removeListener(ISocketListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void fireEvent(ISocketEvent event) {
        List toNotify = null;
        // Copy array
        synchronized (listeners) {
            toNotify = new ArrayList(listeners);
        }
        // Notify all in toNotify
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            ISocketListener l = (ISocketListener) i.next();
            l.handleSocketEvent(event);
        }
    }
}
