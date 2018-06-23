/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.rest;

import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.rest.client.AbstractRequestType;
import org.eclipse.ecf.remoteservice.rest.client.HttpGetRequestType;

public class RestCallableFactory {

    public static IRemoteCallable createCallable(String method, String resourcePath, IRemoteCallParameter[] defaultParameters, AbstractRequestType requestType, long defaultTimeout) {
        return new RemoteCallable(method, resourcePath, defaultParameters, requestType, defaultTimeout);
    }

    public static IRemoteCallable createCallable(String method, String resourcePath, IRemoteCallParameter[] defaultParameters, AbstractRequestType requestType) {
        return createCallable(method, resourcePath, defaultParameters, requestType, IRestCall.DEFAULT_TIMEOUT);
    }

    public static IRemoteCallable createCallable(String method, String resourcePath, IRemoteCallParameter[] defaultParameters) {
        return createCallable(method, resourcePath, defaultParameters, new HttpGetRequestType(), IRestCall.DEFAULT_TIMEOUT);
    }

    public static IRemoteCallable createCallable(String method, String resourcePath) {
        return createCallable(method, resourcePath, null, new HttpGetRequestType(), IRestCall.DEFAULT_TIMEOUT);
    }

    public static IRemoteCallable createCallable(String method) {
        return createCallable(method, method, null, new HttpGetRequestType(), IRestCall.DEFAULT_TIMEOUT);
    }

    public static IRemoteCallable createCallable(String method, String resourcePath, AbstractRequestType requestType, long timeout) {
        return createCallable(method, resourcePath, null, requestType, timeout);
    }

    public static IRemoteCallable createCallable(String method, String resourcePath, AbstractRequestType requestType) {
        return createCallable(method, resourcePath, null, requestType, IRestCall.DEFAULT_TIMEOUT);
    }
}
