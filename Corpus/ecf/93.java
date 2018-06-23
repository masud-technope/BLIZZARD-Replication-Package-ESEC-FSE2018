/*******************************************************************************
 * Copyright (c) 2010 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.rest.synd;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.SystemLogService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice.rest.rss";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    /**
	 * The constructor
	 */
    public  Activator() {
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext bundleContext) throws Exception {
        this.context = bundleContext;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        this.context = null;
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        this.context = null;
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static synchronized Activator getDefault() {
        if (plugin == null) {
            plugin = new Activator();
        }
        return plugin;
    }

    protected LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        logService = (LogService) logServiceTracker.getService();
        if (logService == null)
            logService = new SystemLogService(PLUGIN_ID);
        return logService;
    }

    public void log(IStatus status) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }
}
