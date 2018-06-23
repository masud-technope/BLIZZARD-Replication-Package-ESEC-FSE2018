/*******************************************************************************
 * Copyright (c) 2008 Versant Corp, 2015 Composent, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 *     Scott Lewis - enhancements to support Remote Service Admin specification 
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservices.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends AbstractUIPlugin {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice.ui";

    private volatile ServiceTracker containerManagerTracker;

    // The shared instance
    private static volatile Activator plugin;

    private BundleContext context;

    private IExtensionRegistry extensionRegistry;

    private String servicesViewId;

    public String getLocalServicesViewId() {
        return servicesViewId;
    }

    public void setLocalServicesViewId(String viewId) {
        this.servicesViewId = viewId;
    }

    /**
	 * The constructor
	 */
    public  Activator() {
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        this.context = context;
        ServiceTracker<IExtensionRegistry, IExtensionRegistry> st = new ServiceTracker(this.context, IExtensionRegistry.class, null);
        st.open();
        this.extensionRegistry = st.getService();
        st.close();
    }

    public List<ServicesViewExtension> getLocalServicesViewExtensions() {
        return getServicesViewExtensions(Boolean.TRUE);
    }

    public List<ServicesViewExtension> getRemoteServicesViewExtensions() {
        return getServicesViewExtensions(Boolean.FALSE);
    }

    public List<ServicesViewExtension> getServicesViewExtensions() {
        return getServicesViewExtensions(null);
    }

    public List<ServicesViewExtension> getServicesViewExtensions(Boolean localOnly) {
        List<ServicesViewExtension> results = new ArrayList<ServicesViewExtension>();
        if (this.extensionRegistry != null) {
            IExtensionPoint epoint = this.extensionRegistry.getExtensionPoint("org.eclipse.ecf.remoteservice.ui.servicesview");
            if (epoint != null) {
                IConfigurationElement[] elements = epoint.getConfigurationElements();
                for (IConfigurationElement element : elements) {
                    try {
                        ServicesViewExtension sve = new ServicesViewExtension(element);
                        if (localOnly != null) {
                            if (localOnly.booleanValue()) {
                                if (!sve.isLocal())
                                    sve = null;
                            } else {
                                if (sve.isLocal())
                                    sve = null;
                            }
                        }
                        if (sve != null)
                            results.add(sve);
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        Collections.sort(results, new Comparator<ServicesViewExtension>() {

            @Override
            public int compare(ServicesViewExtension o1, ServicesViewExtension o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });
        return results;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext )
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        if (containerManagerTracker != null) {
            containerManagerTracker.close();
            containerManagerTracker = null;
        }
        this.extensionRegistry = null;
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

    public IContainerManager getContainerManager() {
        BundleContext context = getBundle().getBundleContext();
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }
}
