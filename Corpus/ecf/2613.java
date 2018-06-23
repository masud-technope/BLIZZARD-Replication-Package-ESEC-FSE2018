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
package org.eclipse.ecf.tests.provider.xmpp.sharedobject;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.tests.presence.sharedobject.AbstractChatRoomSOAddTest;
import org.eclipse.ecf.tests.provider.xmpp.XMPP;
import org.eclipse.ecf.tests.sharedobject.TestSharedObject;

/**
 *
 */
public class ChatRoomSOAddTest extends AbstractChatRoomSOAddTest {

    protected String getClientContainerName() {
        return XMPP.CONTAINER_NAME;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.presence.sharedobject.AbstractChatRoomSOAddTest#createSharedObject(org.eclipse.ecf.core.identity.ID)
	 */
    protected ISharedObject createSharedObject(ID objectID) throws Exception {
        return new TestSharedObject(objectID.getName());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.presence.sharedobject.AbstractChatRoomSOAddTest#createSharedObjectID()
	 */
    protected ID createSharedObjectID() throws Exception {
        return IDFactory.getDefault().createGUID();
    }

    protected void tearDown() throws Exception {
        // This is a possible workaround for what appears to be Smack bug:  https://bugs.eclipse.org/bugs/show_bug.cgi?id=321032
        Thread.sleep(2000);
        super.tearDown();
    }
}
