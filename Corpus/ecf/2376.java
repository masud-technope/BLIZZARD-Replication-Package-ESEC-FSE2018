/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.dnssd;

import java.net.URI;
import java.util.Properties;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;

public class DnsSdTestHelper {

    public static final String PORT = "80";

    public static final String PATH = "/";

    public static final String DOMAIN_A_RECORD = "www.ecf-project.org";

    public static final String NAMING_AUTH = "iana";

    public static final String PROTO = "tcp";

    public static final String SCHEME = "http";

    public static final String ECF_DISCOVERY_DNSSD = "ecf.discovery.dnssd";

    public static final String DOMAIN = "dns-sd.ecf-project.org";

    public static final String DNS_RESOLVER = "8.8.8.8";

    public static final String DNS_SERVER = "188.40.102.175";

    public static final String TSIG_KEY = "EdoBE6i8YeIiFKiKEGC9LaI4X+C44Rdd6Nwh7ZvBi6tCpszvkartLoPjVYKoJ4RdY4hnFV3zAKp1d1t2MToxUQ==";

    public static final String TSIG_KEY_NAME = "junit.ecf-project.org";

    public static final String REG_DOMAIN = "dnssd2.ecf-project.org";

    public static final String REG_SCHEME = "junit";

    public static final long TTL = 600;

    public static IServiceInfo createServiceInfo(Namespace namespace) {
        final Properties props = new Properties();
        final URI uri = URI.create(SCHEME + "://" + DOMAIN_A_RECORD + ":" + PORT + PATH);
        IServiceTypeID serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(namespace, new String[] { SCHEME }, new String[] { DOMAIN }, new String[] { PROTO }, NAMING_AUTH);
        final ServiceProperties serviceProperties = new ServiceProperties(props);
        serviceProperties.setPropertyString("path", PATH);
        serviceProperties.setPropertyString("dns-sd.ptcl", SCHEME);
        return new ServiceInfo(uri, DOMAIN_A_RECORD, serviceTypeID, 10, 0, serviceProperties);
    }
}
