/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.core.identity.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @since 3.4
 */
public class ExtensionRegistryRunnable implements ISafeRunnable {

    private BundleContext context;

    public  ExtensionRegistryRunnable(BundleContext ctxt) {
        this.context = ctxt;
    }

    /**
	 * @since 3.8
	 */
    protected BundleContext getContext() {
        return this.context;
    }

    protected void runWithoutRegistry() throws Exception {
    // by default do nothing
    }

    protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
    // by default do nothing
    }

    protected void logWarning(Throwable exception) {
        Activator a = Activator.getDefault();
        if (a != null)
            a.log(new //$NON-NLS-1$
            Status(//$NON-NLS-1$
            IStatus.WARNING, //$NON-NLS-1$
            Activator.PLUGIN_ID, //$NON-NLS-1$
            IStatus.WARNING, //$NON-NLS-1$
            "Warning: code cannot be run", exception));
    }

    public void run() throws Exception {
        try {
            runWithRegistry(getExtensionRegistry());
        } catch (NoClassDefFoundError e) {
            runWithoutRegistry();
        }
    }

    private IExtensionRegistry getExtensionRegistry() {
        if (context == null)
            return null;
        @SuppressWarnings({ "rawtypes", "unchecked" }) ServiceTracker extensionRegistryTracker = new ServiceTracker(context, IExtensionRegistry.class.getName(), null);
        extensionRegistryTracker.open();
        IExtensionRegistry result = (IExtensionRegistry) extensionRegistryTracker.getService();
        extensionRegistryTracker.close();
        return result;
    }

    public void handleException(Throwable exception) {
        logWarning(exception);
    }
}
