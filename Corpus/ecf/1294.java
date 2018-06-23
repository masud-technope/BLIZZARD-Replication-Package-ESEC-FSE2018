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
package org.eclipse.ecf.internal.server;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.server";

    //The shared instance.
    private static Activator plugin;

    /**
	 * The constructor.
	 */
    public  Activator() {
        plugin = this;
    }

    /**
	 * This method is called upon plug-in activation
	 */
    public void start(BundleContext context) throws Exception {
    // nothing to do
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
    }

    /**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
    public static synchronized Activator getDefault() {
        if (plugin == null) {
            plugin = new Activator();
        }
        return plugin;
    }
}
