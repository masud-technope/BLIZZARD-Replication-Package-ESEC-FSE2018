/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - Reworked completely
 *****************************************************************************/
package org.eclipse.ecf.tests.discovery;

import java.util.Arrays;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.tests.discovery.listener.TestServiceListener;
import org.eclipse.ecf.tests.discovery.listener.TestServiceTypeListener;
import org.eclipse.equinox.concurrent.future.IFuture;

public abstract class DiscoveryTest extends AbstractDiscoveryTest {

    protected long waitTimeForProvider = 1000;

    protected int eventsToExpect = 1;

    public  DiscoveryTest(String name) {
        super(name);
    }

    protected void setWaitTimeForProvider(long aWaitTimeForProvider) {
        this.waitTimeForProvider = aWaitTimeForProvider + (aWaitTimeForProvider * 1 / 2);
    }

    protected void registerService(IServiceInfo serviceInfo) throws Exception {
        assertNotNull(serviceInfo);
        assertNotNull(discoveryAdvertiser);
        discoveryAdvertiser.registerService(serviceInfo);
    }

    protected void unregisterService(IServiceInfo serviceInfo) throws Exception {
        assertNotNull(serviceInfo);
        assertNotNull(discoveryAdvertiser);
        discoveryAdvertiser.unregisterService(serviceInfo);
    }

