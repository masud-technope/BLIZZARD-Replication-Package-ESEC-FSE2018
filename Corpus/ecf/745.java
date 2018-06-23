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
package org.eclipse.ecf.tests.sharedobject;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;

/**
 *
 */
public class AddSharedObjectTest extends AbstractSharedObjectTest {

    public static final String SERVER_NAME = "ecftcp://localhost:5888/server";

    public static final String TEST_USERNAME0 = "slewis";

    public static final String TEST_USERNAME1 = "luca";

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#getClientCount()
	 */
    protected int getClientCount() {
        return 1;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        createServerAndClients();
        connectClients();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanUpServerAndClients();
    }

    public void testAddSharedObject() throws Exception {
        final ISharedObjectManager manager = getClientSOManager(0);
        assertNotNull(manager);
        final ID id = manager.addSharedObject(IDFactory.getDefault().createStringID("foo"), new TestSharedObject(TEST_USERNAME0), null);
        assertNotNull(id);
        final ISharedObject sharedObject = manager.getSharedObject(id);
        assertNotNull(sharedObject);
        sleep(1000);
    }

    public void testAddTwoSharedObjects() throws Exception {
        final ISharedObjectManager manager = getClientSOManager(0);
        assertNotNull(manager);
        final ID id0 = manager.addSharedObject(IDFactory.getDefault().createStringID("foo0"), new TestSharedObject(TEST_USERNAME0), null);
        assertNotNull(id0);
        final ID id1 = manager.addSharedObject(IDFactory.getDefault().createStringID("foo1"), new TestSharedObject(TEST_USERNAME1), null);
        assertNotNull(id1);
        final ISharedObject sharedObject0 = manager.getSharedObject(id0);
        assertNotNull(sharedObject0);
        final ISharedObject sharedObject1 = manager.getSharedObject(id1);
        assertNotNull(sharedObject1);
        sleep(1000);
    }
}
