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
import org.eclipse.ecf.core.util.Trace;

public class NullPatternLocator implements LocatorDecorator {

    private final ServiceLocationEnumeration emptyServiceLocationEnumeration = new ServiceLocationEnumeration() {

        public Object next() throws ServiceLocationException {
            //$NON-NLS-1$
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, "no elements");
        }

        public boolean hasMoreElements() {
            return false;
        }

        public Object nextElement() {
            throw new NoSuchElementException();
        }
    };

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findAttributes(ch.ethz.iks.slp.ServiceURL, java.util.List, java.util.List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceURL url, final List scopes, final List attributeIds) {
        return emptyServiceLocationEnumeration;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findAttributes(ch.ethz.iks.slp.ServiceType, java.util.List, java.util.List)
	 */
    public ServiceLocationEnumeration findAttributes(final ServiceType type, final List scopes, final List attributeIds) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "findAttributes(ch.ethz.iks.slp.ServiceType, java.util.List, java.util.List)", Locator.class + " not present");
        return emptyServiceLocationEnumeration;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findServiceTypes(java.lang.String, java.util.List)
	 */
    public ServiceLocationEnumeration findServiceTypes(final String namingAuthority, final List scopes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "findServiceTypes(String, List)", Locator.class + " not present");
        return emptyServiceLocationEnumeration;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#findServices(ch.ethz.iks.slp.ServiceType, java.util.List, java.lang.String)
	 */
    public ServiceLocationEnumeration findServices(final ServiceType type, final List scopes, final String searchFilter) throws IllegalArgumentException {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "findServies(ServiceType, List, String)", Locator.class + " not present");
        return emptyServiceLocationEnumeration;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#getLocale()
	 */
    public Locale getLocale() {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getLocale()", Locator.class + " not present");
        return Locale.getDefault();
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Locator#setLocale(java.util.Locale)
	 */
    public void setLocale(final Locale locale) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "setLocale(Locale)", Locator.class + " not present");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs(ch.ethz.iks.slp.ServiceType, java.util.List)
	 */
    public Map getServiceURLs(final ServiceType serviceType, final List scopes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getServiceURLs(ServiceType, List scopes", Locator.class + " not present");
        return new HashMap();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs()
	 */
    public Map getServiceURLs() {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getServiceURLs()", Locator.class + " not present");
        return new HashMap();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.jslp.LocatorDecorator#getServiceURLs(java.lang.String, java.util.List)
	 */
    public List getServiceURLs(String namingAuthority, List scopes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getServiceURLs()", Locator.class + " not present");
        return new ArrayList();
    }
}
