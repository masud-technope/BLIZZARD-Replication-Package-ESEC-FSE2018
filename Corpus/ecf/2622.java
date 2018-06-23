/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.r_osgi.identity;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.ecf.core.identity.*;

/**
 * @since 3.5
 */
public class R_OSGiWSSNamespace extends R_OSGiNamespace {

    private static final long serialVersionUID = 8705667690964282332L;

    //$NON-NLS-1$
    public static final String NAME_WSS = "ecf.namespace.r_osgi.wss";

    /**
	 * the namespace scheme.
	 */
    //$NON-NLS-1$
    private static final String NAMESPACE_SCHEME_WSS = "r-osgi.wss";

    /**
	 * the singleton instance of this namespace.
	 */
    private static Namespace instance;

    /**
	 * get the singleton instance of this namespace.
	 * 
	 * @return the instance.
	 */
    public static Namespace getDefault() {
        if (instance == null) {
            new R_OSGiWSSNamespace();
        }
        return instance;
    }

    /**
	 * constructor.
	 */
    public  R_OSGiWSSNamespace() {
        //$NON-NLS-1$
        super(NAME_WSS, "R-OSGi Secure Websockets Namespace");
        instance = this;
    }

    /**
	 * create a new ID within this namespace.
	 * 
	 * @param parameters
	 *            the parameter to pass to the ID.
	 * @return the new ID
	 * @throws IDCreateException
	 *             if the creation fails.
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
    public ID createInstance(final Object[] parameters) throws IDCreateException {
        try {
            String uriString = (String) parameters[0];
            if (uriString == null)
                throw new //$NON-NLS-1$
                NullPointerException(//$NON-NLS-1$
                "URI parameter is null");
            if (//$NON-NLS-1$
            !uriString.startsWith(NAMESPACE_SCHEME_WSS) && !uriString.startsWith("https"))
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new URISyntaxException(uriString, "URI must have " + NAMESPACE_SCHEME_WSS + " as protocol");
            URI uri = new URI(uriString);
            return new R_OSGiWSID(true, uri.getHost(), uri.getPort());
        } catch (Exception e) {
            throw new IDCreateException(getName() + " createInstance()", e);
        }
    }

    /**
	 * get the scheme of this namespace.
	 * 
	 * @return the scheme.
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
    public String getScheme() {
        return NAMESPACE_SCHEME_WSS;
    }

    /**
	 * get all supported schemes.
	 * 
	 * @return an array of supported schemes.
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedSchemes()
	 */
    public String[] getSupportedSchemes() {
        return new String[] { NAMESPACE_SCHEME_WSS };
    }
}
