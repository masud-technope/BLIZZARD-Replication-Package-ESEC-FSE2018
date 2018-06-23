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
package org.eclipse.ecf.internal.provider.discovery;

import java.net.URI;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceTypeID;

public class CompositeNamespace extends Namespace {

    private static final long serialVersionUID = -4774766051014928510L;

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.composite";

    public  CompositeNamespace() {
        //$NON-NLS-1$
        super(NAME, "Composite Namespace");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
    public ID createInstance(final Object[] parameters) {
        if (parameters == null || parameters.length < 1 || parameters.length > 2) {
            //$NON-NLS-1$
            throw new IDCreateException("parameter count must be non null and of length >= 1 and =< 2");
        } else if (parameters.length == 2 && parameters[0] instanceof String && parameters[1] instanceof URI) {
            return new CompositeServiceID(this, new ServiceTypeID(this, (String) parameters[0]), (URI) parameters[1]);
        } else if (parameters.length == 2 && parameters[0] instanceof IServiceTypeID && parameters[1] instanceof URI) {
            return new CompositeServiceID(this, (IServiceTypeID) parameters[0], (URI) parameters[1]);
        } else if (parameters.length == 1 && parameters[0] instanceof IServiceTypeID) {
            final IServiceTypeID iServiceTypeID = (IServiceTypeID) parameters[0];
            return new ServiceTypeID(this, iServiceTypeID.getName());
        } else {
            //$NON-NLS-1$
            throw new IDCreateException("wrong parameters");
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
    public String getScheme() {
        //$NON-NLS-1$
        return "composite";
    }
}
