/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.identity;

import java.net.URI;
import java.net.URL;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.remoteservice.rest.client.RestClientContainer;

/**
 * This class represents a {@link Namespace} for {@link RestClientContainer}s.
 */
public class RestNamespace extends Namespace {

    private static final long serialVersionUID = -398861350452016954L;

    /**
	 * @since 2.7
	 */
    public static Namespace INSTANCE;

    /**
	 * The name of this namespace.
	 */
    //$NON-NLS-1$
    public static final String NAME = "ecf.rest.namespace";

    /**
	 * The scheme of this namespace.
	 */
    //$NON-NLS-1$
    public static final String SCHEME = "rest";

    public  RestNamespace() {
        //$NON-NLS-1$
        super(NAME, "RestID Namespace");
        INSTANCE = this;
    }

    public  RestNamespace(String name, String desc) {
        super(name, desc);
    }

    private String getInitFromExternalForm(Object[] args) {
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

    /**
	 * Creates an instance of an {@link RestID}. The parameters must contain
	 * specific information.
	 * 
	 * @param parameters
	 *            a collection of attributes to call the right constructor on
	 *            {@link RestID}.
	 * @return an instance of {@link RestID}. Will not be <code>null</code>.
	 */
    public ID createInstance(Object[] parameters) throws IDCreateException {
        URI uri = null;
        try {
            final String init = getInitFromExternalForm(parameters);
            if (init != null) {
                uri = URI.create(init);
                return new RestID(this, uri);
            }
            if (parameters != null) {
                if (parameters[0] instanceof URI)
                    return new RestID(this, (URI) parameters[0]);
                else if (parameters[0] instanceof String)
                    return new RestID(this, URI.create((String) parameters[0]));
                else if (parameters[0] instanceof URL)
                    return new RestID(this, URI.create(((URL) parameters[0]).toExternalForm()));
                else if (parameters[0] instanceof RestID)
                    return (ID) parameters[0];
            }
            //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid parameters to RestID creation");
        } catch (Exception e) {
            throw new IDCreateException("Could not create rest ID", e);
        }
    }

    public String getScheme() {
        return SCHEME;
    }

    public Class[][] getSupportedParameterTypes() {
        return new Class[][] { { ID.class }, { URI.class }, { String.class }, { URL.class } };
    }
}
