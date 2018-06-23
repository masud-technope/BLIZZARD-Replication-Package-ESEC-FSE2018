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
package org.eclipse.ecf.tests.provider.dnssd;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.provider.dnssd.DnsSdNamespace;
import org.eclipse.ecf.tests.discovery.DiscoveryServiceTest;

/**
 * In order for this unit test to succeed, the system search path has to include "dns-sd.ecf-project.org".
 */
public class DnsSdDiscoveryServiceTest extends DiscoveryServiceTest {

    public  DnsSdDiscoveryServiceTest() {
        this(DnsSdTestHelper.ECF_DISCOVERY_DNSSD + ".locator", DnsSdTestHelper.DOMAIN, DnsSdTestHelper.SCHEME, DnsSdTestHelper.PROTO);
    }

    public  DnsSdDiscoveryServiceTest(String aContainer, String aDomain, String aService, String aProtocol) {
        super(aContainer);
        setNamingAuthority(DnsSdTestHelper.NAMING_AUTH);
        setScope(aDomain);
        setServices(new String[] { aService });
        setProtocol(aProtocol);
        setComparator(new DnsSdDiscoveryComparator());
        eventsToExpect = 7;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        serviceInfo = DnsSdTestHelper.createServiceInfo(discoveryLocator.getServicesNamespace());
        assertNotNull(serviceInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testAddServiceListenerIServiceListener()
	 */
    public void testAddServiceListenerIServiceListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testAddServiceListenerIServiceTypeIDIServiceListener()
	 */
    public void testAddServiceListenerIServiceTypeIDIServiceListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testAddServiceTypeListener()
	 */
    public void testAddServiceTypeListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#testAddServiceListenerIServiceListenerOSGi()
	 */
    public void testAddServiceListenerIServiceListenerOSGi() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#testAddServiceListenerIServiceTypeIDIServiceListenerOSGi()
	 */
    public void testAddServiceListenerIServiceTypeIDIServiceListenerOSGi() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#testAddServiceListenerIServiceTypeIDIServiceListenerOSGiWildcards()
	 */
    public void testAddServiceListenerIServiceTypeIDIServiceListenerOSGiWildcards() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#testAddServiceTypeListenerOSGi()
	 */
    public void testAddServiceTypeListenerOSGi() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testRegisterService()
	 */
    public void testRegisterService() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testRemoveServiceListenerIServiceListener()
	 */
    public void testRemoveServiceListenerIServiceListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testRemoveServiceListenerIServiceTypeIDIServiceListener()
	 */
    public void testRemoveServiceListenerIServiceTypeIDIServiceListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testRemoveServiceTypeListener()
	 */
    public void testRemoveServiceTypeListener() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testUnregisterService()
	 */
    public void testUnregisterService() throws ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testGetAsyncServiceInfo()
	 */
    public void testGetAsyncServiceInfo() throws OperationCanceledException, InterruptedException, ContainerConnectException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testGetAsyncServices()
	 */
    public void testGetAsyncServices() throws ContainerConnectException, OperationCanceledException, InterruptedException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testGetAsyncServicesIServiceTypeID()
	 */
    public void testGetAsyncServicesIServiceTypeID() throws ContainerConnectException, OperationCanceledException, InterruptedException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#testGetAsyncServiceTypes()
	 */
    public void testGetAsyncServiceTypes() throws ContainerConnectException, OperationCanceledException, InterruptedException {
    // NOP, not applicable for DNS-SD
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#getDiscoveryAdvertiser()
	 */
    protected IDiscoveryAdvertiser getDiscoveryAdvertiser() {
        return new IDiscoveryAdvertiser() {

            /* (non-Javadoc)
			 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
			 */
            public Object getAdapter(Class adapter) {
                return null;
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
			 */
            public void unregisterService(IServiceInfo serviceInfo) {
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterAllServices()
			 */
            public void unregisterAllServices() {
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#registerService(org.eclipse.ecf.discovery.IServiceInfo)
			 */
            public void registerService(IServiceInfo serviceInfo) {
            }

            /* (non-Javadoc)
			 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#getServicesNamespace()
			 */
            public Namespace getServicesNamespace() {
                return new DnsSdNamespace();
            }
        };
    }
}
