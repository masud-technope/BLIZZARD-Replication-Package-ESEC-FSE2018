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
import junit.framework.TestCase;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;

/**
 *
 */
public class FileIDFactoryTest extends TestCase {

    protected FileIDFactory factory;

    protected IRetrieveFileTransferContainerAdapter getRetrieveAdapter() throws Exception {
        return (IRetrieveFileTransferContainerAdapter) ContainerFactory.getDefault().createContainer().getAdapter(IRetrieveFileTransferContainerAdapter.class);
    }

    protected ISendFileTransferContainerAdapter getSendAdapter() throws Exception {
        return (ISendFileTransferContainerAdapter) ContainerFactory.getDefault().createContainer().getAdapter(ISendFileTransferContainerAdapter.class);
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        factory = FileIDFactory.getDefault();
        super.setUp();
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        factory = null;
        super.tearDown();
    }

    public void testIDFactory() {
        assertNotNull(factory);
    }

    protected IFileID createFileID(Namespace ns, URL url) throws Exception {
        assertNotNull(ns);
        assertNotNull(url);
        return FileIDFactory.getDefault().createFileID(ns, url);
    }

    protected IFileID createFileID(Namespace ns, String url) throws Exception {
        assertNotNull(ns);
        assertNotNull(url);
        return FileIDFactory.getDefault().createFileID(ns, url);
    }

    protected IFileID createFileID(Namespace ns, Object[] args) throws Exception {
        assertNotNull(ns);
        return FileIDFactory.getDefault().createFileID(ns, args);
    }

    public void testCreateFromURL() throws Exception {
        final IFileID fileID = createFileID(getRetrieveAdapter().getRetrieveNamespace(), new URL("http://www.eclipse.org/ecf"));
        assertNotNull(fileID);
    }

    public void testCreateFromString() throws Exception {
        final IFileID fileID = createFileID(getRetrieveAdapter().getRetrieveNamespace(), "http://www.eclipse.org/ecf");
        assertNotNull(fileID);
    }

    public void testCreateFromObjectArray() throws Exception {
        final IFileID fileID = createFileID(getRetrieveAdapter().getRetrieveNamespace(), new Object[] { "http://www.eclipse.org/ecf" });
        assertNotNull(fileID);
    }
}
