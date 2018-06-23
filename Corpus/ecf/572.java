/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.bulletinboard.commons.webapp;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;

/**
 * @author Erkki
 */
public abstract class WebRequest {

    protected HttpClient client;

    protected HttpMethod method;

    public  WebRequest(HttpClient client) {
        super();
        this.client = client;
    }

    public abstract void setParameters(NameValuePair params[]);

    public abstract void addParameters(NameValuePair params[]);

    public abstract void addParameter(NameValuePair param);

    public void execute() throws IOException {
        try {
            client.executeMethod(method);
        } finally {
            client.getState().purgeExpiredCookies();
        }
    }

    public final HttpMethod getMethod() {
        return method;
    }

    public final void releaseConnection() {
        method.releaseConnection();
    }

    public final byte[] getResponseBody() throws IOException {
        return method.getResponseBody();
    }

    public final String getResponseBodyAsString() throws IOException {
        return method.getResponseBodyAsString();
    }

    public final InputStream getResponseBodyAsStream() throws IOException {
        return method.getResponseBodyAsStream();
    }

    public HttpClient getHttpClient() {
        return this.client;
    }
}
