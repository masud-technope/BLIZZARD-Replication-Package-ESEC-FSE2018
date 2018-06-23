/****************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.provider.remoteservice.container;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.examples.provider.trivial.identity.TrivialNamespace;
import org.eclipse.ecf.remoteservice.IOSGiRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.util.RemoteFilterImpl;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Trivial container implementation. Note that container adapter implementations can be
 * provided by the container class to expose appropriate adapters.
 */
@SuppressWarnings("restriction")
public class RSExampleContainer extends AbstractContainer implements IRemoteServiceContainerAdapter, IOSGiRemoteServiceContainerAdapter {

    /*
	 * The targetID.  This value is set on 'connect' and unset in 'disconnect'.
	 * This represents the other process that this container is connected to.
	 * Value is returned via getConnectedID()
	 */
    private ID targetID = null;

    /*
	 * This is the ID for this container.  Returned via getID().
	 */
    private ID containerID = null;

    public  RSExampleContainer() throws IDCreateException {
        super();
        this.containerID = IDFactory.getDefault().createGUID();
    }

    public  RSExampleContainer(ID id) {
        super();
        Assert.isNotNull(id);
        this.containerID = id;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
        if (!targetID.getNamespace().getName().equals(getConnectNamespace().getName()))
            throw new ContainerConnectException("targetID not of appropriate Namespace");
        fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));
        // XXX connect to remote service here
        this.targetID = targetID;
        fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public void disconnect() {
        fireContainerEvent(new ContainerDisconnectingEvent(getID(), targetID));
        final ID oldID = targetID;
        // XXX disconnect here
        fireContainerEvent(new ContainerDisconnectedEvent(getID(), oldID));
    }

    public void dispose() {
        disconnect();
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.clear();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(TrivialNamespace.NAME);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public ID getConnectedID() {
        return targetID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return containerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.AbstractContainer#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(@SuppressWarnings("rawtypes") Class serviceType) {
        /*
		 * See AbstractContainer.getAdapter() implementation.
		 */
        return super.getAdapter(serviceType);
    }

    // IOSGiRemoteServiceContainerAdapter impl.  This supercedes the implementation of 
    // IRemoteServiceContainerAdapter.registerRemoteService
    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, @SuppressWarnings("rawtypes") ServiceReference aServiceReference, @SuppressWarnings("rawtypes") Dictionary properties) {
        // TODO Auto-generated method stub
        return null;
    }

    // IRemoteServiceContainerAdapter impl
    private List<IRemoteServiceListener> remoteServiceListeners = new ArrayList<IRemoteServiceListener>();

    void fireRemoteServiceEvent(IRemoteServiceEvent event) {
        List<IRemoteServiceListener> toNotify = null;
        // Copy array
        synchronized (remoteServiceListeners) {
            toNotify = new ArrayList<IRemoteServiceListener>(remoteServiceListeners);
        }
        for (IRemoteServiceListener i : toNotify) {
            i.handleServiceEvent(event);
        }
    }

    public void addRemoteServiceListener(IRemoteServiceListener listener) {
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.add(listener);
        }
    }

    public void removeRemoteServiceListener(IRemoteServiceListener listener) {
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.remove(listener);
        }
    }

    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, @SuppressWarnings("rawtypes") Dictionary properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        // TODO Auto-generated method stub
        return null;
    }

    public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        // TODO Auto-generated method stub
        return null;
    }

    public IFuture asyncGetRemoteServiceReferences(ID[] idFilter, String clazz, String filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public IFuture asyncGetRemoteServiceReferences(ID target, String clazz, String filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public Namespace getRemoteServiceNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteService getRemoteService(IRemoteServiceReference reference) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean ungetRemoteService(IRemoteServiceReference reference) {
        // TODO Auto-generated method stub
        return false;
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        return new RemoteFilterImpl(filter);
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
    // TODO Auto-generated method stub
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        // TODO Auto-generated method stub
        return false;
    }
}
