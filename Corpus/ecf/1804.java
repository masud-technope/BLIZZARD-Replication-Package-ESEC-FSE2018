/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.r_osgi;

import ch.ethz.iks.r_osgi.RemoteOSGiService;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.provider.r_osgi.identity.R_OSGiWSNamespace;

class R_OSGiWSRemoteServiceContainer extends R_OSGiRemoteServiceContainer {

    public  R_OSGiWSRemoteServiceContainer(RemoteOSGiService service, ID containerID) throws IDCreateException {
        super(service, containerID);
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(R_OSGiWSNamespace.NAME_WS);
    }
}
