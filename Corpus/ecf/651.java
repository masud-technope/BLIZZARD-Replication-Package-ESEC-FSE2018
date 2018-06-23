/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.jslp.identity;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.internal.provider.jslp.ServicePropertiesAdapter;
import org.eclipse.ecf.internal.provider.jslp.ServiceURLAdapter;
import org.eclipse.ecf.provider.jslp.container.JSLPServiceInfo;
import org.eclipse.ecf.provider.jslp.identity.JSLPNamespace;
import org.eclipse.ecf.tests.discovery.DiscoveryTestHelper;
import org.eclipse.ecf.tests.discovery.identity.ServiceIDTest;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

public class JSLPServiceIDTest extends ServiceIDTest {

    public  JSLPServiceIDTest() {
        super(JSLPNamespace.NAME);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.identity.ServiceIDTest#testCreateServiceTypeIDFromInternalString()
	 */
    public void testCreateServiceTypeIDWithProviderSpecificString() {
        final String internalRep = "service:foo.eclipse:bar";
        IServiceTypeID stid = (IServiceTypeID) new JSLPNamespace().createInstance(new Object[] { internalRep });
        assertEquals(internalRep, stid.getInternal());
        assertTrue(stid.getName().startsWith("_foo._bar"));
        assertTrue(stid.getName().endsWith("._eclipse"));
        assertEquals("eclipse", stid.getNamingAuthority());
        assertTrue(Arrays.equals(new String[] { "foo", "bar" }, stid.getServices()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_SCOPE, stid.getScopes()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_PROTO, stid.getProtocols()));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.identity.ServiceIDTest#testCreateServiceTypeIDFromInternalString()
	 * 
	 * test from ECF discovery -> jSLP
	 */
    public void testRemoveServicePrefixECFtojSLP() throws ServiceLocationException {
        final IServiceTypeID stid = (IServiceTypeID) createIDFromString(DiscoveryTestHelper.SERVICE_TYPE);
        assertEquals("service:" + DiscoveryTestHelper.SERVICES[0] + "." + DiscoveryTestHelper.NAMINGAUTHORITY + ":" + DiscoveryTestHelper.SERVICES[1] + ":" + DiscoveryTestHelper.SERVICES[2], stid.getInternal());
        assertEquals(DiscoveryTestHelper.SERVICE_TYPE, stid.getName());
        assertEquals(DiscoveryTestHelper.NAMINGAUTHORITY, stid.getNamingAuthority());
        assertTrue(Arrays.equals(DiscoveryTestHelper.SERVICES, stid.getServices()));
        assertTrue(Arrays.equals(new String[] { DiscoveryTestHelper.SCOPE }, stid.getScopes()));
        assertTrue(Arrays.equals(new String[] { DiscoveryTestHelper.PROTOCOL }, stid.getProtocols()));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.identity.ServiceIDTest#testCreateServiceTypeIDFromInternalString()
	 * 
	 * test from jSLP -> ECF discovery which needs to remove the first occurrence of "service:"
	 */
    public void testCreateByjSLPAndRemoveServicePrefix() throws ServiceLocationException {
        final String internalRep = "service:foo.eclipse:bar";
        final ServiceURL sUrl = new ServiceURL(internalRep + "://localhost:1234/a/path/to/something", ServiceURL.LIFETIME_PERMANENT);
        final IServiceInfo serviceInfo = new JSLPServiceInfo(DiscoveryTestHelper.SERVICENAME, new ServiceURLAdapter(sUrl), DiscoveryTestHelper.PRIORITY, DiscoveryTestHelper.WEIGHT, new ServicePropertiesAdapter(new ArrayList()));
        assertEquals(serviceInfo.getPriority(), DiscoveryTestHelper.PRIORITY);
        assertEquals(serviceInfo.getWeight(), DiscoveryTestHelper.WEIGHT);
        final IServiceID sid = serviceInfo.getServiceID();
        assertEquals(serviceInfo.getServiceName(), DiscoveryTestHelper.SERVICENAME);
        final IServiceTypeID stid = sid.getServiceTypeID();
        String internal = stid.getInternal();
        assertEquals(internalRep, internal);
        assertEquals("_foo._bar._" + IServiceTypeID.DEFAULT_PROTO[0] + "." + IServiceTypeID.DEFAULT_SCOPE[0] + "._eclipse", stid.getName());
        assertEquals("eclipse", stid.getNamingAuthority());
        assertTrue(Arrays.equals(new String[] { "foo", "bar" }, stid.getServices()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_SCOPE, stid.getScopes()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_PROTO, stid.getProtocols()));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.identity.ServiceIDTest#testRemoveServicePrefixWithServiceService()
	 * 
	 * test from jSLP -> ECF discovery which needs to remove the first occurrence of "service:"
	 */
    public void testCreateByjSLPAndRemoveServicePrefixWithServiceService() throws ServiceLocationException {
        final String internalRep = "service:service.eclipse:foo:bar";
        final ServiceURL sUrl = new ServiceURL(internalRep + "://localhost:1234/a/path/to/something", ServiceURL.LIFETIME_PERMANENT);
        final IServiceInfo serviceInfo = new JSLPServiceInfo(DiscoveryTestHelper.SERVICENAME, new ServiceURLAdapter(sUrl), DiscoveryTestHelper.PRIORITY, DiscoveryTestHelper.WEIGHT, new ServicePropertiesAdapter(new ArrayList()));
        assertEquals(serviceInfo.getPriority(), DiscoveryTestHelper.PRIORITY);
        assertEquals(serviceInfo.getWeight(), DiscoveryTestHelper.WEIGHT);
        final IServiceID sid = serviceInfo.getServiceID();
        assertEquals(serviceInfo.getServiceName(), DiscoveryTestHelper.SERVICENAME);
        final IServiceTypeID stid = sid.getServiceTypeID();
        assertEquals(internalRep, stid.getInternal());
        assertEquals("_service._foo._bar._" + IServiceTypeID.DEFAULT_PROTO[0] + "." + IServiceTypeID.DEFAULT_SCOPE[0] + "._eclipse", stid.getName());
        assertEquals("eclipse", stid.getNamingAuthority());
        assertTrue(Arrays.equals(new String[] { "service", "foo", "bar" }, stid.getServices()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_SCOPE, stid.getScopes()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_PROTO, stid.getProtocols()));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.identity.ServiceIDTest#testCreateServiceTypeIDFromInternalString()
	 */
    public void testCreateServiceTypeIDFromSLPStringWithDefaultNamingAuthority() {
        final String internalRep = "service:foo.iana:bar";
        final IServiceTypeID stid = (IServiceTypeID) new JSLPNamespace().createInstance(new Object[] { internalRep });
        // the internalRep contains "iana" but getInternal may not!
        final int indexOf = stid.getInternal().toLowerCase().indexOf("iana");
        assertTrue(indexOf == -1);
        assertEquals(IServiceTypeID.DEFAULT_NA, stid.getNamingAuthority());
        assertNotSame(internalRep, stid.getName());
    }

    public void testECFDefaultsTojSLP() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        IServiceTypeID stid = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, DiscoveryTestHelper.SERVICES, DiscoveryTestHelper.PROTOCOLS);
        assertNotNull(stid);
        assertTrue(Arrays.equals(DiscoveryTestHelper.SERVICES, stid.getServices()));
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_SCOPE, stid.getScopes()));
        assertTrue(Arrays.equals(DiscoveryTestHelper.PROTOCOLS, stid.getProtocols()));
        String internal = stid.getInternal();
        assertEquals("service:" + DiscoveryTestHelper.SERVICES[0] + ":" + DiscoveryTestHelper.SERVICES[1] + ":" + DiscoveryTestHelper.SERVICES[2], internal);
    }

    public void testjSLPDefaultsToECF() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        IServiceTypeID stid = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, DiscoveryTestHelper.SERVICES, new String[] { DiscoveryTestHelper.SCOPE }, DiscoveryTestHelper.PROTOCOLS, DiscoveryTestHelper.NAMINGAUTHORITY);
        assertNotNull(stid);
        assertEquals(DiscoveryTestHelper.NAMINGAUTHORITY, stid.getNamingAuthority());
        assertEquals("_ecf._junit._tests._someProtocol." + DiscoveryTestHelper.SCOPE + "._" + DiscoveryTestHelper.NAMINGAUTHORITY, stid.getName());
    }

    public void testjSLPDefaultsToECF2() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        IServiceTypeID stid = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, DiscoveryTestHelper.SERVICES, DiscoveryTestHelper.PROTOCOLS);
        assertNotNull(stid);
        assertEquals(IServiceTypeID.DEFAULT_NA, stid.getNamingAuthority());
        assertEquals("_ecf._junit._tests._someProtocol." + IServiceTypeID.DEFAULT_SCOPE[0] + "._" + IServiceTypeID.DEFAULT_NA, stid.getName());
    }
}
