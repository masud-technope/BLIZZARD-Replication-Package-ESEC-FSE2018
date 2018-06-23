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
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.jobs.JobsExecutor;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.remoteservice.Activator;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.client.AbstractClientService.UriRequest;
import org.eclipse.ecf.remoteservice.events.*;
import org.eclipse.ecf.remoteservice.util.RemoteFilterImpl;
import org.eclipse.equinox.concurrent.future.*;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Remote service client abstract superclass.
 * 
 * @since 4.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClientContainer extends AbstractContainer implements IRemoteServiceClientContainerAdapter {

    protected ID containerID;

    // The ID we've been assigned to connect to
    protected ID connectedID;

    protected Object connectLock = new Object();

    protected IConnectContext connectContext;

    protected Object remoteResponseDeserializerLock = new Object();

    protected IRemoteResponseDeserializer remoteResponseDeserializer = null;

    protected Object parameterSerializerLock = new Object();

    protected IRemoteCallParameterSerializer parameterSerializer = null;

    protected RemoteServiceClientRegistry registry;

    protected List remoteServiceListeners = new ArrayList();

    private List referencesInUse = new ArrayList();

    /**
	 * @since 4.1
	 */
    protected boolean alwaysSendDefaultParameters;

    public  AbstractClientContainer(ID containerID) {
        this.containerID = containerID;
        Assert.isNotNull(this.containerID);
        this.registry = new RemoteServiceClientRegistry(this);
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    public IConnectContext getConnectContextForAuthentication() {
        return connectContext;
    }

    public void setResponseDeserializer(IRemoteResponseDeserializer resource) {
        synchronized (remoteResponseDeserializerLock) {
            this.remoteResponseDeserializer = resource;
        }
    }

    public IRemoteResponseDeserializer getResponseDeserializer() {
        synchronized (remoteResponseDeserializerLock) {
            return this.remoteResponseDeserializer;
        }
    }

    public void setParameterSerializer(IRemoteCallParameterSerializer serializer) {
        synchronized (parameterSerializerLock) {
            this.parameterSerializer = serializer;
        }
    }

    protected IRemoteCallParameterSerializer getParameterSerializer() {
        synchronized (parameterSerializerLock) {
            return this.parameterSerializer;
        }
    }

    protected IRemoteResponseDeserializer getResponseDeserializer(IRemoteCall call, IRemoteCallable callable, Map responseHeaders) {
        synchronized (remoteResponseDeserializerLock) {
            return remoteResponseDeserializer;
        }
    }

    protected IRemoteCallParameterSerializer getParameterSerializer(IRemoteCallParameter parameter, Object value) {
        synchronized (parameterSerializerLock) {
            return parameterSerializer;
        }
    }

    /**
	 * Set the flag to <code>true</code> to include default parameters (which are specified when the callables are created) with
	 * every request to the remote service.
	 * <p>
	 * Setting to <code>false</code> will only send those parameter specified when the call is invoked.
	 * <p>
	 * Parameters which are specifed with the call override the defaults. Default parameters with a value of <code>null</code>
	 * are not included.
	 * 
	 * @param alwaysSendDefaultParameters whether to send default parameters with every remote call
	 * @since 4.1
	 */
    public void setAlwaysSendDefaultParameters(boolean alwaysSendDefaultParameters) {
        this.alwaysSendDefaultParameters = alwaysSendDefaultParameters;
    }

    public void addRemoteServiceListener(IRemoteServiceListener listener) {
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.add(listener);
        }
    }

    public IFuture asyncGetRemoteServiceReferences(final ID[] idFilter, final String clazz, final String filter) {
        //$NON-NLS-1$
        IExecutor executor = new JobsExecutor("asyncGetRemoteServiceReferences");
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(idFilter, clazz, filter);
            }
        }, null);
    }

    public IFuture asyncGetRemoteServiceReferences(final ID target, final String clazz, final String filter) {
        //$NON-NLS-1$
        IExecutor executor = new JobsExecutor("asyncGetRemoteServiceReferences");
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(target, clazz, filter);
            }
        }, null);
    }

    /**
	 * @since 5.0
	 */
    public IFuture asyncGetRemoteServiceReferences(final ID target, final ID[] idFilter, final String clazz, final String filter) {
        //$NON-NLS-1$
        IExecutor executor = new JobsExecutor("asyncGetRemoteServiceReferences");
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(target, idFilter, clazz, filter);
            }
        }, null);
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        return new RemoteFilterImpl(filter);
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        return registry.getAllRemoteServiceReferences(clazz, (filter == null) ? null : createRemoteFilter(filter));
    }

    public IRemoteService getRemoteService(IRemoteServiceReference reference) {
        if (reference == null || !(reference instanceof RemoteServiceClientReference))
            return null;
        RemoteServiceClientRegistration registration = registry.findServiceRegistration((RemoteServiceClientReference) reference);
        if (registration == null)
            return null;
        IRemoteService result = (registration == null) ? null : createRemoteService(registration);
        if (result != null)
            referencesInUse.add(reference);
        return result;
    }

    public IRemoteServiceID getRemoteServiceID(ID containerID1, long containerRelativeID) {
        return registry.getRemoteServiceID(containerID1, containerRelativeID);
    }

    public Namespace getRemoteServiceNamespace() {
        return getConnectNamespace();
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
        return registry.findServiceReference(serviceID);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
        return registry.getRemoteServiceReferences(idFilter, clazz, (filter == null) ? null : createRemoteFilter(filter));
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return registry.getRemoteServiceReferences(target, clazz, (filter == null) ? null : createRemoteFilter(filter));
    }

    /**
	 * @since 5.0
	 */
    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return registry.getRemoteServiceReferences(target, idFilter, clazz, (filter == null) ? null : createRemoteFilter(filter));
    }

    public IRemoteServiceRegistration registerRemoteService(final String[] clazzes, Object service, Dictionary properties) {
        //$NON-NLS-1$
        throw new RuntimeException("registerRemoteService cannot be used with client container");
    }

    public void removeRemoteServiceListener(IRemoteServiceListener listener) {
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.remove(listener);
        }
    }

    public boolean ungetRemoteService(final IRemoteServiceReference reference) {
        boolean result = referencesInUse.contains(reference);
        referencesInUse.remove(reference);
        fireRemoteServiceEvent(new IRemoteServiceUnregisteredEvent() {

            public IRemoteServiceReference getReference() {
                return reference;
            }

            public ID getLocalContainerID() {
                return getID();
            }

            public ID getContainerID() {
                return getID();
            }

            public String[] getClazzes() {
                return registry.getClazzes(reference);
            }
        });
        return result;
    }

    // Implementation of IRestClientContainerAdapter
    public IRemoteServiceRegistration registerCallables(IRemoteCallable[] callables, Dictionary properties) {
        Assert.isNotNull(callables);
        final RemoteServiceClientRegistration registration = createRestServiceRegistration(callables, properties);
        this.registry.registerRegistration(registration);
        // notify
        fireRemoteServiceEvent(new IRemoteServiceRegisteredEvent() {

            public IRemoteServiceReference getReference() {
                return registration.getReference();
            }

            public ID getLocalContainerID() {
                return registration.getContainerID();
            }

            public ID getContainerID() {
                return getID();
            }

            public String[] getClazzes() {
                return registration.getClazzes();
            }
        });
        return registration;
    }

    /**
	 * @param serviceType serviceType
	 * @param callables callables
	 * @param properties properties
	 * @return IRemoteServiceRegistration registration created for registration
	 * @since 8.5
	 */
    public IRemoteServiceRegistration registerCallables(Class<?> serviceType, IRemoteCallable[] callables, Dictionary properties) {
        return registerCallables(new String[] { serviceType.getName() }, new IRemoteCallable[][] { callables }, properties);
    }

    public IRemoteServiceRegistration registerCallables(String[] clazzes, IRemoteCallable[][] callables, Dictionary properties) {
        final RemoteServiceClientRegistration registration = createRestServiceRegistration(clazzes, callables, properties);
        this.registry.registerRegistration(registration);
        // notify
        fireRemoteServiceEvent(new IRemoteServiceRegisteredEvent() {

            public IRemoteServiceReference getReference() {
                return registration.getReference();
            }

            public ID getLocalContainerID() {
                return registration.getContainerID();
            }

            public ID getContainerID() {
                return getID();
            }

            public String[] getClazzes() {
                return registration.getClazzes();
            }
        });
        return registration;
    }

    // IContainer implementation methods
    public void connect(ID targetID, IConnectContext connectContext1) throws ContainerConnectException {
        if (targetID == null)
            //$NON-NLS-1$
            throw new ContainerConnectException("targetID cannot be null");
        Namespace targetNamespace = targetID.getNamespace();
        Namespace connectNamespace = getConnectNamespace();
        if (connectNamespace == null)
            //$NON-NLS-1$
            throw new ContainerConnectException("targetID namespace cannot be null");
        if (!(targetNamespace.getName().equals(connectNamespace.getName())))
            //$NON-NLS-1$
            throw new ContainerConnectException("targetID of incorrect type");
        fireContainerEvent(new ContainerConnectingEvent(containerID, targetID));
        synchronized (connectLock) {
            if (connectedID == null) {
                connectedID = targetID;
                this.connectContext = connectContext1;
            } else if (!connectedID.equals(targetID))
                throw new //$NON-NLS-1$
                ContainerConnectException(//$NON-NLS-1$
                "Already connected to " + connectedID.getName());
        }
        fireContainerEvent(new ContainerConnectedEvent(containerID, targetID));
    }

    public void disconnect() {
        ID oldId = connectedID;
        fireContainerEvent(new ContainerDisconnectingEvent(containerID, oldId));
        synchronized (connectLock) {
            connectedID = null;
            connectContext = null;
        }
        fireContainerEvent(new ContainerDisconnectedEvent(containerID, oldId));
    }

    public ID getConnectedID() {
        synchronized (connectLock) {
            return connectedID;
        }
    }

    public ID getID() {
        return containerID;
    }

    public void dispose() {
        disconnect();
        synchronized (remoteServiceListeners) {
            remoteServiceListeners.clear();
        }
        super.dispose();
    }

    void fireRemoteServiceEvent(IRemoteServiceEvent event) {
        List toNotify = null;
        // Copy array
        synchronized (remoteServiceListeners) {
            toNotify = new ArrayList(remoteServiceListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            ((IRemoteServiceListener) i.next()).handleServiceEvent(event);
        }
    }

    protected RemoteServiceClientRegistration createRestServiceRegistration(String[] clazzes, IRemoteCallable[][] callables, Dictionary properties) {
        return new RemoteServiceClientRegistration(getRemoteServiceNamespace(), clazzes, callables, properties, registry);
    }

    protected RemoteServiceClientRegistration createRestServiceRegistration(IRemoteCallable[] callables, Dictionary properties) {
        return new RemoteServiceClientRegistration(getRemoteServiceNamespace(), callables, properties, registry);
    }

    protected void logException(String string, Throwable e) {
        Activator a = Activator.getDefault();
        if (a != null)
            a.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, string, e));
    }

    protected ID getRemoteCallTargetID() {
        // First synchronize on connect lock
        synchronized (connectLock) {
            ID cID = getConnectedID();
            return (cID == null) ? getID() : cID;
        }
    }

    /**
	 * @param uri uri
	 * @param call remote call
	 * @param callable callable
	 * @return IRemoteCallParameter[] remote call parameters prepared
	 * @throws NotSerializableException if cannot be serialized
	 * @since 8.5
	 */
    protected IRemoteCallParameter[] prepareCallParameters(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        List<IRemoteCallParameter> results = new ArrayList<IRemoteCallParameter>();
        Object[] callParameters = call.getParameters();
        IRemoteCallParameter[] defaultCallableParameters = callable.getDefaultParameters();
        if (callParameters != null) {
            for (int i = 0; i < callParameters.length; i++) {
                Object p = callParameters[i];
                // If the parameter is already a remote call parameter just add
                if (p instanceof IRemoteCallParameter) {
                    results.add((IRemoteCallParameter) p);
                    continue;
                }
                if (defaultCallableParameters != null && i < defaultCallableParameters.length) {
                    // callableParameter
                    if (p == null)
                        results.add(defaultCallableParameters[i]);
                    // If not null, then we need to serialize
                    IRemoteCallParameter val = serializeParameter(uri, call, callable, defaultCallableParameters[i], p);
                    if (val != null)
                        results.add(val);
                }
            }
        }
        return results.toArray(new IRemoteCallParameter[results.size()]);
    }

    /**
	 * @param uri uri
	 * @param call call
	 * @param callable callable
	 * @return IRemoteCallParameter[] extra parameters
	 * @throws NotSerializableException if not serializable
	 * @since 8.5
	 */
    protected IRemoteCallParameter[] prepareExtraParameters(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        List<IRemoteCallParameter> results = new ArrayList<IRemoteCallParameter>();
        Object[] callParameters = call.getParameters();
        IRemoteCallParameter[] defaultCallableParameters = callable.getDefaultParameters();
        // This depends on the previous for loop and how many (default) parameters have been used by it.
        if (alwaysSendDefaultParameters && (defaultCallableParameters.length > callParameters.length)) {
            // Start with the first parameter that wasn't specified
            for (int i = callParameters.length; i < defaultCallableParameters.length; i++) {
                IRemoteCallParameter param = defaultCallableParameters[i];
                Object value = param.getValue();
                // skip default parameters with null values
                if (value == null)
                    continue;
                // else serialize the parameter using the container's parameterSerializer
                results.add(serializeParameter(uri, call, callable, param, value));
            }
        }
        return results.toArray(new IRemoteCallParameter[results.size()]);
    }

    protected IRemoteCallParameter[] prepareParameters(String uri, IRemoteCall call, IRemoteCallable callable) throws NotSerializableException {
        List<IRemoteCallParameter> results = new ArrayList<IRemoteCallParameter>();
        IRemoteCallParameter[] preparedCallParameters = prepareCallParameters(uri, call, callable);
        for (int i = 0; i < preparedCallParameters.length; i++) results.add(preparedCallParameters[i]);
        IRemoteCallParameter[] preparedExtraParameters = prepareExtraParameters(uri, call, callable);
        for (int i = 0; i < preparedExtraParameters.length; i++) results.add(preparedExtraParameters[i]);
        return results.toArray(new IRemoteCallParameter[results.size()]);
    }

    /**
	 * Serialize the parameter using the container's parameterSerializer. If there is no serializer for this container, return null.
	 * 
	 * @param uri uri
	 * @param call call
	 * @param callable callable
	 * @param defaultParameter default parameter
	 * @param parameterValue parameter value
	 * @return IRemoteCallParameter the serialized parameter or null if there is no parameterSerializer for this container
	 * @throws NotSerializableException thrown if parameters cannot be serialized
	 * @see IRemoteCallParameterSerializer#serializeParameter(String, IRemoteCall, IRemoteCallable, IRemoteCallParameter, Object)
	 * @since 4.1
	 */
    protected IRemoteCallParameter serializeParameter(String uri, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter defaultParameter, Object parameterValue) throws NotSerializableException {
        // Get parameter serializer...and
        IRemoteCallParameterSerializer serializer = getParameterSerializer();
        IRemoteCallParameter val = (serializer == null) ? null : serializer.serializeParameter(uri, call, callable, defaultParameter, parameterValue);
        return val;
    }

    /**
	 * Serialize the parameter using the container's parameterSerializer. If there is no serializer for this container, return null.
	 * 
	 * @param uri uri
	 * @param call call
	 * @param callable callable
	 * @param currentParameters current parameters
	 * @param parameterValue parameter value
	 * @return IRemoteCallParameter[] parameters for given 
	 * @throws NotSerializableException thrown if parameters cannot be serialized
	 * @since 8.0
	 */
    protected IRemoteCallParameter[] serializeParameter(String uri, IRemoteCall call, IRemoteCallable callable, List currentParameters, Object[] parameterValue) throws NotSerializableException {
        // Get parameter serializer...and
        IRemoteCallParameterSerializer serializer = getParameterSerializer();
        IRemoteCallParameter[] current = (IRemoteCallParameter[]) currentParameters.toArray(new IRemoteCallParameter[currentParameters.size()]);
        IRemoteCallParameter[] val = (serializer == null) ? current : serializer.serializeParameter(uri, call, callable, current, parameterValue);
        return val;
    }

    /**
	 * @param uri uri
	 * @param call call
	 * @param callable callable
	 * @param responseHeaders http response headers
	 * @param responseBody response body as byte[]
	 * @return Object response deserialized via response deserializer
	 * @throws NotSerializableException if response cannot be deserialized for processing
	 * @since 8.0
	 */
    protected Object processResponse(String uri, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException {
        IRemoteResponseDeserializer deserializer = getResponseDeserializer();
        return (deserializer == null) ? null : deserializer.deserializeResponse(uri, call, callable, responseHeaders, responseBody);
    }

    /**
	 * Create an implementer of {@link IRemoteService} for the given registration.
	 * 
	 * @param registration registration from which to create the associated IRemoteService.  Will not be <code>null</code>.
	 * @return IRemoteService the remote service associated with this client container.  Should not return <code>null</code>.
	 */
    protected abstract IRemoteService createRemoteService(RemoteServiceClientRegistration registration);

    /**
	 * Prepare an endpoint address for the given call and callable.
	 * 
	 * @param call to create an endpoint for.  Will not be <code>null</code>.
	 * @param callable to create an endpoing for.  Will not be <code>null</code>.
	 * @return String that represents the endpoing for the given call and callable.  May only return <code>null</code> if the
	 * given call should not be completed (i.e. there is no endpoint associated with the given call).
	 */
    protected abstract String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable);

    /**
	 * @param endpoint endpoint
	 * @param call call
	 * @param callable callable
	 * @return UriRequest to use for request.  May be <code>null</code>
	 * @since 8.5
	 */
    public UriRequest createUriRequest(String endpoint, IRemoteCall call, IRemoteCallable callable) {
        return null;
    }
}
