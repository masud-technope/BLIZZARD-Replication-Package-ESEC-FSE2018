package org.eclipse.ecf.tutorial.internal.lab1;

import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.tutorial.lab1";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker containerManagerTracker;

    private BundleContext context;

    public BundleContext getContext() {
        return context;
    }

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    public IContainerFactory getContainerFactory() {
        return getContainerManager().getContainerFactory();
    }

    public IContainerManager getContainerManager() {
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        this.context = context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (containerManagerTracker != null) {
            containerManagerTracker.close();
            containerManagerTracker = null;
        }
        plugin = null;
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

    /**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
