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
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;

public class GetRemoteFileNameTest extends AbstractRetrieveTestCase {

    private static final String HTTP_RETRIEVE = "http://www.jtricks.com/download-unknown";

    protected static final String HTTP2_RETRIEVE = "http://www.jtricks.com/download-text";

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

    private boolean done = false;

    private Object lock = new Object();

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#handleStartEvent(org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent)
	 */
    protected void handleStartEvent(IIncomingFileTransferReceiveStartEvent event) {
        super.handleStartEvent(event);
        assertNotNull(event.getFileID());
        assertNotNull(event.getFileID().getFilename());
        try {
            incomingFileTransfer = event.receive(tmpFile);
            synchronized (lock) {
                done = true;
                lock.notify();
            }
        } catch (final IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    protected void testReceive(String url) throws Exception {
        assertNotNull(retrieveAdapter);
        final IFileTransferListener listener = createFileTransferListener();
        final IFileID fileID = createFileID(new URL(url));
        done = false;
        retrieveAdapter.sendRetrieveRequest(fileID, listener, null);
        synchronized (lock) {
            try {
                lock.wait(7000);
            } catch (InterruptedException e) {
            }
        }
        if (!done)
            fail();
        System.out.println("remote file name=" + incomingFileTransfer.getRemoteFileName());
        waitForDone(10000);
        assertHasEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
        assertHasMoreThanEventCount(dataEvents, IIncomingFileTransferReceiveDataEvent.class, 0);
        assertDoneOK();
        assertTrue(tmpFile.exists());
        assertTrue(tmpFile.length() > 0);
    }

    public void testReceiveFile() throws Exception {
        testReceive(HTTP_RETRIEVE);
    }

    public void testHttp2ReceiveFile() throws Exception {
        testReceive(HTTP2_RETRIEVE);
    }
}
