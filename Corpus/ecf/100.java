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
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.equinox.concurrent.future.IFuture;

/**
 * Entry point discovery locator. This interface exposes the ability to
 * add/remove listeners for newly discovered services and service types, and get
 * (synch) and request (asynchronous) service info from a remote service
 * provider.
 * <p>
 * This interface can be used by container provider implementations as an
 * adapter so that calls to IContainer.getAdapter(IDiscoveryLocator.class) will
 * return a non-null instance of a class that implements this interface. Clients
 * can then proceed to use this interface to interact with the given discovery
 * implementation.
 * 
 * @since 3.0
 */
public interface IDiscoveryLocator extends IAdaptable {

    /**
	 * The name of the discovery container under which it is registered with the
	 * OSGi runtime as a service property
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_NAME = "org.eclipse.ecf.discovery.containerName";

    /**
	 * Synchronously retrieve info about the service
	 * 
	 * @param aServiceID
	 *            IServiceID of the service to get info about. Must not be
	 *            <code>null</code>.
	 * @return IServiceInfo the service info retrieved. <code>null</code> if no
	 *         information retrievable.
	 */
    public IServiceInfo getServiceInfo(IServiceID aServiceID);

    /**
	 * Synchronously get service info about all known services
	 * 
	 * @return IServiceInfo[] the resulting array of service info instances.
	 *         Will not be <code>null</code>. May be of length 0.
	 */
    public IServiceInfo[] getServices();

    /**
	 * Synchronously get service info about all known services of given service
	 * type
	 * 
	 * @param aServiceTypeID
	 *            IServiceTypeID defining the type of service we are interested
	 *            in getting service info about. Must not be <code>null</code>
	 * @return IServiceInfo[] the resulting array of service info instances.
	 *         Will not be <code>null</code>. May be of length 0.
	 */
    public IServiceInfo[] getServices(IServiceTypeID aServiceTypeID);

    /**
	 * Synchronously get service info about all known services of given service
	 * type
	 * 
	 * @return IServiceTypeID[] the resulting array of service type IDs. Will
	 *         not be <code>null</code>. May be of length 0.
	 */
    public IServiceTypeID[] getServiceTypes();

    /**
	 * Get a Namespace for services associated with this discovery container
	 * adapter. The given Namespace may be used via IServiceIDFactory to create
	 * IServiceIDs rather than simple IDs. For example:
	 * 
	 * <pre>
	 * IServiceID serviceID = ServiceIDFactory.getDefault().createServiceID(container.getServicesNamespace(),
	 * 		serviceType, serviceName);
	 * </pre>
	 * 
	 * @return Namespace for creating service IDs. Will not be <code>null</code>
	 *         .
	 */
    public Namespace getServicesNamespace();

    /**
	 * Purges the underlying IServiceInfo cache if available in the current
	 * provider
	 * 
	 * @return The previous cache content
	 */
    public IServiceInfo[] purgeCache();

    /* Listener related API */
    /**
	 * Add a service listener. The given listener will have its method called
	 * when a service is discovered.
	 * 
	 * @param listener
	 *            IServiceListener to be notified. Must not be <code>null</code>
	 *            .
	 */
    public void addServiceListener(IServiceListener listener);

    /**
	 * Add a service listener. The given listener will have its method called
	 * when a service with a type matching that specified by the first parameter
	 * is discovered.
	 * 
	 * @param type
	 *            String type to listen for. Must not be <code>null</code>. Must
	 *            be formatted according to this specific IDiscoveryContainer
	 * @param listener
	 *            IServiceListener to be notified. Must not be <code>null</code>
	 *            .
	 */
    public void addServiceListener(IServiceTypeID type, IServiceListener listener);

    /**
	 * Add a service type listener. The given listener will have its method
	 * called when a service type is discovered.
	 * 
	 * @param listener
	 *            the listener to be notified. Must not be <code>null</code>.
	 */
    public void addServiceTypeListener(IServiceTypeListener listener);

    /**
	 * Remove a service listener. Remove the listener from this container
	 * 
	 * @param listener
	 *            IServiceListener listener to be removed. Must not be
	 *            <code>null</code>.
	 */
    public void removeServiceListener(IServiceListener listener);

    /**
	 * Remove a service listener. Remove the listener associated with the type
	 * specified by the first parameter.
	 * 
	 * @param type
	 *            String of the desired type to remove the listener. Must not be
	 *            <code>null</code>. Must be formatted according to this
	 *            specific IDiscoveryContainer
	 * @param listener
	 *            IServiceListener listener to be removed. Must not be
	 *            <code>null</code>.
	 */
    public void removeServiceListener(IServiceTypeID type, IServiceListener listener);

    /**
	 * Remove a service type listener. Remove the type listener.
	 * 
	 * @param listener
	 *            IServiceTypeListener to be removed. Must not be
	 *            <code>null</code>.
	 */
    public void removeServiceTypeListener(IServiceTypeListener listener);

    /* Future related API */
    /**
	 * Asynchronously retrieve info about the service
	 * 
	 * @param aServiceID
	 *            IServiceID of the service to get info about. Must not be
	 *            <code>null</code>.
	 * @return IFuture a future status wrapping an IServiceInfo or
	 *         <code>null</code> if no information retrievable.
	 */
    public IFuture getAsyncServiceInfo(IServiceID aServiceID);

    /**
	 * Asynchronously get service info about all known services
	 * 
	 * @return IFuture wrapping an IServiceTypeID[]. The resulting array of
	 *         service type IDs will not be <code>null</code>. May be of length
	 *         0.
	 */
    public IFuture getAsyncServices();

    /**
	 * Asynchronously get service info about all known services of given service
	 * type
	 * 
	 * @param aServiceTypeID
	 *            IServiceTypeID defining the type of service we are interested
	 *            in getting service info about. Must not be <code>null</code>
	 * @return IFuture wrapping an IServiceTypeID[]. The resulting array of
	 *         service type IDs will not be <code>null</code>. May be of length
	 *         0.
	 */
    public IFuture getAsyncServices(IServiceTypeID aServiceTypeID);

    /**
	 * Asynchronously get service info about all known services of given service
	 * type
	 * 
	 * @return IFuture wrapping an IServiceTypeID[]. The resulting array of
	 *         service type IDs will not be <code>null</code>. May be of length
	 *         0.
	 */
    public IFuture getAsyncServiceTypes();
}
