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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.api.tools.internal.BundleVersionRange;
import org.eclipse.pde.api.tools.internal.RequiredComponentDescription;
import org.eclipse.pde.api.tools.internal.provisional.Factory;
import org.eclipse.pde.api.tools.internal.provisional.IApiAnnotations;
import org.eclipse.pde.api.tools.internal.provisional.IApiDescription;
import org.eclipse.pde.api.tools.internal.provisional.IRequiredComponentDescription;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeContainer;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeRoot;
import org.eclipse.pde.api.tools.internal.util.Util;
import junit.framework.TestCase;

/**
 * Test creation of states and components.
 *
 * The API Baseline Tests should be run as JUnit tests, not JUnit Plug-in Tests.
 * This means that there is no OSGi environment available. The vm argument
 * requiredBundles must be set to a valid baseline. In addition, rather than use
 * the EE profiles provided by OSGi, the baseline will resolve using EEs found
 * in the org.eclipse.pde.api.tools.internal.util.profiles inside the
 * org.eclipse.pde.api.tools bundle.
 *
 * "-DrequiredBundles=${eclipse_home}/plugins"
 *
 * @since 1.0.0
 */
public class ApiBaselineTests extends TestCase {

    //$NON-NLS-1$
    static final String _1_0_0 = "1.0.0";

    //$NON-NLS-1$
    static final String COMPONENT_B = "component.b";

    //$NON-NLS-1$
    static final String COMPONENT_A = "component.a";

    //$NON-NLS-1$
    static final String TEST_PLUGINS = "test-plugins";

    IApiBaseline fBaseline = null;

