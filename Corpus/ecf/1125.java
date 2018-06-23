/****************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.docshare2;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class DocShareActivator extends Plugin {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.internal.provisional.docshare";

    // The shared instance
    private static DocShareActivator plugin;

    private BundleContext context;

    private ServiceTracker syncStrategyFactoryServiceTracker;

    /**
	 * The constructor
	 */
    public  DocShareActivator() {
    // nothing to do
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        super.start(ctxt);
        plugin = this;
        this.context = ctxt;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        super.stop(ctxt);
        if (syncStrategyFactoryServiceTracker != null) {
            syncStrategyFactoryServiceTracker.close();
            syncStrategyFactoryServiceTracker = null;
        }
        plugin = null;
        this.context = null;
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static DocShareActivator getDefault() {
        return plugin;
    }

    /**
	 * Returns a Synch Strategy Factory for Cola
	 * 
	 * @return IDocumentSynchronizationStrategyFactory
	 */
    public IDocumentSynchronizationStrategyFactory getColaSynchronizationStrategyFactory() {
        if (syncStrategyFactoryServiceTracker == null) {
            syncStrategyFactoryServiceTracker = new ServiceTracker(context, IDocumentSynchronizationStrategyFactory.class.getName(), null);
            syncStrategyFactoryServiceTracker.open();
        }
        return (IDocumentSynchronizationStrategyFactory) syncStrategyFactoryServiceTracker.getService();
    }
}
