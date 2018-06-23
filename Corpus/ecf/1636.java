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
package org.eclipse.ecf.tests.filetransfer;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.IRemoteFileAttributes;
import org.eclipse.ecf.filetransfer.IRemoteFileInfo;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemBrowserContainerAdapter;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemListener;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemRequest;
import org.eclipse.ecf.filetransfer.events.IRemoteFileSystemBrowseEvent;
import org.eclipse.ecf.filetransfer.events.IRemoteFileSystemEvent;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.equinox.concurrent.future.TimeoutException;

/**
 *
 */
public abstract class AbstractBrowseTestCase extends AbstractFileTransferTestCase {

    protected IRemoteFileSystemBrowserContainerAdapter adapter = null;

    protected Object lock = new Object();

    protected boolean done = false;

    protected IRemoteFileSystemRequest request = null;

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        final IContainer container = ContainerFactory.getDefault().createContainer();
        adapter = (IRemoteFileSystemBrowserContainerAdapter) container.getAdapter(IRemoteFileSystemBrowserContainerAdapter.class);
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        adapter = null;
        if (request != null) {
            request.cancel();
            request = null;
        }
    }

    protected IRemoteFileSystemListener createRemoteFileSystemListener() {
        return new IRemoteFileSystemListener() {

            public void handleRemoteFileEvent(IRemoteFileSystemEvent event) {
                if (event instanceof IRemoteFileSystemBrowseEvent) {
                    handleFileSystemBrowseEvent((IRemoteFileSystemBrowseEvent) event);
                } else
                    handleUnknownEvent(event);
            }
        };
    }

    protected IFileID createFileID(URL directoryOrFile) throws Exception {
        return FileIDFactory.getDefault().createFileID(adapter.getBrowseNamespace(), directoryOrFile);
    }

    protected void testBrowse(URL directoryOrFile) throws Exception {
        Assert.isNotNull(adapter);
        request = adapter.sendBrowseRequest(createFileID(directoryOrFile), createRemoteFileSystemListener());
    }

    /**
		 * @param event
		 */
    protected void handleUnknownEvent(IRemoteFileSystemEvent event) {
        trace("handleUnknownEvent(" + event + ")");
    }

    /**
	 * @param event
	 */
    protected void handleFileSystemBrowseEvent(IRemoteFileSystemBrowseEvent event) {
        trace("handleFileSystemBrowseEvent(" + event + ")");
        if (event.getException() != null) {
            trace(event.getException().toString());
        }
    }

    protected void waitForDone(int timeout) throws Exception {
        final long start = System.currentTimeMillis();
        synchronized (lock) {
            while (!done && ((System.currentTimeMillis() - start) < timeout)) {
                lock.wait(timeout / 20);
            }
            if (!done)
                throw new TimeoutException(timeout);
        }
    }

    protected void assertHasEvent(Collection collection, Class eventType) {
        assertHasEventCount(collection, eventType, 1);
    }

    protected void assertHasEventCount(Collection collection, Class eventType, int eventCount) {
        int count = 0;
        for (final Iterator i = collection.iterator(); i.hasNext(); ) {
            final Object o = i.next();
            if (eventType.isInstance(o))
                count++;
        }
        assertEquals(eventCount, count);
    }

    protected void assertHasMoreThanEventCount(Collection collection, Class eventType, int eventCount) {
        int count = 0;
        for (final Iterator i = collection.iterator(); i.hasNext(); ) {
            final Object o = i.next();
            if (eventType.isInstance(o))
                count++;
        }
        assertTrue(count > eventCount);
    }

    /**
	 * @param remoteFiles
	 */
    protected void verifyRemoteFiles(final IRemoteFile[] remoteFiles) {
        for (int i = 0; i < remoteFiles.length; i++) {
            final IRemoteFile first = remoteFiles[i];
            final IRemoteFileInfo firstInfo = first.getInfo();
            assertNotNull(firstInfo);
            final IFileID firstID = first.getID();
            assertNotNull(firstID);
            trace("firstID=" + firstID);
            // Now check out info
            assertNotNull(firstInfo.getName());
            assertTrue(firstInfo.getLastModified() > 0);
            trace("lastModified=" + new SimpleDateFormat().format(new Date(firstInfo.getLastModified())));
            trace("length=" + firstInfo.getLength());
            trace("isDirectory=" + firstInfo.isDirectory());
            final IRemoteFileAttributes attributes = firstInfo.getAttributes();
            assertNotNull(attributes);
            final Iterator attrNames = attributes.getAttributeKeys();
            for (; attrNames.hasNext(); ) {
                final String key = (String) attrNames.next();
                String s = "attrname=" + key;
                s += " attrvalue=" + attributes.getAttribute(key);
                trace(s);
            }
        }
    }
}
