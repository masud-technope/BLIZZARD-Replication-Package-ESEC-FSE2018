/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl;

import ch.ethz.iks.slp.ServiceURL;
import junit.framework.TestCase;

public class ServiceURLTest extends TestCase {

    public  ServiceURLTest() {
        super("ServiceURLTest");
        System.setProperty("net.slp.port", "10427");
    }

    public void testServiceURL1() throws Exception {
        String urlString = "service:test:myservice://localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL2() throws Exception {
        String urlString = "service:test:myservice://localhost:80";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals("service:test:myservice", url.getServiceType().toString());
        assertEquals("localhost", url.getHost());
        assertEquals(80, url.getPort());
        assertEquals("", url.getURLPath());
        assertEquals("", url.getUserInfo());
        assertEquals(null, url.getProtocol());
        assertEquals(urlString, url.toString());
    }

    public void testServiceURL3() throws Exception {
        String urlString = "service:test:myservice://localhost:80/path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 80);
        assertEquals(url.getURLPath(), "/path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL4() throws Exception {
        String urlString = "service:test:myservice://localhost/my/path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "/my/path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL5() throws Exception {
        String urlString = "service:test:myservice://http://localhost:8080/my/path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 8080);
        assertEquals(url.getURLPath(), "/my/path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL6() throws Exception {
        String urlString = "service:test://http://localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURLNamingAuthorityCustom() throws Exception {
        String urlString = "service:test.foo://http://localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test.foo");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), urlString);
        assertTrue("foo".equals(url.getServiceType().getNamingAuthority()));
    }

    public void testServiceURLNamingAuthorityDefault() throws Exception {
        String urlString = "service:test://http://localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), urlString);
        assertTrue("".equals(url.getServiceType().getNamingAuthority()));
    }

    public void testServiceURLNamingAuthorityIana() throws Exception {
        String urlString = "service:test.iana://http://localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), "service:test://http://localhost");
        assertTrue("".equals(url.getServiceType().getNamingAuthority()));
    }

    public void testServiceURLUserInfo() throws Exception {
        String urlString = "service:test.iana://http://foobar@localhost";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "");
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.toString(), "service:test://http://foobar@localhost");
        assertEquals(url.getUserInfo(), "foobar");
        assertTrue("".equals(url.getServiceType().getNamingAuthority()));
    }

    // https://bugs.eclipse.org/258252
    public void testServiceURL258252a() throws Exception {
        String urlString = "service:test:myservice://localhost:80/my:path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 80);
        assertEquals(url.getURLPath(), "/my:path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL258252b() throws Exception {
        String urlString = "service:test:myservice://localhost/my:path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "/my:path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }

    public void testServiceURL258252c() throws Exception {
        String urlString = "service:test:myservice://localhost/foo/bar#path";
        ServiceURL url = new ServiceURL(urlString, 0);
        assertEquals(url.getServiceType().toString(), "service:test:myservice");
        assertEquals(url.getHost(), "localhost");
        assertEquals(url.getPort(), 0);
        assertEquals(url.getURLPath(), "/foo/bar#path");
        assertEquals(url.getUserInfo(), "");
        assertEquals(url.getProtocol(), null);
        assertEquals(url.toString(), urlString);
    }
}
