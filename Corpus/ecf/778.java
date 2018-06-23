/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.r_osgi;

import java.util.Dictionary;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.*;
import org.osgi.framework.ServiceRegistration;

/**
 * The R-OSGi adapter implementation of the IRemoteServiceRegistration.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
final class RemoteServiceRegistrationImpl implements IRemoteServiceRegistration {

    private IRemoteServiceID remoteServiceID;

    // the service registration.
    private ServiceRegistration reg;

    private IRemoteServiceReference remoteReference;

    private R_OSGiRemoteServiceContainer container;

    /**
	 * constructor.
	 * 
	 * @param containerID
	 *            the container ID.
	 * @param reg
	 *            the R-OSGi internal service registration.
	 */
    public  RemoteServiceRegistrationImpl(final R_OSGiRemoteServiceContainer container, final IRemoteServiceID remoteServiceID, final ServiceRegistration reg) {
        Assert.isNotNull(remoteServiceID);
        Assert.isNotNull(reg);
        Assert.isNotNull(container);
        this.container = container;
        this.remoteServiceID = remoteServiceID;
        this.reg = reg;
        this.remoteReference = new LocalRemoteServiceReferenceImpl(remoteServiceID, reg.getReference());
    }

    /**
	 * get the container ID.
	 * 
	 * @return the container ID.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#getContainerID()
	 */
    public ID getContainerID() {
        return getID().getContainerID();
    }

    /**
	 * get a property of the service.
	 * 
	 * @param key
	 *            the key.
	 * @return the value.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#getProperty(java.lang.String)
	 */
    public Object getProperty(final String key) {
        return reg.getReference().getProperty(key);
    }

    /**
	 * get the property keys.
	 * 
	 * @return the keys.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#getPropertyKeys()
	 */
    public String[] getPropertyKeys() {
        return reg.getReference().getPropertyKeys();
    }

    /**
	 * get the remote service reference; FIXME: problem: with R-OSGi, there is
	 * not necessarily a remote service reference with the registered remote
	 * service.
	 * 
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#getReference()
	 */
    public IRemoteServiceReference getReference() {
        return remoteReference;
    }

    /**
	 * update the properties of the remote service.
	 * 
	 * @param properties
	 *            a set of property key/value pairs to be updated.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#setProperties(java.util.Dictionary)
	 */
    public void setProperties(final Dictionary properties) {
        reg.setProperties(properties);
    }

    /**
	 * unregister the remote service.
	 * 
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceRegistration#unregister()
	 */
    public void unregister() {
        container.removeRegistration(reg);
        try {
            reg.unregister();
        } catch (IllegalStateException e) {
        }
    }

    public IRemoteServiceID getID() {
        return remoteServiceID;
    }
}
