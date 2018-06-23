/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.presence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;

/**
 * 
 */
public abstract class AbstractChatTest extends AbstractPresenceTestCase {

    IChatManager chat0, chat1 = null;

    public static final int SLEEPTIME = new Integer(System.getProperty("org.eclipse.ecf.tests.presence.AbstractChatTest.SLEEPTIME", "5000")).intValue();

    List receivedChatMessages = new ArrayList();

    IIMMessageListener listener = new IIMMessageListener() {

        public void handleMessageEvent(IIMMessageEvent messageEvent) {
            if (messageEvent instanceof IChatMessageEvent) {
                final IChatMessage chatmessage = ((IChatMessageEvent) messageEvent).getChatMessage();
                System.out.println("received chat message=" + chatmessage);
                receivedChatMessages.add(chatmessage);
            }
        }
    };

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.presence.AbstractPresenceTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(2);
        clients = createClients();
        chat0 = getPresenceAdapter(0).getChatManager();
        chat1 = getPresenceAdapter(1).getChatManager();
        chat1.addMessageListener(listener);
        for (int i = 0; i < 2; i++) {
            connectClient(i);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        receivedChatMessages.clear();
        disconnectClients();
    }

    public void testSendIM() throws Exception {
        chat0.getChatMessageSender().sendChatMessage(getClient(1).getConnectedID(), "abcdef");
        sleep(SLEEPTIME);
        assertHasEvent(receivedChatMessages, IChatMessage.class);
        final IChatMessage message = (IChatMessage) receivedChatMessages.get(0);
        assertTrue(message.getBody().equals("abcdef"));
        assertTrue(message.getType().equals(IChatMessage.Type.CHAT));
        assertNull(message.getSubject());
        final Map props = message.getProperties();
        assertNotNull(props);
        assertTrue(props.size() == 0);
    }

    public void testSendIM2() throws Exception {
        final Map sendprops = new HashMap();
        sendprops.put("prop1", "this");
        final ID sendthreadid = IDFactory.getDefault().createStringID("thread1");
        // Send the whole thing
        chat0.getChatMessageSender().sendChatMessage(getClient(1).getConnectedID(), sendthreadid, IChatMessage.Type.CHAT, "subject1", "uvwxyz", sendprops);
        sleep(SLEEPTIME);
        assertHasEvent(receivedChatMessages, IChatMessage.class);
        final IChatMessage message = (IChatMessage) receivedChatMessages.get(0);
        // For some reason, the smack library doesn't seem to get this right.
        // assertTrue(message.getThreadID().equals(sendthreadid));
        assertTrue(message.getSubject().equals("subject1"));
        assertTrue(message.getBody().equals("uvwxyz"));
        assertTrue(message.getType().equals(IChatMessage.Type.CHAT));
        final Map props = message.getProperties();
        assertNotNull(props);
        assertTrue(props.size() == 1);
        final String val = (String) props.get("prop1");
        assertNotNull(val);
        assertEquals(val, "this");
    }

    public void testSendMessageProperties() throws Exception {
        final Map sendprops = new HashMap();
        sendprops.put("prop2", "that");
        // Send the whole thing
        chat0.getChatMessageSender().sendChatMessage(getClient(1).getConnectedID(), null, IChatMessage.Type.CHAT, null, null, sendprops);
        sleep(SLEEPTIME);
        assertHasEvent(receivedChatMessages, IChatMessage.class);
        final IChatMessage message = (IChatMessage) receivedChatMessages.get(0);
        assertNull(message.getSubject());
        assertTrue(message.getBody().equals(""));
        assertTrue(message.getType().equals(IChatMessage.Type.CHAT));
        final Map props = message.getProperties();
        assertNotNull(props);
        assertTrue(props.size() == 1);
        final String val = (String) props.get("prop2");
        assertNotNull(val);
        assertEquals(val, "that");
    }
}
