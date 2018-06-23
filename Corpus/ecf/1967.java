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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.util.ISharedObjectMessageSerializer;

public class SendSharedObjectMessageWithCustomSerializerTest extends AbstractSharedObjectTest {

    public static final String SERVER_NAME = "ecftcp://localhost:5890/server";

    public static final String TEST_USERNAME0 = "slewis";

    private static final int MESSAGE_SEND_COUNT = 10;

    ID sharedObjectID;

    TestMessagingSharedObject sharedObject;

    protected int getClientCount() {
        return 1;
    }

    protected byte[] serialize(Object o) throws IOException {
        System.out.println("serialize message=" + o);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        oos.flush();
        return bos.toByteArray();
    }

    protected Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        System.out.println("deserialize data=" + data);
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bins);
        return ois.readObject();
    }

    protected void setUp() throws Exception {
        super.setUp();
        createServerAndClients();
        connectClients();
        // Setup custom serializer
        ISharedObjectContainer soContainer = getClientSOContainer(0);
        soContainer.setSharedObjectMessageSerializer(new ISharedObjectMessageSerializer() {

            public Object deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
                return deserialize(data);
            }

            public byte[] serializeMessage(ID sharedObjectId, Object message) throws IOException {
                return serialize(message);
            }
        });
        ISharedObjectContainer serverContainer = getServerSOContainer();
        serverContainer.setSharedObjectMessageSerializer(new ISharedObjectMessageSerializer() {

            public Object deserializeMessage(byte[] data) throws IOException, ClassNotFoundException {
                return deserialize(data);
            }

            public byte[] serializeMessage(ID sharedObjectId, Object message) throws IOException {
                return serialize(message);
            }
        });
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
