/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.remoteservice.generic;

import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.remoteservice.RemoteServiceID;

/**
 * @since 3.0
 */
public class RemoteServiceNamespace extends Namespace {

    private static final long serialVersionUID = 1389616654436118037L;

    //$NON-NLS-1$
    private static final String REMOTE_SERVICE_SCHEME = "remoteservice";

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.generic.remoteservice";

    public  RemoteServiceNamespace() {
    // nothing
    }

    public  RemoteServiceNamespace(String name, String desc) {
        super(name, desc);
    }

    public ID createInstance(Object[] parameters) throws IDCreateException {
        if (parameters == null || parameters.length != 2)
            //$NON-NLS-1$
            throw new IDCreateException("Parameters incorrect for remote ID creation");
        try {
            return new RemoteServiceID(this, (ID) parameters[0], ((Long) parameters[1]).longValue());
        } catch (Exception e) {
            throw new IDCreateException("Exception creating remoteID", e);
        }
    }

    public String getScheme() {
        return REMOTE_SERVICE_SCHEME;
    }
}
