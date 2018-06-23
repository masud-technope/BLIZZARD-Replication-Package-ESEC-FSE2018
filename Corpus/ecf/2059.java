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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Erkki
 */
public class GetRequest extends WebRequest {

    /**
	 * Implement our own params list so we can ADD params to the GET method.
	 */
    private List<NameValuePair> params;

    public  GetRequest(HttpClient client, URL url, String appendPath) {
        super(client);
        this.method = new GetMethod(url.toString() + appendPath);
        // log.debug("GET request: " + method.getPath());
        this.params = new ArrayList<NameValuePair>();
    }

    public void setParameters(NameValuePair[] params) {
        this.params = Arrays.asList(params);
    }

    public void addParameters(NameValuePair[] params) {
        this.params.addAll(Arrays.asList(params));
    }

    public void addParameter(NameValuePair param) {
        this.params.add(param);
    }

    @Override
    public void execute() throws IOException {
        method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        super.execute();
    }
}
