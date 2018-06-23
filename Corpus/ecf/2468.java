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
package ch.ethz.iks.slp.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

/**
 * Implementation of the Locator interface. If the configuration does not have
 * to provide UA functionalities, this class does not have to be included in the
 * distribution.
 * 
 * @see ch.ethz.iks.slp.Locator
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class LocatorImpl implements Locator {

    /**
	 * 
	 */
    private Locale locale;

    /**
	 * create a new LocatorImpl instance.
	 */
    public  LocatorImpl() {
        locale = SLPCore.DEFAULT_LOCALE;
    }

    /**
	 * create a new LocatorImpl instance.
	 * 
	 * @param theLocale
	 *            the Locale for this instance.
	 */
    public  LocatorImpl(final Locale locale) {
        this.locale = locale;
    }

    /**
	 * returns the locale for this instance.
	 * 
	 * @return the Locale.
	 */
    public Locale getLocale() {
        return locale;
    }

    /**
	 * Set the locale of this instance.
	 * 
	 * @param locale
	 *            the Locale.
	 * @see Advertiser#setLocale()
	 */
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    /**
	 * find the service types.
	 * 
	 * @param namingAuthority
	 *            the naming authority.
	 * @param scopes
	 *            the scopes.
	 * @return a ServiceLocationEnumeration over the results.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 * @see Locator#findServiceTypes(String, List)
	 */
    public ServiceLocationEnumeration findServiceTypes(final String namingAuthority, final List scopes) throws ServiceLocationException {
        RequestMessage srvTypeReq = new ServiceTypeRequest(namingAuthority, scopes, locale);
        return new ServiceLocationEnumerationImpl(sendRequest(srvTypeReq, scopes));
    }

    /**
	 * find services.
	 * 
	 * @param type
	 *            the service type.
	 * @param scopes
	 *            the scopes.
	 * @param searchFilter
	 *            an LDAP filter expression.
	 * @return a ServiceLocationEnumeration over the results.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 * @see Locator#findAttributes(ServiceType, List, List)
	 */
    public ServiceLocationEnumeration findServices(final ServiceType type, final List scopes, final String searchFilter) throws ServiceLocationException {
        try {
            RequestMessage srvReq = new ServiceRequest(type, scopes, searchFilter, locale);
            return new ServiceLocationEnumerationImpl(sendRequest(srvReq, scopes));
        } catch (IllegalArgumentException ise) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, ise.getMessage() + ": " + searchFilter);
        }
    }

    /**
	 * find attributes by service URL.
	 * 
	 * @param url
	 *            the ServiceURL of the service.
	 * @param scopes
	 *            a List of scoped to be included.
	 * @param attributeIds
	 *            a List of attributes for which the values should be returned,
	 *            if they exist.
	 * @return ServiceLocationEnumeration over the results.
	 * @throws ServiceLocationException
	 *             in case of network errors etc.
	 * @see Locator#findAttributes(ServiceURL, List, List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceURL url, final List scopes, final List attributeIds) throws ServiceLocationException {
        return findAttributes(new AttributeRequest(url, scopes, attributeIds, locale));
    }

    /**
	 * find attributes by service type.
	 * 
	 * @param type
	 *            the service type.
	 * @param scopes
	 *            the scopes.
	 * @param attributeIds
	 *            a List of attributes for which the values should be returned,
	 *            if they exist.
	 * @return a ServiceLocationEnumeration over the results.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 * @see Locator#findAttributes(ServiceType, List, List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceType type, final List scopes, final List attributeIds) throws ServiceLocationException {
        return findAttributes(new AttributeRequest(type, scopes, attributeIds, locale));
    }

    /**
	 * common method that handles a predefined AttributeRequest.
	 * 
	 * @param attReq
	 *            the AttributeRequest.
	 * @return the resulting Attributes as String.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 */
    private ServiceLocationEnumeration findAttributes(final AttributeRequest attReq) throws ServiceLocationException {
        return new ServiceLocationEnumerationImpl(sendRequest(attReq, attReq.scopeList));
    }

    /**
	 * send a request. Uses direct communication to a DA or multicast
	 * convergence, if no DA is known for the specific scope.
	 * 
	 * @param req
	 *            the request.
	 * @param scopeList
	 *            the scopes.
	 * @return the list of results.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 */
    private List sendRequest(final RequestMessage req, final List scopeList) throws ServiceLocationException {
        List scopes = scopeList != null ? scopeList : Arrays.asList(new String[] { "default" });
        ArrayList result = new ArrayList();
        for (Iterator scopeIter = scopes.iterator(); scopeIter.hasNext(); ) {
            String scope = (String) scopeIter.next();
            scope = scope.toLowerCase();
            List dAs = (List) SLPCore.dAs.get(scope);
            SLPCore.platform.logDebug("DAS FOR SCOPE " + scope + ": " + dAs);
            // try to find one
            if ((dAs == null || dAs.isEmpty()) && !SLPCore.noDiscovery) {
                SLPCore.daLookup(Arrays.asList(new String[] { scope }));
                // wait a short time for incoming replies
                synchronized (SLPCore.dAs) {
                    try {
                        SLPCore.dAs.wait(SLPCore.CONFIG.getWaitTime() / 4);
                    } catch (InterruptedException e) {
                        SLPCore.platform.logError(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                }
                dAs = (List) SLPCore.dAs.get(scope);
            }
            if (dAs != null && !dAs.isEmpty()) {
                // a DA is known for this scope, so contact it
                try {
                    result.addAll(sendRequestToDA(req, dAs));
                } catch (ServiceLocationException slp) {
                    result.addAll(SLPCore.multicastConvergence(req));
                }
                continue;
            } else {
                if (SLPCore.noDiscovery) {
                    throw new ServiceLocationException(ServiceLocationException.SCOPE_NOT_SUPPORTED, "Scope " + scope + " is not supported");
                }
                // still no DA available, use multicast
                result.addAll(SLPCore.multicastConvergence(req));
            }
        }
        return result;
    }

    /**
	 * send a request to a DA.
	 * 
	 * @param req
	 *            the request.
	 * @param dAaddresses
	 *            the DA address.
	 * @return the <code>List</code> of results.
	 * @throws ServiceLocationException
	 */
    private List sendRequestToDA(final RequestMessage req, final List dAaddresses) throws ServiceLocationException {
        for (int index = 0; index < dAaddresses.size(); index++) {
            ReplyMessage reply = null;
            try {
                // set the receiver address
                req.address = InetAddress.getByName((String) dAaddresses.get(index));
                req.port = SLPCore.SLP_RESERVED_PORT;
                reply = (ReplyMessage) SLPCore.sendMessage(req, true);
                if (reply.errorCode == 0) {
                    // we could receive a valid reply from the DA
                    return reply.getResult();
                }
            } catch (Exception e) {
                SLPCore.platform.logError(e.getMessage(), e.fillInStackTrace());
                final Object url = dAaddresses.get(index);
                SLPUtils.removeValueFromAll(SLPCore.dAs, url);
                SLPCore.dASPIs.remove(url);
                continue;
            }
        }
        // did not work. Return an empty result array.
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, "No DA reachable");
    }
}
