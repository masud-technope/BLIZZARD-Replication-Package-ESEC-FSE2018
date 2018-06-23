/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.security;

import java.security.PermissionCollection;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IContainerPolicy;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;

public interface ISharedObjectPolicy extends IContainerPolicy {

    /**
	 * Check the request to add a shared object from external source (i.e.
	 * remote container).
	 * 
	 * @param fromID
	 *            the ID of the container making the container add request
	 * @param toID
	 *            the ID of the container receiving/handling the add request.
	 *            Null if directed to all containers in group.
	 * @param localID
	 *            the ID of the local container
	 * @param newObjectDescription
	 *            the ReplicaSharedObjectDescription associated with the shared
	 *            object being added
	 * @return PermissionCollection the permission collection associated with
	 *         successful acceptance of the add request. Null if the add should
	 *         be refused, Non-null if add request is accepted.
	 * @throws SecurityException
	 *             if request should be refused <b>and</b> associated container
	 *             should leave group
	 */
    public PermissionCollection checkAddSharedObject(ID fromID, ID toID, ID localID, ReplicaSharedObjectDescription newObjectDescription) throws SecurityException;
}
