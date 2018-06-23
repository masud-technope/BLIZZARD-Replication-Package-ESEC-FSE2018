/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.tests.presence;

public abstract class AbstractSearchTest extends AbstractPresenceTestCase {

    public static final int CLIENT_COUNT = 1;

    public static final int WAITTIME = 3000;

    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(CLIENT_COUNT);
        clients = createClients();
    }
}
