/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.annotations;

import junit.framework.Test;
import org.eclipse.core.runtime.IPath;

/**
 * Tests a variety of valid annotation use on classes
 *
 * @since 1.0.400
 */
public class ValidClassAnnotationsTests extends InvalidClassAnnotationsTests {

    /**
	 * Constructor
	 *
	 * @param name
	 */
    public  ValidClassAnnotationsTests(String name) {
        super(name);
    }

    /**
	 * @return the tests for this class
	 */
    public static Test suite() {
        return buildTestSuite(ValidClassAnnotationsTests.class);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.annotations.
	 * InvalidClassAnnotationsTests#getTestSourcePath()
	 */
    @Override
    protected IPath getTestSourcePath() {
        //$NON-NLS-1$
        return super.getTestSourcePath().append("valid");
    }

    /**
	 * Tests all the valid annotations on a class
	 *
	 * @throws Exception
	 */
    public void testValidClassAnnotations1I() throws Exception {
        //$NON-NLS-1$
        deployAnnotationTest("test1.java", true, false);
    }

    /**
	 * Tests all the valid annotations on a class
	 *
	 * @throws Exception
	 */
    public void testValidClassAnnotations1F() throws Exception {
        //$NON-NLS-1$
        deployAnnotationTest("test1.java", false, false);
    }

    /**
	 * Tests all the valid annotations on a class in the default package
	 *
	 * @throws Exception
	 */
    public void testValidClassAnnotations2I() throws Exception {
        //$NON-NLS-1$
        deployAnnotationTest("test5.java", true, true);
    }

    /**
	 * Tests all the valid annotations on a class in the default package
	 *
	 * @throws Exception
	 */
    public void testValidClassAnnotations2F() throws Exception {
        //$NON-NLS-1$
        deployAnnotationTest("test5.java", false, true);
    }
}
