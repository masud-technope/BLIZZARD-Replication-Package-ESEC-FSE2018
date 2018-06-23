/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String PLUGIN_ID = "org.eclipse.ecf.tests.osgi.services.remoteserviceadmin";

    private static BundleContext context;

    private static Activator instance;

    public BundleContext getContext() {
        return context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        instance = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        instance = null;
    }

    public static Activator getDefault() {
        return instance;
    }
}
