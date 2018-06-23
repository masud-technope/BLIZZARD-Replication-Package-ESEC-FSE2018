/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.runner;

/**
 * Message identifiers for messages sent by the
 * RemoteTestRunner.
 *
 * @see RemoteTestRunner
 */
public class MessageIds {

    /**
	 * The header length of a message, all messages
	 * have a fixed header length
	 */
    public static final int MSG_HEADER_LENGTH = 8;

    /**
	 * Notification that a test trace has started.
	 * The end of the trace is signaled by a TRACE_END
	 * message. In between the TRACE_START and TRACE_END
	 * the stack trace is submitted as multiple lines.
	 */
    //$NON-NLS-1$
    public static final String TRACE_START = "%TRACES ";

    /**
	 * Notification that a trace ends.
	 */
    //$NON-NLS-1$
    public static final String TRACE_END = "%TRACEE ";

    /**
	 * Notification that the expected result has started.
	 * The end of the expected result is signaled by a Trace_END.
	 */
    //$NON-NLS-1$
    public static final String EXPECTED_START = "%EXPECTS";

    /**
	 * Notification that an expected result ends.
	 */
    //$NON-NLS-1$
    public static final String EXPECTED_END = "%EXPECTE";

    /**
	 * Notification that the expected result has started.
	 * The end of the expected result is signaled by a Trace_END.
	 */
    //$NON-NLS-1$
    public static final String ACTUAL_START = "%ACTUALS";

    /**
	 * Notification that an expected result ends.
	 */
    //$NON-NLS-1$
    public static final String ACTUAL_END = "%ACTUALE";

    /**
	 * Notification that a trace for a reran test has started.
	 * The end of the trace is signaled by a RTrace_END
	 * message.
	 */
    //$NON-NLS-1$
    public static final String RTRACE_START = "%RTRACES";

    /**
	 * Notification that a trace of a reran trace ends.
	 */
    //$NON-NLS-1$
    public static final String RTRACE_END = "%RTRACEE";

    /**
	 * Notification that a test run has started.
	 * MessageIds.TEST_RUN_START + testCount.toString + " " + version
	 */
    //$NON-NLS-1$
    public static final String TEST_RUN_START = "%TESTC  ";

    /**
	 * Notification that a test has started.
	 * MessageIds.TEST_START + testID + "," + testName
	 */
    //$NON-NLS-1$
    public static final String TEST_START = "%TESTS  ";

    /**
	 * Notification that a test has started.
	 * TEST_END + testID + "," + testName
	 */
    //$NON-NLS-1$
    public static final String TEST_END = "%TESTE  ";

    /**
	 * Notification that a test had a error.
	 * TEST_ERROR + testID + "," + testName.
	 * After the notification follows the stack trace.
	 */
    //$NON-NLS-1$
    public static final String TEST_ERROR = "%ERROR  ";

    /**
	 * Notification that a test had a failure.
	 * TEST_FAILED + testID + "," + testName.
	 * After the notification follows the stack trace.
	 */
    //$NON-NLS-1$
    public static final String TEST_FAILED = "%FAILED ";

    /**
	 * Notification that a test run has ended.
	 * TEST_RUN_END + elapsedTime.toString().
	 */
    //$NON-NLS-1$
    public static final String TEST_RUN_END = "%RUNTIME";

    /**
	 * Notification that a test run was successfully stopped.
	 */
    //$NON-NLS-1$
    public static final String TEST_STOPPED = "%TSTSTP ";

    /**
	 * Notification that a test was reran.
	 * TEST_RERAN + testId + " " + testClass + " " + testName + STATUS.
	 * Status = "OK" or "FAILURE".
	 */
    //$NON-NLS-1$
    public static final String TEST_RERAN = "%TSTRERN";

    /**
	 * Notification about a test inside the test suite.
	 * TEST_TREE + testId + "," + testName + "," + isSuite + "," + testcount
	 * isSuite = "true" or "false"
	 */
    //$NON-NLS-1$
    public static final String TEST_TREE = "%TSTTREE";

    /**
	 * Request to stop the current test run.
	 */
    //$NON-NLS-1$
    public static final String TEST_STOP = ">STOP   ";

    /**
	 * Request to rerun a test.
	 * TEST_RERUN + testId + " " + testClass + " "+testName
	 */
    //$NON-NLS-1$
    public static final String TEST_RERUN = ">RERUN  ";

    /**
	 * MessageFormat to encode test method identifiers:
	 * testMethod(testClass)
	 */
    //$NON-NLS-1$
    public static final String TEST_IDENTIFIER_MESSAGE_FORMAT = "{0}({1})";

    /**
	 * Test identifier prefix for ignored tests.
	 */
    //$NON-NLS-1$
    public static final String IGNORED_TEST_PREFIX = "@Ignore: ";

    /**
	 * Test identifier prefix for tests with assumption failures.
	 */
    //$NON-NLS-1$
    public static final String ASSUMPTION_FAILED_TEST_PREFIX = "@AssumptionFailure: ";
}
