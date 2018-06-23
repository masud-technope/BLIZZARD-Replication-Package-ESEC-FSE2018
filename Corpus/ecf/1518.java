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

import org.eclipse.ecf.core.identity.ID;

public class FetchVariantsRequest extends FetchVariantRequest {

    private static final long serialVersionUID = -5776703885952265394L;

    public  FetchVariantsRequest(ID fromId, String path, int type) {
        super(fromId, path, type);
    }
}
