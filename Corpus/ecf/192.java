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

import ch.ethz.iks.r_osgi.*;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.r_osgi.identity.*;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceUnregisteredEvent;
import org.eclipse.equinox.concurrent.future.*;
import org.osgi.framework.*;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The R-OSGi remote service container adapter. Implements the adapter and the
 * container interface.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
class R_OSGiRemoteServiceContainer implements IOSGiRemoteServiceContainerAdapter, IRemoteServiceContainerAdapter, IContainer, RemoteServiceListener {

    // the bundle context.
    private BundleContext context;

    // the R-OSGi remote service instance.
    private RemoteOSGiService remoteService;

    // the list of subscribed container listeners.
    private final List containerListeners = new ArrayList(0);

    // the ID of this container.
    R_OSGiID containerID;

    // the ID of the remote peer to which the container is connected to, or
    // null, if not yet connected.
    private R_OSGiID connectedID;

    // tracks the lifecycle of remote services.
    private ServiceTracker remoteServicesTracker;

    // service reference -> service registration
    private Map remoteServicesRegs = new HashMap(0);

    // the map of remote service listeners. Maps the listener to the service
    // registration of the internal R-OSGi remote service listener service.
    private Map remoteServiceListeners = new HashMap(0);

    // Connect context to use for connect calls
    private IConnectContext connectContext;

    // New system property to allow the per-transport exposure of remote services to be defeated.
    //$NON-NLS-1$ //$NON-NLS-2$
    private boolean exposeRemoteServicesOnAllTransports = Boolean.parseBoolean(System.getProperty("org.eclipse.ecf.internal.provider.r_osgi.exposeRemoteServicesOnAllTransports", "false"));

    public  R_OSGiRemoteServiceContainer(RemoteOSGiService service, final ID containerID) throws IDCreateException {
        Assert.isNotNull(service);
        Assert.isNotNull(containerID);
        context = Activator.getDefault().getContext();
        remoteService = service;
        if (containerID instanceof StringID) {
            this.containerID = createR_OSGiID(((StringID) containerID).getName());
        } else if (containerID instanceof R_OSGiID) {
            this.containerID = (R_OSGiID) containerID;
        } else {
            //$NON-NLS-1$
            throw new IDCreateException("Incompatible ID " + containerID);
        }
        startRegTracker();
    }

