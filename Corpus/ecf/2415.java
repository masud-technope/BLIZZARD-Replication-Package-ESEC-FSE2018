/****************************************************************************
 * Copyright (c) 2004, 2015 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.filetransfer.xmpp;

import java.io.File;
import java.io.FileOutputStream;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDoneEvent;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

/**
 * 
 */
public class XMPPOutgoingTest extends ContainerAbstractTestCase {

    private static final String TESTSRCPATH = "test.src";

    private static final String TESTSRCFILE = TESTSRCPATH + "/test.txt";

    private static final String TESTTARGETPATH = "test.target";

    static final String XMPP_CONTAINER = "ecf.xmpps.smack";

    ISendFileTransferContainerAdapter adapter0, adapter1 = null;

    IOutgoingFileTransfer outgoing;

    File incomingDirectory;

    File incomingFile;

    IFileID targetID;

    boolean requestAccepted;

    class IncomingFileTransferRequestListener implements IIncomingFileTransferRequestListener {

        private final boolean accept;

        public  IncomingFileTransferRequestListener(boolean accept) {
            this.accept = accept;
        }

        public void handleFileTransferRequest(IFileTransferRequestEvent event) {
            System.out.println("receiver.handleFileTransferRequest(" + event + ")");
            incomingDirectory = new File(TESTTARGETPATH);
            incomingDirectory.mkdirs();
            incomingFile = new File(incomingDirectory, event.getFileTransferInfo().getFile().getName());
            try {
                if (accept)
                    event.accept(new FileOutputStream(incomingFile), receiverTransferListener);
                else
                    event.reject();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                fail("exception calling accept for receive file transfer");
            }
        }
    }

    private IFileTransferListener receiverTransferListener = new IFileTransferListener() {

        public void handleTransferEvent(IFileTransferEvent event) {
        // System.out.println("receiver.handleTransferEvent(" + event +
        // ")");
        }
    };

    private IFileTransferListener senderTransferListener = new IFileTransferListener() {

        public void handleTransferEvent(IFileTransferEvent event) {
            System.out.println("sender.handleTransferEvent=" + event);
            if (event instanceof IOutgoingFileTransferResponseEvent) {
                final IOutgoingFileTransferResponseEvent revent = (IOutgoingFileTransferResponseEvent) event;
                outgoing = revent.getSource();
                requestAccepted = revent.requestAccepted();
                if (!requestAccepted)
                    System.out.println("sender.  File transfer request REJECTED");
            } else if (event instanceof IOutgoingFileTransferSendDoneEvent) {
                syncNotify();
            }
        }
    };

    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(2);
        // Create two clients
        clients = createClients();
        // Connect clients
        for (int i = 0; i < 2; i++) connectClient(i);
        adapter0 = getOutgoingFileTransfer(0);
        // 0 is the receiver, so has to listen for
        // 1 is the sender
        adapter1 = getOutgoingFileTransfer(1);
        // Target ID is client 0's connectedID (includes resource name now that
        // connected)
        targetID = createFileID(adapter1, getClient(0).getConnectedID(), TESTSRCFILE);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        disconnectClients();
        if (incomingFile != null)
            incomingFile.delete();
        incomingFile = null;
        if (incomingDirectory != null)
            incomingDirectory.delete();
        incomingDirectory = null;
        outgoing = null;
        targetID = null;
    }

    public void testReceiverAccept() throws Exception {
        // setup receiver to accept the request
        // file transfer requests
        adapter0.addListener(new IncomingFileTransferRequestListener(true));
        // Initiate send request
        adapter1.sendOutgoingRequest(targetID, new File(TESTSRCFILE), senderTransferListener, null);
        syncWaitForNotify(20000);
        assertNotNull(outgoing);
        assertNull(outgoing.getException());
        assertTrue(requestAccepted);
        assertNotNull(incomingFile);
        assertTrue(incomingFile.exists());
    }

    public void testReceiverReject() throws Exception {
        // setup receiver to accept the request
        // file transfer requests
        adapter0.addListener(new IncomingFileTransferRequestListener(false));
        // Initiate send request
        adapter1.sendOutgoingRequest(targetID, new File(TESTSRCFILE), senderTransferListener, null);
        syncWaitForNotify(3000);
        assertNotNull(outgoing);
        assertFalse(requestAccepted);
    }

    protected String getClientContainerName() {
        return XMPP_CONTAINER;
    }

    private ISendFileTransferContainerAdapter getOutgoingFileTransfer(int client) {
        return (ISendFileTransferContainerAdapter) getClient(client).getAdapter(ISendFileTransferContainerAdapter.class);
    }

    protected IFileID createFileID(ISendFileTransferContainerAdapter adapter, ID clientID, String filename) throws FileCreateException {
        return FileIDFactory.getDefault().createFileID(adapter.getOutgoingNamespace(), new Object[] { clientID, filename });
    }

    protected ID getServerConnectID(int client) {
        try {
            return IDFactory.getDefault().createID(getClient(client).getConnectNamespace(), getUsername(client));
        } catch (final IDCreateException e) {
            e.printStackTrace(System.err);
            fail("Could not create server connect ID");
            return null;
        }
    }
}
