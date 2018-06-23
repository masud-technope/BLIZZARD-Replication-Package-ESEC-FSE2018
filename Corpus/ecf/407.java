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
package org.eclipse.ecf.provider.comm.tcp;

import java.io.Serializable;

public class ConnectResultMessage implements Serializable {

    private static final long serialVersionUID = 3833188038300938804L;

    Serializable data;

    public  ConnectResultMessage(Serializable data) {
        this.data = data;
    }

    public Serializable getData() {
        return data;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ConnectResultMessage[");
        //$NON-NLS-1$
        buf.append(data).append("]");
        return buf.toString();
    }
}
