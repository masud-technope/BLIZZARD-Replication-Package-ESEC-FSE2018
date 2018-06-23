/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.provider.r_osgi;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.ServiceReference;

public class LocalRemoteServiceReferenceImpl implements IRemoteServiceReference {

    private final IRemoteServiceID remoteServiceID;

    private ServiceReference reference;

    public  LocalRemoteServiceReferenceImpl(IRemoteServiceID remoteServiceID, ServiceReference ref) {
        this.remoteServiceID = remoteServiceID;
        this.reference = ref;
    }

    public ID getContainerID() {
        return remoteServiceID.getContainerID();
    }

    public IRemoteServiceID getID() {
        return remoteServiceID;
    }

    public Object getProperty(String key) {
        return reference.getProperty(key);
    }

    public String[] getPropertyKeys() {
        return reference.getPropertyKeys();
    }

    public boolean isActive() {
        return true;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("LocalRemoteServiceReferenceImpl[");
        //$NON-NLS-1$
        buf.append("remoteServiceID=").append(remoteServiceID);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";reference=").append(reference).append("]");
        return buf.toString();
    }
}
