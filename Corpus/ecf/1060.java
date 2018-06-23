/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.storage;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Storage interface for IContainer instances.
 */
public interface IContainerStore extends IAdaptable {

    /**
	 * Get all {@link IContainerEntry}s.  Will return from secure storage all container entries previously added
	 * via {@link #store(IStorableContainerAdapter)}.
	 * 
	 * @return IContainerEntry[] of all container entries.  Will not return <code>null</code>.  If no containers previously
	 * stored, will return empty array.
	 */
    public IContainerEntry[] getContainerEntries();

    /**
	 * Store a {@link IStorableContainerAdapter} in this container store.
	 * 
	 * @param containerAdapter the {@link IStorableContainerAdapter} to store.  Must not be <code>null</code>.
	 * @return {@link IContainerEntry} result of storage.  Will not return <code>null</code>.
	 * @throws StorageException if containerAdapter cannot be properly stored for whatever reason.
	 */
    public IContainerEntry store(IStorableContainerAdapter containerAdapter) throws StorageException;

    /**
	 * Retrieve an IContainerEntry for a given container ID.
	 * 
	 * @param containerID the containerID to retrieve.  Must not be <code>null</code>.
	 * @return IContainerEntry found, or <code>null</code> if container with given ID not found.
	 */
    public IContainerEntry retrieve(ID containerID);

    /**
	 * Retrieve an IContainerEntry for a given {@link IIDEntry}.
	 * 
	 * @param idEntry the idEntry to retrieve.  Must not be <code>null</code>.
	 * @return IContainerEntry found, or <code>null</code> if container with given ID not found.
	 */
    public IContainerEntry retrieve(IIDEntry idEntry);
}
