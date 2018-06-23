package org.eclipse.ecf.internal.tests.httpservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private BundleContext context;

    private static Activator instance;

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        instance = this;
        this.context = context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        this.context = null;
        instance = null;
    }

    public static Activator getDefault() {
        return instance;
    }

    public BundleContext getContext() {
        return context;
    }
}
