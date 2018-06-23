/*******************************************************************************
 * Copyright (c) 2014 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.eclipse.ecf.core.util.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public abstract class RndStatsTestCase extends TestCase {

    // Run each test ITERATION multiple times
    private static final int ITERATIONS = Integer.getInteger("RndStatsTestCase.iterations", 1).intValue();

    // Seed (indirectly) determines test order
    private static final long SEED = Long.getLong("RndStatsTestCase.seed", System.currentTimeMillis()).longValue();

    // An inclusion filter to only run testa matching this substring (case sensitive!)
    private static final String FILTER = System.getProperty("RndStatsTestCase.filter", "");

    /**
	 * Subclass AllTests calls this method (it differs from the regular static
	 * {@link TestCase#suite()} method in the parameter {@link TestSuite}) prior
	 * to returing the actual {@link TestSuite} in its standard method to set up
	 * the {@link TestSuite}.
	 * 
	 * @param suite
	 *            With test classes to be executed
	 * @return A randomized {@link TestSuite} with all tests contained times
	 *         {@link RndStatsTestCase#ITERATIONS}
	 */
    public static Test suite(TestSuite suite) {
        // Add all tests of all test classes to the list
        final /*<Test>*/
        List tests = new ArrayList();
        final /*<TestSuite>*/
        List suites = Collections.list(suite.tests());
        for (int i = 0; i < ITERATIONS; i++) {
            for (final Iterator itr = suites.iterator(); itr.hasNext(); ) {
                final TestSuite aSuite = (TestSuite) itr.next();
                // Callee just passes a TestSuite of classes. tests() returns
                // all real tests of current TestSuite.
                /*<Test>*/
                final List testsOfClass = Collections.list(aSuite.tests());
                if ("".equals(FILTER)) {
                    tests.addAll(testsOfClass);
                } else {
                    for (final Iterator itr2 = testsOfClass.iterator(); itr2.hasNext(); ) {
                        final Test t = (Test) itr2.next();
                        if (StringUtils.contains(t.toString(), FILTER)) {
                            tests.add(t);
                        }
                    }
                }
            }
        }
        // shuffle the list to create randomized test order
        System.out.println("Seed used for test ordering: " + SEED);
        Collections.shuffle(tests, new Random(SEED));
        // Create empty test suite and add tests in order of shuffeled list
        suite = new MyTestSuite(RndStatsTestCase.class.getName());
        for (final Iterator itr = tests.iterator(); itr.hasNext(); ) {
            final Test t = (Test) itr.next();
            suite.addTest(t);
        }
        return suite;
    }

    /**
	 * Hooks into JUnit to register a {@link TestListener} and to print
	 * statistics at end of {@link TestSuite}
	 */
    public static class MyTestSuite extends TestSuite {

        private StatisticalTestListener statisticalTestListener;

        public  MyTestSuite(String name) {
            super(name);
            statisticalTestListener = new StatisticalTestListener();
        }

        public void run(TestResult result) {
            result.addListener(statisticalTestListener);
            // This runs all the tests added in suite(TestSuite)
            super.run(result);
            // Finally, all tests have ended
            System.out.println("Executions/Failures/Errors/Test name");
            final Collection values = statisticalTestListener.results.values();
            for (Iterator itr = values.iterator(); itr.hasNext(); ) {
                StatisticalTestListener.Result v = (StatisticalTestListener.Result) itr.next();
                System.out.println(v.cnt + "/" + v.failures.size() + "/" + v.errors.size() + " :" + v.t.toString() + (v.failures.size() > 0 ? " with predecessors: " + v.failures : ""));
            }
            System.out.println("Total: " + values.size());
        }

        /**
		 * Aggregates test results in {@link Result} per {@link Test}
		 */
        public static class StatisticalTestListener implements TestListener {

            // Use test's name (method + class) as key to aggregate multiple
            // executions of the same test.
            public final /*<String, Result>*/
            Map results;

            private Test predecessor;

            public  StatisticalTestListener() {
                results = new HashMap();
            }

            public void addError(Test test, Throwable t) {
                final Result r = (Result) results.get(test.toString());
                r.errors.add(t);
            }

            public void addFailure(Test test, AssertionFailedError t) {
                final Result r = (Result) results.get(test.toString());
                r.failures.add(new Failure(t, predecessor));
            }

            public void endTest(Test test) {
                predecessor = test;
            }

            public void startTest(Test test) {
                if (!results.containsKey(test.toString())) {
                    final Result value = new Result();
                    value.t = test.toString();
                    value.tests.add(test);
                    value.cnt += 1;
                    results.put(test.toString(), value);
                } else {
                    final Result val = (Result) results.get(test.toString());
                    val.cnt += 1;
                }
            }

            /**
			 * Struct-like holder for aggregated test results
			 */
            public static class Result {

                public String t;

                public int cnt = 0;

                public final List tests = new ArrayList();

                public final List errors = new ArrayList();

                public final List failures = new ArrayList();
            }

            public static class Failure {

                public AssertionFailedError afe;

                public Test predecessor;

                public  Failure(AssertionFailedError t, Test predecessor) {
                    this.afe = t;
                    this.predecessor = predecessor;
                }

                public String toString() {
                    return "Failure [afe=" + afe + ", predecessor=" + predecessor + "]";
                }
            }
        }
    }
}
