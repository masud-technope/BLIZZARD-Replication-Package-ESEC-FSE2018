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

import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * Factory for creating {@link DiscoveredEndpointDescription}s. A discovered
 * endpoint description factory is used by a {@link EndpointDescriptionLocator}
 * to convert a service info (discovered by the locator) into a discovered
 * endpoint description (instance of DiscoveredEndpointDescription). This
 * discovered endpoint description is then used by the
 * {@link EndpointDescriptionLocator} to notify endpoint description listeners
 * as per section 122.6 of the <a
 * href="http://www.osgi.org/download/r4v42/r4.enterprise.pdf">OSGi Enterprise
 * Specification</a> OSGi enterprise specification.
 * 
 * <p>
 * If no other instances of this service have been registered, a default
 * instance of {@link DiscoveredEndpointDescriptionFactory} will be used by the
 * {@link EndpointDescriptionLocator}. Note that this default instance is
 * registered with the lowest possible priority, so that if other
 * {@link IDiscoveredEndpointDescriptionFactory} instances are registered, they
 * will be preferred/used over the default. This means that those wishing to
 * customize/control this process of converting {@link IServiceInfo}s to
 * {@link DiscoveredEndpointDescription} must
 * create their own implementation of
 * {@link IDiscoveredEndpointDescriptionFactory}
 *  and register it with the OSGi service registry with a priority
 * ({org.osgi.framework.Constants#SERVICE_RANKING}) higher than
 * {@link Integer#MIN_VALUE}
 * <p>
 * Then at runtime, when needed by the {@link EndpointDescriptionLocator}, the
 * new discovered endpoint description factory will be used.
 * 
 * @see IServiceInfoFactory
 * 
 */
public interface IDiscoveredEndpointDescriptionFactory {

    /**
	 * Create an EndpointDescription for a discovered remote service.
	 * Implementers of this factory service may return the type of
	 * EndpointDescription appropriate for the associated distribution system
	 * (e.g. ECFEndpointDescription). Implementers should return
	 * <code>null</code> if no notification should occur.
	 * 
	 * @param locator
	 *            the locator responsible for the discoveredServiceInfo. Must
	 *            not be <code>null</code>.
	 * @param discoveredServiceInfo
	 *            the discovered service info. Must not be <code>null</code>.
	 * @return DiscoveredEndpointDescription that will be used to notify
	 *         EndpointListeners about a new EndpointDescription. If
	 *         <code>null</code> is returned, no notification should be
	 *         performed by the calling code.
	 */
    public DiscoveredEndpointDescription createDiscoveredEndpointDescription(IDiscoveryLocator locator, IServiceInfo discoveredServiceInfo);

    /**
	 * Remove an EndpointDescription for a previously discovered remote service.
	 * Implementers of this factory service may return the type of
	 * EndpointDescription appropriate for the associated distribution system
	 * (e.g. ECFEndpointDescription). Implementers should return
	 * <code>null</code> if no notification should occur.
	 * 
	 * @param locator
	 *            the locator responsible for the discoveredServiceInfo. Must
	 *            not be <code>null</code>.
	 * @param serviceID
	 *            the discovered service ID. Must not be <code>null</code>.
	 * @return EndpointDescription that will be used to notify EndpointListeners
	 *         about an undiscovered EndpointDescription. If <code>null</code>
	 *         is returned, no notification should be performed by the calling
	 *         code.
	 */
    public DiscoveredEndpointDescription removeDiscoveredEndpointDescription(IDiscoveryLocator locator, IServiceID serviceID);

    /**
	 * Remove the DiscoveredEndpointDescription associated with the given
	 * endpointDescription.
	 * 
	 * @param endpointDescription
	 *            that was previously associated with a
	 *            DiscoveredEndpointDescription (via
	 *            {@link #createDiscoveredEndpointDescription(IDiscoveryLocator, IServiceInfo)}
	 *            to be removed. Must not be <code>null</code>.
	 * @return <code>true</code> if actually removed, <code>false</code> if
	 *         nothing was removed.
	 */
    public boolean removeDiscoveredEndpointDescription(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription);

    /**
	 * Remove all DiscoveredEndpointDescription from this factory.
	 */
    public void removeAllDiscoveredEndpointDescriptions();
}
