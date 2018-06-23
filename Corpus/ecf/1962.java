/*******************************************************************************
 * Copyright (c) 2009, 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.discovery;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.provider.discovery.CompositeDiscoveryContainer;
import org.eclipse.ecf.tests.discovery.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

public abstract class SingleCompositeDiscoveryServiceContainerTest extends CompositeDiscoveryServiceContainerTest {
    // Whether the OSGi hooks should be de-/registered after and before each
    // individual test and not just after/before the test class. 
    // E.g. when tests are executed in random order and thus are interleaved
    // with other tests, setUp/tearDown has to run after each test. Otherwise
    // expect test failures.
    //	public static boolean SETUP_OSGI_HOOKS_PER_TEST = false;
    //
    //	private static int testMethods;
    //	private static int testMethodsLeft;
    //	private static ServiceRegistration findHook;
    //	
    //	// count all test methods
    //	static {
    //		Method[] methods = SingleCompositeDiscoveryServiceContainerTest.class.getMethods();
    //		for (int i = 0; i < methods.length; i++) {
    //			Method method = methods[i];
    //			if (method.getName().startsWith("test") && method.getModifiers() == Modifier.PUBLIC) {
    //				testMethods++;
    //			}
    //		}
    //		testMethodsLeft = testMethods;
    //	}
    //
    //	private final String ecfDiscoveryContainerName;
    //	private final String className;
    //
    //	public SingleCompositeDiscoveryServiceContainerTest(String anECFDiscoveryContainerName, String aClassName) {
    //		ecfDiscoveryContainerName = anECFDiscoveryContainerName;
    //		className = aClassName;
    //	}
    //	
    //	private boolean doSetUp() {
    //		return SETUP_OSGI_HOOKS_PER_TEST || testMethodsLeft == testMethods;
    //	}
    //
    //	private boolean doTearDown() {
    //		return SETUP_OSGI_HOOKS_PER_TEST || --testMethodsLeft == 0;
    //	}
    //	
    //	/* (non-Javadoc)
    //	 * @see org.eclipse.ecf.tests.provider.discovery.CompositeDiscoveryServiceContainerTest#setUp()
    //	 */
    //	protected void setUp() throws Exception {
    //		// HACK: @BeforeClass JUnit4 functionality
    //		if(doSetUp()) {
    //			// HACK: forcefully start the (nested) discovery container if it hasn't been started yet
    //			// assuming the bundle declares a lazy start buddy policy
    //			Class.forName(className);
    //			
    //			// initially close the existing CDC to get rid of other test left overs
    //			Activator.getDefault().closeServiceTracker(containerUnderTest);
    //			
    //			final BundleContext context = Activator.getDefault().getContext();
    //			final String[] clazzes = new String[]{FindHook.class.getName(), EventHook.class.getName()};
    //			findHook = context.registerService(clazzes, new DiscoveryContainerFilterHook(ecfDiscoveryContainerName), null);
    //		}
    //		super.setUp();
    //	}
    //	
    //	/* (non-Javadoc)
    //	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#tearDown()
    //	 */
    //	protected void tearDown() throws Exception {
    //		super.tearDown();
    //		// HACK: @BeforeClass JUnit4 functionality
    //		if(doTearDown()) {
    //			if(findHook != null) {
    //				findHook.unregister();
    //				findHook = null;
    //			}
    //			// close tracker to force creation of a new CDC instance
    //			Activator.getDefault().closeServiceTracker(containerUnderTest);
    //
    //			// reset so other instances can reuse
    //			testMethodsLeft = testMethods;
    //		}
    //	}
    //	
    //	/* (non-Javadoc)
    //	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#getDiscoveryLocator()
    //	 */
    //	protected IDiscoveryLocator getDiscoveryLocator() {
    //		final IDiscoveryLocator idl = super.getDiscoveryLocator();
    //		checkCompositeDiscoveryContainer(className, (CompositeDiscoveryContainer)idl);
    //		return idl;
    //	}
    //
    //
    //	/* (non-Javadoc)
    //	 * @see org.eclipse.ecf.tests.discovery.DiscoveryServiceTest#getDiscoveryAdvertiser()
    //	 */
    //	protected IDiscoveryAdvertiser getDiscoveryAdvertiser() {
    //		final IDiscoveryAdvertiser ida = super.getDiscoveryAdvertiser();
    //		checkCompositeDiscoveryContainer(className, (CompositeDiscoveryContainer)ida);
    //		return ida;
    //	}
    //	
    //	// make sure the CDC has only a single IDC registered with the correct type
    //	private static void checkCompositeDiscoveryContainer(final String aClassName, final CompositeDiscoveryContainer cdc) {
    //		final Collection discoveryContainers = cdc.getDiscoveryContainers();
    //		assertTrue("One IDiscoveryContainer must be registered with the CDC at this point: " + discoveryContainers, discoveryContainers.size() == 1);
    //		for (final Iterator iterator = discoveryContainers.iterator(); iterator.hasNext();) {
    //			final IDiscoveryLocator dl = (IDiscoveryLocator) iterator.next();
    //			assertEquals(aClassName, dl.getClass().getName());
    //		}
    //	}
    //
    //	// Filters the corresponding IDC from the result set that is _not_ supposed to be part of the test
    //	// we need an EventHook too because due to ECF's namespaces the "other" bundle is started asynchronously
    //	// and consequently registered with the CDC 
    //	private class DiscoveryContainerFilterHook implements FindHook, EventHook {
    //
    //		private static final String BUNDLE_UNDER_TEST = "org.eclipse.ecf.provider.discovery"; // rename if bundle name change
    //		private final String containerName;
    //
    //		public DiscoveryContainerFilterHook(String anECFDiscoveryContainerName) {
    //			containerName = anECFDiscoveryContainerName;
    //		}
    //
    //		/* (non-Javadoc)
    //		 * @see org.osgi.framework.hooks.service.FindHook#find(org.osgi.framework.BundleContext, java.lang.String, java.lang.String, boolean, java.util.Collection)
    //		 */
    //		public void find(BundleContext context, String name, String filter, boolean allServices, Collection references) {
    //			
    //			// is it the composite discovery bundle who tries to find the service?
    //			final String symbolicName = context.getBundle().getSymbolicName();
    //			final Collection removees = new ArrayList();
    //			if(BUNDLE_UNDER_TEST.equals(symbolicName)) {
    //				for (final Iterator iterator = references.iterator(); iterator.hasNext();) {
    //					// filter the corresponding container 
    //					final ServiceReference serviceReference = (ServiceReference) iterator.next();
    //					final String property = (String) serviceReference.getProperty(IDiscoveryLocator.CONTAINER_NAME);
    //					if(property != null && property.equals(containerName)) {
    //						removees.add(serviceReference);
    //						System.out.println("Removed reference: " + property);
    //						break;
    //					}
    //				}
    //				references.removeAll(removees);
    //			}
    //		}
    //
    //		/* (non-Javadoc)
    //		 * @see org.osgi.framework.hooks.service.EventHook#event(org.osgi.framework.ServiceEvent, java.util.Collection)
    //		 */
    //		public void event(ServiceEvent aServiceEvent, Collection aCollection) {
    //			if (aServiceEvent.getType() == ServiceEvent.REGISTERED) {
    //				final ServiceReference serviceReference = aServiceEvent.getServiceReference();
    //				final String property = (String) serviceReference.getProperty(IDiscoveryLocator.CONTAINER_NAME);
    //				if(property != null && property.equals(containerName)) {
    //					final Collection removees = new ArrayList();
    //					for (Iterator iterator = aCollection.iterator(); iterator.hasNext();) {
    //						final BundleContext bundleContext = (BundleContext) iterator.next();
    //						final String symbolicName = bundleContext.getBundle().getSymbolicName();
    //						if(BUNDLE_UNDER_TEST.equals(symbolicName)) {
    //							removees.add(bundleContext);
    //							System.out.println("Filtered reference: " + property);
    //							break;
    //						}
    //					}
    //					aCollection.removeAll(removees);
    //				}
    //			}
    //		}
    //	}
}
