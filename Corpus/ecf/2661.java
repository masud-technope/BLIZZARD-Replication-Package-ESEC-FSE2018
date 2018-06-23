package org.eclipse.ecf.internal.console;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private BundleContext context;

    private ECFCommandProvider commandProvider;

    private ServiceRegistration commandProviderRegistration;

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        this.commandProvider = new ECFCommandProvider(this.context);
        commandProviderRegistration = this.context.registerService(CommandProvider.class.getName(), commandProvider, null);
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        if (commandProviderRegistration != null) {
            commandProviderRegistration.unregister();
            commandProviderRegistration = null;
        }
        if (commandProvider != null) {
            commandProvider.dispose();
            commandProvider = null;
        }
        this.context = null;
    }
}
