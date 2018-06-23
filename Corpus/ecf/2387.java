/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.Activator;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.DebugOptions;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.LogUtility;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.PropertiesUtil;
import org.eclipse.ecf.remoteservice.IExtendedRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.IOSGiRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRSAConsumerContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceUnregisteredEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.remoteserviceadmin.EndpointPermission;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.util.tracker.ServiceTracker;

/**
 * ECF implementation of <a
 * href="http://www.osgi.org/download/r4v42/r4.enterprise.pdf">OSGI 4.2 Remote
 * Service Admin service</a>. This service can be used by topology managers to
 * to export and/or import remote services with any <a
 * href="http://wiki.eclipse.org/ECF_Connection_Creation_and_Management">ECF
 * container</a> that implements the <a
 * href="http://wiki.eclipse.org/ECF/API_Docs#Remote_Services_API">ECF remote
 * service API</a>.
 */
public class RemoteServiceAdmin implements org.osgi.service.remoteserviceadmin.RemoteServiceAdmin {

    //$NON-NLS-1$
    public static final String SERVICE_PROP = "org.eclipse.ecf.rsa";

    private static final boolean disableServiceVersionChecking = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.disableServiceVersionChecking", //$NON-NLS-1$
    "false")).booleanValue();

    private Bundle clientBundle;

    private boolean hostAutoCreateContainer = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.hostAutoCreateContainer", //$NON-NLS-1$
    "true")).booleanValue();

    private String[] hostDefaultConfigTypes = new String[] { System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.hostDefaultConfigType", "ecf.generic.server") };

    private boolean consumerAutoCreateContainer = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.consumerAutoCreateContainer", //$NON-NLS-1$
    "true")).booleanValue();

    private Object eventAdminTrackerLock = new Object();

    private ServiceTracker eventAdminTracker;

    private Object remoteServiceAdminListenerTrackerLock = new Object();

    private ServiceTracker remoteServiceAdminListenerTracker;

    private HostContainerSelector defaultHostContainerSelector;

    private ServiceRegistration defaultHostContainerSelectorRegistration;

    private ConsumerContainerSelector defaultConsumerContainerSelector;

    private ServiceRegistration defaultConsumerContainerSelectorRegistration;

    private Collection<org.osgi.service.remoteserviceadmin.ExportRegistration> exportedRegistrations;

    private Collection<org.osgi.service.remoteserviceadmin.ImportRegistration> importedRegistrations;

    private Collection<ExportRegistration> localExportedRegistrations = new ArrayList<ExportRegistration>();

    private Collection<ImportRegistration> localImportedRegistrations = new ArrayList<ImportRegistration>();

    private ServiceRegistration eventListenerHookRegistration;

    /**
	 * @return List of exported registrations
	 * @since 4.2
	 */
    public List<ExportRegistration> getExportedRegistrations() {
        synchronized (exportedRegistrations) {
            return new ArrayList(exportedRegistrations);
        }
    }

    /**
	 * @return List of imported registrations
	 * @since 4.2
	 */
    public List<ImportRegistration> getImportedRegistrations() {
        synchronized (importedRegistrations) {
            return new ArrayList(importedRegistrations);
        }
    }

    public  RemoteServiceAdmin(Bundle clientBundle) {
        this(clientBundle, new ArrayList<org.osgi.service.remoteserviceadmin.ExportRegistration>(), new ArrayList<org.osgi.service.remoteserviceadmin.ImportRegistration>());
    }

    /**
	 * @param clientBundle client bundle for RemoteServiceAdmin instance
	 * @param exportedRegistrations of exported registrations
	 * @param importedRegistrations of imported registrations
	 * @since 4.1
	 */
    public  RemoteServiceAdmin(Bundle clientBundle, Collection<org.osgi.service.remoteserviceadmin.ExportRegistration> exportedRegistrations, Collection<org.osgi.service.remoteserviceadmin.ImportRegistration> importedRegistrations) {
        this.clientBundle = clientBundle;
        Assert.isNotNull(this.clientBundle);
        this.exportedRegistrations = exportedRegistrations;
        this.importedRegistrations = importedRegistrations;
        // Only setup defaults if it hasn't already been done by some other
        // Remote Service Admin instance
        Properties props = new Properties();
        props.put(org.osgi.framework.Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
        // host container selector
        ServiceReference[] hostContainerSelectorRefs = null;
        BundleContext rsaBundleContext = getRSABundleContext();
        try {
            hostContainerSelectorRefs = rsaBundleContext.getServiceReferences(IHostContainerSelector.class.getName(), null);
        } catch (InvalidSyntaxException e) {
        }
        // register a default only if no others already exist
        if (hostContainerSelectorRefs == null || hostContainerSelectorRefs.length == 0) {
            defaultHostContainerSelector = new HostContainerSelector(hostDefaultConfigTypes, hostAutoCreateContainer);
            defaultHostContainerSelectorRegistration = rsaBundleContext.registerService(IHostContainerSelector.class.getName(), defaultHostContainerSelector, (Dictionary) props);
        }
        // consumer container selector
        ServiceReference[] consumerContainerSelectorRefs = null;
        try {
            consumerContainerSelectorRefs = rsaBundleContext.getServiceReferences(IConsumerContainerSelector.class.getName(), null);
        } catch (InvalidSyntaxException e) {
        }
        // register a default only if no others already exist
        if (consumerContainerSelectorRefs == null || consumerContainerSelectorRefs.length == 0) {
            defaultConsumerContainerSelector = new ConsumerContainerSelector(consumerAutoCreateContainer);
            defaultConsumerContainerSelectorRegistration = rsaBundleContext.registerService(IConsumerContainerSelector.class.getName(), defaultConsumerContainerSelector, (Dictionary) props);
        }
        eventListenerHookRegistration = rsaBundleContext.registerService(EventListenerHook.class.getName(), new RSAEventListenerHook(), null);
    }

    private void handleServiceUnregistering(ServiceReference serviceReference) {
        List<ExportRegistration> ers = getExportedRegistrations();
        for (ExportRegistration exportedRegistration : ers) {
            if (exportedRegistration.match(serviceReference)) {
                trace("handleServiceUnregistering", //$NON-NLS-1$ //$NON-NLS-2$
                "closing exportRegistration for serviceReference=" + serviceReference);
                exportedRegistration.close();
            }
        }
    }

    class RSAEventListenerHook implements EventListenerHook {

        public void event(ServiceEvent event, Map<BundleContext, Collection<ListenerInfo>> listeners) {
            switch(event.getType()) {
                case ServiceEvent.UNREGISTERING:
                    handleServiceUnregistering(event.getServiceReference());
                    break;
                default:
                    break;
            }
        }
    }

    private boolean validExportedInterfaces(ServiceReference serviceReference, String[] exportedInterfaces) {
        if (exportedInterfaces == null || exportedInterfaces.length == 0)
            return false;
        List<String> objectClassList = Arrays.asList((String[]) serviceReference.getProperty(org.osgi.framework.Constants.OBJECTCLASS));
        for (int i = 0; i < exportedInterfaces.length; i++) if (!objectClassList.contains(exportedInterfaces[i]))
            return false;
        return true;
    }

    // RemoteServiceAdmin service interface impl methods
    public Collection<org.osgi.service.remoteserviceadmin.ExportRegistration> exportService(final ServiceReference<?> serviceReference, Map<String, ?> op) {
        trace("exportService", //$NON-NLS-1$ //$NON-NLS-2$
        "serviceReference=" + serviceReference + ",properties=" + //$NON-NLS-1$
        op);
        final Map<String, ?> overridingProperties = PropertiesUtil.mergeProperties(serviceReference, op == null ? Collections.EMPTY_MAP : op);
        // get exported interfaces
        final String[] exportedInterfaces = PropertiesUtil.getExportedInterfaces(serviceReference, overridingProperties);
        if (exportedInterfaces == null)
            throw new IllegalArgumentException(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_INTERFACES + //$NON-NLS-1$
            " not set");
        // verifyExportedInterfaces
        if (!validExportedInterfaces(serviceReference, exportedInterfaces))
            return Collections.EMPTY_LIST;
        // Get optional exported configs
        String[] ecs = PropertiesUtil.getStringArrayFromPropertyValue(overridingProperties.get(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_CONFIGS));
        if (ecs == null) {
            ecs = PropertiesUtil.getStringArrayFromPropertyValue(serviceReference.getProperty(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_CONFIGS));
        }
        final String[] exportedConfigs = ecs;
        // Get all intents (service.intents, service.exported.intents,
        // service.exported.intents.extra)
        final String[] serviceIntents = PropertiesUtil.getServiceIntents(serviceReference, overridingProperties);
        // Create result registrations.  This collection will be returned
        Collection<ExportRegistration> resultRegistrations = new ArrayList<ExportRegistration>();
        // check for previously exported registration for the serviceReference
        synchronized (exportedRegistrations) {
            ExportEndpoint exportEndpoint = findExistingExportEndpoint(serviceReference, null);
            // If found then create a second ExportRegistration from endpoint
            if (exportEndpoint != null) {
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                trace("exportService", "serviceReference=" + serviceReference + " export endpoint already exists=" + exportEndpoint + ".  Returning new ExportRegistration for existing endpoint");
                ExportRegistration reg = new ExportRegistration(exportEndpoint);
                addExportRegistration(reg);
                resultRegistrations.add(reg);
            }
        }
        // If the serviceReference hasn't already been exported before (above)
        if (resultRegistrations.size() == 0) {
            // Get a host container selector
            final IHostContainerSelector hostContainerSelector = getHostContainerSelector();
            // and use it to select ECF remote service containers that match given exported
            // interfaces, configs, and intents
            IRemoteServiceContainer[] rsContainers = null;
            try {
                rsContainers = AccessController.doPrivileged(new PrivilegedExceptionAction() {

                    public Object run() throws SelectContainerException {
                        return hostContainerSelector.selectHostContainers(serviceReference, (Map<String, Object>) overridingProperties, exportedInterfaces, exportedConfigs, serviceIntents);
                    }
                });
            } catch (PrivilegedActionException e) {
                Exception except = e.getException();
                String errorMessage = "Failed to select host container";
                if (except instanceof SelectContainerException) {
                    SelectContainerException sce = (SelectContainerException) except;
                    Throwable sceCause = sce.getCause();
                    if (sceCause instanceof ContainerCreateException) {
                        Map<String, Object> props = new HashMap<String, Object>(overridingProperties);
                        props.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, "0");
                        props.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, "import.error.config");
                        props.put(RemoteConstants.ENDPOINT_ID, "export.error.id");
                        props.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, StringID.class.getName());
                        ExportRegistration errorRegistration = new RemoteServiceAdmin.ExportRegistration(sceCause, new EndpointDescription(serviceReference, props));
                        addExportRegistration(errorRegistration);
                        resultRegistrations.add(errorRegistration);
                    } else
                        throw new IllegalArgumentException(errorMessage, except);
                } else
                    throw new IllegalArgumentException(errorMessage, except);
            }
            // If no registration exist (no errorRegistration added above)
            if (resultRegistrations.size() == 0) {
                // If no containers found above, log warning and return
                if (rsContainers == null || rsContainers.length == 0) {
                    String errorMessage = "No containers found for serviceReference=" + serviceReference + " properties=" + overridingProperties + //$NON-NLS-1$//$NON-NLS-2$
                    ". Remote service NOT EXPORTED";
                    logWarning(//$NON-NLS-1$
                    "exportService", //$NON-NLS-1$
                    errorMessage);
                    return Collections.EMPTY_LIST;
                }
                // actually do the export
                synchronized (exportedRegistrations) {
                    // For all selected containers
                    for (int i = 0; i < rsContainers.length; i++) {
                        Map endpointDescriptionProperties = createExportEndpointDescriptionProperties(serviceReference, (Map<String, Object>) overridingProperties, exportedInterfaces, serviceIntents, rsContainers[i]);
                        // otherwise, actually export the service to create
                        // a new ExportEndpoint and use it to create a new
                        // ExportRegistration
                        EndpointDescription endpointDescription = new EndpointDescription(endpointDescriptionProperties);
                        checkEndpointPermission(endpointDescription, EndpointPermission.EXPORT);
                        ExportRegistration exportRegistration = null;
                        try {
                            // Actually do the export and return export
                            // registration
                            exportRegistration = exportService(serviceReference, overridingProperties, exportedInterfaces, rsContainers[i], endpointDescriptionProperties);
                        } catch (Exception e) {
                            exportRegistration = new ExportRegistration(e, endpointDescription);
                        }
                        addExportRegistration(exportRegistration);
                        // We add it to the results in either success or error case
                        resultRegistrations.add(exportRegistration);
                    }
                }
            }
        }
        // publish all activeExportRegistrations
        for (ExportRegistration exportReg : resultRegistrations) publishExportEvent(exportReg);
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("exportService", "exported registrations=" + resultRegistrations);
        // and return
        return new ArrayList<org.osgi.service.remoteserviceadmin.ExportRegistration>(resultRegistrations);
    }

    public org.osgi.service.remoteserviceadmin.ImportRegistration importService(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("importService", "endpointDescription=" + endpointDescription);
        // First, make sure that the client bundle has the IMPORT endpoint
        // permission
        checkEndpointPermission(endpointDescription, EndpointPermission.IMPORT);
        final EndpointDescription ed = (endpointDescription instanceof EndpointDescription) ? (EndpointDescription) endpointDescription : new EndpointDescription(endpointDescription.getProperties());
        // Now get IConsumerContainerSelector, to select the ECF container
        // for the given endpointDescription
        final IConsumerContainerSelector consumerContainerSelector = getConsumerContainerSelector();
        // If there is none, then we can go no further
        if (consumerContainerSelector == null) {
            //$NON-NLS-1$
            String errorMessage = "No consumerContainerSelector available";
            //$NON-NLS-1$
            logError("importService", errorMessage, new SelectContainerException(errorMessage, null, null));
            // As specified in section 122.5.2, return null
            return null;
        }
        // Select the rsContainer to handle the endpoint description
        IRemoteServiceContainer rsContainer = null;
        ImportRegistration importRegistration = null;
        try {
            rsContainer = AccessController.doPrivileged(new PrivilegedExceptionAction<IRemoteServiceContainer>() {

                public IRemoteServiceContainer run() throws SelectContainerException {
                    return consumerContainerSelector.selectConsumerContainer(ed);
                }
            });
        } catch (PrivilegedActionException e) {
            logError("importService", "Unexpected exception in selectConsumerContainer", e.getException());
            importRegistration = new ImportRegistration(ed, e.getException());
        } catch (Exception e) {
            logError("importService", "Unexpected exception in selectConsumerContainer", e);
            importRegistration = new ImportRegistration(ed, e);
        }
        // If none found, log an error and return null
        if (rsContainer == null && importRegistration == null) {
            String errorMessage = //$NON-NLS-1$
            "No remote service container selected for endpoint=" + endpointDescription + ". Remote service NOT IMPORTED";
            //$NON-NLS-1$
            logError("importService", errorMessage, new SelectContainerException(errorMessage, null, null));
            // As specified in section 122.5.2, return null
            return null;
        }
        // registration
        synchronized (importedRegistrations) {
            if (importRegistration == null) {
                ImportEndpoint importEndpoint = findImportEndpoint(ed);
                importRegistration = ((importEndpoint != null) ? new ImportRegistration(importEndpoint) : importService(ed, rsContainer));
            }
            addImportRegistration(importRegistration);
        }
        // publish import event
        publishImportEvent(importRegistration);
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("importService", "importRegistration=" + importRegistration);
        // Finally, return the importRegistration. It may be null or not.
        return importRegistration;
    }

    public Collection<org.osgi.service.remoteserviceadmin.ExportReference> getExportedServices() {
        Collection<org.osgi.service.remoteserviceadmin.ExportReference> results = new ArrayList<org.osgi.service.remoteserviceadmin.ExportReference>();
        synchronized (exportedRegistrations) {
            // assumes that a SecurityException is thrown when accessed without READ permission
            if (exportedRegistrations.isEmpty())
                checkRSAReadAccess();
            for (org.osgi.service.remoteserviceadmin.ExportRegistration reg : exportedRegistrations) {
                org.osgi.service.remoteserviceadmin.ExportReference eRef = reg.getExportReference();
                if (eRef != null && checkEndpointPermissionRead(//$NON-NLS-1$
                "getExportedServices", eRef.getExportedEndpoint()))
                    results.add(eRef);
            }
        }
        return results;
    }

    private void checkRSAReadAccess() {
        Map<String, Object> props = new HashMap();
        props.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, UUID.randomUUID().toString());
        props.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { UUID.randomUUID().toString() });
        props.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, UUID.randomUUID().toString());
        checkEndpointPermission(new org.osgi.service.remoteserviceadmin.EndpointDescription(props), org.osgi.service.remoteserviceadmin.EndpointPermission.READ);
    }

    public Collection<org.osgi.service.remoteserviceadmin.ImportReference> getImportedEndpoints() {
        Collection<org.osgi.service.remoteserviceadmin.ImportReference> results = new ArrayList<org.osgi.service.remoteserviceadmin.ImportReference>();
        synchronized (importedRegistrations) {
            // assumes that a SecurityException is thrown when accessed without READ permission
            if (importedRegistrations.isEmpty())
                checkRSAReadAccess();
            for (org.osgi.service.remoteserviceadmin.ImportRegistration reg : importedRegistrations) {
                org.osgi.service.remoteserviceadmin.ImportReference iRef = reg.getImportReference();
                if (iRef != null && checkEndpointPermissionRead("getImportedEndpoints", iRef.getImportedEndpoint()))
                    results.add(iRef);
            }
        }
        return results;
    }

    // end RemoteServiceAdmin service interface impl methods
    private boolean checkEndpointPermissionRead(String methodName, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        try {
            checkEndpointPermission(endpointDescription, EndpointPermission.READ);
            return true;
        } catch (SecurityException e) {
            logError(methodName, "permission check failed for read access to endpointDescription=" + endpointDescription, e);
            return false;
        }
    }

    private BundleContext getClientBundleContext() {
        return clientBundle.getBundleContext();
    }

    private BundleContext getRSABundleContext() {
        return Activator.getContext();
    }

    private Bundle getRSABundle() {
        BundleContext bc = Activator.getContext();
        if (bc == null)
            return null;
        return bc.getBundle();
    }

    private void addImportRegistration(ImportRegistration importRegistration) {
        synchronized (importedRegistrations) {
            importedRegistrations.add(importRegistration);
            localImportedRegistrations.add(importRegistration);
        }
    }

    private void addExportRegistration(ExportRegistration exportRegistration) {
        synchronized (exportedRegistrations) {
            exportedRegistrations.add(exportRegistration);
            localExportedRegistrations.add(exportRegistration);
        }
    }

    private boolean removeExportRegistration(ExportRegistration exportRegistration) {
        synchronized (exportedRegistrations) {
            localExportedRegistrations.remove(exportRegistration);
            return exportedRegistrations.remove(exportRegistration);
        }
    }

    private boolean removeImportRegistration(ImportRegistration importRegistration) {
        synchronized (importedRegistrations) {
            localExportedRegistrations.remove(importRegistration);
            return importedRegistrations.remove(importRegistration);
        }
    }

    private void checkEndpointPermission(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, String permissionType) throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null)
            return;
        sm.checkPermission(new EndpointPermission(endpointDescription, Activator.getDefault().getFrameworkUUID(), permissionType));
    }

    class ExportEndpoint {

        private ServiceReference serviceReference;

        private EndpointDescription endpointDescription;

        private IRemoteServiceRegistration rsRegistration;

        private Set<ExportRegistration> activeExportRegistrations = new HashSet<ExportRegistration>();

        private Map<String, Object> originalProperties;

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ExportEndpoint[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("serviceReference=").append(serviceReference).append(";");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("rsRegistration=").append(rsRegistration).append("]");
            return buf.toString();
        }

         ExportEndpoint(ServiceReference serviceReference, EndpointDescription endpointDescription, IRemoteServiceRegistration reg, Map<String, Object> originalProperties) {
            Assert.isNotNull(serviceReference);
            this.serviceReference = serviceReference;
            Assert.isNotNull(endpointDescription);
            this.endpointDescription = endpointDescription;
            Assert.isNotNull(reg);
            this.rsRegistration = reg;
            Assert.isNotNull(originalProperties);
            this.originalProperties = originalProperties;
        }

        synchronized ID getContainerID() {
            return endpointDescription.getContainerID();
        }

        synchronized ServiceReference getServiceReference() {
            return serviceReference;
        }

        synchronized EndpointDescription getEndpointDescription() {
            return endpointDescription;
        }

        synchronized IRemoteServiceRegistration getRemoteServiceRegistration() {
            return rsRegistration;
        }

        synchronized boolean addExportRegistration(ExportRegistration exportRegistration) {
            return this.activeExportRegistrations.add(exportRegistration);
        }

        synchronized boolean close(ExportRegistration exportRegistration) {
            boolean removed = this.activeExportRegistrations.remove(exportRegistration);
            if (removed && activeExportRegistrations.size() == 0) {
                if (rsRegistration != null) {
                    rsRegistration.unregister();
                    rsRegistration = null;
                }
                serviceReference = null;
                endpointDescription = null;
                originalProperties = null;
            }
            return removed;
        }

        synchronized EndpointDescription update(Map properties) {
            // As per ExportRegistraiton.update javadocs, query the
            // serviceReference for current properties
            Map<String, Object> serviceReferenceProperties = PropertiesUtil.copyProperties(serviceReference, new HashMap());
            // As per ExportRegistration.update javadocs, if properties argument
            // is null,
            // use the original ED properties
            // Get copy of original remote service properties
            Map<String, Object> rsProperties = PropertiesUtil.copyProperties(this.originalProperties, new HashMap<String, Object>());
            Map<String, Object> updateProperties = (properties == null) ? rsProperties : PropertiesUtil.copyProperties(properties, rsProperties);
            Map<String, Object> updatedEDProperties = PropertiesUtil.mergeProperties(updateProperties, serviceReferenceProperties);
            // update timestamp
            updatedEDProperties.put(RemoteConstants.ENDPOINT_TIMESTAMP, System.currentTimeMillis());
            // Create new endpoint description, and this will be our updated
            // EndpointDescription
            this.endpointDescription = new EndpointDescription(updatedEDProperties);
            return this.endpointDescription;
        }
    }

    /**
	 * @since 4.2
	 */
    public class ExportRegistration implements org.osgi.service.remoteserviceadmin.ExportRegistration {

        private ExportReference exportReference;

        private boolean closed = false;

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ExportRegistration[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("exportReference=").append(exportReference).append(";");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("closed=").append(closed).append("]");
            return buf.toString();
        }

         ExportRegistration(ExportEndpoint exportEndpoint) {
            Assert.isNotNull(exportEndpoint);
            exportEndpoint.addExportRegistration(this);
            this.exportReference = new ExportReference(exportEndpoint);
        }

         ExportRegistration(Throwable exception, EndpointDescription errorEndpointDescription) {
            Assert.isNotNull(exception);
            this.exportReference = new ExportReference(exception, errorEndpointDescription);
        }

        public ID getContainerID() {
            return (closed) ? null : exportReference.getContainerID();
        }

        ServiceReference getServiceReference() {
            return (closed) ? null : exportReference.getExportedService();
        }

        public long getRemoteServiceId() {
            return (closed) ? 0 : exportReference.getRemoteServiceId();
        }

        public org.osgi.service.remoteserviceadmin.ExportReference getExportReference() {
            Throwable t = getException();
            if (t != null)
                return null;
            return (closed) ? null : exportReference;
        }

        boolean match(ServiceReference serviceReference) {
            return match(serviceReference, null);
        }

        boolean match(ServiceReference serviceReference, ID containerID) {
            ServiceReference ourServiceReference = getServiceReference();
            if (ourServiceReference == null)
                return false;
            boolean serviceReferenceCompare = ourServiceReference.equals(serviceReference);
            // references
            if (containerID == null)
                return serviceReferenceCompare;
            ID ourContainerID = getContainerID();
            if (ourContainerID == null)
                return false;
            return serviceReferenceCompare && ourContainerID.equals(containerID);
        }

        synchronized ExportEndpoint getExportEndpoint(ServiceReference serviceReference, ID containerID) {
            return (closed ? null : (match(serviceReference, containerID) ? exportReference.getExportEndpoint() : null));
        }

        IRemoteServiceRegistration getRemoteServiceRegistration() {
            return (closed) ? null : exportReference.getRemoteServiceRegistration();
        }

        EndpointDescription getEndpointDescription() {
            return (closed) ? null : exportReference.getEndpointDescription();
        }

        public void close() {
            boolean publish = false;
            ID containerID = null;
            Throwable exception = null;
            EndpointDescription endpointDescription = null;
            ExportReference exRef = null;
            synchronized (this) {
                // Only do this once
                if (!closed) {
                    containerID = getContainerID();
                    exception = getException();
                    endpointDescription = getEndpointDescription();
                    exRef = this.exportReference;
                    publish = exportReference.close(this);
                    this.exportReference = null;
                    closed = true;
                }
            }
            removeExportRegistration(this);
            Bundle rsaBundle = getRSABundle();
            // Only publish events
            if (publish && rsaBundle != null && exRef != null)
                publishEvent(new RemoteServiceAdminEvent(containerID, RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, rsaBundle, exRef, exception, endpointDescription), endpointDescription);
        }

        public Throwable getException() {
            return (closed) ? updateException : exportReference.getException();
        }

        private Throwable updateException;

        public org.osgi.service.remoteserviceadmin.EndpointDescription update(Map<String, ?> properties) {
            // to IllegalStateException and return null
            if (closed) {
                updateException = new //$NON-NLS-1$
                IllegalStateException(//$NON-NLS-1$
                "Update failed since ExportRegistration already closed");
                return null;
            }
            // Update exportReference. If exception is thrown, or update
            // returns null then set updateException and return null;
            EndpointDescription updatedED = null;
            Map<String, Object> props = PropertiesUtil.copySerializableProperties(properties, new TreeMap<String, Object>());
            try {
                updatedED = exportReference.update(props);
            } catch (RuntimeException e) {
                updateException = e;
                return null;
            }
            // If the exportReference returned null, then the underlying ExportEndpoint was null
            if (updatedED == null) {
                updateException = new //$NON-NLS-1$
                IllegalStateException(//$NON-NLS-1$
                "Update failed because ExportEndpoint was null");
                return null;
            }
            // We've succeeded in the update to set updateException to null
            // in case it was set by previous update
            this.updateException = null;
            Bundle rsaBundle = getRSABundle();
            // Notify with EXPORT_UPDATE
            if (rsaBundle != null)
                publishEvent(new RemoteServiceAdminEvent(getContainerID(), RemoteServiceAdminEvent.EXPORT_UPDATE, rsaBundle, this.exportReference, null, updatedED), updatedED);
            return updatedED;
        }
    }

    /**
	 * @since 4.2
	 */
    public class ExportReference implements org.osgi.service.remoteserviceadmin.ExportReference {

        private ExportEndpoint exportEndpoint;

        private Throwable exception;

        private EndpointDescription errorEndpointDescription;

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ExportReference[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("exportEndpoint=" + exportEndpoint).append(";");
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            buf.append("exception=").append(exception).append(";").append("]");
            return buf.toString();
        }

         ExportReference(ExportEndpoint exportEndpoint) {
            Assert.isNotNull(exportEndpoint);
            this.exportEndpoint = exportEndpoint;
        }

        synchronized EndpointDescription update(Map<String, ?> properties) {
            if (exportEndpoint == null)
                return null;
            return exportEndpoint.update(properties);
        }

         ExportReference(Throwable exception, EndpointDescription errorEndpointDescription) {
            Assert.isNotNull(exception);
            this.exception = exception;
            Assert.isNotNull(exception);
            this.errorEndpointDescription = errorEndpointDescription;
        }

        synchronized Throwable getException() {
            return exception;
        }

        synchronized boolean close(ExportRegistration exportRegistration) {
            if (exportEndpoint == null)
                return false;
            boolean result = exportEndpoint.close(exportRegistration);
            exportEndpoint = null;
            return result;
        }

        synchronized ExportEndpoint getExportEndpoint() {
            return exportEndpoint;
        }

        synchronized IRemoteServiceRegistration getRemoteServiceRegistration() {
            return (exportEndpoint == null) ? null : exportEndpoint.getRemoteServiceRegistration();
        }

        public synchronized ID getContainerID() {
            return (exportEndpoint == null) ? null : exportEndpoint.getContainerID();
        }

        public synchronized long getRemoteServiceId() {
            IRemoteServiceRegistration r = getRemoteServiceRegistration();
            return (r == null) ? 0 : r.getID().getContainerRelativeID();
        }

        public synchronized ServiceReference getExportedService() {
            return (exportEndpoint == null) ? null : exportEndpoint.getServiceReference();
        }

        public synchronized org.osgi.service.remoteserviceadmin.EndpointDescription getExportedEndpoint() {
            return (exportEndpoint == null) ? null : exportEndpoint.getEndpointDescription();
        }

        synchronized EndpointDescription getEndpointDescription() {
            return (exportEndpoint == null) ? errorEndpointDescription : exportEndpoint.getEndpointDescription();
        }
    }

    class ImportEndpoint {

        private IRemoteServiceContainer rsContainer;

        private IRemoteService rs;

        private IRemoteServiceListener rsListener;

        private EndpointDescription endpointDescription;

        private IRemoteServiceReference rsReference;

        private ServiceRegistration proxyRegistration;

        private Set<ImportRegistration> activeImportRegistrations = new HashSet<ImportRegistration>();

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ImportEndpoint[");
            //$NON-NLS-1$
            buf.append("proxyRegistration=").append(proxyRegistration);
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            buf.append("rsReference=").append(rsReference).append(";").append("]");
            return buf.toString();
        }

         ImportEndpoint(IRemoteServiceContainer rsContainer, IRemoteServiceReference rsReference, IRemoteService rs, ServiceRegistration proxyRegistration, EndpointDescription endpointDescription) {
            this.rsContainer = rsContainer;
            this.rsReference = rsReference;
            this.endpointDescription = endpointDescription;
            this.rs = rs;
            this.proxyRegistration = proxyRegistration;
            this.rsListener = new RemoteServiceListener();
            // Add the remoteservice listener to the container adapter, so that
            // the rsListener notified asynchronously if our underlying remote
            // service reference is unregistered locally due to disconnect or remote
            // ejection
            this.rsContainer.getContainerAdapter().addRemoteServiceListener(this.rsListener);
        }

        synchronized EndpointDescription getEndpointDescription() {
            return endpointDescription;
        }

        synchronized ServiceRegistration getProxyRegistration() {
            return proxyRegistration;
        }

        synchronized ID getContainerID() {
            return (rsReference == null) ? null : rsReference.getContainerID();
        }

        synchronized boolean addImportRegistration(ImportRegistration importRegistration) {
            return this.activeImportRegistrations.add(importRegistration);
        }

        synchronized boolean close(ImportRegistration importRegistration) {
            boolean removed = this.activeImportRegistrations.remove(importRegistration);
            if (removed && activeImportRegistrations.size() == 0) {
                if (proxyRegistration != null) {
                    try {
                        proxyRegistration.unregister();
                    } catch (Throwable t) {
                    }
                    proxyRegistration = null;
                }
                IRemoteServiceContainerAdapter rsContainerAdapter = rsContainer.getContainerAdapter();
                if (rsContainerAdapter != null) {
                    if (rsReference != null) {
                        rsContainerAdapter.ungetRemoteService(rsReference);
                        rsReference = null;
                    }
                    if (rsListener != null) {
                        rsContainerAdapter.removeRemoteServiceListener(rsListener);
                        rsListener = null;
                    }
                }
                rs = null;
                endpointDescription = null;
                rsContainer = null;
            }
            return removed;
        }

        synchronized boolean match(IRemoteServiceID remoteServiceID) {
            if (remoteServiceID == null || rsReference == null)
                return false;
            return rsReference.getID().equals(remoteServiceID);
        }

        synchronized boolean match(EndpointDescription ed) {
            if (activeImportRegistrations.size() == 0)
                return false;
            return this.endpointDescription.isSameService(ed);
        }

        synchronized void update(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
            if (proxyRegistration == null)
                return;
            // Get or create ECF endpoint description
            EndpointDescription updatedEndpoint = (endpoint instanceof EndpointDescription) ? ((EndpointDescription) endpoint) : new EndpointDescription(endpoint.getProperties());
            // Create new proxy properties from updatedEndpoint and rsReference and rs
            Map newProxyProperties = createProxyProperties(rsContainer.getContainer().getID(), updatedEndpoint, rsReference, rs);
            // set the endpoint description with the proxy properties
            updatedEndpoint.setPropertiesOverrides(newProxyProperties);
            // set this endpointDescription to updatedEndpoint
            this.endpointDescription = updatedEndpoint;
            // Set proxyRegistration properties
            this.proxyRegistration.setProperties(PropertiesUtil.createDictionaryFromMap(newProxyProperties));
        }
    }

    /**
	 * @since 4.2
	 */
    public class ImportRegistration implements org.osgi.service.remoteserviceadmin.ImportRegistration {

        private ImportReference importReference;

        private boolean closed = false;

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ImportRegistration[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("importReference=").append(importReference).append(";");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("closed=").append(closed).append("]");
            return buf.toString();
        }

         ImportRegistration(ImportEndpoint importEndpoint) {
            Assert.isNotNull(importEndpoint);
            importEndpoint.addImportRegistration(this);
            this.importReference = new ImportReference(importEndpoint);
        }

         ImportRegistration(EndpointDescription errorEndpointDescription, Throwable exception) {
            this.importReference = new ImportReference(errorEndpointDescription, exception);
        }

        public ID getContainerID() {
            return (closed) ? null : importReference.getContainerID();
        }

        public long getRemoteServiceId() {
            return (closed) ? 0 : importReference.getRemoteServiceId();
        }

        EndpointDescription getEndpointDescription() {
            return (closed) ? null : importReference.getEndpointDescription();
        }

        boolean match(IRemoteServiceID remoteServiceID) {
            return (closed) ? null : importReference.match(remoteServiceID);
        }

        boolean match(EndpointDescription ed) {
            return (getImportEndpoint(ed) != null);
        }

        ImportEndpoint getImportEndpoint(EndpointDescription ed) {
            return (closed) ? null : importReference.match(ed);
        }

        public org.osgi.service.remoteserviceadmin.ImportReference getImportReference() {
            Throwable t = getException();
            if (t != null)
                return null;
            return (closed) ? null : importReference;
        }

        public void close() {
            boolean publish = false;
            ID containerID = null;
            Throwable exception = null;
            EndpointDescription endpointDescription = null;
            ImportReference imRef = null;
            synchronized (this) {
                // only do this once
                if (!closed) {
                    containerID = getContainerID();
                    exception = getException();
                    endpointDescription = getEndpointDescription();
                    imRef = this.importReference;
                    publish = importReference.close(this);
                    this.importReference = null;
                    closed = true;
                }
            }
            removeImportRegistration(this);
            Bundle rsaBundle = getRSABundle();
            if (publish && rsaBundle != null && imRef != null)
                publishEvent(new RemoteServiceAdminEvent(containerID, RemoteServiceAdminEvent.IMPORT_UNREGISTRATION, rsaBundle, imRef, exception, endpointDescription), endpointDescription);
        }

        public Throwable getException() {
            return (closed) ? updateException : importReference.getException();
        }

        private Throwable updateException;

        public boolean update(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
            // to IllegalStateException and return null
            if (closed) {
                updateException = new IllegalStateException("Update failed since ImportRegistration already closed");
                return false;
            }
            try {
                importReference.update(endpoint);
            } catch (Exception e) {
                updateException = e;
                return false;
            }
            Bundle rsaBundle = getRSABundle();
            EndpointDescription ed = getEndpointDescription();
            if (rsaBundle != null)
                publishEvent(new RemoteServiceAdminEvent(getContainerID(), RemoteServiceAdminEvent.IMPORT_UPDATE, rsaBundle, this.importReference, null, ed), ed);
            return true;
        }
    }

    /**
	 * @since 4.2
	 */
    public class ImportReference implements org.osgi.service.remoteserviceadmin.ImportReference {

        private ImportEndpoint importEndpoint;

        private Throwable exception;

        private EndpointDescription errorEndpointDescription;

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("ImportReference[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("importEndpoint=").append(importEndpoint).append(";");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("exception=").append(exception).append("]");
            return buf.toString();
        }

         ImportReference(ImportEndpoint importEndpoint) {
            Assert.isNotNull(importEndpoint);
            this.importEndpoint = importEndpoint;
        }

        synchronized void update(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint) {
            if (importEndpoint != null)
                importEndpoint.update(endpoint);
        }

         ImportReference(EndpointDescription endpointDescription, Throwable exception) {
            Assert.isNotNull(exception);
            this.exception = exception;
            Assert.isNotNull(endpointDescription);
            this.errorEndpointDescription = endpointDescription;
        }

        synchronized Throwable getException() {
            return exception;
        }

        synchronized boolean match(IRemoteServiceID remoteServiceID) {
            return (importEndpoint == null) ? false : importEndpoint.match(remoteServiceID);
        }

        synchronized ImportEndpoint match(EndpointDescription ed) {
            if (importEndpoint != null && importEndpoint.match(ed))
                return importEndpoint;
            return null;
        }

        synchronized EndpointDescription getEndpointDescription() {
            return (importEndpoint == null) ? errorEndpointDescription : importEndpoint.getEndpointDescription();
        }

        public synchronized ID getContainerID() {
            return (importEndpoint == null) ? null : importEndpoint.getContainerID();
        }

        public synchronized long getRemoteServiceId() {
            if (importEndpoint == null)
                return 0;
            IRemoteServiceReference ref = importEndpoint.rsReference;
            if (ref == null)
                return 0;
            return ref.getID().getContainerRelativeID();
        }

        public synchronized ServiceReference getImportedService() {
            return (importEndpoint == null) ? null : importEndpoint.getProxyRegistration().getReference();
        }

        public synchronized org.osgi.service.remoteserviceadmin.EndpointDescription getImportedEndpoint() {
            return (importEndpoint == null) ? null : importEndpoint.getEndpointDescription();
        }

        synchronized boolean close(ImportRegistration importRegistration) {
            if (importEndpoint == null)
                return false;
            boolean result = importEndpoint.close(importRegistration);
            importEndpoint = null;
            return result;
        }
    }

    private void publishEvent(final RemoteServiceAdminEvent event, EndpointDescription endpointDescription) {
        // send event synchronously to RemoteServiceAdminListeners
        EndpointPermission perm = new EndpointPermission(endpointDescription, Activator.getDefault().getFrameworkUUID(), EndpointPermission.READ);
        // notify synchronously all appropriate listeners (those with READ
        // permission)
        final RemoteServiceAdminListener[] listeners = getListeners(perm);
        if (listeners != null)
            for (int i = 0; i < listeners.length; i++) {
                final RemoteServiceAdminListener listener = listeners[i];
                SafeRunner.run(new ISafeRunnable() {

                    public void handleException(Throwable exception) {
                        //$NON-NLS-1$ //$NON-NLS-2$
                        logError("publishEvent", "Exeption in RemoteServiceAdminListener.remoteAdminEvent for listener=" + listener, exception);
                    }

                    public void run() throws Exception {
                        listener.remoteAdminEvent(event);
                    }
                });
            }
        // Now also post the event asynchronously to EventAdmin
        postEvent(event, endpointDescription);
    }

    private void postEvent(RemoteServiceAdminEvent event, EndpointDescription endpointDescription) {
        int eventType = event.getType();
        String eventTypeName = null;
        String registrationTypeName = null;
        switch(eventType) {
            case (RemoteServiceAdminEvent.EXPORT_REGISTRATION):
                //$NON-NLS-1$
                eventTypeName = "EXPORT_REGISTRATION";
                //$NON-NLS-1$
                registrationTypeName = "export.registration";
                break;
            case (RemoteServiceAdminEvent.EXPORT_ERROR):
                //$NON-NLS-1$
                eventTypeName = "EXPORT_ERROR";
                //$NON-NLS-1$
                registrationTypeName = "export.registration";
                break;
            case (RemoteServiceAdminEvent.EXPORT_UNREGISTRATION):
                //$NON-NLS-1$
                eventTypeName = "EXPORT_UNREGISTRATION";
                //$NON-NLS-1$
                registrationTypeName = "export.registration";
                break;
            case (RemoteServiceAdminEvent.EXPORT_WARNING):
                //$NON-NLS-1$
                eventTypeName = "EXPORT_WARNING";
                //$NON-NLS-1$
                registrationTypeName = "export.registration";
                break;
            case (RemoteServiceAdminEvent.EXPORT_UPDATE):
                //$NON-NLS-1$
                eventTypeName = "EXPORT_UPDATE";
                //$NON-NLS-1$
                registrationTypeName = "export.registration";
                break;
            case (RemoteServiceAdminEvent.IMPORT_REGISTRATION):
                //$NON-NLS-1$
                eventTypeName = "IMPORT_REGISTRATION";
                //$NON-NLS-1$
                registrationTypeName = "import.registration";
                break;
            case (RemoteServiceAdminEvent.IMPORT_ERROR):
                //$NON-NLS-1$
                eventTypeName = "IMPORT_ERROR";
                //$NON-NLS-1$
                registrationTypeName = "import.registration";
                break;
            case (RemoteServiceAdminEvent.IMPORT_UNREGISTRATION):
                //$NON-NLS-1$
                eventTypeName = "IMPORT_UNREGISTRATION";
                //$NON-NLS-1$
                registrationTypeName = "import.registration";
                break;
            case (RemoteServiceAdminEvent.IMPORT_WARNING):
                //$NON-NLS-1$
                eventTypeName = "IMPORT_WARNING";
                //$NON-NLS-1$
                registrationTypeName = "import.registration";
                break;
            case (RemoteServiceAdminEvent.IMPORT_UPDATE):
                //$NON-NLS-1$
                eventTypeName = "IMPORT_UPDATE";
                //$NON-NLS-1$
                registrationTypeName = "import.registration";
                break;
        }
        if (eventTypeName == null) {
            logError("postEvent", //$NON-NLS-1$ //$NON-NLS-2$
            "Event type=" + eventType + " not understood for event=" + event + //$NON-NLS-1$ //$NON-NLS-2$
            ".  Not posting");
            return;
        }
        //$NON-NLS-1$
        final String topic = "org/osgi/service/remoteserviceadmin/" + eventTypeName;
        Bundle rsaBundle = getRSABundle();
        if (rsaBundle == null) {
            logError("postEvent", //$NON-NLS-1$ //$NON-NLS-2$
            "RSA Bundle is null.  Not posting remote service admin event=" + event);
            return;
        }
        final Dictionary eventProperties = new Properties();
        //$NON-NLS-1$
        eventProperties.put("bundle", rsaBundle);
        //$NON-NLS-1$
        eventProperties.put(//$NON-NLS-1$
        "bundle.id", new Long(rsaBundle.getBundleId()));
        //$NON-NLS-1$
        eventProperties.put(//$NON-NLS-1$
        "bundle.symbolicname", rsaBundle.getSymbolicName());
        //$NON-NLS-1$
        eventProperties.put("bundle.version", rsaBundle.getVersion());
        List<String> result = new ArrayList<String>();
        Map signers1 = clientBundle.getSignerCertificates(Bundle.SIGNERS_ALL);
        for (Iterator i = signers1.keySet().iterator(); i.hasNext(); ) result.add(i.next().toString());
        String[] signers = (String[]) result.toArray(new String[result.size()]);
        if (signers != null && signers.length > 0)
            //$NON-NLS-1$
            eventProperties.put("bundle.signer", signers);
        Throwable t = event.getException();
        if (t != null) {
            //$NON-NLS-1$
            eventProperties.put("cause", t);
            // Additions for RSA 1.1 section 122.7.1
            //$NON-NLS-1$
            eventProperties.put("exception", t);
            //$NON-NLS-1$
            eventProperties.put("exception.class", t.getClass().getName());
            String exceptionMessage = t.getMessage();
            if (exceptionMessage != null)
                //$NON-NLS-1$
                eventProperties.put(//$NON-NLS-1$
                "exception.message", //$NON-NLS-1$
                exceptionMessage);
        }
        long serviceId = endpointDescription.getServiceId();
        if (serviceId != 0)
            eventProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID, new Long(serviceId));
        String frameworkUUID = endpointDescription.getFrameworkUUID();
        if (frameworkUUID != null)
            eventProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID, frameworkUUID);
        String endpointId = endpointDescription.getId();
        if (endpointId != null)
            eventProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointId);
        List<String> interfaces = endpointDescription.getInterfaces();
        if (interfaces != null && interfaces.size() > 0)
            eventProperties.put(org.osgi.framework.Constants.OBJECTCLASS, interfaces.toArray(new String[interfaces.size()]));
        List<String> importedConfigs = endpointDescription.getConfigurationTypes();
        if (importedConfigs != null && importedConfigs.size() > 0)
            eventProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, importedConfigs.toArray(new String[importedConfigs.size()]));
        //$NON-NLS-1$
        eventProperties.put("timestamp", new Long(new Date().getTime()));
        //$NON-NLS-1$
        eventProperties.put("event", event);
        if (registrationTypeName != null)
            eventProperties.put(registrationTypeName, endpointDescription);
        final EventAdmin eventAdmin = AccessController.doPrivileged(new PrivilegedAction<EventAdmin>() {

            public EventAdmin run() {
                synchronized (eventAdminTrackerLock) {
                    eventAdminTracker = new ServiceTracker(getRSABundleContext(), EventAdmin.class.getName(), null);
                    eventAdminTracker.open();
                }
                return (EventAdmin) eventAdminTracker.getService();
            }
        });
        if (eventAdmin == null) {
            logError(//$NON-NLS-1$
            "postEvent", "No EventAdmin service available to send eventTopic=" + topic + //$NON-NLS-1$
            " eventProperties=" + //$NON-NLS-1$
            eventProperties);
            return;
        }
        // post via event admin
        AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                eventAdmin.postEvent(new Event(topic, eventProperties));
                return null;
            }
        });
    }

    private void publishExportEvent(ExportRegistration exportRegistration) {
        Throwable exception = exportRegistration.getException();
        org.osgi.service.remoteserviceadmin.ExportReference exportReference = (exception == null) ? exportRegistration.getExportReference() : null;
        EndpointDescription endpointDescription = exportRegistration.getEndpointDescription();
        RemoteServiceAdminEvent rsaEvent = new RemoteServiceAdminEvent(exportRegistration.getContainerID(), (exception == null) ? RemoteServiceAdminEvent.EXPORT_REGISTRATION : RemoteServiceAdminEvent.EXPORT_ERROR, getRSABundle(), exportReference, exception, endpointDescription);
        publishEvent(rsaEvent, endpointDescription);
    }

    private void publishImportEvent(ImportRegistration importRegistration) {
        Throwable exception = importRegistration.getException();
        org.osgi.service.remoteserviceadmin.ImportReference importReference = (exception == null) ? importRegistration.getImportReference() : null;
        EndpointDescription endpointDescription = importRegistration.getEndpointDescription();
        RemoteServiceAdminEvent rsaEvent = new RemoteServiceAdminEvent(importRegistration.getContainerID(), (exception == null) ? RemoteServiceAdminEvent.IMPORT_REGISTRATION : RemoteServiceAdminEvent.IMPORT_ERROR, getRSABundle(), importReference, exception, endpointDescription);
        publishEvent(rsaEvent, endpointDescription);
    }

    private RemoteServiceAdminListener[] getListeners(EndpointPermission perm) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                synchronized (remoteServiceAdminListenerTrackerLock) {
                    if (remoteServiceAdminListenerTracker == null) {
                        remoteServiceAdminListenerTracker = new ServiceTracker(getRSABundleContext(), RemoteServiceAdminListener.class.getName(), null);
                        remoteServiceAdminListenerTracker.open();
                    }
                    return null;
                }
            }
        });
        ServiceReference[] unfilteredRefs = remoteServiceAdminListenerTracker.getServiceReferences();
        if (unfilteredRefs == null)
            return null;
        // Filter by Bundle.hasPermission
        List<ServiceReference> filteredRefs = new ArrayList<ServiceReference>();
        for (ServiceReference ref : unfilteredRefs) if (perm == null || ref.getBundle().hasPermission(perm))
            filteredRefs.add(ref);
        List<RemoteServiceAdminListener> results = new ArrayList<RemoteServiceAdminListener>();
        for (final ServiceReference ref : filteredRefs) {
            RemoteServiceAdminListener l = AccessController.doPrivileged(new PrivilegedAction<RemoteServiceAdminListener>() {

                public RemoteServiceAdminListener run() {
                    return (RemoteServiceAdminListener) remoteServiceAdminListenerTracker.getService(ref);
                }
            });
            if (l != null)
                results.add(l);
        }
        return results.toArray(new RemoteServiceAdminListener[results.size()]);
    }

    private ExportEndpoint findExistingExportEndpoint(ServiceReference serviceReference, ID containerID) {
        for (org.osgi.service.remoteserviceadmin.ExportRegistration eReg : exportedRegistrations) {
            if (eReg instanceof ExportRegistration) {
                ExportEndpoint exportEndpoint = ((ExportRegistration) eReg).getExportEndpoint(serviceReference, containerID);
                if (exportEndpoint != null)
                    return exportEndpoint;
            }
        }
        return null;
    }

    private Object consumerContainerSelectorTrackerLock = new Object();

    private ServiceTracker consumerContainerSelectorTracker;

    private Object hostContainerSelectorTrackerLock = new Object();

    private ServiceTracker hostContainerSelectorTracker;

    protected IHostContainerSelector getHostContainerSelector() {
        return AccessController.doPrivileged(new PrivilegedAction<IHostContainerSelector>() {

            public IHostContainerSelector run() {
                synchronized (hostContainerSelectorTrackerLock) {
                    if (hostContainerSelectorTracker == null) {
                        hostContainerSelectorTracker = new ServiceTracker(getRSABundleContext(), IHostContainerSelector.class.getName(), null);
                        hostContainerSelectorTracker.open();
                    }
                }
                return (IHostContainerSelector) hostContainerSelectorTracker.getService();
            }
        });
    }

    protected IConsumerContainerSelector getConsumerContainerSelector() {
        return AccessController.doPrivileged(new PrivilegedAction<IConsumerContainerSelector>() {

            public IConsumerContainerSelector run() {
                synchronized (consumerContainerSelectorTrackerLock) {
                    if (consumerContainerSelectorTracker == null) {
                        consumerContainerSelectorTracker = new ServiceTracker(getRSABundleContext(), IConsumerContainerSelector.class.getName(), null);
                        consumerContainerSelectorTracker.open();
                    }
                }
                return (IConsumerContainerSelector) consumerContainerSelectorTracker.getService();
            }
        });
    }

    private ContainerTypeDescription getContainerTypeDescription(ID containerID) {
        return Activator.getDefault().getContainerManager().getContainerTypeDescription(containerID);
    }

    private boolean isClient(IContainer container) {
        ContainerTypeDescription ctd = getContainerTypeDescription(container.getID());
        if (ctd == null)
            return false;
        else
            return !ctd.isServer();
    }

    private Version getPackageVersion(final ServiceReference serviceReference, String serviceInterface, String packageName) {
        Object service = AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                return getRSABundleContext().getService(serviceReference);
            }
        });
        if (service == null)
            return null;
        List<Class> interfaces = new ArrayList<Class>();
        Class<?> serviceClass = service.getClass();
        while (!serviceClass.equals(Object.class)) {
            interfaces.addAll(Arrays.asList(serviceClass.getInterfaces()));
            serviceClass = serviceClass.getSuperclass();
        }
        Class[] interfaceClasses = interfaces.toArray(new Class[interfaces.size()]);
        if (interfaceClasses == null)
            return null;
        Class interfaceClass = null;
        for (int i = 0; i < interfaceClasses.length; i++) if (interfaceClasses[i].getName().equals(serviceInterface))
            interfaceClass = interfaceClasses[i];
        if (interfaceClass == null)
            return null;
        Bundle providingBundle = FrameworkUtil.getBundle(interfaceClass);
        if (providingBundle == null)
            return null;
        return getVersionForPackage(providingBundle, packageName);
    }

    private Map<String, Object> createExportEndpointDescriptionProperties(ServiceReference serviceReference, Map<String, Object> overridingProperties, String[] exportedInterfaces, String[] serviceIntents, IRemoteServiceContainer rsContainer) {
        IContainer container = rsContainer.getContainer();
        ID containerID = container.getID();
        Map<String, Object> endpointDescriptionProperties = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        // OSGi properties
        // OBJECTCLASS set to exportedInterfaces
        endpointDescriptionProperties.put(org.osgi.framework.Constants.OBJECTCLASS, exportedInterfaces);
        // Service interface versions
        for (int i = 0; i < exportedInterfaces.length; i++) {
            String packageName = getPackageName(exportedInterfaces[i]);
            String packageVersionKey = org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_ + packageName;
            // If it's pre-set...by registration or by overridingProperties,
            // then use that value
            String packageVersion = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, packageVersionKey);
            if (packageVersion == null) {
                Version version = getPackageVersion(serviceReference, exportedInterfaces[i], packageName);
                if (version != null && !version.equals(Version.emptyVersion))
                    packageVersion = version.toString();
            }
            // Only set the package version if we have a non-null value
            if (packageVersion != null)
                endpointDescriptionProperties.put(packageVersionKey, packageVersion);
        }
        // ENDPOINT_ID
        String endpointId = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID);
        if (endpointId == null)
            endpointId = UUID.randomUUID().toString();
        endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointId);
        // ECF ENDPOINT ID
        String ecfEndpointId = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, RemoteConstants.ENDPOINT_ID);
        if (ecfEndpointId == null)
            ecfEndpointId = containerID.getName();
        endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_ID, ecfEndpointId);
        // ENDPOINT_SERVICE_ID
        // This is always set to the value from serviceReference as per 122.5.1
        Long serviceId = (Long) serviceReference.getProperty(org.osgi.framework.Constants.SERVICE_ID);
        endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID, serviceId);
        // ENDPOINT_FRAMEWORK_ID
        String frameworkId = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID);
        if (frameworkId == null)
            frameworkId = Activator.getDefault().getFrameworkUUID();
        endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID, frameworkId);
        // REMOTE_CONFIGS_SUPPORTED
        String[] remoteConfigsSupported = getSupportedConfigs(container.getID());
        if (remoteConfigsSupported == null)
            remoteConfigsSupported = new String[0];
        endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED, remoteConfigsSupported);
        // SERVICE_IMPORTED_CONFIGS...set to constant value for all ECF
        // providers
        // supported (which is computed
        // for the exporting ECF container
        endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, remoteConfigsSupported);
        // SERVICE_INTENTS
        Object intents = PropertiesUtil.getPropertyValue(null, overridingProperties, org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS);
        if (intents == null)
            intents = serviceIntents;
        if (intents != null)
            endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS, intents);
        // REMOTE_INTENTS_SUPPORTED
        String[] remoteIntentsSupported = getSupportedIntents(container.getID());
        if (remoteIntentsSupported != null)
            endpointDescriptionProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED, remoteIntentsSupported);
        // ECF properties
        // ID namespace
        String idNamespace = containerID.getNamespace().getName();
        endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, idNamespace);
        // timestamp
        endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_TIMESTAMP, System.currentTimeMillis());
        // ENDPOINT_CONNECTTARGET_ID
        String connectTarget = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, RemoteConstants.ENDPOINT_CONNECTTARGET_ID);
        if (connectTarget == null && isClient(container)) {
            ID connectedID = container.getConnectedID();
            if (connectedID != null && !connectedID.equals(containerID))
                connectTarget = connectedID.getName();
        }
        if (connectTarget != null)
            endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_CONNECTTARGET_ID, connectTarget);
        // ENDPOINT_IDFILTER_IDS
        String[] idFilter = (String[]) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, RemoteConstants.ENDPOINT_IDFILTER_IDS);
        if (idFilter != null && idFilter.length > 0)
            endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_IDFILTER_IDS, idFilter);
        // ENDPOINT_REMOTESERVICE_FILTER
        String rsFilter = (String) PropertiesUtil.getPropertyValue(serviceReference, overridingProperties, RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER);
        if (rsFilter != null)
            endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER, rsFilter);
        Map<String, Object> nonReservedProps = PropertiesUtil.copyNonReservedProperties(overridingProperties, endpointDescriptionProperties);
        // Finally, copy all non-reserved properties
        return PropertiesUtil.removePrivateConfigProperties(remoteConfigsSupported, nonReservedProps);
    }

    private Map<String, Object> copyNonReservedProperties(ServiceReference serviceReference, Map<String, Object> overridingProperties, Map<String, Object> target) {
        // copy all other properties...from service reference
        PropertiesUtil.copyNonReservedProperties(serviceReference, target);
        // And override with overridingProperties
        PropertiesUtil.copyNonReservedProperties(overridingProperties, target);
        return target;
    }

    private String[] getSupportedConfigs(ID containerID) {
        ContainerTypeDescription ctd = getContainerTypeDescription(containerID);
        return (ctd == null) ? null : ctd.getSupportedConfigs();
    }

    private String[] getImportedConfigs(ID containerID, String[] exporterSupportedConfigs) {
        ContainerTypeDescription ctd = getContainerTypeDescription(containerID);
        return (ctd == null) ? null : ctd.getImportedConfigs(exporterSupportedConfigs);
    }

    private String[] getSupportedIntents(ID containerID) {
        ContainerTypeDescription ctd = getContainerTypeDescription(containerID);
        return (ctd == null) ? null : ctd.getSupportedIntents();
    }

    private ID[] getIDFilter(EndpointDescription endpointDescription, ID endpointID) {
        ID[] idFilter = endpointDescription.getIDFilter();
        // If it is null,
        return (idFilter == null) ? new ID[] { endpointID } : idFilter;
    }

    private String getRemoteServiceFilter(EndpointDescription endpointDescription) {
        long rsId = 0;
        // if the ECF remote service id is present in properties, allow it to
        // override
        Long l = endpointDescription.getRemoteServiceId();
        if (l != null)
            rsId = l.longValue();
        // endpoint description
        if (rsId == 0)
            rsId = endpointDescription.getServiceId();
        // If it's *still* zero, then just use the raw filter
        if (rsId == 0) {
            // filter
            return endpointDescription.getRemoteServiceFilter();
        } else {
            String edRsFilter = endpointDescription.getRemoteServiceFilter();
            // It's a real remote service id...so we return
            StringBuffer result = //$NON-NLS-1$
            new StringBuffer("(&(").append(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID).append("=").append(rsId).append(//$NON-NLS-1$ //$NON-NLS-2$
            ")");
            if (edRsFilter != null)
                result.append(edRsFilter);
            //$NON-NLS-1$
            result.append(")");
            return result.toString();
        }
    }

    private ImportEndpoint createAndRegisterProxy(final EndpointDescription endpointDescription, final IRemoteServiceContainer rsContainer, final IRemoteServiceReference selectedRsReference) throws Exception {
        final BundleContext proxyServiceFactoryContext = getProxyServiceFactoryContext(endpointDescription);
        if (proxyServiceFactoryContext == null)
            throw new NullPointerException("getProxyServiceFactoryContext returned null.  Cannot register proxy service factory");
        final IRemoteServiceContainerAdapter containerAdapter = rsContainer.getContainerAdapter();
        ID rsContainerID = rsContainer.getContainer().getID();
        // First get IRemoteService for selectedRsReference
        final IRemoteService rs = containerAdapter.getRemoteService(selectedRsReference);
        if (rs == null)
            throw new NullPointerException("getRemoteService returned null for selectedRsReference=" + //$NON-NLS-1$
            selectedRsReference + //$NON-NLS-1$
            ",rsContainerID=" + rsContainerID);
        final Map proxyProperties = createProxyProperties(rsContainerID, endpointDescription, selectedRsReference, rs);
        // sync sref props with endpoint props
        endpointDescription.setPropertiesOverrides(proxyProperties);
        final List<String> originalTypes = endpointDescription.getInterfaces();
        final List<String> asyncServiceTypes = endpointDescription.getAsyncInterfaces();
        final List<String> serviceTypes = new ArrayList<String>(originalTypes);
        if (asyncServiceTypes != null)
            for (String ast : asyncServiceTypes) if (ast != null && !serviceTypes.contains(ast))
                serviceTypes.add(ast);
        ServiceRegistration proxyRegistration = AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>() {

            public ServiceRegistration run() {
                return proxyServiceFactoryContext.registerService((String[]) serviceTypes.toArray(new String[serviceTypes.size()]), createProxyServiceFactory(endpointDescription, containerAdapter, selectedRsReference, rs), (Dictionary) PropertiesUtil.createDictionaryFromMap(proxyProperties));
            }
        });
        return new ImportEndpoint(rsContainer, selectedRsReference, rs, proxyRegistration, endpointDescription);
    }

    private BundleContext getProxyServiceFactoryContext(EndpointDescription endpointDescription) throws Exception {
        Activator a = Activator.getDefault();
        if (a == null)
            throw new NullPointerException("ECF RemoteServiceAdmin Activator cannot be null.");
        if (a.isOldEquinox()) {
            // In this case, we get the Bundle that exposes the first service
            // interface class
            BundleContext rsaContext = Activator.getContext();
            if (rsaContext == null)
                throw new NullPointerException("RSA BundleContext cannot be null");
            List<String> interfaces = endpointDescription.getInterfaces();
            Collection<Class> serviceInterfaceClasses = loadServiceInterfacesViaBundle(rsaContext.getBundle(), interfaces.toArray(new String[interfaces.size()]));
            if (serviceInterfaceClasses.size() == 0)
                throw new NullPointerException("No interface classes loadable for endpointDescription=" + endpointDescription);
            // Get the bundle responsible for the first service interface class
            Class serviceInterfaceClass = serviceInterfaceClasses.iterator().next();
            Bundle bundle = FrameworkUtil.getBundle(serviceInterfaceClass);
            if (bundle == null)
                throw new BundleException("Bundle for service interface class=" + //$NON-NLS-1$
                serviceInterfaceClass.getName() + //$NON-NLS-1$
                " cannot be found");
            int bundleState = bundle.getState();
            BundleContext bundleContext = bundle.getBundleContext();
            if (bundleContext == null)
                throw new BundleException("Bundle=" + //$NON-NLS-1$
                bundle.getSymbolicName() + //$NON-NLS-1$
                " in wrong state (" + //$NON-NLS-1$
                bundleState + ") for using BundleContext proxy service factory");
            return bundleContext;
        }
        return a.getProxyServiceFactoryBundleContext(endpointDescription);
    }

    private ServiceFactory createProxyServiceFactory(EndpointDescription endpointDescription, IRemoteServiceContainerAdapter containerAdapter, IRemoteServiceReference selectedRsReference, IRemoteService remoteService) {
        return new ProxyServiceFactory(endpointDescription.getInterfaceVersions(), containerAdapter, selectedRsReference, remoteService);
    }

    private Collection<Class> loadServiceInterfacesViaBundle(Bundle bundle, String[] interfaces) {
        List<Class> result = new ArrayList<Class>();
        for (int i = 0; i < interfaces.length; i++) {
            try {
                result.add(bundle.loadClass(interfaces[i]));
            } catch (ClassNotFoundException e) {
                logError("loadInterfacesViaBundle", "interface=" + interfaces[i] + " cannot be loaded by clientBundle=" + bundle.getSymbolicName(), e);
                continue;
            } catch (IllegalStateException e) {
                logError("loadInterfacesViaBundle", "interface=" + interfaces[i] + " cannot be loaded since clientBundle is in illegal state", e);
                continue;
            }
        }
        return result;
    }

    class ProxyServiceFactory implements ServiceFactory {

        private final IRemoteServiceContainerAdapter containerAdapter;

        private final IRemoteServiceReference rsReference;

        private IRemoteService remoteService;

        private Map<String, Version> interfaceVersions;

        private long remoteProxyCount = 0L;

        public  ProxyServiceFactory(Map<String, Version> interfaceVersions, IRemoteServiceContainerAdapter containerAdapter, IRemoteServiceReference rsReference, IRemoteService remoteService) {
            this.containerAdapter = containerAdapter;
            this.rsReference = rsReference;
            this.interfaceVersions = interfaceVersions;
            this.remoteService = remoteService;
        }

        public Object getService(Bundle bundle, ServiceRegistration registration) {
            Object proxy = createProxy(bundle, registration.getReference(), remoteService, interfaceVersions);
            if (proxy != null)
                remoteProxyCount++;
            return proxy;
        }

        public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            if (remoteProxyCount == 1L)
                containerAdapter.ungetRemoteService(rsReference);
            remoteProxyCount--;
            ungetProxyClassLoader(bundle);
        }
    }

    private Object createProxy(Bundle requestingBundle, ServiceReference serviceReference, IRemoteService remoteService, Map<String, Version> interfaceVersions) {
        // Get symbolicName once for possible use below
        String bundleSymbolicName = requestingBundle.getSymbolicName();
        // Get String[] via OBJECTCLASS constant property
        String[] serviceClassnames = (String[]) serviceReference.getProperty(org.osgi.framework.Constants.OBJECTCLASS);
        // Load as many of the serviceInterface classes as possible
        Collection<Class> serviceInterfaceClasses = loadServiceInterfacesViaBundle(requestingBundle, serviceClassnames);
        // load...otherwise the service can't be accessed
        if (serviceInterfaceClasses.size() < 1)
            throw new RuntimeException("ProxyServiceFactory cannot load any serviceInterfaces=" + serviceInterfaceClasses + //$NON-NLS-1$
            " for serviceReference=" + //$NON-NLS-1$
            serviceReference + //$NON-NLS-1$
            " via clientBundle=" + //$NON-NLS-1$
            bundleSymbolicName);
        // Now verify that the classes are of valid versions
        if (!verifyServiceInterfaceVersionsForProxy(requestingBundle, serviceInterfaceClasses, interfaceVersions))
            return null;
        // Now create/get class loader for proxy. This will typically
        // be an instance of ProxyClassLoader
        ClassLoader cl = getProxyClassLoader(requestingBundle);
        try {
            return remoteService.getProxy(cl, (Class[]) serviceInterfaceClasses.toArray(new Class[serviceInterfaceClasses.size()]));
        } catch (ECFException e) {
            throw new ServiceException("ProxyServiceFactory cannot create proxy for clientBundle=" + bundleSymbolicName + " from serviceReference=" + serviceReference, e);
        }
    }

    private Map<Bundle, ProxyClassLoader> proxyClassLoaders = new HashMap<Bundle, ProxyClassLoader>();

    private ClassLoader getProxyClassLoader(Bundle bundle) {
        ProxyClassLoader proxyClassLoaderForBundle = null;
        synchronized (proxyClassLoaders) {
            proxyClassLoaderForBundle = proxyClassLoaders.get(bundle);
            if (proxyClassLoaderForBundle == null) {
                proxyClassLoaderForBundle = new ProxyClassLoader(bundle);
                proxyClassLoaders.put(bundle, proxyClassLoaderForBundle);
            } else
                proxyClassLoaderForBundle.addServiceUseCount();
        }
        return proxyClassLoaderForBundle;
    }

    private void ungetProxyClassLoader(Bundle bundle) {
        synchronized (proxyClassLoaders) {
            ProxyClassLoader proxyClassLoaderForBundle = proxyClassLoaders.get(bundle);
            if (proxyClassLoaderForBundle != null) {
                int useCount = proxyClassLoaderForBundle.getServiceUseCount();
                if (useCount == 0)
                    proxyClassLoaders.remove(bundle);
                else
                    proxyClassLoaderForBundle.removeServiceUseCount();
            }
        }
    }

    protected class ProxyClassLoader extends ClassLoader {

        private Bundle loadingBundle;

        private int serviceUseCount = 0;

        public  ProxyClassLoader(Bundle loadingBundle) {
            this.loadingBundle = loadingBundle;
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            return loadingBundle.loadClass(name);
        }

        public int getServiceUseCount() {
            return serviceUseCount;
        }

        public void addServiceUseCount() {
            serviceUseCount++;
        }

        public void removeServiceUseCount() {
            serviceUseCount--;
        }
    }

    private String getPackageName(String className) {
        //$NON-NLS-1$
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex == -1)
            //$NON-NLS-1$
            return "";
        return className.substring(0, lastDotIndex);
    }

    private boolean comparePackageVersions(String packageName, Version remoteVersion, Version localVersion) throws RuntimeException {
        // If no remote version info, then set it to empty
        if (remoteVersion == null)
            remoteVersion = Version.emptyVersion;
        if (localVersion == null)
            localVersion = Version.emptyVersion;
        // We do strict comparison of remote with local
        int compareResult = localVersion.compareTo(remoteVersion);
        // Now check compare result, and throw exception to fail compare
        return (compareResult != 0);
    }

    private boolean verifyServiceInterfaceVersionsForProxy(Bundle bundle, Collection<Class> classes, Map<String, Version> interfaceVersions) {
        // For all service interface classes
        boolean result = true;
        // For enhancement https://bugs.eclipse.org/bugs/show_bug.cgi?id=472106
        if (disableServiceVersionChecking) {
            logWarning(//$NON-NLS-1$
            "verifyServiceInterfaceVersionsForProxy", "Service version checking disabled via service property");
            return result;
        }
        for (Class clazz : classes) {
            String className = clazz.getName();
            String packageName = getPackageName(className);
            // Now get remoteVersion, localVersion and do compare via package
            // version comparator service
            Version remoteVersion = interfaceVersions.get(className);
            Version localVersion = getPackageVersionViaRequestingBundle(packageName, bundle, remoteVersion);
            //$NON-NLS-1$
            LogUtility.trace(//$NON-NLS-1$
            "comparePackageVersions", DebugOptions.PACKAGE_VERSION_COMPARATOR, this.getClass(), //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
            "bundle=" + bundle.getSymbolicName() + ",class=" + clazz + ",packageName=" + packageName + //$NON-NLS-1$
            ",remoteVersion=" + remoteVersion + //$NON-NLS-1$
            ",localVersion=" + //$NON-NLS-1$
            localVersion);
            if (comparePackageVersions(packageName, remoteVersion, localVersion)) {
                logError("verifyServiceInterfaceVersionsForProxy", "Failed version check for proxy creation.  clientBundle=" + //$NON-NLS-1$
                clientBundle + //$NON-NLS-1$
                " interfaceType=" + className + " remoteVersion=" + remoteVersion + " localVersion=" + localVersion);
                result = false;
            }
        }
        return result;
    }

    private Version getVersionForMatchingCapability(String packageName, BundleCapability capability) {
        // If it's a package namespace (Import-Package)
        Map<String, Object> attributes = capability.getAttributes();
        // Then we get the package attribute
        String p = (String) attributes.get(BundleRevision.PACKAGE_NAMESPACE);
        // And compare it to the package name
        if (p != null && packageName.equals(p))
            return (Version) attributes.get(Constants.VERSION_ATTRIBUTE);
        return null;
    }

    private Version getPackageVersionForMatchingWire(String packageName, List<BundleWire> bundleWires, String namespace) {
        Version result = null;
        for (BundleWire wire : bundleWires) {
            if (namespace.equals(BundleRevision.PACKAGE_NAMESPACE))
                result = getVersionForMatchingCapability(packageName, wire.getCapability());
            else if (namespace.equals(BundleRevision.BUNDLE_NAMESPACE))
                // If it's a bundle namespace (Require-Bundle), then we get the
                // version for package
                // of the providing bundle
                result = getVersionForPackage(wire.getProvider().getBundle(), packageName);
            if (result != null)
                return result;
        }
        return result;
    }

    private Version getVersionForPackage(final Bundle providingBundle, String packageName) {
        Version result = null;
        BundleRevision providingBundleRevision = AccessController.doPrivileged(new PrivilegedAction<BundleRevision>() {

            public BundleRevision run() {
                return providingBundle.adapt(BundleRevision.class);
            }
        });
        if (providingBundleRevision == null)
            return null;
        List<BundleCapability> providerCapabilities = providingBundleRevision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
        for (BundleCapability c : providerCapabilities) {
            result = getVersionForMatchingCapability(packageName, c);
            if (result != null)
                return result;
        }
        return result;
    }

    private Version getPackageVersionViaRequestingBundle(String packageName, final Bundle requestingBundle, Version remoteVersion) {
        Version result = null;
        // First check the requesting bundle for the desired export package
        // capability
        BundleRevision requestingBundleRevision = AccessController.doPrivileged(new PrivilegedAction<BundleRevision>() {

            public BundleRevision run() {
                return requestingBundle.adapt(BundleRevision.class);
            }
        });
        if (requestingBundleRevision != null) {
            List<BundleCapability> requestingBundleCapabilities = requestingBundleRevision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
            for (BundleCapability requestingBundleCapability : requestingBundleCapabilities) {
                Version candidate = getVersionForMatchingCapability(packageName, requestingBundleCapability);
                // If found, set our result
                if (candidate != null) {
                    if (remoteVersion != null && candidate.equals(remoteVersion))
                        return candidate;
                    result = candidate;
                }
            }
        }
        // look in exported package that are wired to the requesting bundle
        if (result == null) {
            // look for wired exported packages
            BundleWiring requestingBundleWiring = requestingBundle.adapt(BundleWiring.class);
            if (requestingBundleWiring != null) {
                result = getPackageVersionForMatchingWire(packageName, requestingBundleWiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE), BundleRevision.PACKAGE_NAMESPACE);
                // in wired require bundles
                if (result == null)
                    result = getPackageVersionForMatchingWire(packageName, requestingBundleWiring.getRequiredWires(BundleRevision.BUNDLE_NAMESPACE), BundleRevision.BUNDLE_NAMESPACE);
            }
        }
        return result;
    }

    private IRemoteServiceReference selectRemoteServiceReference(Collection<IRemoteServiceReference> rsRefs, ID targetID, ID[] idFilter, Collection<String> interfaces, String rsFilter, IRemoteServiceContainer rsContainer) {
        if (rsRefs.size() == 0)
            return null;
        if (rsRefs.size() > 1) {
            logWarning("selectRemoteServiceReference", //$NON-NLS-1$ //$NON-NLS-2$
            "rsRefs=" + rsRefs + ",targetID=" + targetID + ",idFilter=" + //$NON-NLS-1$ //$NON-NLS-2$
            idFilter + ",interfaces=" + interfaces + ",rsFilter=" + //$NON-NLS-1$ //$NON-NLS-2$
            rsFilter + ",rsContainer=" + //$NON-NLS-1$
            rsContainer.getContainer().getID() + //$NON-NLS-1$
            " has " + //$NON-NLS-1$
            rsRefs.size() + " values.  Selecting the first element");
        }
        return rsRefs.iterator().next();
    }

    private Map createProxyProperties(ID importContainerID, EndpointDescription endpointDescription, IRemoteServiceReference rsReference, IRemoteService remoteService) {
        Map resultProperties = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        PropertiesUtil.copyNonReservedProperties(rsReference, resultProperties);
        PropertiesUtil.copyNonReservedProperties(endpointDescription.getProperties(), resultProperties);
        // remove OBJECTCLASS
        resultProperties.remove(org.eclipse.ecf.remoteservice.Constants.OBJECTCLASS);
        // remove remote service id
        resultProperties.remove(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID);
        // Set intents if there are intents
        Object intentsValue = PropertiesUtil.convertToStringPlusValue(endpointDescription.getIntents());
        if (intentsValue != null)
            resultProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS, intentsValue);
        // Set service.imported to IRemoteService unless
        // SERVICE_IMPORTED_VALUETYPE is
        // set
        String serviceImportedType = (String) endpointDescription.getProperties().get(org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_VALUETYPE);
        if (serviceImportedType == null || serviceImportedType.equals(IRemoteService.class.getName()))
            resultProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, remoteService);
        else
            resultProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, new Boolean(true));
        String[] exporterSupportedConfigs = PropertiesUtil.getStringArrayFromPropertyValue(endpointDescription.getProperties().get(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED));
        String[] importedConfigs = getImportedConfigs(importContainerID, exporterSupportedConfigs);
        // Set service.imported.configs
        resultProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, importedConfigs);
        // Set endpoint.id 
        String endpointId = endpointDescription.getId();
        resultProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointId);
        return resultProperties;
    }

    private ExportRegistration exportService(final ServiceReference serviceReference, Map<String, ?> overridingProperties, String[] exportedInterfaces, IRemoteServiceContainer rsContainer, Map<String, Object> endpointDescriptionProperties) throws Exception {
        // Create remote service properties
        Map remoteServiceProperties = copyNonReservedProperties(serviceReference, (Map<String, Object>) overridingProperties, new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER));
        IRemoteServiceContainerAdapter containerAdapter = rsContainer.getContainerAdapter();
        // create serializable dictionary from remote service properties
        Dictionary rsp = PropertiesUtil.createSerializableDictionaryFromMap(remoteServiceProperties);
        // Register remote service via ECF container adapter to create
        // remote service registration
        IRemoteServiceRegistration remoteRegistration = null;
        if (containerAdapter instanceof IOSGiRemoteServiceContainerAdapter) {
            IOSGiRemoteServiceContainerAdapter osgiContainerAdapter = (IOSGiRemoteServiceContainerAdapter) containerAdapter;
            remoteRegistration = osgiContainerAdapter.registerRemoteService(exportedInterfaces, serviceReference, rsp);
        } else {
            Object service = AccessController.doPrivileged(new PrivilegedAction<Object>() {

                public Object run() {
                    return getClientBundleContext().getService(serviceReference);
                }
            });
            remoteRegistration = containerAdapter.registerRemoteService(exportedInterfaces, service, rsp);
        }
        endpointDescriptionProperties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, remoteRegistration.getID().getContainerRelativeID());
        if (remoteRegistration instanceof IExtendedRemoteServiceRegistration) {
            IExtendedRemoteServiceRegistration iersr = (IExtendedRemoteServiceRegistration) remoteRegistration;
            Map<String, Object> extraProperties = iersr.getExtraProperties();
            if (extraProperties != null)
                endpointDescriptionProperties = PropertiesUtil.mergeProperties(endpointDescriptionProperties, extraProperties);
        }
        // Copy only serializable properties
        endpointDescriptionProperties = PropertiesUtil.copySerializableProperties(endpointDescriptionProperties, new TreeMap<String, Object>());
        // Create ExportEndpoint/ExportRegistration
        return new ExportRegistration(new ExportEndpoint(serviceReference, new EndpointDescription(endpointDescriptionProperties), remoteRegistration, endpointDescriptionProperties));
    }

    private ImportRegistration importService(final EndpointDescription endpointDescription, IRemoteServiceContainer rsContainer) {
        // Get interfaces from endpoint description
        Collection<String> interfaces = endpointDescription.getInterfaces();
        Assert.isNotNull(interfaces);
        Assert.isTrue(interfaces.size() > 0);
        // Get ECF endpoint ID...if this throws IDCreateException (because the
        // local system does not have
        // namespace for creating ID, or no namespace is present in
        // endpointDescription or endpoint id,
        // then it will be caught by the caller
        ID endpointContainerID = endpointDescription.getContainerID();
        Assert.isNotNull(endpointContainerID);
        // Get connect target ID. May be null
        ID tID = endpointDescription.getConnectTargetID();
        if (tID == null)
            tID = endpointContainerID;
        final ID targetID = tID;
        // Get idFilter...also may be null
        final ID[] idFilter = getIDFilter(endpointDescription, targetID);
        // Get remote service filter
        final String rsFilter = getRemoteServiceFilter(endpointDescription);
        // IRemoteServiceReferences from query
        Collection<IRemoteServiceReference> rsRefs = new ArrayList<IRemoteServiceReference>();
        // Get IRemoteServiceContainerAdapter
        final IRemoteServiceContainerAdapter containerAdapter = rsContainer.getContainerAdapter();
        // rsContainerID
        ID rsContainerID = rsContainer.getContainer().getID();
        try {
            final IRSAConsumerContainerAdapter cca = (IRSAConsumerContainerAdapter) containerAdapter.getAdapter(IRSAConsumerContainerAdapter.class);
            // Get first interface name for service reference
            // lookup
            final String intf = interfaces.iterator().next();
            // Get/lookup remote service references
            IRemoteServiceReference[] refs = AccessController.doPrivileged(new PrivilegedExceptionAction<IRemoteServiceReference[]>() {

                public IRemoteServiceReference[] run() throws ContainerConnectException, InvalidSyntaxException {
                    // If the RSAConsumerContainerAdapter is present, use it
                    if (cca != null)
                        // Call importEndpoint if the IRSAConsumerContainerAdapter is present
                        return cca.importEndpoint(endpointDescription.getProperties());
                    else
                        // Otherwise use the 'old' container adapter
                        return containerAdapter.getRemoteServiceReferences(targetID, idFilter, intf, rsFilter);
                }
            });
            if (refs == null) {
                logWarning("doImportService", "getRemoteServiceReferences return null for targetID=" + //$NON-NLS-1$
                targetID + //$NON-NLS-1$
                ",idFilter=" + idFilter + ",intf=" + intf + //$NON-NLS-1$ //$NON-NLS-2$
                ",rsFilter=" + //$NON-NLS-1$ //$NON-NLS-2$
                rsFilter + //$NON-NLS-1$
                " on rsContainerID=" + //$NON-NLS-1$
                rsContainerID);
            } else
                for (int i = 0; i < refs.length; i++) rsRefs.add(refs[i]);
            // If there are several refs resulting (should not be)
            // we select the one to use
            IRemoteServiceReference selectedRsReference = selectRemoteServiceReference(rsRefs, targetID, idFilter, interfaces, rsFilter, rsContainer);
            // If none found, we obviously can't continue
            if (selectedRsReference == null)
                throw new RemoteReferenceNotFoundException(targetID, idFilter, interfaces, rsFilter);
            return new ImportRegistration(createAndRegisterProxy(endpointDescription, rsContainer, selectedRsReference));
        } catch (PrivilegedActionException e) {
            logError("importService", "selectRemoteServiceReference returned null for rsRefs=" + rsRefs + ",targetID=" + targetID + ",idFilter=" + idFilter + ",interfaces=" + interfaces + ",rsFilter=" + rsFilter + ",rsContainerID=" + rsContainerID, e.getException());
            return new ImportRegistration(endpointDescription, e.getException());
        } catch (Exception e) {
            logError("importService", "selectRemoteServiceReference returned null for rsRefs=" + rsRefs + ",targetID=" + targetID + ",idFilter=" + idFilter + ",interfaces=" + interfaces + ",rsFilter=" + rsFilter + ",rsContainerID=" + rsContainerID, e);
            return new ImportRegistration(endpointDescription, e);
        }
    }

    public void close() {
        trace("close", //$NON-NLS-1$ //$NON-NLS-2$
        "closing importedRegistrations=" + importedRegistrations + " exportedRegistrations=" + //$NON-NLS-1$
        exportedRegistrations);
        // close any imported and exported registrations
        List<org.osgi.service.remoteserviceadmin.ImportRegistration> toClose = null;
        synchronized (importedRegistrations) {
            toClose = new ArrayList<org.osgi.service.remoteserviceadmin.ImportRegistration>(localImportedRegistrations);
        }
        for (org.osgi.service.remoteserviceadmin.ImportRegistration reg : toClose) reg.close();
        List<org.osgi.service.remoteserviceadmin.ExportRegistration> toClose1 = null;
        synchronized (localExportedRegistrations) {
            toClose1 = new ArrayList<org.osgi.service.remoteserviceadmin.ExportRegistration>(localExportedRegistrations);
        }
        for (org.osgi.service.remoteserviceadmin.ExportRegistration reg1 : toClose1) reg1.close();
        this.localExportedRegistrations.clear();
        this.localImportedRegistrations.clear();
        synchronized (remoteServiceAdminListenerTrackerLock) {
            if (remoteServiceAdminListenerTracker != null) {
                remoteServiceAdminListenerTracker.close();
                remoteServiceAdminListenerTracker = null;
            }
        }
        synchronized (eventAdminTrackerLock) {
            if (eventAdminTracker != null) {
                eventAdminTracker.close();
                eventAdminTracker = null;
            }
        }
        synchronized (proxyClassLoaders) {
            proxyClassLoaders.clear();
        }
        synchronized (consumerContainerSelectorTrackerLock) {
            if (consumerContainerSelectorTracker != null) {
                consumerContainerSelectorTracker.close();
                consumerContainerSelectorTracker = null;
            }
        }
        if (defaultConsumerContainerSelector != null) {
            defaultConsumerContainerSelector.close();
            defaultConsumerContainerSelector = null;
        }
        synchronized (hostContainerSelectorTrackerLock) {
            if (hostContainerSelectorTracker != null) {
                hostContainerSelectorTracker.close();
                hostContainerSelectorTracker = null;
            }
        }
        if (defaultHostContainerSelector != null) {
            defaultHostContainerSelector.close();
            defaultHostContainerSelector = null;
        }
        if (defaultHostContainerSelectorRegistration != null) {
            defaultHostContainerSelectorRegistration.unregister();
            defaultHostContainerSelectorRegistration = null;
        }
        if (defaultHostContainerSelector != null) {
            defaultHostContainerSelector.close();
            defaultHostContainerSelector = null;
        }
        if (defaultConsumerContainerSelectorRegistration != null) {
            defaultConsumerContainerSelectorRegistration.unregister();
            defaultConsumerContainerSelectorRegistration = null;
        }
        if (defaultConsumerContainerSelector != null) {
            defaultConsumerContainerSelector.close();
            defaultConsumerContainerSelector = null;
        }
        if (eventListenerHookRegistration != null) {
            eventListenerHookRegistration.unregister();
            eventListenerHookRegistration = null;
        }
    }

    private ImportEndpoint findImportEndpoint(EndpointDescription ed) {
        for (org.osgi.service.remoteserviceadmin.ImportRegistration reg : importedRegistrations) {
            if (reg instanceof ImportRegistration) {
                ImportEndpoint endpoint = ((ImportRegistration) reg).getImportEndpoint(ed);
                if (endpoint != null)
                    return endpoint;
            }
        }
        return null;
    }

    private void unimportService(IRemoteServiceID remoteServiceID) {
        List<ImportRegistration> removedRegistrations = new ArrayList<ImportRegistration>();
        synchronized (importedRegistrations) {
            for (Iterator<org.osgi.service.remoteserviceadmin.ImportRegistration> i = importedRegistrations.iterator(); i.hasNext(); ) {
                org.osgi.service.remoteserviceadmin.ImportRegistration iReg = i.next();
                if (iReg instanceof ImportRegistration) {
                    ImportRegistration importRegistration = (ImportRegistration) iReg;
                    if (importRegistration.match(remoteServiceID))
                        removedRegistrations.add(importRegistration);
                }
            }
        }
        // Now close all of them
        for (ImportRegistration removedReg : removedRegistrations) {
            //$NON-NLS-1$ //$NON-NLS-2$
            trace("unimportService", "closing importRegistration=" + removedReg);
            removedReg.close();
        }
    }

    class RemoteServiceListener implements IRemoteServiceListener {

        public void handleServiceEvent(IRemoteServiceEvent event) {
            if (event instanceof IRemoteServiceUnregisteredEvent)
                unimportService(event.getReference().getID());
        }
    }

    private void trace(String methodName, String message) {
        LogUtility.trace(methodName, DebugOptions.REMOTE_SERVICE_ADMIN, this.getClass(), message);
    }

    private void logWarning(String methodName, String message) {
        LogUtility.logWarning(methodName, DebugOptions.REMOTE_SERVICE_ADMIN, this.getClass(), message);
    }

    private void logError(String methodName, String message, Throwable t) {
        LogUtility.logError(methodName, DebugOptions.REMOTE_SERVICE_ADMIN, this.getClass(), message, t);
    }

    private void logError(String methodName, String message) {
        logError(methodName, message, (Throwable) null);
    }

    public class RemoteServiceAdminEvent extends org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent {

        private ID containerID;

        private EndpointDescription endpointDescription;

        public  RemoteServiceAdminEvent(ID containerID, int type, Bundle source, org.osgi.service.remoteserviceadmin.ExportReference exportReference, Throwable exception, EndpointDescription endpointDescription) {
            super(type, source, exportReference, exception);
            this.containerID = containerID;
            this.endpointDescription = endpointDescription;
        }

        public  RemoteServiceAdminEvent(ID containerID, int type, Bundle source, org.osgi.service.remoteserviceadmin.ImportReference importReference, Throwable exception, EndpointDescription endpointDescription) {
            super(type, source, importReference, exception);
            this.containerID = containerID;
            this.endpointDescription = endpointDescription;
        }

        /**
		 * @return EndpointDescription for this remote service admin event
		 * @since 3.0
		 */
        public EndpointDescription getEndpointDescription() {
            return endpointDescription;
        }

        public ID getContainerID() {
            return containerID;
        }

        public String toString() {
            return //$NON-NLS-1$
            "RemoteServiceAdminEvent[containerID=" + containerID + ", getType()=" + getType() + ", getSource()=" + //$NON-NLS-1$ //$NON-NLS-2$
            getSource() + //$NON-NLS-1$
            ", getException()=" + //$NON-NLS-1$
            getException() + ", getImportReference()=" + getImportReference() + ", getExportReference()=" + getExportReference() + //$NON-NLS-1$ //$NON-NLS-2$
            "]";
        }
    }
}
