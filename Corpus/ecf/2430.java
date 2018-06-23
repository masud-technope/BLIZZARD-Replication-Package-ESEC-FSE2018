/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddException;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.ServerSOContainer;
import org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject;
import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.InvalidSyntaxException;

public class ServletServerContainer extends ServerSOContainer implements IRemoteServiceContainerAdapter {

    private RegistrySharedObject remoteServiceAdapter;

    protected RegistrySharedObject getRegistrySharedObject() {
        return remoteServiceAdapter;
    }

    public  ServletServerContainer(ID id) {
        super(new SOContainerConfig(id));
        remoteServiceAdapter = new RegistrySharedObject();
        try {
            getSharedObjectManager().addSharedObject(IDFactory.getDefault().createStringID(RegistrySharedObject.class.getName()), remoteServiceAdapter, null);
        } catch (SharedObjectAddException e) {
            throw new RuntimeException("Cannot add RegistrySharedObject", e);
        }
    }

    public void addRemoteServiceListener(IRemoteServiceListener listener) {
        getRegistrySharedObject().addRemoteServiceListener(listener);
    }

    public void removeRemoteServiceListener(IRemoteServiceListener listener) {
        getRegistrySharedObject().removeRemoteServiceListener(listener);
    }

    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, @SuppressWarnings("rawtypes") Dictionary properties) {
        return getRegistrySharedObject().registerRemoteService(clazzes, service, properties);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return getRegistrySharedObject().getRemoteServiceReferences(target, clazz, filter);
    }

    @SuppressWarnings("rawtypes")
    public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) {
        return getRegistrySharedObject().asyncGetRemoteServiceReferences(target, clazz, filter);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
        return getRegistrySharedObject().getRemoteServiceReferences(idFilter, clazz, filter);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return getRegistrySharedObject().getRemoteServiceReferences(target, clazz, filter);
    }

    @SuppressWarnings("rawtypes")
    public IFuture asyncGetRemoteServiceReferences(ID[] idFilter, String clazz, String filter) {
        return getRegistrySharedObject().asyncGetRemoteServiceReferences(idFilter, clazz, filter);
    }

    @SuppressWarnings("rawtypes")
    public IFuture asyncGetRemoteServiceReferences(ID target, String clazz, String filter) {
        return getRegistrySharedObject().asyncGetRemoteServiceReferences(target, clazz, filter);
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return getRegistrySharedObject().getAllRemoteServiceReferences(clazz, filter);
    }

    public Namespace getRemoteServiceNamespace() {
        return getRegistrySharedObject().getRemoteServiceNamespace();
    }

    public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID) {
        return getRegistrySharedObject().getRemoteServiceID(containerID, containerRelativeID);
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
        return getRegistrySharedObject().getRemoteServiceReference(serviceID);
    }

    public IRemoteService getRemoteService(IRemoteServiceReference reference) {
        return getRegistrySharedObject().getRemoteService(reference);
    }

    public boolean ungetRemoteService(IRemoteServiceReference reference) {
        return getRegistrySharedObject().ungetRemoteService(reference);
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        return getRegistrySharedObject().createRemoteFilter(filter);
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        getRegistrySharedObject().setConnectContextForAuthentication(connectContext);
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        return getRegistrySharedObject().setRemoteServiceCallPolicy(policy);
    }

    @Override
    public void dispose() {
        if (remoteServiceAdapter != null) {
            getSharedObjectManager().removeSharedObject(remoteServiceAdapter.getID());
            remoteServiceAdapter = null;
        }
        super.dispose();
    }
}
