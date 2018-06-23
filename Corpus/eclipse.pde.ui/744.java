/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.compatibility;

import junit.framework.Test;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.api.tools.internal.problems.ApiProblemFactory;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;

/**
 * Tests that the builder correctly reports compatibility problems
 * related to member types.
 *
 * @since 1.0
 */
public class InterfaceCompatibilityRestrictionTests extends InterfaceCompatibilityTests {

    /**
	 * Workspace relative path classes in bundle/project A
	 */
    //$NON-NLS-1$
    protected static IPath WORKSPACE_CLASSES_PACKAGE_A = new Path("bundle.a/src/a/interfaces/restrictions");

    /**
	 * Package prefix for test classes
	 */
    //$NON-NLS-1$
    protected static String PACKAGE_PREFIX = "a.interfaces.restrictions.";

    /**
	 * Constructor
	 * @param name
	 */
    public  InterfaceCompatibilityRestrictionTests(String name) {
        super(name);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestSourcePath()
	 */
    @Override
    protected IPath getTestSourcePath() {
        //$NON-NLS-1$
        return super.getTestSourcePath().append("restrictions");
    }

    /**
	 * @return the tests for this class
	 */
    public static Test suite() {
        return buildTestSuite(InterfaceCompatibilityRestrictionTests.class);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#getDefaultProblemId()
	 */
    @Override
    protected int getDefaultProblemId() {
        return ApiProblemFactory.createProblemId(IApiProblem.CATEGORY_COMPATIBILITY, IDelta.INTERFACE_ELEMENT_TYPE, IDelta.ADDED, IDelta.RESTRICTIONS);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestingProjectName()
	 */
    @Override
    protected String getTestingProjectName() {
        //$NON-NLS-1$
        return "intercompat";
    }

    public void testRemoveNoImplementI() throws Exception {
        xRemoveNoImplement(true);
    }

    public void testRemoveNoImplementF() throws Exception {
        xRemoveNoImplement(false);
    }

    /**
	 * Tests removing a noimplement annotation
	 */
    private void xRemoveNoImplement(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("RemoveNoImplement.java");
        // no problem expected
        performCompatibilityTest(filePath, incremental);
    }

    public void testAddNoImplementI() throws Exception {
        xAddNoImplement(true);
    }

    public void testAddNoImplementF() throws Exception {
        xAddNoImplement(false);
    }

    /**
	 * Test adding a noimplement annotation
	 */
    private void xAddNoImplement(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("AddNoImplement.java");
        int[] ids = new int[] { getDefaultProblemId() };
        setExpectedProblemIds(ids);
        String[][] args = new String[1][];
        //$NON-NLS-1$
        args[0] = new String[] { PACKAGE_PREFIX + "AddNoImplement" };
        setExpectedMessageArgs(args);
        performCompatibilityTest(filePath, incremental);
    }

    /**
	 * Tests removing a noextend annotation using an incremental build
	 */
    public void testRemoveNoExtendI() {
        xRemoveNoExtend(true);
    }

    /**
	 * Tests removing a noextend annotation using a full build
	 */
    public void testRemoveNoExtendF() {
        xRemoveNoExtend(false);
    }

    private void xRemoveNoExtend(boolean inc) {
        try {
            //$NON-NLS-1$
            IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("RemoveNoExtend.java");
            // no problem expected
            performCompatibilityTest(filePath, inc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests adding a noextend annotation using an incremental build
	 */
    public void testAddNoExtendI() {
        xAddNoExtend(true);
    }

    /**
	 * Tests adding a noextend annotation using a full build
	 */
    public void testAddNoExtendF() {
        xAddNoExtend(false);
    }

    private void xAddNoExtend(boolean inc) {
        try {
            //$NON-NLS-1$
            IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("AddNoExtend.java");
            int[] ids = new int[] { getDefaultProblemId() };
            setExpectedProblemIds(ids);
            String[][] args = new String[1][];
            //$NON-NLS-1$
            args[0] = new String[] { PACKAGE_PREFIX + "AddNoExtend" };
            setExpectedMessageArgs(args);
            performCompatibilityTest(filePath, inc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests adding both noextend and noimplement annotations using an incremental build
	 */
    public void testAddNoExtendNoImplementI() {
        xAddNoExtendNoImplement(true);
    }

    /**
	 * Tests adding both noextend and noimplement annotations using a full build
	 */
    public void testAddNoExtendNoImplementF() {
        xAddNoExtendNoImplement(false);
    }

    private void xAddNoExtendNoImplement(boolean inc) {
        try {
            //$NON-NLS-1$
            IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("AddNoExtendNoImplement.java");
            int[] ids = new int[] { getDefaultProblemId() };
            setExpectedProblemIds(ids);
            String[][] args = new String[1][];
            //$NON-NLS-1$
            args[0] = new String[] { PACKAGE_PREFIX + "AddNoExtendNoImplement" };
            setExpectedMessageArgs(args);
            performCompatibilityTest(filePath, inc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
	 * Tests removing both noextend and noimplement annotations using an incremental build
	 */
    public void testRemoveNoExtendNoImplementI() {
        xRemoveNoExtendNoImplement(true);
    }

    /**
	 * Tests removing both noextend and noimplement annotations using a full build
	 */
    public void testRemoveNoExtendNoImplementF() {
        xRemoveNoExtendNoImplement(false);
    }

    private void xRemoveNoExtendNoImplement(boolean inc) {
        try {
            //$NON-NLS-1$
            IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("RemoveNoExtendNoImplement.java");
            // no problem expected
            performCompatibilityTest(filePath, inc);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
