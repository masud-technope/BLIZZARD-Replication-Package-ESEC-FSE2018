/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests.launching;

import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jdt.debug.testplugin.AlternateDelegate;
import org.eclipse.jdt.debug.testplugin.launching.TestLaunchDelegate1;
import org.eclipse.jdt.debug.testplugin.launching.TestLaunchDelegate2;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * Tests for launch delegates
 */
public class LaunchDelegateTests extends AbstractDebugTest {

    /**
	 * Constructor
	 * @param name
	 */
    public  LaunchDelegateTests(String name) {
        super(name);
    }

    /**
	 * Ensures a launch delegate can provide a launch object for
	 * a launch.
	 * @throws CoreException
	 */
    public void testProvideLaunch() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        //$NON-NLS-1$
        ILaunchConfigurationType configurationType = manager.getLaunchConfigurationType("org.eclipse.jdt.debug.tests.testConfigType");
        //$NON-NLS-1$
        assertNotNull("Missing test launch config type", configurationType);
        //$NON-NLS-1$
        ILaunchConfigurationWorkingCopy workingCopy = configurationType.newInstance(null, "provide-launch-object");
        // delegate will throw exception if test fails
        ILaunch launch = workingCopy.launch(ILaunchManager.DEBUG_MODE, null);
        manager.removeLaunch(launch);
    }

    /**
	 * Tests that a delegate extension can provide the source path computer.
	 */
    public void testSourcePathComputerExtension() {
        ILaunchManager manager = getLaunchManager();
        //$NON-NLS-1$
        ILaunchConfigurationType configurationType = manager.getLaunchConfigurationType("org.eclipse.jdt.debug.tests.testConfigType");
        //$NON-NLS-1$
        assertNotNull("Missing test launch config type", configurationType);
        ISourcePathComputer sourcePathComputer = configurationType.getSourcePathComputer();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong source path computer", "org.eclipse.jdt.debug.tests.testSourcePathComputer", sourcePathComputer.getId());
    }

    /**
	 * Tests that a delegate extension can provide the source locator.
	 */
    public void testSourceLocatorExtension() {
        ILaunchManager manager = getLaunchManager();
        //$NON-NLS-1$
        ILaunchConfigurationType configurationType = manager.getLaunchConfigurationType("org.eclipse.jdt.debug.tests.testConfigType");
        //$NON-NLS-1$
        assertNotNull("Missing test launch config type", configurationType);
        String sourceLocatorId = configurationType.getSourceLocatorId();
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong source locater id", "org.eclipse.jdt.debug.tests.testSourceLocator", sourceLocatorId);
    }

    /**
	 * Test launch delegate for mixed launch mode.
	 * @throws CoreException 
	 */
    public void testMixedModeDelegate() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        HashSet<String> modes = new HashSet<String>();
        //$NON-NLS-1$
        modes.add("alternate");
        modes.add(ILaunchManager.DEBUG_MODE);
        //$NON-NLS-1$
        assertTrue("Should support mixed mode (alternate/debug)", type.supportsModeCombination(modes));
        ILaunchDelegate[] delegates = type.getDelegates(modes);
        //$NON-NLS-1$
        assertTrue("missing delegate", delegates.length > 0);
        //$NON-NLS-1$
        assertEquals("Wrong number of delegates", 1, delegates.length);
        //$NON-NLS-1$
        assertTrue("Wrong delegate", delegates[0].getDelegate() instanceof AlternateDelegate);
    }

    /**
	 * Tests if the java launch delegate was found as one of the delegates for debug mode.
	 * @throws CoreException
	 */
    public void testSingleDebugModeDelegate() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        HashSet<String> modes = new HashSet<String>();
        modes.add(ILaunchManager.DEBUG_MODE);
        //$NON-NLS-1$
        assertTrue("Should support mode (debug)", type.supportsModeCombination(modes));
        ILaunchDelegate[] delegates = type.getDelegates(modes);
        //$NON-NLS-1$
        assertTrue("missing delegate", delegates.length > 0);
        boolean found = false;
        for (int i = 0; i < delegates.length; i++) {
            if (//$NON-NLS-1$
            delegates[i].getDelegate().getClass().getName().endsWith("JavaLaunchDelegate")) {
                found = true;
                break;
            }
        }
        //$NON-NLS-1$
        assertTrue("The java launch delegate was not one of the returned delegates", found);
    }

    /**
	 * Tests correct delegate is found for alternate mode.
	 * @throws CoreException
	 */
    public void testSingleAlternateModeDelegate() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        HashSet<String> modes = new HashSet<String>();
        //$NON-NLS-1$
        modes.add("alternate");
        //$NON-NLS-1$
        assertTrue("Should support mode (alternate)", type.supportsModeCombination(modes));
        ILaunchDelegate[] delegates = type.getDelegates(modes);
        //$NON-NLS-1$
        assertTrue("missing delegate", delegates.length > 0);
        //$NON-NLS-1$
        assertEquals("Wrong number of delegates", 1, delegates.length);
        //$NON-NLS-1$
        assertTrue("Wrong delegate", delegates[0].getDelegate() instanceof AlternateDelegate);
    }

    /**
	 * Checks that the delegate definition is collecting and parsing mode combination information properly from both the delegate
	 * contribution and from modeCombination child elements
	 * @throws CoreException
	 */
    public void testMultipleModeSingleDelegate() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        String[][] modesavail = { { "alternate" }, { "debug", "alternate" }, { "debug", "alternate2" } };
        HashSet<String> modes = null;
        for (int i = 0; i < modesavail.length; i++) {
            modes = new HashSet<String>(Arrays.asList(modesavail[i]));
            //$NON-NLS-1$
            assertTrue("Should support modes " + modes.toString(), type.supportsModeCombination(modes));
            ILaunchDelegate[] delegates = type.getDelegates(modes);
            //$NON-NLS-1$
            assertTrue("missing delegate", delegates.length > 0);
            //$NON-NLS-1$
            assertEquals("Wrong number of delegates", 1, delegates.length);
            //$NON-NLS-1$
            assertTrue("Wrong delegate", delegates[0].getDelegate() instanceof AlternateDelegate);
        }
    }

    /**
	 * Checks that all applicable delegates are found for given types and mode combinations
	 * @throws CoreException
	 */
    public void testSingleModeMultipleDelegates() throws CoreException {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        HashSet<String> modes = new HashSet<String>();
        //$NON-NLS-1$
        modes.add("alternate2");
        //$NON-NLS-1$
        assertTrue("Should support mode 'alternate2'", type.supportsModeCombination(modes));
        ILaunchDelegate[] delegates = type.getDelegates(modes);
        //$NON-NLS-1$
        assertTrue("missing delegate", delegates.length > 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong number of delegates, should be 2 only " + delegates.length + " found", 2, delegates.length);
        HashSet<Class<? extends ILaunchConfigurationDelegate>> dels = new HashSet<Class<? extends ILaunchConfigurationDelegate>>();
        for (int i = 0; i < delegates.length; i++) {
            dels.add(delegates[i].getDelegate().getClass());
        }
        HashSet<Object> ds = new HashSet<Object>(Arrays.asList(new Object[] { TestLaunchDelegate1.class, TestLaunchDelegate2.class }));
        //$NON-NLS-1$
        assertTrue("There must be only TestLaunchDelegate1 and TestLaunchDelegate2 as registered delegates for the mode alternate2 and the local java type", ds.containsAll(dels));
    }

    /**
	 * Checks to see the even with a partial match of a mode combination it will indicate that it does not support the specified modes
	 */
    public void testPartialModeCombination() {
        ILaunchManager manager = getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        //$NON-NLS-1$
        assertNotNull("Missing java application launch config type", type);
        HashSet<String> modes = new HashSet<String>();
        //$NON-NLS-1$
        modes.add("alternate2");
        //$NON-NLS-1$
        modes.add("foo");
        //$NON-NLS-1$
        assertTrue("Should not support modes: " + modes.toString(), !type.supportsModeCombination(modes));
    }
}
