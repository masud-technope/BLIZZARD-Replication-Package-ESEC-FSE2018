/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice;

import java.util.Dictionary;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * @since 5.0
 */
public interface IRemoteServiceHost extends IAdaptable {

    /**
	 * Register a new remote service. This method is to be called by the service
	 * server...i.e. the client that wishes to make available a service to other
	 * client within this container.
	 * 
	 * @param clazzes
	 *            the interface classes that the service exposes to remote
	 *            clients. Must not be <code>null</code> and must not be an
	 *            empty array.
	 * @param service
	 *            the service object.  Under normal conditions this object must
	 *            <ul><li>not be <code>null</code></li>
	 *            <li>implement all of the classes specified by the first parameter</li>
	 *            </ul>
	 *            The only situation when the service object may be <code>null</code> is if
	 *            the service property {@link Constants#SERVICE_REGISTER_PROXY} is set
	 *            in the properties.  If {@link Constants#SERVICE_REGISTER_PROXY} is set
	 *            in the properties parameter (to an arbitrary value), then the service
	 *            object may then be <code>null</code>.
	 * @param properties
	 *            to be associated with service
	 * @return IRemoteServiceRegistration the service registration. Will not
	 *         return <code>null</code> .
	 */
    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary properties);

    /**
	 * Add listener for remote service registration/unregistration for this
	 * container
	 * 
	 * @param listener
	 *            notified of service registration/unregistration events. Must
	 *            not be <code>null</code> .
	 */
    public void addRemoteServiceListener(IRemoteServiceListener listener);

    /**
	 * Remove remote service registration/unregistration listener for this
	 * container.
	 * 
	 * @param listener
	 *            to remove. Must not be <code>null</code> .
	 */
    public void removeRemoteServiceListener(IRemoteServiceListener listener);

    /**
	 * Get namespace to use for this remote service provider.
	 * @return Namespace to use for creating IRemoteServiceID for this remote service provider.  Will
	 * not return <code>null</code>.
	 * @since 3.0
	 */
    public Namespace getRemoteServiceNamespace();

    /**
	 * Get a remote service ID from a containerID and a containerRelative long value.  Will return a non-null value
	 * if the IRemoteServiceRegistration/Reference is currently 'known' to this container adapter.  <code>null</code> 
	 * if not.
	 * @param containerID the containerID that is the server/host for the remote service.  Must not be <code>null</code>.  This 
	 * must be the containerID for the <b>server</b>/host of the remote service.  
	 * @param containerRelativeID the long value identifying the remote service relative to the container ID.
	 * @return IRemoteServiceID instance if the associated IRemoteServiceRegistration/Reference is known to this container
	 * adapter, <code>null</code> if it is not.
	 * @since 3.0
	 */
    public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID);
}
