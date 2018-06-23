/****************************************************************************
 * Copyright (c) 2006, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.tests;

import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.tests";

    // The shared instance
    private static Activator plugin;

    private static ServiceTracker idFactoryServiceTracker;

    private static IIDFactory idFactory;

    private static ServiceTracker containerFactoryServiceTracker;

    private static IContainerFactory containerFactory;

    private static ServiceTracker containerManagerServiceTracker;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        plugin = this;
        idFactoryServiceTracker = new ServiceTracker(context, IIDFactory.class.getName(), null);
        idFactoryServiceTracker.open();
        idFactory = (IIDFactory) idFactoryServiceTracker.getService();
        containerFactoryServiceTracker = new ServiceTracker(context, IContainerFactory.class.getName(), null);
        containerFactoryServiceTracker.open();
        containerFactory = (IContainerFactory) containerFactoryServiceTracker.getService();
        containerManagerServiceTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
        containerManagerServiceTracker.open();
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (idFactoryServiceTracker != null) {
            idFactoryServiceTracker.close();
            idFactoryServiceTracker = null;
        }
        if (containerFactoryServiceTracker != null) {
            containerFactoryServiceTracker.close();
            containerFactoryServiceTracker = null;
        }
        if (containerManagerServiceTracker != null) {
            containerManagerServiceTracker.close();
            containerManagerServiceTracker = null;
        }
        plugin = null;
        idFactory = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public IIDFactory getIDFactory() {
        return idFactory;
    }

    public IContainerFactory getContainerFactory() {
        return containerFactory;
    }

    /**
	 * @return container manager.
	 */
    public IContainerManager getContainerManager() {
        return (IContainerManager) containerManagerServiceTracker.getService();
    }
}
