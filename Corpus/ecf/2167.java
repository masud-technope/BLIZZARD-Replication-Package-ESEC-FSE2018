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
package org.eclipse.ecf.core.util;

import java.lang.reflect.Method;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.core.identity.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Helper class for eliminating direct references to Platform static methods
 * getAdapterManager and getExtensionRegistry. Note that instead of
 * Platform.getAdapterManager(), clients can call
 * PlatformHelper.getAdapterManager(). If this returns null, the Platform class
 * is not available.
 */
public class PlatformHelper {

    @SuppressWarnings("rawtypes")
    private static Class platformClass = null;

    private static IAdapterManager adapterManagerCache = null;

    private static IExtensionRegistry extensionRegistryCache = null;

    static {
        Activator a = Activator.getDefault();
        if (a != null) {
            try {
                BundleContext c = a.getBundleContext();
                if (c != null) {
                    Bundle[] bundles = c.getBundles();
                    Bundle coreRuntime = null;
                    for (int i = 0; i < bundles.length; i++) if (bundles[i].getSymbolicName().equals("org.eclipse.core.runtime")) {
                        coreRuntime = bundles[i];
                        platformClass = coreRuntime.loadClass("org.eclipse.core.runtime.Platform");
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    a.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, "Cannot load Platform class", e));
                } catch (Throwable t) {
                }
            }
        }
    }

    public static synchronized boolean isPlatformAvailable() {
        return platformClass != null;
    }

    public static synchronized IAdapterManager getPlatformAdapterManager() {
        if (adapterManagerCache != null)
            return adapterManagerCache;
        if (isPlatformAvailable()) {
            try {
                @SuppressWarnings("unchecked") Method m = //$NON-NLS-1$
                platformClass.getMethod(//$NON-NLS-1$
                "getAdapterManager", //$NON-NLS-1$
                (Class[]) null);
                adapterManagerCache = (IAdapterManager) m.invoke(null, (Object[]) null);
                return adapterManagerCache;
            } catch (Exception e) {
                Activator.getDefault().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, "Cannot get PlatformAdapterManager()", e));
                return null;
            }
        }
        return null;
    }

    public static synchronized IExtensionRegistry getExtensionRegistry() {
        if (extensionRegistryCache != null)
            return extensionRegistryCache;
        if (isPlatformAvailable()) {
            try {
                @SuppressWarnings("unchecked") Method m = //$NON-NLS-1$
                platformClass.getMethod(//$NON-NLS-1$
                "getExtensionRegistry", //$NON-NLS-1$
                (Class[]) null);
                extensionRegistryCache = (IExtensionRegistry) m.invoke(null, (Object[]) null);
                return extensionRegistryCache;
            } catch (Exception e) {
                Activator.getDefault().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, "Cannot get PlatformExtensionRegistry()", e));
                return null;
            }
        }
        return null;
    }
}