    /*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        discoveryAdvertiser.unregisterService(serviceInfo);
        discoveryLocator.purgeCache();
        super.tearDown();
    }

    protected void registerService() {
        try {
            discoveryAdvertiser.registerService(serviceInfo);
        } catch (final ECFRuntimeException e) {
            fail("IServiceInfo may be valid with this IDCA " + e.getMessage());
        }
    }

    protected void unregisterService() {
        try {
            discoveryAdvertiser.unregisterService(serviceInfo);
        } catch (final ECFRuntimeException e) {
            fail("unregistering of " + serviceInfo + " should just work");
        }
    }

    protected void addListenerRegisterAndWait(TestServiceListener testServiceListener, IServiceInfo aServiceInfo) {
        synchronized (testServiceListener) {
            // register a service which we expect the test listener to get notified of
            registerService();
            try {
                testServiceListener.wait(waitTimeForProvider);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Some discovery unrelated threading issues?");
            }
        }
    }

    protected void addServiceListener(TestServiceListener serviceListener) {
        discoveryLocator.addServiceListener(serviceListener);
        addListenerRegisterAndWait(serviceListener, serviceInfo);
        discoveryLocator.removeServiceListener(serviceListener);
        IContainerEvent[] event = serviceListener.getEvent();
        assertNotNull("Test listener didn't receive any discovery event", event);
        assertEquals("Test listener received unexpected amount of discovery events: \n\t" + Arrays.asList(event), eventsToExpect, event.length);
        IServiceInfo serviceInfo2 = ((IServiceEvent) event[eventsToExpect - 1]).getServiceInfo();
        assertTrue("IServiceInfo should match, expected:\n\t" + serviceInfo + " but was \n\t" + serviceInfo2, comparator.compare(serviceInfo2, serviceInfo) == 0);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)}.
	 * @throws ContainerConnectException 
	 */
    public void testGetServiceInfo() throws ContainerConnectException {
        registerService();
        final IServiceInfo info = discoveryLocator.getServiceInfo(serviceInfo.getServiceID());
        assertTrue("IServiceInfo should match, expected:\n\t" + serviceInfo + " but:\n\t" + info, comparator.compare(info, serviceInfo) == 0);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceTypes()}.
	 * @throws ContainerConnectException 
	 */
    public void testGetServiceTypes() throws ContainerConnectException {
        registerService();
        final IServiceTypeID[] serviceTypeIDs = discoveryLocator.getServiceTypes();
        assertTrue("IServiceInfo[] is empty", serviceTypeIDs.length >= 1);
        for (int i = 0; i < serviceTypeIDs.length; i++) {
            IServiceTypeID iServiceTypeId = serviceTypeIDs[i];
            if (serviceInfo.getServiceID().getServiceTypeID().equals(iServiceTypeId)) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices()}.
	 * @throws ContainerConnectException 
	 */
    public void testGetServices() throws ContainerConnectException {
        registerService();
        final IServiceInfo[] services = discoveryLocator.getServices();
        assertTrue("IServiceInfo[] is empty", services.length >= 1);
        for (int i = 0; i < services.length; i++) {
            IServiceInfo iServiceInfo = services[i];
            if (comparator.compare(iServiceInfo, serviceInfo) == 0) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)}.
	 * @throws ContainerConnectException 
	 */
    public void testGetServicesIServiceTypeID() throws ContainerConnectException {
        registerService();
        final IServiceInfo serviceInfos[] = discoveryLocator.getServices(serviceInfo.getServiceID().getServiceTypeID());
        assertTrue("IServiceInfo[] is empty", serviceInfos.length >= 1);
        for (int i = 0; i < serviceInfos.length; i++) {
            IServiceInfo iServiceInfo = serviceInfos[i];
            if (comparator.compare(iServiceInfo, serviceInfo) == 0) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 * @throws ContainerConnectException 
	 */
    public void testRegisterService() throws ContainerConnectException {
        registerService();
        final IServiceInfo[] services = discoveryLocator.getServices();
        assertTrue("IServiceInfo[] is empty", services.length >= 1);
        for (int i = 0; i < services.length; i++) {
            final IServiceInfo service = services[i];
            if (comparator.compare(service, serviceInfo) == 0) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 * @throws ContainerConnectException 
	 */
    public void testUnregisterService() throws ContainerConnectException {
        testRegisterService();
        unregisterService();
        final IServiceInfo[] services = discoveryLocator.getServices();
        for (int i = 0; i < services.length; i++) {
            final IServiceInfo service = services[i];
            if (comparator.compare(service, serviceInfo) == 0) {
                fail("Expected service to be not registered anymore");
            }
        }
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testAddServiceListenerIServiceListener() throws ContainerConnectException {
        IServiceInfo[] services = discoveryLocator.getServices();
        assertTrue("No Services must be registerd at this point " + (services.length == 0 ? "" : services[0].toString()), services.length == 0);
        final TestServiceListener tsl = new TestServiceListener(eventsToExpect, discoveryLocator, getName(), getTestId());
        addServiceListener(tsl);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testAddServiceListenerIServiceTypeIDIServiceListener() throws ContainerConnectException {
        IServiceInfo[] services = discoveryLocator.getServices();
        assertTrue("No Services must be registerd at this point " + (services.length == 0 ? "" : services[0].toString()), services.length == 0);
        final TestServiceListener tsl = new TestServiceListener(eventsToExpect, discoveryLocator, getName(), getTestId());
        discoveryLocator.addServiceListener(serviceInfo.getServiceID().getServiceTypeID(), tsl);
        addListenerRegisterAndWait(tsl, serviceInfo);
        discoveryLocator.removeServiceListener(serviceInfo.getServiceID().getServiceTypeID(), tsl);
        IContainerEvent[] event = tsl.getEvent();
        assertNotNull("Test listener didn't receive discovery", event);
        assertEquals("Test listener received unexpected amount of discovery events: \n\t" + Arrays.asList(event), eventsToExpect, event.length);
        IServiceInfo serviceInfo2 = ((IServiceEvent) event[eventsToExpect - 1]).getServiceInfo();
        assertTrue("IServiceInfo should match, expected:\n\t" + serviceInfo + " but:\n\t" + serviceInfo2, comparator.compare(serviceInfo2, serviceInfo) == 0);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testAddServiceTypeListener() throws ContainerConnectException {
        IServiceInfo[] services = discoveryLocator.getServices();
        assertTrue("No Services must be registerd at this point " + (services.length == 0 ? "" : services[0].toString()), services.length == 0);
        final TestServiceTypeListener testTypeListener = new TestServiceTypeListener(eventsToExpect);
        discoveryLocator.addServiceTypeListener(testTypeListener);
        synchronized (testTypeListener) {
            // register a service which we expect the test listener to get notified of
            registerService();
            try {
                testTypeListener.wait(waitTimeForProvider);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Some discovery unrelated threading issues?");
            }
        }
        discoveryLocator.removeServiceTypeListener(testTypeListener);
        IContainerEvent[] event = testTypeListener.getEvent();
        assertNotNull("Test listener didn't receive discovery", event);
        assertEquals("Test listener received unexpected amount of discovery events: \n\t" + Arrays.asList(event), eventsToExpect, event.length);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testRemoveServiceListenerIServiceListener() throws ContainerConnectException {
        final TestServiceListener serviceListener = new TestServiceListener(eventsToExpect, discoveryLocator, getName(), getTestId());
        addServiceListener(serviceListener);
    //TODO reregister and verify the listener doesn't receive any events any longer.
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testRemoveServiceListenerIServiceTypeIDIServiceListener() throws ContainerConnectException {
        final TestServiceListener serviceListener = new TestServiceListener(eventsToExpect, discoveryLocator, getName(), getTestId());
        addServiceListener(serviceListener);
    //TODO reregister and verify the listener doesn't receive any events any longer.
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 * @throws ContainerConnectException 
	 */
    public void testRemoveServiceTypeListener() throws ContainerConnectException {
        assertTrue("No Services must be registerd at this point", discoveryLocator.getServices().length == 0);
        final TestServiceTypeListener testTypeListener = new TestServiceTypeListener(eventsToExpect);
        discoveryLocator.addServiceTypeListener(testTypeListener);
        synchronized (testTypeListener) {
            // register a service which we expect the test listener to get notified of
            registerService();
            try {
                testTypeListener.wait(waitTimeForProvider);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Some discovery unrelated threading issues?");
            }
        }
        discoveryLocator.removeServiceTypeListener(testTypeListener);
        IContainerEvent[] event = testTypeListener.getEvent();
        assertNotNull("Test listener didn't receive any discovery event", event);
        assertEquals("Test listener received unexpected amount of discovery events: \n\t" + Arrays.asList(event), eventsToExpect, event.length);
    //TODO reregister and verify the listener doesn't receive any events any longer.
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)}.
	 * @throws InterruptedException 
	 * @throws OperationCanceledException 
	 * @throws ContainerConnectException 
	 */
    public void testGetAsyncServiceInfo() throws OperationCanceledException, InterruptedException, ContainerConnectException {
        registerService();
        final IFuture aFuture = discoveryLocator.getAsyncServiceInfo(serviceInfo.getServiceID());
        final Object object = aFuture.get();
        assertTrue(object instanceof IServiceInfo);
        final IServiceInfo info = (IServiceInfo) object;
        assertTrue("IServiceInfo should match, expected:\n\t" + serviceInfo + " but:\n\t" + info, comparator.compare(info, serviceInfo) == 0);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServices()}.
	 * @throws ContainerConnectException 
	 * @throws InterruptedException 
	 * @throws OperationCanceledException 
	 */
    public void testGetAsyncServices() throws ContainerConnectException, OperationCanceledException, InterruptedException {
        registerService();
        final IFuture aFuture = discoveryLocator.getAsyncServices();
        final Object object = aFuture.get();
        assertTrue(object instanceof IServiceInfo[]);
        final IServiceInfo[] services = (IServiceInfo[]) object;
        assertTrue("Found: " + services.length + Arrays.asList(services), services.length == eventsToExpect);
        for (int i = 0; i < services.length; i++) {
            IServiceInfo iServiceInfo = services[i];
            if (comparator.compare(iServiceInfo, serviceInfo) == 0) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)}.
	 * @throws ContainerConnectException 
	 * @throws InterruptedException 
	 * @throws OperationCanceledException 
	 */
    public void testGetAsyncServicesIServiceTypeID() throws ContainerConnectException, OperationCanceledException, InterruptedException {
        registerService();
        final IFuture aFuture = discoveryLocator.getAsyncServices(serviceInfo.getServiceID().getServiceTypeID());
        final Object object = aFuture.get();
        assertTrue(object instanceof IServiceInfo[]);
        final IServiceInfo[] services = (IServiceInfo[]) object;
        assertTrue("Found: " + services.length + Arrays.asList(services), services.length == eventsToExpect);
        for (int i = 0; i < services.length; i++) {
            IServiceInfo iServiceInfo = services[i];
            if (comparator.compare(iServiceInfo, serviceInfo) == 0) {
                return;
            }
        }
        fail("Self registered service not found");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServiceTypes()}.
	 * @throws ContainerConnectException 
	 * @throws InterruptedException 
	 * @throws OperationCanceledException 
	 */
    public void testGetAsyncServiceTypes() throws ContainerConnectException, OperationCanceledException, InterruptedException {
        registerService();
        final IFuture aFuture = discoveryLocator.getAsyncServiceTypes();
        final Object object = aFuture.get();
        assertTrue(object instanceof IServiceTypeID[]);
        final IServiceTypeID[] services = (IServiceTypeID[]) object;
        // just expect one event as the implementation filters dupes
        assertTrue("Found: " + services.length + Arrays.asList(services), services.length == 1);
        for (int i = 0; i < services.length; i++) {
            IServiceTypeID iServiceTypeId = services[i];
            if (serviceInfo.getServiceID().getServiceTypeID().equals(iServiceTypeId)) {
                return;
            }
        }
        fail("Self registered service not found");
    }
}
