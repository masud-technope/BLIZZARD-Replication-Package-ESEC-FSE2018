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
package org.eclipse.ecf.tests.filetransfer;

import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;

public class FileTransferListenerWrapper implements IFileTransferListener {

    private IFileTransferListener wrapped;

    public  FileTransferListenerWrapper(IFileTransferListener wrapped) {
        this.wrapped = wrapped;
    }

    public void handleTransferEvent(IFileTransferEvent event) {
        if (wrapped != null) {
            wrapped.handleTransferEvent(event);
        }
        if (event instanceof IFileTransferConnectStartEvent) {
            handleStartConnectEvent((IFileTransferConnectStartEvent) event);
        } else if (event instanceof IIncomingFileTransferReceiveStartEvent) {
            handleStartEvent((IIncomingFileTransferReceiveStartEvent) event);
        } else if (event instanceof IIncomingFileTransferReceiveDataEvent) {
            handleDataEvent((IIncomingFileTransferReceiveDataEvent) event);
        } else if (event instanceof IIncomingFileTransferReceiveDoneEvent) {
            handleDoneEvent((IIncomingFileTransferReceiveDoneEvent) event);
        } else {
            handleUnexpectedEvent(event);
        }
    }

    protected void handleUnexpectedEvent(IFileTransferEvent event) {
    }

    protected void handleDoneEvent(IIncomingFileTransferReceiveDoneEvent event) {
    }

    protected void handleDataEvent(IIncomingFileTransferReceiveDataEvent event) {
    }

    protected void handleStartEvent(IIncomingFileTransferReceiveStartEvent event) {
    }

    protected void handleStartConnectEvent(IFileTransferConnectStartEvent event) {
    }
}
