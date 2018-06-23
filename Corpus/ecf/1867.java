/*******************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.internal.discovery.DiscoveryNamespace;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class DiscoveryServiceRegistryTest extends DiscoveryServiceTest {

    protected ServiceRegistration registerService;

    protected IServiceInfo genericServiceInfo;

    public  DiscoveryServiceRegistryTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        // override the service info with a _generic_ service info. Generic in
        // the sense that its namespace if the parent DiscoveryNamespace and not
        // a provider specific one. We need to use the parent DN because users of the
        // whiteboard pattern do not interact with the IDA directly.
        IServiceTypeID serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(IDFactory.getDefault().getNamespaceByName(DiscoveryNamespace.NAME), services, new String[] { scope }, new String[] { protocol }, namingAuthority);
        assertNotNull(serviceTypeID);
        // Register a generic IServiceInfo (discovery generic) in the OSGi
        // service registry. Let all comparison be done on the provider specific
        // ("serviceInfo") super class member though Otherwise we would have to
        // register a specific type ignoring ServiceInfoComparator (see below) because the
        // generic IServiceInfo will never be equal to the (converted) discovery
        // provider .
        genericServiceInfo = new ServiceInfo(serviceInfo.getLocation(), DiscoveryTestHelper.SERVICENAME, serviceTypeID, 1, 1, serviceInfo.getServiceProperties(), ttl);
    }

    protected synchronized void registerService() {
        BundleContext context = Activator.getDefault().getContext();
        registerService = context.registerService(IServiceInfo.class, genericServiceInfo, null);
    }

    protected synchronized void unregisterService() {
        registerService.unregister();
        registerService = null;
    }
    //	
    //	private static class TypeIgnoringComparator extends ServiceInfoComparator {
    //
    //		public int compare(Object arg0, Object arg1) {
    //			if (arg0 instanceof IServiceInfo && arg1 instanceof IServiceInfo) {
    //				final IServiceInfo first = (IServiceInfo) arg0;
    //				final IServiceInfo second = (IServiceInfo) arg1;
    //				
    //				final IServiceID firstServiceId = first.getServiceID();
    //				final IServiceID secondServiceId = second.getServiceID();
    //								
    //				boolean idsSame = firstServiceId.getLocation().equals(secondServiceId.getLocation());
    //				boolean idTypesSame = firstServiceId.getServiceTypeID().equals(secondServiceId.getServiceTypeID());
    //				
    //				boolean prioSame = first.getPriority() == second.getPriority();
    //				boolean weightSame = first.getWeight() == second.getWeight();
    //				boolean servicePropertiesSame = compareServiceProperties(first.getServiceProperties(), second.getServiceProperties());
    //				boolean ttlSame = first.getTTL() == second.getTTL(); 
    //				final boolean result = (idsSame && idTypesSame && prioSame && weightSame && servicePropertiesSame && ttlSame);
    //				if (result == true) {
    //					return 0;
    //				}
    //			}
    //			return -1;
    //		}
    //	}
}