    private void startRegTracker() {
        try {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            final String filter = "(" + org.eclipse.ecf.remoteservice.Constants.SERVICE_CONTAINER_ID + "=" + containerID + ")";
            remoteServicesTracker = new ServiceTracker(context, context.createFilter(filter), new ServiceTrackerCustomizer() {

                public Object addingService(ServiceReference reference) {
                    return reference;
                }

                public void modifiedService(ServiceReference reference, Object service) {
                    // service got modified
                    return;
                }

                public void removedService(ServiceReference reference, Object service) {
                // service got removed
                }
            });
            remoteServicesTracker.open();
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
	 * add a remote service listener. This method accepts an ECF remote service
	 * listener and registers a R-OSGi listener service as an adapter.
	 * 
	 * @param listener
	 *            the ECF remote service listener.
	 * 
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#addRemoteServiceListener(org.eclipse.ecf.remoteservice.IRemoteServiceListener)
	 */
    public void addRemoteServiceListener(final IRemoteServiceListener listener) {
        Assert.isNotNull(listener);
        final RemoteServiceListener l = new RemoteServiceListener() {

            public void remoteServiceEvent(final RemoteServiceEvent event) {
                switch(event.getType()) {
                    case RemoteServiceEvent.REGISTERED:
                        listener.handleServiceEvent(new IRemoteServiceRegisteredEvent() {

                            IRemoteServiceReference reference = new RemoteServiceReferenceImpl(createRemoteServiceID(event.getRemoteReference()), event.getRemoteReference());

                            public String[] getClazzes() {
                                return event.getRemoteReference().getServiceInterfaces();
                            }

                            public ID getContainerID() {
                                return getReference().getContainerID();
                            }

                            public IRemoteServiceReference getReference() {
                                return reference;
                            }

                            public String toString() {
                                return //$NON-NLS-1$ //$NON-NLS-2$
                                "RemoteServiceRegisteredEvent(" + containerID + "," + //$NON-NLS-1$ //$NON-NLS-2$
                                getReference();
                            }

                            public ID getLocalContainerID() {
                                return getID();
                            }
                        });
                        return;
                    case RemoteServiceEvent.UNREGISTERING:
                        listener.handleServiceEvent(new IRemoteServiceUnregisteredEvent() {

                            IRemoteServiceReference reference = new RemoteServiceReferenceImpl(createRemoteServiceID(event.getRemoteReference()), event.getRemoteReference());

                            public String[] getClazzes() {
                                return event.getRemoteReference().getServiceInterfaces();
                            }

                            public ID getContainerID() {
                                return containerID;
                            }

                            public IRemoteServiceReference getReference() {
                                return reference;
                            }

                            public String toString() {
                                return //$NON-NLS-1$ //$NON-NLS-2$
                                "RemoteServiceUnregisteredEvent(" + containerID + "," + //$NON-NLS-1$ //$NON-NLS-2$
                                getReference();
                            }

                            public ID getLocalContainerID() {
                                return getID();
                            }
                        });
                        return;
                }
            }
        };
        // register the listener as a service (whiteboard pattern)
        final ServiceRegistration reg = context.registerService(RemoteServiceListener.class.getName(), l, null);
        // keep track of the listener so that it can be removed when requested.
        remoteServiceListeners.put(listener, reg);
    }

    /**
	 * get a remote service by its remote service reference.
	 * 
	 * @param reference
	 *            the remote service reference.
	 * @return the IRemoteService object, encapsulating the service proxy and
	 *         additional methods for asynchronous and other access methods.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#getRemoteService(org.eclipse.ecf.remoteservice.IRemoteServiceReference)
	 */
    public IRemoteService getRemoteService(final IRemoteServiceReference reference) {
        Assert.isNotNull(reference);
        if (!(reference instanceof RemoteServiceReferenceImpl))
            return null;
        final RemoteServiceReferenceImpl impl = (RemoteServiceReferenceImpl) reference;
        Object rs = remoteService.getRemoteService(impl.getR_OSGiServiceReference());
        if (rs == null)
            return null;
        final RemoteServiceImpl rsImpl = new RemoteServiceImpl(impl, rs);
        synchronized (refToImplMap) {
            List remoteServiceImplList = (List) refToImplMap.get(reference);
            if (remoteServiceImplList == null)
                remoteServiceImplList = new ArrayList();
            remoteServiceImplList.add(rsImpl);
            refToImplMap.put(reference, remoteServiceImplList);
        }
        return rsImpl;
    }

    // <IRemoteServiceReference,List<RemoteServiceImpl>>
    private Map refToImplMap = new HashMap();

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        // r-osgi does not support the idFilter, since it does not support pub/sub
        return getRemoteServiceReferences(target, clazz, filter);
    }

    /**
	 * get remote service references.
	 * 
	 * @param idFilter
	 *            a filter that limits the results to services registered by one
	 *            of the IDs.
	 * @param clazz
	 *            the interface name of the remote service.
	 * @param filter
	 *            LDAP filter string that is matched against the service
	 *            properties.
	 * @return the matching remote service references.
	 * @throws InvalidSyntaxException
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#getRemoteServiceReferences(org.eclipse.ecf.core.identity.ID[],
	 *      java.lang.String, java.lang.String)
	 */
    public IRemoteServiceReference[] getRemoteServiceReferences(final ID[] idFilter, final String clazz, final String filter) throws InvalidSyntaxException {
        // r-osgi does not support the idFilter, since it does not support pub/sub
        IRemoteFilter remoteFilter = (filter == null) ? null : createRemoteFilter(filter);
        List results = getRemoteServiceReferencesConnected(clazz, remoteFilter);
        if (results == null)
            return null;
        return (IRemoteServiceReference[]) results.toArray(new IRemoteServiceReference[] {});
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID targetID, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        if (clazz == null)
            return null;
        IRemoteFilter remoteFilter = (filter == null) ? null : createRemoteFilter(filter);
        synchronized (this) {
            List results = new ArrayList();
            if (getConnectedID() == null)
                connect(targetID, connectContext);
            results = getRemoteServiceReferencesConnected(clazz, remoteFilter);
            if (results == null || results.size() == 0)
                return null;
            return (IRemoteServiceReference[]) results.toArray(new IRemoteServiceReference[] {});
        }
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        List results = new ArrayList();
        // Get remote service references from locally registered services first
        synchronized (remoteServicesRegs) {
            for (Iterator i1 = remoteServicesRegs.keySet().iterator(); i1.hasNext(); ) {
                ServiceReference ref = (ServiceReference) i1.next();
                Dictionary refProperties = prepareProperties(ref);
                if (clazz == null) {
                    results.add(createLocalRemoteServiceReference(ref));
                } else {
                    IRemoteFilter rf = createRemoteFilter(filter != null ? //$NON-NLS-1$ //$NON-NLS-2$
                    "(&" + filter + "(" + Constants.OBJECTCLASS + //$NON-NLS-1$//$NON-NLS-2$
                    "=" + //$NON-NLS-1$//$NON-NLS-2$
                    clazz + "))" : //$NON-NLS-1$
                    "(" + Constants.OBJECTCLASS + //$NON-NLS-1$//$NON-NLS-2$
                    "=" + //$NON-NLS-1$//$NON-NLS-2$
                    clazz + ")");
                    if (rf.match(refProperties)) {
                        results.add(createLocalRemoteServiceReference(ref));
                    }
                }
            }
        }
        IRemoteFilter remoteFilter = (filter == null) ? null : createRemoteFilter(filter);
        if (getConnectedID() != null) {
            final RemoteServiceReference[] rrefs = remoteService.getRemoteServiceReferences(connectedID.getURI(), clazz, remoteFilter);
            if (rrefs != null)
                for (int i = 0; i < rrefs.length; i++) results.add(getCachedRemoteServiceReference(rrefs[i]));
        }
        return (IRemoteServiceReference[]) results.toArray(new IRemoteServiceReference[] {});
    }

    private IRemoteServiceReference createLocalRemoteServiceReference(ServiceReference ref) {
        return new LocalRemoteServiceReferenceImpl(createRemoteServiceID(containerID, (Long) ref.getProperty(Constants.SERVICE_ID)), ref);
    }

    private synchronized List getRemoteServiceReferencesConnected(final String clazz, IRemoteFilter filter) {
        List results = new ArrayList();
        if (connectedID == null) {
            try {
                IRemoteServiceReference[] refs = getAllRemoteServiceReferences(clazz, (filter == null) ? null : filter.toString());
                if (refs == null)
                    return results;
                for (int i = 0; i < refs.length; i++) results.add(refs[i]);
            } catch (InvalidSyntaxException e) {
            }
        } else {
            RemoteServiceReference[] rrefs = remoteService.getRemoteServiceReferences(connectedID.getURI(), clazz, filter);
            if (rrefs == null)
                return results;
            for (int i = 0; i < rrefs.length; i++) results.add(getCachedRemoteServiceReference(rrefs[i]));
        }
        return results;
    }

    private Map cachedRemoteServiceReferences = new HashMap();

    private IRemoteServiceReference getCachedRemoteServiceReference(RemoteServiceReference rref) {
        IRemoteServiceReference result = null;
        synchronized (cachedRemoteServiceReferences) {
            result = (IRemoteServiceReference) cachedRemoteServiceReferences.get(rref);
            if (result == null) {
                result = new RemoteServiceReferenceImpl(createRemoteServiceID(rref), rref);
                cachedRemoteServiceReferences.put(rref, result);
            }
        }
        return result;
    }

    IRemoteServiceID createRemoteServiceID(R_OSGiID cID, Long l) {
        return (IRemoteServiceID) IDFactory.getDefault().createID(getRemoteServiceNamespace(), new Object[] { cID, l });
    }

    IRemoteServiceID createRemoteServiceID(RemoteServiceReference rref) {
        URI uri = rref.getURI();
        //$NON-NLS-1$
        String fragmentString = "#" + uri.getFragment();
        String uriStr = uri.toString();
        int fragmentLoc = uriStr.indexOf(fragmentString);
        if (fragmentLoc != -1) {
            uriStr = uriStr.substring(0, fragmentLoc);
        }
        return createRemoteServiceID(createR_OSGiID(uriStr), (Long) rref.getProperty(Constants.SERVICE_ID));
    }

    private R_OSGiID createR_OSGiID(String uriStr) {
        return R_OSGiContainerInstantiator.createR_OSGiID(getConnectNamespace(), uriStr);
    }

    /**
	 * register a service object as a remote service.
	 * 
	 * @param clazzes
	 *            the names of the service interfaces under which the service
	 *            will be registered.
	 * @param service
	 *            the service object.
	 * @param properties
	 *            the service properties.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#registerRemoteService(java.lang.String[],
	 *      java.lang.Object, java.util.Dictionary)
	 */
    public IRemoteServiceRegistration registerRemoteService(final String[] clazzes, final Object service, final Dictionary properties) {
        return registerRemoteService(clazzes, service, properties, context);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IOSGiRemoteServiceContainerAdapter#registerRemoteService(java.lang.String[], org.osgi.framework.ServiceReference, java.util.Dictionary)
	 */
    public IRemoteServiceRegistration registerRemoteService(final String[] clazzes, final ServiceReference aServiceReference, final Dictionary properties) {
        final Object service = context.getService(aServiceReference);
        final BundleContext bundleContext = aServiceReference.getBundle().getBundleContext();
        return registerRemoteService(clazzes, service, properties, bundleContext);
    }

    private String getPackageName(String className) {
        //$NON-NLS-1$
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex == -1)
            //$NON-NLS-1$
            return "";
        return className.substring(0, lastDotIndex);
    }

    private Version getPackageVersion(final Object service, String serviceInterface, String packageName) {
        List<Class> interfaces = new ArrayList<Class>();
        Class<?> serviceClass = service.getClass();
        while (!serviceClass.equals(Object.class)) {
            interfaces.addAll(Arrays.asList(serviceClass.getInterfaces()));
            serviceClass = serviceClass.getSuperclass();
        }
        Class[] interfaceClasses = interfaces.toArray(new Class[interfaces.size()]);
        if (interfaceClasses == null)
            return null;
        Class interfaceClass = null;
        for (int i = 0; i < interfaceClasses.length; i++) if (interfaceClasses[i].getName().equals(serviceInterface))
            interfaceClass = interfaceClasses[i];
        if (interfaceClass == null)
            return null;
        Bundle providingBundle = FrameworkUtil.getBundle(interfaceClass);
        if (providingBundle == null)
            return null;
        return getVersionForPackage(providingBundle, packageName);
    }

    private Version getVersionForMatchingCapability(String packageName, BundleCapability capability) {
        // If it's a package namespace (Import-Package)
        Map<String, Object> attributes = capability.getAttributes();
        // Then we get the package attribute
        String p = (String) attributes.get(BundleRevision.PACKAGE_NAMESPACE);
        // And compare it to the package name
        if (p != null && packageName.equals(p))
            return (Version) attributes.get(Constants.VERSION_ATTRIBUTE);
        return null;
    }

    private Version getVersionForPackage(final Bundle providingBundle, String packageName) {
        Version result = null;
        BundleRevision providingBundleRevision = AccessController.doPrivileged(new PrivilegedAction<BundleRevision>() {

            public BundleRevision run() {
                return providingBundle.adapt(BundleRevision.class);
            }
        });
        if (providingBundleRevision == null)
            return null;
        List<BundleCapability> providerCapabilities = providingBundleRevision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
        for (BundleCapability c : providerCapabilities) {
            result = getVersionForMatchingCapability(packageName, c);
            if (result != null)
                return result;
        }
        return result;
    }

    private IRemoteServiceRegistration registerRemoteService(final String[] clazzes, final Object service, final Dictionary properties, final BundleContext aContext) {
        if (containerID == null) {
            //$NON-NLS-1$
            throw new IllegalStateException("Container is not connected");
        }
        final Dictionary props = properties == null ? new Hashtable() : clone(properties);
        R_OSGiNamespace ns = (R_OSGiNamespace) containerID.getNamespace();
        // add the hint property for R-OSGi that this service is intended to be
        // accessed remotely.
        Object rosgiRegistrationValue = Boolean.TRUE;
        if (!exposeRemoteServicesOnAllTransports)
            //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            rosgiRegistrationValue = (ns instanceof R_OSGiWSSNamespace) ? "https" : (ns instanceof R_OSGiWSNamespace) ? "http" : "r-osgi";
        props.put(RemoteOSGiService.R_OSGi_REGISTRATION, rosgiRegistrationValue);
        // remove the RFC 119 hint, if present, to avoid loops
        //$NON-NLS-1$
        props.remove("osgi.remote.interfaces");
        //$NON-NLS-1$
        props.remove("service.exported.interfaces");
        // ECF remote service properties
        // container ID (ID)
        props.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_CONTAINER_ID, containerID);
        // Object classes (String [])
        props.put(org.eclipse.ecf.remoteservice.Constants.OBJECTCLASS, clazzes);
        // service ranking (Integer).  Allow this to be set by user
        Integer serviceRanking = (Integer) props.get(org.eclipse.ecf.remoteservice.Constants.SERVICE_RANKING);
        serviceRanking = (serviceRanking == null) ? new Integer(0) : serviceRanking;
        props.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_RANKING, serviceRanking);
        for (String clazz : clazzes) {
            // Add osgi-standard remote service version for each package
            String packageName = getPackageName(clazz);
            //$NON-NLS-1$
            String packageVersionKey = "endpoint.package.version." + packageName;
            Version packageVersion = getPackageVersion(service, clazz, packageName);
            if (packageVersion != null) {
                props.put(packageVersionKey, packageVersion.toString());
            }
        }
        final ServiceRegistration reg = aContext.registerService(clazzes, service, props);
        // Set ECF remote service id property based upon local service property
        reg.setProperties(prepareProperties(reg.getReference()));
        synchronized (remoteServicesRegs) {
            remoteServicesRegs.put(reg.getReference(), reg);
        }
        // Construct a IRemoteServiceID, and provide to new registration impl instance
        return new RemoteServiceRegistrationImpl(this, createRemoteServiceID(containerID, (Long) reg.getReference().getProperty(Constants.SERVICE_ID)), reg);
    }

    Dictionary prepareProperties(ServiceReference reference) {
        String[] propKeys = reference.getPropertyKeys();
        Dictionary newDictionary = new Properties();
        for (int i = 0; i < propKeys.length; i++) {
            Object v = reference.getProperty(propKeys[i]);
            newDictionary.put(propKeys[i], v);
            // Make the remote service SERVICE_ID have the same value as OSGi SERVICE_ID
            if (Constants.SERVICE_ID.equals(propKeys[i])) {
                newDictionary.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, v);
            }
        }
        return newDictionary;
    }

    /**
	 * remove a registered remote service listener.
	 * 
	 * @param listener
	 *            the remote service listener.
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#removeRemoteServiceListener(org.eclipse.ecf.remoteservice.IRemoteServiceListener)
	 */
    public void removeRemoteServiceListener(final IRemoteServiceListener listener) {
        remoteServiceListeners.remove(listener);
    }

    /**
	 * 
	 * @see org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter#ungetRemoteService(org.eclipse.ecf.remoteservice.IRemoteServiceReference)
	 */
    public boolean ungetRemoteService(IRemoteServiceReference reference) {
        if (!(reference instanceof RemoteServiceReferenceImpl))
            return false;
        RemoteServiceReferenceImpl impl = (RemoteServiceReferenceImpl) reference;
        RemoteServiceReference rsr = impl.getR_OSGiServiceReference();
        if (!rsr.isActive())
            return false;
        remoteService.ungetRemoteService(rsr);
        synchronized (refToImplMap) {
            List remoteServiceImplList = (List) refToImplMap.remove(reference);
            if (remoteServiceImplList != null) {
                for (Iterator i = remoteServiceImplList.iterator(); i.hasNext(); ) {
                    RemoteServiceImpl rsImpl = (RemoteServiceImpl) i.next();
                    if (rsImpl != null)
                        rsImpl.dispose();
                    i.remove();
                }
            }
        }
        return true;
    }

    /**
	 * returns an adapter for a given class. In this particular case, only the
	 * IRemoteServiceContainerAdapter interface and the IContainer interface are
	 * supported.
	 * 
	 * @param adapter
	 *            the class to adapt to.
	 * @return the adapter or null if the adaptation is not supported.
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(final Class adapter) {
        if (adapter.equals(IRemoteServiceContainerAdapter.class)) {
            return this;
        } else if (adapter.equals(IContainer.class)) {
            return this;
        }
        return null;
    }

    // container part
    /**
	 * add a container listener.
	 * 
	 * @param listener
	 *            the container listener.
	 * 
	 * @see org.eclipse.ecf.core.IContainer#addListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void addListener(final IContainerListener listener) {
        containerListeners.add(listener);
    }

    /**
	 * connect the container to a remote container instance.
	 * 
	 * @param targetID
	 *            the target ID to connect to.
	 * @param connectContext
	 *            the connection context.
	 * @throws ContainerConnectException
	 *             if the connecting fails.
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.security.IConnectContext)
	 */
    public synchronized void connect(final ID targetID, final IConnectContext cc) throws ContainerConnectException {
        if (targetID == null)
            //$NON-NLS-1$
            throw new ContainerConnectException("targetID must not be null");
        // Get and check namespace
        String targetNamespace = targetID.getNamespace().getName();
        if (!(targetNamespace.equals(getConnectNamespace().getName()) || targetNamespace.equals(StringID.class.getName()))) {
            //$NON-NLS-1$
            throw new ContainerConnectException("targetID is of incorrect connect namespace for this container");
        }
        // check that we are not connected
        if (connectedID != null) {
            // already connected to this target
            if (connectedID.getName().equals(targetID.getName())) {
                return;
            }
            //$NON-NLS-1$
            throw new ContainerConnectException("Container is already connected to " + connectedID);
        }
        this.connectContext = cc;
        try {
            final R_OSGiID target = (targetNamespace.equals(StringID.class.getName()) ? createR_OSGiID(((StringID) targetID).getName()) : ((R_OSGiID) targetID));
            fireListeners(new ContainerConnectingEvent(containerID, connectedID));
            final RemoteServiceReference[] refs = doConnect(target);
            if (refs != null)
                for (int i = 0; i < refs.length; i++) checkImport(refs[i]);
            connectedID = target;
            startRegTracker();
        } catch (IOException ioe) {
            throw new ContainerConnectException(ioe);
        } catch (IDCreateException e) {
            throw new ContainerConnectException(e);
        }
        fireListeners(new ContainerConnectedEvent(containerID, connectedID));
    }

    private RemoteServiceReference[] doConnect(R_OSGiID targetID) throws IOException {
        return remoteService.connect(targetID.getURI());
    }

    private void doDisconnect(R_OSGiID targetID) {
        synchronized (cachedRemoteServiceReferences) {
            cachedRemoteServiceReferences.clear();
        }
        remoteService.disconnect(targetID.getURI());
    }

    /**
	 * disconnect from the remote container.
	 * 
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
    public synchronized void disconnect() {
        if (connectedID != null) {
            fireListeners(new ContainerDisconnectingEvent(containerID, connectedID));
            doDisconnect(connectedID);
            connectedID = null;
            fireListeners(new ContainerDisconnectedEvent(containerID, connectedID));
        }
    }

    /**
	 * dispose the container.
	 * 
	 * @see org.eclipse.ecf.core.IContainer#dispose()
	 */
    public void dispose() {
        // unregister remote services
        if (remoteServicesTracker != null) {
            final ServiceReference[] refs = remoteServicesTracker.getServiceReferences();
            if (refs != null) {
                for (int i = 0; i < refs.length; i++) {
                    final ServiceRegistration reg = (ServiceRegistration) remoteServicesRegs.get(refs[i]);
                    if (reg != null) {
                        reg.unregister();
                    }
                }
            }
            remoteServicesTracker.close();
            remoteServicesTracker = null;
        }
        // unregister remote listeners
        final ServiceRegistration[] lstn = (ServiceRegistration[]) remoteServiceListeners.values().toArray(new ServiceRegistration[remoteServiceListeners.size()]);
        for (int i = 0; i < lstn.length; i++) {
            try {
                lstn[i].unregister();
            } catch (Throwable t) {
            }
        }
        if (connectedID != null) {
            disconnect();
        }
        synchronized (refToImplMap) {
            refToImplMap.clear();
        }
        fireListeners(new ContainerDisposeEvent(containerID));
        containerListeners.clear();
    }

    /**
	 * get the connect namespace.
	 * 
	 * @return the connect namespace.
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
    public Namespace getConnectNamespace() {
        return R_OSGiNamespace.getDefault();
    }

    /**
	 * get the ID to which the container is connected to. Can be
	 * <code>null</code> if the container is not yet connected.
	 * 
	 * @return the ID or null.
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
    public synchronized ID getConnectedID() {
        return connectedID;
    }

    /**
	 * remove a registered container listener.
	 * 
	 * @param listener
	 *            the container listener.
	 * @see org.eclipse.ecf.core.IContainer#removeListener(org.eclipse.ecf.core.IContainerListener)
	 */
    public void removeListener(final IContainerListener listener) {
        containerListeners.remove(listener);
    }

    /**
	 * get the ID of this container instance.
	 * 
	 * @return the ID of this container.
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
    public ID getID() {
        return containerID;
    }

    /**
	 * fire the listeners.
	 * 
	 * @param event
	 *            the event.
	 */
    private void fireListeners(final IContainerEvent event) {
        final IContainerListener[] listeners = (IContainerListener[]) containerListeners.toArray(new IContainerListener[containerListeners.size()]);
        new Thread() {

            public void run() {
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].handleEvent(event);
                }
            }
        }.start();
    }

    /**
	 * get the events from R-OSGi received through a RemoteServiceListener
	 * instance.
	 * 
	 * @see ch.ethz.iks.r_osgi.RemoteServiceListener#remoteServiceEvent(ch.ethz.iks.r_osgi.RemoteServiceEvent)
	 */
    public void remoteServiceEvent(final RemoteServiceEvent event) {
        if (event.getType() == RemoteServiceEvent.REGISTERED) {
            checkImport(event.getRemoteReference());
        }
    }

    /**
	 * check if the remote service should be automatically imported by this
	 * container.
	 * 
	 * @param ref
	 *            the remote service reference to check.
	 */
    private void checkImport(final RemoteServiceReference ref) {
        final Object target = ref.getProperty(org.eclipse.ecf.remoteservice.Constants.SERVICE_REGISTRATION_TARGETS);
        if (target instanceof ID && ((ID) target).equals(containerID)) {
            remoteService.getRemoteService(ref);
        } else if (target instanceof ID[]) {
            final ID[] targets = (ID[]) target;
            for (int i = 0; i < targets.length; i++) {
                if (targets[i].equals(containerID)) {
                    remoteService.getRemoteService(ref);
                }
            }
        }
    }

    /**
	 * Clone a dictionary instance to avoid modification by the caller of the
	 * registration method.
	 * 
	 * @param props
	 *            the dictionary instance.
	 * @return a clone.
	 */
    private Hashtable clone(final Dictionary props) {
        final Hashtable clone = new Hashtable();
        for (Enumeration e = props.keys(); e.hasMoreElements(); ) {
            final Object key = e.nextElement();
            clone.put(key, props.get(key));
        }
        return clone;
    }

    public IFuture asyncGetRemoteServiceReferences(final ID[] idFilter, final String clazz, final String filter) {
        IExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(idFilter, clazz, filter);
            }
        }, null);
    }

    public IFuture asyncGetRemoteServiceReferences(final ID target, final String clazz, final String filter) {
        IExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(target, clazz, filter);
            }
        }, null);
    }

    public IFuture asyncGetRemoteServiceReferences(final ID target, final ID[] idFilter, final String clazz, final String filter) {
        IExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(target, idFilter, clazz, filter);
            }
        }, null);
    }

    public Namespace getRemoteServiceNamespace() {
        return IDFactory.getDefault().getNamespaceByName(R_OSGiRemoteServiceNamespace.NAME);
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        return new RemoteFilterImpl(context, filter);
    }

    public IRemoteServiceID getRemoteServiceID(ID containerId, long containerRelativeId) {
        return (IRemoteServiceID) IDFactory.getDefault().createID(getRemoteServiceNamespace(), new Object[] { containerID, new Long(containerRelativeId) });
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceId) {
        if (serviceId == null)
            return null;
        ID cID = serviceId.getContainerID();
        // If the container ID isn't relevant to us we ignore
        if (cID instanceof R_OSGiID) {
            // If it's not the same as who we're connected to, we ignore
            if (cID.equals(getConnectedID())) {
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                final String filter = "(" + Constants.SERVICE_ID + "=" + serviceId + ")";
                try {
                    // Get remote service references...I imagine this can/would block
                    RemoteServiceReference[] refs = remoteService.getRemoteServiceReferences(((R_OSGiID) cID).getURI(), null, context.createFilter(filter));
                    // There should be either zero or 1 remote service reference
                    if (refs == null || refs.length == 0)
                        return null;
                    return getCachedRemoteServiceReference(refs[0]);
                } catch (InvalidSyntaxException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        // XXX...we need to see if r-OSGi has a means to implement this
        return false;
    }

    void removeRegistration(ServiceRegistration reg) {
        synchronized (remoteServicesRegs) {
            remoteServicesRegs.remove(reg.getReference());
        }
    }
}
