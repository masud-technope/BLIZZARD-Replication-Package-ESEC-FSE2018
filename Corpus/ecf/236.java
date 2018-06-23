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

import java.util.List;
import java.util.Locale;

/**
 * Locator implements the UA properties of SLP. Services can be discovered by
 * type or by URL, attributes of discovered services can be retrieved and
 * service types can be listed.
 * 
 * @author Jan S. Rellermeyer, Systems Group, ETH Zurich
 * @since 0.1
 */
public interface Locator {

    /**
	 * Returns the locale of this Locator instance.
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
	 * Find all services types that are currently
	 * registered in the network.
	 * 
	 * @param namingAuthority
	 *            the naming authority for the service type. If omitted,
	 *            ALL Service Types are returned, regardless of Naming Authority.
	 *            With the empty <code>String</code> (""), <code>IANA</code> will be assumed.
	 * @param scopes
	 *            a <code>List</code> of scopes in that service types are to
	 *            be discovered.
	 * @return a ServiceLocationEnumeration over the discovered ServiceTypes.
	 * @throws ServiceLocationException
	 *             whenever called.
	 */
    ServiceLocationEnumeration findServiceTypes(String namingAuthority, List scopes) throws ServiceLocationException;

    /**
	 * Find all services that match a certain service type.
	 * 
	 * @param type
	 *            the ServiceType.
	 * @param scopes
	 *            A <code>List</code> of scope <code>Strings</code>, RFC
	 *            2614 uses <code>Vector</code> here but jSLP prefers the
	 *            Collection Framework.
	 * @param searchFilter
	 *            an RFC 1960 compliant <code>String</code> of a LDAP filter.
	 *            RFC 2614 proposes the newer RFC 2254 style filters that adds
	 *            support for extensible matches.
	 * @return a ServiceLocationEnumeration over the <code>ServiceURLs</code>
	 *         of the found services.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 * @throws InvalidSyntaxException 
	 */
    ServiceLocationEnumeration findServices(ServiceType type, List scopes, String searchFilter) throws ServiceLocationException, IllegalArgumentException;

    /**
	 * Find all services that match a ServiceURL.
	 * 
	 * @param url
	 *            the ServiceURL.
	 * @param scopes
	 *            A <code>List</code> of scopes <code>Strings</code>, RFC
	 *            2614 uses <code>Vector</code> here but jSLP prefers the
	 *            Collection Framework.
	 * @param attributeIds
	 *            A List of attribute-value-pairs like
	 * 
	 * <pre>
	 * (key = value)
	 * </pre>
	 * 
	 * that must match. If null, no attribute constraints are applied.
	 * @return a ServiceLocationEnumeration over the <code>ServiceURLs</code>
	 *         of the found services.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
    ServiceLocationEnumeration findAttributes(ServiceURL url, List scopes, List attributeIds) throws ServiceLocationException;

    /**
	 * Find all services that match a ServiceType.
	 * 
	 * @param type
	 *            the ServiceType.
	 * @param scopes
	 *            A <code>List</code> of scope <code>Strings</code>, RFC
	 *            2614 uses <code>Vector</code> here but jSLP prefers the
	 *            Collection Framework.
	 * @param attributeIds
	 *            A List of attribute-value-pairs like
	 * 
	 * <pre>
	 * (key = value)
	 * </pre>
	 * 
	 * that must match. If null, no attribute constraints are applied.
	 * @return a ServiceLocationEnumeration over the ServiceURLs of the found
	 *         services.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
    ServiceLocationEnumeration findAttributes(ServiceType type, List scopes, List attributeIds) throws ServiceLocationException;
}
