/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.r_osgi.identity;

import ch.ethz.iks.r_osgi.URI;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * The ID implementation of R-OSGi URIs. Currently only works with the R-OSGi
 * namespace and hence with the R-OSGi default transport (persistent tcp
 * connections).
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public class R_OSGiID extends BaseID {

    /**
	 * the serial UID.
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * the internal URI.
	 */
    private URI uri;

    /**
	 * create a new R-OSGi ID from an URI string.
	 * 
	 * @param uriString
	 *            the URI of a remote service.
	 */
    public  R_OSGiID(final String uriString) {
        super(R_OSGiNamespace.getDefault());
        this.uri = new URI(uriString);
    }

    /**
	 * @param ns namespace
	 * @since 3.5
	 */
    protected  R_OSGiID(Namespace ns) {
        super(ns);
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
        if (id instanceof R_OSGiID) {
            final R_OSGiID other = (R_OSGiID) id;
            return uri.equals(other.uri);
        }
        return false;
    }

    /**
	 * get the internal URI.
	 * 
	 * @return the internal R-OSGi URI.
	 */
    public URI getURI() {
        return uri;
    }

    /**
	 * get the name.
	 * 
	 * @return the name as a String.
	 */
    protected String namespaceGetName() {
        return uri.toString();
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 */
    protected int namespaceHashCode() {
        return uri.hashCode();
    }

    /**
	 * get a string representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return uri.toString();
    }

    /**
	 * @see org.eclipse.ecf.core.identity.ID#toExternalForm()
	 * @since 3.0
	 */
    public String toExternalForm() {
        return uri.toString();
    }
}
