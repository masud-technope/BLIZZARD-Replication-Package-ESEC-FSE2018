/*******************************************************************************
* Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.osgi.services.distribution.generic;

import org.eclipse.ecf.tests.osgi.services.distribution.AbstractTwoRemoteServiceAccessTest;

public class GenericTwoRemoteServiceAccessTest extends AbstractTwoRemoteServiceAccessTest {

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(1);
        createServerAndClients();
        connectClients();
        setupRemoteServiceAdapters();
    }

    protected void tearDown() throws Exception {
        cleanUpServerAndClients();
        super.tearDown();
    }

    protected String getClientContainerName() {
        return "ecf.generic.client";
    }
}
