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

import java.io.NotSerializableException;
import java.net.MalformedURLException;
import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.remoteservice.rpc.Activator;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.rpc.RpcException;
import org.eclipse.ecf.remoteservice.rpc.identity.RpcId;

public class RpcClientService extends AbstractClientService {

    private XmlRpcClient client;

    public  RpcClientService(RpcClientContainer container, RemoteServiceClientRegistration registration) throws RpcException {
        super(container, registration);
        client = getXmlRpcClient();
    }

    protected XmlRpcClientConfig getXmlRpcClientConfig() throws RpcException {
        try {
            RpcId id = (RpcId) container.getID();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(id.toURI().toURL());
            config.setEnabledForExtensions(true);
            // Set default timeouts
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);
            return config;
        } catch (MalformedURLException e) {
            handleException(e.getMessage(), e);
            return null;
        }
    }

    protected XmlRpcClient getXmlRpcClient() throws RpcException {
        XmlRpcClient xmlRpcClient = new XmlRpcClient();
        xmlRpcClient.setConfig(getXmlRpcClientConfig());
        xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
        return xmlRpcClient;
    }

    /**
	 * Create a Dynamic Proxy for using XML-RPC servers, which builded on Apache XML-RPC.	   
	 */
    public Object createProxy(ClassLoader cl, Class[] classes) {
        if (classes == null || classes.length < 1)
            return null;
        ClientFactory factory = new ClientFactory(client);
        return factory.newInstance(classes[0]);
    }

    /**
	 * Create a Dynamic Proxy for using XML-RPC servers, which builded on Apache XML-RPC.
	 * See the <a href="http://ws.apache.org/xmlrpc/advanced.html">Dynamic proxies</a> section.    
	 */
    protected Object createProxy(Class[] classes) {
        if (classes == null || classes.length < 1)
            return null;
        ClientFactory factory = new ClientFactory(client);
        return factory.newInstance(classes[0]);
    }

    /**
	 * Calls the XML-RPC Service with given operation of IRemoteCall. The returned value is
	 * the returned value from server
	 * 
	 * @param call The remote call to make.  Must not be <code>null</code>.
	 * @param callable The callable with default parameters to use to make the call.
	 * @return The XML-RPC Service's return value
	 */
    protected Object invokeRemoteCall(final IRemoteCall call, final IRemoteCallable callable) throws ECFException {
        String operation = prepareEndpointAddress(call, callable);
        Object result = null;
        try {
            result = client.execute(operation, toObjectsArray(operation, call, callable));
        } catch (Exception e) {
            handleException("Exception while executing method:" + operation, e);
        }
        return result;
    }

    protected void handleException(String message, Throwable e) throws RpcException {
        logException(message, e);
        throw new RpcException(message, e);
    }

    protected Object[] toObjectsArray(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        IRemoteCallParameter[] rpcParameters = prepareParameters(uri, call, callable);
        Object[] result = new Object[rpcParameters.length];
        for (int i = 0; i < rpcParameters.length; i++) {
            result[i] = rpcParameters[i].getValue();
        }
        return result;
    }

    protected void logException(String string, Throwable e) {
        Activator a = Activator.getDefault();
        if (a != null)
            a.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, string, e));
    }

    protected void logWarning(String string, Throwable e) {
        Activator a = Activator.getDefault();
        if (a != null)
            a.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, string));
    }
}
