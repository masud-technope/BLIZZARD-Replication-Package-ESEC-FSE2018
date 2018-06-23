/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.compatibility;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.api.tools.internal.problems.ApiProblemFactory;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;
import junit.framework.Test;

/**
 * Tests that the builder correctly reports compatibility problems related to
 * method type parameters and varargs.
 *
 * @since 1.0
 */
public class MethodCompatibilityTypeParameterTests extends MethodCompatibilityTests {

    /**
	 * Workspace relative path classes in bundle/project A
	 */
    //$NON-NLS-1$
    protected static IPath WORKSPACE_CLASSES_PACKAGE_A = new Path("bundle.a/src/a/methods/typeparameters");

    /**
	 * Package prefix for test classes
	 */
    //$NON-NLS-1$
    protected static String PACKAGE_PREFIX = "a.methods.typeparameters.";

    /**
	 * Constructor
	 * @param name
	 */
    public  MethodCompatibilityTypeParameterTests(String name) {
        super(name);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestSourcePath()
	 */
    @Override
    protected IPath getTestSourcePath() {
        //$NON-NLS-1$
        return super.getTestSourcePath().append("typeparameters");
    }

    /**
	 * @return the tests for this class
	 */
    public static Test suite() {
        return buildTestSuite(MethodCompatibilityTypeParameterTests.class);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestingProjectName()
	 */
    @Override
    protected String getTestingProjectName() {
        //$NON-NLS-1$
        return "classcompat";
    }

    /**
	 * Tests adding the first type parameter to a method -- a compatible change.
	 */
    private void xAddFirstTypeParameter(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("AddFirstTypeParameter.java");
        performCompatibilityTest(filePath, incremental);
    }

    public void testAddFirstTypeParameterI() throws Exception {
        xAddFirstTypeParameter(true);
    }

    public void testAddFirstTypeParameterF() throws Exception {
        xAddFirstTypeParameter(false);
    }

    /**
	 * Tests adding a type parameter to a method
	 */
    private void xAddTypeParameter(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("AddTypeParameter.java");
        int[] ids = new int[] { ApiProblemFactory.createProblemId(IApiProblem.CATEGORY_COMPATIBILITY, IDelta.METHOD_ELEMENT_TYPE, IDelta.ADDED, IDelta.TYPE_PARAMETER) };
        setExpectedProblemIds(ids);
        String[][] args = new String[1][];
        //$NON-NLS-1$ //$NON-NLS-2$
        args[0] = new String[] { PACKAGE_PREFIX + "AddTypeParameter.method(Object)", "U" };
        setExpectedMessageArgs(args);
        performCompatibilityTest(filePath, incremental);
    }

    public void testAddTypeParameterI() throws Exception {
        xAddTypeParameter(true);
    }

    public void testAddTypeParameterF() throws Exception {
        xAddTypeParameter(false);
    }

    /**
	 * Tests removing a type parameter from a method
	 */
    private void xRemoveTypeParameter(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("RemoveTypeParameter.java");
        int[] ids = new int[] { ApiProblemFactory.createProblemId(IApiProblem.CATEGORY_COMPATIBILITY, IDelta.METHOD_ELEMENT_TYPE, IDelta.REMOVED, IDelta.TYPE_PARAMETER) };
        setExpectedProblemIds(ids);
        String[][] args = new String[1][];
        //$NON-NLS-1$ //$NON-NLS-2$
        args[0] = new String[] { PACKAGE_PREFIX + "RemoveTypeParameter.method(Object)", "U" };
        setExpectedMessageArgs(args);
        performCompatibilityTest(filePath, incremental);
    }

    public void testRemoveTypeParameterI() throws Exception {
        xRemoveTypeParameter(true);
    }

    public void testRemoveTypeParameterF() throws Exception {
        xRemoveTypeParameter(false);
    }

    /**
	 * Tests converting variable arguments to an array
	 */
    private void xVarArgsToArray(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("VarArgsToArray.java");
        int[] ids = new int[] { ApiProblemFactory.createProblemId(IApiProblem.CATEGORY_COMPATIBILITY, IDelta.METHOD_ELEMENT_TYPE, IDelta.CHANGED, IDelta.VARARGS_TO_ARRAY) };
        setExpectedProblemIds(ids);
        String[][] args = new String[1][];
        //$NON-NLS-1$ //$NON-NLS-2$
        args[0] = new String[] { PACKAGE_PREFIX + "VarArgsToArray", "method(int, int[])" };
        setExpectedMessageArgs(args);
        performCompatibilityTest(filePath, incremental);
    }

    public void testVarArgsToArrayI() throws Exception {
        xVarArgsToArray(true);
    }

    public void testVarArgsToArrayF() throws Exception {
        xVarArgsToArray(false);
    }

    /**
	 * Tests converting an array to variable arguments
	 */
    private void xArrayToVarArgs(boolean incremental) throws Exception {
        //$NON-NLS-1$
        IPath filePath = WORKSPACE_CLASSES_PACKAGE_A.append("ArrayToVarArgs.java");
        // no problems
        performCompatibilityTest(filePath, incremental);
    }

    public void testArrayToVarArgsI() throws Exception {
        xArrayToVarArgs(true);
    }

    public void testArrayToVarArgsF() throws Exception {
        xArrayToVarArgs(false);
    }
}
