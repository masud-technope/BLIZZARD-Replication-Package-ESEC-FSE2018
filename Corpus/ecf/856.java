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

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.ServiceURL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import org.eclipse.ecf.core.util.Trace;

public class NullPatternAdvertiser implements Advertiser {

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#addAttributes(ch.ethz.iks.slp.ServiceURL, java.util.Dictionary)
	 */
    public void addAttributes(final ServiceURL url, final Dictionary attributes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "addAttributes(ServiceURL, Dictionary)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#deleteAttributes(ch.ethz.iks.slp.ServiceURL, java.util.Dictionary)
	 */
    public void deleteAttributes(final ServiceURL url, final Dictionary attributeIds) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "deleteAttributes(ServiceURL, Dictionary)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#deregister(ch.ethz.iks.slp.ServiceURL)
	 */
    public void deregister(final ServiceURL url) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "deregister(ServiceURL)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#deregister(ch.ethz.iks.slp.ServiceURL, java.util.List)
	 */
    public void deregister(final ServiceURL url, final List scopes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "deregister(ServiceURL, List)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#getLocale()
	 */
    public Locale getLocale() {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getLocale()", Advertiser.class + " not present");
        return Locale.getDefault();
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#getMyIP()
	 */
    public InetAddress getMyIP() {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getMyIP()", Advertiser.class + " not present");
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            Trace.catching(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "getMyIP()", e);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#register(ch.ethz.iks.slp.ServiceURL, java.util.Dictionary)
	 */
    public void register(final ServiceURL url, final Dictionary attributes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "register(ServiceURL, Dictionary)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#register(ch.ethz.iks.slp.ServiceURL, java.util.List, java.util.Dictionary)
	 */
    public void register(final ServiceURL url, final List scopes, final Dictionary attributes) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "register(ServiceURL, List, Dictionary)", Advertiser.class + " not present");
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.Advertiser#setLocale(java.util.Locale)
	 */
    public void setLocale(final Locale locale) {
        //$NON-NLS-1$//$NON-NLS-2$
        Trace.trace(Activator.PLUGIN_ID, JSLPDebugOptions.METHODS_TRACING, getClass(), "setLocale(Locale)", Advertiser.class + " not present");
    }
}
