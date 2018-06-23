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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.Activator;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.DebugOptions;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.LogUtility;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Abstract superclass for topology managers. This abstract superclass provides
 * basic functionality for topology managers to reuse. New topology managers can
 * extend this class to get or customize desired functionality. Alternatively,
 * they can use this class as a guide to implementing desired topology manager
 * behavior. For description of the role of topology managers see the <a
 * href="http://www.osgi.org/download/r4v42/r4.enterprise.pdf">OSGI 4.2 Remote
 * Service Admin specification (chap 122)</a>.
 * 
 */
public abstract class AbstractTopologyManager {

    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_INTERFACES_WILDCARD = "*";

    private BundleContext context;

    private ServiceTracker<IServiceInfoFactory, IServiceInfoFactory> serviceInfoFactoryTracker;

    private ServiceTracker remoteServiceAdminTracker;

    private Object remoteServiceAdminTrackerLock = new Object();

    private final Map<org.osgi.service.remoteserviceadmin.EndpointDescription, List<ServiceRegistration<IServiceInfo>>> registrations = new HashMap<org.osgi.service.remoteserviceadmin.EndpointDescription, List<ServiceRegistration<IServiceInfo>>>();

    private final ReentrantLock registrationLock;

    private boolean requireServiceExportedConfigs = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.AbstractTopologyManager.requireServiceExportedConfigs", //$NON-NLS-1$
    "false")).booleanValue();

    public  AbstractTopologyManager(BundleContext context) {
        serviceInfoFactoryTracker = new ServiceTracker(context, createISIFFilter(context), null);
        serviceInfoFactoryTracker.open();
        this.context = context;
        // Use a FAIR lock here to guarantee that an endpoint removed operation
        // for EP x never executes before its corresponding endpoint added op
        // for EP x.
        // This might happen for an unfair lock (e.g. synchronized) because it
        // doesn't maintain ordering of the waiting threads.
        this.registrationLock = new ReentrantLock(true);
    }

    protected BundleContext getContext() {
        return context;
    }

    protected String getFrameworkUUID() {
        Activator a = Activator.getDefault();
        if (a == null)
            return null;
        return a.getFrameworkUUID();
    }

    public void close() {
        registrationLock.lock();
        try {
            for (org.osgi.service.remoteserviceadmin.EndpointDescription ed : registrations.keySet()) unadvertiseEndpointDescription(ed);
            registrations.clear();
        } finally {
            registrationLock.unlock();
        }
        synchronized (remoteServiceAdminTrackerLock) {
            if (remoteServiceAdminTracker != null) {
                remoteServiceAdminTracker.close();
                remoteServiceAdminTracker = null;
            }
        }
        context = null;
    }

    protected void logWarning(String methodName, String message) {
        LogUtility.logWarning(methodName, DebugOptions.TOPOLOGY_MANAGER, this.getClass(), message);
    }

    protected Filter createRSAFilter() {
        String //$NON-NLS-1$
        filterString = "(&(" + org.osgi.framework.Constants.OBJECTCLASS + //$NON-NLS-1$
        "=" + org.osgi.service.remoteserviceadmin.RemoteServiceAdmin.class.getName() + //$NON-NLS-1$
        ")(" + org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.SERVICE_PROP + //$NON-NLS-1$
        "=*))";
        try {
            return getContext().createFilter(filterString);
        } catch (InvalidSyntaxException doesNotHappen) {
            doesNotHappen.printStackTrace();
            return null;
        }
    }

    /**
	 * @param ctx the bundle context
	 * @return Filter the created filter
	 * @since 4.0
	 */
    protected Filter createISIFFilter(BundleContext ctx) {
        String //$NON-NLS-1$
        filterString = "(" + org.osgi.framework.Constants.OBJECTCLASS + //$NON-NLS-1$
        "=" + IServiceInfoFactory.class.getName() + //$NON-NLS-1$
        ")";
        try {
            return ctx.createFilter(filterString);
        } catch (InvalidSyntaxException doesNotHappen) {
            doesNotHappen.printStackTrace();
            return null;
        }
    }

    protected org.osgi.service.remoteserviceadmin.RemoteServiceAdmin getRemoteServiceAdmin() {
        synchronized (remoteServiceAdminTrackerLock) {
            if (remoteServiceAdminTracker == null) {
                remoteServiceAdminTracker = new ServiceTracker(Activator.getContext(), createRSAFilter(), null);
                remoteServiceAdminTracker.open();
            }
        }
        return (org.osgi.service.remoteserviceadmin.RemoteServiceAdmin) remoteServiceAdminTracker.getService();
    }

    private void addRegistration(org.osgi.service.remoteserviceadmin.EndpointDescription ed, ServiceRegistration<IServiceInfo> reg) {
        List<ServiceRegistration<IServiceInfo>> regs = this.registrations.get(ed);
        if (regs == null)
            regs = new ArrayList<ServiceRegistration<IServiceInfo>>();
        regs.add(reg);
        this.registrations.put(ed, regs);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @since 4.1
	 */
    protected void advertiseModifyEndpointDescription(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        this.registrationLock.lock();
        try {
            final IServiceInfoFactory service = serviceInfoFactoryTracker.getService();
            if (service != null) {
                final IServiceInfo serviceInfo = service.createServiceInfo(null, endpointDescription);
                if (serviceInfo != null) {
                    trace("advertiseModifyEndpointDescription", //$NON-NLS-1$
                    "advertising modify endpointDescription=" + //$NON-NLS-1$
                    endpointDescription + //$NON-NLS-1$
                    " and IServiceInfo " + //$NON-NLS-1$
                    serviceInfo);
                    final ServiceRegistration<IServiceInfo> registerService = this.context.registerService(IServiceInfo.class, serviceInfo, null);
                    addRegistration(endpointDescription, registerService);
                } else {
                    logError("advertiseModifyEndpointDescription", //$NON-NLS-1$1
                    "IServiceInfoFactory failed to convert EndpointDescription " + //$NON-NLS-1$1
                    endpointDescription);
                }
            } else {
                logError("advertiseModifyEndpointDescription", "no IServiceInfoFactory service found");
            }
        } finally {
            this.registrationLock.unlock();
        }
    }

    /**
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void advertiseEndpointDescription(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        this.registrationLock.lock();
        try {
            if (this.registrations.containsKey(endpointDescription)) {
                return;
            }
            final IServiceInfoFactory service = serviceInfoFactoryTracker.getService();
            if (service != null) {
                final IServiceInfo serviceInfo = service.createServiceInfo(null, endpointDescription);
                if (serviceInfo != null) {
                    trace("advertiseEndpointDescription", //$NON-NLS-1$
                    "advertising endpointDescription=" + //$NON-NLS-1$
                    endpointDescription + //$NON-NLS-1$
                    " and IServiceInfo " + //$NON-NLS-1$
                    serviceInfo);
                    final ServiceRegistration<IServiceInfo> registerService = this.context.registerService(IServiceInfo.class, serviceInfo, null);
                    addRegistration(endpointDescription, registerService);
                } else {
                    logError("advertiseEndpointDescription", //$NON-NLS-1$1
                    "IServiceInfoFactory failed to convert EndpointDescription " + //$NON-NLS-1$1
                    endpointDescription);
                }
            } else {
                logError("advertiseEndpointDescription", "no IServiceInfoFactory service found");
            }
        } finally {
            this.registrationLock.unlock();
        }
    }

    /**
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void unadvertiseEndpointDescription(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        this.registrationLock.lock();
        try {
            final List<ServiceRegistration<IServiceInfo>> serviceRegistrations = this.registrations.remove(endpointDescription);
            if (serviceRegistrations != null) {
                for (ServiceRegistration<IServiceInfo> serviceRegistration : serviceRegistrations) serviceRegistration.unregister();
                return;
            }
        } finally {
            this.registrationLock.unlock();
        }
    }

    protected void logError(String methodName, String message, Throwable exception) {
        LogUtility.logError(methodName, DebugOptions.TOPOLOGY_MANAGER, this.getClass(), message, exception);
    }

    protected void logError(String methodName, String message, IStatus result) {
        LogUtility.logError(methodName, DebugOptions.TOPOLOGY_MANAGER, this.getClass(), result);
    }

    protected void trace(String methodName, String message) {
        LogUtility.trace(methodName, DebugOptions.TOPOLOGY_MANAGER, this.getClass(), message);
    }

    protected void logError(String methodName, String message) {
        LogUtility.logError(methodName, DebugOptions.TOPOLOGY_MANAGER, this.getClass(), message);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void handleECFEndpointAdded(EndpointDescription endpointDescription) {
        trace("handleECFEndpointAdded", //$NON-NLS-1$ //$NON-NLS-2$
        "endpointDescription=" + endpointDescription);
        // Import service
        RemoteServiceAdmin rsa = (RemoteServiceAdmin) getRemoteServiceAdmin();
        if (rsa != null)
            rsa.importService(endpointDescription);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void handleECFEndpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        trace("handleECFEndpointRemoved", //$NON-NLS-1$ //$NON-NLS-2$
        "endpointDescription=" + endpointDescription);
        RemoteServiceAdmin rsa = (RemoteServiceAdmin) getRemoteServiceAdmin();
        if (rsa != null) {
            List<RemoteServiceAdmin.ImportRegistration> importedRegistrations = rsa.getImportedRegistrations();
            org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription ed = (org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) endpointDescription;
            for (RemoteServiceAdmin.ImportRegistration importedRegistration : importedRegistrations) {
                if (importedRegistration.match(ed)) {
                    trace("handleEndpointRemoved", //$NON-NLS-1$ //$NON-NLS-2$
                    "closing importedRegistration=" + importedRegistration);
                    importedRegistration.close();
                }
            }
        }
    }

    /**
	 * @param endpoint endpoint description
	 * @since 4.1
	 */
    protected void handleECFEndpointModified(EndpointDescription endpoint) {
        trace("handleECFEndpointModified", //$NON-NLS-1$ //$NON-NLS-2$
        "endpointDescription=" + endpoint);
        RemoteServiceAdmin rsa = (RemoteServiceAdmin) getRemoteServiceAdmin();
        if (rsa != null) {
            List<RemoteServiceAdmin.ImportRegistration> importedRegistrations = rsa.getImportedRegistrations();
            org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription ed = (org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) endpoint;
            for (RemoteServiceAdmin.ImportRegistration importedRegistration : importedRegistrations) {
                if (importedRegistration.match(ed)) {
                    trace("handleECFEndpointModified", //$NON-NLS-1$ //$NON-NLS-2$
                    "updating importedRegistration=" + importedRegistration);
                    importedRegistration.update(endpoint);
                }
            }
        }
    }

    /**
	 * @param listener listener
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void handleNonECFEndpointAdded(EndpointListener listener, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        //$NON-NLS-1$//$NON-NLS-2$
        trace("handleNonECFEndpointAdded", "ed=" + endpointDescription);
    }

    /**
	 * @param listener listener
	 * @param endpointDescription endpoint description
	 * @since 3.0
	 */
    protected void handleNonECFEndpointRemoved(EndpointListener listener, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        //$NON-NLS-1$//$NON-NLS-2$
        trace("handleNonECFEndpointRemoved", "ed=" + endpointDescription);
    }

    /**
	 * @param basicTopologyManagerImpl basic topology manager
	 * @param endpointDescription endpointDescription
	 * @since 4.1
	 */
    protected void handleNonECFEndpointModified(EndpointEventListener basicTopologyManagerImpl, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("handleNonECFEndpointModified", "ed=" + endpointDescription);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param matchedFilter matched filter
	 * @since 3.0
	 */
    protected void handleNonECFEndpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, String matchedFilter) {
        advertiseEndpointDescription(endpointDescription);
    }

    /**
	 * @param result result
	 * @param endpointDescription endpoint description
	 * @param advertise advertise
	 * @since 3.0
	 */
    protected void handleAdvertisingResult(IStatus result, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, boolean advertise) {
        if (!result.isOK())
            logError("handleAdvertisingResult", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            (advertise ? "Advertise" : "Unadvertise") + " of endpointDescription=" + endpointDescription + //$NON-NLS-1$
            " FAILED", result);
    }

    protected void handleInvalidImportRegistration(ImportRegistration importRegistration, Throwable t) {
        logError("handleInvalidImportRegistration", //$NON-NLS-1$ //$NON-NLS-2$
        "importRegistration=" + importRegistration, t);
    }

    /**
	 * @param event the service event
	 * @param listeners map of listeners
	 * @since 3.0
	 */
    protected void handleEvent(ServiceEvent event, Map listeners) {
        switch(event.getType()) {
            case ServiceEvent.MODIFIED:
                handleServiceModifying(event.getServiceReference());
                break;
            case ServiceEvent.REGISTERED:
                handleServiceRegistering(event.getServiceReference());
                break;
            case ServiceEvent.UNREGISTERING:
                handleServiceUnregistering(event.getServiceReference());
                break;
            default:
                break;
        }
    }

    protected void handleServiceRegistering(ServiceReference serviceReference) {
        // Using OSGI 5 Chap 13 Remote Services spec, get the specified remote
        // interfaces for the given service reference
        String[] exportedInterfaces = PropertiesUtil.getExportedInterfaces(serviceReference);
        // If no remote interfaces set, then we don't do anything with it
        if (exportedInterfaces == null)
            return;
        // Get serviceExportedConfigs property
        String[] serviceExportedConfigs = PropertiesUtil.getStringArrayFromPropertyValue(serviceReference.getProperty(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_CONFIGS));
        // is null/not set, then we don't do anything with this service registration
        if (requireServiceExportedConfigs && (serviceExportedConfigs == null || Arrays.asList(serviceExportedConfigs).size() == 0))
            return;
        // If we get this far, then we are going to export it
        // prepare export properties
        Map<String, Object> exportProperties = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        exportProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_INTERFACES, exportedInterfaces);
        trace("handleServiceRegistering", //$NON-NLS-1$ //$NON-NLS-2$
        "serviceReference=" + serviceReference + " exportProperties=" + //$NON-NLS-1$
        exportProperties);
        org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa = getRemoteServiceAdmin();
        // Do the export with RSA
        if (rsa != null)
            rsa.exportService(serviceReference, exportProperties);
    }

    protected void handleServiceModifying(ServiceReference serviceReference) {
        RemoteServiceAdmin rsa = (RemoteServiceAdmin) getRemoteServiceAdmin();
        if (rsa != null) {
            List<RemoteServiceAdmin.ExportRegistration> exportedRegistrations = rsa.getExportedRegistrations();
            for (RemoteServiceAdmin.ExportRegistration exportedRegistration : exportedRegistrations) {
                if (exportedRegistration.match(serviceReference)) {
                    trace("handleServiceModifying", //$NON-NLS-1$ //$NON-NLS-2$
                    "modifying exportRegistration for serviceReference=" + serviceReference);
                    EndpointDescription updatedED = (EndpointDescription) exportedRegistration.update(null);
                    if (updatedED == null)
                        logWarning("handleServiceModifying", //$NON-NLS-1$//$NON-NLS-2$
                        "ExportRegistration.update failed with exception=" + exportedRegistration.getException());
                }
            }
        }
    }

    protected void handleServiceUnregistering(ServiceReference serviceReference) {
        RemoteServiceAdmin rsa = (RemoteServiceAdmin) getRemoteServiceAdmin();
        if (rsa != null) {
            List<RemoteServiceAdmin.ExportRegistration> exportedRegistrations = ((RemoteServiceAdmin) getRemoteServiceAdmin()).getExportedRegistrations();
            for (RemoteServiceAdmin.ExportRegistration exportedRegistration : exportedRegistrations) {
                if (exportedRegistration.match(serviceReference)) {
                    trace("handleServiceUnregistering", //$NON-NLS-1$ //$NON-NLS-2$
                    "closing exportRegistration for serviceReference=" + serviceReference);
                    exportedRegistration.close();
                }
            }
        }
    }
}
