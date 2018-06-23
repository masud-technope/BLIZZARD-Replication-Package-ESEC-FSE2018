/*******************************************************************************
 * Copyright (c) Mar 26, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.model.tests;

import java.io.File;
import java.util.List;
import junit.framework.Test;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.provisional.builder.IReference;
import org.eclipse.test.OrderedTestSuite;

/**
 * Tests reading JJava 8 classfiles and extracting specific references
 *
 * @since 1.0.400
 */
public class Java8ClassfileScannerTests extends ScannerTest {

    //$NON-NLS-1$
    private static IPath WORKSPACE_ROOT = TestSuiteHelper.getPluginDirectoryPath().append("test_classes_workspace_java8");

    //$NON-NLS-1$ //$NON-NLS-2$
    private static IPath ROOT_PATH = TestSuiteHelper.getPluginDirectoryPath().append("test-source").append("invokedynamic");

    /**
	 * Returns the {@link Test} suite to run
	 *
	 * @return the {@link Test} suite
	 */
    public static Test suite() {
        return new OrderedTestSuite(Java8ClassfileScannerTests.class, new String[] { "testStaticMethodRef", "testInstanceMethodRef", "testArbitraryObjectMethodRef", "testConstructorMethodRef", //$NON-NLS-1$
        "testCleanup" });
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.model.tests.ScannerTest#getWorkspaceRoot()
	 */
    @Override
    protected IPath getWorkspaceRoot() {
        return WORKSPACE_ROOT;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.model.tests.ScannerTest#getSourcePath()
	 */
    @Override
    protected IPath getSourcePath() {
        return ROOT_PATH;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.model.tests.ScannerTest#getPackageName()
	 */
    @Override
    protected String getPackageName() {
        //$NON-NLS-1$
        return "invokedynamic";
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.model.tests.ScannerTest#doCompile()
	 */
    @Override
    protected boolean doCompile() {
        boolean result = true;
        String[] sourceFilePaths = new String[] { ROOT_PATH.toOSString() };
        result &= TestSuiteHelper.compile(sourceFilePaths, WORKSPACE_ROOT.toOSString(), TestSuiteHelper.getCompilerOptions());
        //$NON-NLS-1$
        assertTrue("working directory should compile", result);
        return true;
    }

    /**
	 * Tests getting an invoke dynamic ref for a static method ref
	 *
	 * @throws Exception
	 */
    public void testStaticMethodRef() throws Exception {
        //$NON-NLS-1$
        List<IReference> refs = getRefSet("test1");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        IReference ref = findMemberReference("invokedynamic.test1", "m1", "invokedynamic.test1$MR", "mrCompare", IReference.REF_VIRTUALMETHOD, refs);
        //$NON-NLS-1$
        assertTrue("There should be a ref for invokedynamic.test1$MR#mrCompare", ref != null);
    }

    /**
	 * Tests getting an invoke dynamic ref for an instance method ref
	 *
	 * @throws Exception
	 */
    public void testInstanceMethodRef() throws Exception {
        //$NON-NLS-1$
        List<IReference> refs = getRefSet("test2");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        IReference ref = findMemberReference("invokedynamic.test2", "m1", "invokedynamic.test2$MR", "mrCompare", IReference.REF_VIRTUALMETHOD, refs);
        //$NON-NLS-1$
        assertTrue("There should be a ref for invokedynamic.test2$MR#mrCompare", ref != null);
    }

    /**
	 * Tests an invoke dynamic reference to an instance method of an arbitrary
	 * object
	 *
	 * @throws Exception
	 */
    public void testArbitraryObjectMethodRef() throws Exception {
        //$NON-NLS-1$
        List<IReference> refs = getRefSet("test3");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        IReference ref = findMemberReference("invokedynamic.test3", "m1", "java.lang.String", "compareToIgnoreCase", IReference.REF_VIRTUALMETHOD, refs);
        //$NON-NLS-1$
        assertTrue("There should be a ref for String#compareToIgnoreCase", ref != null);
    }

    /**
	 * Tests an invoke dynamic reference to a constructor method ref
	 *
	 * @throws Exception
	 */
    public void testConstructorMethodRef() throws Exception {
        //$NON-NLS-1$
        List<IReference> refs = getRefSet("test4");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        IReference ref = findMemberReference("invokedynamic.test4", "m1", "java.util.HashSet", "<init>", IReference.REF_VIRTUALMETHOD, refs);
        //$NON-NLS-1$
        assertTrue("There should be a ref for HashSet#<init>", ref != null);
    }

    /**
	 * Cleans up after the tests are done. This must be the last test run
	 *
	 * @throws Exception
	 */
    public void testCleanup() throws Exception {
        cleanUp();
        // remove workspace root
        assertTrue(TestSuiteHelper.delete(new File(WORKSPACE_ROOT.toOSString())));
    }
}
