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
package org.eclipse.ecf.tests.provider.xmpp;

import org.eclipse.ecf.tests.presence.AbstractChatRoomParticipantTest;

/**
 *
 */
public class ChatRoomParticipantTest extends AbstractChatRoomParticipantTest {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.presence.AbstractPresenceTestCase#getClientContainerName()
	 */
    protected String getClientContainerName() {
        return XMPP.CONTAINER_NAME;
    }

    protected void tearDown() throws Exception {
        // This is a possible workaround for what appears to be Smack bug:  https://bugs.eclipse.org/bugs/show_bug.cgi?id=321032
        Thread.sleep(2000);
        super.tearDown();
    }
}
