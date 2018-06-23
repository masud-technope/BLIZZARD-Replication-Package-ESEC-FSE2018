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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.filetransfer.IRemoteFile;
import org.eclipse.ecf.filetransfer.events.IRemoteFileSystemBrowseEvent;
import org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase;

/**
 *
 */
public class FileBrowseTest extends AbstractBrowseTestCase {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase#handleDirectoryEvent(org.eclipse.ecf.filetransfer.events.IRemoteFileSystemBrowseEvent)
	 */
    protected void handleFileSystemBrowseEvent(IRemoteFileSystemBrowseEvent event) {
        super.handleFileSystemBrowseEvent(event);
        assertNotNull(event);
        final IRemoteFile[] remoteFiles = event.getRemoteFiles();
        assertNotNull(remoteFiles);
        assertTrue(remoteFiles.length > 0);
        verifyRemoteFiles(remoteFiles);
        done = true;
    }

    protected File[] roots;

    protected File[] files;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        roots = File.listRoots();
        final List files = new ArrayList();
        for (int i = 0; i < roots.length; i++) {
            final File[] fs = roots[i].listFiles();
            if (fs != null)
                for (int j = 0; j < fs.length; j++) {
                    if (fs[j].exists())
                        files.add(fs[j]);
                }
        }
        this.files = (File[]) files.toArray(new File[] {});
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractBrowseTestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        this.roots = null;
        this.files = null;
    }

    public void testBrowseRoots() throws Exception {
        for (int i = 0; i < roots.length; i++) {
            if (roots[i].exists())
                testBrowse(new URL("efs:" + roots[i].toURL().toString()));
            Thread.sleep(100);
        }
    }

    public void testFileBrowse() throws Exception {
        for (int i = 0; i < files.length; i++) {
            testBrowse(new URL("efs:" + files[i].toURL().toString()));
            Thread.sleep(100);
        }
    }
}
