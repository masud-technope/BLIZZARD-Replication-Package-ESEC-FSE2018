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
package org.eclipse.ecf.internal.remoteservices.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ui.DiscoveryHandlerUtil;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.InvalidSyntaxException;

public class RemoteServiceHandlerUtil {

    public static IRemoteServiceContainerAdapter getActiveIRemoteServiceContainerAdapterChecked(ExecutionEvent event) throws ExecutionException {
        final ID activeConnectId = getActiveConnectIDChecked(event);
        final IContainer container = getContainerWithConnectID(activeConnectId);
        if (container == null) {
            return null;
        }
        final IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
        return adapter;
    }

    public static IRemoteServiceReference[] getActiveIRemoteServiceReferencesChecked(ExecutionEvent event) throws ExecutionException {
        final IServiceInfo serviceInfo = DiscoveryHandlerUtil.getActiveIServiceInfoChecked(event);
        final IRemoteServiceContainerAdapter adapter = getActiveIRemoteServiceContainerAdapterChecked(event);
        try {
            return getRemoteServiceReferencesForRemoteServiceAdapter(adapter, serviceInfo);
        } catch (IDCreateException e) {
            throw new ExecutionException(e.getMessage(), e);
        } catch (InvalidSyntaxException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    public static ID getActiveConnectIDChecked(ExecutionEvent event) throws ExecutionException {
        final IServiceInfo serviceInfo = DiscoveryHandlerUtil.getActiveIServiceInfoChecked(event);
        final String connectNamespace = getConnectNamespace(serviceInfo);
        final String connectId = getConnectID(serviceInfo);
        try {
            return IDFactory.getDefault().createID(connectNamespace, connectId);
        } catch (IDCreateException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    public static IContainer getActiveIRemoteServiceContainerChecked(ExecutionEvent event) throws ExecutionException {
        final IServiceInfo serviceInfo = DiscoveryHandlerUtil.getActiveIServiceInfoChecked(event);
        final ID createConnectId = getActiveConnectIDChecked(event);
        final IContainer container = getContainerWithConnectID(createConnectId);
        if (container != null) {
            return container;
        }
        // TODO remove parameters once
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=256586 is fixed
        final Object[] parameters = new Object[] { createConnectId };
        try {
            // new one
            return ContainerFactory.getDefault().createContainer(getContainerFactory(serviceInfo), parameters);
        } catch (ContainerCreateException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    // TODO push this functionality down into the ContainerManager
    private static IContainer getContainerWithConnectID(ID aConnectedID) {
        final IContainerManager containerManager = Activator.getDefault().getContainerManager();
        final IContainer[] containers = containerManager.getAllContainers();
        if (containers == null) {
            return null;
        }
        for (int i = 0; i < containers.length; i++) {
            ID connectedId = containers[i].getConnectedID();
            if (connectedId != null && connectedId.equals(aConnectedID)) {
                return containers[i];
            }
        }
        return null;
    }

    private static IRemoteServiceReference[] getRemoteServiceReferencesForRemoteServiceAdapter(IRemoteServiceContainerAdapter adapter, IServiceInfo serviceInfo) throws InvalidSyntaxException, IDCreateException {
        ID serviceID = null;
        final String serviceNamespace = getServiceNamespace(serviceInfo);
        final String serviceid = getServiceID(serviceInfo);
        if (serviceNamespace != null && serviceid != null) {
            serviceID = IDFactory.getDefault().createID(serviceNamespace, serviceid);
        }
        final ID[] targets = (serviceID == null) ? null : new ID[] { serviceID };
        return adapter.getRemoteServiceReferences(targets, getRemoteServiceClass(serviceInfo), null);
    }

    private static String getServiceNamespace(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE);
    }

    private static String getServiceID(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(RemoteConstants.ENDPOINT_ID);
    }

    private static String getRemoteServiceClass(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(org.osgi.framework.Constants.OBJECTCLASS);
    }

    private static String getConnectNamespace(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE);
    }

    private static String getConnectID(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(RemoteConstants.ENDPOINT_CONNECTTARGET_ID);
    }

    // XXX FIX...this has to use proper container factory type.
    private static String getContainerFactory(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS);
    }
}
