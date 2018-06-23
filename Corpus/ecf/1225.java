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

import org.eclipse.ecf.core.identity.*;
import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 * Representation of an ID instance inside of an {@link IIDStore}.
 */
public interface IIDEntry {

    /**
	 * Get the underlying {@link ISecurePreferences} node that represents this IDEntry
	 * in the storage.
	 * 
	 * @return {@link ISecurePreferences} that represents this IIDEntry in the underlying storage.  Will
	 * not return <code>null</code>.
	 */
    public ISecurePreferences getPreferences();

    /**
	 * Create an ID from this IDEntry.  This method may be used to create new ID instance from this
	 * {@link IIDEntry}.  The created ID will be equivalent (via ID.equals(other)) to the ID previously
	 * stored via {@link IIDStore#store(ID)}.
	 * 
	 * @return {@link ID} that corresponds to this previously stored {@link IIDEntry}.
	 * @throws IDCreateException if the ID cannot be created in this environment...e.g. due to missing
	 * {@link Namespace}.
	 */
    public ID createID() throws IDCreateException;

    /**
	 * Get any {@link IIDEntry}s that have previously been associated with this IIDEntry via {@link #putAssociate(java.lang.String,IIDEntry,boolean)}.
	 * @param key the String key for retrieving associates.  Must not be <code>null</code>. 
	 * 
	 * @return IIDEntry[] of associated IIDEntry instances that have previously been successfully stored via {@link #putAssociate(java.lang.String,IIDEntry,boolean)}.
	 * If no IIDEntries have been previously stored with the given key, an empty array will be returned.  Will not return <code>null</code>.  Note
	 * that the order of the returned IIDEntrys will not necessarily correspond to the order added via {@link #putAssociate(String, IIDEntry, boolean)}.
	 */
    public IIDEntry[] getAssociates(String key);

    /**
	 * Associate an IIDEntry instance with a String key in this IIDEntry.  The association is one-way (i.e. if successful, future calls to
	 * this {@link #getAssociates(java.lang.String)} with the same key will include the new entry.
	 * 
	 * @param key the String key for storing associates.  Must not be <code>null</code>..
	 * @param entry the {@link IIDEntry} to associated with this {@link IIDEntry}.  Must not be <code>null</code>.
	 * @param encrypt if <code>true</code> associate IIDEntry will be encrypted, <code>false</code> and it will 
	 * not be encrypted.
	 * @throws IDStoreException thrown if the given {@link IIDEntry} cannot be stored.
	 */
    public void putAssociate(String key, IIDEntry entry, boolean encrypt) throws IDStoreException;

    /**
	 * Delete this IIDEntry from the {@link IIDStore}.  This will <b>not</be> delete any associated {@link IIDEntry}s.  It is
	 * up to the client to explicitly remove any such associated entries.
	 */
    public void delete();
}
