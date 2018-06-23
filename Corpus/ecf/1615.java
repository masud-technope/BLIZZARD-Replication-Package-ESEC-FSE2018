/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.osgi.services.discovery;

import org.eclipse.ecf.internal.osgi.services.discovery.*;
import java.io.Serializable;
import java.net.*;
import java.util.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.*;
import org.eclipse.ecf.osgi.services.discovery.ECFServicePublication;
import org.eclipse.ecf.remoteservice.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.*;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * ServicePublicationHandler bi-directionally connects ECF Discovery API with
 * RFC119 ServicePublication.
 */
public class ServicePublicationHandler implements ServiceTrackerCustomizer, Discovery {

    /**
	 * This map acts as a mapping from ServiceReferences to IServiceInfo objects
	 * and is used to retrieve the IServiceInfo associated with a given
	 * ServiceReference on OSGi service unpublish
	 */
    private Map serviceRefToServiceInfo = Collections.synchronizedMap(new HashMap());

    /*---------------- ECF Discovery --> RFC 119 ----------------*/
    /**
	 * ECF Discovery IServiceListener. Receives IServiceEvents fired by ECF
	 * discovery upon service discovery/un-discovery
	 */
    private final IServiceListener serviceListener = new IServiceListener() {

        /* (non-Javadoc)
		 * @see org.eclipse.ecf.discovery.IServiceListener#serviceDiscovered(org.eclipse.ecf.discovery.IServiceEvent)
		 */
        public void serviceDiscovered(IServiceEvent anEvent) {
            IServiceInfo serviceInfo = anEvent.getServiceInfo();
            IServiceID serviceID = serviceInfo.getServiceID();
            //$NON-NLS-1$ //$NON-NLS-2$
            trace("handleOSGIServiceDiscovered", " serviceInfo=" + serviceInfo);
            if (checkValid(serviceID)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("handleOSGIServiceDiscovered matched", " serviceInfo=" + serviceInfo);
                DiscoveredServiceTracker[] discoveredTrackers = findMatchingDiscoveredServiceTrackers(serviceInfo);
                for (int i = 0; i < discoveredTrackers.length; i++) {
                    discoveredTrackers[i].serviceChanged(new DiscoveredServiceNotificationImpl(DiscoveredServiceNotification.AVAILABLE, serviceInfo));
                }
            }
        }

        /* (non-Javadoc)
		 * @see org.eclipse.ecf.discovery.IServiceListener#serviceUndiscovered(org.eclipse.ecf.discovery.IServiceEvent)
		 */
        public void serviceUndiscovered(IServiceEvent anEvent) {
            IServiceInfo serviceInfo = anEvent.getServiceInfo();
            IServiceID serviceID = serviceInfo.getServiceID();
            if (checkValid(serviceID)) {
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("handleOSGIServiceUndiscovered", " serviceInfo=" + serviceInfo);
                DiscoveredServiceTracker[] discoveredTrackers = findMatchingDiscoveredServiceTrackers(serviceInfo);
                for (int i = 0; i < discoveredTrackers.length; i++) {
                    discoveredTrackers[i].serviceChanged(new DiscoveredServiceNotificationImpl(DiscoveredServiceNotification.UNAVAILABLE, serviceInfo));
                }
            }
        }
    };

    /**
	 * Creates a new {@link ServicePublicationHandler} and immediately starts listening for ECF discovery events
	 */
    public  ServicePublicationHandler() {
        //TODO tutorial "implement listener registration with ECF discovery"
        throw new UnsupportedOperationException("ServicePublicationHandler()");
    }

    /*---------------- RFC 119 --> ECF Discovery ----------------*/
    /* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
    public Object addingService(ServiceReference reference) {
        if (checkValid(reference)) {
            publishService(reference);
        }
        return Activator.getDefault().getContext().getService(reference);
    }

    /* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
    public void modifiedService(ServiceReference reference, Object service) {
        if (checkValid(reference)) {
            unpublishService(reference);
            publishService(reference);
        }
    }

    /* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
    public void removedService(ServiceReference reference, Object service) {
        if (checkValid(reference)) {
            unpublishService(reference);
        }
    }

    /*----------- Converter methods RFC 119 --> ECF Discovery -----------*/
    /**
	 * Publishes the given ServiceReference with ECF discovery
	 * @param reference The {@link ServiceReference} to be published
	 */
    private void publishService(ServiceReference reference) {
        //TODO tutorial "implement servicePublicationProperties to IServiceType conversion"
        throw new UnsupportedOperationException("publishService(ServiceReference reference)");
    }

