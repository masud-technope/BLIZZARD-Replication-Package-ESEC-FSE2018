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

import java.util.Arrays;
import java.util.List;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.provider.dnssd.DnsSdNamespace;
import org.eclipse.ecf.provider.dnssd.DnsSdServiceTypeID;

public class DnsSdAdvertiserInternalTest extends DnsSdAbstractDiscoveryTest {

    private static final String dnssd4 = "dnssd4.ecf-project.org";

    private static final String dnssd5 = "dnssd5.ecf-project.org";

    private static final String dnssd6 = "dnssd6.ecf-project.org";

    private static final String dnssd7 = "dnssd7.ecf-project.org";

    private static final String dnssd8 = "dnssd8.ecf-project.org";

    public  DnsSdAdvertiserInternalTest() {
        super();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.AbstractDiscoveryTest#getDiscoveryLocator()
	 */
    protected IDiscoveryAdvertiser getDiscoveryAdvertiser() {
        final TestDnsSdDiscoveryAdvertiser advertiser = new TestDnsSdDiscoveryAdvertiser();
        try {
            advertiser.connect(null, null);
        } catch (ContainerConnectException e) {
            e.printStackTrace();
        }
        return advertiser;
    }

    /**
	 * Test that delegate domains work
	 * 
	 * dnssd4 -> @
	 * 		  -> dnssd5 -> dnssd6
	 * 
	 * must return {dnssd4, dnssd5, dnssd6}
	 */
    public void testBrowsingDelegates() {
        final DnsSdNamespace namespace = new DnsSdNamespace();
        // only scope matters
        final DnsSdServiceTypeID aServiceTypeId = new DnsSdServiceTypeID(namespace, "_foo._bar._udp." + dnssd4 + "._iana");
        final String[] registrationDomains = ((TestDnsSdDiscoveryAdvertiser) discoveryAdvertiser).getRegistrationDomains(aServiceTypeId);
        assertTrue(registrationDomains.length == 3);
        final List list = Arrays.asList(registrationDomains);
        assertTrue(list.contains(dnssd4 + "."));
        assertTrue(list.contains(dnssd5 + "."));
        assertTrue(list.contains(dnssd6 + "."));
    }

    /**
	 * Test that delegate domains work
	 * 
	 * dnssd5 -> dnssd6
	 * 
	 * must return {dnssd6}
	 */
    public void testBrowsingDelegates2() {
        final DnsSdNamespace namespace = new DnsSdNamespace();
        // only scope matters
        final DnsSdServiceTypeID aServiceTypeId = new DnsSdServiceTypeID(namespace, "_foo._bar._udp." + dnssd5 + "._iana");
        final String[] registrationDomains = ((TestDnsSdDiscoveryAdvertiser) discoveryAdvertiser).getRegistrationDomains(aServiceTypeId);
        assertTrue(registrationDomains.length == 2);
        final List list = Arrays.asList(registrationDomains);
        assertTrue(list.contains(dnssd5 + "."));
        assertTrue(list.contains(dnssd6 + "."));
    }

    /**
	 * Test that delegate domains work
	 * 
	 * dnssd6 has no browsing domain set
	 * 
	 * must return {dnssd6}
	 */
    public void testBrowsingDelegates3() {
        final DnsSdNamespace namespace = new DnsSdNamespace();
        // only scope matters
        final DnsSdServiceTypeID aServiceTypeId = new DnsSdServiceTypeID(namespace, "_foo._bar._udp." + dnssd6 + "._iana");
        final String[] registrationDomains = ((TestDnsSdDiscoveryAdvertiser) discoveryAdvertiser).getRegistrationDomains(aServiceTypeId);
        assertTrue(registrationDomains.length == 1);
        final List list = Arrays.asList(registrationDomains);
        assertTrue(list.contains(dnssd6 + "."));
    }

    /**
	 * Test that delegate domains work
	 * 
	 * dnssd7 and dnssd8 both point to each other
	 * 
	 * must return {dnssd7, dnssd8}
	 */
    public void testBrowsingDelegatesWithCycles() {
        final DnsSdNamespace namespace = new DnsSdNamespace();
        // only scope matters
        final DnsSdServiceTypeID aServiceTypeId = new DnsSdServiceTypeID(namespace, "_foo._bar._udp." + dnssd7 + "._iana");
        final String[] registrationDomains = ((TestDnsSdDiscoveryAdvertiser) discoveryAdvertiser).getRegistrationDomains(aServiceTypeId);
        assertTrue(registrationDomains.length == 2);
        final List list = Arrays.asList(registrationDomains);
        assertTrue(list.contains(dnssd7 + "."));
        assertTrue(list.contains(dnssd8 + "."));
    }
}
