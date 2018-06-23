package org.eclipse.ecf.internal.server.generic;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceContainerAdapterFactory;
import org.eclipse.ecf.server.generic.*;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.server.generic";

    // The shared instance
    private static Activator plugin;

    private BundleContext context = null;

    private ServerManager serverManager = null;

    private ServiceTracker discoveryTracker = null;

    private ServiceTracker logServiceTracker = null;

    private ServiceTracker containerManagerTracker = null;

    private GenericServerContainerGroupFactory gscgFactory = null;

    private ServiceRegistration gscgRegistration;

    /**
	 * The constructor
	 */
    public  Activator() {
    // null constructor
    }

    public BundleContext getContext() {
        return context;
    }

    public IDiscoveryAdvertiser getDiscovery() {
        return (IDiscoveryAdvertiser) discoveryTracker.getService();
    }

    public IContainerManager getContainerManager() {
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(this.context, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }

    public Bundle getBundle() {
        if (context == null)
            return null;
        return context.getBundle();
    }

    protected LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        return (LogService) logServiceTracker.getService();
    }

    public void log(IStatus status) {
        LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        this.context = ctxt;
        plugin = this;
        this.discoveryTracker = new ServiceTracker(ctxt, IDiscoveryAdvertiser.class.getName(), null);
        this.discoveryTracker.open();
        try {
            // for backward compatibility
            serverManager = new ServerManager();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // Register generic server container group factory service
        this.gscgFactory = new GenericServerContainerGroupFactory();
        this.gscgRegistration = this.context.registerService(IGenericServerContainerGroupFactory.class.getName(), gscgFactory, null);
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                AdapterManagerTracker t = new AdapterManagerTracker(getContext());
                t.open();
                IAdapterManager am = t.getAdapterManager();
                t.close();
                if (am != null) {
                    am.registerAdapters(new RemoteServiceContainerAdapterFactory(), org.eclipse.ecf.server.generic.GenericServerContainer.class);
                    am.registerAdapters(new RemoteServiceContainerAdapterFactory(), org.eclipse.ecf.server.generic.SSLGenericServerContainer.class);
                }
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (serverManager != null) {
            serverManager.closeServers();
            serverManager = null;
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        if (discoveryTracker != null) {
            discoveryTracker.close();
            discoveryTracker = null;
        }
        if (gscgRegistration != null) {
            gscgRegistration.unregister();
            gscgRegistration = null;
            if (gscgFactory != null) {
                gscgFactory.close();
                gscgFactory = null;
            }
        }
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static synchronized Activator getDefault() {
        if (plugin == null)
            //$NON-NLS-1$
            throw new NullPointerException("Default Activator is null");
        return plugin;
    }

    public static void log(String message) {
        getDefault().log(new Status(IStatus.INFO, getDefault().getBundle().getSymbolicName(), IStatus.INFO, message, null));
    }

    public static void log(String message, Throwable e) {
        getDefault().log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, message, e));
    }
}
