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

import java.net.URL;
import java.net.UnknownHostException;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;

public class URLRetrieveTestUnknownHost extends AbstractRetrieveTestCase {

    public static final String HTTP_UNKNOWN_HOST_URL = "http://unknown-abcdefghi.eclipse.org/foo";

    private boolean CANCEL_SUPPORTED_ON_CONNECT = new Boolean(System.getProperty("org.eclipse.ecf.tests.filetransfer.cancelSupportedOnConnect", "true")).booleanValue();

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void testReceive(String url, IFileTransferListener listener) throws Exception {
        assertNotNull(retrieveAdapter);
        final IFileID fileID = createFileID(new URL(url));
        retrieveAdapter.sendRetrieveRequest(fileID, listener, null);
        waitForDone(10000);
    }

    public void testReceiveFile_unknownHostWithoutConnectJob() throws Exception {
        final IFileTransferListener listener = createFileTransferListener();
        try {
            // path does not matter as server does not respond.
            testReceive(HTTP_UNKNOWN_HOST_URL, listener);
            if (CANCEL_SUPPORTED_ON_CONNECT) {
                assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
            }
            assertHasNoEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
            assertHasNoEvent(dataEvents, IIncomingFileTransferReceiveDataEvent.class);
            assertHasEvent(doneEvents, IIncomingFileTransferReceiveDoneEvent.class);
            assertDoneExceptionBeforeServerResponse(UnknownHostException.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testReceiveFile_unknownHostWithConnectJob() throws Exception {
        final IFileTransferListener listener = createFileTransferListener();
        final FileTransferListenerWrapper lw = new FileTransferListenerWrapper(listener) {

            protected void handleStartConnectEvent(final IFileTransferConnectStartEvent event) {
                assertNotNull(event.getFileID());
                assertNotNull(event.getFileID().getFilename());
                FileTransferJob connectJob = event.prepareConnectJob(null);
                connectJob.addJobChangeListener(new JobChangeTraceListener(startTime));
                event.connectUsingJob(connectJob);
            }
        };
        try {
            // path does not matter as server does not respond.
            testReceive(HTTP_UNKNOWN_HOST_URL, lw);
            if (CANCEL_SUPPORTED_ON_CONNECT) {
                assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
            }
            assertHasNoEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
            assertHasNoEvent(dataEvents, IIncomingFileTransferReceiveDataEvent.class);
            assertDoneExceptionBeforeServerResponse(UnknownHostException.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}
