package org.eclipse.ecf.internal.examples.provider.trivial;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.core.util.ExtensionRegistryRunnable;
import org.eclipse.ecf.examples.provider.trivial.identity.TrivialNamespace;
import org.eclipse.ecf.internal.examples.provider.trivial.container.TrivialContainerInstantiator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.ecf.internal.examples.provider.trivial";

    // The shared instance
    private static Activator plugin;

    private BundleContext context;

    /**
	 * The constructor
	 */
    public  Activator() {
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext context) throws Exception {
        this.context = context;
        plugin = this;
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                // If we don't have a registry, then register trivial namespace
                Activator.this.context.registerService(Namespace.class, new TrivialNamespace(TrivialNamespace.NAME), null);
                // And create and register ContainerTypeDescription
                Activator.this.context.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(TrivialContainerInstantiator.NAME, (IContainerInstantiator) new TrivialContainerInstantiator()), null);
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        return plugin;
    }

    public BundleContext getContext() {
        return context;
    }
}
