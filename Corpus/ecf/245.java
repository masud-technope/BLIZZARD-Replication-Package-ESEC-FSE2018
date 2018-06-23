/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.equinox.concurrent.future.IFuture;

/**
 * Helper class for making it easier to call a remote service with method name and optional parameters.
 * 
 * @since 3.0
 */
public class RemoteServiceHelper {

    // Default of 30 seconds.
    public static long defaultTimeout = 30000;

    public static Object[] EMPTY_PARAMS = new Object[] {};

    public static long getDefaultTimeout() {
        return defaultTimeout;
    }

    public static void setDefaultTimeout(long timeout) {
        defaultTimeout = timeout;
    }

    /**
	 * Invoke given method asynchronously, and call listener upon successful completion.
	 * Uses default timeout.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @param listener the listener to call back when remote call initiated and completed.  Must not be <code>null</code>.
	 */
    public static void asyncExec(IRemoteService remoteService, final String method, final Object[] parameters, IRemoteCallListener listener) {
        asyncExec(remoteService, method, parameters, getDefaultTimeout(), listener);
    }

    /**
	 * Invoke given method asynchronously, and call listener upon successful completion.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @param timeout the timeout (in ms) for the remote call.
	 * @param listener the listener to call back when remote call initiated and completed.  Must not be <code>null</code>.
	 */
    public static void asyncExec(IRemoteService remoteService, final String method, final Object[] parameters, final long timeout, IRemoteCallListener listener) {
        Assert.isNotNull(remoteService);
        Assert.isNotNull(method);
        Assert.isNotNull(listener);
        final Object[] params = (parameters == null) ? EMPTY_PARAMS : parameters;
        remoteService.callAsync(new IRemoteCall() {

            public String getMethod() {
                return method;
            }

            public Object[] getParameters() {
                return params;
            }

            public long getTimeout() {
                return timeout;
            }
        }, listener);
    }

    /**
	 * Invoke given method asynchronously, return an IFuture immediately that can be subsequently queried for
	 * completion.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @param timeout the timeout (in ms) for the remote call.
	 * @return IFuture the future created
	 */
    public static IFuture futureExec(IRemoteService remoteService, final String method, final Object[] parameters, final long timeout) {
        Assert.isNotNull(remoteService);
        Assert.isNotNull(method);
        final Object[] params = (parameters == null) ? EMPTY_PARAMS : parameters;
        return remoteService.callAsync(new IRemoteCall() {

            public String getMethod() {
                return method;
            }

            public Object[] getParameters() {
                return params;
            }

            public long getTimeout() {
                return timeout;
            }
        });
    }

    /**
	 * Invoke given method asynchronously, return an IFuture immediately that can be subsequently queried for
	 * completion.  Uses default timeout.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @return IFuture the future created
	 */
    public static IFuture futureExec(IRemoteService remoteService, final String method, final Object[] parameters) {
        return futureExec(remoteService, method, parameters, getDefaultTimeout());
    }

    /**
	 * Invoke given method synchronously, blocking the calling thread until a result is received or
	 * timeout.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @param timeout the timeout (in ms) for the remote call.
	 * @return Object the result of this synchronous execution
	 * @throws ECFException if some problem with execution
	 */
    public static Object syncExec(IRemoteService remoteService, final String method, final Object[] parameters, final long timeout) throws ECFException {
        Assert.isNotNull(remoteService);
        Assert.isNotNull(method);
        final Object[] params = (parameters == null) ? EMPTY_PARAMS : parameters;
        return remoteService.callSync(new IRemoteCall() {

            public String getMethod() {
                return method;
            }

            public Object[] getParameters() {
                return params;
            }

            public long getTimeout() {
                return timeout;
            }
        });
    }

    /**
	 * Invoke given method synchronously, blocking the calling thread until a result is received or
	 * timeout.  Uses default timeout.
	 * 
	 * @param remoteService the IRemoteService to invoke.  Must not be <code>null</code>.
	 * @param method the method to invoke.  Must not be <code>null</code>.
	 * @param parameters the parameters associated with the method to invoke.  May be <code>null</code> (no parameters).
	 * @return Object the result of this synchronous execution
	 * @throws ECFException if some problem with execution
	 */
    public static Object syncExec(IRemoteService remoteService, final String method, final Object[] parameters) throws ECFException {
        return syncExec(remoteService, method, parameters, getDefaultTimeout());
    }
}
