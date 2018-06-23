/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.sharedobject;

import java.io.IOException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;

public class SendSharedObjectMessageTest extends AbstractSharedObjectTest {

    public static final String SERVER_NAME = "ecftcp://localhost:5889/server";

    public static final String TEST_USERNAME0 = "slewis";

    private static final int MESSAGE_SEND_COUNT = 10;

    ID sharedObjectID;

    TestMessagingSharedObject sharedObject;

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
        // Add test messaging shared object
        sharedObjectID = addClientSharedObject(0, IDFactory.getDefault().createStringID("foo0"), new TestMessagingSharedObject(TEST_USERNAME0, new IMessageReceiver() {

            public void handleMessage(ID fromID, Object message) {
                System.out.println("received fromId=" + fromID + " message=" + message);
            }
        }), null);
        sharedObject = (TestMessagingSharedObject) getClientSOManager(0).getSharedObject(sharedObjectID);
        sleep(2000);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        cleanUpServerAndClients();
        sharedObjectID = null;
        sharedObject = null;
    }

    private void testMessageSend(String message) throws IOException {
        sharedObject.sendMessage(null, message);
        sleep(1000);
    }

    public void testMessageSend() throws Exception {
        for (int i = 0; i < MESSAGE_SEND_COUNT; i++) {
            testMessageSend("greetings program");
        }
    }
}
