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

import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;

/**
 * Consumer container selector service contract. When an ECF RemoteServiceAdmin
 * instance is asked to import a service (i.e. via
 * {@link RemoteServiceAdmin#importService(org.osgi.service.remoteserviceadmin.EndpointDescription)}
 * ), the RSA first gets an instance of this service via the service registry,
 * and then uses it to select an ECF consumer container instance by calling
 * {@link #selectConsumerContainer(EndpointDescription)}.
 * <p>
 * The {@link IRemoteServiceContainer} returned is then used on the consumer
 * side, to actually import the remote service.
 * <p>
 * If no other instances of this service have been registered, a default
 * instance of {@link ConsumerContainerSelector} will be used. Note that this
 * default instance is registered with the lowest possible priority, so that if
 * other {@link IConsumerContainerSelector} instances are registered, they will
 * be preferred/used over the default.
 * 
 */
public interface IConsumerContainerSelector {

    /**
	 * Select (or create and initialize) a consumer remote service container.
	 * 
	 * @param endpointDescription
	 *            the endpoint description that has been discovered.
	 * @return IRemoteServiceContainer to be used for importing the remote
	 *         service. May be <code>null</code> if not container is available
	 *         for use as a consumer for the given endpointDescription.
	 * @throws SelectContainerException
	 *             thrown if the host container selection or
	 *             creation/configuration fails.
	 */
    public IRemoteServiceContainer selectConsumerContainer(EndpointDescription endpointDescription) throws SelectContainerException;
}
