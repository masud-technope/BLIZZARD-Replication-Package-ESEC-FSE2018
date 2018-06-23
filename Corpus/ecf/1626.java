/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.server.generic;

import junit.framework.TestCase;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.server.generic.SimpleGenericServer;

public class GenericServerTest extends TestCase {

    private static final int SERVER_PORT = 5555;

    private static final String SERVER_PATH = "/server";

    private static final int SERVER_KEEPALIVE = 30000;

    SimpleGenericServer server;

    protected void setUp() throws Exception {
        super.setUp();
        server = new SimpleGenericServer("localhost", SERVER_PORT);
        server.start(SERVER_PATH, SERVER_KEEPALIVE);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        server.stop();
        server = null;
    }

    public void testGetRemoteServiceContainerAdapter() throws Exception {
        IContainer container = server.getFirstServerContainer();
        assertNotNull(container);
        IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
        assertNotNull(adapter);
    }
}
