/*******************************************************************************
 * Copyright (c) 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - initial API and implementation
 *             (bug 102632: [JUnit] Support for JUnit 4.)
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.runner.junit3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.internal.junit.runner.FailuresFirstPrioritizer;
import org.eclipse.jdt.internal.junit.runner.ITestLoader;
import org.eclipse.jdt.internal.junit.runner.ITestPrioritizer;
import org.eclipse.jdt.internal.junit.runner.ITestReference;
import org.eclipse.jdt.internal.junit.runner.JUnitMessages;
import org.eclipse.jdt.internal.junit.runner.NullPrioritizer;
import org.eclipse.jdt.internal.junit.runner.RemoteTestRunner;

public class JUnit3TestLoader implements ITestLoader {

    //$NON-NLS-1$
    private static final String SUITE_METHODNAME = "suite";

    //$NON-NLS-1$
    public static final String SET_UP_TEST_METHOD_NAME = "setUpTest";

    // WANT: give test loaders a schema
    public ITestReference[] loadTests(Class[] testClasses, String testName, String[] failureNames, RemoteTestRunner listener) {
        // instantiate all tests
        ITestReference[] suites = new ITestReference[testClasses.length];
        ITestPrioritizer prioritizer;
        if (failureNames != null)
            prioritizer = new FailuresFirstPrioritizer(failureNames);
        else
            prioritizer = new NullPrioritizer();
        for (int i = 0; i < suites.length; i++) {
            Class testClassName = testClasses[i];
            Test test = getTest(testClassName, testName, listener);
            prioritizer.prioritize(test);
            suites[i] = new JUnit3TestReference(test);
        }
        return suites;
    }

    private Test createTest(String testName, Class testClass) {
        Class[] classArgs = { String.class };
        Test test;
        Constructor constructor = null;
        try {
            try {
                constructor = testClass.getConstructor(classArgs);
                test = (Test) constructor.newInstance(new Object[] { testName });
            } catch (NoSuchMethodException e) {
                constructor = testClass.getConstructor(new Class[0]);
                test = (Test) constructor.newInstance(new Object[0]);
                if (test instanceof TestCase)
                    ((TestCase) test).setName(testName);
            }
            if (test != null)
                return test;
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        } catch (ClassCastException e) {
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        return error(testName, "Could not create test \'" + testName + "\' ");
    }

    public Test getTest(Class testClass, String testName, RemoteTestRunner failureListener) {
        if (testName != null) {
            return setupTest(testClass, createTest(testName, testClass), testName);
        }
        Method suiteMethod = null;
        try {
            suiteMethod = testClass.getMethod(JUnit3TestLoader.SUITE_METHODNAME, new Class[0]);
        } catch (Exception e) {
            return new TestSuite(testClass);
        }
        if (!Modifier.isStatic(suiteMethod.getModifiers())) {
            //$NON-NLS-1$ //$NON-NLS-2$
            return error(JUnitMessages.getString("RemoteTestRunner.error"), JUnitMessages.getString("RemoteTestRunner.error.suite.notstatic"));
        }
        try {
            Test test = (Test) // static
            suiteMethod.invoke(// static
            null, // static
            new Class[0]);
            if (test != null) {
                return test;
            }
            //$NON-NLS-1$ //$NON-NLS-2$
            return error(JUnitMessages.getString("RemoteTestRunner.error"), JUnitMessages.getString("RemoteTestRunner.error.suite.nullreturn"));
        } catch (InvocationTargetException e) {
            String message = JUnitMessages.getFormattedString("RemoteTestRunner.error.invoke", e.getTargetException().toString());
            failureListener.runFailed(message, e);
            return new TestSuite(testClass);
        } catch (IllegalAccessException e) {
            String message = JUnitMessages.getFormattedString("RemoteTestRunner.error.invoke", e.toString());
            failureListener.runFailed(message, e);
            return new TestSuite(testClass);
        }
    }

    /**
	 * Prepare a single test to be run standalone. If the test case class
	 * provides a static method Test setUpTest(Test test) then this method will
	 * be invoked. Instead of calling the test method directly the "decorated"
	 * test returned from setUpTest will be called. The purpose of this
	 * mechanism is to enable tests which requires a set-up to be run
	 * individually.
	 *
	 * @param reloadedTestClass test class
	 * @param reloadedTest test instance
	 * @param testName test name
	 * @return the reloaded test, or the test wrapped with setUpTest(..) if available
	 */
    private Test setupTest(Class reloadedTestClass, Test reloadedTest, String testName) {
        if (reloadedTestClass == null)
            return reloadedTest;
        Method setup = null;
        try {
            setup = reloadedTestClass.getMethod(SET_UP_TEST_METHOD_NAME, new Class[] { Test.class });
        } catch (SecurityException e1) {
            return reloadedTest;
        } catch (NoSuchMethodException e) {
            return reloadedTest;
        }
        if (setup.getReturnType() != Test.class)
            //$NON-NLS-1$
            return error(testName, JUnitMessages.getString("RemoteTestRunner.error.notestreturn"));
        if (!Modifier.isPublic(setup.getModifiers()))
            //$NON-NLS-1$
            return error(testName, JUnitMessages.getString("RemoteTestRunner.error.shouldbepublic"));
        if (!Modifier.isStatic(setup.getModifiers()))
            //$NON-NLS-1$
            return error(testName, JUnitMessages.getString("RemoteTestRunner.error.shouldbestatic"));
        try {
            Test test = (Test) setup.invoke(null, new Object[] { reloadedTest });
            if (test == null)
                return error(//$NON-NLS-1$
                testName, //$NON-NLS-1$
                JUnitMessages.getString("RemoteTestRunner.error.nullreturn"));
            return test;
        } catch (IllegalArgumentException e) {
            return error(testName, JUnitMessages.getFormattedString("RemoteTestRunner.error.couldnotinvoke", e));
        } catch (IllegalAccessException e) {
            return error(testName, JUnitMessages.getFormattedString("RemoteTestRunner.error.couldnotinvoke", e));
        } catch (InvocationTargetException e) {
            return error(testName, JUnitMessages.getFormattedString("RemoteTestRunner.error.invocationexception", e.getTargetException()));
        }
    }

    /**
	 * @param testName test name
	 * @param message error message
	 * @return a test which will fail and log an error message.
	 */
    private Test error(String testName, final String message) {
        return new TestCase(testName) {

            protected void runTest() {
                fail(message);
            }
        };
    }
}
