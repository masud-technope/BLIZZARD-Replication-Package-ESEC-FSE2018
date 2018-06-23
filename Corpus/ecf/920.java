/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.discovery;

import org.eclipse.ecf.tutorial.osgi.services.discovery.ServicePublicationHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.Trace;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.SystemLogService;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.osgi.framework.*;
import org.osgi.service.discovery.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.tutorial.osgi.services.discovery";

    private static final long DISCOVERY_TIMEOUT = 5000;

    private ServiceTracker servicePublicationTracker;

    private ServiceTracker locatorTracker;

    private ServiceTracker advertiserTracker;

    private ServiceTracker discoveredServiceTrackerTracker;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    private BundleContext context;

    private static Activator plugin;

    private ServicePublicationHandler servicePublicationHandler;

    public static final Activator getDefault() {
        return plugin;
    }

    /* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        plugin = this;
        this.context = ctxt;
        servicePublicationHandler = new ServicePublicationHandler();
        servicePublicationTracker = new ServiceTracker(context, ServicePublication.class.getName(), servicePublicationHandler);
        servicePublicationTracker.open();
        // register the discovery service which is provided by SPH
        Dictionary props = new Hashtable();
        props.put(Discovery.PROP_KEY_VENDOR_NAME, "Eclipse.org");
        props.put(Discovery.PROP_KEY_PRODUCT_NAME, "ECF Discovery");
        props.put(Discovery.PROP_KEY_PRODUCT_VERSION, "1.0.0");
        props.put(Discovery.PROP_KEY_SUPPORTED_PROTOCOLS, "SLP|mDNS|DNS-SRV");
        ctxt.registerService(Discovery.class.getName(), servicePublicationHandler, props);
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
            logService.log(null, LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }

    public void log(ServiceReference sr, IStatus status) {
        log(sr, LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }

    public void log(ServiceReference sr, int level, String message, Throwable t) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(sr, level, message, t);
    }

    public BundleContext getContext() {
        return context;
    }

    public ServicePublicationHandler getServicePublicationHandler() {
        return servicePublicationHandler;
    }

    public IDiscoveryLocator getLocator() {
        if (locatorTracker == null) {
            locatorTracker = new ServiceTracker(this.context, IDiscoveryLocator.class.getName(), null);
            locatorTracker.open();
        }
        try {
            return (IDiscoveryLocator) locatorTracker.waitForService(DISCOVERY_TIMEOUT);
        } catch (InterruptedException e) {
            log("getLocator", "Cannot get IDiscoveryAdvertiser", e);
        }
        return null;
    }

    public IDiscoveryAdvertiser getAdvertiser() {
        if (advertiserTracker == null) {
            advertiserTracker = new ServiceTracker(this.context, IDiscoveryAdvertiser.class.getName(), null);
            advertiserTracker.open();
        }
        try {
            return (IDiscoveryAdvertiser) advertiserTracker.waitForService(DISCOVERY_TIMEOUT);
        } catch (InterruptedException e) {
            log("getAdvertiser", "Cannot get IDiscoveryAdvertiser", e);
        }
        return null;
    }

    public ServiceReference[] getDiscoveredServiceTrackerReferences() {
        if (discoveredServiceTrackerTracker == null) {
            discoveredServiceTrackerTracker = new ServiceTracker(this.context, DiscoveredServiceTracker.class.getName(), null);
            discoveredServiceTrackerTracker.open();
        }
        return discoveredServiceTrackerTracker.getServiceReferences();
    }

    /* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (discoveredServiceTrackerTracker != null) {
            discoveredServiceTrackerTracker.close();
            discoveredServiceTrackerTracker = null;
        }
        if (locatorTracker != null) {
            locatorTracker.close();
            locatorTracker = null;
        }
        if (advertiserTracker != null) {
            advertiserTracker.close();
            advertiserTracker = null;
        }
        if (servicePublicationTracker != null) {
            servicePublicationTracker.close();
            servicePublicationTracker = null;
        }
        if (servicePublicationHandler != null) {
            servicePublicationHandler.dispose();
            servicePublicationHandler = null;
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        this.context = null;
        plugin = null;
    }

    public void log(String method, String message, Throwable t) {
        //$NON-NLS-1$
        IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, method + ":" + message, t);
        log(status);
        Trace.catching(Activator.PLUGIN_ID, DebugOptions.EXCEPTIONS_CATCHING, this.getClass(), //$NON-NLS-1$
        method + ":" + message, //$NON-NLS-1$
        t);
    }
}
