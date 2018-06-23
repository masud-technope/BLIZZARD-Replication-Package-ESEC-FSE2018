/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.updatesite;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.http.HttpContext;

public class UpdateSiteContext implements HttpContext {

    URL updateSiteDirectory;

    HttpContext defaultContext;

    public  UpdateSiteContext(HttpContext defaultContext, URL url) {
        this.updateSiteDirectory = url;
        this.defaultContext = defaultContext;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
	 */
    public String getMimeType(String name) {
        return defaultContext.getMimeType(name);
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
	 */
    public URL getResource(String name) {
        try {
            while (//$NON-NLS-1$
            name.startsWith("/")) name = name.substring(1);
            return new URL(updateSiteDirectory, name);
        } catch (final MalformedURLException e) {
            return updateSiteDirectory;
        }
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.http.HttpContext#handleSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return defaultContext.handleSecurity(request, response);
    }
}
