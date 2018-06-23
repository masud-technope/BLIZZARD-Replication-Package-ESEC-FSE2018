/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.riena.identity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;

public class RienaID extends BaseID {

    private static final long serialVersionUID = -439251125954095550L;

    private String url;

    private int hc;

    public  RienaID(RienaNamespace ns, String url) {
        super(ns);
        Assert.isNotNull(url);
        if (url.startsWith(RienaNamespace.SCHEME + RienaNamespace.SCHEME_SEPARATOR)) {
            this.url = url.substring((RienaNamespace.SCHEME + RienaNamespace.SCHEME_SEPARATOR).length());
        } else
            this.url = url;
        this.hc = (RienaNamespace.SCHEME + RienaNamespace.SCHEME_SEPARATOR + this.url).hashCode();
    }

    protected int namespaceCompareTo(BaseID o) {
        if (o == null)
            return Integer.MIN_VALUE;
        if (!(o instanceof RienaID))
            return Integer.MIN_VALUE;
        return this.url.compareTo(((RienaID) o).url);
    }

    protected boolean namespaceEquals(BaseID o) {
        if (o == null)
            return false;
        if (!(o instanceof RienaID))
            return false;
        return this.url.equals(((RienaID) o).url);
    }

    protected String namespaceGetName() {
        return RienaNamespace.NAME + RienaNamespace.SCHEME_SEPARATOR + this.url;
    }

    protected int namespaceHashCode() {
        return hc;
    }

    public String getURL() {
        return url;
    }
}
