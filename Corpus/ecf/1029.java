package org.eclipse.ecf.internal.provider.vbulletin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    public static final String NAMESPACE_IDENTIFIER = "ecf.vbulletin";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.internal.provider.vbulletin";

    // The shared instance
    private static Activator plugin;

    /**
	 * The constructor
	 */
    public  Activator() {
        plugin = this;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
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

    public static String getNamespaceIdentifier() {
        return NAMESPACE_IDENTIFIER;
    }
}
