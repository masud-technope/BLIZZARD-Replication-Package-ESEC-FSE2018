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

import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 * Namespace entry for {@link IIDStore}.  Instances of this class are created/returned via
 * calls to {@link IIDStore#getNamespaceEntries()} or {@link IIDStore#getNamespaceEntry(org.eclipse.ecf.core.identity.Namespace)}.
 */
public interface INamespaceEntry {

    /**
	 * Get the underlying {@link ISecurePreferences} node that represents this {@link INamespaceEntry}
	 * in the storage.
	 * 
	 * @return {@link ISecurePreferences} that represents this IIDEntry in the underlying storage.  Will
	 * not return <code>null</code>.
	 */
    public ISecurePreferences getPreferences();

    /**
	 * Get the {@link IIDEntry}s for this namespace that are currently stored.
	 * 
	 * @return IIDEntry array of for the IDs that are stored for this Namespace.  Will not return <code>null</code>.
	 */
    public IIDEntry[] getIDEntries();

    /**
	 * Delete this {@link INamespaceEntry} from the {@link IIDStore}.  Note that this <b>will</b> also delete 
	 * all ID entries underneath this Namespace, and so should be used with caution.
	 * 
	 */
    public void delete();
}
