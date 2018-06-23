/****************************************************************************
 * Copyright (c) 2007 IBM, Composent Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Aniszczyk - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.httpclient4;

import javax.net.ssl.SSLSocketFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.LogHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.filetransfer.httpclient4";

    // The shared instance
    private static Activator plugin;

    private BundleContext context = null;

    private ServiceTracker logServiceTracker = null;

    private ServiceTracker sslSocketFactoryTracker;

    private ISSLSocketFactoryModifier sslSocketFactoryModifier;

    /**
	 * The constructor
	 */
    public  Activator() {
    //
    }

    public BundleContext getContext() {
        return context;
    }

    public void start(BundleContext ctxt) throws Exception {
        plugin = this;
        this.context = ctxt;
        // to set the socket factory for the specific proxy and httpclient instance
        try {
            //$NON-NLS-1$
            Class socketFactoryModifierClass = Class.forName("org.eclipse.ecf.internal.provider.filetransfer.httpclient4.ssl.SSLSocketFactoryModifier");
            sslSocketFactoryModifier = (ISSLSocketFactoryModifier) socketFactoryModifierClass.newInstance();
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, "Unexpected Error in Activator.start", t));
        }
    }

    public ISSLSocketFactoryModifier getSSLSocketFactoryModifier() {
        return sslSocketFactoryModifier;
    }

    public void stop(BundleContext ctxt) throws Exception {
        if (sslSocketFactoryModifier != null) {
            sslSocketFactoryModifier.dispose();
            sslSocketFactoryModifier = null;
        }
        if (sslSocketFactoryTracker != null) {
            sslSocketFactoryTracker.close();
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
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

    private synchronized LogService getLogService() {
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

    public synchronized SSLSocketFactory getSSLSocketFactory() {
        if (sslSocketFactoryTracker == null) {
            sslSocketFactoryTracker = new ServiceTracker(this.context, SSLSocketFactory.class.getName(), null);
            sslSocketFactoryTracker.open();
        }
        return (SSLSocketFactory) sslSocketFactoryTracker.getService();
    }

    public static void logNoProxyWarning(Throwable e) {
        Activator a = getDefault();
        if (a != null) {
            //$NON-NLS-1$
            a.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.ERROR, "Warning: Platform proxy API not available", e));
        }
    }
}
