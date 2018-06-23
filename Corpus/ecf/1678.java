/*******************************************************************************
* Copyright (c) 2013 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.generic;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.server.generic.SSLGenericServerContainer;
import org.eclipse.ecf.server.generic.SSLSimpleGenericServer;
import org.eclipse.ecf.tests.remoteservice.IConcatService;

public class SSLSimpleConcatServer {

    public static final String PATH = "/sslserver";

    public static final String HOST = "localhost";

    public static final int KEEPALIVE = 30000;

    private IRemoteServiceRegistration registration = null;

    private SSLSimpleGenericServer server;

    public class ConcatService implements IConcatService {

        public String concat(String string1, String string2) {
            System.out.println("server.concat(" + string1 + "," + string2 + ")");
            return string1 + string2;
        }
    }

    public void start(int port) throws Exception {
        // Start server
        server = new SSLSimpleGenericServer(HOST, port);
        server.start(PATH, KEEPALIVE);
        SSLGenericServerContainer serverContainer = server.getServerContainer(0);
        IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) serverContainer.getAdapter(IRemoteServiceContainerAdapter.class);
        Assert.isNotNull(adapter);
        registration = adapter.registerRemoteService(new String[] { IConcatService.class.getName() }, new ConcatService(), null);
        Assert.isNotNull(registration);
        System.out.println("generic server started with id=" + serverContainer.getID());
    }

    public IRemoteServiceRegistration getConcatServiceRegistration() {
        return registration;
    }

    public void stop() {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        server.stop();
        server = null;
    }
}
