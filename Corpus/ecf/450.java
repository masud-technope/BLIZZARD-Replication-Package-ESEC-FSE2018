/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation   
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ExtensionRegistryRunnable;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainerInstantiator;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryNamespace;
import org.eclipse.ecf.provider.zookeeper.core.internal.BundleStoppingListener;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class DiscoveryActivator implements BundleActivator {

    private static BundleContext context;

    private ServiceRegistration discoveryRegistration;

    private static Set<BundleStoppingListener> stopListeners = new HashSet<BundleStoppingListener>();

    private ServiceTracker confTracker, logServiceTracker;

    public void start(final BundleContext ctxt) {
        context = ctxt;
        SafeRunner.run(new ExtensionRegistryRunnable(ctxt) {

            protected void runWithoutRegistry() throws Exception {
                ctxt.registerService(Namespace.class, new ZooDiscoveryNamespace(), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ZooDiscoveryContainerInstantiator.NAME, new ZooDiscoveryContainerInstantiator(), "Zookeeper Discovery Container"), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ZooDiscoveryContainerInstantiator.NAME + ".advertiser", new ZooDiscoveryContainerInstantiator(), "Zookeeper Discovery Advertiser Container"), null);
                ctxt.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(ZooDiscoveryContainerInstantiator.NAME + ".locator", new ZooDiscoveryContainerInstantiator(), "Zookeeper Discovery Locator Container"), null);
            }
        });
        final Properties props = new Properties();
        props.put(IDiscoveryLocator.CONTAINER_NAME, ZooDiscoveryContainerInstantiator.NAME);
        props.put(IDiscoveryAdvertiser.CONTAINER_NAME, ZooDiscoveryContainerInstantiator.NAME);
        // register ourselves using a service factory
        discoveryRegistration = ctxt.registerService(new String[] { IDiscoveryLocator.class.getName(), IDiscoveryAdvertiser.class.getName() }, new ServiceFactory() {

            private volatile ZooDiscoveryContainer zdc;

            public Object getService(Bundle bundle, ServiceRegistration registration) {
                if (zdc == null) {
                    zdc = ZooDiscoveryContainer.getSingleton();
                    zdc.setDiscoveryProperties(props);
                }
                return zdc;
            }

            public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            }
        }, (Dictionary) props);
        // setup and open log service tracker
        logServiceTracker = new ServiceTracker(ctxt, org.osgi.service.log.LogService.class.getName(), null) {

            public Object addingService(ServiceReference reference) {
                Logger.bindLogService((LogService) context.getService(reference));
                return super.addingService(reference);
            }

            public void removedService(ServiceReference reference, Object service) {
                Logger.unbindLogService((LogService) service);
                super.removedService(reference, service);
            }
        };
        logServiceTracker.open(true);
        // Autostart by consuming our own service
        if (ZooDiscoveryContainer.autoStart()) {
            ctxt.getService(discoveryRegistration.getReference());
        }
    }

    public void stop(BundleContext c) throws Exception {
        dispose();
        Logger.log(LogService.LOG_INFO, PrettyPrinter.prompt(PrettyPrinter.DEACTIVATED, null), null);
    }

    private void dispose() {
        for (BundleStoppingListener l : stopListeners) {
            l.bundleStopping();
        }
        stopListeners.clear();
        ZooDiscoveryContainer.getSingleton().shutdown();
        if (this.discoveryRegistration != null) {
            this.discoveryRegistration.unregister();
        }
        if (this.confTracker != null) {
            this.confTracker.close();
        }
        // Initiates an orderly shutdown of all our cached threads
        ZooDiscoveryContainer.CACHED_THREAD_POOL.shutdown();
    }

    public static BundleContext getContext() {
        return context;
    }

    public static void registerBundleStoppingListner(BundleStoppingListener l) {
        stopListeners.add(l);
    }
}
