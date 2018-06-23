/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.runtime;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.*;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class PDERuntimePlugin extends AbstractUIPlugin {

    //$NON-NLS-1$
    public static final String ID = "org.eclipse.pde.runtime";

    private static PDERuntimePlugin inst;

    private BundleContext fContext;

    private ServiceTracker packageAdminTracker;

    private ServiceTracker platformAdminTracker;

    public  PDERuntimePlugin() {
        inst = this;
    }

    private static boolean isBundleAvailable(String bundleID) {
        Bundle bundle = Platform.getBundle(bundleID);
        return bundle != null && (bundle.getState() & (Bundle.ACTIVE | Bundle.STARTING | Bundle.RESOLVED)) != 0;
    }

    public static final boolean HAS_IDE_BUNDLES;

    static {
        boolean result = false;
        try {
            result = //$NON-NLS-1$
            isBundleAvailable("org.eclipse.core.resources") && isBundleAvailable("org.eclipse.pde.core") && isBundleAvailable("org.eclipse.jdt.core") && isBundleAvailable("org.eclipse.help") && isBundleAvailable("org.eclipse.pde.ui") && isBundleAvailable("org.eclipse.jdt.ui");
        } catch (Throwable // do nothing
        exception) {
        }
        HAS_IDE_BUNDLES = result;
    }

    public static IWorkbenchPage getActivePage() {
        return getDefault().internalGetActivePage();
    }

    public static Shell getActiveWorkbenchShell() {
        return getActiveWorkbenchWindow().getShell();
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    public PackageAdmin getPackageAdmin() {
        if (packageAdminTracker == null) {
            return null;
        }
        return (PackageAdmin) packageAdminTracker.getService();
    }

    public PlatformAdmin getPlatformAdmin() {
        if (platformAdminTracker == null) {
            return null;
        }
        return (PlatformAdmin) platformAdminTracker.getService();
    }

    public static PDERuntimePlugin getDefault() {
        return inst;
    }

    public static String getPluginId() {
        return getDefault().getBundle().getSymbolicName();
    }

    private IWorkbenchPage internalGetActivePage() {
        return getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        this.fContext = context;
        packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        packageAdminTracker.open();
        platformAdminTracker = new ServiceTracker(context, PlatformAdmin.class.getName(), null);
        platformAdminTracker.open();
    }

    public BundleContext getBundleContext() {
        return this.fContext;
    }

    public State getState() {
        return getPlatformAdmin().getState(false);
    }

    public static void log(Throwable e) {
        if (e instanceof InvocationTargetException)
            e = ((InvocationTargetException) e).getTargetException();
        IStatus status = null;
        if (e instanceof CoreException) {
            status = ((CoreException) e).getStatus();
        } else if (e.getMessage() != null) {
            status = new Status(IStatus.ERROR, ID, IStatus.OK, e.getMessage(), e);
        }
        if (status != null)
            getDefault().getLog().log(status);
    }

    /**
	 * This method is called when the plug-in is stopped
	 */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        if (packageAdminTracker != null) {
            packageAdminTracker.close();
            packageAdminTracker = null;
        }
        if (platformAdminTracker != null) {
            platformAdminTracker.close();
            platformAdminTracker = null;
        }
    }
}
