/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle Activator
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public class Activator implements BundleActivator {

    /**
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(final BundleContext context) throws Exception {
        // create the platform abstraction layer but do not initialize!!!
        SLPCore.platform = new OSGiPlatformAbstraction(context);
        // register the service factories so each consumer gets its own Locator/Activator instance
        context.registerService("ch.ethz.iks.slp.Advertiser", new ServiceFactory() {

            public Object getService(Bundle bundle, ServiceRegistration registration) {
                SLPCore.init();
                SLPCore.initMulticastSocket();
                return new AdvertiserImpl();
            }

            public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            }
        }, null);
        context.registerService("ch.ethz.iks.slp.Locator", new ServiceFactory() {

            public Object getService(Bundle bundle, ServiceRegistration registration) {
                SLPCore.init();
                return new LocatorImpl();
            }

            public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            }
        }, null);
    }

    /**
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(final BundleContext context) throws Exception {
    }
}
