/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;

/**
 * @since 3.3
 */
public class EndpointAdapterFactory implements IAdapterFactory {

    private EndpointNodeWorkbenchAdapter endpointNodeAdapter = new EndpointNodeWorkbenchAdapter();

    private EndpointPropertyNodeWorkbenchAdapter endpointPropertyNodeAdapter = new EndpointPropertyNodeWorkbenchAdapter();

    private EndpointPropertyGroupNodeWorkbenchAdapter endpointPropertyGroupNodeAdapter = new EndpointPropertyGroupNodeWorkbenchAdapter();

    private EndpointGroupNodeWorkbenchAdapter endpointGroupNodeAdapter = new EndpointGroupNodeWorkbenchAdapter();

    private EndpointInterfacesNodeWorkbenchAdapter endpointInterfacesNodeAdapter = new EndpointInterfacesNodeWorkbenchAdapter();

    private EndpointAsyncInterfacesNodeWorkbenchAdapter endpointAsyncInterfacesNodeAdapter = new EndpointAsyncInterfacesNodeWorkbenchAdapter();

    private EndpointPackageVersionNodeWorkbenchAdapter endpointPackageVersionNodeAdapter = new EndpointPackageVersionNodeWorkbenchAdapter();

    private EndpointHostGroupNodeWorkbenchAdapter endpointHostGroupNodeAdapter = new EndpointHostGroupNodeWorkbenchAdapter();

    private EndpointDiscoveryGroupNodeWorkbenchAdapter endpointDiscoveryGroupNodeAdapter = new EndpointDiscoveryGroupNodeWorkbenchAdapter();

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (adapterType.isInstance(adaptableObject)) {
            return adaptableObject;
        }
        if (adapterType == IWorkbenchAdapter.class || adapterType == IWorkbenchAdapter2.class || adapterType == IWorkbenchAdapter3.class) {
            return getWorkbenchElement(adaptableObject);
        }
        return null;
    }

    protected Object getWorkbenchElement(Object adaptableObject) {
        if (adaptableObject instanceof EndpointNode)
            return endpointNodeAdapter;
        if (adaptableObject instanceof EndpointAsyncInterfacesNode)
            return endpointAsyncInterfacesNodeAdapter;
        if (adaptableObject instanceof EndpointInterfacesNode)
            return endpointInterfacesNodeAdapter;
        if (adaptableObject instanceof EndpointPackageVersionNode)
            return endpointPackageVersionNodeAdapter;
        if (adaptableObject instanceof EndpointPropertyGroupNode)
            return endpointPropertyGroupNodeAdapter;
        if (adaptableObject instanceof EndpointPropertyNode)
            return endpointPropertyNodeAdapter;
        if (adaptableObject instanceof EndpointHostGroupNode)
            return endpointHostGroupNodeAdapter;
        if (adaptableObject instanceof EndpointDiscoveryGroupNode)
            return endpointDiscoveryGroupNodeAdapter;
        if (adaptableObject instanceof EndpointGroupNode)
            return endpointGroupNodeAdapter;
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IWorkbenchAdapter3.class };
    }
}
