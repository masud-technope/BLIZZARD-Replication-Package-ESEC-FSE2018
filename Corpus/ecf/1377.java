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
package org.eclipse.ecf.internal.remoteservices.ui.property;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ui.DiscoveryPropertyTesterUtil;
import org.eclipse.ecf.internal.remoteservices.ui.Activator;
import org.eclipse.ecf.remoteservice.Constants;

@Deprecated
public class ConnectedTester extends PropertyTester {

    public  ConnectedTester() {
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        // consumers expect connected or disconnected
        if (!(expectedValue instanceof Boolean)) {
            return false;
        }
        boolean expected = ((Boolean) expectedValue).booleanValue();
        boolean hasContainer = hasContainer(receiver);
        if (expected && hasContainer) {
            return true;
        } else if (expected && !hasContainer) {
            return false;
        } else if (!expected && hasContainer) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasContainer(Object receiver) {
        // get the container instance
        IServiceInfo serviceInfo = DiscoveryPropertyTesterUtil.getIServiceInfoReceiver(receiver);
        final String connectNamespace = getConnectNamespace(serviceInfo);
        final String connectId = getConnectID(serviceInfo);
        try {
            final ID createConnectId = IDFactory.getDefault().createID(connectNamespace, connectId);
            return (getContainerByConnectID(createConnectId) != null);
        } catch (IDCreateException e) {
            return false;
        }
    }

    /**
	 * @param connectID
	 *            The conected ID for which an IContainer is to be returned
	 * @return a IContainer instance of null
	 */
    // TODO push this functionality down into the ContainerManager
    private IContainer getContainerByConnectID(ID connectID) {
        final IContainerManager containerManager = Activator.getDefault().getContainerManager();
        final IContainer[] containers = containerManager.getAllContainers();
        if (containers == null) {
            return null;
        }
        for (int i = 0; i < containers.length; i++) {
            ID connectedId = containers[i].getConnectedID();
            if (connectedId != null && connectedId.equals(connectID)) {
                return containers[i];
            }
        }
        return null;
    }

    private String getConnectNamespace(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(Constants.SERVICE_CONNECT_ID_NAMESPACE);
    }

    private String getConnectID(IServiceInfo serviceInfo) {
        return serviceInfo.getServiceProperties().getPropertyString(Constants.SERVICE_CONNECT_ID);
    }
}
