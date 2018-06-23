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
package org.eclipse.ecf.internal.presence.collab.ui;

import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.presence.collab.ui";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    private ServiceTracker containerManagerTracker;

    /**
	 * The constructor
	 */
    public  Activator() {
    // nothing
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context1) throws Exception {
        super.start(context1);
        plugin = this;
        this.context = context1;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context1) throws Exception {
        super.stop(context1);
        if (containerManagerTracker != null) {
            containerManagerTracker.close();
            containerManagerTracker = null;
        }
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public IContainerManager getContainerManager() {
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }
}
