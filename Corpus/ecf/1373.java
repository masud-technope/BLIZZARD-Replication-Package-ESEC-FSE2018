/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice.internal.provider.rest.consumer;

import java.io.NotSerializableException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteResponseDeserializer;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.rest.RestCallableFactory;
import org.eclipse.ecf.remoteservice.rest.client.HttpGetRequestType;
import org.eclipse.ecf.remoteservice.rest.client.RestClientContainer;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.json.JSONException;
import org.json.JSONObject;
import com.mycorp.examples.timeservice.ITimeService;

public class TimeServiceRestClientContainer extends RestClientContainer {

    public static final String TIMESERVICE_CONSUMER_CONFIG_NAME = "com.mycorp.examples.timeservice.rest.consumer";

    private static final String TIMESERVICE_HOST_CONFIG_NAME = "com.mycorp.examples.timeservice.rest.host";

    private IRemoteServiceRegistration reg;

     TimeServiceRestClientContainer(RestID id) {
        super(id);
        // This sets up the JSON deserialization of the server's response.
        // See below for implementation of TimeServiceRestResponseDeserializer
        setResponseDeserializer(new IRemoteResponseDeserializer() {

            public Object deserializeResponse(String endpoint, IRemoteCall call, IRemoteCallable callable, @SuppressWarnings("rawtypes") Map responseHeaders, byte[] responseBody) throws NotSerializableException {
                try {
                    return new JSONObject(new String(responseBody)).get("time");
                } catch (JSONException e1) {
                    NotSerializableException t = new NotSerializableException("Exception serializing response from endpoing=" + endpoint);
                    t.setStackTrace(e1.getStackTrace());
                    throw t;
                }
            }
        });
    }

    @Override
    public void connect(ID targetID, IConnectContext connectContext1) throws ContainerConnectException {
        super.connect(targetID, connectContext1);
        // Create the IRemoteCallable to represent
        // access to the ITimeService method.
        IRemoteCallable callable = RestCallableFactory.createCallable("getCurrentTime", ITimeService.class.getName(), null, new HttpGetRequestType(), 30000);
        // Register the callable and associate it with the ITimeService class
        // name
        reg = registerCallables(new String[] { ITimeService.class.getName() }, new IRemoteCallable[][] { { callable } }, null);
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if (reg != null) {
            reg.unregister();
            reg = null;
        }
    }

    @Override
    public Namespace getConnectNamespace() {
        return RestNamespace.INSTANCE;
    }

    public static class Instantiator extends RemoteServiceContainerInstantiator {

        @Override
        public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters) throws ContainerCreateException {
            // Create new container instance with random uuid
            return new TimeServiceRestClientContainer((RestID) RestNamespace.INSTANCE.createInstance(new Object[] { "uuid:" + UUID.randomUUID().toString() }));
        }

        public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
            if (Arrays.asList(exporterSupportedConfigs).contains(TIMESERVICE_HOST_CONFIG_NAME))
                return new String[] { TimeServiceRestClientContainer.TIMESERVICE_CONSUMER_CONFIG_NAME };
            else
                return null;
        }
    }
}
