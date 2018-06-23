/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.events;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Event;

/**
 * Shared object event
 * 
 */
public interface ISharedObjectEvent extends Event {

    /**
	 * Get ID of sender shared object responsible for this event
	 * 
	 * @return ID of sender shared object. Will not be null.
	 */
    public ID getSenderSharedObjectID();

    /**
	 * Get the Event from the sender shared object
	 * 
	 * @return Event the event in question
	 */
    public Event getEvent();
}
