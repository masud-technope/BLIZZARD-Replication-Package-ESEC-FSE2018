package org.eclipse.ecf.internal.tests.securestorage;

import org.eclipse.ecf.storage.IContainerStore;
import org.eclipse.ecf.storage.IIDStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private static Activator plugin;

    private BundleContext context;

    ServiceTracker storageServiceTracker;

    ServiceTracker containerStorageServiceTracker;

    public static Activator getDefault() {
        return plugin;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        plugin = this;
        this.context = context;
    }

    public IIDStore getIDStore() {
        if (storageServiceTracker == null) {
            storageServiceTracker = new ServiceTracker(context, IIDStore.class.getName(), null);
            storageServiceTracker.open();
        }
        return (IIDStore) storageServiceTracker.getService();
    }

    public IContainerStore getContainerStore() {
        if (containerStorageServiceTracker == null) {
            containerStorageServiceTracker = new ServiceTracker(context, IContainerStore.class.getName(), null);
            containerStorageServiceTracker.open();
        }
        return (IContainerStore) containerStorageServiceTracker.getService();
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (storageServiceTracker != null) {
            storageServiceTracker.close();
            storageServiceTracker = null;
        }
        if (containerStorageServiceTracker != null) {
            containerStorageServiceTracker.close();
            containerStorageServiceTracker = null;
        }
        this.context = null;
        plugin = null;
    }
}
