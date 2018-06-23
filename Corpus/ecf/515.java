/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic;

import org.eclipse.ecf.core.identity.ID;

/**
 * @since 6.0
 */
public class SSLSimpleGenericServer extends SSLAbstractGenericServer {

    public  SSLSimpleGenericServer(String host, int port) {
        super(host, port);
    }

    protected void handleDisconnect(ID targetId) {
    // nothing
    }

    protected void handleEject(ID targetId) {
    // nothing
    }
}
