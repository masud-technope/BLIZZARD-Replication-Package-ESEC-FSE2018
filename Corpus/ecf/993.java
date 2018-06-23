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
package org.eclipse.ecf.tests.filetransfer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;

public class URLCancelTest extends AbstractRetrieveTestCase {

    private static final String HTTP_RETRIEVE = "http://www.eclipse.org/ecf/ip_log.html";

    private static final String HTTPS_RETRIEVE = URLRetrieveTest.HTTPS_RETRIEVE;

    File tmpFile = null;

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        tmpFile = File.createTempFile("ECFTest", "");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (tmpFile != null)
            tmpFile.delete();
        tmpFile = null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#handleStartEvent(org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent)
	 */
    protected void handleStartEvent(IIncomingFileTransferReceiveStartEvent event) {
        super.handleStartEvent(event);
        assertNotNull(event.getFileID());
        assertNotNull(event.getFileID().getFilename());
        try {
            incomingFileTransfer = event.receive(tmpFile);
        } catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#handleDataEvent(org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent)
	 */
    protected void handleDataEvent(IIncomingFileTransferReceiveDataEvent event) {
        super.handleDataEvent(event);
        if (incomingFileTransfer != null && incomingFileTransfer.getPercentComplete() > 0.50) {
            incomingFileTransfer.cancel();
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#handleDoneEvent(org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent)
	 */
    protected void handleDoneEvent(IIncomingFileTransferReceiveDoneEvent event) {
        super.handleDoneEvent(event);
        if (incomingFileTransfer != null)
            assertTrue(incomingFileTransfer.getException() != null);
    }

    protected void testReceive(String url) throws Exception {
        assertNotNull(retrieveAdapter);
        final IFileTransferListener listener = createFileTransferListener();
        final IFileID fileID = createFileID(new URL(url));
        retrieveAdapter.sendRetrieveRequest(fileID, listener, null);
        waitForDone(20000);
    }

    public void testReceiveFile() throws Exception {
        testReceive(HTTP_RETRIEVE);
    }

    public void testHttpsReceiveFile() throws Exception {
        testReceive(HTTPS_RETRIEVE);
    }
}
