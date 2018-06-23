/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * @since 4.3
 */
public interface IEndpointDescriptionLocator {

    /**
	 * Get endpoints discovered by this endpoint locator
	 * @return EndpointDescription[] of previously discovered endpoint.  Will not return null,
	 * but may return empty array.
	 */
    EndpointDescription[] getDiscoveredEndpoints();

    /**
	 * Get the service ID associated with the given endpoint description.
	 * 
	 * @param endpointDescription endpoint description
	 * @return IServiceID associated discovered endpoint description. Will
	 *         return <code>null</code> if no associated serviceID
	 */
    IServiceID getNetworkDiscoveredServiceID(EndpointDescription endpointDescription);

    /**
	 * Discover the given endpointDescription.  This method will not block
	 * and will result in local EndpointEventListeners to be notified that the
	 * given endpointDescription is discovered.
	 * about
	 * @param endpointDescription must not be null
	 */
    void discoverEndpoint(EndpointDescription endpointDescription);

    /**
	 * Update the given endpointDescription.  This method will not block
	 * and will result in local EndpointEventListeners to be notified that the
	 * given endpointDescription is updated.
	 * about
	 * @param endpointDescription must not be null
	 */
    void updateEndpoint(EndpointDescription endpointDescription);

    /**
	 * Remove the given endpointDescription.  This method will not block
	 * and will result in local EndpointEventListeners to be notified that the
	 * given endpointDescription is removed.
	 * about
	 * @param endpointDescription must not be null
	 */
    void undiscoverEndpoint(EndpointDescription endpointDescription);
}
