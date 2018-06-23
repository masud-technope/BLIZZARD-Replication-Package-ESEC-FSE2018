/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jslp;

import ch.ethz.iks.slp.*;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.Trace;

/**
 * This decorator add additional methods which will eventually be moved to jSLP itself
 */
public class LocatorDecoratorImpl implements LocatorDecorator {

    private final Locator locator;

    public  LocatorDecoratorImpl(final Locator aLocator) {
        Assert.isNotNull(aLocator);
        locator = aLocator;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findAttributes(ch.ethz.iks.slp.ServiceType, java.util.List, java.util.List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceType type, final List scopes, final List attributeIds) throws ServiceLocationException {
        return locator.findAttributes(type, scopes, attributeIds);
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findAttributes(ch.ethz.iks.slp.ServiceURL, java.util.List, java.util.List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceURL url, final List scopes, final List attributeIds) throws ServiceLocationException {
        return locator.findAttributes(url, scopes, attributeIds);
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findServices(ch.ethz.iks.slp.ServiceType, java.util.List, java.lang.String)
	 */
    public ServiceLocationEnumeration findServices(final ServiceType type, final List scopes, final String searchFilter) throws ServiceLocationException, IllegalArgumentException {
        return locator.findServices(type, scopes, searchFilter);
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findServiceTypes(java.lang.String, java.util.List)
	 */
    public ServiceLocationEnumeration findServiceTypes(final String namingAuthority, final List scopes) throws ServiceLocationException {
        return locator.findServiceTypes(namingAuthority, scopes);
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#getLocale()
	 */
    public Locale getLocale() {
        return locator.getLocale();
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#setLocale(java.util.Locale)
	 */
    public void setLocale(final Locale locale) {
        locator.setLocale(locale);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs(java.lang.String, java.util.List)
	 */
    public List getServiceURLs(final String namingAuthority, final List scopes) throws ServiceLocationException {
        final Enumeration stEnum = findServiceTypes(namingAuthority, scopes);
        final Set aSet = new HashSet(Collections.list(stEnum));
        final List result = new ArrayList();
        for (final Iterator itr = aSet.iterator(); itr.hasNext(); ) {
            final String type = (String) itr.next();
            try {
                final ServiceLocationEnumeration services = findServices(new ServiceType(type), scopes, null);
                while (services.hasMoreElements()) {
                    final ServiceURL url = (ServiceURL) services.next();
                    result.add(url);
                }
            } catch (IllegalArgumentException e) {
                Trace.catching(Activator.PLUGIN_ID, JSLPDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "getServiceURLs(String, List)", e);
                continue;
            }
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs(ch.ethz.iks.slp.ServiceType, java.util.List)
	 */
    public Map getServiceURLs(final ServiceType aServiceType, final List scopes) throws ServiceLocationException {
        final Map result = new HashMap();
        final ServiceLocationEnumeration services = findServices(aServiceType, scopes, null);
        while (services.hasMoreElements()) {
            final ServiceURL url = (ServiceURL) services.next();
            result.put(url, Collections.list(findAttributes(url, scopes, null)));
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs()
	 */
    public Map getServiceURLs() throws ServiceLocationException {
        final Enumeration stEnum = findServiceTypes(null, null);
        final Set aSet = new HashSet(Collections.list(stEnum));
        final Map result = new HashMap();
        for (final Iterator itr = aSet.iterator(); itr.hasNext(); ) {
            final String type = (String) itr.next();
            try {
                final ServiceLocationEnumeration services = findServices(new ServiceType(type), null, null);
                while (services.hasMoreElements()) {
                    final ServiceURL url = (ServiceURL) services.next();
                    result.put(url, Collections.list(findAttributes(url, null, null)));
                }
            } catch (IllegalArgumentException e) {
                Trace.catching(Activator.PLUGIN_ID, JSLPDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "getServiceURLs()", e);
                continue;
            }
        }
        return result;
    }
}
