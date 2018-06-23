/****************************************************************************
 * Copyright (c) 2004-2011 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

/**
 * Remote service API constants.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 8.9
 */
public interface Constants {

    /**
	 * Remote service property identifying all (named &quot;ecf.robjectClass&quot;) 
	 * of the class names under which a service was registered in the remote
	 * services API (of type <code>java.lang.String[]</code>).
	 * 
	 * <p>
	 * This property is set by the remote services API when a service is
	 * registered.
	 */
    //$NON-NLS-1$
    public static final String OBJECTCLASS = "ecf.robjectClass";

    /**
	 * Remote service property (named &quot;ecf.rsvc.id&quot;) identifying a
	 * service's remote registration number (of type <code>java.lang.Long</code>).
	 * 
	 * <p>
	 * The value of this property is assigned by the remote services API when a
	 * service is registered. The remote services API assigns a unique value
	 * that is larger than all previously assigned values since the remote
	 * services API was started. These values are NOT persistent across restarts
	 * of the remote services API.
	 */
    //$NON-NLS-1$
    public static final String SERVICE_ID = "ecf.rsvc.id";

    /**
	 * Service property (named &quot;ecf.rsvc.ranking&quot;) identifying a
	 * service's ranking number (of type <code>java.lang.Integer</code>).
	 * 
	 * <p>
	 * This property may be supplied in the <code>properties
	 * Dictionary</code>
	 * object passed to the
	 * <code>IRemoteServiceContainerAdapter.registerRemoteService</code>
	 * method.
	 * 
	 * <p>
	 * The service ranking is used by the remote services API to determine the
	 * <i>default </i> service to be returned from a call to the
	 * {@link IRemoteServiceContainerAdapter#getRemoteServiceReferences(org.eclipse.ecf.core.identity.ID[], String, String)}
	 * method: If more than one service implements the specified class, the
	 * <code>RemoteServiceReference</code> object with the highest ranking is
	 * returned.
	 * 
	 * <p>
	 * The default ranking is zero (0). A service with a ranking of
	 * <code>Integer.MAX_VALUE</code> is very likely to be returned as the
	 * default service, whereas a service with a ranking of
	 * <code>Integer.MIN_VALUE</code> is very unlikely to be returned.
	 * 
	 * <p>
	 * If the supplied property value is not of type
	 * <code>java.lang.Integer</code>, it is deemed to have a ranking value
	 * of zero.
	 */
    //$NON-NLS-1$
    public static final String SERVICE_RANKING = "ecf.rsvc.ranking";

    /**
	 * Service property (named &quot;remoteservice.description&quot;)
	 * identifying a a registration's target for receiving the service. The
	 * value of the property MUST be either a non-<code>null</code> instance
	 * of org.eclipse.ecf.core.identity.ID OR an ID[].
	 * <p>
	 * This property may be supplied in the properties <code>Dictionary</code>
	 * object passed to the
	 * <code>IRemoteServiceContainerAdapter.registerRemoteService</code>
	 * method.
	 */
    //$NON-NLS-1$
    public static final String SERVICE_REGISTRATION_TARGETS = "ecf.rsvc.reg.targets";

    /**
	 * Remote service property that defines the remote service container ID.
	 * <p>
	 * This property may be supplied in the properties <code>Dictionary</code>
	 * object passed to the <code>BundleContext.registerService</code> method.
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String SERVICE_CONTAINER_ID = "ecf.rsvc.cid";

    /**
	 * Remote service property used on service registration to indicate that a service proxy 
	 * should be created rather than using the given service object (which may be null
	 * when this service property is set).
	 * @since 4.0
	 */
    //$NON-NLS-1$
    public static final String SERVICE_REGISTER_PROXY = "ecf.rsvc.proxy";

    /**
	 * Remote service property used to prevent the automatic addition of the IRemoteServiceProxy
	 * interface to the proxy returned from IRemoteService.getProxy.  If this service
	 * property is set (to any non-null Object value), it will prevent the the IRemoteServiceProxy from
	 * being added to the set of interfaces exposed by the proxy returned from IRemoteService.getProxy.
	 * @since 6.0
	 */
    //$NON-NLS-1$
    public static final String SERVICE_PREVENT_RSPROXY = "ecf.rsvc.norsproxy";

