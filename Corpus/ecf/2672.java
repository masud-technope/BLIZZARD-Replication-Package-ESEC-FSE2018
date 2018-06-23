/******************************************************************************* 
 * Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.identity;

import java.net.URI;
import java.net.URL;
import org.eclipse.ecf.core.identity.*;

public class SoapNamespace extends Namespace {

    private static final long serialVersionUID = 4615619057198794146L;

    /**
	 * The name of this namespace.
	 */
    //$NON-NLS-1$
    public static final String NAME = "ecf.soap.namespace";

    /**
	 * The scheme of this namespace.
	 */
    //$NON-NLS-1$
    public static final String SCHEME = "soap";

    public  SoapNamespace() {
    // 
    }

    public  SoapNamespace(String name, String desc) {
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
	 * Creates an instance of an {@link SoapID}. The parameters must contain
	 * specific information.
	 * 
	 * @param parameters
	 *            a collection of attributes to call the right constructor on
	 *            {@link SoapID}.
	 * @return an instance of {@link SoapID}. Will not be <code>null</code>.
	 */
    public ID createInstance(Object[] parameters) throws IDCreateException {
        URI uri = null;
        try {
            final String init = getInitFromExternalForm(parameters);
            if (init != null) {
                uri = URI.create(init);
                return new SoapID(this, uri);
            }
            if (parameters != null) {
                if (parameters[0] instanceof URI)
                    return new SoapID(this, (URI) parameters[0]);
                else if (parameters[0] instanceof String)
                    return new SoapID(this, URI.create((String) parameters[0]));
                else if (parameters[0] instanceof URL)
                    return new SoapID(this, URI.create(((URL) parameters[0]).toExternalForm()));
                else if (parameters[0] instanceof SoapID)
                    return (ID) parameters[0];
            }
            //$NON-NLS-1$
            throw new IllegalArgumentException("Invalid parameters to Soap ID creation");
        } catch (Exception e) {
            throw new IDCreateException("Could not create Soap ID", e);
        }
    }

    public String getScheme() {
        return SCHEME;
    }

    public Class[][] getSupportedParameterTypes() {
        return new Class[][] { { ID.class }, { URI.class }, { String.class }, { URL.class } };
    }
}
