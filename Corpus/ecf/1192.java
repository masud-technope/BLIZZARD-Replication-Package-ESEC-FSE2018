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

import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;

/**
 * Container event that indicates that a shared object message is being sent.
 */
public interface IContainerSharedObjectMessageSendingEvent extends IContainerEvent {

    /**
	 * Get the target container ID for the message being sent.
	 * 
	 * @return ID of target container for the message being sent.  If <code>null</code>, this indicates
	 * that all container members of group should receive the message being sent.
	 */
    public ID getTargetContainerID();

    /**
	 * Get the shared object ID of the sending shared object.  This value will not be <code>null</code>
	 * and the ID given will be the ID of a currently active shared object existing within the
	 * enclosing container.
	 * 
	 * @return ID of the shared object that is sending the message.  Will not be <code>null</code>.
	 */
    public ID getSharedObjectID();

    /**
	 * Get the message being sent.  This will return the message being sent by the shared
	 * object identified by {@link #getSharedObjectID()}.  May be <code>null</code> if null
	 * is being sent.
	 * 
	 * @return Object the message being sent by the shared object identified via {@link #getSharedObjectID()}.
	 * May be <code>null</code>.
	 */
    public Object getMessage();
}
