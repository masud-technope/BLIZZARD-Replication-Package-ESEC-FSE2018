/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import javax.servlet.http.HttpServlet;

public class RemoteServiceHttpServlet extends HttpServlet {

    private static final long serialVersionUID = -871598533602636840L;

    private IRemoteCallParameterDeserializer parameterDeserializer;

    private IRemoteCallResponseSerializer responseSerializer;

    protected void setRemoteCallParameterDeserializer(IRemoteCallParameterDeserializer parameterDeserializer) {
        this.parameterDeserializer = parameterDeserializer;
    }

    protected void setRemoteCallResponseSerializer(IRemoteCallResponseSerializer responseSerializer) {
        this.responseSerializer = responseSerializer;
    }

    protected IRemoteCallParameterDeserializer getRemoteCallParameterDeserializer() {
        return this.parameterDeserializer;
    }

    protected IRemoteCallResponseSerializer getRemoteCallResponseSerializer() {
        return this.responseSerializer;
    }
}
