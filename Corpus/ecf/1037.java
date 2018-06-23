/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import java.net.URI;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * Service information contract. Defines the information associated with a
 * remotely discoverable service
 * 
 * see http://www.dns-sd.org/ServiceTypes.html and
 * http://www.ietf.org/rfc/rfc2782.txt
 * 
 */
public interface IServiceInfo extends IAdaptable {

    /**
	 * Get URI for service
	 * 
	 * @return {@link java.net.URI} the location for the service.
	 */
    public URI getLocation();

    /**
	 * Get ServiceID for service.
	 * 
	 * @return ServiceID the serviceID for the service. Will not be
	 *         <code>null</code>.
	 */
    public IServiceID getServiceID();

    /**
	 * The priority for the service
	 * 
	 * Priority: The priority of this target host. A client MUST attempt to
	 * contact the target host with the lowest-numbered priority it can reach;
	 * target hosts with the same priority SHOULD be tried in an order defined
	 * by the weight field.
	 * 
	 * @return int the priority. 0 if no priority information for service.
	 */
    public int getPriority();

    /**
	 * The weight for the service. 0 if no weight information for service.
	 * 
	 * Weight: A server selection mechanism. The weight field specifies a
	 * relative weight for entries with the same priority. Larger weights SHOULD
	 * be given a proportionately higher probability of being selected. Domain
	 * administrators SHOULD use Weight 0 when there isn't any server selection
	 * to do. In the presence of records containing weights greater than 0,
	 * records with weight 0 should have a very small chance of being selected.
	 * 
	 * @return int the weight
	 */
    public int getWeight();

    /**
	 * The time to live for the service. -1 if no TTL given for service.
	 * 
	 * TTL: A time to live (TTL) defining the live time of a service.
	 * 
	 * @return long the time to live in seconds
	 * @since 4.0
	 */
    public long getTTL();

    /**
	 * Map with any/all properties associated with the service. Properties are
	 * assumed to be name/value pairs, both of type String.
	 * 
	 * @return Map the properties associated with this service. Will not be
	 *         <code>null</code>.
	 */
    public IServiceProperties getServiceProperties();

    /**
	 * A user choose label used for pretty printing this service.
	 * 
	 * @return A human readable service name. Not used for uniqueness!
	 * @since 3.0
	 */
    public String getServiceName();
}
