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
import org.eclipse.ecf.core.util.Event;

/**
 * An event received by a container
 * 
 */
public interface IContainerEvent extends Event {

    /**
	 * Get ID of local discovery container (the discovery container receiving this event).
	 * 
	 * @return ID for local container. Will not return <code>null</code>.
	 */
    public ID getLocalContainerID();
}
