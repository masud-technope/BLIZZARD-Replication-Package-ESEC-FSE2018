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
package org.eclipse.ecf.example.collab.share;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.events.RemoteSharedObjectEvent;

public class RemoteSharedObjectMsgEvent extends RemoteSharedObjectEvent {

    private static final long serialVersionUID = -7198080945310388254L;

    /**
	 * @param senderObj
	 * @param remoteCont
	 * @param msg
	 */
    public  RemoteSharedObjectMsgEvent(ID senderObj, ID remoteCont, SharedObjectMsg msg) {
        super(senderObj, remoteCont, msg);
    }

    public SharedObjectMsg getMsg() {
        return (SharedObjectMsg) super.getData();
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("RemoteSharedObjectMsgEvent[");
        //$NON-NLS-1$
        buf.append(getSenderSharedObjectID()).append(";").append(getRemoteContainerID()).append(//$NON-NLS-1$
        ";").append(//$NON-NLS-1$
        getMsg());
        //$NON-NLS-1$
        buf.append("]");
        return buf.toString();
    }
}
