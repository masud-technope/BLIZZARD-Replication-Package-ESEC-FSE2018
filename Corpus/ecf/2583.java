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
 * Container connecting event
 * 
 */
public interface IContainerConnectingEvent extends IContainerEvent {

    /**
	 * Get ID of container target (the container we are connecting to)
	 * 
	 * @return ID the ID of the container we connecting to. Will not be null.
	 */
    public ID getTargetID();

    /**
	 * Get data associated with connecting
	 * 
	 * @return Object the object data for connect.  May be null
	 */
    public Object getData();
}
