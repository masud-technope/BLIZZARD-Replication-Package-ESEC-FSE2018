/*******************************************************************************
 * Copyright (c) 2011 Composent and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import java.util.Properties;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractTwoRemoteServiceAccessTest extends AbstractDistributionTest {

    protected static final int REGISTER_WAIT = Integer.parseInt(System.getProperty("waittime", "15000"));

    private final String[] classes = new String[] { TestServiceInterface1.class.getName(), TestServiceInterface2.class.getName() };

    private ServiceTracker st;

    private ServiceRegistration registration;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.osgi.services.distribution.AbstractDistributionTest#tearDown()
	 */
    protected void tearDown() throws Exception {
        // Unregister on server
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        if (st != null) {
            st.close();
            st = null;
        }
        Thread.sleep(REGISTER_WAIT);
        super.tearDown();
    }

    protected void createServiceTrackerAndRegister(String lookupClass, final Properties props) throws Exception {
        // Setup service tracker for client
        st = createProxyServiceTracker(lookupClass);
        // Actually register
        registration = registerService(classes, new TestService1(), props);
        // Wait
        Thread.sleep(REGISTER_WAIT);
    }

    protected void createServiceTrackerAndRegister(String lookupClass) throws Exception {
        createServiceTrackerAndRegister(lookupClass, getServiceProperties());
    }

    protected Properties getServiceProperties() {
        final Properties props = new Properties();
        props.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, getServerContainerName());
        props.put(RemoteConstants.SERVICE_EXPORTED_INTERFACES, "*");
        return props;
    }

    protected IRemoteCall createRemoteCall() {
        return new IRemoteCall() {

            public String getMethod() {
                return "doStuff1";
            }

            public Object[] getParameters() {
                return new Object[] {};
            }

            public long getTimeout() {
                return 30000;
            }
        };
    }

    public void testGetRemoteService1Reference() throws Exception {
        String lookupClass = TestServiceInterface1.class.getName();
        createServiceTrackerAndRegister(lookupClass);
        // Service Consumer - Get (remote) ervice references
        final ServiceReference[] remoteReferences = st.getServiceReferences();
        assertReferencesValidAndFirstHasCorrectType(remoteReferences, lookupClass);
        // Spec requires that the 'service.imported' property be set
        assertTrue(remoteReferences[0].getProperty(RemoteConstants.SERVICE_IMPORTED) != null);
    }

    public void testGetRemoteService2Reference() throws Exception {
        String lookupClass = TestServiceInterface2.class.getName();
        createServiceTrackerAndRegister(lookupClass);
        // Service Consumer - Get (remote) ervice references
        final ServiceReference[] remoteReferences = st.getServiceReferences();
        assertReferencesValidAndFirstHasCorrectType(remoteReferences, lookupClass);
        // Spec requires that the 'service.imported' property be set
        assertTrue(remoteReferences[0].getProperty(RemoteConstants.SERVICE_IMPORTED) != null);
    }

    public void testProxyWithService1() throws Exception {
        String lookupClass = TestServiceInterface1.class.getName();
        createServiceTrackerAndRegister(lookupClass);
        // Client - Get service references from service tracker
        final ServiceReference[] remoteReferences = st.getServiceReferences();
        assertReferencesValidAndFirstHasCorrectType(remoteReferences, lookupClass);
        // Get proxy/service
        final TestServiceInterface1 proxy = (TestServiceInterface1) getContext().getService(remoteReferences[0]);
        assertNotNull(proxy);
        // Now use proxy
        final String result = proxy.doStuff1();
        Trace.trace(Activator.PLUGIN_ID, "proxy.doStuff1 result=" + result);
        assertTrue(TestServiceInterface1.TEST_SERVICE_STRING1.equals(result));
    }

    public void testProxyWithService2() throws Exception {
        String lookupClass = TestServiceInterface2.class.getName();
        createServiceTrackerAndRegister(lookupClass);
        // Client - Get service references from service tracker
        final ServiceReference[] remoteReferences = st.getServiceReferences();
        assertReferencesValidAndFirstHasCorrectType(remoteReferences, lookupClass);
        // Get proxy/service
        final TestServiceInterface2 proxy = (TestServiceInterface2) getContext().getService(remoteReferences[0]);
        assertNotNull(proxy);
        // Now use proxy
        String result = proxy.doStuff1();
        Trace.trace(Activator.PLUGIN_ID, "proxy.doStuff1 result=" + result);
        assertTrue(TestServiceInterface1.TEST_SERVICE_STRING1.equals(result));
        // Now use proxy
        result = proxy.doStuff2();
        Trace.trace(Activator.PLUGIN_ID, "proxy.doStuff2 result=" + result);
        assertTrue(TestServiceInterface2.TEST_SERVICE_STRING2.equals(result));
    }
}
