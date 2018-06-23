/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jmdns.identity;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

public class JMDNSNamespace extends Namespace {

    private static final long serialVersionUID = -7220857203720337921L;

    //$NON-NLS-1$
    public static final String JMDNS_SCHEME = "jmdns";

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.jmdns";

    public  JMDNSNamespace() {
        super();
    }

    /**
	 * @param description description for namespace
	 * @since 4.3
	 */
    public  JMDNSNamespace(String description) {
        super(NAME, description);
    }

    private String getInitFromExternalForm(final Object[] args) {
        if (args == null || args.length < 1 || args[0] == null)
            return null;
        if (args[0] instanceof String) {
            final String arg = (String) args[0];
            if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
                final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
                if (index >= arg.length())
                    return null;
                return arg.substring(index + 1);
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	*/
    public ID createInstance(final Object[] parameters) {
        String type = null;
        try {
            final String init = getInitFromExternalForm(parameters);
            if (init != null)
                type = init;
            else {
                if (parameters == null || parameters.length < 1 || parameters.length > 2) {
                    throw new IDCreateException("Parameters cannot be null and must be of length 1 or 2");
                }
                if (parameters[0] instanceof IServiceTypeID) {
                    type = ((IServiceTypeID) parameters[0]).getName();
                } else if (parameters[0] instanceof String) {
                    type = (String) parameters[0];
                } else
                    throw new IDCreateException("Service type id parameter has to be of type String or IServiceTypeID");
            }
        } catch (final Exception e) {
            throw new IDCreateException(getName() + " createInstance()", e);
        }
        final JMDNSServiceTypeID stid = new JMDNSServiceTypeID(this, type);
        if (parameters.length == 1) {
            return stid;
        } else if (parameters[1] instanceof String) {
            try {
                final URI uri = new URI((String) parameters[1]);
                return new JMDNSServiceID(this, stid, uri);
            } catch (URISyntaxException e) {
                throw new IDCreateException("Second parameter as String must follow URI syntax");
            }
        } else if (parameters[1] instanceof URI) {
            return new JMDNSServiceID(this, stid, (URI) parameters[1]);
        } else {
            //$NON-NLS-1$
            throw new IDCreateException("Second parameter must be of either String or URI type");
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
    public String getScheme() {
        return JMDNS_SCHEME;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
	 */
    public Class[][] getSupportedParameterTypes() {
        return new Class[][] { { String.class }, { String.class, String.class } };
    }
}
