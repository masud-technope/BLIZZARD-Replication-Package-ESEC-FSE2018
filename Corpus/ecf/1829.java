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

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * Discovered endpoint description. Instances of this class represent discovered
 * endpoint descriptions that were discovered by a particular discovery locator
 * namespace. Instances of this class are typically created via a
 * {@link IDiscoveredEndpointDescriptionFactory}.
 * 
 * @see IDiscoveredEndpointDescriptionFactory
 */
public class DiscoveredEndpointDescription {

    private Namespace discoveryLocatorNamespace;

    private IServiceID serviceID;

    private org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription;

    private int hashCode = 7;

    public  DiscoveredEndpointDescription(Namespace discoveryLocatorNamespace, IServiceID serviceID, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        this.discoveryLocatorNamespace = discoveryLocatorNamespace;
        this.serviceID = serviceID;
        this.endpointDescription = endpointDescription;
        this.hashCode = 31 * this.hashCode + discoveryLocatorNamespace.getName().hashCode();
        this.hashCode = 31 * this.hashCode + endpointDescription.hashCode();
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof DiscoveredEndpointDescription))
            return false;
        DiscoveredEndpointDescription o = (DiscoveredEndpointDescription) other;
        return (this.discoveryLocatorNamespace.equals(o.discoveryLocatorNamespace) && this.endpointDescription.equals(o.endpointDescription));
    }

    public Namespace getDiscoveryLocatorNamespace() {
        return discoveryLocatorNamespace;
    }

    public IServiceID getServiceID() {
        return serviceID;
    }

    public org.osgi.service.remoteserviceadmin.EndpointDescription getEndpointDescription() {
        return endpointDescription;
    }
}
