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

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Storage entry for IContainer instances. 
 */
public interface IContainerEntry {

    /**
	 * Get the underlying {@link ISecurePreferences} node that represents this IContainerEntry
	 * in the storage.
	 * 
	 * @return {@link ISecurePreferences} that represents this IContainerEntry in the underlying storage.  Will
	 * not return <code>null</code>.
	 */
    public ISecurePreferences getPreferences();

    /**
	 * Get the container's ID for the stored {@link IContainer}.  The returned ID will be equivalent to
	 * the ID returned by the original container's {@link IContainer#getID()}.
	 * 
	 * @return ID for the container.  Will not be <code>null</code>.
	 * @throws IDCreateException if ID cannot be created.
	 */
    public ID getContainerID() throws IDCreateException;

    /**
	 * Get the container factory name for the stored {@link IContainer}.
	 * @return String the container factory name.  Will not be <code>null</code>.
	 * @throws StorageException if some exception reading from {@link ISecurePreferences}.
	 */
    public String getFactoryName() throws StorageException;

    /**
	 * Create an IContainer from this IContainerEntry.  This method may be used to create new IContainer instance from this
	 * {@link IContainerEntry}.  The created IContainer will be equivalent (via ID.equals(other)) to the ID previously
	 * stored via {@link IContainerStore#store(IStorableContainerAdapter)}.
	 * 
	 * @return {@link IContainer} that corresponds to this previously stored {@link IContainerEntry}.
	 * @throws ContainerCreateException if the IContainer cannot be created in this environment.
	 */
    public IContainer createContainer() throws ContainerCreateException;

    /**
	 * Delete this IContainerEntry from the {@link IContainerStore}.  
	 */
    public void delete();
}
