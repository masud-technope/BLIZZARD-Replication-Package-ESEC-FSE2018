/****************************************************************************
 * Copyright (c) 2010 Eugen Reiswich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eugen Reiswich - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.xmpp.remoteservice;

import java.net.URISyntaxException;
import junit.framework.TestCase;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.tests.provider.xmpp.XMPPS;
import org.osgi.framework.InvalidSyntaxException;

public class RemoteServiceRetrievalTest extends TestCase {

    private XMPPClient[] xmppClients;

    private static final int SLEEPTIME = new Integer(System.getProperty("org.eclipse.ecf.tests.provider.xmpp.remoteservice.RemoteServiceRetrieval.SLEEPTIME", "6000")).intValue();

    public void setUp() {
        try {
            createXMPPClients();
        } catch (ContainerConnectException e) {
            e.printStackTrace();
            fail();
        } catch (ContainerCreateException e) {
            e.printStackTrace();
            fail();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        registerRemoteServicesNoFilterIDs();
    }

    private XMPPClient getClient(int clientNr) {
        assertTrue(0 <= clientNr);
        assertTrue(clientNr <= 2);
        return xmppClients[clientNr];
    }

    /*
	 * Make sure that the right service is returned for the right client.
	 */
    public void testRightServiceForClients() throws Exception {
        // Client 1 tries to retrieve services registered by client 2 & 3
        IExampleService remoteService1 = getClient(0).getRemoteService(getClient(1));
        assertNotNull(remoteService1);
        assertEquals(getClient(1).getClientID(), remoteService1.getClientID());
        IExampleService remoteService2 = getClient(0).getRemoteService(getClient(2));
        assertNotNull(remoteService2);
        assertEquals(getClient(2).getClientID(), remoteService2.getClientID());
        // Client 2 tries to retrieve services registered by client 1 & 3
        IExampleService remoteService3 = getClient(1).getRemoteService(getClient(0));
        assertNotNull(remoteService3);
        assertEquals(getClient(0).getClientID(), remoteService3.getClientID());
        IExampleService remoteService4 = getClient(1).getRemoteService(getClient(2));
        assertNotNull(remoteService4);
        assertEquals(getClient(2).getClientID(), remoteService4.getClientID());
        // Client 3 tries to retrieve services registered by client 1 & 2
        IExampleService remoteService5 = getClient(2).getRemoteService(getClient(0));
        assertNotNull(remoteService5);
        assertEquals(getClient(0).getClientID(), remoteService5.getClientID());
        IExampleService remoteService6 = getClient(2).getRemoteService(getClient(1));
        assertNotNull(remoteService6);
        assertEquals(getClient(1).getClientID(), remoteService6.getClientID());
    }

    public void testRegisterAndUnregisterRemoteServices() throws Exception {
        // Client 0 tries to get service from client 1
        IExampleService remoteService1 = getClient(0).getRemoteService(getClient(1));
        assertNotNull(remoteService1);
        // unregister remote service on client 1, no services available for
        // client 1
        getClient(1).unregisterRemoteService();
        // wait for unregistration to propogate
        Thread.sleep(SLEEPTIME);
        // Now lookup and make sure the reference is now null
        IExampleService remoteService2 = getClient(0).getRemoteService(getClient(1));
        assertNull(remoteService2);
        // register remote service on client 1
        registerRemoteServiceOnClient(getClient(1));
        // wait for registration to propogate
        Thread.sleep(SLEEPTIME);
        IExampleService remoteService3 = getClient(0).getRemoteService(getClient(1));
        assertNotNull(remoteService3);
    }

    /*
	 * Remote service registration without filterIDs.
	 */
    private void registerRemoteServicesNoFilterIDs() {
        registerRemoteServiceOnClient(getClient(0));
        registerRemoteServiceOnClient(getClient(1));
        registerRemoteServiceOnClient(getClient(2));
    }

    private void registerRemoteServiceOnClient(XMPPClient client) {
        client.registerRemoteService(IExampleService.class.getName(), new ExampleService(client.getClientID()));
    }

    public void tearDown() {
        for (int clientNumber = 0; clientNumber <= 2; clientNumber++) {
            getClient(clientNumber).tearDown();
        }
    }

    protected void createXMPPClients() throws URISyntaxException, ContainerConnectException, ContainerCreateException {
        xmppClients = new XMPPClient[3];
        for (int clientNr = 0; clientNr <= 2; clientNr++) {
            // usernames already contain server address e.g.
            // ecf-test1@ecf-project.org
            String username = System.getProperty("username" + clientNr);
            String password = System.getProperty("password" + clientNr);
            assertNotNull(username);
            assertNotNull(password);
            XMPPClient xmppClient = new XMPPClient(username, password);
            xmppClient.connect();
            xmppClients[clientNr] = xmppClient;
        }
        assertEquals(3, xmppClients.length);
    }

    /*
	 * Convenience class for Client data
	 */
    private class XMPPClient {

        private IContainer container;

        private XMPPID clientID;

        private IConnectContext connectContext;

        private IRemoteServiceContainerAdapter adapter;

        private IRemoteServiceRegistration registration;

        public  XMPPClient(String username, String password) throws ContainerCreateException, URISyntaxException {
            container = ContainerFactory.getDefault().createContainer(XMPPS.CONTAINER_NAME);
            assertNotNull(container);
            adapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
            assertNotNull(adapter);
            clientID = new XMPPID(container.getConnectNamespace(), username);
            assertNotNull(clientID);
            connectContext = ConnectContextFactory.createUsernamePasswordConnectContext(username, password);
            assertNotNull(connectContext);
        }

        void tearDown() {
            unregisterRemoteService();
            try {
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            container.disconnect();
        }

        XMPPID getClientID() {
            return clientID;
        }

        void connect() throws ContainerConnectException {
            container.connect(clientID, connectContext);
        }

        public void registerRemoteService(String svcInterface, Object svc) {
            this.registration = adapter.registerRemoteService(new String[] { svcInterface }, svc, null);
        }

        void unregisterRemoteService() {
            if (registration != null) {
                // unregister the remote service registration
                registration.unregister();
            }
        }

        private IExampleService getRemoteService(XMPPClient toClient) throws InvalidSyntaxException, ECFException {
            IRemoteServiceReference[] remoteServiceReferences = adapter.getRemoteServiceReferences(new ID[] { toClient.getClientID() }, IExampleService.class.getName(), null);
            if (remoteServiceReferences == null || remoteServiceReferences.length == 0)
                return null;
            assertEquals(1, remoteServiceReferences.length);
            IRemoteService remoteService = adapter.getRemoteService(remoteServiceReferences[0]);
            IExampleService remoteServiceUser = (IExampleService) remoteService.getProxy();
            return remoteServiceUser;
        }
    }
}
