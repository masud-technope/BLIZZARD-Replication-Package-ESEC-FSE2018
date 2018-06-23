/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis <slewis@composent.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice;

import java.util.Map;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;

/**
 * Abstract container that is intended for use by RSA distribution providers.  Subclasses may extend
 * and override to create custom container adapter types.  By default, an instance of RSARemoteServiceContainerAdapter
 * is created by this class upon construction.
 * 
 * @since 8.9
 */
public abstract class AbstractRSAContainer extends AbstractContainer {

    private final ID id;

    private final RSARemoteServiceContainerAdapter containerAdapter;

    public  AbstractRSAContainer(ID id) {
        this.id = id;
        this.containerAdapter = createContainerAdapter();
    }

    /**
	 *  Export an endpoint specified by the RSARemoteServiceRegistration.  Subclasses must
	 *  implement to respond to a remote service export.  This method will be called by
	 *  the ECF RemoteServiceAdmin.exportService when this container should handle the export.
	 *  
	 * @param registration the RSARemoteServiceRegistration that is being registered.  Will not
	 * be <code>null</code>.
	 *  
	 * @return Map of extra properties to add to the RSA EndpointDescription.   
	 * Any properties in the returned map will override or add to the endpoint description.  For example,
	 * if one of the properties in the returned Map is a String value for the key 'ecf.endpoint.id', 
	 * then the value from the map will override this property in the endpoint description.  The result
	 * may be <code>null</code>, in which case no properties will be overridden or added.
	 */
    protected abstract Map<String, Object> exportRemoteService(RSARemoteServiceRegistration registration);

    /**
	 * Unregister the endpoint for the given RSARemoteServiceRegistration.   Subclasses must implement
	 * to respond to a remote service export.  This method will be called when a remote service
	 * is unregistered, or unexported.
	 * 
	 * @param registration the registration identifying the remote service to unregister.  Will not
	 * be <code>null</code>.
	 */
    protected abstract void unexportRemoteService(RSARemoteServiceRegistration registration);

    protected RSARemoteServiceContainerAdapter createContainerAdapter() {
        return new RSARemoteServiceContainerAdapter(this);
    }

    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
        //$NON-NLS-1$
        throw new ContainerConnectException("Cannot connect this container");
    }

    public ID getConnectedID() {
        return null;
    }

    public Namespace getConnectNamespace() {
        return getID().getNamespace();
    }

    public void disconnect() {
    // do nothing
    }

    public ID getID() {
        return id;
    }

    @Override
    public Object getAdapter(Class serviceType) {
        Object result = super.getAdapter(serviceType);
        if (result == null && serviceType.isAssignableFrom(IRemoteServiceContainerAdapter.class))
            return containerAdapter;
        return null;
    }
}
