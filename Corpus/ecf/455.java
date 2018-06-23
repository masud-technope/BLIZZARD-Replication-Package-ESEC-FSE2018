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
package org.eclipse.ecf.internal.provider.jmdns;

import java.util.Properties;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.service.IDiscoveryService;
import org.eclipse.ecf.provider.jmdns.container.ContainerInstantiator;
import org.eclipse.ecf.provider.jmdns.container.JMDNSDiscoveryContainer;
import org.eclipse.ecf.provider.jmdns.identity.JMDNSNamespace;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class JMDNSPlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String NAME = "ecf.discovery.jmdns";

    // The shared instance.
    private static JMDNSPlugin plugin;

    private BundleContext context = null;

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.jmdns";

    /**
	 * The constructor.
	 */
    public  JMDNSPlugin() {
        super();
        plugin = this;
    }

    private AdapterManagerTracker adapterManagerTracker = null;

    private ServiceRegistration serviceRegistration;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    public IAdapterManager getAdapterManager() {
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new AdapterManagerTracker(this.context);
            adapterManagerTracker.open();
        }
        return adapterManagerTracker.getAdapterManager();
    }

    /**
	 * This method is called upon plug-in activation
	 */
    public void start(final BundleContext ctxt) throws Exception {
        this.context = ctxt;
        final Properties props = new Properties();
        props.put(IDiscoveryService.CONTAINER_NAME, NAME);
        props.put(Constants.SERVICE_RANKING, new Integer(750));
        String[] clazzes = new String[] { IDiscoveryService.class.getName(), IDiscoveryLocator.class.getName(), IDiscoveryAdvertiser.class.getName() };
        serviceRegistration = context.registerService(clazzes, serviceFactory, props);
        SafeRunner.run(new ExtensionRegistryRunnable(ctxt) {

            protected void runWithoutRegistry() throws Exception {
                ctxt.registerService(Namespace.class, new JMDNSNamespace("JMDNS Discovery Namespace"), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ContainerInstantiator.JMDNS_CONTAINER_NAME, new ContainerInstantiator(), "JMDNS Discovery Container", true, false), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ContainerInstantiator.JMDNS_LOCATOR_NAME, new ContainerInstantiator(), "JMDNS Discovery Locator"), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ContainerInstantiator.JMDNS_ADVERTISER_NAME, new ContainerInstantiator(), "JMDNS Discovery Advertiser"), null);
            }
        });
    }

    private final DiscoveryServiceFactory serviceFactory = new DiscoveryServiceFactory();

    class DiscoveryServiceFactory implements ServiceFactory {

        private volatile JMDNSDiscoveryContainer jdc;

        /* (non-Javadoc)
		 * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration)
		 */
        public Object getService(final Bundle bundle, final ServiceRegistration registration) {
            if (jdc == null) {
                try {
                    jdc = new JMDNSDiscoveryContainer();
                    jdc.connect(null, null);
                } catch (final IDCreateException e) {
                    Trace.catching(JMDNSPlugin.PLUGIN_ID, JMDNSDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "getService(Bundle, ServiceRegistration)", e);
                } catch (final ContainerConnectException e) {
                    Trace.catching(JMDNSPlugin.PLUGIN_ID, JMDNSDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "getService(Bundle, ServiceRegistration)", e);
                    jdc = null;
                }
            }
            return jdc;
        }

        /**
		 * @return false if this factory has never created a service instance, true otherwise
		 */
        public boolean isActive() {
            return jdc != null;
        }

        /* (non-Javadoc)
		 * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration, java.lang.Object)
		 */
        public void ungetService(final Bundle bundle, final ServiceRegistration registration, final Object service) {
        //TODO-mkuppe we later might want to dispose jmDNS when the last!!! consumer ungets the service 
        //Though don't forget about the (ECF) Container which might still be in use
        }
    }

    protected Bundle getBundle() {
        if (context == null) {
            return null;
        }
        return context.getBundle();
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    public void stop(final BundleContext ctxt) throws Exception {
        if (serviceRegistration != null && serviceFactory.isActive()) {
            ServiceReference reference = serviceRegistration.getReference();
            IDiscoveryLocator aLocator = (IDiscoveryLocator) ctxt.getService(reference);
            serviceRegistration.unregister();
            IContainer container = (IContainer) aLocator.getAdapter(IContainer.class);
            container.disconnect();
            container.dispose();
            serviceRegistration = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        this.context = ctxt;
        plugin = null;
    }

    /**
	 * Returns the shared instance.
	 */
    public static synchronized JMDNSPlugin getDefault() {
        return plugin;
    }

    /**
	 * @param string
	 * @param t
	 */
    public void logException(final String string, final Throwable t) {
        getLogService();
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, string, t);
        }
    }

    /**
	 * @param string
	 * @param t
	 */
    public void logInfo(final String string, final Throwable t) {
        getLogService();
        if (logService != null) {
            logService.log(LogService.LOG_INFO, string, t);
        }
    }

    protected LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        logService = (LogService) logServiceTracker.getService();
        if (logService == null) {
            logService = new SystemLogService(PLUGIN_ID);
        }
        return logService;
    }

    /**
	 * @param errorString
	 */
    public void logError(final String errorString) {
        getLogService();
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, errorString);
        }
    }
}
