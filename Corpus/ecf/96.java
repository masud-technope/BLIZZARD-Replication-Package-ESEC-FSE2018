/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery;

import java.net.URI;
import java.util.Comparator;
import java.util.Properties;
import java.util.Random;
import junit.framework.TestCase;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;

public abstract class AbstractDiscoveryTest extends TestCase {

    public static final String TEST_NAME = "testName";

    private final Random random;

    protected IServiceInfo serviceInfo;

    protected String protocol = DiscoveryTestHelper.PROTOCOL;

    protected String scope = DiscoveryTestHelper.SCOPE;

    protected long ttl = DiscoveryTestHelper.TTL;

    protected String namingAuthority = DiscoveryTestHelper.NAMINGAUTHORITY;

    protected String[] services = DiscoveryTestHelper.SERVICES;

    protected Comparator comparator = new ServiceInfoComparator();

    protected String containerUnderTest;

    protected IDiscoveryLocator discoveryLocator = null;

    protected IDiscoveryAdvertiser discoveryAdvertiser = null;

    private String hostname = DiscoveryTestHelper.HOSTNAME;

    private String testId;

    public  AbstractDiscoveryTest(String name) {
        super();
        this.containerUnderTest = name;
        this.random = new Random();
    }

    public String getTestId() {
        return testId;
    }

    protected abstract IDiscoveryLocator getDiscoveryLocator();

    protected abstract IDiscoveryAdvertiser getDiscoveryAdvertiser();

    protected void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    protected void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    protected void setScope(String scope) {
        this.scope = scope;
    }

    protected void setTTL(long aTTL) {
        this.ttl = aTTL;
    }

    protected void setHostname(String aHostname) {
        this.hostname = aHostname;
    }

    protected void setNamingAuthority(String namingAuthority) {
        this.namingAuthority = namingAuthority;
    }

    protected void setServices(String[] aServices) {
        this.services = aServices;
    }

    protected void setUp() throws Exception {
        super.setUp();
        assertNotNull(containerUnderTest);
        assertTrue(containerUnderTest.startsWith("ecf.discovery."));
        discoveryLocator = getDiscoveryLocator();
        discoveryAdvertiser = getDiscoveryAdvertiser();
        assertNotNull("IDiscoveryLocator must not be null", discoveryLocator);
        assertNotNull("IDiscoveryAdvertiser must not be null", discoveryAdvertiser);
        final Properties props = new Properties();
        final URI uri = DiscoveryTestHelper.createDefaultURI(hostname);
        IServiceTypeID serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(discoveryLocator.getServicesNamespace(), services, new String[] { scope }, new String[] { protocol }, namingAuthority);
        assertNotNull(serviceTypeID);
        final ServiceProperties serviceProperties = new ServiceProperties(props);
        serviceProperties.setPropertyString(TEST_NAME, getName());
        testId = Long.toString(random.nextLong());
        serviceProperties.setPropertyString(getName() + "testIdentifier", testId);
        serviceProperties.setPropertyString(getName() + "servicePropertiesString", "serviceProperties");
        serviceProperties.setProperty(getName() + "servicePropertiesIntegerMax", new Integer(Integer.MIN_VALUE));
        serviceProperties.setProperty(getName() + "servicePropertiesIntegerMin", new Integer(Integer.MAX_VALUE));
        serviceProperties.setProperty(getName() + "servicePropertiesBoolean", new Boolean(false));
        serviceProperties.setPropertyBytes(getName() + "servicePropertiesByte", new byte[] { -127, -126, -125, 0, 1, 2, 3, 'a', 'b', 'c', 'd', 126, 127 });
        serviceInfo = new ServiceInfo(uri, DiscoveryTestHelper.SERVICENAME, serviceTypeID, 1, 1, serviceProperties, ttl);
        assertNotNull(serviceInfo);
    }
}
