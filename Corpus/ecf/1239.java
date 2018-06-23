/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *   Composent, Inc - Simplifications
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import org.eclipse.ecf.remoteservice.RemoteCall;

/**
 * Implementation of {@link IRestCall}.  Note that {@link RestCallFactory} should 
 * typically be used to construct instances.
 */
public class RestCall extends RemoteCall implements IRestCall, Serializable {

    private static final long serialVersionUID = -2688657222934833060L;

    private Map requestHeaders;

    public  RestCall(String fqMethod, Object[] params, Map requestHeaders, long timeout) {
        super(fqMethod, params, timeout);
        this.requestHeaders = requestHeaders;
    }

    public  RestCall(String fqMethod, Object[] params, Map requestHeaders) {
        this(fqMethod, params, requestHeaders, IRestCall.DEFAULT_TIMEOUT);
    }

    public  RestCall(String fqMethod, Object[] params) {
        this(fqMethod, params, null);
    }

    public  RestCall(String fqMethod) {
        this(fqMethod, null);
    }

    public Map getRequestHeaders() {
        return requestHeaders;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("RestCall[requestHeaders=");
        buffer.append(requestHeaders);
        //$NON-NLS-1$
        buffer.append(", method=");
        buffer.append(method);
        //$NON-NLS-1$
        buffer.append(", parameters=");
        buffer.append(parameters != null ? Arrays.asList(parameters) : null);
        //$NON-NLS-1$
        buffer.append(", timeout=");
        buffer.append(timeout);
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }
}
