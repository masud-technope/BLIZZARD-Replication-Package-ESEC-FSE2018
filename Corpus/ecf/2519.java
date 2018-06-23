/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.core.identity.ID;

/**
 * Remote service ID.
 *
 * @since 3.0
 */
public interface IRemoteServiceID extends ID {

    /**
	 * Get the container ID for this remote service.  Will not return <code>null</code>.
	 * @return ID the ID for the container associated with this remote service.  Will not return <code>null</code>.
	 */
    public ID getContainerID();

    /**
	 * Get container-relative ID for the remote service identified
	 * @return int the container-relative ID.  
	 */
    public long getContainerRelativeID();
}