    /**
	 * Unpublishes the given ServiceReference
	 * @param reference
	 */
    private void unpublishService(ServiceReference reference) {
        IServiceInfo svcInfo = null;
        try {
            svcInfo = (IServiceInfo) serviceRefToServiceInfo.remove(reference);
            if (svcInfo != null) {
                Activator.getDefault().getAdvertiser().unregisterService(svcInfo);
            }
        } catch (ECFRuntimeException e) {
            logError("publishService", "Cannot unregister serviceInfo=" + svcInfo, e);
        }
    }

    /**
	 * 
	 * @param serviceReference
	 * @param serviceInfo
	 * @return
	 */
    private boolean matchWithDiscoveredServiceInfo(ServiceReference serviceReference, IServiceInfo serviceInfo) {
        // If short on time, returning true here is fine too
        return true;
    }

    /**
	 * Returns an array of {@link DiscoveredServiceTracker}s responsible for the given {@link IServiceInfo} or an empty array if the is none
	 */
    private DiscoveredServiceTracker[] findMatchingDiscoveredServiceTrackers(IServiceInfo serviceInfo) {
        ServiceReference[] sourceTrackers = Activator.getDefault().getDiscoveredServiceTrackerReferences();
        if (sourceTrackers == null) {
            return new DiscoveredServiceTracker[0];
        }
        List matchingTrackers = new ArrayList();
        BundleContext context = Activator.getDefault().getContext();
        for (int i = 0; i < sourceTrackers.length; i++) {
            if (matchWithDiscoveredServiceInfo(sourceTrackers[i], serviceInfo)) {
                matchingTrackers.add(context.getService(sourceTrackers[i]));
            }
        }
        return (DiscoveredServiceTracker[]) matchingTrackers.toArray(new DiscoveredServiceTracker[] {});
    }

    /**
	 * Checks the given ServiceReference for validity with this SPH. In order for a {@link ServiceReference}
	 * to be valid, certain properties have to be set:
	 * <p>
	 * 	{@link ServicePublication#PROP_KEY_SERVICE_INTERFACE_NAME\
	 *	{@link ServicePublication#PROP_KEY_SERVICE_PROPERTIES}
	 *  {@link ECFServicePublication#PROP_KEY_ENDPOINT_CONTAINERID}
	 *  {@link Constants#SERVICE_NAMESPACE}
	 *  {@link Constants#SERVICE_ID}
	 * @param reference The reference to be checked
	 * @return true if the given reference conforms to our requirements, false otherwise
	 */
    private boolean checkValid(ServiceReference reference) {
        // ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME
        Collection svcInterfaces = ServicePropertyUtils.getCollectionProperty(reference, ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME);
        if (svcInterfaces == null) {
            logError(//$NON-NLS-1$
            "handleServicePublication", //$NON-NLS-1$
            "ignoring " + reference + ". ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME not set", null);
            return false;
        }
        // We also use the optional RFC 119 property PROP_KEY_SERVICE_PROPERTIES
        Map servicePublicationServiceProperties = ServicePropertyUtils.getMapProperty(reference, ServicePublication.PROP_KEY_SERVICE_PROPERTIES);
        if (servicePublicationServiceProperties == null) {
            logError(//$NON-NLS-1$
            "handleServicePublication", //$NON-NLS-1$
            "ignoring " + reference + ". ServicePublication.PROP_KEY_SERVICE_PROPERTIES not set", null);
            return false;
        }
        // ECFServicePublication.PROP_KEY_ENDPOINT_CONTAINERID
        ID endpointContainerID = (ID) reference.getProperty(ECFServicePublication.PROP_KEY_ENDPOINT_CONTAINERID);
        if (endpointContainerID == null) {
            logError(//$NON-NLS-1$
            "handleServicePublication", //$NON-NLS-1$
            "ignoring " + reference + ". ECFServicePublication.PROP_KEY_ENDPOINT_CONTAINERID not set", null);
            return false;
        }
        // Constants.SERVICE_NAMESPACE
        String rsnamespace = ServicePropertyUtils.getStringProperty(reference, Constants.SERVICE_NAMESPACE);
        if (rsnamespace == null) {
            logError("handleServicePublication", //$NON-NLS-1$ //$NON-NLS-2$
            "ignoring " + reference + //$NON-NLS-1$
            ". Constants.SERVICE_NAMESPACE not set", //$NON-NLS-1$
            null);
            return false;
        }
        // Constants.SERVICE_ID
        Long remoteServiceID = (Long) reference.getProperty(Constants.SERVICE_ID);
        if (remoteServiceID == null) {
            logError("handleServicePublication", //$NON-NLS-1$ //$NON-NLS-2$
            "ignoring " + reference + //$NON-NLS-1$
            ". Constants.SERVICE_ID not set", //$NON-NLS-1$
            null);
            return false;
        }
        return true;
    }

