/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.io.NotSerializableException;
import java.lang.reflect.Method;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallStartEvent;
import org.eclipse.equinox.concurrent.future.*;

/**
 * Remote service client service.  Implements {@link IRemoteService}.
 * 
 * @since 4.0
 */
public abstract class AbstractClientService extends AbstractRemoteService {

    private long nextID = 0;

    protected RemoteServiceClientRegistration registration;

    protected AbstractClientContainer container;

    public  AbstractClientService(AbstractClientContainer container, RemoteServiceClientRegistration registration) {
        this.container = container;
        Assert.isNotNull(container);
        this.registration = registration;
        Assert.isNotNull(this.registration);
    }

    public Object callSync(IRemoteCall call) throws ECFException {
        IRemoteCallable callable = getRegistration().lookupCallable(call);
        if (callable == null)
            //$NON-NLS-1$
            throw new ECFException("Callable not found for call=" + call);
        return invokeRemoteCall(call, callable);
    }

    public IFuture callAsync(final IRemoteCall call) {
        return callAsync(call, getRegistration().lookupCallable(call));
    }

    public void callAsync(IRemoteCall call, IRemoteCallListener listener) {
        callAsync(call, getRegistration().lookupCallable(call), listener);
    }

    public void fireAsync(IRemoteCall call) throws ECFException {
        IRemoteCallable callable = getRegistration().lookupCallable(call);
        if (callable == null)
            //$NON-NLS-1$
            throw new ECFException("Remote callable not found");
        callAsync(call, callable);
    }

    protected Object invokeSync(IRemoteCall remoteCall) throws ECFException {
        // Now lookup callable
        IRemoteCallable callable = getRegistration().lookupCallable(remoteCall);
        // If not found...we're finished
        if (callable == null)
            //$NON-NLS-1$
            throw new ECFException("Callable not found for call=" + remoteCall);
        return invokeRemoteCall(remoteCall, callable);
    }

    protected Object[] getCallParametersForProxyInvoke(String callMethod, Method proxyMethod, Object[] args) {
        return args;
    }

    protected String getCallMethodNameForProxyInvoke(Method method, Object[] args) {
        return RemoteServiceClientRegistration.getFQMethod(method.getDeclaringClass().getName(), method.getName());
    }

    protected long getNextRequestID() {
        return nextID++;
    }

    protected void callAsync(IRemoteCall call, IRemoteCallable restClientCallable, IRemoteCallListener listener) {
        final AbstractExecutor executor = new ThreadsExecutor();
        executor.execute(new AsyncResult(call, restClientCallable, listener), null);
    }

    protected IFuture callAsync(final IRemoteCall call, final IRemoteCallable callable) {
        final AbstractExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                if (callable == null)
                    throw new ECFException("Callable not found");
                return invokeRemoteCall(call, callable);
            }
        }, null);
    }

    /**
	 * inner class implementing the asynchronous result object. This
	 * implementation also provides the calling infrastructure.
	 */
    protected class AsyncResult implements IProgressRunnable {

        IRemoteCall call;

        // the remote call object.
        IRemoteCallable callable;

        // the callback listener, if provided.
        IRemoteCallListener listener;

        // the result of the call.
        Object result;

        // the exception, if any happened during the call.
        Throwable exception;

        // constructor
        public  AsyncResult(final IRemoteCall call, final IRemoteCallable callable, final IRemoteCallListener listener) {
            this.call = call;
            this.callable = callable;
            this.listener = listener;
        }

        public Object run(IProgressMonitor monitor) throws Exception {
            Object r = null;
            Throwable e = null;
            final long reqID = getNextRequestID();
            if (listener != null) {
                listener.handleEvent(new IRemoteCallStartEvent() {

                    public IRemoteCall getCall() {
                        return call;
                    }

                    public IRemoteServiceReference getReference() {
                        return getRegistration().getReference();
                    }

                    public long getRequestId() {
                        return reqID;
                    }
                });
            }
            try {
                if (callable == null)
                    throw new ECFException(//$NON-NLS-1$
                    "Restcall not found for method=" + //$NON-NLS-1$
                    call.getMethod());
                r = invokeRemoteCall(call, callable);
            } catch (Throwable t) {
                e = t;
            }
            synchronized (AsyncResult.this) {
                result = r;
                exception = e;
                AsyncResult.this.notify();
            }
            if (listener != null) {
                listener.handleEvent(new IRemoteCallCompleteEvent() {

                    public Throwable getException() {
                        return exception;
                    }

                    public Object getResponse() {
                        return result;
                    }

                    public boolean hadException() {
                        return exception != null;
                    }

                    public long getRequestId() {
                        return reqID;
                    }
                });
            }
            return null;
        }
    }

    protected void handleInvokeException(String message, Throwable e) throws ECFException {
        throw new ECFException(message, e);
    }

    protected AbstractClientContainer getClientContainer() {
        return container;
    }

    protected RemoteServiceClientRegistration getRegistration() {
        return registration;
    }

    protected String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
        return getClientContainer().prepareEndpointAddress(call, callable);
    }

    protected IRemoteCallParameter[] prepareParameters(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        return getClientContainer().prepareParameters(uri, call, callable);
    }

    /**
	 * @param uri uri
	 * @param call call
	 * @param callable callable
	 * @param responseHeaders responseHeaders
	 * @param responseBody responseBody
	 * @return Object processed response
	 * @throws NotSerializableException if response cannot be deserialized
	 * @since 8.0
	 */
    protected Object processResponse(String uri, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException {
        return getClientContainer().processResponse(uri, call, callable, responseHeaders, responseBody);
    }

    protected IRemoteServiceID getRemoteServiceID() {
        return registration.getID();
    }

    protected IRemoteServiceReference getRemoteServiceReference() {
        return registration.getReference();
    }

    protected String[] getInterfaceClassNames() {
        return registration.getClazzes();
    }

    /**
	 * @since 8.5
	 */
    public static class UriRequest {

        private final String uri;

        private final IRemoteCall call;

        private final IRemoteCallable callable;

        public  UriRequest(String uri, IRemoteCall call, IRemoteCallable callable) {
            this.uri = uri;
            this.call = call;
            this.callable = callable;
        }

        public String getUri() {
            return uri;
        }

        public IRemoteCall getRemoteCall() {
            return call;
        }

        public IRemoteCallable getRemoteCallable() {
            return callable;
        }
    }

    /**
	 * @param endpoint endpoint
	 * @param call call
	 * @param callable callable
	 * @return UriRequest new UriRequest
	 * @since 8.5
	 */
    protected UriRequest createUriRequest(String endpoint, IRemoteCall call, IRemoteCallable callable) {
        return getClientContainer().createUriRequest(endpoint, call, callable);
    }

    /**
	 * Invoke remote call.  The implementation of this method should actually 
	 * make the remote call for the given call and associated callable.
	 * 
	 * @param call the call for the remote call.  Will not be <code>null</code>.
	 * @param callable the callable associated with the remote call.  Will not be <code>null</code>.
	 * @return Object the result of the remote call.  May be <code>null</code>.
	 * @throws ECFException thrown if the call fails.
	 */
    protected abstract Object invokeRemoteCall(final IRemoteCall call, final IRemoteCallable callable) throws ECFException;
}
