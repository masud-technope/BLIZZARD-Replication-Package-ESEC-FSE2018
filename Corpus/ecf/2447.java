/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis and Jeff McAffer - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.ecf.server;

import java.util.Dictionary;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Service Host
 * @since 2.0
 */
public interface IServiceHost {

    /**
	 * Get the connect target location for this service host.   Will not be <code>null</code>.
	 * @return String that provides an connect target location for IServiceClients to use
	 * to connect to this service host.
	 */
    public String getConnectTargetLocation();

    /**
	 * Start the service host.  This will make the service host active, and available on the network.
	 * @throws Exception if something goes wrong with initialization or starting.
	 */
    public void start() throws Exception;

    /**
	 * Register service host.  Registers this service host in the service registry
	 * with the given BundleContext.  
	 * @param context the BundleContext to register with.  Must not be <code>null</code>.
	 * @param properties to associate with IServiceHost registration.  May be <code>null</code>.
	 * @return ServiceRegistration registration for the IServiceHost service.  Will not be <code>null</code>.
	 */
    public ServiceRegistration registerServiceHost(BundleContext context, Dictionary properties);

    /**
	 * Register a remote service with this service client.  This allows remote services to be registered/exposed
	 * for remote usage. 
	 * @param clazzes the interface class names of the remote services expose.
	 * @param service the actual service implementation.
	 * @param remoteServiceProperties and remote service properties to be exposed to clients of the remote service.
	 * @return IRemoteServiceRegistration the remote service registration for the registered remote service.
	 * Will not be <code>null</code>.
	 */
    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary remoteServiceProperties);

    /**
	 * Get the remote service container for this service client.
	 * @return IRemoteServiceContainer will not be <code>null</code>.
	 */
    public IRemoteServiceContainer getRemoteServiceContainer();

    /**
	 * Stop this service host.  This will stop any/all the underlying containers for this host.
	 */
    public void stop();
}
