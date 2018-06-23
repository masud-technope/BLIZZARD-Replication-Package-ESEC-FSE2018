/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery;

import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

/**
 * Service type discovery event that provides access to service type
 */
public interface IServiceTypeEvent extends IContainerEvent {

    /**
	 * Get service type id for this service type event.
	 * @return IServiceTypeID for this service type event.  Will not be <code>null</code>.
	 */
    public IServiceTypeID getServiceTypeID();
}
