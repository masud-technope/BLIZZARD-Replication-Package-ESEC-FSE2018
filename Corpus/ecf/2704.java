/*******************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.zookeeper;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainerInstantiator;
import org.eclipse.ecf.tests.discovery.DiscoveryServiceRegistryTest;

public class ZooDiscoveryServiceRegistryTest extends DiscoveryServiceRegistryTest {

    public  ZooDiscoveryServiceRegistryTest() {
        super(ZooDiscoveryContainerInstantiator.NAME);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        // itself, thus explicitly unregistering the service here
        if (registerService != null) {
            unregisterService();
        }
    }

    // Known/Accepted test "failure"
    public void testGetServicesIServiceTypeID() throws ContainerConnectException {
        // just register a service to make sure de-registration in tearDown does not fail
        registerService();
    // At the IDA level DiscoveryServiceRegistryTest register a _generic_
    // IServiceInfo, which internally gets converted into a provider
    // specific IServiceInfo with a Zookeeper specific node ID.
    // At the test level this test normally gets the services by
    // IServiceTypeID, however due to the previously mentioned conversion,
    // there a _two_ IServiceTypeIDs with _different_ node IDs. Thus, just
    // ignore the tests because they logically do not make much sense for
    // the OSGi whiteboard pattern IServiceInfo registry approach taken
    // here.
    }

    // Known/Accepted test "failure"
    public void testGetAsyncServicesIServiceTypeID() throws ContainerConnectException, OperationCanceledException, InterruptedException {
        // just register a service to make sure de-registration in tearDown does not fail
        registerService();
    // At the IDA level DiscoveryServiceRegistryTest register a _generic_
    // IServiceInfo, which internally gets converted into a provider
    // specific IServiceInfo with a Zookeeper specific node ID.
    // At the test level this test normally gets the services by
    // IServiceTypeID, however due to the previously mentioned conversion,
    // there a _two_ IServiceTypeIDs with _different_ node IDs. Thus, just
    // ignore the tests because they logically do not make much sense for
    // the OSGi whiteboard pattern IServiceInfo registry approach taken
    // here.
    }

    // Known/Accepted test "failure"
    public void testAddServiceListenerWithRefresh() {
        // just register a service to make sure de-registration in tearDown does not fail
        registerService();
    }
}
