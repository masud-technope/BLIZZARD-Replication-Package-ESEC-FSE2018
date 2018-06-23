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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 * @author Erkki
 */
public class MultipartRequest extends WebRequest {

    // private static final Logger log =
    // Logger.getLogger(MultipartRequest.class);
    private ArrayList<Part> parts;

    public  MultipartRequest(HttpClient client, URL url, String appendPath) {
        super(client);
        this.parts = new ArrayList<Part>();
        this.method = new PostMethod(url.toString() + appendPath);
        // TODO: the comment below -- was it specific to the deprecated
        // MultipartPostMethod?
        // For some reason this ended up causing an infinite loop.
        // When uploading avatar @ idle thumbs.
        method.setFollowRedirects(false);
    // log.debug("Multipart POST request: " + method.getPath());
    }

    public void setParameters(NameValuePair[] params) {
        parts.clear();
        for (NameValuePair pair : params) {
            parts.add(new StringPart(pair.getName(), pair.getValue()));
        }
    }

    public void addParameters(NameValuePair[] params) {
        for (NameValuePair pair : params) {
            parts.add(new StringPart(pair.getName(), pair.getValue()));
        }
    }

    public void addParameter(NameValuePair param) {
        parts.add(new StringPart(param.getName(), param.getValue()));
    }

    public void addFileParameter(String name, File file) throws FileNotFoundException {
        parts.add(new FilePart(name, file));
    }

    /**
	 * TODO: make sure we are using the MultipartRequestEntity in the correct
	 * manner. The deprecated MultipartPostMethod was very different.
	 * @throws IOException 
	 */
    @Override
    public void execute() throws IOException {
        ((PostMethod) method).setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), method.getParams()));
        super.execute();
    }
}
