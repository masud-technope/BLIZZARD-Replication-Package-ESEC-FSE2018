/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.r_osgi;

import ch.ethz.iks.r_osgi.RemoteOSGiException;
import java.lang.reflect.Method;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.reflection.ClassUtil;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallStartEvent;
import org.eclipse.equinox.concurrent.future.*;

/**
 * The R-OSGi adapter implementation of the IRemoteService interface.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
final class RemoteServiceImpl extends AbstractRemoteService {

    // the ECF remote refImpl
    RemoteServiceReferenceImpl refImpl;

    // the service object.
    Object service;

    // the next free service ID.
    private long nextID;

    /**
	 * constructor.
	 * 
	 * @param service
	 *            the service (proxy) object.
	 */
    public  RemoteServiceImpl(final RemoteServiceReferenceImpl refImpl, final Object service) {
        this.refImpl = refImpl;
        this.service = service;
    }

    protected IRemoteServiceID getRemoteServiceID() {
        return refImpl.getID();
    }

    protected IRemoteServiceReference getRemoteServiceReference() {
        return refImpl;
    }

    protected String[] getInterfaceClassNames() {
        return refImpl.getR_OSGiServiceReference().getServiceInterfaces();
    }

    /**
	 * call the service asynchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @param listener
	 *            the callback listener.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callAsync(org.eclipse.ecf.remoteservice.IRemoteCall,
	 *      org.eclipse.ecf.remoteservice.IRemoteCallListener)
	 */
    public void callAsync(final IRemoteCall call, final IRemoteCallListener listener) {
        new AsyncResult(call, listener).start();
    }

    /**
	 * call the service asynchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @return the result proxy.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callAsync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
    public IFuture callAsync(final IRemoteCall call) {
        return getAsyncExecutor().execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return callSync(call);
            }
        }, null);
    }

    private IExecutor asyncExecutor;

    private IExecutor syncExecutor;

    private final Object executorLock = new Object();

    private IExecutor getAsyncExecutor() {
        synchronized (executorLock) {
            if (asyncExecutor == null) {
                IExecutor executor = Activator.getDefault().getExecutor(false);
                asyncExecutor = (executor == null) ? new ThreadsExecutor() : executor;
            }
            return asyncExecutor;
        }
    }

    private IExecutor getSyncExecutor() {
        synchronized (executorLock) {
            if (syncExecutor == null) {
                IExecutor executor = Activator.getDefault().getExecutor(true);
                syncExecutor = (executor == null) ? new ImmediateExecutor() : executor;
            }
            return syncExecutor;
        }
    }

    /**
	 * call the service synchronously.
	 * 
	 * @param call
	 *            the call object.
	 * @return the result or <code>null</code>
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#callSync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
    public Object callSync(final IRemoteCall call) throws ECFException {
        Object[] ps = call.getParameters();
        final Object[] parameters = (ps == null) ? EMPTY_ARGS : ps;
        final Class[] formalParams = new Class[parameters.length];
        for (int i = 0; i < formalParams.length; i++) {
            formalParams[i] = call.getParameters()[i].getClass();
        }
        IFuture future = getSyncExecutor().execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                final Method method = ClassUtil.getMethod(service.getClass(), call.getMethod(), formalParams);
                return method.invoke(service, parameters);
            }
        }, null);
        Object result = null;
        try {
            result = future.get(call.getTimeout());
        } catch (OperationCanceledException e) {
            throw new ECFException("callSync cancelled", e);
        } catch (InterruptedException e) {
            return null;
        } catch (TimeoutException e) {
            throw new ECFException("callSync timed out after " + Long.toString(call.getTimeout()) + "ms", new TimeoutException(call.getTimeout()));
        }
        IStatus status = future.getStatus();
        if (!status.isOK())
            //$NON-NLS-1$
            throw new ECFException("Exception during callSync", status.getException());
        return result;
    }

    /**
	 * fire an asynchronous call without getting the result returned.
	 * 
	 * @param call
	 *            the call object.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#fireAsync(org.eclipse.ecf.remoteservice.IRemoteCall)
	 */
    public void fireAsync(final IRemoteCall call) throws ECFException {
        try {
            callAsync(call);
        } catch (RemoteOSGiException r) {
            throw new ECFException(r);
        } catch (Throwable t) {
        }
    }

    /**
	 * get the service proxy object.
	 * 
	 * @return the service proxy object.
	 * @see org.eclipse.ecf.remoteservice.IRemoteService#getProxy()
	 */
    public Object getProxy() throws ECFException {
        if (!refImpl.getR_OSGiServiceReference().isActive()) {
            //$NON-NLS-1$
            throw new ECFException("Container currently not connected");
        }
        return super.getProxy();
    }

    /**
	 * get the next call id.
	 * 
	 * @return the next call id.
	 */
    synchronized long getNextID() {
        return nextID++;
    }

    /**
	 * inner class implementing the asynchronous result object. This
	 * implementation also provides the calling infrastructure.
	 */
    private class AsyncResult extends Thread {

        // the result of the call.
        Object result;

        // the exception, if any happened during the call.
        Throwable exception;

        // the remote call object.
        IRemoteCall call;

        // the callback listener, if provided.
        private IRemoteCallListener listener;

        // constructor
         AsyncResult(final IRemoteCall call, final IRemoteCallListener listener) {
            this.call = call;
            this.listener = listener;
        }

        // the call happens here.
        public void run() {
            Object r = null;
            Throwable e = null;
            final long reqID = getNextID();
            if (listener != null) {
                listener.handleEvent(new IRemoteCallStartEvent() {

                    public IRemoteCall getCall() {
                        return call;
                    }

                    public IRemoteServiceReference getReference() {
                        return refImpl;
                    }

                    public long getRequestId() {
                        return reqID;
                    }
                });
            }
            try {
                r = callSync(call);
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
        }
    }
}
