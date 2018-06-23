/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.lang.reflect.Method;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.RemoteCall;
import org.osgi.framework.ServiceException;

/**
 * Abstract client remote service instance.   This class should be overridden to implement the abstract
 * invokeAsync, and invokeSync methods, which will be called when the proxy created is called by clients.
 * 
 * @since 8.9
 */
public abstract class AbstractRSAClientService extends AbstractClientService {

    public static class RSARemoteCall extends RemoteCall {

        private final Object proxy;

        private final Method reflectMethod;

        public  RSARemoteCall(Object proxy, Method method, String methodName, Object[] parameters, long timeout) {
            super(methodName, parameters, timeout);
            this.reflectMethod = method;
            this.proxy = proxy;
        }

        public Method getReflectMethod() {
            return reflectMethod;
        }

        public Object getProxy() {
            return proxy;
        }
    }

    /**
	 * @param call the remote call to invoke
	 * @param callable the remote callable to invoke
	 * @return Object result of remote call
	 * @throws ECFException if invoke fails
	 */
    @Override
    protected Object invokeRemoteCall(IRemoteCall call, IRemoteCallable callable) throws ECFException {
        return null;
    }

    public  AbstractRSAClientService(AbstractClientContainer container, RemoteServiceClientRegistration registration) {
        super(container, registration);
    }

    /**
	 * Invoke a remote call asynchronously.  This method should not block and should return either a {@link org.eclipse.equinox.concurrent.future.IFuture}, {@link java.util.concurrent.Future}, or {@link java.util.concurrent.CompletableFuture}
	 * or a
	 * CompletableFuture based upon the return type defined in the asynchronous service interface.
	 * 
	 * @param remoteCall the RSARemoteCall to use to make the asynchronous remote call.  Will not be <code>null</code>.
	 * @return Object.   Should return a non-null instance of {@link org.eclipse.equinox.concurrent.future.IFuture}, {@link java.util.concurrent.Future}, or {@link java.util.concurrent.CompletableFuture}
	 * @throws ECFException if async cannot be invoked
	 */
    protected abstract Object invokeAsync(RSARemoteCall remoteCall) throws ECFException;

    /**
	 * Invoke a remote call synchronously.  This method should block until a value may be returned, or the remote
	 * call has failed or timed out.
	 * 
	 * @param remoteCall the RSARemoteCall to synchronously invoke.  Will not be <code>null</code>.
	 * @return the result (of appropriate type)
	 * @throws ECFException if some exception occurred during invocation
	 */
    protected abstract Object invokeSync(RSARemoteCall remoteCall) throws ECFException;

    protected RSARemoteCall createRemoteCall(Object proxy, Method method, String methodName, Object[] parameters, long timeout) {
        return new RSARemoteCall(proxy, method, methodName, parameters, timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object resultObject = invokeObject(proxy, method, args);
            if (resultObject != null)
                return resultObject;
            if (isAsync(proxy, method, args))
                return invokeAsync(createRemoteCall(proxy, method, getAsyncInvokeMethodName(method), args, IRemoteCall.DEFAULT_TIMEOUT));
            final String callMethod = getCallMethodNameForProxyInvoke(method, args);
            final Object[] callParameters = getCallParametersForProxyInvoke(callMethod, method, args);
            final long callTimeout = getCallTimeoutForProxyInvoke(callMethod, method, args);
            return invokeSync(createRemoteCall(proxy, method, callMethod, callParameters, callTimeout));
        } catch (Throwable t) {
            if (t instanceof ServiceException)
                throw t;
            throw new ServiceException("Service exception on remote service proxy rsid=" + getRemoteServiceID(), ServiceException.REMOTE, t);
        }
    }
}
