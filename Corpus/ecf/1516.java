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
package org.eclipse.ecf.internal.examples.updatesite;

import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private BundleContext context;

    private ServiceTracker httpServiceTracker;

    private ServiceTracker discoveryTracker;

    private static Activator plugin;

    public static Activator getDefault() {
        return plugin;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        context = ctxt;
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (httpServiceTracker != null) {
            httpServiceTracker.close();
            httpServiceTracker = null;
        }
        if (discoveryTracker != null) {
            discoveryTracker.close();
            discoveryTracker = null;
        }
        ctxt = null;
    }

    public HttpService waitForHttpService(int waittime) throws InterruptedException {
        if (httpServiceTracker == null) {
            httpServiceTracker = new ServiceTracker(context, HttpService.class.getName(), null);
            httpServiceTracker.open();
        }
        return (HttpService) httpServiceTracker.waitForService(waittime);
    }

    public IDiscoveryAdvertiser waitForDiscoveryService(int waittime) throws InterruptedException {
        if (discoveryTracker == null) {
            discoveryTracker = new ServiceTracker(context, IDiscoveryAdvertiser.class.getName(), null);
            discoveryTracker.open();
        }
        return (IDiscoveryAdvertiser) discoveryTracker.waitForService(waittime);
    }
}
