package org.eclipse.ecf.internal.tests.filetransfer.jreprovider;

import org.eclipse.ecf.provider.filetransfer.IFileTransferProtocolToFactoryMapper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private ServiceTracker protocolToFactoryMapperTracker = null;

    private BundleContext context;

    private static Activator bundle;

    public static Activator getDefault() {
        return bundle;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        bundle = this;
        this.context = context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (protocolToFactoryMapperTracker != null) {
            protocolToFactoryMapperTracker.close();
            protocolToFactoryMapperTracker = null;
        }
        this.context = null;
        bundle = null;
    }

    public IFileTransferProtocolToFactoryMapper getProtocolToFactoryMapper() {
        if (protocolToFactoryMapperTracker == null) {
            protocolToFactoryMapperTracker = new ServiceTracker(context, IFileTransferProtocolToFactoryMapper.class.getName(), null);
            protocolToFactoryMapperTracker.open();
        }
        return (IFileTransferProtocolToFactoryMapper) protocolToFactoryMapperTracker.getService();
    }
}
