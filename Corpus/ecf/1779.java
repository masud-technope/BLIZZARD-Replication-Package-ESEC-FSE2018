/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.distribution.r_osgi.wss;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.tests.osgi.services.distribution.AbstractRemoteServiceRegisterTest;

public class R_OSGiWSSRemoteServiceRegisterTest extends AbstractRemoteServiceRegisterTest {

    private static final String CONTAINER_TYPE_NAME = "ecf.r_osgi.peer.wss";

    private static final String SERVER_IDENTITY = "r-osgi.wss://localhost";

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(1);
        createServerAndClients();
        setupRemoteServiceAdapters();
    }

    protected void tearDown() throws Exception {
        cleanUpServerAndClients();
        super.tearDown();
    }

    protected int getClientCount() {
        return 0;
    }

    protected IContainer createServer() throws Exception {
        return ContainerFactory.getDefault().createContainer(CONTAINER_TYPE_NAME, SERVER_IDENTITY);
    }

    protected String getClientContainerName() {
        return CONTAINER_TYPE_NAME;
    }

    protected String getServerIdentity() {
        return SERVER_IDENTITY;
    }

    protected String getServerContainerTypeName() {
        return CONTAINER_TYPE_NAME;
    }
}
