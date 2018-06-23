/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rpc;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.rpc.identity.RpcNamespace;

public class RpcNamespaceTest extends AbstractRpcTestCase {

    public void testCreation() {
        String desc = "description";
        RpcNamespace namespace = new RpcNamespace(RpcNamespace.NAME, desc);
        assertEquals(RpcNamespace.NAME, namespace.getName());
        assertEquals(desc, namespace.getDescription());
    }

    public void testCreateInstance() {
        RpcNamespace namespace = new RpcNamespace(RpcNamespace.NAME, null);
        Object[] parameters;
        try {
            String url = RpcConstants.TEST_ECHO_TARGET;
            parameters = new Object[] { new URL(url) };
            ID id = namespace.createInstance(parameters);
            assertEquals(url, id.getName());
        } catch (MalformedURLException e) {
            fail();
        }
    }

    public void testGetScheme() {
        RpcNamespace namespace = new RpcNamespace(RpcNamespace.NAME, null);
        assertEquals(RpcNamespace.SCHEME, namespace.getScheme());
    }
}
