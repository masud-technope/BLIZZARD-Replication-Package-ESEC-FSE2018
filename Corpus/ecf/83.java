/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.client;

import org.eclipse.ecf.remoteservice.client.IRemoteCallableRequestType;
import java.util.Map;

public abstract class AbstractRequestType implements IRemoteCallableRequestType {

    protected Map defaultRequestHeaders;

    public  AbstractRequestType(Map defaultRequestHeaders) {
        this.defaultRequestHeaders = defaultRequestHeaders;
    }

    public  AbstractRequestType() {
    // nothing to do
    }

    public Map getDefaultRequestHeaders() {
        return defaultRequestHeaders;
    }
}
