/****************************************************************************
 * Copyright (c) 2010 Eugen Reiswich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eugen Reiswich - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.xmpp.remoteservice;

import java.io.Serializable;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;

public class ExampleService implements IExampleService, Serializable {

    private static final long serialVersionUID = -333552101728391955L;

    private final XMPPID xmppID;

    public  ExampleService(XMPPID xmppID) {
        this.xmppID = xmppID;
    }

    public XMPPID getClientID() {
        return xmppID;
    }
}
