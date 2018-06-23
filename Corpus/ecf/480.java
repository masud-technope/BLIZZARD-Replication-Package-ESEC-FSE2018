/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.ui.model;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class ModelPlugin extends Plugin {

    // TODO Investigate and fix OSGi ECF classloading issue
    // this is unfortunately necessary to correctly initialize ECF. If missing
    // classloading will
    // fail with a LinkageError for org.eclipse.ecf.identity.ID when ECF hasn't
    // been started already
    // TODO remove once https://bugs.eclipse.org/254684 is fixed
    private final Class hackForClassloadingIssueWithECFandOSGi = org.eclipse.ecf.discovery.identity.IServiceTypeID.class;

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.discovery.model";

    // The shared instance
    private static ModelPlugin plugin;

    /**
	 * The constructor
	 */
    public  ModelPlugin() {
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
    public static ModelPlugin getDefault() {
        return plugin;
    }
}
