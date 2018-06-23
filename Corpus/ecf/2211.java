/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.core.messages;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;

public class FetchVariantRequest implements Serializable {

    private static final long serialVersionUID = -1617853402478189227L;

    private final ID fromId;

    private final String path;

    private final int type;

    public  FetchVariantRequest(ID fromId, String path, int type) {
        this.fromId = fromId;
        this.path = path;
        this.type = type;
    }

    public ID getFromId() {
        return fromId;
    }

    public int getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("FetchVariantRequest[");
        synchronized (buffer) {
            //$NON-NLS-1$
            buffer.append("id=").append(fromId);
            //$NON-NLS-1$
            buffer.append(";path=").append(path);
            //$NON-NLS-1$
            buffer.append(";type=").append(type).append(']');
            return buffer.toString();
        }
    }
}
