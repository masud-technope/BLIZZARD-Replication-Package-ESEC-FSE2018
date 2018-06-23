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

import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;

public class URLRetrieveTestWithConnectJob extends URLRetrieveTest {

    protected void handleStartConnectEvent(IFileTransferConnectStartEvent event) {
        super.handleStartConnectEvent(event);
        assertNotNull(event.getFileID());
        assertNotNull(event.getFileID().getFilename());
        FileTransferJob connectJob = event.prepareConnectJob(null);
        connectJob.addJobChangeListener(new JobChangeTraceListener(startTime));
        event.connectUsingJob(connectJob);
    }
}
