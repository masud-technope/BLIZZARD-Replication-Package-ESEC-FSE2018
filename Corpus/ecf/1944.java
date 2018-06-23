/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.events;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

/**
 * Super interface for remote service events (registration and unregistration).
 */
public interface IRemoteServiceEvent {

    /**
	 * The ID of the local container.
	 * @return ID of local container.  Will not be <code>null</code>.
	 * 
	 * @since 3.0
	 */
    public ID getLocalContainerID();

    /**
	 * The ID of the container that registered the service.
	 * 
	 * @return ID of container that registered service. Will not be
	 *         <code>null</code>.
	 */
    public ID getContainerID();

    /**
	 * Get the remote service reference for the unregistered service.
	 * 
	 * @return {@link IRemoteServiceReference} the reference for the
	 *         unregistered service. Will not be <code>null</code>.
	 */
    public IRemoteServiceReference getReference();

    /**
	 * Get the interface classes associated with/exposed by the remote service.
	 * 
	 * @return String[] set of interface classes exposed by the unregistered
	 *         remote service. Will not be <code>null</code>, but may be
	 *         empty array.
	 */
    public String[] getClazzes();
}
