/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi;

import java.io.IOException;
import org.osgi.framework.Filter;
import ch.ethz.iks.r_osgi.channels.ChannelEndpointManager;

/**
 * <p>
 * RemoteOSGiService provides transparent access to services on remote service
 * platforms. It uses SLP as underlying discovery protocol. Local services can
 * be registered for remoting, applications can register listeners for
 * <code>ServiceTypes</code> to be informed whenever matching services have been
 * discovered.
 * </p>
 * <p>
 * As soon as a service has been discovered and the listener has been informed,
 * the application can fetch the service. In the default case, the service
 * interface is transferred to the receiving peer together with an optional
 * smart proxy class and optional injections. The service then builds a proxy
 * bundle and registers it with the local framework so that the application can
 * get a service reference as if the service was local. Internally, all methods
 * of the service interface are implemented as remote method calls.
 * </p>
 * <p>
 * Services can define smart proxies to move some parts of the code to the
 * client. This is done by an abstract class. All implemented method will be
 * executed on the client, abstract methods will be implemented by remote method
 * calls. Moving parts of the code to the client can be useful for saving
 * service provider platform's resources.
 * </p>
 * <p>
 * Injections are used if the service interface uses classes as method arguments
 * that are not expected to be present on client side. These classes will be
 * automatically injected into the proxy bundle. The registrator can manually
 * inject additional classes.
 * </p>
 * <p>
 * With version 0.5, there is also the possibility to register a service with
 * the MIGRATE_BUNDLE policy. In this case, the bundle that provides the service
 * is moved to the requesting peer.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public interface RemoteOSGiService {

    // public constants for service registrations
    /**
	 * Default proxy prefix.
	 */
    String R_OSGi_PROXY_PREFIX = System.getProperty("ch.ethz.iks.r_osgi.proxyprefix", "ch.ethz.iks.r_osgi.genproxy.endpoint.");

    /**
	 * this property has to be set in order to release a service for remote
	 * access. Currently, the following two policies are supported.
	 * 
	 * @since 0.5
	 */
    //$NON-NLS-1$
    String R_OSGi_REGISTRATION = "service.remote.registration";

    /**
	 * Can be set to use a smart proxy. Smart proxies have to be abstract
	 * classes implementing the service interface. All abstract methods are
	 * implemented as remote calls, implemented methods remain untouched. This
	 * allows to perform some of the work on client side (inside of implemented
	 * methods). The value of this property in the service property dictionary
	 * has to be a the name of a class.
	 * 
	 * @since 0.5
	 */
    //$NON-NLS-1$
    String SMART_PROXY = "service.remote.smartproxy";

    /**
	 * For special purposes, the service can decide to inject other classes into
	 * the proxy bundle that is dynamically created on the client side. For
	 * instance, if types are use as arguments of method calls that are not part
	 * of the standard execution environment and the service does not want to
	 * rely on assumption that the corresponding classes are present on client
	 * side, it can inject these classes. The value of this property in the
	 * service property dictionary has to be an array of <code>Class</code>
	 * objects.
	 * 
	 * @since 0.5
	 */
    //$NON-NLS-1$
    String INJECTIONS = "service.remote.injections";

    /**
	 * property for registration of a service UI component that gived the user a
	 * presentation of the service. The value of the property in the service
	 * property dictionary has to be a name of a class implementing
	 * <code>org.service.proposition.remote.ServiceUIComponent</code>. When this
	 * property is set, the presentation is injected into the bundle and the
	 * R-OSGi ServiceUI can display the presentation when the service is
	 * discovered.
	 * 
	 * @since 0.5
	 */
    //$NON-NLS-1$
    String PRESENTATION = "service.presentation";

    /**
	 * the property key for the host name of the remote service. This constant
	 * is set by R-OSGi when a service is transferred to a remote peer. So to
	 * find out whether a service is provided by an R-OSGi proxy, check for the
	 * presence of this key in the service properties.
	 * 
	 * @since 1.0
	 */
    //$NON-NLS-1$
    String SERVICE_URI = "service.uri";

    /**
	 * connect to a remote OSGi framework. Has to be called prior to any service
	 * access. Causes the frameworks to exchange leases and start the transport
	 * of remote events.
	 * 
	 * @param endpoint
	 *            the endpoint to connect to.
	 * @return the array of remote service references of the services that the
	 *         remote frameworks offers.
	 * @throws RemoteOSGiException
	 *             in case of connection errors.
	 *             if the connection attempt fails.
	 * @throws IOException
	 * @since 0.6
	 */
    RemoteServiceReference[] connect(final URI endpoint) throws RemoteOSGiException, IOException;

    /**
	 * disconnect from a connected host.
	 * 
	 * @param endpoint
	 *            the URI of the remote host.
	 * @throws RemoteOSGiException
	 *             if something goes wrong.
	 */
    void disconnect(final URI endpoint) throws RemoteOSGiException;

    /**
	 * get a remote service reference for a given URI.
	 * 
	 * @param serviceURI
	 *            the uri of the service. Has to be a channel URI including a
	 *            fragment, which is the service ID on the other peer.
	 * @return the remote service reference, or <code>null</code>, if the
	 *         service is not present.
	 */
    RemoteServiceReference getRemoteServiceReference(final URI serviceURI);

    /**
	 * get remote service references for all services on a certain peer that
	 * match the given criteria.
	 * 
	 * @param endpointAddress
	 *            the URI of the peer.
	 * @param clazz
	 *            a service interface class, or <code>null</code> for all
	 *            services.
	 * @param filter
	 *            a filter string, or <code>null</code>
	 * @return an array of remote service references, or <code>null</code> if no
	 *         services match.
	 */
    RemoteServiceReference[] getRemoteServiceReferences(final URI endpointAddress, final String clazz, final Filter filter);

    /**
	 * get the a remote service. If there is no proxy bundle for the service so
	 * far, it is generated.
	 * 
	 * @param ref
	 *            the remote service reference.
	 * @return the service belonging to the service url or null, if no such
	 *         service is present.
	 */
    Object getRemoteService(final RemoteServiceReference ref);

    /**
	 * get a copy of the bundle that has registered the remote service.
	 * 
	 * @param ref
	 *            the remote service reference
	 * @param timeout
	 *            number of milliseconds to wait for the service to be
	 *            registered after the bundle has been started. A value of 0
	 *            means indefinite time, a negative value means don't wait at
	 *            all.
	 * @return the service object or null if the timeout is exceeded and the
	 *         service has not appeared.
	 * @throws InterruptedException 
	 */
    Object getRemoteServiceBundle(final RemoteServiceReference ref, final int timeout) throws InterruptedException;

    /**
	 * unget the service. The proxy bundle will be uninstalled. The service will
	 * be no longer available, unless it is retrieved through
	 * {@link #getRemoteService(RemoteServiceReference)} again.
	 * 
	 * @param remoteServiceReference
	 *            thre remote service reference.
	 */
    void ungetRemoteService(final RemoteServiceReference remoteServiceReference);

    /**
	 * get the endpoint manager for a channel to a given remote peer.
	 * 
	 * @param remoteEndpointAddress
	 *            the endpoint address of the remote peer.
	 * @return the endpoint manager, or <code>null</code> if no such channel
	 *         exists.
	 */
    ChannelEndpointManager getEndpointManager(final URI remoteEndpointAddress);

    /**
	 * make an asynchronous remote call to a service
	 * 
	 * @param service
	 *            the URI of the service
	 * @param methodSignature
	 *            the signature of the method to call
	 * @param args
	 *            the arguments to pass
	 * @param callback
	 *            a callback to be called when the result is available
	 */
    void asyncRemoteCall(final URI service, final String methodSignature, final Object[] args, final AsyncRemoteCallCallback callback);

    /**
	 * get the port on which the corresponding NetworkChannelFactory for the
	 * given protocol listens for incoming connections.
	 * 
	 * @param protocol
	 *            the protocol identifier string. E.g., "r-osgi" for the default
	 *            TCP-based transport
	 * @return the port number or -1 if the protocol is not supported.
	 */
    int getListeningPort(final String protocol);
}
