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

public class ContainerConnectingEvent implements IContainerConnectingEvent {

    ID localContainerID;

    ID targetID;

    Object data;

    public  ContainerConnectingEvent(ID localContainerID, ID targetID, Object data) {
        this.localContainerID = localContainerID;
        this.targetID = targetID;
        this.data = data;
    }

    public  ContainerConnectingEvent(ID localContainerID, ID targetID) {
        this(localContainerID, targetID, null);
    }

    public ID getTargetID() {
        return targetID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerConnectingEvent#getData()
	 */
    public Object getData() {
        return data;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.IContainerEvent#getLocalContainerID()
	 */
    public ID getLocalContainerID() {
        return localContainerID;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ContainerConnectingEvent[");
        //$NON-NLS-1$
        buf.append(getLocalContainerID()).append(";");
        //$NON-NLS-1$
        buf.append(getTargetID()).append(";");
        //$NON-NLS-1$
        buf.append(getData()).append("]");
        return buf.toString();
    }
}
