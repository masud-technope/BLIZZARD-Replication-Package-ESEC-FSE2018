package org.eclipse.ecf.internal.provider.filetransfer.scp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.LogHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.provider.scp";

    static Activator instance;

    private BundleContext context;

    private ServiceTracker logServiceTracker = null;

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        instance = this;
        this.context = ctxt;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
        }
        instance = null;
        this.context = null;
    }

    public static Activator getDefault() {
        return instance;
    }

    protected LogService getLogService() {
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        return (LogService) logServiceTracker.getService();
    }

    public void log(IStatus status) {
        final LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }
}
