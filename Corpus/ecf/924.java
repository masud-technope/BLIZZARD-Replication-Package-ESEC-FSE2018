/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rpc;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteServiceClientContainerAdapter;
import org.eclipse.ecf.tests.ECFAbstractTestCase;

public abstract class AbstractRpcTestCase extends ECFAbstractTestCase {

    protected IContainer createRpcContainer(String uri) throws ContainerCreateException {
        return getContainerFactory().createContainer(RpcConstants.RPC_CONTAINER_TYPE, uri);
    }

    protected IContainer createRpcContainer(ID rpcId) throws ContainerCreateException {
        return getContainerFactory().createContainer(RpcConstants.RPC_CONTAINER_TYPE, rpcId);
    }

    protected ID createRpcID(String id) throws IDCreateException {
        return getIDFactory().createID(RpcConstants.NAMESPACE, id);
    }

    protected IRemoteServiceClientContainerAdapter getRemoteServiceClientContainerAdapter(IContainer container) {
        return (IRemoteServiceClientContainerAdapter) container.getAdapter(IRemoteServiceClientContainerAdapter.class);
    }

    protected IRemoteServiceRegistration registerCallable(IContainer container, IRemoteCallable callable, Dictionary<String, Object> properties) {
        return getRemoteServiceClientContainerAdapter(container).registerCallables(new IRemoteCallable[] { callable }, properties);
    }

    protected IRemoteServiceRegistration registerCallable(IContainer container, IRemoteCallable[] callables, Dictionary<String, Object> properties) {
        return getRemoteServiceClientContainerAdapter(container).registerCallables(callables, properties);
    }
}
