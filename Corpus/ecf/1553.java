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

import org.eclipse.ecf.core.identity.IIdentifiable;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Container adapter for storable containers.  Containers that can be stored via {@link IContainerStore}s
 * must implement this interface.
 */
public interface IStorableContainerAdapter extends IIdentifiable {

    /**
	 * @return <code>true</code> if the implementer of this adapter should be stored as encrypted,
	 * <code>false</code> if encryption should not be used.
	 */
    public boolean storeEncrypted();

    /**
	 * Get the container factory name for storage and restoration.  The returned
	 * String will be used for creating a restored version of this container via {@link IContainerEntry#createContainer()}.
	 * @return String that identifies the container factory name for this container.  The returned value must
	 * not be <code>null</code>.
	 */
    public String getContainerFactoryName();

    /**
	 * Store the contents of the implementer in the given storage instance.  This method is called during {@link IContainerStore#store(IStorableContainerAdapter)}
	 * and gives the implementer a chance to store any state.  The state is then passed to the {@link #restore(ISecurePreferences)}
	 * method during restore of this container.
	 * @param containerStorage the ISecurePreferences storage to use to store any state associated with this container.  
	 * @throws StorageException if the store of this container should fail.
	 */
    public void store(ISecurePreferences containerStorage) throws StorageException;

    /**
	 * Restore the contents of the container given the storage instance.  This method is called during {@link IContainerEntry#createContainer()},
	 * to give the implementer an opportunity to restore any state previously stored via {@link #store(ISecurePreferences)}.
	 * @param containerStorage the ISecurePreferences storage whose contents were set via {@link #store(ISecurePreferences)}.
	 * @throws StorageException if the restore should fail.
	 */
    public void restore(ISecurePreferences containerStorage) throws StorageException;
}
