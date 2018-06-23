/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.client;

import org.eclipse.ecf.remoteservice.client.*;

public abstract class AbstractSoapClientService extends AbstractClientService {

    public  AbstractSoapClientService(AbstractClientContainer container, RemoteServiceClientRegistration registration) {
        super(container, registration);
    }
}
