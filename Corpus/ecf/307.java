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
package org.eclipse.ecf.tests.discovery;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public abstract class DiscoveryTestHelper {

    public static final int WEIGHT = 43;

    public static final int PRIORITY = 42;

    public static final String SERVICENAME = "aServiceNAME";

    public static final String NAMINGAUTHORITY = "someNamingAuthority";

    public static final String SCOPE = "someScope";

    public static final String PROTOCOL = "someProtocol";

    public static final int PORT = 3282;

    public static final String USERNAME = System.getProperty("user.name", "testuser");

    public static final String PASSWORD = "testpassword";

    public static final String PATH = "/a/Path/to/Something";

    public static final String QUERY = "someQuery";

    public static final String FRAGMENT = "aFragment";

    public static final String[] SERVICES = new String[] { "ecf", "junit", "tests" };

    public static final String[] PROTOCOLS = new String[] { PROTOCOL };

    public static final String SERVICE_TYPE = "_" + SERVICES[0] + "._" + SERVICES[1] + "._" + SERVICES[2] + "._" + PROTOCOL + "." + SCOPE + "._" + NAMINGAUTHORITY;

    //TODO change to something different than DEFAULT_TTL
    public static final long TTL = 3600;

    public static String HOSTNAME;

    public static URI createDefaultURI(String aHostname) {
        //		return URI.create(PROTOCOL + "://" + USERNAME + ":" + PASSWORD + "@" + aHostname + ":" + PORT + "/" + PATH + "?" + QUERY + "#" + FRAGMENT);
        return URI.create(PROTOCOLS[0] + "://" + /* + USERNAME + "@" */
        aHostname + ":" + PORT + PATH);
    }

    static {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            HOSTNAME = "127.0.0.1";
        }
    }
}
