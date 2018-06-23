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
import org.eclipse.ecf.remoteservice.RemoteServiceID;

/**
 * @since 3.0
 */
public class RienaRemoteServiceNamespace extends Namespace {

    private static final long serialVersionUID = 6612627823985133944L;

    //$NON-NLS-1$
    private static final String REMOTE_SERVICE_SCHEME = "rienaremoteservice";

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.riena.remoteservice";

    private static Namespace instance;

    public  RienaRemoteServiceNamespace() {
        super();
        instance = this;
    }

    public ID createInstance(Object[] parameters) throws IDCreateException {
        if (parameters == null || parameters.length != 2)
            throw new IDCreateException("Parameters incorrect for remote ID creation");
        try {
            return new RemoteServiceID(this, (RienaID) parameters[0], ((Long) parameters[1]).longValue());
        } catch (Exception e) {
            throw new IDCreateException("Exception creating Riena remoteID", e);
        }
    }

    public String getScheme() {
        return REMOTE_SERVICE_SCHEME;
    }

    public static Namespace getInstance() {
        return instance;
    }
}
