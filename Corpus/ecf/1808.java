/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.presence.sharedobject;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomInfo;
import org.eclipse.ecf.tests.presence.AbstractPresenceTestCase;

/**
 *
 */
public abstract class AbstractChatRoomSOAddTest extends AbstractPresenceTestCase {

    public static final String CHAT_ROOM_NAME = System.getProperty("chat.room.name");

    IChatRoomContainer[] chatRoomContainer;

    protected int getClientCount() {
        return 2;
    }

    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(2);
        chatRoomContainer = new IChatRoomContainer[2];
        clients = createClients();
        for (int i = 0; i < 2; i++) {
            connectClient(i);
            final IChatRoomInfo info = getPresenceAdapter(i).getChatRoomManager().getChatRoomInfo(CHAT_ROOM_NAME);
            if (info == null) {
                chatRoomContainer[i] = null;
            } else {
                chatRoomContainer[i] = info.createChatRoomContainer();
                chatRoomContainer[i].connect(info.getRoomID(), null);
            }
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        cleanUpClients();
    }

    protected abstract ID createSharedObjectID() throws Exception;

    protected abstract ISharedObject createSharedObject(ID objectID) throws Exception;

    public void testAddSharedObject() throws Exception {
        if (chatRoomContainer[0] == null)
            return;
        final IContainer client0Container = chatRoomContainer[0];
        assertNotNull(client0Container);
        // 
        final ISharedObjectContainer socontainer = (ISharedObjectContainer) client0Container.getAdapter(ISharedObjectContainer.class);
        final ISharedObjectManager manager = socontainer.getSharedObjectManager();
        assertNotNull(manager);
        final ID objectID = createSharedObjectID();
        final ID id = manager.addSharedObject(objectID, createSharedObject(objectID), null);
        assertNotNull(id);
        final ISharedObject sharedObject = manager.getSharedObject(id);
        assertNotNull(sharedObject);
        sleep(5000);
    }

    public void testAddSharedObject1() throws Exception {
        if (chatRoomContainer[0] == null)
            return;
        final IContainer client0Container = chatRoomContainer[1];
        assertNotNull(client0Container);
        // 
        final ISharedObjectContainer socontainer = (ISharedObjectContainer) client0Container.getAdapter(ISharedObjectContainer.class);
        final ISharedObjectManager manager = socontainer.getSharedObjectManager();
        assertNotNull(manager);
        final ID objectID = createSharedObjectID();
        final ID id = manager.addSharedObject(objectID, createSharedObject(objectID), null);
        assertNotNull(id);
        final ISharedObject sharedObject = manager.getSharedObject(id);
        assertNotNull(sharedObject);
        sleep(5000);
    }

    public void testAddTwoSharedObjects() throws Exception {
        if (chatRoomContainer[0] == null)
            return;
        final IContainer client0Container = chatRoomContainer[0];
        assertNotNull(client0Container);
        final ISharedObjectContainer socontainer = (ISharedObjectContainer) client0Container.getAdapter(ISharedObjectContainer.class);
        final ISharedObjectManager manager = socontainer.getSharedObjectManager();
        assertNotNull(manager);
        final ID objectID0 = createSharedObjectID();
        final ID id0 = manager.addSharedObject(objectID0, createSharedObject(objectID0), null);
        assertNotNull(id0);
        final ID objectID1 = createSharedObjectID();
        final ID id1 = manager.addSharedObject(objectID1, createSharedObject(objectID1), null);
        assertNotNull(id1);
        final ISharedObject sharedObject0 = manager.getSharedObject(id0);
        assertNotNull(sharedObject0);
        final ISharedObject sharedObject1 = manager.getSharedObject(id1);
        assertNotNull(sharedObject1);
        sleep(5000);
    }

    public void testAddTwoSharedObjects1() throws Exception {
        if (chatRoomContainer[0] == null)
            return;
        final IContainer client0Container = chatRoomContainer[1];
        assertNotNull(client0Container);
        final ISharedObjectContainer socontainer = (ISharedObjectContainer) client0Container.getAdapter(ISharedObjectContainer.class);
        final ISharedObjectManager manager = socontainer.getSharedObjectManager();
        assertNotNull(manager);
        final ID objectID0 = createSharedObjectID();
        final ID id0 = manager.addSharedObject(objectID0, createSharedObject(objectID0), null);
        assertNotNull(id0);
        final ID objectID1 = createSharedObjectID();
        final ID id1 = manager.addSharedObject(objectID1, createSharedObject(objectID1), null);
        assertNotNull(id1);
        final ISharedObject sharedObject0 = manager.getSharedObject(id0);
        assertNotNull(sharedObject0);
        final ISharedObject sharedObject1 = manager.getSharedObject(id1);
        assertNotNull(sharedObject1);
        sleep(5000);
    }
}
