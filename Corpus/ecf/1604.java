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

/**
 * Base event implementation of {@link IServiceEvent}. Subclasses may be created
 * as appropriate.
 */
public class ServiceContainerEvent implements IServiceEvent {

    protected IServiceInfo info;

    protected ID containerID;

    public  ServiceContainerEvent(IServiceInfo info, ID containerID) {
        this.info = info;
        this.containerID = containerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceEvent#getServiceInfo()
	 */
    public IServiceInfo getServiceInfo() {
        return info;
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
        final StringBuffer buf = new StringBuffer("ServiceContainerEvent[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("serviceinfo=").append(info).append("]");
        return buf.toString();
    }
}
