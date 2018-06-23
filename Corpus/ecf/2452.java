package org.eclipse.ecf.internal.examples.remoteservices.hello.host.rs;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.examples.remoteservices.hello.impl.Hello;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private BundleContext context;

    private ServiceTracker containerManagerServiceTracker;

    private IContainer container;

    private IRemoteServiceRegistration serviceRegistration;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        // Create R-OSGi Container
        IContainerManager containerManager = getContainerManagerService();
        container = containerManager.getContainerFactory().createContainer("ecf.r_osgi.peer");
        // Get remote service container adapter
        IRemoteServiceContainerAdapter containerAdapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
        // Register remote service
        serviceRegistration = containerAdapter.registerRemoteService(new String[] { IHello.class.getName() }, new Hello(), null);
        System.out.println("IHello RemoteService registered");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
        if (container != null) {
            container.disconnect();
            container = null;
        }
        if (containerManagerServiceTracker != null) {
            containerManagerServiceTracker.close();
            containerManagerServiceTracker = null;
        }
        this.context = null;
    }

    private IContainerManager getContainerManagerService() {
        if (containerManagerServiceTracker == null) {
            containerManagerServiceTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
            containerManagerServiceTracker.open();
        }
        return (IContainerManager) containerManagerServiceTracker.getService();
    }
}
