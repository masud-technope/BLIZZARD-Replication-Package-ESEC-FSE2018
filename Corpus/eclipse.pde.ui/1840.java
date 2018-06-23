/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.search.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiElement;
import org.eclipse.pde.api.tools.internal.search.SkippedComponent;

/**
 * Tests the {@link org.eclipse.pde.api.tools.internal.search.SkippedComponent} class
 *
 * @since 1.0.1
 */
public class SkippedComponentTests extends SearchTest {

    //$NON-NLS-1$
    static final String SC_NAME = "l.m.n.P";

    IApiComponent TESTING_COMPONENT = null;

    IApiComponent getTestingComponent() {
        if (TESTING_COMPONENT == null) {
            try {
                TESTING_COMPONENT = getTestBaseline().getApiComponent(SC_NAME);
            } catch (CoreException ce) {
                fail(ce.getMessage());
            }
        }
        return TESTING_COMPONENT;
    }

    /**
	 * Tests the {@link SkippedComponent#equals(Object)} method
	 */
    public void testEquals() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            SkippedComponent scomp2 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The components should be equal", scomp1, scomp2);
            //$NON-NLS-1$
            assertTrue("The components should not be equal", !scomp1.equals(tcomp));
            //$NON-NLS-1$
            assertTrue("The components should not be equal", !scomp2.equals(tcomp));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#hashCode()} method
	 */
    public void testHashCode() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            SkippedComponent scomp2 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The component hashcodes should be equal", scomp1.hashCode(), scomp2.hashCode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getComponentId()} method
	 */
    public void testGetComponentId() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            SkippedComponent scomp2 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The component ids should be equal", scomp1.getComponentId(), scomp2.getComponentId());
            //$NON-NLS-1$
            assertEquals("The component ids should be equal", scomp1.getComponentId(), tcomp.getSymbolicName());
            //$NON-NLS-1$
            assertEquals("The component ids should be equal", scomp2.getComponentId(), tcomp.getSymbolicName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#wasExcluded()} method
	 */
    public void testWasExcluded() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertFalse("The testing component was not excluded", scomp1.wasExcluded());
            scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), null);
            //$NON-NLS-1$
            assertTrue("The testing component was excluded", scomp1.wasExcluded());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#hasResolutionErrors()} method
	 */
    public void testHasResolutionErrors() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), null);
            //$NON-NLS-1$
            assertFalse("The testing component did have resolution errors", scomp1.hasResolutionErrors());
            scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertTrue("The testing component did not have resolution errors", scomp1.hasResolutionErrors());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getAncestor(int)} method
	 */
    public void testGetAncestor() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertNull("there should be no ancestors for SkippedComponents", scomp1.getAncestor(IApiElement.COMPONENT));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getApiComponent()} method
	 */
    public void testGetApiComponent() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertNull("there should be no IApiComponent object for SkippedComponents", scomp1.getApiComponent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getName()} method
	 */
    public void testGetName() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The names should be equal", tcomp.getSymbolicName(), scomp1.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getParent()} method
	 */
    public void testGetParent() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertNull("there should be no parentt object for SkippedComponents", scomp1.getParent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getType()} method
	 */
    public void testGetType() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The type should be IApiElement.COMPONENT", IApiElement.COMPONENT, scomp1.getType());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getVersion()} method
	 */
    public void testGetVersion() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertEquals("The version should be 1.0.0", scomp1.getVersion(), DEFAULT_VERSION);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getErrors()} method
	 */
    public void testGetErrors() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertNotNull("There should be resolution errors for the testing component", scomp1.getErrors());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests the {@link SkippedComponent#getErrorDetails()} method
	 */
    public void testGetErrorDetails() {
        try {
            IApiComponent tcomp = getTestingComponent();
            //$NON-NLS-1$
            assertNotNull("The testing component should not be null", tcomp);
            SkippedComponent scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), tcomp.getErrors());
            //$NON-NLS-1$
            assertNotNull("There should be resolution errors for the testing component", scomp1.getErrors());
            String reason = scomp1.getErrorDetails();
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue("The reason should be because of a unresolved constraint", reason.startsWith("Require-Bundle:"));
            scomp1 = new SkippedComponent(tcomp.getSymbolicName(), tcomp.getVersion(), null);
            //$NON-NLS-1$
            assertNull("There should be no errors for the testing component", scomp1.getErrors());
            reason = scomp1.getErrorDetails();
            //$NON-NLS-1$ //$NON-NLS-2$
            assertTrue("The reason should be because it was exclude", reason.startsWith("This component was excluded from the search by the search parameters."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
