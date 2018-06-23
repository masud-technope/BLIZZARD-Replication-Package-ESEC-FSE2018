/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.core;

import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.IContainerManagerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.tests.core.Activator;

public class ContainerManagerServiceTest extends ContainerFactoryServiceAbstractTestCase {

    protected static final String CONTAINER_TYPE_NAME = ContainerManagerServiceTest.class.getName();

    private IContainerManager containerManager = null;

    private IContainerManagerListener containerManagerListener = new IContainerManagerListener() {

        public void containerAdded(IContainer container) {
            System.out.println("containerAdded(" + container + ")");
        }

        public void containerRemoved(IContainer container) {
            System.out.println("containerRemoved(" + container + ")");
        }
    };

    protected IContainer[] createContainers(int length) throws Exception {
        IContainer[] result = new IContainer[length];
        for (int i = 0; i < length; i++) {
            result[i] = Activator.getDefault().getContainerFactory().createContainer();
        }
        return result;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.core.ContainerFactoryAbstractTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        getFixture().addDescription(createContainerTypeDescription());
        containerManager = Activator.getDefault().getContainerManager();
        containerManager.addListener(containerManagerListener);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.tests.core.ContainerFactoryAbstractTestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        getFixture().removeDescription(createContainerTypeDescription());
        containerManager.removeListener(containerManagerListener);
        containerManager = null;
        super.tearDown();
    }

    protected ContainerTypeDescription createContainerTypeDescription() {
        return new ContainerTypeDescription(CONTAINER_TYPE_NAME, new IContainerInstantiator() {

            public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
                return new AbstractContainer() {

                    protected ID id = null;

                    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
                    }

                    public void disconnect() {
                    }

                    public Namespace getConnectNamespace() {
                        return null;
                    }

                    public ID getConnectedID() {
                        return null;
                    }

                    public ID getID() {
                        if (id == null) {
                            try {
                                id = IDFactory.getDefault().createGUID();
                            } catch (IDCreateException e) {
                                e.printStackTrace();
                            }
                        }
                        return id;
                    }
                };
            }

            public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
                return new String[] { "one" };
            }

            public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
                return new Class[][] { { String.class, Class.class } };
            }

            public String[] getSupportedIntents(ContainerTypeDescription description) {
                return null;
            }
        }, DESCRIPTION);
    }

    public void testGetContainerManager() throws Exception {
        assertNotNull(containerManager);
    }

    public void testGetContainersOne() throws Exception {
        IContainer[] c = createContainers(1);
        assertNotNull(c);
        IContainer[] containers = containerManager.getAllContainers();
        assertNotNull(containers);
    // assertTrue(containers.length == 1);
    }

    public void testGetContainerOne() throws Exception {
        IContainer[] c = createContainers(1);
        assertNotNull(c);
        IContainer container = containerManager.getContainer(c[0].getID());
        assertNotNull(container);
        assertTrue(container.getID().equals(c[0].getID()));
    }

    public void testGetContainerN() throws Exception {
        IContainer[] c = createContainers(10);
        assertNotNull(c);
        for (int i = 0; i < 10; i++) {
            IContainer container = containerManager.getContainer(c[i].getID());
            assertNotNull(container);
            assertTrue(container.getID().equals(c[i].getID()));
        }
    }

    public void testHasContainerN() throws Exception {
        IContainer[] c = createContainers(10);
        assertNotNull(c);
        for (int i = 0; i < 10; i++) {
            assertTrue(containerManager.hasContainer(c[i].getID()));
        }
    }

    public void testGetContainerDescriptionN() throws Exception {
        IContainer[] c = createContainers(10);
        assertNotNull(c);
        for (int i = 0; i < 10; i++) {
            ContainerTypeDescription description = containerManager.getContainerTypeDescription(c[i].getID());
            assertNotNull(description);
        }
    }

    public void testClearContainers() throws Exception {
        IContainer[] c = createContainers(10);
        assertNotNull(c);
        containerManager.removeAllContainers();
        IContainer[] cs = containerManager.getAllContainers();
        assertTrue(cs != null);
        assertTrue(cs.length == 0);
    }
}
