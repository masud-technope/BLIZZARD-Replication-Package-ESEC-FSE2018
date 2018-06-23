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
package org.eclipse.ecf.internal.presence;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.util.PlatformHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class PresencePlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.presence";

    // The shared instance.
    private static PresencePlugin plugin;

    private BundleContext context = null;

    private ServiceTracker adapterManagerTracker = null;

    public IAdapterManager getAdapterManager() {
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new ServiceTracker(this.context, IAdapterManager.class.getName(), null);
            adapterManagerTracker.open();
        }
        IAdapterManager adapterManager = (IAdapterManager) adapterManagerTracker.getService();
        // PlatformHelper class
        if (adapterManager == null)
            adapterManager = PlatformHelper.getPlatformAdapterManager();
        return adapterManager;
    }

    /**
	 * The constructor.
	 */
    public  PresencePlugin() {
        super();
        plugin = this;
    }

    /**
	 * This method is called upon plug-in activation
	 */
    public void start(BundleContext ctxt) throws Exception {
        this.context = ctxt;
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        this.context = null;
        plugin = null;
    }

    /**
	 * Returns the shared instance.
	 */
    public static synchronized PresencePlugin getDefault() {
        if (plugin == null) {
            plugin = new PresencePlugin();
        }
        return plugin;
    }
}
