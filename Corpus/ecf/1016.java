/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.discovery;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.IServiceTypeListener;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.equinox.concurrent.future.IFuture;

public class TestDiscoveryContainer implements IDiscoveryLocator, IDiscoveryAdvertiser, IContainer {

    private List services = new ArrayList();

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void addServiceListener(IServiceListener listener) {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void addServiceListener(IServiceTypeID type, IServiceListener listener) {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
    public void addServiceTypeListener(IServiceTypeListener listener) {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IServiceInfo getServiceInfo(IServiceID service) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceTypes()
	 */
    public IServiceTypeID[] getServiceTypes() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices()
	 */
    public IServiceInfo[] getServices() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IServiceInfo[] getServices(IServiceTypeID type) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServicesNamespace()
	 */
    public Namespace getServicesNamespace() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void registerService(IServiceInfo serviceInfo) {
        this.services.add(serviceInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void removeServiceListener(IServiceListener listener) {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)
	 */
    public void removeServiceListener(IServiceTypeID type, IServiceListener listener) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
    public void removeServiceTypeListener(IServiceTypeListener listener) {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)
	 */
    public void unregisterService(IServiceInfo serviceInfo) {
        this.services.remove(serviceInfo);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        throw new UnsupportedOperationException();
    }

    public List getRegisteredServices() {
        return services;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#purgeCache()
	 */
    public IServiceInfo[] purgeCache() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 */
    public IFuture getAsyncServiceInfo(IServiceID aServiceId) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServiceTypes()
	 */
    public IFuture getAsyncServiceTypes() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServices()
	 */
    public IFuture getAsyncServices() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAsyncLocator#getAsyncServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 */
    public IFuture getAsyncServices(IServiceTypeID aServiceTypeId) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterAllServices()
	 */
    public void unregisterAllServices() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#addListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void addListener(IContainerListener listener) {
        throw new UnsupportedOperationException();
    }

    public void connect(ID targetId, IConnectContext connectContext) throws ContainerConnectException {
    // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public void disconnect() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#dispose()
	 */
    public void dispose() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
    public Namespace getConnectNamespace() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return IDFactory.getDefault().createStringID(getClass().getName());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainer#removeListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void removeListener(IContainerListener listener) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        throw new UnsupportedOperationException();
    }
}
