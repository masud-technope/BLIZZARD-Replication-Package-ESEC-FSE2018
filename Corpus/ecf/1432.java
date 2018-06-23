/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFRuntimeException;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;

public abstract class DiscoveryTestsWithoutRegister extends AbstractDiscoveryTest {

    protected IContainer container = null;

    public  DiscoveryTestsWithoutRegister(String name) {
        super(name);
    }

    protected IDiscoveryLocator getDiscoveryLocator() {
        final IDiscoveryLocator adapter = (IDiscoveryLocator) container.getAdapter(IDiscoveryLocator.class);
        assertNotNull("Adapter must not be null", adapter);
        return adapter;
    }

    protected IDiscoveryAdvertiser getDiscoveryAdvertiser() {
        final IDiscoveryAdvertiser adapter = (IDiscoveryAdvertiser) container.getAdapter(IDiscoveryAdvertiser.class);
        assertNotNull("Adapter must not be null", adapter);
        return adapter;
    }

    protected IContainer getContainer(String containerUnderTest) throws ContainerCreateException {
        return ContainerFactory.getDefault().createContainer(containerUnderTest);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.AbstractDiscoveryTest#setUp()
	 */
    protected void setUp() throws Exception {
        container = getContainer(containerUnderTest);
        assertNotNull(container);
        super.setUp();
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        container.disconnect();
        container.dispose();
    }

    public void testConnect() {
        assertNull(container.getConnectedID());
        try {
            container.connect(null, null);
        } catch (final ContainerConnectException e) {
            fail("connect may not fail the first time");
        }
        assertNotNull(container.getConnectedID());
    }

    public void testConnectTwoTimes() {
        testConnect();
        try {
            container.connect(null, null);
        } catch (final ContainerConnectException e) {
            return;
        }
        fail("succeeding connects should fail");
    }

    public void testDisconnect() {
        testConnect();
        container.disconnect();
        assertNull(container.getConnectedID());
    }

    public void testReconnect() {
        testDisconnect();
        testConnect();
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)}.
	 */
    public void testGetServiceInfoWithNull() {
        try {
            discoveryLocator.getServiceInfo(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)}.
	 */
    public void testGetServicesIServiceTypeIDWithNull() {
        try {
            discoveryLocator.getServices(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
    public void testUnregisterServiceWithNull() {
        testConnect();
        try {
            discoveryAdvertiser.unregisterService(null);
        } catch (final ECFRuntimeException e) {
            fail("null must cause AssertionFailedException");
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null must cause AssertionFailedException");
    }

    public void testDispose() {
        testConnect();
        container.dispose();
        assertNull(container.getConnectedID());
        try {
            container.connect(null, null);
        } catch (final ContainerConnectException e) {
            return;
        }
        fail("A disposed container must not be reusable");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
    public void testAddServiceTypeListenerWithNull() {
        try {
            discoveryLocator.addServiceTypeListener(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.core.IContainer#getConnectNamespace()}.
	 */
    public void testGetConnectNamespace() {
        testConnect();
        assertNotNull(container.getConnectNamespace());
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.core.IContainer#getID()}.
	 */
    public void testGetID() {
        testConnect();
        assertNotNull(container.getID());
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#getServicesNamespace()}.
	 */
    public void testGetServicesNamespace() {
        testConnect();
        final Namespace namespace = discoveryLocator.getServicesNamespace();
        assertNotNull(namespace);
        final IServiceTypeID serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(namespace, serviceInfo.getServiceID().getServiceTypeID());
        assertNotNull("It must be possible to obtain a IServiceTypeID", serviceTypeID);
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
    public void testRemoveServiceTypeListenerWithNull() {
        try {
            discoveryLocator.removeServiceTypeListener(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
    public void testRegisterServiceWithNull() {
        testConnect();
        try {
            discoveryAdvertiser.registerService(null);
        } catch (final ECFRuntimeException e) {
            fail("null must cause AssertionFailedException");
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null must cause AssertionFailedException");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
    public void testAddServiceListenerIServiceListenerWithNull() {
        try {
            discoveryLocator.addServiceListener(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
    public void testAddServiceListenerIServiceTypeIDIServiceListenerWithNull() {
        try {
            discoveryLocator.addServiceListener(null, null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
    public void testRemoveServiceListenerIServiceListenerWithNull() {
        try {
            discoveryLocator.removeServiceListener(null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }

    /**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IdiscoveryLocatorAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
    public void testRemoveServiceListenerIServiceTypeIDIServiceListenerWithNull() {
        try {
            discoveryLocator.removeServiceListener(null, null);
        } catch (final AssertionFailedException e) {
            return;
        }
        fail("null argument is not allowed in api");
    }
}
