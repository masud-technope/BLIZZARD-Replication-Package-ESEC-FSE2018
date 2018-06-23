package org.eclipse.ecf.internal.examples.webinar;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.internal.examples.webinar.util";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        this.context = context;
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
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
	 * @return array of containers.
	 * 
	 */
    public IContainer[] getContainers() {
        final ServiceTracker tracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
        tracker.open();
        return ((IContainerManager) tracker.getService()).getAllContainers();
    }
}
