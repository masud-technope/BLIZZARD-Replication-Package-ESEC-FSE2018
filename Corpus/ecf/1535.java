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

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteServiceClientContainerAdapter;
import org.eclipse.ecf.remoteservice.client.RemoteCallableFactory;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.osgi.framework.InvalidSyntaxException;

public class RpcRemoteServiceAdapterTest extends AbstractRpcTestCase {

    IContainer container;

    protected void setUp() throws Exception {
        super.setUp();
        container = createRpcContainer(RpcConstants.TEST_ECHO_TARGET);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        container.disconnect();
        container.dispose();
        getContainerManager().removeAllContainers();
    }

    IRemoteServiceClientContainerAdapter getRemoteServiceClientContainerAdapter() {
        return super.getRemoteServiceClientContainerAdapter(container);
    }

    IRemoteServiceRegistration createRpcRegistration(String method) {
        IRemoteCallable callable = RemoteCallableFactory.createCallable(method);
        return registerCallable(container, callable, null);
    }

    public void testGetRemoteService() {
        IRemoteServiceRegistration registration = createRpcRegistration(RpcConstants.TEST_ECHO_METHOD);
        IRemoteServiceReference reference = registration.getReference();
        assertNotNull(reference);
        IRemoteService remoteService = getRemoteServiceClientContainerAdapter().getRemoteService(reference);
        assertNotNull(remoteService);
    }

    public void testGetRemoteServiceReference() {
        IRemoteServiceRegistration registration = createRpcRegistration(RpcConstants.TEST_ECHO_METHOD);
        IRemoteServiceReference remoteServiceReference = getRemoteServiceClientContainerAdapter().getRemoteServiceReference(registration.getID());
        assertEquals(registration.getReference(), remoteServiceReference);
    }

    public void testUngetRemoteService() {
        IRemoteServiceRegistration registration = createRpcRegistration(RpcConstants.TEST_ECHO_METHOD);
        IRemoteServiceReference reference = registration.getReference();
        getRemoteServiceClientContainerAdapter().getRemoteService(reference);
        assertTrue(getRemoteServiceClientContainerAdapter().ungetRemoteService(reference));
    }

    public void testRemoteServiceRegisteredEvent() {
        IRemoteServiceClientContainerAdapter adapter = getRemoteServiceClientContainerAdapter();
        adapter.addRemoteServiceListener(new IRemoteServiceListener() {

            public void handleServiceEvent(IRemoteServiceEvent event) {
                assertTrue(event instanceof IRemoteServiceRegisteredEvent);
            }
        });
        createRpcRegistration(RpcConstants.TEST_ECHO_METHOD);
    }

    public void testCreateRemoteFilter() {
        String filter = "(" + Constants.OBJECTCLASS + "=" + IRemoteService.class.getName() + ")";
        try {
            IRemoteFilter remoteFilter = getRemoteServiceClientContainerAdapter().createRemoteFilter(filter);
            assertNotNull(remoteFilter);
        } catch (InvalidSyntaxException e) {
            fail();
        }
    }

    public void testGetRemoteServiceID() {
        IRemoteServiceRegistration registration = createRpcRegistration(RpcConstants.TEST_ECHO_METHOD);
        long containerRelativeID = registration.getID().getContainerRelativeID();
        IRemoteServiceID remoteServiceID = getRemoteServiceClientContainerAdapter().getRemoteServiceID(container.getID(), containerRelativeID);
        assertEquals(registration.getID(), remoteServiceID);
    }
}
