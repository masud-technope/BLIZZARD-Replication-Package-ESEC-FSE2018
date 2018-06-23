/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.remoteservice;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.RemoteServiceContainer;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AbstractConcatConsumerTestCase extends TestCase {

    private static int ASYNC_WAITTIME = 5000;

    protected IRemoteServiceContainer rsContainer;

    protected ID targetID;

    protected abstract String getContainerType();

    protected ID createStringID(String value) {
        return IDFactory.getDefault().createStringID(value);
    }

    protected ID createID(IContainer container, String value) {
        return IDFactory.getDefault().createID(container.getConnectNamespace(), value);
    }

    protected IContainer createContainer() throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType());
    }

    protected IContainer createContainer(ID containerID) throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType(), containerID);
    }

    protected IContainer createContainer(String containerID) throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType(), createStringID(containerID));
    }

    protected IRemoteServiceContainer createRemoteServiceContainer(IContainer container) {
        return new RemoteServiceContainer(container, (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class));
    }

    protected void tearDown() throws Exception {
        rsContainer.getContainer().disconnect();
        rsContainer.getContainer().dispose();
        ((IContainerManager) ContainerFactory.getDefault()).removeAllContainers();
        rsContainer = null;
        targetID = null;
    }

    protected Object createRemoteService() {
        return new IConcatService() {

            public String concat(String string1, String string2) {
                final String result = string1.concat(string2);
                System.out.println("SERVICE.concat(" + string1 + "," + string2 + ") returning " + result);
                return string1.concat(string2);
            }
        };
    }

    protected Dictionary createRemoteServiceProperties() {
        return null;
    }

    protected IRemoteService getRemoteService(ID target, String clazz, String filter) {
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(target, clazz, filter);
        if (refs == null || refs.length == 0)
            return null;
        return rsContainer.getContainerAdapter().getRemoteService(refs[0]);
    }

    protected IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) {
        try {
            return rsContainer.getContainerAdapter().getRemoteServiceReferences(target, clazz, filter);
        } catch (final InvalidSyntaxException e) {
            fail("should not happen");
        } catch (final ContainerConnectException e) {
            fail("connect problem");
        }
        return null;
    }

    protected IRemoteServiceReference[] getRemoteServiceReferences(ID[] targets, String clazz, String filter) {
        try {
            return rsContainer.getContainerAdapter().getRemoteServiceReferences(targets, clazz, filter);
        } catch (final InvalidSyntaxException e) {
            fail("should not happen");
        }
        return null;
    }

    public void testGetServiceReferences() throws Exception {
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
    }

    public void testGetRemoteServiceIDs() throws Exception {
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
        for (int i = 0; i < refs.length; i++) {
            IRemoteServiceID rsid = refs[i].getID();
            assertNotNull(rsid);
        }
    }

    public void testGetRemoteServiceReferenceObjectClass() throws Exception {
        String className = getRemoteServiceClass().getName();
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(targetID, className, getRemoteServiceFilter());
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
        for (int i = 0; i < refs.length; i++) {
            String[] intfClasses = (String[]) refs[i].getProperty(org.eclipse.ecf.remoteservice.Constants.OBJECTCLASS);
            List classNames = Arrays.asList(intfClasses);
            assertTrue(classNames.contains(className));
        }
    }

    public void testGetRemoteServiceReferenceServiceID() throws Exception {
        final IRemoteServiceReference[] refs = getRemoteServiceReferences(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertTrue(refs != null);
        assertTrue(refs.length > 0);
        for (int i = 0; i < refs.length; i++) {
            IRemoteServiceID rsid = refs[i].getID();
            Long sid = (Long) refs[i].getProperty(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID);
            assertNotNull(sid);
            assertTrue(sid.longValue() > -1);
            assertTrue(sid.longValue() == rsid.getContainerRelativeID());
        }
    }

    public void testGetService() throws Exception {
        final IRemoteService service = getRemoteService(targetID, IConcatService.class.getName(), null);
        assertNotNull(service);
    }

    public void testCallSynch() throws Exception {
        final IRemoteService service = getRemoteService(targetID, IConcatService.class.getName(), null);
        assertNotNull(service);
        traceCallStart("callSynch");
        final Object result = service.callSync(createRemoteConcat("Eclipse ", "is cool"));
        traceCallEnd("callSynch", result);
        assertNotNull(result);
        assertTrue(result.equals("Eclipse ".concat("is cool")));
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

    protected IRemoteCallListener createRemoteCallListener() {
        return new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                System.out.println("CLIENT.handleEvent(" + event + ")");
            }
        };
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
                return 3000;
            }
        };
    }

    protected IRemoteCall createRemoteConcat(String first, String second) {
        return createRemoteCall("concat", new Object[] { first, second });
    }

    protected Class getRemoteServiceClass() {
        return IConcatService.class;
    }

    protected String getRemoteServiceFilter() {
        return null;
    }

    public void testGetNamespace() throws Exception {
        Namespace ns = rsContainer.getContainer().getConnectNamespace();
        assertNotNull(ns);
    }

    public void testGetRSNamespace() throws Exception {
        Namespace ns = rsContainer.getContainerAdapter().getRemoteServiceNamespace();
        assertNotNull(ns);
    }

    public void testGetRSReference() throws Exception {
        IRemoteServiceReference[] references = getRemoteServiceReferences(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(references);
        assertTrue(references.length == 1);
    }

    public void testCallAsynch() throws Exception {
        final IRemoteService service = getRemoteService(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(service);
        traceCallStart("callAsynch");
        service.callAsync(createRemoteConcat("ECF ", "is cool"), createRemoteCallListener());
        traceCallEnd("callAsynch");
        Thread.sleep(ASYNC_WAITTIME);
    }

    public void testFireAsynch() throws Exception {
        final IRemoteService service = getRemoteService(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(service);
        traceCallStart("fireAsynch");
        service.fireAsync(createRemoteConcat("Eclipse ", "sucks"));
        traceCallEnd("fireAsynch");
        Thread.sleep(ASYNC_WAITTIME);
    }

    public void testProxy() throws Exception {
        final IRemoteService service = getRemoteService(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(service);
        final IConcatService proxy = (IConcatService) service.getProxy();
        assertNotNull(proxy);
        traceCallStart("getProxy");
        final String result = proxy.concat("ECF ", "sucks");
        traceCallEnd("getProxy", result);
        Thread.sleep(ASYNC_WAITTIME);
    }

    public void testAsyncResult() throws Exception {
        final IRemoteService service = getRemoteService(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(service);
        traceCallStart("callAsynchResult");
        final IFuture result = service.callAsync(createRemoteConcat("ECF AsynchResults ", "are cool"));
        Thread.sleep(ASYNC_WAITTIME);
        traceCallEnd("callAsynchResult", result);
        assertNotNull(result);
    }

    public void testGetLocallyRegisteredService() throws Exception {
        String classname = getRemoteServiceClass().getName();
        registerLocalService(classname, createRemoteService(), createRemoteServiceProperties());
        IRemoteServiceReference[] references = rsContainer.getContainerAdapter().getRemoteServiceReferences(new ID[] { rsContainer.getContainer().getID() }, classname, getRemoteServiceFilter());
        assertNotNull(references);
        assertTrue(references.length == 1);
        assertTrue(references[0].getContainerID().equals(rsContainer.getContainer().getID()));
        Thread.sleep(ASYNC_WAITTIME);
    }

    private void registerLocalService(String name, Object service, Dictionary properties) {
        rsContainer.getContainerAdapter().registerRemoteService(new String[] { name }, service, properties);
    }

    public void testGetRemoteServiceWithLocallyRegisteredService() throws Exception {
        String classname = getRemoteServiceClass().getName();
        // Register service locally
        registerLocalService(classname, createRemoteService(), createRemoteServiceProperties());
        // Now get remote service
        final IRemoteService service = getRemoteService(targetID, getRemoteServiceClass().getName(), getRemoteServiceFilter());
        assertNotNull(service);
        // Now get both
        IRemoteServiceReference[] references = getRemoteServiceReferences(new ID[] { rsContainer.getContainer().getID(), targetID }, classname, getRemoteServiceFilter());
        assertNotNull(references);
        assertTrue(references.length == 2);
        Thread.sleep(ASYNC_WAITTIME);
    }
}
