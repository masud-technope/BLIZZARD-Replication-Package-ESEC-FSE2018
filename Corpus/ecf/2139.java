package org.eclipse.ecf.twitter.client;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin implements BundleActivator {

    private IStatus status;

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.twitter.ui";

    // The shared instance
    private static Activator plugin;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
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
}
