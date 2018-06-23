/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.eclipse.ecf.provider.filetransfer.IFileTransferProtocolToFactoryMapper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.tests.filetransfer";

    // The shared instance
    private static Activator plugin;

    private BundleContext context = null;

    private ServiceTracker tracker = null;

    private ServiceTracker proxyServiceTracker = null;

    private ServiceTracker protocolToFactoryMapperTracker = null;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    public Bundle getBundle() {
        if (context == null)
            return null;
        else
            return context.getBundle();
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        plugin = this;
        tracker = new ServiceTracker(context, IRetrieveFileTransferFactory.class.getName(), null);
        tracker.open();
        try {
            proxyServiceTracker = new ServiceTracker(context, IProxyService.class.getName(), null);
            proxyServiceTracker.open();
        } catch (NoClassDefFoundError e) {
            System.out.println("Proxy API not available...continuing with testing without it");
        }
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (tracker != null) {
            tracker.close();
            tracker = null;
        }
        if (proxyServiceTracker != null) {
            proxyServiceTracker.close();
            proxyServiceTracker = null;
        }
        if (protocolToFactoryMapperTracker != null) {
            protocolToFactoryMapperTracker.close();
            protocolToFactoryMapperTracker = null;
        }
        this.context = null;
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    /**
	 * @return IRetrieveFileTransferFactory retrieve file transfer factory
	 */
    public IRetrieveFileTransferFactory getRetrieveFileTransferFactory() {
        return (IRetrieveFileTransferFactory) tracker.getService();
    }

    public IProxyService getProxyService() {
        return (IProxyService) proxyServiceTracker.getService();
    }

    public IFileTransferProtocolToFactoryMapper getProtocolToFactoryMapper() {
        if (protocolToFactoryMapperTracker == null) {
            protocolToFactoryMapperTracker = new ServiceTracker(context, IFileTransferProtocolToFactoryMapper.class.getName(), null);
            protocolToFactoryMapperTracker.open();
        }
        return (IFileTransferProtocolToFactoryMapper) protocolToFactoryMapperTracker.getService();
    }
}
