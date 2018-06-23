/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.remoteservices.hello.host;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.examples.remoteservices.hello.IHelloAsync;
import org.eclipse.ecf.examples.remoteservices.hello.impl.Hello;
import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class HelloHostApplication implements IApplication, IDistributionConstants {

    private static final String DEFAULT_CONTAINER_TYPE = "ecf.r_osgi.peer";

    public static final String DEFAULT_CONTAINER_ID = "r-osgi://localhost:9278";

    private BundleContext bundleContext;

    private String containerType = DEFAULT_CONTAINER_TYPE;

    private String containerId = DEFAULT_CONTAINER_ID;

    private final Object appLock = new Object();

    private boolean done = false;

    private ServiceRegistration helloRegistration;

    private ServiceRegistration discoveryListenerRegistration;

    private ServiceRegistration distributionListenerRegistration;

    public Object start(IApplicationContext appContext) throws Exception {
        bundleContext = Activator.getContext();
        // Process Arguments
        processArgs(appContext);
        // Finally, register the actual remote service
        registerHelloRemoteService();
        // Register console provider. This adds 'start' and 'stop' commands to
        // the OSGI console
        // so that the hello remote service can be started/stopped
        registerConsoleProvider();
        // wait until stopped
        waitForDone();
        return IApplication.EXIT_OK;
    }

    void registerHelloRemoteService() {
        // Setup properties for remote service distribution, as per OSGi 4.2
        // remote services
        // specification (chap 13 in compendium spec)
        Dictionary props = new Hashtable();
        // add OSGi service property indicated export of all interfaces exposed
        // by service (wildcard)
        props.put(IDistributionConstants.SERVICE_EXPORTED_INTERFACES, IDistributionConstants.SERVICE_EXPORTED_INTERFACES_WILDCARD);
        // add OSGi service property specifying config
        props.put(IDistributionConstants.SERVICE_EXPORTED_CONFIGS, containerType);
        // add ECF service property specifying container factory args
        props.put(IDistributionConstants.SERVICE_EXPORTED_CONTAINER_FACTORY_ARGUMENTS, containerId);
        props.put("ecf.exported.async.objectClass", new String[] { IHelloAsync.class.getName() });
        // register remote service
        helloRegistration = bundleContext.registerService(IHello.class.getName(), new Hello(), props);
        // tell everyone
        System.out.println("Host: Hello Service Registered");
    }

    void unregisterHelloRemoteService() {
        if (helloRegistration != null) {
            helloRegistration.unregister();
            helloRegistration = null;
        }
        // tell everyone
        System.out.println("Host: Hello Remote Service Unregistered");
    }

    public void stop() {
        unregisterHelloRemoteService();
        if (discoveryListenerRegistration != null) {
            discoveryListenerRegistration.unregister();
            discoveryListenerRegistration = null;
        }
        if (distributionListenerRegistration != null) {
            distributionListenerRegistration.unregister();
            distributionListenerRegistration = null;
        }
        bundleContext = null;
        synchronized (appLock) {
            done = true;
            appLock.notifyAll();
        }
    }

    private void registerConsoleProvider() {
        // Register the console hello start/stop command provider
        HelloCommandProvider helloCommandProvider = new HelloCommandProvider(this);
        Dictionary props = new Hashtable();
        props.put(org.osgi.framework.Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE - 100));
        bundleContext.registerService(CommandProvider.class.getName(), helloCommandProvider, props);
    }

    private void processArgs(IApplicationContext appContext) {
        String[] originalArgs = (String[]) appContext.getArguments().get("application.args");
        if (originalArgs == null)
            return;
        for (int i = 0; i < originalArgs.length; i++) {
            if (originalArgs[i].equals("-containerType")) {
                containerType = originalArgs[i + 1];
                i++;
            } else if (originalArgs[i].equals("-containerId")) {
                containerId = originalArgs[i + 1];
                i++;
            }
        }
    }

    private void waitForDone() {
        // then just wait here
        synchronized (appLock) {
            while (!done) {
                try {
                    appLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
