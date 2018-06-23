/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectContext;

/**
 * Contract for ECF communications container<br>
 * <br>
 * IContainer instances are used by clients to define a context for
 * communications. <br>
 * <br>
 * The typical life cycle of an ECF communications container is:
 * <ol>
 * <li>Create an IContainer instance via a {@link ContainerFactory}</li>
 * <li><b>Optional</b>: Setup client-specific protocol adapters for
 * communicating via specific protocols</li>
 * <li>Connect the container to a remote process or group</li>
 * <li>Engage in communication via protocol adapters</li>
 * <li>Disconnect</li>
 * </ol>
 * For example, to create and connect an ECF "generic client":
 * 
 * <pre>
 * // Create container instance via factory
 * IContainer container = ContainerFactory.getDefault().createContainer(
 * 		&quot;ecf.generic.client&quot;);
 * 
 * // Get presence protocol adapter
 * IPresenceContainerAdapter presence = (IPresenceContainerAdapter) container
 * 		.getAdapter(IPresenceContainerAdapter.class);
 * // ... setup presence listeners and local input here using presence
 * 
 * // Connect
 * container.connect(target, targetConnectContext);
 * 
 * // Engage in appropriate communications here using protocol adapter(s)
 * // Manage protocol adapters as needed when finished
 * 
 * // Disconnect
 * container.disconnect();
 * </pre>
 * 
 */
public interface IContainer extends IAdaptable, IIdentifiable {

    /**
	 * Connect to a target remote process or process group. The target
	 * identified by the first parameter (targetID) is connected the
	 * implementation class. If authentication information is required, the
	 * required information is given via via the second parameter
	 * (connectContext).
	 * 
	 * Callers note that depending upon the provider implementation this method
	 * may block. It is suggested that callers use a separate thread to call
	 * this method.
	 * 
	 * This method provides an implementation independent way for container
	 * implementations to connect, authenticate, and communicate with a remote
	 * service or group of services. Providers are responsible for implementing
	 * this operation in a way appropriate to the given remote service (or
	 * group) via expected protocol.
	 * 
	 * @param targetID
	 *            the ID of the remote server or group to connect to. See
	 *            {@link #getConnectNamespace()} for a explanation of the
	 *            constraints upon this parameter.
	 * @param connectContext
	 *            any required context to allow this container to authenticate.
	 *            May be <code>null</code> if underlying provider does not
	 *            have any authentication requirements for connection.
	 * @exception ContainerConnectException
	 *                thrown if communication cannot be established with remote
	 *                service. Causes can include network connection failure,
	 *                authentication failure, server error, or if container is
	 *                already connected.
	 */
    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException;

    /**
	 * Get the target ID that this container instance has connected to. Returns
	 * null if not connected.
	 * 
	 * @return ID of the target we are connected to. Returns <code>null</code>
	 *         if container not connected.
	 */
    public ID getConnectedID();

    /**
	 * Get the Namespace for creating a targetID suitable for use as the first
	 * parameter in subsequent calls to {@link #connect(ID, IConnectContext)}.
	 * If this method returns <code>null</code>, then it means that
	 * <code>null</code> is expected as a valid parameter in subsequent calls
	 * to {@link #connect(ID, IConnectContext)}. If this method returns a non-<code>null</code>
	 * Namespace, then the <code>targetID</code> parameter in
	 * {@link #connect(ID, IConnectContext)} must be non-<code>null</code>
	 * instance created of the returned Namespace.
	 * 
	 * @return Namespace the namespace associated with subsequent calls to
	 *         {@link #connect(ID, IConnectContext)}. If <code>null</code>,
	 *         then the <code>targetID</code> instances passed to
	 *         {@link #connect(ID, IConnectContext)} may be <code>null</code>.
	 *         If not <code>null</code>, then <code>targetID</code>
	 *         instances passed to {@link #connect(ID, IConnectContext)} must be
	 *         instances of the returned Namespace.
	 */
    public Namespace getConnectNamespace();

    /**
	 * Disconnect. This operation will disconnect the local container instance
	 * from any previously joined target or group. Subsequent calls to
	 * getConnectedID() will return <code>null</code>.
	 */
    public void disconnect();

    /**
	 * This specialization of IAdaptable.getAdapter() returns additional
	 * services supported by this container. A container that supports
	 * additional services over and above the methods on <code>IContainer</code>
	 * should return them using this method. It is recommended that clients use
	 * this method rather than instanceof checks and downcasts to find out about
	 * the capabilities of a specific container.
	 * <p>
	 * Typically, after obtaining an IContainer, a client would use this method
	 * as a means to obtain a more meaningful interface to the container. This
	 * interface may or may not extend IContainer. For example, a client could
	 * use the following code to obtain an instance of ISharedObjectContainer:
	 * </p>
	 * 
	 * <pre>
	 * IContainer newContainer = ContainerFactory.createContainer(type);
	 * ISharedObjectContainer soContainer = (ISharedObjectContainer) newContainer
	 * 		.getAdapter(ISharedObjectContainer.class);
	 * if (soContainer == null)
	 * 	throw new ContainerCreateException(message);
	 * </pre>
	 * 
	 * <p>
	 * Implementations of this method should delegate to
	 * <code>IAdapterManager.loadAdapter()</code> if the service
	 * cannot be provided directly to ensure extensibility by third-party
	 * plug-ins.
	 * </p>
	 * 
	 * @param serviceType
	 *            the service type to look up
	 * @return the service instance castable to the given class, or
	 *         <code>null</code> if this container does not support the given
	 *         service
	 */
    public Object getAdapter(Class serviceType);

    /**
	 * Dispose this IContainer instance. The container instance will be made
	 * inactive after the completion of this method and will be unavailable for
	 * subsequent usage.
	 * 
	 */
    public void dispose();

    /**
	 * Add listener to IContainer. The listener's handleEvent method will be
	 * synchronously called when container methods are called. Minimally, the
	 * events delivered to the listener are as follows <br>
	 * <table BORDER=1 CELLPADDING=4 CELLSPACING=0>
	 * <caption>Container Events</caption>
	 * <tr>
	 * <td>container action</td>
	 * <td>Event</td>
	 * </tr>
	 * <tr>
	 * <td>connect start</td>
	 * <td>IContainerConnectingEvent</td>
	 * </tr>
	 * <tr>
	 * <td>connect complete</td>
	 * <td>IContainerConnectedEvent</td>
	 * </tr>
	 * <tr>
	 * <td>disconnect start</td>
	 * <td>IContainerDisconnectingEvent</td>
	 * </tr>
	 * <tr>
	 * <td>disconnect complete</td>
	 * <td>IContainerDisconnectedEvent</td>
	 * </tr>
	 * </table>
	 * 
	 * @param listener
	 *            the IContainerListener to add
	 */
    public void addListener(IContainerListener listener);

    /**
	 * Remove listener from IContainer.
	 * 
	 * @param listener
	 *            the IContainerListener to remove
	 */
    public void removeListener(IContainerListener listener);
}
