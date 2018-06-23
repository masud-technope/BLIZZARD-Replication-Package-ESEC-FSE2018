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

public class AsynchMessage implements Serializable {

    private static final long serialVersionUID = 3258689905679873075L;

    Serializable data;

    /**
	 * @since 4.3
	 */
    public  AsynchMessage() {
    //
    }

    /**
	 * @param data data for message
	 * @since 4.3
	 */
    public  AsynchMessage(Serializable data) {
        this.data = data;
    }

    /**
	 * @return Serializable data from this message
	 * @since 4.3
	 */
    public Serializable getData() {
        return data;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("AsynchMessage[");
        //$NON-NLS-1$
        buf.append(data).append("]");
        return buf.toString();
    }
}
