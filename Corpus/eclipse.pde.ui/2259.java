/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.tests.target;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTargetTests {

    public static Test suite() {
        //$NON-NLS-1$
        TestSuite suite = new TestSuite("Test Suite for testing targets");
        suite.addTest(TargetEnvironmentTestCase.suite());
        suite.addTest(TargetPlatformHelperTests.suite());
        suite.addTest(LocalTargetDefinitionTests.suite());
        suite.addTest(WorkspaceTargetDefinitionTests.suite());
        suite.addTest(TargetDefinitionPersistenceTests.suite());
        suite.addTest(TargetDefinitionResolutionTests.suite());
        suite.addTest(TargetDefinitionFeatureResolutionTests.suite());
        suite.addTest(IUBundleContainerTests.suite());
        return suite;
    }
}
