/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp;

import java.net.InetAddress;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

/**
 * Advertiser implements the SA properties of SLP. Services can be registered
 * and deregistered. The SLP framework handles DA discovery.
 * 
 * @author Jan S. Rellermeyer, Systems Group, ETH Zurich
 * @since 0.1
 */
public interface Advertiser {

    /**
	 * Returns the locale of this Advertiser instance.
	 * 
	 * @return the current Locale.
	 */
    Locale getLocale();

    /**
	 * Get the locale of this instance.
	 * 
	 * @param locale
	 *            the Locale.
	 * @see Advertiser#getLocale()
	 */
    void setLocale(final Locale locale);

    /**
	 * Register a service with the SLP framework. The service will be registered
	 * with all known DAs that support the default scope and with the local SA
	 * registry for multicast discovery.
	 * 
	 * @param url
	 *            the <code>ServiceURL</code> of the service.
	 * @param attributes
	 *            a <code>Dictionary</code> of attributes for the service. RFC
	 *            2614 proposes a <code>Vector</code> of attribute-value-pairs
	 *            here but the <code>Dictionary</code> makes this
	 *            implementation more close to <code>OSGi</code>.
	 * @throws ServiceLocationException
	 *             in case that the registration failed for any reason.
	 */
    void register(ServiceURL url, Dictionary attributes) throws ServiceLocationException;

    /**
	 * Register a service with the SLP framework. The service will be registered
	 * with all known DAs that support at least one of the given scopes and with
	 * the local SA registry for multicast discovery.
	 * 
	 * @param url
	 *            the ServiceURL of the service.
	 * @param scopes
	 *            a <code>List</code> of scope names as <code>Strings</code>.
	 * @param attributes
	 *            a <code>Dictionary</code> of attributes for the service. RFC
	 *            2614 proposes a <code>Vector</code> of attribute-value-pairs
	 *            here but the <code>Dictionary</code> makes this
	 *            implementation more close to <code>OSGi</code>
	 * @throws ServiceLocationException
	 *             in case that the registration failed for any reason.
	 */
    void register(ServiceURL url, List scopes, Dictionary attributes) throws ServiceLocationException;

    /**
	 * Unregister a service with the SLP framework. The service will be
	 * unregistered with all known DAs in the scopes that it was registered in.
	 * 
	 * @param url
	 *            the <code>ServiceURL</code> of the service.
	 * @throws ServiceLocationException
	 *             in case that the deregistration failed for any reason.
	 */
    void deregister(ServiceURL url) throws ServiceLocationException;

    /**
	 * deregister a service in some scopes.
	 * 
	 * @param url
	 *            the ServiceURL of the service.
	 * @param scopes
	 *            the scopes.
	 * @throws ServiceLocationException
	 *             if the deregistration has failed for any reason.
	 * @see Advertiser#deregister(ServiceURL, List)
	 * @since 0.7.1
	 */
    void deregister(final ServiceURL url, final List scopes) throws ServiceLocationException;

    /**
	 * <b>Not yet implemented.</b> Add attributes to an already registered
	 * service. Allows incremental registration.
	 * 
	 * @param url
	 *            the <code>ServiceURL</code> of the service.
	 * @param attributes
	 *            the attributes to be added.
	 * @throws ServiceLocationException
	 *             whenever called.
	 */
    void addAttributes(ServiceURL url, Dictionary attributes) throws ServiceLocationException;

    /**
	 * <b>Not yet implemented.</b> Remove attributes to an already registered
	 * service. Allows incremental registration.
	 * 
	 * @param url
	 *            the <code>ServiceURL</code> of the service.
	 * @param attributeIds
	 *            the attributes to be removed.
	 * @throws ServiceLocationException
	 *             whenever called.
	 */
    void deleteAttributes(ServiceURL url, Dictionary attributeIds) throws ServiceLocationException;

    /**
	 * Get the IP address of this machine that is configured as primary jSLP
	 * address. Can be used to register Services that are located on this host.
	 * 
	 * @return the local InetAddress.
	 */
    InetAddress getMyIP();
}
