/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.discovery;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.equinox.concurrent.future.IFuture;

/**
 * 
 */
public class SingletonDiscoveryContainer implements IDiscoveryContainerAdapter, IContainer {

    private final IDiscoveryContainerAdapter discovery;

    private final IContainer container;

    private boolean initialized = false;

    /**
	 * @param container
	 */
    public  SingletonDiscoveryContainer(final IContainer container) {
        super();
        this.container = container;
        this.discovery = (IDiscoveryContainerAdapter) container.getAdapter(IDiscoveryContainerAdapter.class);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)
	 */
    public synchronized void connect(final ID targetID, final IConnectContext connectContext) throws ContainerConnectException {
        if (initialized == false) {
            initialized = true;
            container.connect(targetID, connectContext);
        }
    //TODO do some meaningful logging
    // clients aren't allowed to call this;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public void disconnect() {
    //TODO do some meaningful logging
    // clients aren't allowed to call this;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#dispose()
	 */
    public void dispose() {
    //TODO do some meaningful logging
    // clients aren't allowed to call this;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(final Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.isInstance(this))
            return this;
        return null;
    }

    //******************* below just delegates ******************//
    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void addServiceListener(final IServiceTypeID type, final IServiceListener listener) {
        discovery.addServiceListener(type, listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
    public void addServiceTypeListener(final IServiceTypeListener listener) {
        discovery.addServiceTypeListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IServiceInfo getServiceInfo(final IServiceID service) {
        return discovery.getServiceInfo(service);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IServiceInfo[] getServices(final IServiceTypeID type) {
        return discovery.getServices(type);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServicesNamespace()
	 */
    public Namespace getServicesNamespace() {
        return discovery.getServicesNamespace();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void registerService(final IServiceInfo serviceInfo) {
        discovery.registerService(serviceInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void removeServiceListener(final IServiceTypeID type, final IServiceListener listener) {
        discovery.removeServiceListener(type, listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
    public void removeServiceTypeListener(final IServiceTypeListener listener) {
        discovery.removeServiceTypeListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void unregisterService(final IServiceInfo serviceInfo) {
        discovery.unregisterService(serviceInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#addListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void addListener(final IContainerListener listener) {
        container.addListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return container.getConnectedID();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
    public Namespace getConnectNamespace() {
        return container.getConnectNamespace();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return container.getID();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#removeListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void removeListener(final IContainerListener listener) {
        container.removeListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices()
	 */
    public IServiceInfo[] getServices() {
        return discovery.getServices();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceTypes()
	 */
    public IServiceTypeID[] getServiceTypes() {
        return discovery.getServiceTypes();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void addServiceListener(final IServiceListener listener) {
        discovery.addServiceListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void removeServiceListener(final IServiceListener listener) {
        discovery.removeServiceListener(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#purgeCache()
	 */
    public IServiceInfo[] purgeCache() {
        return discovery.purgeCache();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IFuture getAsyncServiceInfo(IServiceID aServiceID) {
        return discovery.getAsyncServiceInfo(aServiceID);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServiceTypes()
	 */
    public IFuture getAsyncServiceTypes() {
        return discovery.getAsyncServiceTypes();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServices()
	 */
    public IFuture getAsyncServices() {
        return discovery.getAsyncServices();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IFuture getAsyncServices(IServiceTypeID aServiceTypeID) {
        return discovery.getAsyncServices(aServiceTypeID);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterAllServices()
	 */
    public void unregisterAllServices() {
        discovery.unregisterAllServices();
    }
}
