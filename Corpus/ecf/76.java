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

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.provider.dnssd.IDnsSdDiscoveryConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Activator implements BundleActivator {

    private static Activator instance;

    private IDiscoveryLocator discoveryLocator;

    private IDiscoveryAdvertiser discoveryAdvertiser;

    private ServiceListener locListener;

    private ServiceListener advListener;

    private final Object locLock = new Object();

    private final Object advLock = new Object();

    public  Activator() {
        instance = this;
    }

    /* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(final BundleContext context) throws Exception {
        final ServiceReference configAdminServiceRef = context.getServiceReference(ConfigurationAdmin.class.getName());
        if (configAdminServiceRef == null) {
            System.err.println("You don't have config admin deployed. Some tests will fail that require configuration!");
            return;
        }
        final ConfigurationAdmin configAdmin = (ConfigurationAdmin) context.getService(configAdminServiceRef);
        Configuration config = configAdmin.createFactoryConfiguration(DnsSdTestHelper.ECF_DISCOVERY_DNSSD + ".locator", null);
        Dictionary properties = new Hashtable();
        properties.put(IDnsSdDiscoveryConstants.CA_SEARCH_PATH, new String[] { DnsSdTestHelper.DOMAIN });
        properties.put(IDnsSdDiscoveryConstants.CA_RESOLVER, DnsSdTestHelper.DNS_RESOLVER);
        properties.put(IDnsSdDiscoveryConstants.CA_TSIG_KEY, DnsSdTestHelper.TSIG_KEY);
        properties.put(IDnsSdDiscoveryConstants.CA_TSIG_KEY_NAME, DnsSdTestHelper.TSIG_KEY_NAME);
        config.update(properties);
        String filter = "(" + Constants.SERVICE_PID + "=" + config.getPid() + ")";
        // add the service listener
        locListener = new ServiceListener() {

            public void serviceChanged(ServiceEvent event) {
                switch(event.getType()) {
                    case ServiceEvent.REGISTERED:
                        ServiceReference serviceReference = event.getServiceReference();
                        discoveryLocator = (IDiscoveryLocator) context.getService(serviceReference);
                        synchronized (locLock) {
                            locLock.notifyAll();
                        }
                }
            }
        };
        context.addServiceListener(locListener, filter);
        // try to get the service initially
        ServiceReference[] references = context.getServiceReferences(IDiscoveryLocator.class.getName(), filter);
        if (references != null) {
            for (int i = 0; i < references.length; ) {
                ServiceReference serviceReference = references[i];
                discoveryLocator = (IDiscoveryLocator) context.getService(serviceReference);
                synchronized (locLock) {
                    locLock.notifyAll();
                }
            }
        }
        // advertiser
        config = configAdmin.createFactoryConfiguration(DnsSdTestHelper.ECF_DISCOVERY_DNSSD + ".advertiser", null);
        properties = new Hashtable();
        properties.put(IDnsSdDiscoveryConstants.CA_SEARCH_PATH, new String[] { DnsSdTestHelper.DOMAIN });
        properties.put(IDnsSdDiscoveryConstants.CA_RESOLVER, DnsSdTestHelper.DNS_RESOLVER);
        properties.put(IDnsSdDiscoveryConstants.CA_TSIG_KEY, DnsSdTestHelper.TSIG_KEY);
        properties.put(IDnsSdDiscoveryConstants.CA_TSIG_KEY_NAME, DnsSdTestHelper.TSIG_KEY_NAME);
        config.update(properties);
        filter = "(" + Constants.SERVICE_PID + "=" + config.getPid() + ")";
        // add the service listener
        advListener = new ServiceListener() {

            public void serviceChanged(ServiceEvent event) {
                switch(event.getType()) {
                    case ServiceEvent.REGISTERED:
                        ServiceReference serviceReference = event.getServiceReference();
                        discoveryAdvertiser = (IDiscoveryAdvertiser) context.getService(serviceReference);
                        synchronized (advLock) {
                            advLock.notifyAll();
                        }
                }
            }
        };
        context.addServiceListener(advListener, filter);
        // try to get the service initially
        references = context.getServiceReferences(IDiscoveryAdvertiser.class.getName(), filter);
        if (references != null) {
            for (int i = 0; i < references.length; ) {
                ServiceReference serviceReference = references[i];
                discoveryAdvertiser = (IDiscoveryAdvertiser) context.getService(serviceReference);
                synchronized (advLock) {
                    advLock.notifyAll();
                }
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (locListener != null) {
            context.removeServiceListener(locListener);
            locListener = null;
        }
        if (advListener != null) {
            context.removeServiceListener(advListener);
            advListener = null;
        }
    }

    public static Activator getDefault() {
        return instance;
    }

    public IDiscoveryLocator getDiscoveryLocator() {
        if (discoveryLocator == null) {
            try {
                synchronized (locLock) {
                    // wait 2 few for config admin
                    locLock.wait(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return discoveryLocator;
    }

    public IDiscoveryAdvertiser getDiscoveryAdvertiser() {
        if (discoveryAdvertiser == null) {
            try {
                synchronized (advLock) {
                    // wait a few sec for config admin
                    advLock.wait(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return discoveryAdvertiser;
    }
}
