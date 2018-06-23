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

import java.util.Map;
import org.eclipse.ecf.remoteservice.client.IRemoteCallableRequestType;

public class SoapCallableRequestType implements IRemoteCallableRequestType {

    private Map options;

    public  SoapCallableRequestType() {
    // nothing
    }

    public  SoapCallableRequestType(Map options) {
        this.options = options;
    }

    public Map getOptions() {
        return options;
    }
}
