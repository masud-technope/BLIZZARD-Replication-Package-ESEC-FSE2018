/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rest;

import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;

public class RestNamespaceTest extends TestCase {

    public void testCreation() {
        String desc = "description";
        RestNamespace namespace = new RestNamespace(RestNamespace.NAME, desc);
        assertEquals(RestNamespace.NAME, namespace.getName());
        assertEquals(desc, namespace.getDescription());
    }

    public void testCreateInstance() {
        RestNamespace namespace = new RestNamespace(RestNamespace.NAME, null);
        Object[] parameters;
        try {
            String url = "http://www.twitter.com";
            parameters = new Object[] { new URL(url) };
            ID id = namespace.createInstance(parameters);
            assertEquals(url, id.getName());
        } catch (MalformedURLException e) {
            fail();
        }
    }

    public void testGetScheme() {
        RestNamespace namespace = new RestNamespace(RestNamespace.NAME, null);
        assertEquals(RestNamespace.SCHEME, namespace.getScheme());
    }
}
