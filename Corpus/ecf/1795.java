/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.distribution;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.AbstractTopologyManager;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointEvent;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.EndpointListener;

public class OSGiTopologyManagerImpl extends AbstractTopologyManager implements EndpointListener, EndpointEventListener {

    private static final boolean exportRegisteredSvcs = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.osgitopologymanager.exportRegisteredSvcs", //$NON-NLS-1$ //$NON-NLS-2$
    "true")).booleanValue();

    private static final String exportRegisteredSvcsFilter = System.getProperty("org.eclipse.ecf.osgi.services.osgitopologymanager.exportRegisteredSvcsFilter", //$NON-NLS-1$ //$NON-NLS-2$
    "(service.exported.interfaces=*)");

    private static final String exportRegisteredSvcsClassname = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.osgitopologymanager.exportRegisteredSvcsClassname");

    private String ecfLocalEndpointListenerScope;

    private String ecfNonLocalEndpointListenerScope;

    private String osgiLocalEndpointListenerScope;

    private String osgiNonLocalEndpointListenerScope;

    //$NON-NLS-1$ //$NON-NLS-2$
    private static final String ONLY_ECF_SCOPE = "(" + RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE + "=*)";

    private static final String NO_ECF_SCOPE = //$NON-NLS-1$
    "(!(" + RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE + //$NON-NLS-1$
    "=*))";

     OSGiTopologyManagerImpl(BundleContext context) {
        super(context);
        String frameworkUUID = getFrameworkUUID();
        //$NON-NLS-1$
        StringBuffer ecfLocalScope = new StringBuffer("");
        ecfLocalScope.append("(&(").append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID).append("=").append(frameworkUUID).append(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ")");
        ecfLocalScope.append(ONLY_ECF_SCOPE);
        //$NON-NLS-1$
        ecfLocalScope.append(")");
        ecfLocalEndpointListenerScope = ecfLocalScope.toString();
        //$NON-NLS-1$
        StringBuffer ecfNonLocalScope = new StringBuffer("");
        ecfNonLocalScope.append("(&(!(").append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID).append("=").append(frameworkUUID).append(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "))");
        ecfNonLocalScope.append(ONLY_ECF_SCOPE);
        //$NON-NLS-1$
        ecfNonLocalScope.append(")");
        ecfNonLocalEndpointListenerScope = ecfNonLocalScope.toString();
        //$NON-NLS-1$
        StringBuffer localScope = new StringBuffer("");
        localScope.append("(&(").append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID).append("=").append(frameworkUUID).append(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ")");
        localScope.append(NO_ECF_SCOPE);
        //$NON-NLS-1$
        localScope.append(")");
        osgiLocalEndpointListenerScope = localScope.toString();
        //$NON-NLS-1$
        StringBuffer nonlocalScope = new StringBuffer("");
        nonlocalScope.append("(&(!(").append(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID).append("=").append(frameworkUUID).append(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "))");
        nonlocalScope.append(NO_ECF_SCOPE);
        //$NON-NLS-1$
        nonlocalScope.append(")");
        osgiNonLocalEndpointListenerScope = nonlocalScope.toString();
    }

    String[] getScope() {
        return new String[] { ecfLocalEndpointListenerScope, ecfNonLocalEndpointListenerScope, osgiLocalEndpointListenerScope, osgiNonLocalEndpointListenerScope };
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

    // EndpointListener impl
    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.remoteserviceadmin.EndpointListener#endpointAdded(org
	 * .osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
	 * 
	 * 
	 * From the R5 spec page 329 section 122.6.2:
	 * 
	 * Notify the Endpoint Listener of a new Endpoint Description. The second
	 * parameter is the filter that matched the Endpoint Description.
	 * Registering the same Endpoint multiple times counts as a single
	 * registration.
	 */
    public void endpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        handleEndpointAdded(endpoint, matchedFilter);
    }

    protected void handleEndpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(osgiLocalEndpointListenerScope)) {
            advertiseEndpointDescription(endpoint);
        } else if (matchedFilter.equals(osgiNonLocalEndpointListenerScope)) {
            EndpointDescription ed = convertEndpointDescriptionFromOSGiToECF(endpoint);
            if (ed != null) {
                handleECFEndpointAdded(ed);
            }
        } else if (matchedFilter.equals(ecfNonLocalEndpointListenerScope)) {
            handleECFEndpointAdded((EndpointDescription) endpoint);
        } else if (matchedFilter.equals(ecfLocalEndpointListenerScope)) {
            advertiseEndpointDescription(endpoint);
        }
    }

    private EndpointDescription convertEndpointDescriptionFromOSGiToECF(org.osgi.service.remoteserviceadmin.EndpointDescription ed) {
        Map<String, Object> newProps = new HashMap<String, Object>();
        newProps.putAll(ed.getProperties());
        String ecfNS = (String) newProps.remove(RemoteConstants.OSGI_CONTAINER_ID_NS);
        if (ecfNS == null)
            return null;
        newProps.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, ecfNS);
        return new EndpointDescription(newProps);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.remoteserviceadmin.EndpointListener#endpointRemoved(
	 * org.osgi.service.remoteserviceadmin.EndpointDescription,
	 * java.lang.String)
	 */
    public void endpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        handleEndpointRemoved(endpoint, matchedFilter);
    }

    protected void handleEndpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(osgiLocalEndpointListenerScope)) {
            unadvertiseEndpointDescription(endpoint);
        } else if (matchedFilter.equals(osgiNonLocalEndpointListenerScope)) {
            EndpointDescription ed = convertEndpointDescriptionFromOSGiToECF(endpoint);
            if (ed != null)
                handleECFEndpointRemoved(ed);
        } else if (matchedFilter.equals(ecfNonLocalEndpointListenerScope)) {
            handleECFEndpointRemoved((EndpointDescription) endpoint);
        } else if (matchedFilter.equals(ecfLocalEndpointListenerScope)) {
            unadvertiseEndpointDescription(endpoint);
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

    private static Long getOSGiEndpointModifiedValue(Map<String, Object> properties) {
        Object modifiedValue = properties.get(RemoteConstants.OSGI_ENDPOINT_MODIFIED);
        if (modifiedValue != null && modifiedValue instanceof String)
            return Long.valueOf((String) modifiedValue);
        return null;
    }

    protected void handleEndpointModified(org.osgi.service.remoteserviceadmin.EndpointDescription endpoint, String matchedFilter) {
        if (matchedFilter.equals(osgiLocalEndpointListenerScope)) {
            Map<String, Object> edProperties = endpoint.getProperties();
            Long modified = getOSGiEndpointModifiedValue(edProperties);
            Map<String, Object> newEdProperties = new HashMap<String, Object>();
            newEdProperties.putAll(endpoint.getProperties());
            if (modified != null) {
                newEdProperties.remove(RemoteConstants.OSGI_ENDPOINT_MODIFIED);
                handleNonECFEndpointModified(this, new org.osgi.service.remoteserviceadmin.EndpointDescription(newEdProperties));
            } else {
                newEdProperties.put(RemoteConstants.OSGI_ENDPOINT_MODIFIED, String.valueOf(System.currentTimeMillis()));
                advertiseModifyEndpointDescription(new org.osgi.service.remoteserviceadmin.EndpointDescription(newEdProperties));
            }
        } else if (matchedFilter.equals(osgiNonLocalEndpointListenerScope)) {
            handleNonECFEndpointModified(this, endpoint);
        }
    }

    public void activate() {
        if (exportRegisteredSvcs)
            exportRegisteredServices(exportRegisteredSvcsClassname, exportRegisteredSvcsFilter);
    }
}
