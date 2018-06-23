/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.irc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.SystemLogService;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.irc";

    // The shared instance.
    private static Activator plugin;

    private BundleContext bundleContext = null;

    private ServiceTracker logServiceTracker = null;

    private LogService logService;

    public static void log(String message) {
        getDefault().log(new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, null));
    }

    public static void log(String message, Throwable e) {
        getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, "Caught exception", e));
    }

    protected LogService getLogService() {
        if (logService == null) {
            if (logServiceTracker == null) {
                logServiceTracker = new ServiceTracker(this.bundleContext, LogService.class.getName(), null);
                logServiceTracker.open();
            }
            logService = (LogService) logServiceTracker.getService();
            if (logService == null) {
                logService = new SystemLogService(PLUGIN_ID);
            }
        }
        return logService;
    }

    public void log(IStatus status) {
        LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }

    /**
	 * The constructor.
	 */
    public  Activator() {
        plugin = this;
    }

    /**
	 * This method is called upon plug-in activation
	 */
    public void start(BundleContext context) throws Exception {
        this.bundleContext = context;
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    public void stop(BundleContext context) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        this.bundleContext = null;
        plugin = null;
    }

    /**
	 * Returns the shared instance.
	 */
    public static synchronized Activator getDefault() {
        if (plugin == null) {
            plugin = new Activator();
        }
        return plugin;
    }

    public boolean hasDatashare() {
        if (bundleContext == null)
            return false;
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) if (bundles[i].getSymbolicName().equals("org.eclipse.ecf.provider.datashare.nio"))
            return true;
        return false;
    }
}
