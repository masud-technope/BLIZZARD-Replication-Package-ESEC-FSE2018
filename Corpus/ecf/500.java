/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.events;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;

public class ContainerEjectedEvent implements IContainerEjectedEvent {

    private final ID localContainerID;

    private final ID groupID;

    private final Serializable reason;

    public  ContainerEjectedEvent(ID localContainerID, ID targetID, Serializable reason) {
        super();
        this.localContainerID = localContainerID;
        this.groupID = targetID;
        this.reason = reason;
    }

    public ID getTargetID() {
        return groupID;
    }

    public ID getLocalContainerID() {
        return localContainerID;
    }

    public Serializable getReason() {
        return reason;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ContainerEjectedEvent[");
        //$NON-NLS-1$
        buf.append(getLocalContainerID()).append(";");
        //$NON-NLS-1$
        buf.append(getTargetID()).append(";");
        //$NON-NLS-1$
        buf.append(getReason()).append("]");
        return buf.toString();
    }
}
