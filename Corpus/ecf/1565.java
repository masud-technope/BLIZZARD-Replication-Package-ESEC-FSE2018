/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.provider.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.discovery.CompositeDiscoveryContainer;
import org.eclipse.ecf.provider.discovery.CompositeServiceContainerEvent;
import org.eclipse.ecf.tests.discovery.DiscoveryContainerTest;
import org.eclipse.ecf.tests.discovery.listener.TestServiceListener;

public class CompositeDiscoveryContainerTest extends DiscoveryContainerTest {

    private TestDiscoveryContainer testDiscoveryContainer;

    public  CompositeDiscoveryContainerTest() {
        super(CompositeDiscoveryContainer.NAME);
        setComparator(new CompositeServiceInfoComporator());
        //TODO  jSLP currently has the longer rediscovery interval
        //$NON-NLS-1$);
        setWaitTimeForProvider(Long.parseLong(System.getProperty("net.slp.rediscover", new Long(60L * 1000L).toString())));
        //TODO-mkuppe https://bugs.eclipse.org/bugs/show_bug.cgi?id=218308
        setScope(IServiceTypeID.DEFAULT_SCOPE[0]);
        setHostname(System.getProperty("net.mdns.interface", "127.0.0.1"));
    }
    //	/* (non-Javadoc)
    //	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#setUp()
    //	 */
    //	protected void setUp() throws Exception {
    //		super.setUp();
    //		eventsToExpect = ((CompositeDiscoveryContainer) discoveryLocator).getDiscoveryContainers().size();
    //	}
    //
    //	/**
    //	 * Check if 
    //	 * @throws ContainerConnectException 
    //	 */
    //	public void testAddContainerWithRegisteredServices() throws ContainerConnectException {
    //		try {
    //			try {
    //				discoveryAdvertiser.registerService(serviceInfo);
    //			} catch (ECFRuntimeException e) {
    //				fail("Registering a service failed on a new IDCA");
    //			}
    //			CompositeDiscoveryContainer cdc = (CompositeDiscoveryContainer) discoveryLocator;
    //			testDiscoveryContainer = new TestDiscoveryContainer();
    //			assertTrue(cdc.addContainer(testDiscoveryContainer));
    //			List registeredServices = testDiscoveryContainer.getRegisteredServices();
    //			assertEquals("registerService(aService) wasn't called on TestDiscoveryContainer", serviceInfo, registeredServices.get(0));
    //		} finally {
    //			CompositeDiscoveryContainer cdc = (CompositeDiscoveryContainer) discoveryLocator;
    //			cdc.removeContainer(testDiscoveryContainer);
    //		}
    //	}
    //
    //	public void testAddContainerWithoutRegisteredServices() throws ContainerConnectException {
    //		try {
    //			try {
    //				discoveryAdvertiser.registerService(serviceInfo);
    //				discoveryAdvertiser.unregisterService(serviceInfo);
    //			} catch (ECFRuntimeException e) {
    //				fail("Re-/Unregistering a service failed on a new IDCA");
    //			}
    //			CompositeDiscoveryContainer cdc = (CompositeDiscoveryContainer) discoveryLocator;
    //			testDiscoveryContainer = new TestDiscoveryContainer();
    //			assertTrue(cdc.addContainer(testDiscoveryContainer));
    //			List registeredServices = testDiscoveryContainer.getRegisteredServices();
    //			assertTrue(registeredServices.isEmpty());
    //		} finally {
    //			CompositeDiscoveryContainer cdc = (CompositeDiscoveryContainer) discoveryLocator;
    //			cdc.removeContainer(testDiscoveryContainer);
    //		}
    //	}
    //
    //	protected void addServiceListener(TestServiceListener serviceListener) {
    //		discoveryLocator.addServiceListener(serviceListener);
    //		addListenerRegisterAndWait(serviceListener, serviceInfo);
    //		discoveryLocator.removeServiceListener(serviceListener);
    //
    //		// make sure we use a live container;
    //		final IContainer ic = (IContainer) serviceListener.getLocator();
    //		assertTrue(ic.getConnectedID() != null);
    //		
    //		// check if we received correct amount of events
    //		final IContainerEvent[] events = serviceListener.getEvent();
    //		assertNotNull("Test listener didn't receive any discovery events.", events);
    //		assertEquals("Test listener received unexpected amount of discovery events: \n\t" + Arrays.asList(events), eventsToExpect, events.length);
    //		
    //		final List origContainers = new ArrayList();
    //		for (int i = 0; i < events.length; i++) {
    //			final CompositeServiceContainerEvent event = (CompositeServiceContainerEvent) events[i];
    //
    //			// check if the local container is hidden correctly
    //			final ID localContainerId = event.getLocalContainerID();
    //			final ID connectedId = container.getConnectedID();
    //			assertEquals(localContainerId, connectedId);
    //			
    //			// check the IServiceInfo for correct fields/properties
    //			final IServiceInfo serviceInfo2 = ((IServiceEvent) event).getServiceInfo();
    //			assertTrue("IServiceInfo should match, expected:\n\t" + serviceInfo + " but was \n\t" + serviceInfo2, comparator.compare(serviceInfo2, serviceInfo) == 0);
    //			
    //			// add the underlying discovery container the the result set
    //			origContainers.add(event.getOriginalLocalContainerID());
    //		}
    //		// check that all underlying containers fired an event
    //		assertEquals("A nested container didn't send an event, but another multiple.", eventsToExpect, origContainers.size());
    //	}
}
