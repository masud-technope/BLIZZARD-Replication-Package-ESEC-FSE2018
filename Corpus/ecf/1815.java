/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.presence.ui;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.presence.ui";

    //$NON-NLS-1$
    public static final String CONTACTS_IMAGE = "contacts";

    //$NON-NLS-1$
    public static final String COLLABORATION_IMAGE = "collaboration";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker tracker;

    private ServiceTracker extensionRegistryTracker = null;

    private ServiceTracker containerManagerTracker = null;

    private BundleContext bundleContext;

    public IPresenceService[] getPresenceServices() {
        ServiceReference[] references = tracker.getServiceReferences();
        if (references == null) {
            return new IPresenceService[0];
        }
        int length = references.length;
        IPresenceService[] services = new IPresenceService[length];
        for (int i = 0; i < length; i++) {
            services[i] = (IPresenceService) tracker.getService(references[i]);
        }
        return services;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        this.bundleContext = context;
        tracker = new ServiceTracker(context, IPresenceService.class.getName(), null);
        tracker.open();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
	 */
    protected ImageRegistry createImageRegistry() {
        ImageRegistry registry = super.createImageRegistry();
        //$NON-NLS-1$
        registry.put(CONTACTS_IMAGE, AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/contacts.gif").createImage());
        //$NON-NLS-1$
        registry.put(COLLABORATION_IMAGE, AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/collaboration.gif").createImage());
        return registry;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        if (extensionRegistryTracker != null) {
            extensionRegistryTracker.close();
            extensionRegistryTracker = null;
        }
        if (containerManagerTracker != null) {
            containerManagerTracker.close();
            containerManagerTracker = null;
        }
        this.bundleContext = null;
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

    public IExtensionRegistry getExtensionRegistry() {
        if (extensionRegistryTracker == null) {
            this.extensionRegistryTracker = new ServiceTracker(bundleContext, IExtensionRegistry.class.getName(), null);
            this.extensionRegistryTracker.open();
        }
        return (IExtensionRegistry) extensionRegistryTracker.getService();
    }

    public IContainerManager getContainerManager() {
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(bundleContext, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }
}
