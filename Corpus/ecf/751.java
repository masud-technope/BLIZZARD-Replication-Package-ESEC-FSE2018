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

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.equinox.concurrent.future.IFuture;

/**
 * Interface providing runtime access to a remote service. An instance
 * implementing this interface will be returned from the
 * IRemoteServiceContainerAdapter.getRemoteService(IRemoteServiceReference) and
 * may then be used to communicate with a remote service. The methods on this
 * interface support accessing the remote service in several ways:<br>
 * <ul>
 * <li>callSync -- A synchronous invocation that will block the calling thread
 * until complete (or timeout) and return the result from the remote or throw
 * exception if remote invocation fails or throws exception</li>
 * <li>callAsync/1 -- An asynchronous invocation that will not block the caller
 * thread but rather return a non-<code>null</code> {@link IFuture} instance
 * that can be polled for results.  See {@link IFuture#get()},
 * {@link IFuture#get(long)}, and {@link IFuture#isDone()}.
 * timeout, exception, or successful completion)</li>
 * <li>callAsync/2 -- An asynchronous invocation that will not block the caller
 * thread but rather notify the given listener asynchronously when complete (via
 * timeout, exception, or successful completion)</li>
 * <li>fireAsync -- An asynchronous invocation that will simply execute the
 * remote method asynchronously, but will not provide any response or remote
 * method failure information</li>
 * <li>getProxy -- Access to a local proxy for the remote service that will
 * expose the appropriate interface to the caller, and synchronously call the
 * remote methods when invoked. 
 * </ul>
 * 
 */
public interface IRemoteService {

    /**
	 * Call remote method specified by call parameter synchronously.
	 * 
	 * @param call
	 *            the remote call to make
	 * @return Object the result of the call. Will be <code>null</code> if
	 *         remote provides <code>null</code> as result.
	 * @throws ECFException
	 *             thrown if disconnect occurs, caller not currently connected,
	 *             or remote throws Exception
	 * @since 3.0
	 */
    public Object callSync(IRemoteCall call) throws ECFException;

    /**
	 * Call remote method specified by call parameter asynchronously, and notify
	 * specified listener when call starts and completes.
	 * 
	 * @param call
	 *            the remote call to make. Must not be <code>null</code> .
	 * @param listener
	 *            the listener to notify when call starts and is completed. The
	 *            listener will be notified via the two event types
	 *            IRemoteCallStartEvent and IRemoteCallCompleteEvent. Must not
	 *            be <code>null</code> .
	 * 
	 * @see org.eclipse.ecf.remoteservice.events.IRemoteCallStartEvent
	 * @see org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent
	 * @since 3.0
	 */
    public void callAsync(IRemoteCall call, IRemoteCallListener listener);

    /**
	 * Call remote method specified by call parameter asynchronously, and immediately
	 * return {@link IFuture} instance.  Returned IFuture will not be <code>null</code>,
	 * and allows the caller to retrieve the actual resulting value from the remote call
	 * (or exception).
	 * 
	 * @param call the remote call to make. Must not be <code>null</code> .
	 * @return IFuture the asynchronous result to allow the caller to poll
	 * for whether the result {@link IFuture#isDone()}, and then to {@link IFuture#get()}
	 * the actual result.
	 * @since 3.0
	 */
    public IFuture callAsync(IRemoteCall call);

    /**
	 * Fire remote method specified by call parameter. The remote method will be
	 * invoked as a result of asynchronous message send, but no
	 * failure/exception information will be returned, and no result will be
	 * returned
	 * 
	 * @param call
	 *            the remote call to make. Must not be <code>null</code> .
	 * @throws ECFException
	 *             if caller not currently connected
	 * @since 3.0
	 */
    public void fireAsync(IRemoteCall call) throws ECFException;

    /**
	 * Get local proxy for remote interface. The local proxy may then be used to
	 * make remote method calls transparently by invoking the local proxy method
	 * 
	 * @return Object that implements the interface specified in the
	 *         IRemoteServiceReference instance used to retrieve the
	 *         IRemoteService object. The result may then be cast to the
	 *         appropriate type. Will not be <code>null</code>.
	 * @throws ECFException
	 *             If some problem in creating the proxy.  The underlying problem is 
	 *             conveyed in the nested exception.
	 */
    public Object getProxy() throws ECFException;

    /**
	 * Get local proxy for remote interface. The local proxy may then be used to
	 * make remote method calls transparently by invoking the local proxy method
	 * 
	 * @param cl ClassLoader to use to create the proxy class.  
	 *         Must not be <code>null</code>. 
	 * @param interfaceClasses array of Class that has the loaded interface classes.  
	 *         Must not be <code>null</code> and should have dimension of one or more.
	 * @return Object that implements the given interfaceClasses.  The result may then 
	 *         be cast to the one of the types given in interfaceClasses. Will not be
	 *         <code>null</code>
	 * @throws ECFException
	 *             If some problem in creating the proxy.  The underlying problem is 
	 *             conveyed in the nested exception.
	 * @since 6.0
	 */
    public Object getProxy(ClassLoader cl, Class[] interfaceClasses) throws ECFException;
}
