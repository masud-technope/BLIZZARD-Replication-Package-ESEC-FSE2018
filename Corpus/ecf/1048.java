/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.core;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.core.start.ECFStartJob;
import org.eclipse.ecf.core.start.IECFStart;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.internal.core.identity.Activator;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ECFPlugin implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf";

    private static final String ECFNAMESPACE = PLUGIN_ID;

    //$NON-NLS-1$
    private static final String CONTAINER_FACTORY_NAME = "containerFactory";

    //$NON-NLS-1$
    private static final String CONTAINER_FACTORY_EPOINT = ECFNAMESPACE + "." + CONTAINER_FACTORY_NAME;

    //$NON-NLS-1$
    private static final String STARTUP_NAME = "start";

    //$NON-NLS-1$
    public static final String START_EPOINT = ECFNAMESPACE + "." + STARTUP_NAME;

    //$NON-NLS-1$
    public static final String PLUGIN_RESOURCE_BUNDLE = ECFNAMESPACE + ".ECFPluginResources";

    //$NON-NLS-1$
    public static final String CLASS_ATTRIBUTE = "class";

    //$NON-NLS-1$
    public static final String NAME_ATTRIBUTE = "name";

    //$NON-NLS-1$
    public static final String DESCRIPTION_ATTRIBUTE = "description";

    //$NON-NLS-1$
    public static final String VALUE_ATTRIBUTE = "value";

    //$NON-NLS-1$
    public static final String SERVER_ATTRIBUTE = "server";

    //$NON-NLS-1$
    public static final String HIDDEN_ATTRIBUTE = "hidden";

    //$NON-NLS-1$
    public static final String ASYNCH_ATTRIBUTE = "asynchronous";

    //$NON-NLS-1$
    public static final String CONTAINER_NAME = "container";

    //$NON-NLS-1$
    public static final String CONTAINER_EPOINT = ECFNAMESPACE + "." + CONTAINER_NAME;

    //$NON-NLS-1$
    public static final String FACTORY_ATTRIBUTE = "factoryName";

    //$NON-NLS-1$
    public static final String ID_ATTRIBUTE = "containerId";

    //$NON-NLS-1$
    public static final String PARAMETER_ELEMENT = "parameter";

    //$NON-NLS-1$
    public static final String PARAMETER_NAME = "name";

    //$NON-NLS-1$
    public static final String PARAMETER_VALUE = "value";

    // The shared instance.
    private static ECFPlugin plugin;

    BundleContext context = null;

    private Map disposables = new HashMap(1);

    // This is Object rather than IExtensionRegistryManager to avoid loading 
    // IRegistryChangeListener class (optional)
    Object registryManager = null;

    private ServiceRegistration containerFactoryServiceRegistration;

    private ServiceRegistration containerManagerServiceRegistration;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    private AdapterManagerTracker adapterManagerTracker = null;

    private BundleActivator ecfTrustManager;

    /**
	 * Returns the shared instance.
	 * @return ECFPlugin
	 */
    public static synchronized ECFPlugin getDefault() {
        if (plugin == null)
            plugin = new ECFPlugin();
        return plugin;
    }

    public  ECFPlugin() {
    // null constructor
    }

    public void start(BundleContext ctxt) throws Exception {
        plugin = this;
        this.context = ctxt;
        // initialize the default ssl socket factory 
        try {
            //$NON-NLS-1$
            Class ecfSocketFactoryClass = Class.forName("org.eclipse.ecf.internal.ssl.ECFTrustManager");
            ecfTrustManager = (BundleActivator) ecfSocketFactoryClass.newInstance();
            ecfTrustManager.start(ctxt);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, "Unexpected Error in ECFPlugin.start", t));
        }
        // initialize from ContainerTypeDescription services
        if (containerTypeDescriptionTracker == null) {
            containerTypeDescriptionTracker = new ServiceTracker(this.context, ContainerTypeDescription.class.getName(), new ServiceTrackerCustomizer() {

                public Object addingService(ServiceReference reference) {
                    ContainerTypeDescription ctd = (ContainerTypeDescription) context.getService(reference);
                    if (ctd != null && ctd.getName() != null)
                        ContainerFactory.getDefault().addDescription(ctd);
                    return ctd;
                }

                public void modifiedService(ServiceReference reference, Object service) {
                // nothing
                }

                public void removedService(ServiceReference reference, Object service) {
                    IContainerFactory cf = ContainerFactory.getDefault();
                    cf.removeDescription((ContainerTypeDescription) service);
                }
            });
            containerTypeDescriptionTracker.open();
        }
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
                if (registry != null) {
                    registryManager = new IRegistryChangeListener() {

                        public void registryChanged(IRegistryChangeEvent event) {
                            final IExtensionDelta factoryDeltas[] = event.getExtensionDeltas(ECFNAMESPACE, CONTAINER_FACTORY_NAME);
                            for (int i = 0; i < factoryDeltas.length; i++) {
                                switch(factoryDeltas[i].getKind()) {
                                    case IExtensionDelta.ADDED:
                                        addContainerFactoryExtensions(factoryDeltas[i].getExtension().getConfigurationElements());
                                        break;
                                    case IExtensionDelta.REMOVED:
                                        removeContainerFactoryExtensions(factoryDeltas[i].getExtension().getConfigurationElements());
                                        break;
                                }
                            }
                            final IExtensionDelta containerDeltas[] = event.getExtensionDeltas(ECFNAMESPACE, CONTAINER_NAME);
                            for (int i = 0; i < containerDeltas.length; i++) {
                                switch(containerDeltas[i].getKind()) {
                                    case IExtensionDelta.ADDED:
                                        addContainerExtensions(containerDeltas[i].getExtension().getConfigurationElements());
                                        break;
                                    case IExtensionDelta.REMOVED:
                                        removeContainerExtensions(containerDeltas[i].getExtension().getConfigurationElements());
                                        break;
                                }
                            }
                        }
                    };
                    registry.addRegistryChangeListener((IRegistryChangeListener) registryManager);
                }
            }
        });
        // defer extension execution until first consumer calls
        final ServiceFactory sf = new ServiceFactory() {

            public Object getService(Bundle bundle, ServiceRegistration registration) {
                return ContainerFactory.getDefault();
            }

            public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            // NOP
            }
        };
        containerFactoryServiceRegistration = ctxt.registerService(IContainerFactory.class.getName(), sf, null);
        containerManagerServiceRegistration = ctxt.registerService(IContainerManager.class.getName(), sf, null);
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
                if (registry != null) {
                    final IExtensionPoint extensionPoint = registry.getExtensionPoint(START_EPOINT);
                    if (extensionPoint == null)
                        return;
                    IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
                    final String method = "runStartExtensions";
                    // For each configuration element
                    for (int m = 0; m < configurationElements.length; m++) {
                        final IConfigurationElement member = configurationElements[m];
                        try {
                            // The only required attribute is "class"
                            boolean sync = (member.getAttribute(ASYNCH_ATTRIBUTE) == null);
                            IECFStart clazz = (IECFStart) member.createExecutableExtension(CLASS_ATTRIBUTE);
                            // Create job to do start, and schedule
                            if (sync) {
                                IStatus result = null;
                                try {
                                    result = clazz.run(new NullProgressMonitor());
                                } catch (final Throwable e) {
                                    logException(method, "startup extension error", e);
                                }
                                if (result != null && !result.isOK())
                                    logException(result, result.getMessage(), result.getException());
                            } else {
                                final ECFStartJob job = new ECFStartJob(clazz.getClass().getName(), clazz);
                                job.schedule();
                            }
                        } catch (final CoreException e) {
                            logException(e.getStatus(), method, e);
                        } catch (final Exception e) {
                            logException(method, "Unknown start exception", e);
                        }
                    }
                }
            }
        });
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithoutRegistry() throws Exception {
                ECFPlugin.this.context.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(BaseContainer.Instantiator.NAME, new BaseContainer.Instantiator()), null);
            }
        });
    }

    private ServiceTracker containerTypeDescriptionTracker;

    public void initializeExtensions() {
        SafeRunner.run(new ExtensionRegistryRunnable(this.context) {

            protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
                if (registry != null) {
                    IExtensionPoint extensionPoint = registry.getExtensionPoint(CONTAINER_FACTORY_EPOINT);
                    if (extensionPoint == null)
                        return;
                    addContainerFactoryExtensions(extensionPoint.getConfigurationElements());
                    extensionPoint = registry.getExtensionPoint(CONTAINER_EPOINT);
                    if (extensionPoint == null)
                        return;
                    addContainerExtensions(extensionPoint.getConfigurationElements());
                }
            }
        });
    }

    public void stop(BundleContext ctxt) throws Exception {
        fireDisposables();
        this.disposables = null;
        SafeRunner.run(new ExtensionRegistryRunnable(ctxt) {

            protected void runWithRegistry(IExtensionRegistry registry) throws Exception {
                if (registry != null)
                    registry.removeRegistryChangeListener((IRegistryChangeListener) registryManager);
            }
        });
        this.registryManager = null;
        if (containerTypeDescriptionTracker != null) {
            containerTypeDescriptionTracker.close();
            containerTypeDescriptionTracker = null;
        }
        if (ecfTrustManager != null) {
            ecfTrustManager.stop(ctxt);
            ecfTrustManager = null;
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        if (containerFactoryServiceRegistration != null) {
            containerFactoryServiceRegistration.unregister();
            containerFactoryServiceRegistration = null;
        }
        if (containerManagerServiceRegistration != null) {
            containerManagerServiceRegistration.unregister();
            containerManagerServiceRegistration = null;
        }
        if (adapterManagerTracker != null) {
            adapterManagerTracker.close();
            adapterManagerTracker = null;
        }
        this.context = null;
    }

    public void addDisposable(IDisposable disposable) {
        disposables.put(disposable, null);
    }

    public void removeDisposable(IDisposable disposable) {
        disposables.remove(disposable);
    }

    protected void fireDisposables() {
        for (final Iterator i = disposables.keySet().iterator(); i.hasNext(); ) {
            final IDisposable d = (IDisposable) i.next();
            if (d != null)
                d.dispose();
        }
    }

    public Bundle getBundle() {
        if (context == null)
            return null;
        return context.getBundle();
    }

    private LogService systemLogService;

    protected LogService getLogService() {
        if (context == null) {
            if (systemLogService == null)
                systemLogService = new SystemLogService(PLUGIN_ID);
            return systemLogService;
        }
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        logService = (LogService) logServiceTracker.getService();
        if (logService == null)
            logService = new SystemLogService(PLUGIN_ID);
        return logService;
    }

    public void log(IStatus status) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }

    protected void logException(String method, String message, Throwable exception) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, exception));
        Trace.catching(PLUGIN_ID, ECFDebugOptions.EXCEPTIONS_CATCHING, ECFPlugin.class, method, exception);
    }

    protected void logException(IStatus status, String methodName, Throwable exception) {
        log(status);
        Trace.catching(status.getPlugin(), ECFDebugOptions.EXCEPTIONS_CATCHING, ECFPlugin.class, methodName, exception);
    }

    /**
	 * Remove extensions for container factory extension point
	 * 
	 * @param members
	 *            the members to remove
	 */
    protected void removeContainerFactoryExtensions(IConfigurationElement[] members) {
        // For each configuration element
        for (int m = 0; m < members.length; m++) {
            final IConfigurationElement member = members[m];
            String name = null;
            try {
                // Get name and get version, if available
                name = member.getAttribute(NAME_ATTRIBUTE);
                if (name == null)
                    name = member.getAttribute(CLASS_ATTRIBUTE);
                final IContainerFactory factory = ContainerFactory.getDefault();
                final ContainerTypeDescription cd = factory.getDescriptionByName(name);
                if (cd == null || !factory.containsDescription(cd))
                    continue;
                // remove
                factory.removeDescription(cd);
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("removeContainerFactoryExtensions", "Removed ContainerTypeDescription=" + cd);
            } catch (final Exception e) {
                logException("removeContainerFactoryExtensions", "Unexpected exception", e);
            }
        }
    }

    private void trace(String method, String message) {
        //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        Trace.trace(PLUGIN_ID, ECFDebugOptions.DEBUG, "TRACING " + ECFPlugin.class.getName() + "#" + method + " " + message);
    }

    void removeContainerExtensions(IConfigurationElement[] members) {
        // For each configuration element
        for (int m = 0; m < members.length; m++) {
            final IConfigurationElement member = members[m];
            // The only required attribute is "factoryName"
            String factoryName = member.getAttribute(FACTORY_ATTRIBUTE);
            // Skip over if factory name is invalid
            if (//$NON-NLS-1$
            factoryName == null || "".equals(factoryName))
                continue;
            IContainerManager manager = (IContainerManager) ContainerFactory.getDefault();
            IContainer[] containers = manager.getAllContainers();
            if (containers == null)
                continue;
            for (int i = 0; i < containers.length; i++) {
                ContainerTypeDescription containerTypeDescription = manager.getContainerTypeDescription(containers[i].getID());
                if (containerTypeDescription != null && containerTypeDescription.getName().equals(factoryName)) {
                    // Remove from manager
                    IContainer removedContainer = manager.removeContainer(containers[i]);
                    if (removedContainer != null) {
                        try {
                            containers[i].dispose();
                        } catch (Exception e) {
                            logException("removeContainerException", "Unexpected exception disposing container id=" + containers[i].getID(), e);
                        }
                    }
                }
            }
        }
    }

    /**
	 * Add container factory extension point extensions
	 * 
	 * @param members
	 *            to add
	 */
    protected void addContainerFactoryExtensions(IConfigurationElement[] members) {
        final IContainerFactory factory = ContainerFactory.getDefault();
        // For each configuration element
        for (int m = 0; m < members.length; m++) {
            final IConfigurationElement member = members[m];
            Object exten = null;
            String name = null;
            try {
                // Get value of containerFactory name attribute 
                name = member.getAttribute(NAME_ATTRIBUTE);
                if (name != null) {
                    ContainerTypeDescription ctd = factory.getDescriptionByName(name);
                    // If we've got one already by this name, then we skip this new one
                    if (ctd != null) {
                        // log with warning
                        //$NON-NLS-1$ //$NON-NLS-2$
                        log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Factory already has container type description with name=" + name + ".  Ignoring extension from " + member.getContributor().getName()));
                        // and continue
                        continue;
                    }
                }
                // The only required attribute is "class"
                exten = member.createExecutableExtension(CLASS_ATTRIBUTE);
                final String clazz = exten.getClass().getName();
                if (name == null)
                    name = clazz;
                // Get description, if present
                String description = member.getAttribute(DESCRIPTION_ATTRIBUTE);
                if (description == null)
                    //$NON-NLS-1$
                    description = "";
                String s = member.getAttribute(SERVER_ATTRIBUTE);
                final boolean server = (s == null) ? false : Boolean.valueOf(s).booleanValue();
                s = member.getAttribute(HIDDEN_ATTRIBUTE);
                final boolean hidden = (s == null) ? false : Boolean.valueOf(s).booleanValue();
                // Now make description instance
                final ContainerTypeDescription scd = new ContainerTypeDescription(name, (IContainerInstantiator) exten, description, server, hidden);
                if (factory.containsDescription(scd)) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Factory already has container type description=" + scd + ".  Ignoring extension from " + member.getContributor().getName()));
                    continue;
                }
                // Now add the description and we're ready to go.
                factory.addDescription(scd);
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("addContainerFactoryDescription", "added ContainerTypeDescription=" + scd);
            } catch (final CoreException e) {
                logException(e.getStatus(), "addContainerFactoryExtension", e);
            } catch (final Exception e) {
                logException("addContainerFactoryExtension", "Unexpected error", e);
            }
        }
    }

    void addContainerExtensions(IConfigurationElement[] members) {
        // For each configuration element
        for (int m = 0; m < members.length; m++) {
            final IConfigurationElement member = members[m];
            String factory = null;
            String id = null;
            try {
                // The only required attribute is "factoryName"
                factory = member.getAttribute(FACTORY_ATTRIBUTE);
                // Skip over if factory name is invalid
                if (factory == null || //$NON-NLS-1$
                "".equals(//$NON-NLS-1$
                factory))
                    continue;
                // get id attribute
                id = member.getAttribute(ID_ATTRIBUTE);
                id = //$NON-NLS-1$
                (id == null || "".equals(id)) ? //$NON-NLS-1$
                null : //$NON-NLS-1$
                id;
                Map parameters = getParametersForContainer(member);
                ContainerFactory.getDefault().createContainer(factory, id, parameters);
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("addContainerExtensions", "Created container with id=" + id);
            } catch (final CoreException e) {
                logException(e.getStatus(), "addContainerExtensions", e);
            } catch (final Exception e) {
                logException("addContainerExtensions", "Exception creating container with id=" + id, null);
            }
        }
    }

    Map getParametersForContainer(IConfigurationElement member) {
        IConfigurationElement[] elements = member.getChildren(PARAMETER_ELEMENT);
        if (elements == null)
            return null;
        Map results = null;
        for (int i = 0; i < elements.length; i++) {
            String name = elements[i].getAttribute(PARAMETER_NAME);
            String value = elements[i].getAttribute(PARAMETER_VALUE);
            if (//$NON-NLS-1$ //$NON-NLS-2$
            name != null && !"".equals(name) && value != null && !"".equals(value)) {
                if (results == null)
                    results = new Properties();
                results.put(name, value);
            }
        }
        return results;
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
}
