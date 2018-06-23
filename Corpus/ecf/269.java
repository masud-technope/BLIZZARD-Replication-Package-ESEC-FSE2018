/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.phpbb;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPBBPlugin implements BundleActivator {

    public static final String NAMESPACE_IDENTIFIER = "ecf.phpbb";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.phpbb";

    // The shared instance
    private static PHPBBPlugin plugin;

    /**
	 * The constructor
	 */
    public  PHPBBPlugin() {
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static PHPBBPlugin getDefault() {
        return plugin;
    }

    public static String getNamespaceIdentifier() {
        return NAMESPACE_IDENTIFIER;
    }
}
