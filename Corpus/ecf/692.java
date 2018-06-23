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

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.*;

/**
 * Entry point remote service container adapter. This is the entry point
 * interface for accessing remote services through ECF containers.
 * 
 */
public interface IRemoteServiceContainerAdapter extends IRemoteServiceHost, IRemoteServiceConsumer {

    /**
	 * Add listener for remote service registration/unregistration for this
	 * container
	 * 
	 * @param listener
	 *            notified of service registration/unregistration events. Must
	 *            not be <code>null</code> .
	 */
    public void addRemoteServiceListener(IRemoteServiceListener listener);

    /**
	 * Remove remote service registration/unregistration listener for this
	 * container.
	 * 
	 * @param listener
	 *            to remove. Must not be <code>null</code> .
	 */
    public void removeRemoteServiceListener(IRemoteServiceListener listener);

    /**
	 * Register a new remote service. This method is to be called by the service
	 * server...i.e. the client that wishes to make available a service to other
	 * client within this container.
	 * 
	 * @param clazzes
	 *            the interface classes that the service exposes to remote
	 *            clients. Must not be <code>null</code> and must not be an
	 *            empty array.
	 * @param service
	 *            the service object.  Under normal conditions this object must
	 *            <ul><li>not be <code>null</code></li>
	 *            <li>implement all of the classes specified by the first parameter</li>
	 *            </ul>
	 *            The only situation when the service object may be <code>null</code> is if
	 *            the service property {@link Constants#SERVICE_REGISTER_PROXY} is set
	 *            in the properties.  If {@link Constants#SERVICE_REGISTER_PROXY} is set
	 *            in the properties parameter (to an arbitrary value), then the service
	 *            object may then be <code>null</code>.
	 * @param properties
	 *            to be associated with service
	 * @return IRemoteServiceRegistration the service registration. Will not
	 *         return <code>null</code> .
	 */
    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary properties);

    /**
	 * Returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * <p>
	 * Note this method assumes that the enclosing container has previously
	 * been connected, and uses the idFilter to filter among targets within the
	 * previously connected set of container IDs.  To request connection as 
	 * part of reference lookup, see {@link #getRemoteServiceReferences(ID, String, String)}.
	 * </p>
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p><code>target</code> is a remote container to connect to.  If <code>null</code>, no connection attempt is made.</p>
	 * <p>
	 * <code>idFilter</code> is used to select a registered services that were
	 * registered by a given set of containers with id in idFilter. Only
	 * services exposed by a container with id in idFilter will be returned.  If <code>idFilter</code> is <code>null</code>, all containers are
	 * considered to match the filter.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * If <code>filter</code> is <code>null</code>, all registered services
	 * are considered to match the filter. If <code>filter</code> cannot be
	 * parsed, an {@link InvalidSyntaxException} will be thrown with a human
	 * readable message where the filter became unparsable.
	 * 
	 * @param target
	 *            a target container to connect to if enclosing container is not already 
	 *            connected.  May be <code>null</code>.
	 * @param idFilter
	 *            an array of ID instances that will restrict the search for
	 *            matching container ids If null, all remote containers will be
	 *            considered in search for matching IRemoteServiceReference
	 *            instances. May be <code>null</code>.
	 * 
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return Array of IRemoteServiceReferences matching given search criteria or 
	 *            <code>null</code> if no services are found that match the search.
	 * 
	 * @throws InvalidSyntaxException If filter contains an invalid filter string that cannot be parsed.
	 * @since 5.0
	 */
    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException;

    /**
	 * Asynchronously returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * <p>
	 * The IFuture is returned immediately, and subsequent calls to {@link IFuture#get()}
	 * or {@link IFuture#get(long)} will return the actual results received.  The type of
	 * the Object returned from {@link IFuture#get()} will be IRemoteServiceReference [].
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p><code>target</code> is a remote container to connect to.  If <code>null</code>, no connection attempt is made.</p>
	 * <p>
	 * <code>idFilter</code> is used to select a registered services that were
	 * registered by a given set of containers with id in idFilter. Only
	 * services exposed by a container with id in idFilter will be returned.  If <code>idFilter</code> is <code>null</code>, all containers are
	 * considered to match the filter.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * If <code>filter</code> is <code>null</code>, all registered services
	 * are considered to match the filter. If <code>filter</code> cannot be
	 * parsed, an {@link InvalidSyntaxException} will be thrown with a human
	 * readable message where the filter became unparsable.
	 * 
	 * @param target
	 *            an target to connect to if enclosing container is not already 
	 *            connected.  May be <code>null</code>.
	 * 
	 * @param idFilter
	 *            an array of ID instances that will restrict the search for
	 *            matching container ids If null, all remote containers will be
	 *            considered in search for matching IRemoteServiceReference
	 *            instances. May be <code>null</code>.
	 *            
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return IFuture that through subsequent calls to IFuture#get() will return
	 *         IRemoteServiceReference [] with IRemoteServiceReferences matching given search criteria. 
	 *         Will not return <code>null</code>.
	 * 
	 * @since 5.0
	 */
    public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter);

    /**
	 * Returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * <p>
	 * Note this method assumes that the enclosing container has previously
	 * been connected, and uses the idFilter to filter among targets within the
	 * previously connected set of container IDs.  To request connection as 
	 * part of reference lookup, see {@link #getRemoteServiceReferences(ID, String, String)}.
	 * </p>
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <code>idFilter</code> is used to select a registered services that were
	 * registered by a given set of containers with id in idFilter. Only
	 * services exposed by a container with id in idFilter will be returned.
	 * 
	 * <p>
	 * If <code>idFilter</code> is <code>null</code>, all containers are
	 * considered to match the filter.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * 
	 * <p>
	 * If <code>filter</code> is <code>null</code>, all registered services
	 * are considered to match the filter. If <code>filter</code> cannot be
	 * parsed, an {@link InvalidSyntaxException} will be thrown with a human
	 * readable message where the filter became unparsable.
	 * 
	 * @param idFilter
	 *            an array of ID instances that will restrict the search for
	 *            matching container ids If null, all remote containers will be
	 *            considered in search for matching IRemoteServiceReference
	 *            instances. May be <code>null</code>.
	 * 
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return Array of IRemoteServiceReferences matching given search criteria or 
	 *            <code>null</code> if no services are found that match the search.
	 * 
	 * @throws InvalidSyntaxException If filter contains an invalid filter string that cannot be parsed.
	 */
    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException;

    /**
	 * <p>
	 * Returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * </p>
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * </p>
	 * <p>target is a remote container to connect to.</p>
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * 
	 * <p>
	 * If <code>filter</code> is <code>null</code>, all registered services
	 * are considered to match the filter. If <code>filter</code> cannot be
	 * parsed, an {@link InvalidSyntaxException} will be thrown with a human
	 * readable message where the filter became unparsable.
	 * 
	 * @param target
	 *            an target to connect to if enclosing container is not already 
	 *            connected.  May be <code>null</code>.
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return Array of IRemoteServiceReferences matching given search criteria or 
	 *            <code>null</code> if no services are found that match the search.
	 * 
	 * @throws InvalidSyntaxException If filter contains an invalid filter string that cannot be parsed.
	 * @since 3.0
	 */
    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException;

    /**
	 * Asynchronously returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * <p>
	 * Note this method assumes that the enclosing container has previously
	 * been connected, and uses the idFilter to filter among targets within the
	 * previously connected set of container IDs.  To request connection as 
	 * part of reference lookup, see {@link #getRemoteServiceReferences(ID, String, String)}.
	 * </p>
	 * <p>
	 * The IFuture is returned immediately, and subsequent calls to {@link IFuture#get()}
	 * or {@link IFuture#get(long)} will return the actual results received.  The type of
	 * the Object returned from {@link IFuture#get()} will be IRemoteServiceReference [].
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <code>idFilter</code> is used to select a registered services that were
	 * registered by a given set of containers with id in idFilter. Only
	 * services exposed by a container with id in idFilter will be returned.
	 * 
	 * <p>
	 * If <code>idFilter</code> is <code>null</code>, all containers are
	 * considered to match the filter.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * 
	 * @param idFilter
	 *            an array of ID instances that will restrict the search for
	 *            matching container ids If null, all remote containers will be
	 *            considered in search for matching IRemoteServiceReference
	 *            instances. May be <code>null</code>.
	 * 
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return IFuture that through subsequent calls to IFuture#get() will return
	 *         IRemoteServiceReference [] with IRemoteServiceReferences matching given search criteria. 
	 *         Will not return <code>null</code>.
	 * 
	 * @since 3.0
	 */
    public IFuture asyncGetRemoteServiceReferences(ID[] idFilter, String clazz, String filter);

    /**
	 * Asynchronously returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class and match the
	 * specified idFilter, and filter criteria.
	 * <p>
	 * The IFuture is returned immediately, and subsequent calls to {@link IFuture#get()}
	 * or {@link IFuture#get(long)} will return the actual results received.  The type of
	 * the Object returned from {@link IFuture#get()} will be IRemoteServiceReference [].
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <code>idFilter</code> is used to select a registered services that were
	 * registered by a given set of containers with id in idFilter. Only
	 * services exposed by a container with id in idFilter will be returned.
	 * 
	 * <p>target is a remote container to connect to.</p>
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * 
	 * @param target
	 *            an target to connect to if enclosing container is not already 
	 *            connected.  May be <code>null</code>.
	 * 
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. Must not be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return IFuture that through subsequent calls to IFuture#get() will return
	 *         IRemoteServiceReference [] with IRemoteServiceReferences matching given search criteria. 
	 *         Will not return <code>null</code>.
	 * 
	 * @since 3.0
	 */
    public IFuture asyncGetRemoteServiceReferences(ID target, String clazz, String filter);

    /**
	 * <p>
	 * Returns an array of <code>IRemoteServiceReference</code> objects. The
	 * returned array of <code>IRemoteServiceReference</code> objects contains
	 * services that were registered under the specified class, or if the clazz
	 * parameter is <code>null</code> all services registered.
	 * </p>
	 * <p>
	 * The list is valid at the time of the call to this method, however since
	 * the remote service container is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * </p>
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter} for a description of the filter string syntax.
	 * </p>
	 * <p>
	 * If <code>filter</code> is <code>null</code>, all registered services
	 * are considered to match the filter. If <code>filter</code> cannot be
	 * parsed, an {@link InvalidSyntaxException} will be thrown with a human
	 * readable message where the filter became unparsable.
	 * </p>
	 * @param clazz
	 *            the fully qualified name of the interface class that describes
	 *            the desired service. May be <code>null</code>.
	 * @param filter
	 *            The filter criteria. May be <code>null</code>.
	 * @return Array of IRemoteServiceReferences matching given search criteria or 
	 *            <code>null</code> if no services are found that match the search.
	 * 
	 * @throws InvalidSyntaxException If filter contains an invalid filter string that cannot be parsed.
	 * @since 3.0
	 */
    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException;

    /**
	 * Get namespace to use for this remote service provider.
	 * @return Namespace to use for creating IRemoteServiceID for this remote service provider.  Will
	 * not return <code>null</code>.
	 * @since 3.0
	 */
    public Namespace getRemoteServiceNamespace();

    /**
	 * Get a remote service ID from a containerID and a containerRelative long value.  Will return a non-null value
	 * if the IRemoteServiceRegistration/Reference is currently 'known' to this container adapter.  <code>null</code> 
	 * if not.
	 * @param containerID the containerID that is the server/host for the remote service.  Must not be <code>null</code>.  This 
	 * must be the containerID for the <b>server</b>/host of the remote service.  
	 * @param containerRelativeID the long value identifying the remote service relative to the container ID.
	 * @return IRemoteServiceID instance if the associated IRemoteServiceRegistration/Reference is known to this container
	 * adapter, <code>null</code> if it is not.
	 * @since 3.0
	 */
    public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID);

    /**
	 * Get the remote service reference known to this container for the given IRemoteServiceID.  Note that
	 * this method must be guaranteed not to block by the provider implementation.
	 * 
	 * @param serviceID the serviceID to retrieve the IRemoteServiceReference for.
	 * @return IRemoteServiceReference the remote service reference associated with the given serviceID.
	 * Will return <code>null</code> if no IRemoteServiceReference found for the given serviceID.
	 * @since 3.0
	 */
    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID);

    /**
	 * Get remote service for given IRemoteServiceReference. Note that clients
	 * that call this method successfully should later call
	 * {@link IRemoteServiceContainerAdapter#ungetRemoteService(IRemoteServiceReference)}
	 * when the IRemoteService will no longer be used.
	 * 
	 * @param reference
	 *            the IRemoteServiceReference for the desired service. Must not
	 *            be <code>null</code> .
	 * @return IRemoteService representing the remote service. If remote service
	 *         no longer exists for reference, then <code>null</code> is
	 *         returned.
	 * 
	 * @see #ungetRemoteService(IRemoteServiceReference)
	 */
    public IRemoteService getRemoteService(IRemoteServiceReference reference);

    /**
	 * Unget IRemoteServiceReference. Release all resources associated with the
	 * given IRemoteServiceReference. This method should be called by users of
	 * the IRemoteServiceReference that have previously called
	 * {@link IRemoteServiceContainerAdapter#getRemoteService(IRemoteServiceReference)}.
	 * If this method returns true, then the previously used IRemoteService will
	 * no longer be usable.
	 * 
	 * @param reference
	 *            the IRemoteServiceReference to unget
	 * @return true if unget successful, false if not. If this method returns
	 *         true, then the IRemoteService instance previously retrieved via
	 *         the given IRemoteServiceReference instance provided will no
	 *         longer be usable.
	 * 
	 * @see #getRemoteService(IRemoteServiceReference)
	 */
    public boolean ungetRemoteService(IRemoteServiceReference reference);

    /**
	 * Creates a <code>IRemoteFilter</code> object. This <code>IRemoteFilter</code> object may
	 * be used to match a <code>IRemoteServiceReference</code> object or a
	 * <code>Dictionary</code> object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A <code>IRemoteFilter</code> object encapsulating the filter string.
	 * @throws InvalidSyntaxException If <code>filter</code> contains an invalid
	 *         filter string that cannot be parsed.
	 * @throws NullPointerException If <code>filter</code> is null.
	 * @throws java.lang.IllegalStateException If this IRemoteServiceContainerAdapter is no
	 *         longer valid.
	 * 
	 * @since 3.0
	 * @see "Framework specification for a description of the filter string syntax."
	 * @see FrameworkUtil#createFilter(String)
	 */
    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException;

    /**
	 * Set connect context for authentication upon subsequent calls to
	 * {@link #getRemoteServiceReferences(ID[], String, String)} or {@link #asyncGetRemoteServiceReferences(ID[], String, String)}. This
	 * method should be called with a non-null connectContext in order to allow
	 * authentication to occur during.
	 * 
	 * @param connectContext
	 *            the connect context to use for authenticating.
	 *            If <code>null</code>, then no authentication will be
	 *            attempted.
	 *            
	 * @since 3.0
	 */
    public void setConnectContextForAuthentication(IConnectContext connectContext);

    /**
	 * Set the remote service call policy to enable authorization on remote service method calls
	 * @param policy Implementation of <code>IRemoteServiceCallPolicy</code> containing authorization specific code
	 * @return <code>true</code> if the underlying provider supports using the policy, <code>false</code> if
	 * it does not support using the policy.
	 * @since 6.0
	 */
    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy);
}
