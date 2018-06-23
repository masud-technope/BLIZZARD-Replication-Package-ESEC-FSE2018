package org.eclipse.ecf.tests.provider.discovery;

import org.eclipse.ecf.tests.discovery.RndStatsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends RndStatsTestCase {

    private static final boolean IS_ON = Boolean.getBoolean("org.eclipse.ecf.tests.provider.discovery.AllTests.isOn");

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        if (!IS_ON) {
            return suite;
        }
        //$JUnit-BEGIN$
        //suite.addTestSuite(CompositeDiscoveryContainerTest.class);
        //suite.addTestSuite(CompositeDiscoveryContainerWithoutRegTest.class);
        //suite.addTestSuite(CompositeDiscoveryServiceContainerTest.class);
        //suite.addTestSuite(WithoutJMDNSCompositeDiscoveryServiceContainerTest.class);
        //suite.addTestSuite(WithoutJSLPCompositeDiscoveryServiceContainerTest.class);
        //$JUnit-END$
        // Since using randomized tests, turn on proper cleanup
        SingleCompositeDiscoveryServiceContainerTest.SETUP_OSGI_HOOKS_PER_TEST = true;
        return RndStatsTestCase.suite(suite);
    }
}