    /**
	 * This constant allows the fully qualified async remote service proxy to be specified
	 * as a service property.  For example, if the remote service interface is as so:
	 * <pre>
	 * package foo;
	 * 
	 * public interface Bar {
	 *    String doStuff();
	 * }
	 * </pre>
	 * then by default, the async remote service proxy interface class would be expected 
	 * to be the following:
	 * <pre>
	 * package foo;
	 * import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
	 * import org.eclipse.equinox.concurrent.future.IFuture;
	 *
	 * public interface BarAsync extends IAsyncRemoteServiceProxy {
	 *    IFuture doStuffAsync();
	 * }
	 * </pre>
	 * This property allows a new class to be associated with the
	 * original service interface, so that rather than looking for the foo.BarAsync class
	 * when a proxy is created, the class specified by the value of the property will
	 * be used instead.  For example, assume the existance of another async
	 * remote service interface:
	 * <pre>
	 * package gogo;
	 * import org.eclipse.ecf.remoteservice.IAsyncRemoteServiceProxy;
	 * import org.eclipse.equinox.concurrent.future.IFuture;
	 * 
	 * public interface MyBar extends IAsyncRemoteServiceProxy {
	 *    IFuture doStuffAsync();
	 * }
	 * </pre>
	 * Further assume that when the remote service was registered, that a service property 
	 * was specified:
	 * <pre>
	 * serviceProps.put("ecf.rsvc.async.proxy_&lt;fq classname&gt;","&lt;fq substitute&gt;");
	 * </pre>
	 * <pre>
	 * serviceProps.put("ecf.rsvc.async.proxy_foo.Bar","gogo.MyBar");
	 * </pre>
	 * Then, when a Bar proxy is created, if the 'gogo.MyBar' interface is available on 
	 * the client, an async remote service proxy will be added to the proxy, and
	 * client will be able to asynchronously call MyBar.doStuffAsync() on the proxy.
	 */
    //$NON-NLS-1$
    public static final String SERVICE_ASYNC_RSPROXY_CLASS_ = "ecf.rsvc.async.proxy_";

    /**
	 * @since 8.3
	 */
    //$NON-NLS-1$
    public static final String SERVICE_PREVENT_ASYNCPROXY = "ecf.rsvc.async.noproxy";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_CONNECT_ID = "ecf.rsvc.cnct.id";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_CONNECT_ID_NAMESPACE = "ecf.rsvc.cnct.id.ns";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_IDFILTER_NAMESPACE = "ecf.rsvc.idfltr.ns";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_IDFILTER_ID = "ecf.rsvc.idfltr.id";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_OBJECTCLASS = "ecf.rsvc.robjectclass";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_FILTER_PROPERTY = "ecf.rsvc.fltr";

    /**
	 * @deprecated
	 */
    //$NON-NLS-1$
    public static final String SERVICE_CONTAINER_FACTORY_NAME = "ecf.rsvc.cfn";