    /*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    @Override
    public void setUp() throws Exception {
        if (fBaseline == null) {
            fBaseline = TestSuiteHelper.createTestingBaseline(TEST_PLUGINS);
            //$NON-NLS-1$
            assertNotNull("the testing baseline should exist", fBaseline);
            List<IRequiredComponentDescription> reqs = new ArrayList<IRequiredComponentDescription>();
            //$NON-NLS-1$
            reqs.add(new RequiredComponentDescription("org.eclipse.core.runtime", new BundleVersionRange(Util.EMPTY_STRING)));
            //$NON-NLS-1$ //$NON-NLS-2$
            validateComponent(fBaseline, COMPONENT_A, "A Plug-in", _1_0_0, "J2SE-1.5", reqs);
            reqs = new ArrayList<IRequiredComponentDescription>();
            //$NON-NLS-1$
            reqs.add(new RequiredComponentDescription("org.eclipse.core.runtime", new BundleVersionRange(Util.EMPTY_STRING)));
            reqs.add(new RequiredComponentDescription(COMPONENT_A, new BundleVersionRange(Util.EMPTY_STRING)));
            //$NON-NLS-1$ //$NON-NLS-2$
            validateComponent(fBaseline, COMPONENT_B, "B Plug-in", _1_0_0, "J2SE-1.4", reqs);
        }
    }

    /**
	 * Resolves a package
	 *
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
    public void testResolvePackage() throws FileNotFoundException, CoreException {
        //$NON-NLS-1$
        assertNotNull("the testing baseline should exist", fBaseline);
        IApiComponent[] components = fBaseline.resolvePackage(fBaseline.getApiComponent(COMPONENT_B), COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("No component", components);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, components.length);
        //$NON-NLS-1$
        assertEquals("Wrong provider for package", fBaseline.getApiComponent(COMPONENT_A), components[0]);
    }

    /**
	 * Resolves a package within a single component
	 *
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
    public void testResolvePackageWithinComponent() throws FileNotFoundException, CoreException {
        //$NON-NLS-1$
        assertNotNull("the testing baseline should exist", fBaseline);
        //$NON-NLS-1$
        IApiComponent[] components = fBaseline.resolvePackage(fBaseline.getApiComponent(COMPONENT_A), "a.b.c");
        //$NON-NLS-1$
        assertNotNull("No component", components);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, components.length);
        //$NON-NLS-1$
        assertEquals("Wrong provider for package", fBaseline.getApiComponent(COMPONENT_A), components[0]);
    }

    /**
	 * Resolves a system package
	 *
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
    public void testResolveJavaLangPackage() throws FileNotFoundException, CoreException {
        //$NON-NLS-1$
        assertNotNull("the testing baseline should exist", fBaseline);
        //$NON-NLS-1$
        IApiComponent[] components = fBaseline.resolvePackage(fBaseline.getApiComponent(COMPONENT_B), "java.lang");
        //$NON-NLS-1$
        assertNotNull("No component", components);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, components.length);
        //$NON-NLS-1$
        assertEquals("Wrong provider for package", fBaseline.getApiComponent(fBaseline.getExecutionEnvironment()), components[0]);
    }

    /**
	 * Resolves a system package
	 *
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
    public void testResolveSystemPackage() throws FileNotFoundException, CoreException {
        //$NON-NLS-1$
        assertNotNull("the testing baseline should exist", fBaseline);
        //$NON-NLS-1$
        IApiComponent[] components = fBaseline.resolvePackage(fBaseline.getApiComponent(COMPONENT_B), "org.w3c.dom");
        //$NON-NLS-1$
        assertNotNull("No component", components);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, components.length);
        //$NON-NLS-1$
        assertEquals("Wrong provider for package", fBaseline.getApiComponent(fBaseline.getExecutionEnvironment()), components[0]);
    }

    /**
	 * Finds the class file for java.lang.Object
	 *
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
    public void testFindJavaLangObject() throws FileNotFoundException, CoreException {
        //$NON-NLS-1$
        assertNotNull("the testing baseline should exist", fBaseline);
        //$NON-NLS-1$
        IApiComponent[] components = fBaseline.resolvePackage(fBaseline.getApiComponent(COMPONENT_B), "java.lang");
        //$NON-NLS-1$
        assertNotNull("No component", components);
        //$NON-NLS-1$
        assertEquals("Wrong size", 1, components.length);
        //$NON-NLS-1$
        assertEquals("Wrong provider for package", fBaseline.getApiComponent(fBaseline.getExecutionEnvironment()), components[0]);
        //$NON-NLS-1$
        IApiTypeRoot classFile = components[0].findTypeRoot("java.lang.Object");
        //$NON-NLS-1$
        assertNotNull("Missing java.lang.Object", classFile);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type name", "java.lang.Object", classFile.getTypeName());
    }

    /**
	 * Validates basic component attributes.
	 *
	 * @param baseline baseline to retrieve the component from
	 * @param id component id
	 * @param name component name
	 * @param version component version
	 * @param environment execution environment id
	 * @param requiredComponents list of {@link IRequiredComponentDescription}
	 * @throws CoreException
	 */
    private void validateComponent(IApiBaseline baseline, String id, String name, String version, String environment, List<IRequiredComponentDescription> requiredComponents) throws CoreException {
        IApiComponent component = baseline.getApiComponent(id);
        //$NON-NLS-1$
        assertEquals("Id: ", id, component.getSymbolicName());
        //$NON-NLS-1$
        assertEquals("Name: ", name, component.getName());
        //$NON-NLS-1$
        assertEquals("Version: ", version, component.getVersion());
        String[] envs = component.getExecutionEnvironments();
        //$NON-NLS-1$
        assertEquals("Wrong number of execution environments", 1, envs.length);
        //$NON-NLS-1$
        assertEquals("Version: ", environment, envs[0]);
        IRequiredComponentDescription[] actual = component.getRequiredComponents();
        //$NON-NLS-1$
        assertEquals("Wrong number of required components", requiredComponents.size(), actual.length);
        for (int i = 0; i < requiredComponents.size(); i++) {
            //$NON-NLS-1$
            assertEquals("Wrong required component", requiredComponents.get(i), actual[i]);
        }
    }

    /**
	 * Tests creating a baseline with a component that has a nested jar of class
	 * files.
	 *
	 * @throws CoreException
	 */
    public void testNestedJarComponent() throws CoreException {
        //$NON-NLS-1$
        IApiBaseline baseline = TestSuiteHelper.createTestingBaseline("test-nested-jars");
        IApiComponent component = baseline.getApiComponent(COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("missing component.a", component);
        IApiTypeContainer[] containers = component.getApiTypeContainers();
        //$NON-NLS-1$
        assertTrue("Missing containers:", containers.length > 0);
        IApiTypeRoot file = null;
        for (int i = 0; i < containers.length; i++) {
            IApiTypeContainer container = containers[i];
            String[] names = container.getPackageNames();
            for (int j = 0; j < names.length; j++) {
                if (names[j].equals(COMPONENT_A)) {
                    file = //$NON-NLS-1$
                    container.findTypeRoot(//$NON-NLS-1$
                    "component.a.A");
                    break;
                }
            }
            if (file != null) {
                break;
            }
        }
        //$NON-NLS-1$
        assertNotNull("Missing class file", file);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong type name", "component.a.A", file.getTypeName());
    }

    /**
	 * Tests that an x-friends directive works. Component A exports package
	 * component.a.friend.of.b as a friend for b. Note - the package should
	 * still be private.
	 *
	 * @throws CoreException
	 */
    public void testXFriendsDirective() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("Missing component.a", component);
        IApiDescription description = component.getApiDescription();
        //$NON-NLS-1$
        IApiAnnotations result = description.resolveAnnotations(Factory.typeDescriptor("component.a.friend.of.b.FriendOfB"));
        //$NON-NLS-1$
        assertNotNull("Missing API description", result);
        int visibility = result.getVisibility();
        //$NON-NLS-1$
        assertTrue("Should be PRIVATE", VisibilityModifiers.isPrivate(visibility));
    }

