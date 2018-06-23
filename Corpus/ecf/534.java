/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.filetransfer.efs;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase;

/**
 *
 */
public class RetrieveTest extends AbstractRetrieveTestCase {

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

    protected void handleStartEvent(IIncomingFileTransferReceiveStartEvent event) {
        super.handleStartEvent(event);
        try {
            event.receive(tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
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

    public void testRetrieve() throws Exception {
        URL url = new File("test.txt").toURI().toURL();
        super.testRetrieve(new URL("efs:" + url.toString()));
        waitForDone(5000);
        assertHasEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
        assertHasMoreThanEventCount(dataEvents, IIncomingFileTransferReceiveDataEvent.class, 0);
        assertHasEvent(doneEvents, IIncomingFileTransferReceiveDoneEvent.class);
        assertTrue(tmpFile.exists());
        assertTrue(tmpFile.length() > 0);
    }
}
