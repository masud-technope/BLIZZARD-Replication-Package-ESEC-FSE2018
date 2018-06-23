/****************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.filetransfer.ui;

import java.io.File;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.filetransfer.ui";

    //$NON-NLS-1$
    public static final String DOWNLOAD_PATH_PREFERENCE = "downloadpath";

    // The shared instance
    private static Activator plugin;

    /**
	 * The constructor
	 */
    public  Activator() {
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        setPreferenceDefaults();
    }

    public String getDefaultDownloadPath() {
        //$NON-NLS-1$
        String defaultFilePath = System.getProperty("user.home");
        if (//$NON-NLS-1$
        Platform.getOS().startsWith("win")) {
            //$NON-NLS-1$
            defaultFilePath = defaultFilePath + File.separator + "Desktop";
        }
        return defaultFilePath;
    }

    /**
	 * 
	 */
    private void setPreferenceDefaults() {
        getPreferenceStore().setDefault(DOWNLOAD_PATH_PREFERENCE, getDefaultDownloadPath());
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
    public static Activator getDefault() {
        return plugin;
    }
}
