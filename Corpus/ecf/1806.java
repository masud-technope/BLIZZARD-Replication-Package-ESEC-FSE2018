/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @since 3.0
 */
public class RemoteServiceContainer implements IRemoteServiceContainer {

    private final IContainer container;

    private final IRemoteServiceContainerAdapter containerAdapter;

    public  RemoteServiceContainer(IContainer container, IRemoteServiceContainerAdapter containerAdapter) {
        Assert.isNotNull(container);
        Assert.isNotNull(containerAdapter);
        this.container = container;
        this.containerAdapter = containerAdapter;
    }

    /**
	 * @since 3.2
	 * @param container container
	 */
    @SuppressWarnings("cast")
    public  RemoteServiceContainer(IContainer container) {
        this(container, (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class));
    }

    public IContainer getContainer() {
        return container;
    }

    public IRemoteServiceContainerAdapter getContainerAdapter() {
        return containerAdapter;
    }

    public IRemoteService getRemoteService(String targetLocation, String serviceInterfaceClass, String filter) throws ContainerConnectException, InvalidSyntaxException {
        ID targetID = null;
        if (targetLocation != null) {
            targetID = IDFactory.getDefault().createID(getContainer().getConnectNamespace(), targetLocation);
        }
        IRemoteServiceReference serviceReference = getServiceReference(targetID, serviceInterfaceClass, filter);
        if (serviceReference == null)
            return null;
        return getContainerAdapter().getRemoteService(serviceReference);
    }

    protected IRemoteServiceReference getServiceReference(ID targetId, String serviceInterfaceClass, String filter) throws ContainerConnectException, InvalidSyntaxException {
        IRemoteServiceReference[] references = getContainerAdapter().getRemoteServiceReferences(targetId, serviceInterfaceClass, filter);
        if (references == null || references.length == 0)
            return null;
        return selectReference(references);
    }

    protected IRemoteServiceReference selectReference(IRemoteServiceReference[] references) {
        int length = (references == null) ? 0 : references.length;
        if (length == 0) /* if no service is being tracked */
        {
            return null;
        }
        int index = 0;
        if (length > 1) /* if more than one service, select highest ranking */
        {
            int rankings[] = new int[length];
            int count = 0;
            int maxRanking = Integer.MIN_VALUE;
            for (int i = 0; i < length; i++) {
                Object property = references[i].getProperty(org.eclipse.ecf.remoteservice.Constants.SERVICE_RANKING);
                int ranking = (property instanceof Integer) ? ((Integer) property).intValue() : 0;
                rankings[i] = ranking;
                if (ranking > maxRanking) {
                    index = i;
                    maxRanking = ranking;
                    count = 1;
                } else {
                    if (ranking == maxRanking) {
                        count++;
                    }
                }
            }
            if (count > 1) /* if still more than one service, select lowest id */
            {
                long minId = Long.MAX_VALUE;
                for (int i = 0; i < length; i++) {
                    if (rankings[i] == maxRanking) {
                        long id = ((Long) (references[i].getProperty(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID))).longValue();
                        if (id < minId) {
                            index = i;
                            minId = id;
                        }
                    }
                }
            }
        }
        return references[index];
    }

    public IRemoteService getRemoteService(String targetLocation, String serviceInterfaceClass) throws ContainerConnectException {
        try {
            return getRemoteService(targetLocation, serviceInterfaceClass, null);
        } catch (InvalidSyntaxException e) {
            return null;
        }
    }

    public IRemoteService getRemoteService(String serviceInterfaceClass) {
        try {
            return getRemoteService(null, serviceInterfaceClass);
        } catch (ContainerConnectException e) {
            return null;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("RemoteServiceContainer [containerID=");
        buffer.append(container.getID());
        //$NON-NLS-1$
        buffer.append(", container=");
        buffer.append(container);
        //$NON-NLS-1$
        buffer.append(", containerAdapter=");
        buffer.append(containerAdapter);
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }
}
