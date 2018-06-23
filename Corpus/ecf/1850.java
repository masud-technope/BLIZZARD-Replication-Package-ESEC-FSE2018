/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.r_osgi.identity;

import ch.ethz.iks.r_osgi.URI;
import org.eclipse.ecf.core.identity.BaseID;

/**
 * @since 3.5
 */
public class R_OSGiWSID extends R_OSGiID {

    public static final int HTTPS_PORT = 443;

    public static final int HTTP_PORT = 80;

    private static final long serialVersionUID = -2801506059914687609L;

    private boolean secure;

    private String hostname;

    private int port;

    private String name;

    public  R_OSGiWSID(boolean secure, String hostname, int port) {
        super(secure ? R_OSGiWSSNamespace.getDefault() : R_OSGiWSNamespace.getDefault());
        this.secure = secure;
        this.hostname = hostname;
        this.port = port;
        //$NON-NLS-1$
        this.name = getNamespace().getScheme() + "://" + this.hostname + getPortAsString();
    }

    private String getPortAsString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return isDefaultPort() ? "" : ":" + String.valueOf(this.port);
    }

    private boolean isDefaultPort() {
        if (this.port < 0)
            return true;
        return secure ? this.port == HTTPS_PORT : this.port == HTTP_PORT;
    }

    /**
	 * compare in the context of the namespace.
	 * 
	 * @param id
	 *            another <code>BaseID</code> to compare to.
	 * @return -1 if smaller, 1 if larger, and 0 for equality.
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf.core.identity.BaseID)
	 */
    protected int namespaceCompareTo(final BaseID id) {
        return getName().compareTo(id.getName());
    }

    /**
	 * check for equality in the context of the namespace.
	 * 
	 * @param id
	 *            another <code>BaseID</code> to check with.
	 * @return <code>true</code> iff the two IDs are equal within the given
	 *         namespace.
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceEquals(org.eclipse.ecf.core.identity.BaseID)
	 */
    protected boolean namespaceEquals(final BaseID id) {
        if (id instanceof R_OSGiWSID) {
            final R_OSGiWSID other = (R_OSGiWSID) id;
            return name.equals(other.name);
        }
        return false;
    }

    /**
	 * get the internal URI.
	 * 
	 * @return the internal R-OSGi URI.
	 */
    public URI getURI() {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return URI.create((this.secure ? "https" : "http") + "://" + this.hostname + getPortAsString());
    }

    /**
	 * get the name.
	 * 
	 * @return the name as a String.
	 */
    protected String namespaceGetName() {
        return name;
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 */
    protected int namespaceHashCode() {
        return getName().hashCode();
    }

    /**
	 * get a string representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return getName();
    }

    /**
	 * @see org.eclipse.ecf.core.identity.ID#toExternalForm()
	 * @since 3.0
	 */
    public String toExternalForm() {
        return getName();
    }
}
