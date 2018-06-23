/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.remoteservice;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxy;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.InvalidSyntaxException;

/**
 *
 */
public abstract class AbstractRemoteServiceTest extends ContainerAbstractTestCase {

    protected static final int SLEEPTIME = new Integer(System.getProperty("org.eclipse.ecf.tests.remoteservice.AbstractRemoteServiceTest.SLEEPTIME", "5000")).intValue();

    protected IRemoteServiceContainerAdapter[] adapters = null;

    protected IRemoteServiceID[] ids;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.tests.ContainerAbstractTestCase#getClientContainerName()
	 */
    protected abstract String getClientContainerName();

    protected void setClientCount(int count) {
        super.setClientCount(count);
        adapters = new IRemoteServiceContainerAdapter[count];
        ids = new IRemoteServiceID[count];
    }

    protected void setupRemoteServiceAdapters() throws Exception {
        if (server != null)
            server.getAdapter(IRemoteServiceContainerAdapter.class);
        final int clientCount = getClientCount();
        for (int i = 0; i < clientCount; i++) {
            adapters[i] = (IRemoteServiceContainerAdapter) getClients()[i].getAdapter(IRemoteServiceContainerAdapter.class);
        }
    }

    protected IRemoteServiceContainerAdapter[] getRemoteServiceAdapters() {
        return adapters;
    }

    protected IRemoteServiceListener createRemoteServiceListener(final boolean server) {
        return new IRemoteServiceListener() {

            public void handleServiceEvent(IRemoteServiceEvent event) {
                System.out.println((server ? "server" : "client") + "handleServiceEvent(" + event + ")");
                if (event instanceof IRemoteServiceRegisteredEvent) {
                    if (server) {
                        ids[0] = event.getReference().getID();
                    } else {
                        ids[1] = event.getReference().getID();
                    }
                }
            }
        };
    }

    protected void addRemoteServiceListeners() {
        for (int i = 0; i < adapters.length; i++) {
            adapters[i].addRemoteServiceListener(createRemoteServiceListener(i == 0));
        }
    }

    protected IRemoteServiceRegistration registerService(IRemoteServiceContainerAdapter adapter, String serviceInterface, Object service, Dictionary serviceProperties, int sleepTime) {
        final IRemoteServiceRegistration result = adapter.registerRemoteService(new String[] { serviceInterface }, service, serviceProperties);
        sleep(sleepTime);
        return result;
    }

    protected IRemoteServiceReference[] getRemoteServiceReferences(IRemoteServiceContainerAdapter adapter, ID target, ID[] idFilter, String clazz, String filter) throws ContainerConnectException {
        try {
            return adapter.getRemoteServiceReferences(target, idFilter, clazz, filter);
        } catch (final InvalidSyntaxException e) {
            fail("should not happen");
        }
        return null;
    }

    protected IRemoteService getRemoteService(IRemoteServiceContainerAdapter adapter, ID target, ID[] idFilter, String clazz, String filter, int sleepTime) throws ContainerConnectException {
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(adapter, target, idFilter, clazz, filter);
        if (refs == null || refs.length == 0)
            return null;
        return adapter.getRemoteService(refs[0]);
    }

    protected String getFilterFromServiceProperties(Dictionary serviceProperties) {
        StringBuffer filter = null;
        if (serviceProperties != null && serviceProperties.size() > 0) {
            filter = new StringBuffer("(&");
            for (final Enumeration e = serviceProperties.keys(); e.hasMoreElements(); ) {
                final Object key = e.nextElement();
                final Object val = serviceProperties.get(key);
                if (key != null && val != null) {
                    filter.append("(").append(key).append("=").append(val).append(")");
                }
            }
            filter.append(")");
        }
        return (filter == null) ? null : filter.toString();
    }

