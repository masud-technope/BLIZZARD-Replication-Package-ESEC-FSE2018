/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rpc.client;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.rpc.identity.RpcId;
import org.eclipse.ecf.remoteservice.rpc.identity.RpcNamespace;

// TODO add working with auth via header params (Cookies, see http://ws.apache.org/xmlrpc/server.html)
public class RpcClientContainer extends AbstractClientContainer implements IRemoteServiceClientContainerAdapter {

    public  RpcClientContainer(RpcId id) {
        super(id);
        setParameterSerializer(new TrivialParameterServializer());
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(RpcNamespace.NAME);
    }

    protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
        IRemoteService service = null;
        try {
            service = new RpcClientService(this, registration);
        } catch (ECFException e) {
            logException(e.getMessage(), e);
        }
        return service;
    }

    protected String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
        // For XML-RPC, endpoint == resource.path
        return callable.getResourcePath();
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        // return false
        return false;
    }
}
