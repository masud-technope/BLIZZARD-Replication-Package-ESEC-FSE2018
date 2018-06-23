/****************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc., IBM and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    
 *****************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.filetransfer.BrowseFileTransferException;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.IRemoteFileAttributes;
import org.eclipse.ecf.filetransfer.IRemoteFileInfo;
import org.eclipse.ecf.filetransfer.events.IRemoteFileSystemBrowseEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;

/**
 *
 */
public class URLBrowseTest extends AbstractBrowseTestCase {

    public URL[] testURLs = null;

    private List events;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        events = new ArrayList();
        testURLs = new URL[3];
        testURLs[0] = new URL("https://www.eclipse.org/ecf/ip_log.html");
        testURLs[1] = new URL("http://www.eclipse.org/ecf/ip_log.html");
        testURLs[2] = new URL("http://google.com:80");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        testURLs = null;
    }

    protected void handleFileSystemBrowseEvent(IRemoteFileSystemBrowseEvent event) {
        super.handleFileSystemBrowseEvent(event);
        events.add(event);
    }

    public void testBrowseURLs() throws Exception {
        for (int i = 0; i < testURLs.length; i++) {
            testBrowse(testURLs[i]);
            Thread.sleep(10000);
        }
        assertHasEventCount(events, IRemoteFileSystemBrowseEvent.class, 3);
        for (Iterator iterator = events.iterator(); iterator.hasNext(); ) {
            IRemoteFileSystemBrowseEvent event = (IRemoteFileSystemBrowseEvent) iterator.next();
            assertNotNull(event);
            final IRemoteFile[] remoteFiles = event.getRemoteFiles();
            assertNotNull(remoteFiles);
            assertEquals(1, remoteFiles.length);
            if (event.getFileID().getName().equals("http://google.com:80")) {
                verifyRemoteFilesWithoutLastModifiedAndContentLength(remoteFiles);
            } else {
                verifyRemoteFiles(remoteFiles);
            }
        }
    }

    public void testBrowseUnknownHost() throws Exception {
        testBrowse(new URL(URLRetrieveTestUnknownHost.HTTP_UNKNOWN_HOST_URL));
        Thread.sleep(3000);
        assertHasEventCount(events, IRemoteFileSystemBrowseEvent.class, 1);
        IRemoteFileSystemBrowseEvent event = (IRemoteFileSystemBrowseEvent) events.get(0);
        assertNotNull(event);
        final IRemoteFile[] remoteFiles = event.getRemoteFiles();
        assertNull(remoteFiles);
        Exception e = event.getException();
        assertNotNull(e);
        if (e instanceof BrowseFileTransferException) {
            BrowseFileTransferException ifte = (BrowseFileTransferException) e;
            assertTrue(ifte.getCause() instanceof UnknownHostException);
        } else
            fail("Event exception is not instance of BrowseFileTransferException");
    }

    protected void verifyRemoteFilesWithoutLastModifiedAndContentLength(final IRemoteFile[] remoteFiles) {
        for (int i = 0; i < remoteFiles.length; i++) {
            final IRemoteFile first = remoteFiles[i];
            final IRemoteFileInfo firstInfo = first.getInfo();
            assertNotNull(firstInfo);
            final IFileID firstID = first.getID();
            assertNotNull(firstID);
            trace("firstID=" + firstID);
            // Now check out info
            assertNotNull(firstInfo.getName());
            //TODO: should this be -1
            assertEquals(0, firstInfo.getLastModified());
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
