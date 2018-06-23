/****************************************************************************
 * Copyright (c) 2009 IBM, Inc., Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.core.util.ProxyAddress;

public class URLRetrieveTestProxy extends URLRetrieveTest {

    // This test depends upon the setting of two system properties:
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyHost=<proxy host name>
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyPort=<proxy port>
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyUsername=<username for proxy authentication>
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyPassword=<password for proxy authentication>
    // e.g.
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyHost=myproxy.foo.com
    // org.eclipse.ecf.tests.filetransfer.URLRetrieveTestProxy.proxyPort=8888
    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        //				"localhost", 909)));
        try {
            String proxyName = System.getProperty(this.getClass().getName() + ".proxyHost");
            if (proxyName != null) {
                String pPort = System.getProperty(this.getClass().getName() + ".proxyPort");
                int proxyPort = ((pPort != null) ? Integer.parseInt(pPort) : 9808);
                String username = System.getProperty(this.getClass().getName() + ".proxyUsername");
                if (username != null) {
                    String password = System.getProperty(this.getClass().getName() + ".proxyPassword");
                    retrieveAdapter.setProxy(new Proxy(Proxy.Type.HTTP, new ProxyAddress(proxyName, proxyPort), username, password));
                } else {
                    retrieveAdapter.setProxy(new Proxy(Proxy.Type.HTTP, new ProxyAddress(proxyName, proxyPort)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        retrieveAdapter.setProxy(null);
        super.tearDown();
    }
}