    private void addPropertiesToDiscoveryServiceProperties(IServiceProperties discoveryServiceProperties, Map servicePublicationServiceProperties) {
        for (Iterator i = servicePublicationServiceProperties.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            if (!(key instanceof String)) {
                trace("addPropertiesToDiscoveryServiceProperties", "skipping non-string key " + key);
                continue;
            }
            String keyStr = (String) key;
            Object val = servicePublicationServiceProperties.get(keyStr);
            if (val instanceof String) {
                discoveryServiceProperties.setPropertyString(keyStr, (String) val);
            } else if (val instanceof byte[]) {
                discoveryServiceProperties.setPropertyBytes(keyStr, (byte[]) val);
            } else if (val instanceof Serializable) {
                discoveryServiceProperties.setProperty(keyStr, val);
            }
        }
    }

    /*----------- Converter methods ECF Discovery --> RFC 119 -----------*/
    /**
	 * Only accept services of type ECFServicePublication.SERVICE_TYPE
	 */
    private boolean checkValid(IServiceID serviceId) {
        String[] service = serviceId.getServiceTypeID().getServices();
        List asList = Arrays.asList(service);
        if (asList.contains(ECFServicePublication.SERVICE_TYPE)) {
            return true;
        }
        return false;
    }

    /**
	 * Creates an {@link URI} from the given {@link ID}
	 * @param endpointContainerID
	 * @return an {@link URI} 
	 * @throws URISyntaxException
	 */
    private URI createURI(ID endpointContainerID) throws URISyntaxException {
        boolean done = false;
        URI uri = null;
        String str = endpointContainerID.getName();
        while (!done) {
            try {
                uri = new URI(str);
                if (!uri.isOpaque()) {
                    done = true;
                } else {
                    str = uri.getRawSchemeSpecificPart();
                }
            } catch (URISyntaxException e) {
                done = true;
            }
        }
        String scheme = ECFServicePublication.SERVICE_TYPE;
        int port = 32565;
        if (uri != null) {
            port = uri.getPort();
            if (port == -1)
                port = 32565;
        }
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            host = "localhost";
        }
        return new URI(scheme, null, host, port, null, null, null);
    }

    /**
	 * Creates an {@link IServiceTypeID} from the given servicePublicationProperties
	 * @param servicePublicationProperties
	 * @param aNamespace
	 * @return
	 * @throws IDCreateException
	 */
    private IServiceTypeID createServiceTypeID(Map servicePublicationProperties, Namespace aNamespace) throws IDCreateException {
        //TODO tutorial "implement servicePublicationProperties to IServiceType conversion"
        throw new UnsupportedOperationException("createServiceTypeID(Map servicePublicationProperties, Namespace aNamespace)");
    }

    /*----------- Utility methods -----------*/
    /**
	 * Returns the object associated with the given key from the map or default if map does not contains key
	 */
    private String getPropertyWithDefault(Map properties, String key, String def) {
        String val = (String) properties.get(key);
        return (val == null) ? def : val;
    }

    /**
	 * Log an Error with the OSGi LogService. Additionally sends a trace.
	 */
    private void logError(String method, String message, Throwable t) {
        Activator.getDefault().log(method, message, t);
    }

    /**
	 * Trace output
	 */
    private void trace(String methodName, String message) {
        Trace.trace(Activator.PLUGIN_ID, DebugOptions.SVCPUBHANDLERDEBUG, this.getClass(), methodName, message);
    }

    /**
	 * Cleans up after bundle life-cycle stop event. Most importantly it unregisters all registered services with ECF discovery
	 */
    public void dispose() {
        IDiscoveryLocator locator = Activator.getDefault().getLocator();
        if (locator != null) {
            locator.removeServiceListener(serviceListener);
            synchronized (serviceRefToServiceInfo) {
                for (Iterator i = serviceRefToServiceInfo.keySet().iterator(); i.hasNext(); ) {
                    ServiceReference sr = (ServiceReference) i.next();
                    unpublishService(sr);
                }
                serviceRefToServiceInfo.clear();
            }
            locator = null;
        }
    }
}
