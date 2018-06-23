/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.remoteservice;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceContainerAdapterFactory;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceNamespace;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    private ServiceTracker logServiceTracker = null;

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.remoteservice";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    private LogService logService;

    /**
	 * The constructor
	 */
    public  Activator() {
        plugin = this;
    }

    private List rscAdapterFactories;

    private static IAdapterManager getAdapterManager(BundleContext ctx) {
        AdapterManagerTracker t = new AdapterManagerTracker(ctx);
        t.open();
        IAdapterManager am = t.getAdapterManager();
        t.close();
        return am;
    }

    public void start(final BundleContext ctxt) throws Exception {
        this.context = ctxt;
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                //$NON-NLS-1$
                ctxt.registerService(//$NON-NLS-1$
                Namespace.class, //$NON-NLS-1$
                new RemoteServiceNamespace(org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceNamespace.NAME, "Generic remote service namespace"), //$NON-NLS-1$
                null);
                IAdapterManager am = getAdapterManager(ctxt);
                if (am != null) {
                    rscAdapterFactories = new ArrayList();
                    IAdapterFactory af = new RemoteServiceContainerAdapterFactory();
                    am.registerAdapters(af, org.eclipse.ecf.provider.generic.SSLServerSOContainer.class);
                    rscAdapterFactories.add(af);
                    af = new RemoteServiceContainerAdapterFactory();
                    am.registerAdapters(af, org.eclipse.ecf.provider.generic.TCPServerSOContainer.class);
                    rscAdapterFactories.add(af);
                    af = new RemoteServiceContainerAdapterFactory();
                    am.registerAdapters(af, org.eclipse.ecf.provider.generic.SSLClientSOContainer.class);
                    rscAdapterFactories.add(af);
                    af = new RemoteServiceContainerAdapterFactory();
                    am.registerAdapters(af, org.eclipse.ecf.provider.generic.TCPClientSOContainer.class);
                    rscAdapterFactories.add(af);
                }
            }
        });
    }

    public void stop(BundleContext ctxt) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        if (rscAdapterFactories != null) {
            IAdapterManager am = getAdapterManager(this.context);
            if (am != null) {
                for (Iterator i = rscAdapterFactories.iterator(); i.hasNext(); ) am.unregisterAdapters((IAdapterFactory) i.next());
            }
            rscAdapterFactories = null;
        }
        this.context = null;
        plugin = null;
    }

    public BundleContext getContext() {
        return context;
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

    /**
	 * @param filter
	 * @return Fileter created via context
	 */
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        return context.createFilter(filter);
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
        LogService ls = getLogService();
        if (ls != null) {
            ls.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }
}
