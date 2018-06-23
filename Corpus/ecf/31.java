/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.events;

import org.eclipse.ecf.core.identity.ID;

/**
 * Container connected event
 * 
 */
public class ContainerConnectedEvent implements IContainerConnectedEvent {

    private final ID joinedContainerID;

    private final ID localContainerID;

    public  ContainerConnectedEvent(ID localContainerID, ID targetID) {
        super();
        this.localContainerID = localContainerID;
        this.joinedContainerID = targetID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerConnectedEvent#getTargetID()
	 */
    public ID getTargetID() {
        return joinedContainerID;
    }

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
        StringBuffer buf = new StringBuffer("ContainerConnectedEvent[");
        //$NON-NLS-1$
        buf.append(getLocalContainerID()).append("]");
        //$NON-NLS-1$
        buf.append(getTargetID()).append(";");
        return buf.toString();
    }
}
