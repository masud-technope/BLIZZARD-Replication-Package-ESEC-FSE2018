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

import java.net.Socket;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;

public abstract class AbstractSocketEvent implements ISocketEvent {

    private Socket factorySocket;

    private Socket wrappedSocket;

    private ISocketEventSource source;

    protected  AbstractSocketEvent(ISocketEventSource source, Socket factorySocket, Socket wrappedSocket) {
        Assert.isNotNull(source);
        Assert.isNotNull(factorySocket);
        Assert.isNotNull(wrappedSocket);
        this.source = source;
        this.factorySocket = factorySocket;
        this.wrappedSocket = wrappedSocket;
    }

    public ISocketEventSource getSource() {
        return source;
    }

    public Socket getFactorySocket() {
        return factorySocket;
    }

    public boolean isSameFactorySocket(ISocketEvent event) {
        AbstractSocketEvent other = (AbstractSocketEvent) event;
        return this.getFactorySocket() == other.getFactorySocket();
    }

    public Socket getSocket() {
        return wrappedSocket;
    }

    protected void setSocket(Socket socket) {
        this.wrappedSocket = socket;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer(getEventName() + "[");
        //$NON-NLS-1$
        sb.append("source=");
        sb.append(source);
        //$NON-NLS-1$
        sb.append(" socket=");
        sb.append(getSocket());
        sb.append(']');
        return sb.toString();
    }

    protected abstract String getEventName();
}
