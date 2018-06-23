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
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Service Client
 * @since 2.0
 */
public interface IServiceClient {

    /**
	 * Get the client's ID in String form. 
	 * @return String unique id for this client.
	 */
    public String getId();

    /**
	 * Connect to the given targetLocation
	 * @param targetLocation
	 * @throws ContainerConnectException
	 */
    public void connect(String targetLocation, IConnectContext connectContext) throws ContainerConnectException;

    /**
	 * @return <code>true</code> if client is connected, <code>false</code> otherwise.
	 */
    public boolean isConnected();

    /**
	 * Register service client.  Registers this service client in the service registry
	 * with the given BundleContext.  
	 * @param context the BundleContext to register with.  Must not be <code>null</code>.
	 * @param properties to associate with IServiceClient registration.  May be <code>null</code>.
	 * @return ServiceRegistration registration for the IServiceClient service.  Will not be <code>null</code>.
	 */
    public ServiceRegistration registerServiceClient(BundleContext context, Dictionary properties);

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
	 * Stop this service client.  This will disconnect the underlying container for this client.
	 */
    public void stop();
}
