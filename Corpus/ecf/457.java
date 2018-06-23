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

import java.util.Map;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.osgi.framework.ServiceReference;

/**
 * Host container selector service contract. When an ECF RemoteServiceAdmin
 * instance is asked to import a service (i.e. via
 * {@link RemoteServiceAdmin#exportService(ServiceReference, java.util.Map)} ),
 * the RSA first gets an instance of this service via the service registry, and
 * then uses it to select an array of ECF host container instances by calling
 * selectHostContainers.
 * <p>
 * The {@link IRemoteServiceContainer} array returned is then used to actually
 * export the remote service (typically via
 * {@link IRemoteServiceContainerAdapter#registerRemoteService(String[], Object, java.util.Dictionary)}
 * <p>
 * If no other instances of this service have been registered, a default
 * instance of {@link HostContainerSelector} will be used. Note that this
 * default instance is registered with the lowest possible priority, so that if
 * other {@link IHostContainerSelector} instances are registered, they will be
 * preferred/used over the default.
 * 
 */
public interface IHostContainerSelector {

    /**
	 * 
	 * Select host containers to use to export a remote service.
	 * 
	 * @param serviceReference
	 *            the service reference given by the
	 *            {@link RemoteServiceAdmin#exportService(ServiceReference, java.util.Map)}
	 * @param overridingProperties
	 *            the map portion given by the
	 *            {@link RemoteServiceAdmin#exportService(ServiceReference, java.util.Map)}
	 * @param exportedInterfaces
	 *            the exportedInterfaces (typically associated with
	 *            {@link org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_INTERFACES}
	 *            ). Will not be <code>null</code>.
	 * @param exportedConfigs
	 *            the exportedConfigs (typically associated with
	 *            {@link org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_CONFIGS}
	 *            ). May be <code>null</code>.
	 * @param serviceIntents
	 *            the service intents (typically associated with
	 *            {@link org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_INTENTS}
	 *            and
	 *            {@link org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_INTENTS_EXTRA}
	 *            ). May be <code>null</code>.
	 * @return IRemoteServiceContainer[] of remote service containers that
	 *         should be used to export the given remote service (typically via
	 *         {@link IRemoteServiceContainerAdapter#registerRemoteService(String[], Object, java.util.Dictionary)}
	 *         ). Will not be <code>null</code>, but may be empty array.
	 * @throws SelectContainerException
	 *             thrown if the host container selection or
	 *             creation/configuration fails.
	 * @since 2.0
	 */
    IRemoteServiceContainer[] selectHostContainers(ServiceReference serviceReference, Map<String, Object> overridingProperties, String[] exportedInterfaces, String[] exportedConfigs, String[] serviceIntents) throws SelectContainerException;
}
