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
package org.eclipse.ecf.internal.provider;

import java.io.*;
import java.util.Hashtable;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;
import org.eclipse.ecf.provider.generic.SSLGenericContainerInstantiator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class ProviderPlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider";

    //$NON-NLS-1$ //$NON-NLS-2$
    private static final boolean genericClassResolverOverride = Boolean.valueOf(System.getProperty("org.eclipse.ecf.provider.classResolverOverride", "false"));

    //The shared instance.
    private static ProviderPlugin plugin;

    public static final String NAMESPACE_IDENTIFIER = org.eclipse.ecf.core.identity.StringID.class.getName();

    private BundleContext context = null;

    private ServiceTracker logServiceTracker = null;

    private AdapterManagerTracker adapterManagerTracker = null;

    private ServiceTracker sslServerSocketFactoryTracker;

    private ServiceTracker sslSocketFactoryTracker;

    public IAdapterManager getAdapterManager() {
        if (context == null)
            return null;
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new AdapterManagerTracker(this.context);
            adapterManagerTracker.open();
        }
        return adapterManagerTracker.getAdapterManager();
    }

    /**
	 * The constructor.
	 */
    public  ProviderPlugin() {
        super();
        plugin = this;
    }

    /**
	 * This method is called upon plug-in activation
	 */
    public void start(final BundleContext context1) throws Exception {
        this.context = context1;
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                //$NON-NLS-1$
                context1.registerService(//$NON-NLS-1$
                ContainerTypeDescription.class, //$NON-NLS-1$
                new ContainerTypeDescription(GenericContainerInstantiator.TCPSERVER_NAME, new GenericContainerInstantiator(), "ECF Generic Server", true, false), //$NON-NLS-1$
                null);
                //$NON-NLS-1$
                context1.registerService(//$NON-NLS-1$
                ContainerTypeDescription.class, //$NON-NLS-1$
                new ContainerTypeDescription(GenericContainerInstantiator.TCPCLIENT_NAME, new GenericContainerInstantiator(), "ECF Generic Client", true, true), //$NON-NLS-1$
                null);
                //$NON-NLS-1$
                context1.registerService(//$NON-NLS-1$
                ContainerTypeDescription.class, //$NON-NLS-1$
                new ContainerTypeDescription(SSLGenericContainerInstantiator.SSLSERVER_NAME, new SSLGenericContainerInstantiator(), "ECF SSL Generic Server", true, false), //$NON-NLS-1$
                null);
                //$NON-NLS-1$
                context1.registerService(//$NON-NLS-1$
                ContainerTypeDescription.class, //$NON-NLS-1$
                new ContainerTypeDescription(SSLGenericContainerInstantiator.SSLCLIENT_NAME, new SSLGenericContainerInstantiator(), "ECF SSL Generic Client", true, true), //$NON-NLS-1$
                null);
            }
        });
        if (genericClassResolverOverride) {
            Hashtable<String, Object> props = new Hashtable<String, Object>();
            props.put(IClassResolver.BUNDLE_PROP_NAME, PLUGIN_ID);
            this.context.registerService(IClassResolver.class, new BundleClassResolver(context.getBundle()), props);
        }
    }

    public ObjectInputStream createObjectInputStream(InputStream ins) throws IOException {
        return (genericClassResolverOverride) ? ClassResolverObjectInputStream.create(this.context, ins) : new ObjectInputStream(ins);
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    public void stop(BundleContext context1) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        if (sslServerSocketFactoryTracker != null) {
            sslServerSocketFactoryTracker.close();
            sslServerSocketFactoryTracker = null;
        }
        if (sslSocketFactoryTracker != null) {
            sslSocketFactoryTracker.close();
            sslSocketFactoryTracker = null;
        }
        this.context = null;
    }

    private LogService systemLogService;

    @SuppressWarnings("unchecked")
    protected LogService getLogService() {
        if (context == null) {
            if (systemLogService == null)
                systemLogService = new SystemLogService(PLUGIN_ID);
            return systemLogService;
        }
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
	 * Returns the shared instance.
	 * @return ProviderPlugin default instance
	 */
    public static synchronized ProviderPlugin getDefault() {
        if (plugin == null) {
            plugin = new ProviderPlugin();
        }
        return plugin;
    }

    public String getNamespaceIdentifier() {
        return NAMESPACE_IDENTIFIER;
    }

    public BundleContext getContext() {
        return this.context;
    }

    @SuppressWarnings("unchecked")
    public SSLServerSocketFactory getSSLServerSocketFactory() {
        if (context == null)
            return null;
        if (sslServerSocketFactoryTracker == null) {
            sslServerSocketFactoryTracker = new ServiceTracker(this.context, SSLServerSocketFactory.class.getName(), null);
            sslServerSocketFactoryTracker.open();
        }
        return (SSLServerSocketFactory) sslServerSocketFactoryTracker.getService();
    }

    @SuppressWarnings("unchecked")
    public SSLSocketFactory getSSLSocketFactory() {
        if (context == null)
            return null;
        if (sslSocketFactoryTracker == null) {
            sslSocketFactoryTracker = new ServiceTracker(this.context, SSLSocketFactory.class.getName(), null);
            sslSocketFactoryTracker.open();
        }
        return (SSLSocketFactory) sslSocketFactoryTracker.getService();
    }
}
