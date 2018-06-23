package org.eclipse.ecf.internal.storage;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.storage.IContainerStore;
import org.eclipse.ecf.storage.IIDStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.storage";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    private BundleContext context = null;

    private ServiceTracker adapterManagerTracker = null;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        plugin = this;
        this.context = context;
        IDStore idStore = new IDStore();
        ContainerStore containerStore = new ContainerStore(idStore);
        context.registerService(IIDStore.class.getName(), idStore, null);
        context.registerService(IContainerStore.class.getName(), containerStore, null);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        context = null;
        plugin = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        logService = (LogService) logServiceTracker.getService();
        if (logService == null)
            logService = new SystemLogService(PLUGIN_ID);
        return logService;
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

    public void log(IStatus status) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }
}
