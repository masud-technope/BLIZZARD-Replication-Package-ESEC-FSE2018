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

import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.osgi.service.remoteserviceadmin.EndpointDescription;

/**
 * Default implementation of {@link IDiscoveredEndpointDescriptionFactory}
 * service.
 * 
 * @see IDiscoveredEndpointDescriptionFactory
 */
public class DiscoveredEndpointDescriptionFactory extends AbstractMetadataFactory implements IDiscoveredEndpointDescriptionFactory {

    public DiscoveredEndpointDescription createDiscoveredEndpointDescription(IDiscoveryLocator locator, IServiceInfo discoveredServiceInfo) {
        try {
            org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription = createEndpointDescription(locator, discoveredServiceInfo);
            return createDiscoveredEndpointDescription(locator, discoveredServiceInfo, endpointDescription);
        } catch (Exception e) {
            logError("createDiscoveredEndpointDescription", "Exception creating discovered endpoint description", e);
            return null;
        }
    }

    public DiscoveredEndpointDescription removeDiscoveredEndpointDescription(IDiscoveryLocator locator, IServiceID serviceID) {
        return null;
    }

    protected org.osgi.service.remoteserviceadmin.EndpointDescription createEndpointDescription(IDiscoveryLocator locator, IServiceInfo discoveredServiceInfo) {
        IServiceProperties discoveredServiceProperties = discoveredServiceInfo.getServiceProperties();
        return decodeEndpointDescription(discoveredServiceProperties);
    }

    protected DiscoveredEndpointDescription createDiscoveredEndpointDescription(IDiscoveryLocator locator, IServiceInfo discoveredServiceInfo, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        return new DiscoveredEndpointDescription(locator.getServicesNamespace(), discoveredServiceInfo.getServiceID(), endpointDescription);
    }

    public void close() {
        super.close();
    }

    public boolean removeDiscoveredEndpointDescription(EndpointDescription endpointDescription) {
        return true;
    }

    public void removeAllDiscoveredEndpointDescriptions() {
    }
}
