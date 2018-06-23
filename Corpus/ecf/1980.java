/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package ch.ethz.iks.slp.test;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import junit.framework.Assert;
import junit.framework.TestCase;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

public class SelfDiscoveryTest extends TestCase {

    private final String HOST_AND_PORT = System.getProperty("net.slp.tests.hostAndPort", "gantenbein:123");

    private ServiceURL service;

    private Dictionary properties;

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    public void setUp() throws InterruptedException {
        try {
            service = new ServiceURL("service:osgi://" + HOST_AND_PORT, 10800);
            int i = 0;
            properties = new Hashtable();
            properties.put("attr", Boolean.FALSE);
            properties.put("attr" + i++, "value");
            properties.put("attr" + i++, "foo,bar");
            properties.put("attr" + i++, "foo:bar");
            properties.put("attr" + i++, "foo bar");
            TestActivator.advertiser.register(service, properties);
        } catch (ServiceLocationException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    public void tearDown() throws InterruptedException {
        try {
            TestActivator.advertiser.deregister(service);
        } catch (ServiceLocationException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
	 * Test method for
	 * {@link ch.ethz.iks.slp.Locator}.
	 */
    public void testService() throws Exception {
        int count = 0;
        for (ServiceLocationEnumeration services = TestActivator.locator.findServices(new ServiceType("service:osgi"), null, null); services.hasMoreElements(); ) {
            assertEquals(services.next().toString(), "service:osgi://" + HOST_AND_PORT);
            count++;
        }
        assertEquals(1, count);
    }

    /**
	 * Test method for
	 * {@link ch.ethz.iks.slp.Locator}.
	 */
    public void testAttributes() throws Exception {
        int count = 0;
        // not fast but DRY
        outter: for (ServiceLocationEnumeration attributes = TestActivator.locator.findAttributes(new ServiceType("service:osgi"), null, null); attributes.hasMoreElements(); ) {
            final String attribute = attributes.next().toString();
            // inner loop over the dict
            Enumeration elements = properties.keys();
            for (; elements.hasMoreElements(); ) {
                String key = elements.nextElement().toString();
                String value = properties.get(key).toString();
                if (attribute.equals(("(" + key + "=" + value + ")"))) {
                    count++;
                    continue outter;
                }
            }
            fail(attribute + " not found in reference " + properties.toString());
        }
        assertEquals(properties.size(), count);
    }

    /**
	 * Test method for
	 * {@link ch.ethz.iks.slp.Locator}.
	 */
    public void testFilter() throws Exception {
        int count = 0;
        for (ServiceLocationEnumeration services = TestActivator.locator.findServices(new ServiceType("service:osgi"), null, "(attr=false)"); services.hasMoreElements(); ) {
            assertEquals(services.next().toString(), "service:osgi://" + HOST_AND_PORT);
            count++;
        }
        assertEquals(1, count);
    }

    /**
	 * Test method for
	 * {@link ch.ethz.iks.slp.Locator}.
	 */
    public void testFilterWithWildcard() throws Exception {
        int count = 0;
        for (ServiceLocationEnumeration services = TestActivator.locator.findServices(new ServiceType("service:osgi"), null, "(attr=*)"); services.hasMoreElements(); ) {
            assertEquals(services.next().toString(), "service:osgi://" + HOST_AND_PORT);
            count++;
        }
        assertEquals(1, count);
    }

    /**
	 * Test method for
	 * {@link ch.ethz.iks.slp.Locator}.
	 * 
	 * [1] http://www.faqs.org/rfcs/rfc1960.html
	 * Cite: If a <value> must contain one of the characters '*' or '(' or ')', these
	 * characters should be escaped by preceding them with the backslash '\' character.
	 */
    public void testFilterWithBrokenParenthesis() throws Exception {
        try {
            // correct filter is (service-type=\(service:osgi\))
            TestActivator.locator.findServices(new ServiceType("service:osgi"), null, "(service-type=(service:osgi))");
        } catch (ServiceLocationException e) {
            if (e.getErrorCode() == 20) {
                return;
            }
        }
        fail();
    }
}
