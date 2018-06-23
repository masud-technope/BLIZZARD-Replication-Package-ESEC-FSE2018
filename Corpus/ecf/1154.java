package org.eclipse.ecf.tests.remoteservice;

import org.eclipse.ecf.core.IContainerManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.tests.remoteservice";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    private ServiceTracker containerManagerServiceTracker;

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
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (containerManagerServiceTracker != null) {
            containerManagerServiceTracker.close();
            containerManagerServiceTracker = null;
        }
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    /**
	 * @return context.
	 * 
	 */
    public BundleContext getContext() {
        return context;
    }

    public IContainerManager getContainerManager() {
        if (containerManagerServiceTracker == null) {
            containerManagerServiceTracker = new ServiceTracker(this.context, IContainerManager.class.getName(), null);
            containerManagerServiceTracker.open();
        }
        return (IContainerManager) containerManagerServiceTracker.getService();
    }
}
