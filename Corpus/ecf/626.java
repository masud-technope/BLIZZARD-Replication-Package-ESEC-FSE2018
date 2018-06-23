package org.eclipse.ecf.internal.core.sharedobject;

import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.provider.ISharedObjectInstantiator;
import org.eclipse.ecf.core.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.sharedobject";

    //$NON-NLS-1$
    protected static final String CLASS_ATTRIBUTE = "class";

    //$NON-NLS-1$
    protected static final String NAME_ATTRIBUTE = "name";

    //$NON-NLS-1$
    protected static final String PROPERTY_ELEMENT_NAME = "property";

    //$NON-NLS-1$
    protected static final String VALUE_ATTRIBUTE = "value";

    //$NON-NLS-1$
    protected static final String NAMESPACE_NAME = "sharedObjectFactory";

    protected static final String SHAREDOBJECT_FACTORY_EPOINT = //$NON-NLS-1$
    PLUGIN_ID + "." + NAMESPACE_NAME;

    //$NON-NLS-1$
    protected static final String DESCRIPTION_ATTRIBUTE = "description";

    private static final int REMOVE_SHAREDOBJECT_ERRORCODE = 1001;

    private static final int FACTORY_NAME_COLLISION_ERRORCODE = 2001;

    // The shared instance
    private static Activator plugin;

    BundleContext context = null;

    private ServiceTracker logServiceTracker = null;

    private AdapterManagerTracker adapterManagerTracker = null;

    /**
	 * The constructor
	 */
    public  Activator() {
    // null constructor
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext ctxt) throws Exception {
        this.context = ctxt;
        plugin = this;
        SafeRunner.run(new ExtensionRegistryRunnable(context) {

            protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
                if (registry != null) {
                    IExtensionPoint extensionPoint = registry.getExtensionPoint(SHAREDOBJECT_FACTORY_EPOINT);
                    if (extensionPoint != null)
                        addSharedObjectExtensions(extensionPoint.getConfigurationElements());
                }
            }
        });
        //$NON-NLS-1$
        Trace.exiting(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_ENTERING, Activator.class, "start");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        //$NON-NLS-1$
        Trace.entering(Activator.PLUGIN_ID, SharedObjectDebugOptions.METHODS_EXITING, Activator.class, "stop");
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        plugin = null;
        this.context = null;
    }

    /**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
    public static Activator getDefault() {
        if (plugin == null) {
            plugin = new Activator();
        }
        return plugin;
    }

    public Bundle getBundle() {
        if (context == null)
            return null;
        return context.getBundle();
    }

    private LogService systemLogService = null;

    protected LogService getLogService() {
        if (context == null) {
            if (systemLogService == null)
                systemLogService = new SystemLogService(PLUGIN_ID);
            return systemLogService;
        }
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker<LogService, LogService>(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        return (LogService) logServiceTracker.getService();
    }

    public void log(IStatus status) {
        LogService logService = getLogService();
        if (logService != null) {
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
        }
    }

    public IAdapterManager getAdapterManager() {
        if (context == null)
            return null;
        // First, try to get the adapter manager via
        if (adapterManagerTracker == null) {
            adapterManagerTracker = new AdapterManagerTracker(this.context);
            adapterManagerTracker.open();
        }
        return adapterManagerTracker.getAdapterManager();
    }

    /**
	 * Remove extensions for shared object extension point
	 * 
	 * @param members
	 *            the members to remove
	 */
    void removeSharedObjectExtensions(IConfigurationElement[] members) {
        for (int m = 0; m < members.length; m++) {
            IConfigurationElement member = members[m];
            String name = null;
            try {
                name = member.getAttribute(NAME_ATTRIBUTE);
                if (name == null) {
                    name = member.getAttribute(CLASS_ATTRIBUTE);
                }
                if (name == null)
                    continue;
                ISharedObjectFactory factory = SharedObjectFactory.getDefault();
                SharedObjectTypeDescription sd = factory.getDescriptionByName(name);
                if (sd == null || !factory.containsDescription(sd)) {
                    continue;
                }
                // remove
                factory.removeDescription(sd);
                org.eclipse.ecf.core.util.Trace.trace(Activator.PLUGIN_ID, SharedObjectDebugOptions.DEBUG, //$NON-NLS-1$
                "removeSharedObjectExtensions.removedDescription(" + //$NON-NLS-1$
                sd + //$NON-NLS-1$
                ")");
            } catch (Exception e) {
                org.eclipse.ecf.core.util.Trace.catching(Activator.PLUGIN_ID, SharedObjectDebugOptions.EXCEPTIONS_CATCHING, Activator.class, "removeSharedObjectExtensions", e);
                getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, REMOVE_SHAREDOBJECT_ERRORCODE, "Exception removing sharedobject extension", e));
            }
        }
    }

    /**
	 * Add shared object extension point extensions
	 * 
	 * @param members
	 *            to add
	 */
    void addSharedObjectExtensions(IConfigurationElement[] members) {
        String bundleName = getDefault().getBundle().getSymbolicName();
        // For each configuration element
        for (int m = 0; m < members.length; m++) {
            IConfigurationElement member = members[m];
            // Get the label of the extender plugin and the ID of the extension.
            IExtension extension = member.getDeclaringExtension();
            ISharedObjectInstantiator exten = null;
            String name = null;
            try {
                // The only required attribute is "class"
                exten = (ISharedObjectInstantiator) member.createExecutableExtension(CLASS_ATTRIBUTE);
                name = member.getAttribute(NAME_ATTRIBUTE);
                if (name == null) {
                    name = member.getAttribute(CLASS_ATTRIBUTE);
                }
                // Get description, if present
                String description = member.getAttribute(DESCRIPTION_ATTRIBUTE);
                if (description == null) {
                    //$NON-NLS-1$
                    description = "";
                }
                IConfigurationElement[] propertyElements = member.getChildren(PROPERTY_ELEMENT_NAME);
                Properties props = new Properties();
                if (propertyElements != null) {
                    if (propertyElements.length > 0) {
                        for (int i = 0; i < propertyElements.length; i++) {
                            String name1 = propertyElements[i].getAttribute(NAME_ATTRIBUTE);
                            String value = propertyElements[i].getAttribute(VALUE_ATTRIBUTE);
                            if (//$NON-NLS-1$
                            name1 != null && !name1.equals("") && //$NON-NLS-1$
                            value != null && //$NON-NLS-1$
                            !value.equals("")) {
                                props.setProperty(name1, value);
                            }
                        }
                    }
                }
                // Get any property elements
                Map properties = props;
                // Now make description instance
                SharedObjectTypeDescription scd = new SharedObjectTypeDescription(name, exten, description, properties);
                org.eclipse.ecf.core.util.Trace.trace(Activator.PLUGIN_ID, SharedObjectDebugOptions.DEBUG, "setupSharedObjectExtensionPoint:createdDescription(" + //$NON-NLS-1$
                scd + ")");
                ISharedObjectFactory factory = SharedObjectFactory.getDefault();
                if (factory.containsDescription(scd))
                    throw new CoreException(new //$NON-NLS-1$
                    Status(//$NON-NLS-1$
                    IStatus.ERROR, //$NON-NLS-1$
                    bundleName, //$NON-NLS-1$
                    FACTORY_NAME_COLLISION_ERRORCODE, "name=" + //$NON-NLS-1$
                    name + //$NON-NLS-1$
                    ";extension point id=" + extension.getExtensionPointUniqueIdentifier(), null));
                // Now add the description and we're ready to go.
                factory.addDescription(scd);
                org.eclipse.ecf.core.util.Trace.trace(Activator.PLUGIN_ID, SharedObjectDebugOptions.DEBUG, "setupSharedObjectExtensionPoint.addedDescriptionToFactory(" + //$NON-NLS-1$
                scd + ")");
            } catch (CoreException e) {
                getDefault().log(e.getStatus());
                org.eclipse.ecf.core.util.Trace.catching(Activator.PLUGIN_ID, SharedObjectDebugOptions.EXCEPTIONS_CATCHING, Activator.class, "addSharedObjectExtensions", e);
            } catch (Exception e) {
                getDefault().log(new Status(IStatus.ERROR, bundleName, FACTORY_NAME_COLLISION_ERRORCODE, "name=" + name + ";extension point id=" + extension.getExtensionPointUniqueIdentifier(), null));
                org.eclipse.ecf.core.util.Trace.catching(Activator.PLUGIN_ID, SharedObjectDebugOptions.EXCEPTIONS_CATCHING, Activator.class, "addSharedObjectExtensions", e);
            }
        }
    }
}
