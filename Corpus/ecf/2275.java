package org.eclipse.ecf.internal.provider.local;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ExtensionRegistryRunnable;
import org.eclipse.ecf.internal.provider.local.container.LocalRemoteServiceContainerInstantiator;
import org.eclipse.ecf.provider.local.identity.LocalNamespace;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(final BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        SafeRunner.run(new ExtensionRegistryRunnable(bundleContext) {

            protected void runWithoutRegistry() throws Exception {
                bundleContext.registerService(Namespace.class, new LocalNamespace(), null);
                //$NON-NLS-1$//$NON-NLS-2$
                bundleContext.registerService(ContainerTypeDescription.class, new ContainerTypeDescription("ecf.local", new LocalRemoteServiceContainerInstantiator(), "Local Container Instantiator", false, false), null);
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }
}
