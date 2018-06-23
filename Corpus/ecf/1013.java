/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.events;

import org.eclipse.ecf.core.identity.ID;

/**
 * Container disconnected event.
 */
public class ContainerDisconnectedEvent implements IContainerDisconnectedEvent {

    private final ID departedContainerID;

    private final ID localContainerID;

    /**
	 * Creates a new ContainerDisconnectedEvent to indicate that the container
	 * has now completely disconnected from its target host.
	 * 
	 * @param localContainerID
	 *            the ID of the local container
	 * @param targetID
	 *            the ID of the target
	 */
    public  ContainerDisconnectedEvent(ID localContainerID, ID targetID) {
        this.localContainerID = localContainerID;
        this.departedContainerID = targetID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerDisconnectedEvent#getTargetID()
	 */
    public ID getTargetID() {
        return departedContainerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerEvent#getLocalContainerID()
	 */
    public ID getLocalContainerID() {
        return localContainerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ContainerDisconnectedEvent[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(getLocalContainerID()).append(";").append("]");
        //$NON-NLS-1$
        buf.append(getTargetID()).append(";");
        return buf.toString();
    }
}
