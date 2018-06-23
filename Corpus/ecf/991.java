/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice.consumer.ds.generic.auth;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.IConsumerContainerSelector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        // Register our consumer container selector, so that it gets used rather
        // than the default one
        // This registration must be done before the remote service is
        // discovered and imported, which
        // is why this bundle is started early. There are other ways to assure
        // that this is done
        // sufficiently early
        context.registerService(IConsumerContainerSelector.class.getName(), new GenericAuthConsumerContainerSelector(), null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }
}
