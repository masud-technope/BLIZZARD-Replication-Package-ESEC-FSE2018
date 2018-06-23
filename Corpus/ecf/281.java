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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomInfo;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomParticipantListener;

/**
 * 
 */
public abstract class AbstractChatRoomParticipantTest extends AbstractPresenceTestCase {

    IChatRoomContainer chatRoomContainer0, chatRoomContainer1 = null;

    public static final int WAITTIME = 20000;

    public static final String CHAT_ROOM_NAME = System.getProperty("chat.room.name");

    protected IChatRoomParticipantListener participantListener0 = new IChatRoomParticipantListener() {

        public void handleArrived(IUser participant) {
            System.out.println("0.handleArrived(" + participant + ")");
        }

        public void handleDeparted(IUser participant) {
            System.out.println("0.handleDeparted(" + participant + ")");
        }

        public void handlePresenceUpdated(ID fromID, IPresence presence) {
            System.out.println("0.handlePresenceUpdated(" + fromID + "," + presence + ")");
        }

        public void handleUpdated(IUser updatedParticipant) {
            System.out.println("0.handleUpdated(" + updatedParticipant + ")");
        }
    };

    protected IChatRoomParticipantListener participantListener1 = new IChatRoomParticipantListener() {

        public void handleArrived(IUser participant) {
            System.out.println("1.handleArrived(" + participant + ")");
        }

        public void handleDeparted(IUser participant) {
            System.out.println("1.handleDeparted(" + participant + ")");
        }

        public void handlePresenceUpdated(ID fromID, IPresence presence) {
            System.out.println("0.handlePresenceUpdated(" + fromID + "," + presence + ")");
        }

        public void handleUpdated(IUser updatedParticipant) {
            System.out.println("1.handleUpdated(" + updatedParticipant + ")");
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
        IChatRoomManager chat0, chat1;
        chat0 = getPresenceAdapter(0).getChatRoomManager();
        chat1 = getPresenceAdapter(1).getChatRoomManager();
        for (int i = 0; i < getClientCount(); i++) {
            connectClient(i);
        }
        final IChatRoomInfo roomInfo0 = chat0.getChatRoomInfo(CHAT_ROOM_NAME);
        if (roomInfo0 == null)
            return;
        chatRoomContainer0 = roomInfo0.createChatRoomContainer();
        chatRoomContainer0.addChatRoomParticipantListener(participantListener0);
        chatRoomContainer0.connect(roomInfo0.getRoomID(), null);
        final IChatRoomInfo roomInfo1 = chat1.getChatRoomInfo(CHAT_ROOM_NAME);
        chatRoomContainer1 = roomInfo1.createChatRoomContainer();
        chatRoomContainer1.addChatRoomParticipantListener(participantListener1);
        chatRoomContainer1.connect(roomInfo1.getRoomID(), null);
        Thread.sleep(2000);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        disconnectClients();
    }

    public void testGetChatRoomParticipants0() throws Exception {
        if (chatRoomContainer0 == null)
            return;
        final ID[] participants = chatRoomContainer0.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == getClientCount());
    }

    public void testGetChatRoomParticipants1() throws Exception {
        if (chatRoomContainer1 == null)
            return;
        final ID[] participants = chatRoomContainer1.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == getClientCount());
    }

    public void testGetChatRoomParticipants2() throws Exception {
        if (chatRoomContainer0 == null)
            return;
        chatRoomContainer0.disconnect();
        Thread.sleep(2000);
        final ID[] participants = chatRoomContainer1.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == (getClientCount() - 1));
    }

    public void testGetChatRoomParticipants3() throws Exception {
        if (chatRoomContainer1 == null)
            return;
        chatRoomContainer1.disconnect();
        final ID[] participants = chatRoomContainer1.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == 0);
    }

    public void testGetChatRoomParticipants4() throws Exception {
        if (chatRoomContainer1 == null)
            return;
        chatRoomContainer1.disconnect();
        Thread.sleep(2000);
        final ID[] participants = chatRoomContainer0.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == (getClientCount() - 1));
    }

    public void testGetChatRoomParticipants5() throws Exception {
        if (chatRoomContainer0 == null)
            return;
        chatRoomContainer0.disconnect();
        final ID[] participants = chatRoomContainer0.getChatRoomParticipants();
        assertNotNull(participants);
        assertTrue(participants.length == 0);
    }
}
