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

import java.util.Dictionary;
import org.eclipse.ecf.core.identity.ID;

/**
 * Remote service registration. The remote service registration is returned to
 * the caller when the IServiceContainer.registerService method is called. The
 * registering bundle can then use the registration instance to unregister the
 * service
 * 
 */
public interface IRemoteServiceRegistration {

    /**
	 * Get the remote service ID for this registration.  Will not return <code>null</code>.
	 * 
	 * @return IRemoteServiceID the id for the remote service associated with this
	 * registration.
	 * @since 3.0
	 */
    public IRemoteServiceID getID();

    /**
	 * Get the container ID for the registration
	 * 
	 * @return ID of the local container. Will not be <code>null</code> .
	 */
    public ID getContainerID();

    /**
	 * Get reference for this registration
	 * 
	 * @return IRemoteServiceReference for this registration. Will not be
	 *         <code>null</code>.
	 */
    public IRemoteServiceReference getReference();

    /**
	 * Set the properties for the registered service
	 * 
	 * @param properties
	 *            to set. Must not be <code>null</code>.
	 */
    public void setProperties(Dictionary properties);

    /**
	 * Get property associated with given key
	 * 
	 * @param key
	 *            the key of the property. Must not be <code>null</code>.
	 * @return Object the property value. <code>null</code> if property not
	 *         found.
	 */
    public Object getProperty(String key);

    /**
	 * Get property keys for registered service
	 * 
	 * @return String [] with property keys. Will not be null, but may be empty
	 *         array.
	 */
    public String[] getPropertyKeys();

    /**
	 * Unregister this service
	 * 
	 */
    public void unregister();
}
