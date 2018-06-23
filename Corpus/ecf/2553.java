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

import java.util.List;
import org.eclipse.ecf.core.ContainerTypeDescription;

public class ContainerFactoryDescriptionsTest extends ContainerFactoryAbstractTestCase {

    private ContainerTypeDescription description;

    protected ContainerTypeDescription getDescription() {
        return description;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.core.ContainerFactoryAbstractTestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        addDescription();
    }

    protected void addDescription() {
        description = createContainerTypeDescription();
        ContainerTypeDescription add = getFixture().addDescription(description);
        assertNull(add);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.tests.core.ContainerFactoryAbstractTestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        removeDescription();
        super.tearDown();
    }

    protected void removeDescription() {
        ContainerTypeDescription remove = getFixture().removeDescription(getDescription());
        assertNotNull(remove);
    }

    public void testGetDescriptions() {
        List d = getFixture().getDescriptions();
        assertNotNull(d);
    }

    public void testAddNullDescription() {
        int prevSize = getFixture().getDescriptions().size();
        ContainerTypeDescription add = getFixture().addDescription(null);
        assertNull(add);
        assertTrue(getFixture().getDescriptions().size() == prevSize);
    }

    public void testContainsDescription() {
        assertTrue(getFixture().containsDescription(getDescription()));
    }

    public void testGetDescriptionByName() {
        ContainerTypeDescription desc = getFixture().getDescriptionByName(getDescription().getName());
        assertNotNull(desc);
    }

    public void testCreateContainer() {
        try {
            getFixture().createContainer(getDescription().getName());
            fail();
        } catch (Exception e) {
        }
    }
}
