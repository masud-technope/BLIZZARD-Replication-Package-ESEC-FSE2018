/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.core.identity.ID;

/**
 * Remote service reference. Instances implementing this interface are returned
 * from the IRemoteServiceContainerAdapter.getRemoteServiceReferences call. Once
 * retrieved, such references can be resolved to an IRemoteService via calls to
 * IRemoteServiceContainerAdapter.getRemoteService(reference)
 * 
 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter
 */
public interface IRemoteServiceReference {

    /**
	 * Get the remote service ID for this reference.  Will not return <code>null</code>.
	 * 
	 * @return IRemoteServiceID the id for the remote service associated with this
	 * reference.
	 * @since 3.0
	 */
    public IRemoteServiceID getID();

    /**
	 * Get container ID for remote service
	 * 
	 * @return ID the containerID for this reference (where the service is
	 *         located). Will not be <code>null</code> .
	 */
    public ID getContainerID();

    /**
	 * Get given property for remote service
	 * 
	 * @param key
	 *            the key for the property to get. Must not be <code>null</code> .
	 * @return Object the object or <code>null</code> if does not have named
	 *         property
	 */
    public Object getProperty(String key);

    /**
	 * Get all property keys for remote service
	 * 
	 * @return String [] of property keys. Will not be <code>null</code>, but
	 *         may be empty array.
	 */
    public String[] getPropertyKeys();

    /**
	 * Return true if reference is active, false otherwise
	 * 
	 * @return true if reference is currently active, false otherwise
	 */
    public boolean isActive();
}
