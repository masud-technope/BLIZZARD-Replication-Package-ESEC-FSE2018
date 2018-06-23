/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.discovery;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class DiscoveryPlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.discovery";

    // The shared instance.
    private static DiscoveryPlugin plugin;

    private BundleContext context;

    private AdapterManagerTracker adapterManagerTracker;

    private ServiceTracker logServiceTracker = null;

    /**
	 * The constructor.
	 */
    public  DiscoveryPlugin() {
        super();
        plugin = this;
    }

    public IAdapterManager getAdapterManager() {
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new AdapterManagerTracker(this.context);
            adapterManagerTracker.open();
        }
        return adapterManagerTracker.getAdapterManager();
    }

    public LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        return (LogService) logServiceTracker.getService();
    }

    public void log(IStatus status) {
        LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }

    /**
	 * This method is called upon plug-in activation
	 * 
	 * @param ctxt
	 *            the bundle context
	 * @throws Exception
	 */
    public void start(final BundleContext ctxt) throws Exception {
        this.context = ctxt;
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                ctxt.registerService(Namespace.class, new DiscoveryNamespace("Discovery Namespace"), null);
            }
        });
    }

    /**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param ctxt
	 *            the bundle context
	 * @throws Exception
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance.
	 * 
	 * @return default discovery plugin instance.
	 */
    public static synchronized DiscoveryPlugin getDefault() {
        if (plugin == null) {
            plugin = new DiscoveryPlugin();
        }
        return plugin;
    }

    public static boolean isStopped() {
        return plugin == null;
    }

    public BundleContext getBundleContext() {
        return context;
    }
}
