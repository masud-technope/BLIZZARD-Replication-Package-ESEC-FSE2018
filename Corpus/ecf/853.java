/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

/**
 * Exception class for the case when no remote reference is found during
 * {@link RemoteServiceAdmin#importService(org.osgi.service.remoteserviceadmin.EndpointDescription)}
 * . Instances of this class will be thrown when the call to
 * {@link IRemoteServiceContainerAdapter#getRemoteServiceReferences(ID, ID[], String, String)}
 * made in
 * {@link RemoteServiceAdmin#importService(org.osgi.service.remoteserviceadmin.EndpointDescription)}
 * fail to find any available remote references (e.g. due to connection problem
 * or remote reference lookup problem).
 * 
 */
public class RemoteReferenceNotFoundException extends Exception {

    private static final long serialVersionUID = -4174685192086828376L;

    private ID targetID;

    private ID[] idFilter;

    private Collection<String> interfaces;

    private String rsFilter;

    public  RemoteReferenceNotFoundException(ID targetID, ID[] idFilter, Collection<String> interfaces, String rsFilter) {
        this.targetID = targetID;
        this.idFilter = idFilter;
        this.interfaces = interfaces;
        this.rsFilter = rsFilter;
    }

    public ID getTargetID() {
        return targetID;
    }

    public ID[] getIdFilter() {
        return idFilter;
    }

    public Collection<String> getInterfaces() {
        return interfaces;
    }

    public String getRsFilter() {
        return rsFilter;
    }

    public String toString() {
        return //$NON-NLS-1$
        "RemoteReferenceNotFoundException[targetID=" + targetID + ", idFilter=" + Arrays.toString(idFilter) + //$NON-NLS-1$ //$NON-NLS-2$
        ", interfaces=" + interfaces + ", rsFilter=" + rsFilter + //$NON-NLS-1$ //$NON-NLS-2$
        "]";
    }
}
