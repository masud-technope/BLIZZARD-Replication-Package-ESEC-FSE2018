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

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.presence.IAccountManager;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

/**
 *
 */
public abstract class AbstractAdapterAccessTest extends ContainerAbstractTestCase {

    protected abstract String getClientContainerName();

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        clients = createClients();
    }

    protected IPresenceContainerAdapter getPresenceAdapter() {
        return (IPresenceContainerAdapter) getClients()[0].getAdapter(IPresenceContainerAdapter.class);
    }

    public void testGetPresenceContainerAdapter() {
        final IPresenceContainerAdapter adapter = getPresenceAdapter();
        assertNotNull(adapter);
    }

    public void testGetDescriptionsForAdapter() {
        final ContainerTypeDescription[] descs = ContainerFactory.getDefault().getDescriptionsForContainerAdapter(IPresenceContainerAdapter.class);
        assertNotNull(descs);
    }

    public void testGetRosterManager() {
        final IPresenceContainerAdapter adapter = getPresenceAdapter();
        assertNotNull(adapter);
        final IRosterManager rosterManager = adapter.getRosterManager();
        assertNotNull(rosterManager);
    }

    public void testGetAccountManager() {
        final IPresenceContainerAdapter adapter = getPresenceAdapter();
        assertNotNull(adapter);
        final IAccountManager accountManager = adapter.getAccountManager();
        assertNotNull(accountManager);
    }

    public void testGetChatManager() {
        final IPresenceContainerAdapter adapter = getPresenceAdapter();
        assertNotNull(adapter);
        final IChatManager chatManager = adapter.getChatManager();
        assertNotNull(chatManager);
    }

    public void testGetChatRoomManager() {
        final IPresenceContainerAdapter adapter = getPresenceAdapter();
        assertNotNull(adapter);
        final IChatRoomManager chatRoomManager = adapter.getChatRoomManager();
        assertNotNull(chatRoomManager);
    }
}
