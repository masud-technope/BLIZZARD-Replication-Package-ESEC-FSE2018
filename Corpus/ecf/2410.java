/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.discovery;

import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.tests.discovery";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker locatorTracker;

    private ServiceTracker advertiserTracker;

    private BundleContext context;

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext aContext) throws Exception {
        plugin = this;
        context = aContext;
        locatorTracker = new ServiceTracker(aContext, IDiscoveryLocator.class.getName(), null);
        advertiserTracker = new ServiceTracker(aContext, IDiscoveryAdvertiser.class.getName(), null);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (locatorTracker != null) {
            locatorTracker.close();
            locatorTracker = null;
        }
        if (advertiserTracker != null) {
            advertiserTracker.close();
            advertiserTracker = null;
        }
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public IDiscoveryLocator getDiscoveryLocator(String containerUnderTest) {
        locatorTracker.open();
        final ServiceReference[] serviceReferences = locatorTracker.getServiceReferences();
        if (serviceReferences != null) {
            for (int i = 0; i < serviceReferences.length; i++) {
                ServiceReference sr = serviceReferences[i];
                if (containerUnderTest.equals(sr.getProperty(IDiscoveryLocator.CONTAINER_NAME))) {
                    return (IDiscoveryLocator) locatorTracker.getService(sr);
                }
            }
        }
        return null;
    }

    public void closeServiceTracker(String containerUnderTest) {
        ServiceReference[] serviceReferences = locatorTracker.getServiceReferences();
        if (serviceReferences != null) {
            for (int i = 0; i < serviceReferences.length; i++) {
                ServiceReference sr = serviceReferences[i];
                if (containerUnderTest.equals(sr.getProperty(IDiscoveryLocator.CONTAINER_NAME))) {
                    locatorTracker.remove(sr);
                }
            }
        }
        if (locatorTracker != null) {
            locatorTracker.close();
        }
        serviceReferences = advertiserTracker.getServiceReferences();
        if (serviceReferences != null) {
            for (int i = 0; i < serviceReferences.length; i++) {
                ServiceReference sr = serviceReferences[i];
                if (containerUnderTest.equals(sr.getProperty(IDiscoveryAdvertiser.CONTAINER_NAME))) {
                    advertiserTracker.remove(sr);
                }
            }
        }
        if (advertiserTracker != null) {
            advertiserTracker.close();
        }
    }

    public IDiscoveryAdvertiser getDiscoveryAdvertiser(String containerUnderTest) {
        advertiserTracker.open();
        final ServiceReference[] serviceReferences = advertiserTracker.getServiceReferences();
        if (serviceReferences != null) {
            for (int i = 0; i < serviceReferences.length; i++) {
                ServiceReference sr = serviceReferences[i];
                if (containerUnderTest.equals(sr.getProperty(IDiscoveryAdvertiser.CONTAINER_NAME))) {
                    return (IDiscoveryAdvertiser) advertiserTracker.getService(sr);
                }
            }
        }
        return null;
    }

    public BundleContext getContext() {
        return context;
    }
}