    /**
	 * Tests that an x-internal directive works. Component A exports package
	 * component.a.internal as internal.
	 *
	 * @throws CoreException
	 */
    public void testXInternalDirective() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("Missing component.a", component);
        IApiDescription description = component.getApiDescription();
        //$NON-NLS-1$
        IApiAnnotations result = description.resolveAnnotations(Factory.typeDescriptor("component.a.internal.InternalClass"));
        //$NON-NLS-1$
        assertNotNull("Missing API description", result);
        int visibility = result.getVisibility();
        //$NON-NLS-1$
        assertTrue("Should be private", VisibilityModifiers.isPrivate(visibility));
    }

    /**
	 * Tests that a 'uses' directive works. Component A exports package
	 * component.a. with a 'uses' directive but should still be API.
	 *
	 * @throws CoreException
	 */
    public void testUsesDirective() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("Missing component.a", component);
        IApiDescription description = component.getApiDescription();
        //$NON-NLS-1$
        IApiAnnotations result = description.resolveAnnotations(Factory.typeDescriptor("component.a.A"));
        //$NON-NLS-1$
        assertNotNull("Missing API description", result);
        int visibility = result.getVisibility();
        //$NON-NLS-1$
        assertTrue("Should be API", VisibilityModifiers.isAPI(visibility));
    }

    /**
	 * Tests that a non-exported package is private. Component A does not export
	 * package component.a.not.exported.
	 *
	 * @throws CoreException
	 */
    public void testNotExported() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        //$NON-NLS-1$
        assertNotNull("Missing component.a", component);
        IApiDescription description = component.getApiDescription();
        //$NON-NLS-1$
        IApiAnnotations result = description.resolveAnnotations(Factory.typeDescriptor("component.a.not.exported.NotExported"));
        //$NON-NLS-1$
        assertNotNull("Missing API description", result);
        int visibility = result.getVisibility();
        //$NON-NLS-1$
        assertTrue("Should be private", VisibilityModifiers.isPrivate(visibility));
    }

    /**
	 * Tests component prerequisites.
	 *
	 * @throws CoreException
	 */
    public void testPrerequisites() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        IApiComponent[] prerequisiteComponents = fBaseline.getPrerequisiteComponents(new IApiComponent[] { component });
        for (int i = 0; i < prerequisiteComponents.length; i++) {
            IApiComponent apiComponent = prerequisiteComponents[i];
            if (//$NON-NLS-1$
            apiComponent.getSymbolicName().equals("org.eclipse.osgi")) {
                // done
                return;
            }
        }
        //$NON-NLS-1$
        assertTrue("Missing prerequisite bundle", false);
    }

    /**
	 * Tests component dependents.
	 *
	 * @throws CoreException
	 */
    public void testDependents() throws CoreException {
        IApiComponent component = fBaseline.getApiComponent(COMPONENT_A);
        IApiComponent[] dependents = fBaseline.getDependentComponents(new IApiComponent[] { component });
        //$NON-NLS-1$
        assertEquals("Wrong number of dependents", 2, dependents.length);
        for (int i = 0; i < dependents.length; i++) {
            IApiComponent apiComponent = dependents[i];
            if (apiComponent.getSymbolicName().equals(COMPONENT_B)) {
                // done
                return;
            }
        }
        //$NON-NLS-1$
        assertEquals("Missing dependent component.b", false);
    }

    /**
	 * Tests getting the location from an 'old' baseline
	 */
    public void testGetLocation() throws Exception {
        //$NON-NLS-1$
        assertNull("The location must be null", fBaseline.getLocation());
        //$NON-NLS-1$
        fBaseline.setLocation("new_loc");
        //$NON-NLS-1$
        assertNotNull("The location must not be null", fBaseline.getLocation());
    }
}
