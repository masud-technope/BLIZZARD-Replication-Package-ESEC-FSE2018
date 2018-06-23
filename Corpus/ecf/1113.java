/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.sharedobject.events;

import org.eclipse.ecf.core.identity.ID;

/**
 * Event class implementing {@link IContainerSharedObjectMessageSendingEvent}.
 */
public class ContainerSharedObjectMessageSendingEvent implements IContainerSharedObjectMessageSendingEvent {

    protected ID localContainerID;

    protected ID targetContainerID;

    protected ID sharedObjectID;

    protected Object message;

    public  ContainerSharedObjectMessageSendingEvent(ID localContainerID, ID targetContainerID, ID sharedObjectID, Object message) {
        this.localContainerID = localContainerID;
        this.targetContainerID = targetContainerID;
        this.sharedObjectID = sharedObjectID;
        this.message = message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageSendingEvent#getMessage()
	 */
    public Object getMessage() {
        return message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageSendingEvent#getSharedObjectID()
	 */
    public ID getSharedObjectID() {
        return sharedObjectID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageSendingEvent#getTargetContainerID()
	 */
    public ID getTargetContainerID() {
        return targetContainerID;
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
        StringBuffer buf = new StringBuffer("ContainerSharedObjectMessageSendingEvent[");
        //$NON-NLS-1$
        buf.append("localContainerID=").append(localContainerID);
        //$NON-NLS-1$
        buf.append(";targetContainerID=").append(targetContainerID);
        //$NON-NLS-1$
        buf.append(";sharedObjectID=").append(sharedObjectID);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";message=").append(message).append("]");
        return buf.toString();
    }
}
