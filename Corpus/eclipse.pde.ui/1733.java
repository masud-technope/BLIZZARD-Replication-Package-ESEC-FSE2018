/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.util.tests;

import org.eclipse.pde.api.tools.internal.ApiBaselineManager;
import org.eclipse.pde.api.tools.internal.model.ApiModelFactory;
import org.eclipse.pde.api.tools.internal.provisional.ApiPlugin;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.tests.AbstractApiTest;

/**
 * Tests that the {@link ApiBaselineManager} is usable in a predictable way in a headless
 * environment
 */
public class HeadlessApiBaselineManagerTests extends AbstractApiTest {

    private ApiBaselineManager fManager = ApiBaselineManager.getManager();

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    @Override
    protected void tearDown() throws Exception {
        fManager.stop();
        super.tearDown();
    }

    /**
	 * Tests that we can get an API baseline that exists from the manager
	 */
    public void testGetApiProfile() {
        //$NON-NLS-1$
        IApiBaseline baseline = ApiModelFactory.newApiBaseline("test1");
        fManager.addApiBaseline(baseline);
        //$NON-NLS-1$
        baseline = fManager.getApiBaseline("test1");
        //$NON-NLS-1$
        assertNotNull("the test1 baseline must exist in the manager", baseline);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("the found baseline must be test1", baseline.getName().equals("test1"));
    }

    /**
	 * Tests that looking up a baseline that does not exist in the manager returns null
	 */
    public void testGetNonExistantProfile() {
        //$NON-NLS-1$
        IApiBaseline baseline = fManager.getApiBaseline("foobaseline");
        //$NON-NLS-1$
        assertNull("There should be no baseline found", baseline);
    }

    /**
	 * Tests that setting the default baseline works
	 */
    public void testSetDefaultProfile() {
        //$NON-NLS-1$
        IApiBaseline baseline = ApiModelFactory.newApiBaseline("test2");
        fManager.addApiBaseline(baseline);
        fManager.setDefaultApiBaseline(baseline.getName());
        baseline = fManager.getDefaultApiBaseline();
        //$NON-NLS-1$
        assertNotNull("the default baseline should not be null", baseline);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("the default baselines' name should be test2", baseline.getName().equals("test2"));
    }

    /**
	 * Tests that setting the default baseline to one that does not exist in the manager will return null
	 * when asked for the default.
	 */
    public void testGetWrongDefault() {
        //$NON-NLS-1$
        fManager.setDefaultApiBaseline("foobaseline");
        IApiBaseline baseline = fManager.getDefaultApiBaseline();
        //$NON-NLS-1$
        assertNull("the default baseline should be null for a non-existant id", baseline);
    }

    /**
	 * Tests getting all baselines from the manager
	 */
    public void testGetAllProfiles() {
        //$NON-NLS-1$
        IApiBaseline baseline = ApiModelFactory.newApiBaseline("test1");
        fManager.addApiBaseline(baseline);
        //$NON-NLS-1$
        baseline = ApiModelFactory.newApiBaseline("test2");
        fManager.addApiBaseline(baseline);
        IApiBaseline[] baselines = fManager.getApiBaselines();
        //$NON-NLS-1$
        assertEquals("there should be 2 baselines", 2, baselines.length);
    }

    /**
	 * Tests removing an existing baseline from the manager
	 */
    public void testRemoveApiProfile() {
        //$NON-NLS-1$
        IApiBaseline baseline = ApiModelFactory.newApiBaseline("test2");
        fManager.addApiBaseline(baseline);
        //$NON-NLS-1$
        boolean result = fManager.removeApiBaseline("test2");
        //$NON-NLS-1$
        assertTrue("the baseline test2 should have been removed from the manager", result);
        //$NON-NLS-1$
        assertTrue("There should only be 0 baselines left", fManager.getApiBaselines().length == 0);
    }

    /**
	 * Tests that isExistingProfileName(..) returns return true when expected to
	 */
    public void testIsExistingName() {
        //$NON-NLS-1$
        IApiBaseline baseline = ApiModelFactory.newApiBaseline("test1");
        fManager.addApiBaseline(baseline);
        //$NON-NLS-1$
        boolean result = fManager.isExistingProfileName("test1");
        //$NON-NLS-1$
        assertTrue("the name test1 should be an existing name", result);
    }

    /**
	 * Tests that isExistingProfileName returns false when asked about an non-existent name
	 */
    public void testisExistingName2() {
        //$NON-NLS-1$
        boolean result = fManager.isExistingProfileName("foobaseline");
        //$NON-NLS-1$
        assertFalse("foobaseline is not an existing name", result);
    }

    /**
	 * Tests that calling the saving(..) method on the manager in headless mode does not fail
	 */
    public void testSavingCall() {
        if (!ApiPlugin.isRunningInFramework()) {
            try {
                fManager.saving(null);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
	 * Tests that calling the doneSaving(..) method on the manager does not fail in
	 * headless mode
	 */
    public void testDoneSavingCall() {
        if (!ApiPlugin.isRunningInFramework()) {
            try {
                fManager.doneSaving(null);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
	 * Tests that calling preparingToSave(..) does not fail in headless mode
	 */
    public void testPreparingToSave() {
        if (!ApiPlugin.isRunningInFramework()) {
            try {
                fManager.prepareToSave(null);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
	 * Tests that calling rollback(..) does not fail in headless mode
	 */
    public void testRollback() {
        if (!ApiPlugin.isRunningInFramework()) {
            try {
                fManager.rollback(null);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
	 * Tests that the workspace baseline is null in headless mode
	 */
    public void testGetWorkspaceProfile() {
        IApiBaseline baseline = fManager.getWorkspaceBaseline();
        if (ApiPlugin.isRunningInFramework()) {
            //$NON-NLS-1$
            assertNotNull("the workspace baseline must not be null with the framework running", baseline);
        } else {
            //$NON-NLS-1$
            assertNull("the workspace baseline must be null in headless mode", baseline);
        }
    }

    /**
	 * Tests that calling the stop method does not fail, and works
	 */
    public void testStop() {
        try {
            fManager.stop();
            //$NON-NLS-1$
            assertTrue("There should be no api baselines in the manager", fManager.getApiBaselines().length == 0);
            //stop it again to free the memory from the map
            fManager.stop();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
