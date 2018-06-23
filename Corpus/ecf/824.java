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

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.storage.*;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 *
 */
public class ContainerEntry implements IContainerEntry {

    //$NON-NLS-1$
    private static final String FACTORY_NAME_KEY = "factoryName";

    ISecurePreferences prefs;

    IIDEntry idEntry;

    ID containerID;

    /**
	 * @param idEntry 
	 */
    public  ContainerEntry(IIDEntry idEntry) {
        this.idEntry = idEntry;
        ISecurePreferences prefs = idEntry.getPreferences();
        this.prefs = prefs.node(ContainerStore.CONTAINER_NODE_NAME);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IContainerEntry#createContainer()
	 */
    public IContainer createContainer() throws ContainerCreateException {
        try {
            IContainer container = ContainerFactory.getDefault().createContainer(getFactoryName(), getContainerID());
            IStorableContainerAdapter containerAdapter = (IStorableContainerAdapter) container.getAdapter(IStorableContainerAdapter.class);
            if (containerAdapter != null) {
                containerAdapter.restore(prefs);
            }
            return container;
        } catch (IDCreateException e) {
            throw new ContainerCreateException("Could not create ID for container", e);
        } catch (StorageException e) {
            throw new ContainerCreateException("Exception on restore", e);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IContainerEntry#delete()
	 */
    public void delete() {
        prefs.removeNode();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IContainerEntry#getContainerID()
	 */
    public ID getContainerID() throws IDCreateException {
        if (containerID == null) {
            containerID = idEntry.createID();
        }
        return containerID;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IContainerEntry#getFactoryName()
	 */
    public String getFactoryName() throws StorageException {
        //$NON-NLS-1$
        return prefs.get(FACTORY_NAME_KEY, "");
    }

    protected void setFactoryName(String factoryName, boolean encrypt) throws StorageException {
        prefs.put(FACTORY_NAME_KEY, factoryName, encrypt);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.IContainerEntry#getPreferences()
	 */
    public ISecurePreferences getPreferences() {
        return prefs;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("ContainerEntry[");
        //$NON-NLS-1$
        sb.append("idEntry=").append(idEntry);
        //$NON-NLS-1$//$NON-NLS-2$
        sb.append(";prefs=").append(getPreferences()).append("]");
        return sb.toString();
    }
}
