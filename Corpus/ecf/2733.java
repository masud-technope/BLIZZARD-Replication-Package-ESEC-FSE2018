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
package org.eclipse.ecf.tests.securestorage;

import junit.framework.TestCase;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.tests.securestorage.Activator;
import org.eclipse.ecf.storage.IContainerEntry;
import org.eclipse.ecf.storage.IContainerStore;
import org.eclipse.ecf.storage.IIDStore;
import org.eclipse.ecf.storage.IStorableContainerAdapter;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

public class ContainerStoreTest extends TestCase {

    IContainerStore containerStore;

    IIDStore idStore;

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        containerStore = Activator.getDefault().getContainerStore();
        idStore = Activator.getDefault().getIDStore();
    }

    protected void clearStore() {
        final IContainerEntry[] containerEntries = containerStore.getContainerEntries();
        for (int i = 0; i < containerEntries.length; i++) {
            containerEntries[i].delete();
        }
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        clearStore();
        containerStore = null;
    }

    public void testContainerStore() {
        assertNotNull(containerStore);
    }

    IContainer createContainer() throws ContainerCreateException {
        return ContainerFactory.getDefault().createContainer("ecf.storage.basecontainer");
    }

    IStorableContainerAdapter getStorableContainerAdapter(IContainer container) {
        return (IStorableContainerAdapter) container.getAdapter(IStorableContainerAdapter.class);
    }

    IContainerEntry storeContainer(IStorableContainerAdapter containerAdapter) throws StorageException {
        return containerStore.store(containerAdapter);
    }

    public ID testStoreContainer() throws Exception {
        final IContainer container = createContainer();
        final IContainerEntry containerEntry = storeContainer(getStorableContainerAdapter(container));
        final ISecurePreferences prefs = containerEntry.getPreferences();
        assertNotNull(prefs);
        return container.getID();
    }

    public void testAndGetContainerEntries() throws Exception {
        testStoreContainer();
        // Now retrieve from container store with given ID
        final IContainerEntry[] containerEntries = containerStore.getContainerEntries();
        assertNotNull(containerEntries);
        assertTrue(containerEntries.length == 1);
    }

    public void testAndRetrieveStoreContainerByID() throws Exception {
        final ID containerID = testStoreContainer();
        // Now retrieve from container store with given ID
        final IContainerEntry containerEntry = containerStore.retrieve(containerID);
        assertNotNull(containerEntry);
        final ID containerIDa = containerEntry.getContainerID();
        assertNotNull(containerIDa);
        assertTrue(containerIDa.equals(containerID));
    }

    public void testAndRetrieveStoreContainerByIDAndCreateContainer() throws Exception {
        final ID containerID = testStoreContainer();
        // Now retrieve from container store with given ID
        final IContainerEntry containerEntry = containerStore.retrieve(containerID);
        assertNotNull(containerEntry);
        final IContainer containera = containerEntry.createContainer();
        final ID containerIDa = containera.getID();
        assertNotNull(containerIDa);
        assertTrue(containerIDa.equals(containerID));
    }
}
