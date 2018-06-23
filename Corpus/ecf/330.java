/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.protocol.msn;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.ecf.tests.protocol.msn.internal.ChallengeTest;
import org.eclipse.ecf.tests.protocol.msn.internal.EncryptionTest;
import org.eclipse.ecf.tests.protocol.msn.internal.StringUtilsTest;

public class AllTests extends TestCase {

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ChallengeTest.class);
        suite.addTestSuite(EncryptionTest.class);
        suite.addTestSuite(StringUtilsTest.class);
        return suite;
    }
}
