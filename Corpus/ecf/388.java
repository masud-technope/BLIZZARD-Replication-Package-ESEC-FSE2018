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

public class ShareResponse implements IResponse {

    private static final long serialVersionUID = -7783203563333880201L;

    private final boolean ok;

    public  ShareResponse(boolean ok) {
        this.ok = ok;
    }

    public Object getResponse() {
        return ok ? Boolean.TRUE : Boolean.FALSE;
    }

    public String toString() {
        //$NON-NLS-1$
        return "ShareResponse[ok=" + ok + ']';
    }
}
