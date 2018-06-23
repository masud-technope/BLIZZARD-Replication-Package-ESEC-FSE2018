/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

/**
 * Base event implementation of {@link IServiceEvent}. Subclasses may be created
 * as appropriate.
 */
public class ServiceTypeContainerEvent implements IServiceTypeEvent {

    protected IServiceTypeID serviceType;

    protected ID containerID;

    public  ServiceTypeContainerEvent(IServiceTypeID serviceType, ID containerID) {
        this.serviceType = serviceType;
        this.containerID = containerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerEvent#getLocalContainerID()
	 */
    public ID getLocalContainerID() {
        return containerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ServiceTypeContainerEvent[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buf.append("servicetypeid=").append(getServiceTypeID()).append(";containerid=").append(getLocalContainerID()).append("]");
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceTypeEvent#getServiceTypeID()
	 */
    public IServiceTypeID getServiceTypeID() {
        return serviceType;
    }
}
