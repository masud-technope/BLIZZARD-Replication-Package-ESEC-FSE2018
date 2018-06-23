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
package org.eclipse.ecf.tests.provider.filetransfer.scp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.events.*;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;

public class SCPRetrieveTest extends AbstractSCPTest {

    //$NON-NLS-1$ //$NON-NLS-2$
    private String retrieveFile = System.getProperty("retrieveFile", "test.txt");

    private IRetrieveFileTransferContainerAdapter adapter = null;

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        adapter = (IRetrieveFileTransferContainerAdapter) baseContainer.getAdapter(IRetrieveFileTransferContainerAdapter.class);
        receiveStartEvents = new ArrayList();
        receiveDataEvents = new ArrayList();
        receiveDoneEvents = new ArrayList();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        receiveStartEvents.clear();
        receiveDataEvents.clear();
        receiveDoneEvents.clear();
        adapter = null;
    }

    List receiveStartEvents;

    List receiveDataEvents;

    List receiveDoneEvents;

    public void testReceive() throws Exception {
        assertNotNull(adapter);
        final IFileTransferListener listener = new IFileTransferListener() {

            public void handleTransferEvent(IFileTransferEvent event) {
                if (event instanceof IIncomingFileTransferReceiveStartEvent) {
                    IIncomingFileTransferReceiveStartEvent rse = (IIncomingFileTransferReceiveStartEvent) event;
                    receiveStartEvents.add(rse);
                    assertNotNull(rse.getFileID());
                    assertNotNull(rse.getFileID().getFilename());
                    try {
                        rse.receive(System.out);
                    } catch (IOException e) {
                        fail(e.getLocalizedMessage());
                    }
                } else if (event instanceof IIncomingFileTransferReceiveDataEvent) {
                    receiveDataEvents.add(event);
                } else if (event instanceof IIncomingFileTransferReceiveDoneEvent) {
                    receiveDoneEvents.add(event);
                    syncNotify();
                }
            }
        };
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String targetURL = "scp://" + host + (retrieveFile.startsWith("/") ? "" : "/") + retrieveFile;
        //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("Retrieving from " + targetURL + " with username=" + username);
        adapter.setConnectContextForAuthentication(ConnectContextFactory.createUsernamePasswordConnectContext(username, password));
        adapter.sendRetrieveRequest(FileIDFactory.getDefault().createFileID(adapter.getRetrieveNamespace(), targetURL), listener, null);
        syncWaitForNotify(60000);
        assertHasEvent(receiveStartEvents, IIncomingFileTransferReceiveStartEvent.class);
        assertHasMoreThanEventCount(receiveDataEvents, IIncomingFileTransferReceiveDataEvent.class, 0);
        assertHasEvent(receiveDoneEvents, IIncomingFileTransferReceiveDoneEvent.class);
    }

    public void syncNotify() {
        super.syncNotify();
    }
}
