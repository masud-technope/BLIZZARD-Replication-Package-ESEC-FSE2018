/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.discovery;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * Entry point discovery advertiser. This interface exposes the ability to
 * register and unregister locally provided services.
 * <p>
 * This interface can be used by container provider implementations as an
 * adapter so that calls to IContainer.getAdapter(IDiscoveryAdvertiser.class)
 * will return a non-null instance of a class that implements this interface.
 * Clients can then proceed to use this interface to interact with the given
 * discovery implementation.
 * 
 * @since 3.0
 */
public interface IDiscoveryAdvertiser extends IAdaptable {

    /**
	 * The name of the discovery container under which it is registered with the
	 * OSGi runtime as a service property
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_NAME = "org.eclipse.ecf.discovery.containerName";

    /**
	 * Register the given service. This publishes the service defined by the
	 * serviceInfo to the underlying publishing mechanism
	 * 
	 * @param serviceInfo
	 *            IServiceInfo of the service to be published. Must not be
	 *            <code>null</code>.
	 */
    public void registerService(IServiceInfo serviceInfo);

    /**
	 * Unregister a previously registered service defined by serviceInfo.
	 * 
	 * @param serviceInfo
	 *            IServiceInfo defining the service to unregister. Must not be
	 *            <code>null</code>.
	 */
    public void unregisterService(IServiceInfo serviceInfo);

    /**
	 * Unregister all previously registered service.
	 */
    public void unregisterAllServices();

    /**
	 * Get a Namespace for services associated with this discovery container
	 * adapter. The given Namespace may be used via IServiceIDFactory to create
	 * IServiceIDs rather than simple IDs. For example:
	 * 
	 * <pre>
	 * IServiceID serviceID = ServiceIDFactory.getDefault().createServiceID(
	 * 		container.getServicesNamespace(), serviceType, serviceName);
	 * </pre>
	 * 
	 * @return Namespace for creating service IDs. Will not be <code>null</code>
	 *         .
	 */
    public Namespace getServicesNamespace();
}
