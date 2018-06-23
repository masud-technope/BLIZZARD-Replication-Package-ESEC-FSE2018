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
 * Event class implementing {@link IContainerSharedObjectMessageReceivingEvent}.
 */
public class ContainerSharedObjectMessageReceivingEvent implements IContainerSharedObjectMessageReceivingEvent {

    protected ID sendingContainerID;

    protected ID sharedObjectID;

    protected ID localContainerID;

    protected Object message;

    public  ContainerSharedObjectMessageReceivingEvent(ID localContainerID, ID sendingContainerID, ID sharedObjectID, Object message) {
        this.localContainerID = localContainerID;
        this.sendingContainerID = sendingContainerID;
        this.sharedObjectID = sharedObjectID;
        this.message = message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageReceivingEvent#getMessage()
	 */
    public Object getMessage() {
        return message;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageReceivingEvent#getSendingContainerID()
	 */
    public ID getSendingContainerID() {
        return sendingContainerID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.events.IContainerSharedObjectMessageReceivingEvent#getSharedObjectID()
	 */
    public ID getSharedObjectID() {
        return sharedObjectID;
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
        StringBuffer buf = new StringBuffer("ContainerSharedObjectMessageReceivingEvent[");
        //$NON-NLS-1$
        buf.append("localContainerID=").append(localContainerID);
        //$NON-NLS-1$
        buf.append(";sendingContainerID=").append(sendingContainerID);
        //$NON-NLS-1$
        buf.append(";sharedObjectID=").append(sharedObjectID);
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append(";message=").append(message).append("]");
        return buf.toString();
    }
}
