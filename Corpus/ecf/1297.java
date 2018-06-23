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

import java.net.URL;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Erkki
 */
public class PostRequest extends WebRequest {

    public  PostRequest(HttpClient client, URL url, String appendPath) {
        super(client);
        this.method = new PostMethod(url.toString() + appendPath);
    // log.debug("POST request: " + method.getPath());
    }

    public void setParameters(NameValuePair[] params) {
        ((PostMethod) method).setRequestBody(params);
    }

    public void addParameters(NameValuePair[] params) {
        ((PostMethod) method).addParameters(params);
    }

    public void addParameter(NameValuePair param) {
        ((PostMethod) method).addParameter(param);
    }
}
