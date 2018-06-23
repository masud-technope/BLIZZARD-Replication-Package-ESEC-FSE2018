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

import org.eclipse.ecf.core.identity.*;

public class RienaNamespace extends Namespace {

    private static final long serialVersionUID = 4512984932537215526L;

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.riena";

    //$NON-NLS-1$
    public static final String SCHEME = "riena";

    private static Namespace instance;

    public  RienaNamespace() {
        //$NON-NLS-1$
        super(NAME, "Riena Namespace");
        instance = this;
    }

    public ID createInstance(Object[] parameters) throws IDCreateException {
        if (parameters == null || parameters.length < 1 || !(parameters[0] instanceof String))
            throw new IDCreateException("Riena ID creation failed with non-String parameter=" + parameters[0]);
        String init = getInitFromExternalForm(parameters);
        try {
            if (init != null)
                return new RienaID(this, init);
            return new RienaID(this, (String) parameters[0]);
        } catch (Exception e) {
            throw new IDCreateException("Exception creating Riena ID", e);
        }
    }

    private String getInitFromExternalForm(Object[] args) {
        if (args == null || args.length < 1 || args[0] == null)
            return null;
        if (args[0] instanceof String) {
            String arg = (String) args[0];
            if (arg.startsWith(this.getClass().getName() + Namespace.SCHEME_SEPARATOR)) {
                int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
                if (index >= arg.length())
                    return null;
                return arg.substring(index + 1);
            }
        }
        return null;
    }

    public String getScheme() {
        return SCHEME;
    }

    public String[] getSupportedSchemes() {
        return new String[] { SCHEME };
    }

    public static Namespace getInstance() {
        return instance;
    }
}
