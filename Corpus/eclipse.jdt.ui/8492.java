/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brock Janiczak (brockj@tpg.com.au)
 *         - https://bugs.eclipse.org/bugs/show_bug.cgi?id=102236: [JUnit] display execution time next to each test
 *     Neale Upstone <neale@nealeupstone.com> - [JUnit] JUnit viewer doesn't recognise <skipped/> node - https://bugs.eclipse.org/bugs/show_bug.cgi?id=276068
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.model;

public interface IXMLTags {

    //$NON-NLS-1$
    public static final String NODE_TESTRUN = "testrun";

    //$NON-NLS-1$
    public static final String NODE_TESTSUITES = "testsuites";

    //$NON-NLS-1$
    public static final String NODE_TESTSUITE = "testsuite";

    //$NON-NLS-1$
    public static final String NODE_PROPERTIES = "properties";

    //$NON-NLS-1$
    public static final String NODE_PROPERTY = "property";

    //$NON-NLS-1$
    public static final String NODE_TESTCASE = "testcase";

    //$NON-NLS-1$
    public static final String NODE_ERROR = "error";

    //$NON-NLS-1$
    public static final String NODE_FAILURE = "failure";

    //$NON-NLS-1$
    public static final String NODE_EXPECTED = "expected";

    //$NON-NLS-1$
    public static final String NODE_ACTUAL = "actual";

    //$NON-NLS-1$
    public static final String NODE_SYSTEM_OUT = "system-out";

    //$NON-NLS-1$
    public static final String NODE_SYSTEM_ERR = "system-err";

    //$NON-NLS-1$
    public static final String NODE_SKIPPED = "skipped";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_NAME = "name";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_PROJECT = "project";

    /**
	 * value: Integer
	 */
    //$NON-NLS-1$
    public static final String ATTR_TESTS = "tests";

    /**
	 * value: Integer
	 */
    //$NON-NLS-1$
    public static final String ATTR_STARTED = "started";

    /**
	 * value: Integer
	 */
    //$NON-NLS-1$
    public static final String ATTR_FAILURES = "failures";

    /**
	 * value: Integer
	 */
    //$NON-NLS-1$
    public static final String ATTR_ERRORS = "errors";

    /**
	 * value: Boolean
	 */
    //$NON-NLS-1$
    public static final String ATTR_IGNORED = "ignored";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_PACKAGE = "package";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_ID = "id";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_CLASSNAME = "classname";

    /**
	 * value: Boolean
	 */
    //$NON-NLS-1$
    public static final String ATTR_INCOMPLETE = "incomplete";

    /**
	 * value: Double
	 */
    //$NON-NLS-1$
    public static final String ATTR_TIME = "time";

    /**
	 * value: String
	 */
    //$NON-NLS-1$
    public static final String ATTR_MESSAGE = "message";
    //	public static final String ATTR_TYPE= "type"; //$NON-NLS-1$
}
