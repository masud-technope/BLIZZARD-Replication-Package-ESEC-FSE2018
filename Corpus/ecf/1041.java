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
package org.eclipse.ecf.provider.comm;

/**
 * Disconnection event
 * 
 */
public class DisconnectEvent extends ConnectionEvent {

    Throwable exception = null;

    public  DisconnectEvent(IAsynchConnection conn, Throwable e, Object data) {
        super(conn, data);
        exception = e;
    }

    public Throwable getException() {
        return exception;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("DisconnectEvent[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buf.append("conn=").append(getConnection()).append(";").append("e=").append(getException());
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("data=").append(getData()).append("]");
        return buf.toString();
    }
}
