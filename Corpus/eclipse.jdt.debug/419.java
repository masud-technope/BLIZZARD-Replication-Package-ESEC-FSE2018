/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.macosx;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Plugin;

public class MacOSXLaunchingPlugin extends Plugin {

    private static MacOSXLaunchingPlugin fgPlugin;

    public  MacOSXLaunchingPlugin() {
        super();
        Assert.isTrue(fgPlugin == null);
        fgPlugin = this;
    }

    public static MacOSXLaunchingPlugin getDefault() {
        return fgPlugin;
    }

    /*
	 * Convenience method which returns the unique identifier of this plug-in.
	 */
    static String getUniqueIdentifier() {
        if (getDefault() == null) {
            //$NON-NLS-1$
            return "org.eclipse.jdt.launching.macosx";
        }
        return getDefault().getBundle().getSymbolicName();
    }
}
