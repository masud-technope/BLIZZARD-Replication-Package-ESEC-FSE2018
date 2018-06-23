/*******************************************************************************
 * Copyright (c) 2006 Ken Gilmer. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ken Gilmer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.collab.editor;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin {

    // The shared instance.
    private static Activator plugin;

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.example.collab.editor";

    /**
	 * The constructor.
	 */
    public  Activator() {
        plugin = this;
    }

    /**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
    public static Activator getDefault() {
        return plugin;
    }
}
