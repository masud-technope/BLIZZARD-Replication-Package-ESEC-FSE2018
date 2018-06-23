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

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.PropertiesUtil;

/**
 * Default implementation of {@link IServiceInfoFactory}.
 * 
 */
public class ServiceInfoFactory extends AbstractMetadataFactory implements IServiceInfoFactory {

    private final List<String> discoveryProperties;

    public  ServiceInfoFactory() {
        discoveryProperties = Arrays.asList(new String[] { RemoteConstants.DISCOVERY_DEFAULT_SERVICE_NAME_PREFIX, RemoteConstants.DISCOVERY_NAMING_AUTHORITY, RemoteConstants.DISCOVERY_PROTOCOLS, RemoteConstants.DISCOVERY_SCOPE, RemoteConstants.DISCOVERY_SERVICE_NAME, RemoteConstants.DISCOVERY_SERVICE_PRIORITY, RemoteConstants.DISCOVERY_SERVICE_TTL, RemoteConstants.DISCOVERY_SERVICE_TYPE, RemoteConstants.DISCOVERY_SERVICE_WEIGHT });
    }

    /**
	 * @since 3.0
	 */
    public IServiceInfo createServiceInfo(IDiscoveryAdvertiser advertiser, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        try {
            IServiceTypeID serviceTypeID = createServiceTypeID(endpointDescription, advertiser);
            String serviceName = createServiceName(endpointDescription, advertiser, serviceTypeID);
            URI uri = createURI(endpointDescription, advertiser, serviceTypeID, serviceName);
            IServiceProperties serviceProperties = createServiceProperties(endpointDescription, advertiser, serviceTypeID, serviceName, uri);
            Map edProperties = endpointDescription.getProperties();
            int priority = PropertiesUtil.getIntWithDefault(edProperties, RemoteConstants.DISCOVERY_SERVICE_PRIORITY, ServiceInfo.DEFAULT_PRIORITY);
            int weight = PropertiesUtil.getIntWithDefault(edProperties, RemoteConstants.DISCOVERY_SERVICE_WEIGHT, ServiceInfo.DEFAULT_WEIGHT);
            Long ttl = PropertiesUtil.getLongWithDefault(edProperties, RemoteConstants.DISCOVERY_SERVICE_TTL, ServiceInfo.DEFAULT_TTL);
            return new ServiceInfo(uri, serviceName, serviceTypeID, priority, weight, serviceProperties, ttl);
        } catch (Exception e) {
            logError("createServiceInfo", "Exception creating service info for endpointDescription=" + endpointDescription + ",advertiser=" + advertiser, e);
            return null;
        }
    }

    protected IServiceInfo createServiceInfo(URI uri, String serviceName, IServiceTypeID serviceTypeID, IServiceProperties serviceProperties) {
        return new ServiceInfo(uri, serviceName, serviceTypeID, serviceProperties);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param advertiser advertiser
	 * @param serviceTypeID serviceTypeID
	 * @param serviceName serviceName
	 * @param uri uri
	 * @return IServiceProperties for the given input parameters
	 * @since 3.0
	 */
    protected IServiceProperties createServiceProperties(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IDiscoveryAdvertiser advertiser, IServiceTypeID serviceTypeID, String serviceName, URI uri) {
        Map<String, Object> props = endpointDescription.getProperties();
        Map<String, Object> result = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        for (String key : props.keySet()) if (!discoveryProperties.contains(key))
            result.put(key, props.get(key));
        ServiceProperties spResult = new ServiceProperties();
        encodeServiceProperties(new EndpointDescription(result), spResult);
        return spResult;
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param advertiser advertiser
	 * @param serviceTypeID serviceTypeID
	 * @param serviceName service name
	 * @return URI created
	 * @throws URISyntaxException thrown if URI cannot be created
	 * @since 3.0
	 */
    protected URI createURI(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IDiscoveryAdvertiser advertiser, IServiceTypeID serviceTypeID, String serviceName) throws URISyntaxException {
        //$NON-NLS-1$
        String path = "/" + serviceName;
        String str = endpointDescription.getId();
        URI uri = null;
        while (true) {
            try {
                uri = new URI(str);
                if (uri.getHost() != null) {
                    break;
                } else {
                    final String rawSchemeSpecificPart = uri.getRawSchemeSpecificPart();
                    // make sure we break eventually
                    if (str.equals(rawSchemeSpecificPart)) {
                        uri = null;
                        break;
                    } else {
                        str = rawSchemeSpecificPart;
                    }
                }
            } catch (URISyntaxException e) {
                uri = null;
                break;
            }
        }
        String scheme = RemoteConstants.DISCOVERY_SERVICE_TYPE;
        int port = 32565;
        if (uri != null) {
            port = uri.getPort();
            if (port == -1)
                port = 32565;
        }
        String host = null;
        if (uri != null) {
            host = uri.getHost();
        } else {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                logWarning("createURI", "failed to get local host adress, falling back to \'localhost\'.", e);
                host = "localhost";
            }
        }
        return new URI(scheme, null, host, port, path, null, null);
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param advertiser advertiser
	 * @param serviceTypeID serviceTypeID
	 * @return String created service name for input parameters
	 * @since 3.0
	 */
    protected String createServiceName(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IDiscoveryAdvertiser advertiser, IServiceTypeID serviceTypeID) {
        // First create unique default name
        String defaultServiceName = createDefaultServiceName(endpointDescription, advertiser, serviceTypeID);
        // Look for service name that was explicitly set
        String serviceName = PropertiesUtil.getStringWithDefault(endpointDescription.getProperties(), RemoteConstants.DISCOVERY_SERVICE_NAME, defaultServiceName);
        return serviceName;
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param advertiser advertiser
	 * @param serviceTypeID serviceTypeID
	 * @return String default service name created
	 * @since 3.0
	 */
    protected String createDefaultServiceName(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IDiscoveryAdvertiser advertiser, IServiceTypeID serviceTypeID) {
        return RemoteConstants.DISCOVERY_DEFAULT_SERVICE_NAME_PREFIX + IDFactory.getDefault().createGUID().getName();
    }

    /**
	 * @param endpointDescription endpoint description
	 * @param advertiser advertiser
	 * @return IServiceTypeID created service type ID
	 * @since 3.0
	 */
    protected IServiceTypeID createServiceTypeID(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IDiscoveryAdvertiser advertiser) {
        Namespace servicesNamespace = IDFactory.getDefault().getNamespaceByName(//$NON-NLS-1$
        "ecf.namespace.discovery");
        if (advertiser != null) {
            servicesNamespace = advertiser.getServicesNamespace();
        }
        Map props = endpointDescription.getProperties();
        String[] scopes = PropertiesUtil.getStringArrayWithDefault(props, RemoteConstants.DISCOVERY_SCOPE, IServiceTypeID.DEFAULT_SCOPE);
        String[] protocols = PropertiesUtil.getStringArrayWithDefault(props, RemoteConstants.DISCOVERY_PROTOCOLS, IServiceTypeID.DEFAULT_SCOPE);
        String namingAuthority = PropertiesUtil.getStringWithDefault(props, RemoteConstants.DISCOVERY_NAMING_AUTHORITY, IServiceTypeID.DEFAULT_NA);
        return ServiceIDFactory.getDefault().createServiceTypeID(servicesNamespace, new String[] { RemoteConstants.DISCOVERY_SERVICE_TYPE }, scopes, protocols, namingAuthority);
    }
}
