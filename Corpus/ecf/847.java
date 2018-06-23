/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.rpc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.remoteservice.rpc.identity.RpcNamespace;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice.rpc";

    // The shared instance
    private static Activator plugin;

    private static BundleContext context;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    static BundleContext getContext() {
        return context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(final BundleContext bundleContext) throws Exception {
        plugin = this;
        Activator.context = bundleContext;
        SafeRunner.run(new ExtensionRegistryRunnable(bundleContext) {

            protected void runWithoutRegistry() throws Exception {
                bundleContext.registerService(Namespace.class, new RpcNamespace(), null);
                bundleContext.registerService(ContainerTypeDescription.class, new ContainerTypeDescription("ecf.xmlrpc.client", new RpcClientContainerInstantiator(), "Rpc Client Container"), //$NON-NLS-1$ //$NON-NLS-2$
                null);
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        plugin = null;
        Activator.context = null;
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
            logServiceTracker = new ServiceTracker(Activator.context, LogService.class.getName(), null);
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
