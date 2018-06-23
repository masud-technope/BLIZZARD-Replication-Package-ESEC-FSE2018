/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.provider.local.container;

import java.util.Dictionary;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.provider.local.identity.LocalID;
import org.eclipse.ecf.provider.local.identity.LocalNamespace;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.InvalidSyntaxException;

public class LocalRemoteServiceContainer extends AbstractContainer implements IRemoteServiceContainerAdapter {

    private LocalID localID;

    private LocalRemoteServiceRegistry registry;

    protected  LocalRemoteServiceContainer(LocalID id) throws SharedObjectInitException {
        this.localID = id;
        registry = new LocalRemoteServiceRegistry(this);
    }

    public void addRemoteServiceListener(IRemoteServiceListener listener) {
        registry.addRemoteServiceListener(listener);
    }

    public void removeRemoteServiceListener(IRemoteServiceListener listener) {
        registry.removeRemoteServiceListener(listener);
    }

    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary properties) {
        return registry.registerRemoteService(clazzes, service, properties);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
        return registry.getRemoteServiceReferences(idFilter, clazz, filter);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return registry.getRemoteServiceReferences(target, clazz, filter);
    }

    public IFuture asyncGetRemoteServiceReferences(ID[] idFilter, String clazz, String filter) {
        return registry.asyncGetRemoteServiceReferences(idFilter, clazz, filter);
    }

    public IFuture asyncGetRemoteServiceReferences(ID target, String clazz, String filter) {
        return registry.asyncGetRemoteServiceReferences(target, clazz, filter);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return registry.getRemoteServiceReferences(target, idFilter, clazz, filter);
    }

    public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) {
        return registry.asyncGetRemoteServiceReferences(target, idFilter, clazz, filter);
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return registry.getAllRemoteServiceReferences(clazz, filter);
    }

    public Namespace getRemoteServiceNamespace() {
        return registry.getRemoteServiceNamespace();
    }

    public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID) {
        return registry.getRemoteServiceID(containerID, containerRelativeID);
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
        return registry.getRemoteServiceReference(serviceID);
    }

    public IRemoteService getRemoteService(IRemoteServiceReference reference) {
        return registry.getRemoteService(reference);
    }

    public boolean ungetRemoteService(IRemoteServiceReference reference) {
        return registry.ungetRemoteService(reference);
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        return registry.createRemoteFilter(filter);
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        registry.setConnectContextForAuthentication(connectContext);
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        return registry.setRemoteServiceCallPolicy(policy);
    }

    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
        if (targetID.equals(targetID))
            return;
        //$NON-NLS-1$
        throw new ContainerConnectException("Local container cannot be connected to targetID" + targetID);
    }

    public ID getConnectedID() {
        return getID();
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(LocalNamespace.NAME);
    }

    public void disconnect() {
    // do nothing
    }

    public ID getID() {
        return localID;
    }
}
