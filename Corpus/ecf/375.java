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
package org.eclipse.ecf.internal.storage;

import org.eclipse.ecf.core.BaseContainer;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.storage.IStorableContainerAdapter;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 *
 */
public class StorableBaseContainer extends BaseContainer implements IStorableContainerAdapter {

    /**
	 * @param idl
	 * @throws ContainerCreateException
	 */
    public  StorableBaseContainer(long idl) throws ContainerCreateException {
        super(idl);
    }

    /**
	 * @param id
	 */
    public  StorableBaseContainer(ID id) {
        super(id);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IStorableContainerAdapter#encrypt()
	 */
    public boolean storeEncrypted() {
        return false;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IStorableContainerAdapter#getFactoryName()
	 */
    public String getContainerFactoryName() {
        //$NON-NLS-1$
        return "ecf.storage.basecontainer";
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IStorableContainerAdapter#handleRestore(org.eclipse.equinox.security.storage.ISecurePreferences)
	 */
    public void restore(ISecurePreferences prefs) throws StorageException {
        //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("restore(" + prefs + ")");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IStorableContainerAdapter#handleStore(org.eclipse.equinox.security.storage.ISecurePreferences)
	 */
    public void store(ISecurePreferences prefs) throws StorageException {
        //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("store(" + prefs + ")");
    }
}
