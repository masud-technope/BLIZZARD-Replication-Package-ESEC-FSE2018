/*******************************************************************************
 * Copyright (c) 2009 Versant Corp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.userinput;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter;
import org.eclipse.ecf.discovery.DiscoveryContainerConfig;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceContainerEvent;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

public class UserInputDiscoveryLocator extends AbstractDiscoveryContainerAdapter implements IDiscoveryLocator {

    private Set knownServices;

    private ID targetId;

    public  UserInputDiscoveryLocator() throws IDCreateException {
        super(UserInputNamespace.NAME, new DiscoveryContainerConfig(IDFactory.getDefault().createStringID(UserInputDiscoveryLocator.class.getName())));
        knownServices = Collections.synchronizedSet(new HashSet());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IServiceInfo getServiceInfo(IServiceID aServiceId) {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("not implemented");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServiceTypes()
	 */
    public IServiceTypeID[] getServiceTypes() {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("not implemented");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServices()
	 */
    public IServiceInfo[] getServices() {
        return (IServiceInfo[]) knownServices.toArray(new IServiceInfo[knownServices.size()]);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IServiceInfo[] getServices(IServiceTypeID aServiceTypeId) {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("not implemented");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#registerService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void registerService(IServiceInfo serviceInfo) {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("not implemented");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void unregisterService(IServiceInfo serviceInfo) {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("not implemented");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(ID targetId, IConnectContext connectContext) throws ContainerConnectException {
        fireContainerEvent(new ContainerConnectingEvent(this.getID(), targetId, connectContext));
        fireContainerEvent(new ContainerConnectedEvent(this.getID(), targetId));
        this.targetId = targetId;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public void disconnect() {
        fireContainerEvent(new ContainerDisconnectingEvent(this.getID(), targetId));
        fireContainerEvent(new ContainerConnectedEvent(this.getID(), targetId));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return getID();
    }

    public void fireServiceResolved(URI anURI, IServiceTypeID id) {
        //$NON-NLS-1$
        IServiceInfo iinfo = new ServiceInfo(anURI, "", id);
        if (knownServices.add(iinfo)) {
            fireServiceDiscovered(new ServiceContainerEvent(iinfo, getID()));
        }
    }

    public void fireServiceRemoved(URI anURI, IServiceTypeID id) {
        //$NON-NLS-1$
        IServiceInfo iinfo = new ServiceInfo(anURI, "", id);
        if (knownServices.remove(iinfo)) {
            fireServiceUndiscovered(new ServiceContainerEvent(iinfo, getID()));
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#getContainerName()
	 */
    public String getContainerName() {
        return "ecf.discovery.userinput";
    }
}
