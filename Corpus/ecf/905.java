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

import org.eclipse.ecf.core.util.Event;

/**
 * Connection event super class.
 * 
 */
public class ConnectionEvent implements Event {

    private final Object data;

    private final IConnection connection;

    public  ConnectionEvent(IConnection source, Object data) {
        this.connection = source;
        this.data = data;
    }

    public IConnection getConnection() {
        return connection;
    }

    public Object getData() {
        return data;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ConnectionEvent[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buf.append("conn=").append(getConnection()).append(";").append("data=").append(getData());
        //$NON-NLS-1$
        buf.append("]");
        return buf.toString();
    }
}
