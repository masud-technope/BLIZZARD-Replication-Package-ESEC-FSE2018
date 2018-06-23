/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jslp;

import ch.ethz.iks.slp.ServiceURL;
import java.net.URI;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.jslp.identity.JSLPNamespace;

/**
 * Adapts jSLP's ServiceURL to an ECF's IServiceID and vice versa
 */
public class ServiceURLAdapter {

    private URI uri;

    private IServiceTypeID serviceID;

    public  ServiceURLAdapter(final ServiceURL aServiceURL) {
        this(aServiceURL, new String[0]);
    }

    public  ServiceURLAdapter(final ServiceURL aServiceURL, final String[] scopes) {
        Assert.isNotNull(aServiceURL);
        Assert.isNotNull(scopes);
        setIServiceTypeID(aServiceURL, scopes);
        setURI(aServiceURL);
    }

    private void setURI(final ServiceURL aServiceURL) {
        final StringBuffer buf = new StringBuffer();
        String protocol = aServiceURL.getProtocol();
        if (protocol == null) {
            //$NON-NLS-1$
            protocol = "unknown";
        }
        buf.append(protocol);
        //$NON-NLS-1$
        buf.append("://");
        final String userInfo = aServiceURL.getUserInfo();
        if (//$NON-NLS-1$
        !"".equals(userInfo)) {
            buf.append(userInfo);
            buf.append('@');
        }
        buf.append(aServiceURL.getHost());
        buf.append(':');
        buf.append(aServiceURL.getPort());
        buf.append(aServiceURL.getURLPath());
        uri = URI.create(buf.toString());
    }

    private void setIServiceTypeID(final ServiceURL aServiceURL, final String[] scopes) {
        final Namespace namespace = IDFactory.getDefault().getNamespaceByName(JSLPNamespace.NAME);
        serviceID = (IServiceTypeID) namespace.createInstance(new Object[] { aServiceURL, scopes });
    }

    /**
	 * @return URI
	 */
    public URI getURI() {
        return uri;
    }

    /**
	 * @return IServiceID
	 */
    public IServiceTypeID getIServiceTypeID() {
        return serviceID;
    }
}
