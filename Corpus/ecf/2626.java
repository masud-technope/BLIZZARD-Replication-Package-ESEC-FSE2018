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

import org.eclipse.ecf.provider.xmpp.identity.XMPPID;

public interface IExampleService {

    public XMPPID getClientID();
}
