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

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.tests.remoteservice.rpc.Activator;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.RemoteCallFactory;
import org.eclipse.ecf.remoteservice.client.IRemoteCallParameter;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteCallParameter;
import org.eclipse.ecf.remoteservice.client.RemoteCallableFactory;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.ecf.tests.remoteservice.rpc.common.IEcho;
import org.eclipse.equinox.concurrent.future.IFuture;

@SuppressWarnings("restriction")
public class RpcRemoteServiceTest extends AbstractRpcTestCase {

    private static final String ECHO_TEST_DATA = "Hello, world";

    IContainer container;

    IRemoteServiceRegistration registrationEcho;

    IRemoteServiceRegistration registrationEchoProxy;

    IRemoteServiceRegistration registrationCalc;

    protected void setUp() throws Exception {
        container = createRpcContainer(RpcConstants.TEST_ECHO_TARGET);
        IRemoteCallable callableEcho = RemoteCallableFactory.createCallable(RpcConstants.TEST_ECHO_METHOD_NAME, RpcConstants.TEST_ECHO_METHOD, new IRemoteCallParameter[] { new RemoteCallParameter(RpcConstants.TEST_ECHO_METHOD_PARAM) });
        registrationEcho = registerCallable(container, callableEcho, null);
        IRemoteCallable callableEchoProxy = RemoteCallableFactory.createCallable(IEcho.class.getName());
        registrationEchoProxy = registerCallable(container, callableEchoProxy, null);
        IRemoteCallable callableCalc = RemoteCallableFactory.createCallable(RpcConstants.TEST_CALC_PLUS_METHOD_NAME, RpcConstants.TEST_CALC_PLUS_METHOD, new IRemoteCallParameter[] { new RemoteCallParameter(RpcConstants.TEST_CALC_PLUS_METHOD_PARAM1), new RemoteCallParameter(RpcConstants.TEST_CALC_PLUS_METHOD_PARAM2) });
        registrationCalc = registerCallable(container, callableCalc, null);
    }

    protected void tearDown() throws Exception {
        registrationEcho.unregister();
        registrationEchoProxy.unregister();
        registrationCalc.unregister();
        container.disconnect();
    }

    public void testCallViaProxy() {
        IRemoteService rpcClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registrationEchoProxy.getReference());
        try {
            IEcho echo = (IEcho) rpcClientService.getProxy(Activator.class.getClassLoader(), new Class[] { IEcho.class });
            assertNotNull(echo);
            Object result = echo.echo(ECHO_TEST_DATA);
            assertNotNull(result);
            assertEquals(result, ECHO_TEST_DATA);
        } catch (ECFException e) {
            e.printStackTrace();
            fail("Could not contact the service");
        }
    }

    public void testCallViaProxy2() {
        IRemoteService rpcClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registrationEchoProxy.getReference());
        try {
            IEcho echo = (IEcho) rpcClientService.getProxy();
            assertNotNull(echo);
            Object result = echo.echo(ECHO_TEST_DATA);
            assertNotNull(result);
            assertEquals(result, ECHO_TEST_DATA);
        } catch (ECFException e) {
            e.printStackTrace();
            fail("Could not contact the service");
        }
    }

    public void testSyncCall() {
        IRemoteService rpcClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registrationEcho.getReference());
        try {
            Object result = rpcClientService.callSync(getEchoCall());
            assertNotNull(result);
            assertTrue(ECHO_TEST_DATA.equals(result));
        } catch (ECFException e) {
            fail("Could not contact the service");
        }
    }

    public void testAsynCall() {
        IRemoteService rpcClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registrationCalc.getReference());
        IFuture future = rpcClientService.callAsync(getCalcPlusCall());
        try {
            Object response = future.get();
            assertTrue(response instanceof Integer);
        } catch (OperationCanceledException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    public void testAsyncCallWithListener() throws Exception {
        IRemoteService rpcClientService = getRemoteServiceClientContainerAdapter(container).getRemoteService(registrationCalc.getReference());
        rpcClientService.callAsync(getCalcPlusCall(), new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                if (event instanceof IRemoteCallCompleteEvent) {
                    IRemoteCallCompleteEvent cce = (IRemoteCallCompleteEvent) event;
                    Object response = cce.getResponse();
                    assertTrue(response instanceof Integer);
                    syncNotify();
                }
            }
        });
        syncWaitForNotify(10000);
    }

    private IRemoteCall getEchoCall() {
        return RemoteCallFactory.createRemoteCall(RpcConstants.TEST_ECHO_METHOD_NAME, new Object[] { ECHO_TEST_DATA });
    }

    private IRemoteCall getCalcPlusCall() {
        return RemoteCallFactory.createRemoteCall(RpcConstants.TEST_CALC_PLUS_METHOD_NAME, new Object[] { new Integer(2), new Integer(3) });
    }
}