    /**
	 * ECF discovery service type for Remote Service Admin. All ECF remote
	 * services published by Remote Service Admin advertisers should have this
	 * value as one of the entries in the list returned from
	 * IServiceTypeID#getServices().
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SERVICE_TYPE = "ecfosgirsvc";

    /**
	 * ECF discovery scope property. Value type is String+. If set, the value
	 * will be used by the IServiceInfoFactory during
	 * IServiceInfoFactory#createServiceInfo(org.eclipse.ecf.discovery.IDiscoveryAdvertiser, EndpointDescription)
	 * to create an IServiceTypeID via
	 * IServiceIDFactory#createServiceTypeID(org.eclipse.ecf.core.identity.Namespace, String[], String[], String[], String)
	 * . The scopes value determines the third parameter. If not explicitly set,
	 * the IServiceTypeID#DEFAULT_SCOPE is used.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SCOPE = "ecf.endpoint.discovery.scope";

    /**
	 * ECF discovery protocols property. Value type is String+. If set, the
	 * value will be used by the IServiceInfoFactory during
	 * IServiceInfoFactory#createServiceInfo(org.eclipse.ecf.discovery.IDiscoveryAdvertiser, EndpointDescription)
	 * to create an IServiceTypeID via
	 * IServiceIDFactory#createServiceTypeID(org.eclipse.ecf.core.identity.Namespace, String[], String[], String[], String)
	 * . The protocols value determines the fourth parameter. If not explicitly
	 * set, the IServiceTypeID#DEFAULT_PROTO is used.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_PROTOCOLS = "ecf.endpoint.discovery.protocols";

    /**
	 * ECF discovery naming authority property. Value type is String. If set,
	 * the value will be used by the IServiceInfoFactory during
	 * IServiceInfoFactory#createServiceInfo(org.eclipse.ecf.discovery.IDiscoveryAdvertiser, EndpointDescription)
	 * to create an IServiceTypeID via
	 * IServiceIDFactory#createServiceTypeID(org.eclipse.ecf.core.identity.Namespace, String[], String[], String[], String)
	 * . The protocols value determines the fifth parameter. If not explicitly
	 * set, the IServiceTypeID#DEFAULT_NA is used.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_NAMING_AUTHORITY = "ecf.endpoint.discovery.namingauthority";

    /**
	 * ECF discovery service name property. Value type is String. If set, the
	 * value will be used by the IServiceInfoFactory during
	 * IServiceInfoFactory#createServiceInfo(org.eclipse.ecf.discovery.IDiscoveryAdvertiser, EndpointDescription)
	 * to create an IServiceInfo with a given name. The default is a
	 * globally unique identifier. Note that if this value is explicitly set,
	 * care should be taken to not have the name conflict with other remote
	 * service names.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SERVICE_NAME = "ecf.endpoint.discovery.servicename";

    /**
	 * @since 8.10
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SERVICE_TTL = "ecf.endpoint.discovery.ttl";

    /**
	 * @since 8.10
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SERVICE_PRIORITY = "ecf.endpoint.discovery.priority";

    /**
	 * @since 8.10
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_SERVICE_WEIGHT = "ecf.endpoint.discovery.weight";

    /**
	 * ECF service name default prefix. If the DISCOVERY_SERVICE_NAME is
	 * <b>not</b> set, this prefix will be the precede the unique identifier.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String DISCOVERY_DEFAULT_SERVICE_NAME_PREFIX = "osgirsvc_";

    /**
	 * ECF EndpointDescription property (with value of type String) that
	 * defines the unique org.eclipse.ecf.core.identity.Namespace name.
	 * If present in the EndpointDescription, the value will be used to
	 * create the containerID for accessing a remote service. The Namespace name
	 * is optional because typically the ID protocol specifier (e.g. 'ecftcp' in
	 * ID with name: 'ecftcp://localhost:3282/server') can be used to
	 * unambiguously determine the appropriate
	 * org.eclipse.ecf.core.identity.Namespace used to create the
	 * container ID for remote service import.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_CONTAINER_ID_NAMESPACE = "ecf.endpoint.id.ns";

    /**
	 * ECF EndpointDescription property (with value of type String) that
	 * defines the ecf endpoint id (typically the container id).
	 * 
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_ID = "ecf.endpoint.id";

    /**
	 * ECF EndpointDescription property (with value of type Long) that
	 * defines a service timestamp set upon initial export of the remote
	 * service.
	 * 
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_TIMESTAMP = "ecf.endpoint.ts";

    /**
	 * Optional ECF EndpointDescription property (with value of type
	 * String) that defines a connect target ID. If set/non-<code>null</code>,
	 * this property can be used by remote service consumers to connect to a
	 * specific container, and access a remote service exposed by some
	 * <b>other</b> member of the group.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_CONNECTTARGET_ID = "ecf.endpoint.connecttarget.id";

    /**
	 * Optional ECF EndpointDescription property (with value of type
	 * String+) that defines one or more IDs used for filtering remote service
	 * references during
	 * RemoteServiceAdmin#importService(org.osgi.service.remoteserviceadmin.EndpointDescription)
	 * .
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_IDFILTER_IDS = "ecf.endpoint.idfilter.ids";

    /**
	 * Optional ECF EndpointDescription property (with value of type
	 * String), that defines a remote services properties filter used during
	 * RemoteServiceAdmin#importService(org.osgi.service.remoteserviceadmin.EndpointDescription)
	 * .
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String ENDPOINT_REMOTESERVICE_FILTER = "ecf.endpoint.rsfilter";

    /**
	 * Container factory arguments for exported remote service hosts. If
	 * specified as a service property upon remote service registration, this
	 * property allows ECF containers to be initialized and configured upon
	 * creation during the call to
	 * HostContainerSelector#selectHostContainers(org.osgi.framework.ServiceReference, Map, String[], String[], String[])
	 * . The type of the value may be String, ID, or Object[]. The
	 * IContainerFactory.createContainer method is then selected based upon the
	 * type of the value...i.e.
	 * IContainerFactory#createContainer(org.eclipse.ecf.core.ContainerTypeDescription, String)
	 * ,
	 * IContainerFactory#createContainer(org.eclipse.ecf.core.identity.ID)
	 * , or IContainerFactory#createContainer(String, Object[]), and the
	 * value is passed in for container creation.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_CONTAINER_FACTORY_ARGS = "ecf.exported.containerfactoryargs";

    /**
	 * Container connect context for exported remote service hosts. If specified
	 * as a service property for remote service export, this property allows ECF
	 * containers to have given a connect context for authentication upon
	 * container connection by
	 * HostContainerSelector#selectHostContainers(org.osgi.framework.ServiceReference, Map, String[], String[], String[])
	 * . The type of the value is IConnectContext.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_CONTAINER_CONNECT_CONTEXT = "ecf.exported.containerconnectcontext";

    /**
	 * Container ID of the target host container for remote service export. If
	 * specified as a service property for remote service export, this property
	 * is used to match against the set of available containers in
	 * HostContainerSelector#selectHostContainers(org.osgi.framework.ServiceReference, Map, String[], String[], String[])
	 * . The type of the value is ID.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_CONTAINER_ID = "ecf.exported.containerid";

    /**
	 * Service property marking the service for async proxy export. It defines
	 * the async interfaces under which this service will be exported on the
	 * remote proxy. This list must be a subset of the types service was
	 * exported (i.e. subset of interfaces specified by #
	 * org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_INTERFACES
	 * . The single value of an asterisk (&quot;*&quot;, &#92;u002A) indicates
	 * all the interface types under which the service was exported.
	 * <p>
	 * The interfaces in the String[] can either be
	 * <ol>
	 * <li>The same fully qualified name as an interface in the #
	 * org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_EXPORTED_INTERFACES
	 * property</li>
	 * <li>The fully qualified name of an interface that follows the
	 * asynchronous proxy conventions to match with one of the existing exported
	 * types.</li>
	 * </ol>
	 * <p>
	 * This property may be supplied in the properties
	 * Dictionary object passed to the
	 * BundleContext.registerService method. The value of this property
	 * must be of type String, String[], or Collection
	 * of String.
	 * @since 8.9
	 * 
	 */
    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_ASYNC_INTERFACES = "ecf.exported.async.interfaces";

    /**
	 * Allows exporting ECF containers to determine the type of value associated
	 * with the
	 * org.osgi.service.remoteserviceadmin.RemoteConstants#SERVICE_IMPORTED
	 * property on the OSGi remote service consumer. For ECF, the default value
	 * type is IRemoteService. If set to some other value (e.g.
	 * Boolean by the exporting host container, then consumers can use
	 * the SERVICE_IMPORTED value appropriately.
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String SERVICE_IMPORTED_VALUETYPE = "ecf.service.imported.valuetype";

    /**
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String OSGI_ENDPOINT_MODIFIED = "ecf.osgi.endpoint.modified";

    /**
	 * @since 8.9
	 */
    //$NON-NLS-1$
    public static final String OSGI_CONTAINER_ID_NS = "ecf.osgi.ns";
}
