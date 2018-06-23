/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.PlatformHelper;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class XmppPlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.xmpp";

    //$NON-NLS-1$
    protected static final String NAMESPACE_IDENTIFIER = "ecf.xmpp";

    //$NON-NLS-1$
    protected static final String SECURE_NAMESPACE_IDENTIFIER = "ecf.xmpps";

    //$NON-NLS-1$
    protected static final String ROOM_NAMESPACE_IDENTIFIER = "xmpp.room.jive";

    // The shared instance.
    private static XmppPlugin plugin;

    private BundleContext context = null;

    private ServiceTracker logServiceTracker = null;

    private Map services;

    private ServiceTracker adapterManagerTracker = null;

    public static void log(String message) {
        getDefault().log(new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, null));
    }

    public static void log(String message, Throwable e) {
        getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e));
    }

    public IAdapterManager getAdapterManager() {
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new ServiceTracker(this.context, IAdapterManager.class.getName(), null);
            adapterManagerTracker.open();
        }
        IAdapterManager adapterManager = (IAdapterManager) adapterManagerTracker.getService();
        // PlatformHelper class
        if (adapterManager == null)
            adapterManager = PlatformHelper.getPlatformAdapterManager();
        if (adapterManager == null)
            //$NON-NLS-1$
            getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Cannot get adapter manager", null));
        return adapterManager;
    }

    /**
	 * The constructor.
	 */
    public  XmppPlugin() {
        super();
        plugin = this;
    }

    protected LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        return (LogService) logServiceTracker.getService();
    }

    public void log(IStatus status) {
        final LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }

    /**
	 * This method is called upon plug-in activation
	 * @param context 
	 * @throws Exception 
	 */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        services = new HashMap();
    }

    /**
	 * This method is called when the plug-in is stopped
	 * @param context 
	 * @throws Exception 
	 */
    public void stop(BundleContext context) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        this.context = null;
        plugin = null;
    }

    public void registerService(IPresenceService service) {
        if (context != null) {
            services.put(service, context.registerService(IPresenceService.class.getName(), service, null));
        }
    }

    public void unregisterService(IPresenceService service) {
        final ServiceRegistration registration = (ServiceRegistration) services.remove(service);
        if (registration != null) {
            registration.unregister();
        }
    }

    /**
	 * Returns the shared instance.
	 * @return default instance of xmpp plugin.
	 */
    public static synchronized XmppPlugin getDefault() {
        if (plugin == null) {
            plugin = new XmppPlugin();
        }
        return plugin;
    }

    public String getNamespaceIdentifier() {
        return NAMESPACE_IDENTIFIER;
    }

    public String getSecureNamespaceIdentifier() {
        return SECURE_NAMESPACE_IDENTIFIER;
    }

    public String getRoomNamespaceIdentifier() {
        return ROOM_NAMESPACE_IDENTIFIER;
    }
}
