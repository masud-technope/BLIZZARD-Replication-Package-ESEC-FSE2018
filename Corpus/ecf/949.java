/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.distribution;

import java.util.Map;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.AbstractTopologyManager;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointEvent;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;

public class BasicTopologyManagerImpl extends AbstractTopologyManager implements EndpointEventListener {

    private static final boolean allowLoopbackReference = new Boolean(//$NON-NLS-1$
    System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.discovery.allowLoopbackReference", //$NON-NLS-1$
    "false")).booleanValue();

    private static final String defaultScope = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.discovery.endpointListenerScope");

    private String ecfEndpointListenerScope;

    //$NON-NLS-1$ //$NON-NLS-2$
    private static final String ONLY_ECF_SCOPE = "(" + RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE + "=*)";

    private static final String NO_ECF_SCOPE = //$NON-NLS-1$
    "(!(" + RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE + //$NON-NLS-1$
    "=*))";

     BasicTopologyManagerImpl(BundleContext context) {
        super(context);
        if (defaultScope != null)
            this.ecfEndpointListenerScope = defaultScope;
        // consider those that have a namespace (only ECF endpoint descriptions)
        if (allowLoopbackReference)
            ecfEndpointListenerScope = ONLY_ECF_SCOPE;
        else {
            // If loopback not allowed, then we have our scope include
            // both !frameworkUUID same, and ONLY_ECF_SCOPE
            //$NON-NLS-1$
            StringBuffer elScope = new StringBuffer("");
            // filter so that local framework uuid is not the same as local
            // value
            //$NON-NLS-1$
            elScope.append("(&(!(").append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID).append("=").append(getFrameworkUUID()).append(//$NON-NLS-1$ //$NON-NLS-2$
            "))");
            elScope.append(ONLY_ECF_SCOPE);
            //$NON-NLS-1$
            elScope.append(")");
            ecfEndpointListenerScope = elScope.toString();
        }
    }

    String[] getScope() {
        return new String[] { ecfEndpointListenerScope, NO_ECF_SCOPE };
    }

    protected String getFrameworkUUID() {
        return super.getFrameworkUUID();
    }

    void exportRegisteredServices(String exportRegisteredSvcsClassname, String exportRegisteredSvcsFilter) {
        try {
            final ServiceReference[] existingServiceRefs = getContext().getAllServiceReferences(exportRegisteredSvcsClassname, exportRegisteredSvcsFilter);
            // export
            if (existingServiceRefs != null && existingServiceRefs.length > 0) {
                // After having collected all pre-registered services (with
                // marker prop) we are going to asynchronously remote them.
                // Registering potentially is a long-running operation (due to
                // discovery I/O...) and thus should no be carried out in the
                // OSGi FW thread. (https://bugs.eclipse.org/405027)
                new Thread(new Runnable() {

                    public void run() {
                        for (int i = 0; i < existingServiceRefs.length; i++) {
                            // This method will check the service properties for
                            // remote service props. If previously registered as
                            // a
                            // remote service, it will export the remote
                            // service if not it will simply return/skip
                            handleServiceRegistering(existingServiceRefs[i]);
                        }
                    }
                }, //$NON-NLS-1$
                "BasicTopologyManagerPreRegSrvExporter").start();
            }
        } catch (InvalidSyntaxException e) {
            logError("exportRegisteredServices", "Could not retrieve existing service references for exportRegisteredSvcsClassname=" + exportRegisteredSvcsClassname + " and exportRegisteredSvcsFilter=" + exportRegisteredSvcsFilter, e);
        }
    }

    protected void handleEndpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(ecfEndpointListenerScope) && (endpoint instanceof EndpointDescription)) {
            handleECFEndpointAdded((EndpointDescription) endpoint);
        }
    }

    protected void handleEndpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(ecfEndpointListenerScope) && (endpoint instanceof EndpointDescription)) {
            handleECFEndpointRemoved((EndpointDescription) endpoint);
        }
    }

    // EventListenerHook impl
    void event(ServiceEvent event, Map listeners) {
        handleEvent(event, listeners);
    }

    // RemoteServiceAdminListener impl
    void handleRemoteAdminEvent(RemoteServiceAdminEvent event) {
        if (!(event instanceof RemoteServiceAdmin.RemoteServiceAdminEvent))
            return;
        RemoteServiceAdmin.RemoteServiceAdminEvent rsaEvent = (RemoteServiceAdmin.RemoteServiceAdminEvent) event;
        int eventType = event.getType();
        EndpointDescription endpointDescription = rsaEvent.getEndpointDescription();
        switch(eventType) {
            case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
                advertiseEndpointDescription(endpointDescription);
                break;
            case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
                unadvertiseEndpointDescription(endpointDescription);
                break;
            case RemoteServiceAdminEvent.EXPORT_ERROR:
                //$NON-NLS-1$ //$NON-NLS-2$
                logError("handleRemoteAdminEvent.EXPORT_ERROR", "Export error with event=" + rsaEvent);
                break;
            case RemoteServiceAdminEvent.EXPORT_WARNING:
                //$NON-NLS-1$ //$NON-NLS-2$
                logWarning("handleRemoteAdminEvent.EXPORT_WARNING", "Export warning with event=" + rsaEvent);
                break;
            case RemoteServiceAdminEvent.EXPORT_UPDATE:
                advertiseModifyEndpointDescription(endpointDescription);
                break;
            case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
                break;
            case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
                break;
            case RemoteServiceAdminEvent.IMPORT_ERROR:
                //$NON-NLS-1$//$NON-NLS-2$
                logError("handleRemoteAdminEvent.IMPORT_ERROR", "Import error with event=" + rsaEvent);
                break;
            case RemoteServiceAdminEvent.IMPORT_WARNING:
                //$NON-NLS-1$ //$NON-NLS-2$
                logWarning("handleRemoteAdminEvent.IMPORT_WARNING", "Import warning with event=" + rsaEvent);
                break;
            default:
                logWarning(//$NON-NLS-1$
                "handleRemoteAdminEvent", //$NON-NLS-1$ //$NON-NLS-2$
                "RemoteServiceAdminEvent=" + rsaEvent + " received with unrecognized type");
        }
    }

    /**
	 * Implementation of
	 * org.osgi.service.remoteserviceadmin.EndpointEventListener for rfc 203/RSA
	 * 1.1
	 * 
	 * @see EndpointEventListener#endpointChanged(EndpointEvent, String)
	 */
    public void endpointChanged(EndpointEvent event, String matchedFilter) {
        int eventType = event.getType();
        org.osgi.service.remoteserviceadmin.EndpointDescription ed = event.getEndpoint();
        switch(eventType) {
            case EndpointEvent.ADDED:
                handleEndpointAdded(ed, matchedFilter);
                break;
            case EndpointEvent.REMOVED:
                handleEndpointRemoved(ed, matchedFilter);
                break;
            case EndpointEvent.MODIFIED:
                handleEndpointModified(ed, matchedFilter);
                break;
            case EndpointEvent.MODIFIED_ENDMATCH:
                handleEndpointModifiedEndmatch(ed, matchedFilter);
                break;
        }
    }

    protected void handleEndpointModifiedEndmatch(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
    // By default do nothing for end match. subclasses may decide
    // to change this behavior
    }

    protected void handleEndpointModified(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(ecfEndpointListenerScope) && (endpoint instanceof EndpointDescription)) {
            handleECFEndpointModified((EndpointDescription) endpoint);
        }
    }
}
