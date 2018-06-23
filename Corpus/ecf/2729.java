/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.osgi.framework.InvalidSyntaxException;

/**
 * 
 * Remote service container that provides access to underlying IContainer and remote service container adapter.
 * @since 3.0
 */
public interface IRemoteServiceContainer {

    /**
	 * Get the container instance for this remote service container.  Will
	 * not return <code>null</code>.
	 * @return IContainer for this remote service container.  Will not return <code>null</code>.
	 */
    public IContainer getContainer();

    /**
	 * Get the container adapter for this remote service container.
	 * Will not return <code>null</code>
	 * 
	 * @return IRemoteServiceContainerAdapter that is the adapter for the container
	 * returned from {@link #getContainer()}.
	 */
    public IRemoteServiceContainerAdapter getContainerAdapter();

    /**
	 * Get the remote service for given targetLocation and given serviceInterface class.
	 * @param targetLocation the targetLocation to connect to.  
	 * See {@link IRemoteServiceContainerAdapter#getRemoteServiceReferences(org.eclipse.ecf.core.identity.ID, String, String)}.  May be <code>null</code>.
	 * @param serviceInterfaceClass the service to find.  Must not be <code>null</code>.
	 * @param filter the {@link IRemoteFilter} to use for finding the desired remote service.
	 * @return IRemoteService the remote service.  May be <code>null</code> if the desired remote service is not available.
	 * @exception ContainerConnectException thrown if underlying container cannot connect to get remote service.
	 * @exception InvalidSyntaxException thrown if the filter does not have correct syntax.
	 */
    public IRemoteService getRemoteService(String targetLocation, String serviceInterfaceClass, String filter) throws ContainerConnectException, InvalidSyntaxException;

    /**
	 * Get the remote service for given targetLocation and given serviceInterface class.
	 * @param targetLocation the targetLocation to connect to.  
	 * See {@link IRemoteServiceContainerAdapter#getRemoteServiceReferences(org.eclipse.ecf.core.identity.ID, String, String)}.  May be <code>null</code>.
	 * @param serviceInterfaceClass the service to find.  Must not be <code>null</code>.
	 * @return IRemoteService the remote service.  May be <code>null</code> if the desired remote service is not available.
	 * @exception ContainerConnectException thrown if underlying container cannot connect to get remote service.
	 */
    public IRemoteService getRemoteService(String targetLocation, String serviceInterfaceClass) throws ContainerConnectException;

    /**
	 * Get the remote service for given serviceInterface class.
	 * @param serviceInterfaceClass the service to find.  Must not be <code>null</code>.
	 * @return IRemoteService the remote service.  May be <code>null</code> if the desired remote service is not available.
	 */
    public IRemoteService getRemoteService(String serviceInterfaceClass);
}
