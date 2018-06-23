/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.model.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.BundleVersionRange;
import org.eclipse.pde.api.tools.internal.RequiredComponentDescription;
import org.eclipse.pde.api.tools.internal.model.ApiModelFactory;
import org.eclipse.pde.api.tools.internal.provisional.IRequiredComponentDescription;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;

/**
 * @since 1.0.0
 */
public class ComponentManifestTests extends TestCase {

    public static Test suite() {
        return new TestSuite(ComponentManifestTests.class);
    }

    public  ComponentManifestTests() {
        super();
    }

    public  ComponentManifestTests(String name) {
        super(name);
    }

    public void testComponentManifest() throws CoreException {
        IPath path = TestSuiteHelper.getPluginDirectoryPath();
        //$NON-NLS-1$
        path = path.append("test-manifests");
        File file = path.toFile();
        //$NON-NLS-1$
        assertTrue("Missing manifest directory", file.exists());
        //$NON-NLS-1$
        IApiBaseline baseline = TestSuiteHelper.newApiBaseline("test", TestSuiteHelper.getEEDescriptionFile());
        try {
            IApiComponent component = ApiModelFactory.newApiComponent(baseline, file.getAbsolutePath());
            baseline.addApiComponents(new IApiComponent[] { component });
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Id: ", "org.eclipse.debug.ui", component.getSymbolicName());
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Name: ", "Debug Platform UI", component.getName());
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Version: ", "3.3.100", component.getVersion());
            String[] envs = component.getExecutionEnvironments();
            //$NON-NLS-1$
            assertEquals("Wrong number of execution environments", 1, envs.length);
            //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("Version: ", "J2SE-1.4", envs[0]);
            IRequiredComponentDescription[] requiredComponents = component.getRequiredComponents();
            //$NON-NLS-1$
            assertEquals("Wrong number of required components", 11, requiredComponents.length);
            List<RequiredComponentDescription> reqs = new ArrayList<RequiredComponentDescription>();
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.core.expressions", new BundleVersionRange("(3.3.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.core.variables", new BundleVersionRange("[3.2.0,4.0.0]")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.ui", new BundleVersionRange("[3.3.0,4.0.0]")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.ui.console", new BundleVersionRange("[3.2.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.help", new BundleVersionRange("3.3.0")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.debug.core", new BundleVersionRange("3.4.0")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.jface.text", new BundleVersionRange("[3.3.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.ui.workbench.texteditor", new BundleVersionRange("[3.3.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.ui.ide", new BundleVersionRange("[3.3.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.ui.editors", new BundleVersionRange("[3.3.0,4.0.0)")));
            //$NON-NLS-1$ //$NON-NLS-2$
            reqs.add(new RequiredComponentDescription("org.eclipse.core.runtime", new BundleVersionRange("[3.3.0,4.0.0)")));
            for (int i = 0; i < reqs.size(); i++) {
                assertEquals(//$NON-NLS-1$
                "Wrong required component", //$NON-NLS-1$
                reqs.get(i), //$NON-NLS-1$
                requiredComponents[i]);
            }
        } finally {
            baseline.dispose();
        }
    }

    public void testReExport() throws CoreException {
        IPath path = TestSuiteHelper.getPluginDirectoryPath();
        //$NON-NLS-1$
        path = path.append("test-manifests");
        File file = path.toFile();
        //$NON-NLS-1$
        assertTrue("Missing manifest directory", file.exists());
        //$NON-NLS-1$
        IApiBaseline baseline = TestSuiteHelper.newApiBaseline("test", TestSuiteHelper.getEEDescriptionFile());
        try {
            IApiComponent component = ApiModelFactory.newApiComponent(baseline, file.getAbsolutePath());
            baseline.addApiComponents(new IApiComponent[] { component });
            boolean debugCoreExport = false;
            boolean others = false;
            IRequiredComponentDescription[] requiredComponents = component.getRequiredComponents();
            for (int i = 0; i < requiredComponents.length; i++) {
                IRequiredComponentDescription description = requiredComponents[i];
                if (//$NON-NLS-1$
                description.getId().equals("org.eclipse.debug.core")) {
                    debugCoreExport = description.isExported();
                } else {
                    others = others || description.isExported();
                }
            }
            //$NON-NLS-1$
            assertTrue("org.eclipse.debug.core should be re-exported", debugCoreExport);
            //$NON-NLS-1$
            assertFalse("Other components should not be re-exported", others);
        } finally {
            baseline.dispose();
        }
    }
}