    protected IRemoteService registerAndGetRemoteService(IRemoteServiceContainerAdapter server, IRemoteServiceContainerAdapter client, ID target, ID[] idFilter, String serviceName, Dictionary serviceProperties, int sleepTime) throws ContainerConnectException {
        registerService(server, serviceName, createService(), serviceProperties, sleepTime);
        return getRemoteService(client, target, idFilter, serviceName, getFilterFromServiceProperties(serviceProperties), sleepTime);
    }

    protected IRemoteCall createRemoteCall(final String method, final Object[] params) {
        return new IRemoteCall() {

            public String getMethod() {
                return method;
            }

            public Object[] getParameters() {
                return params;
            }

            public long getTimeout() {
                return 5000;
            }
        };
    }

    protected Object createService() {
        return new IConcatService() {

            public String concat(String string1, String string2) {
                final String result = string1.concat(string2);
                System.out.println("SERVICE.concat(" + string1 + "," + string2 + ") returning " + result);
                return string1.concat(string2);
            }
        };
    }

    public void testRemoteServiceAdapters() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        assertNotNull(adapters);
        for (int i = 0; i < adapters.length; i++) assertNotNull(adapters[i]);
        Thread.sleep(SLEEPTIME);
    }

    public void testRemoteServiceNamespace() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        assertNotNull(adapters);
        for (int i = 0; i < adapters.length; i++) {
            Namespace namespace = adapters[i].getRemoteServiceNamespace();
            assertNotNull(namespace);
        }
        Thread.sleep(SLEEPTIME);
    }

    public void testRegisterService() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        // adapter [0] is the service 'server'
        final IRemoteServiceRegistration reg = registerService(adapters[0], IConcatService.class.getName(), createService(), customizeProperties(null), 0);
        assertNotNull(reg);
        IRemoteServiceID remoteServiceID = reg.getID();
        assertNotNull(remoteServiceID);
        assertNotNull(remoteServiceID.getContainerID());
        Thread.sleep(SLEEPTIME);
    }

    public void testUnregisterService() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        // adapter [0] is the service 'server'
        final IRemoteServiceRegistration reg = registerService(adapters[0], IConcatService.class.getName(), createService(), customizeProperties(null), SLEEPTIME);
        assertNotNull(reg);
        assertNotNull(reg.getContainerID());
        reg.unregister();
        Thread.sleep(SLEEPTIME);
    }

    protected ID getConnectTargetID() {
        return null;
    }

    protected ID[] getIDFilter() {
        return null;
    }

    public void testGetServiceReferences() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        // Register service on client[0]
        registerService(adapters[0], IConcatService.class.getName(), createService(), customizeProperties(null), SLEEPTIME);
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(adapters[1], getConnectTargetID(), getIDFilter(), IConcatService.class.getName(), null);
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
        Thread.sleep(SLEEPTIME);
    }

    public void testGetServiceReferencesWithFilter() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        final Properties props = new Properties();
        props.put("foo", "bar");
        props.put("foo1", "bar");
        registerService(adapters[0], IConcatService.class.getName(), createService(), customizeProperties(props), SLEEPTIME);
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(adapters[1], getConnectTargetID(), getIDFilter(), IConcatService.class.getName(), getFilterFromServiceProperties(props));
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
        Thread.sleep(SLEEPTIME);
    }

    public void testGetServiceReferencesWithFilterFail() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        final Properties props = new Properties();
        props.put("foo", "bar");
        props.put("foo1", "bar");
        registerService(adapters[0], IConcatService.class.getName(), createService(), customizeProperties(props), SLEEPTIME);
        // Create dictionary that is *not* the same as props, so the filter
        // should miss
        final Properties missProps = new Properties();
        missProps.put("bar", "foo");
        final String missFilter = getFilterFromServiceProperties(missProps);
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(adapters[1], getConnectTargetID(), getIDFilter(), IConcatService.class.getName(), missFilter);
        assertTrue(refs == null);
        Thread.sleep(SLEEPTIME);
    }

    public void testGetService() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        assertNotNull(service);
        Thread.sleep(SLEEPTIME);
    }

    public void testGetProxy() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        assertNotNull(service);
        Object proxy = service.getProxy();
        assertTrue(proxy instanceof IRemoteServiceProxy);
        Thread.sleep(SLEEPTIME);
    }

    public void testGetProxyNoRemoteServiceProxy() throws Exception {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        Properties props = new Properties();
        props.put(Constants.SERVICE_PREVENT_RSPROXY, "true");
        final IRemoteService service = registerAndGetRemoteService(adapters[0], adapters[1], getClient(0).getConnectedID(), getIDFilter(), IConcatService.class.getName(), customizeProperties(props), SLEEPTIME);
        assertNotNull(service);
        Object proxy = service.getProxy();
        assertTrue(!(proxy instanceof IRemoteServiceProxy));
        Thread.sleep(SLEEPTIME);
    }

    protected IRemoteCall createRemoteConcat(String first, String second) {
        return createRemoteCall("concat", new Object[] { first, second });
    }

    protected IRemoteService registerAndGetRemoteService() throws ContainerConnectException {
        final IRemoteServiceContainerAdapter[] adapters = getRemoteServiceAdapters();
        return registerAndGetRemoteService(adapters[0], adapters[1], getClient(0).getConnectedID(), getIDFilter(), IConcatService.class.getName(), customizeProperties(null), SLEEPTIME);
    }

    protected IRemoteCallListener createRemoteCallListener() {
        return new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                System.out.println("CLIENT.handleEvent(" + event + ")");
            }
        };
    }

    public void testCallSynch() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        traceCallStart("callSynch");
        final Object result = service.callSync(createRemoteConcat("Eclipse ", "is cool"));
        traceCallEnd("callSynch", result);
        assertNotNull(result);
        assertTrue(result.equals("Eclipse ".concat("is cool")));
        Thread.sleep(SLEEPTIME);
    }

    protected void traceCallStart(String callType) {
        System.out.println(callType + " start");
    }

    protected void traceCallEnd(String callType, Object result) {
        System.out.println(callType + " end");
        System.out.println("  result=" + result);
    }

    protected void traceCallEnd(String callType) {
        System.out.println(callType + " end.");
    }

    public void testBadCallSynch() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        // exist
        try {
            service.callSync(createRemoteCall("concat1", new Object[] { "first", "second" }));
            fail();
        } catch (final ECFException e) {
        }
        // concat
        try {
            service.callSync(createRemoteCall("concat", new Object[] { "first" }));
            fail();
        } catch (final ECFException e) {
        }
        Thread.sleep(SLEEPTIME);
    }

    public void testCallAsynch() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        traceCallStart("callAsynch");
        service.callAsync(createRemoteConcat("ECF ", "is cool"), createRemoteCallListener());
        traceCallEnd("callAsynch");
        Thread.sleep(SLEEPTIME);
    }

    public void testFireAsynch() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        traceCallStart("fireAsynch");
        service.fireAsync(createRemoteConcat("Eclipse ", "sucks"));
        traceCallEnd("fireAsynch");
        Thread.sleep(SLEEPTIME);
    }

    public void testProxy() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        final IConcatService proxy = (IConcatService) service.getProxy();
        assertNotNull(proxy);
        traceCallStart("getProxy");
        final String result = proxy.concat("ECF ", "sucks");
        traceCallEnd("getProxy", result);
        Thread.sleep(SLEEPTIME);
    }

    public void testAsyncResult() throws Exception {
        final IRemoteService service = registerAndGetRemoteService();
        if (service == null)
            return;
        traceCallStart("callAsynchResult");
        final IFuture result = service.callAsync(createRemoteConcat("ECF AsynchResults ", "are cool"));
        traceCallEnd("callAsynchResult", result);
        assertNotNull(result);
        Thread.sleep(SLEEPTIME);
    }

    protected Dictionary customizeProperties(Dictionary props) {
        return props;
    }
}
