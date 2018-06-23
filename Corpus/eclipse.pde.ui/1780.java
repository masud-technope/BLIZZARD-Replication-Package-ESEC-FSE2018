/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.model.ArchiveApiTypeContainer;
import org.eclipse.pde.api.tools.internal.model.DirectoryApiTypeContainer;
import org.eclipse.pde.api.tools.internal.provisional.model.ApiTypeContainerVisitor;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeContainer;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeRoot;

/**
 * Tests the class file containers
 *
 * @since 1.0.0
 */
public class ApiTypeContainerTests extends TestCase {

    public static Test suite() {
        return new TestSuite(ApiTypeContainerTests.class);
    }

    public  ApiTypeContainerTests() {
        super();
    }

    public  ApiTypeContainerTests(String name) {
        super(name);
    }

    /**
	 * Builds a sample archive on sample.jar
	 *
	 * @return sample archive
	 */
    protected IApiTypeContainer buildArchiveContainer() {
        IPath path = TestSuiteHelper.getPluginDirectoryPath();
        //$NON-NLS-1$ //$NON-NLS-2$
        path = path.append("test-jars").append("sample.jar");
        File file = path.toFile();
        //$NON-NLS-1$
        assertTrue("Missing jar file", file.exists());
        return new ArchiveApiTypeContainer(null, path.toOSString());
    }

    /**
	 * Builds a sample container on directory
	 *
	 * @return sample directory container
	 */
    protected IApiTypeContainer buildDirectoryContainer() {
        IPath path = TestSuiteHelper.getPluginDirectoryPath();
        //$NON-NLS-1$
        path = path.append("test-bin-dir");
        File file = path.toFile();
        //$NON-NLS-1$
        assertTrue("Missing bin directory", file.exists());
        return new DirectoryApiTypeContainer(null, path.toOSString());
    }

    /**
	 * Tests retrieving package names from an archive.
	 *
	 * @throws CoreException
	 */
    public void testArchivePackageNames() throws CoreException {
        doTestPackageNames(buildArchiveContainer());
    }

    /**
	 * Tests retrieving package names from an directory.
	 *
	 * @throws CoreException
	 */
    public void testDirectoryPackageNames() throws CoreException {
        doTestPackageNames(buildDirectoryContainer());
    }

    /**
	 * Tests retrieving package names.
	 *
	 * @param container class file container
	 * @throws CoreException
	 */
    protected void doTestPackageNames(IApiTypeContainer container) throws CoreException {
        String[] packageNames = container.getPackageNames();
        Set<String> knownNames = new HashSet<String>();
        //$NON-NLS-1$
        knownNames.add("");
        //$NON-NLS-1$
        knownNames.add("a");
        //$NON-NLS-1$
        knownNames.add("a.b.c");
        //$NON-NLS-1$
        assertEquals("Wrong number of packages", 3, packageNames.length);
        for (int i = 0; i < packageNames.length; i++) {
            //$NON-NLS-1$
            assertTrue("Missing package " + packageNames[i], knownNames.remove(packageNames[i]));
        }
        //$NON-NLS-1$
        assertTrue("Should be no left over packages", knownNames.isEmpty());
    }

    /**
	 * Tests visiting packages in an archive.
	 *
	 * @throws CoreException
	 */
    public void testArchiveVistPackages() throws CoreException {
        doTestVisitPackages(buildArchiveContainer());
    }

    /**
	 * Tests visiting packages in an directory.
	 *
	 * @throws CoreException
	 */
    public void testDirectoryVistPackages() throws CoreException {
        doTestVisitPackages(buildDirectoryContainer());
    }

    /**
	 * Test visiting packages
	 *
	 * @param container class file container
	 * @throws CoreException
	 */
    protected void doTestVisitPackages(IApiTypeContainer container) throws CoreException {
        final List<String> expectedPkgOrder = new ArrayList<String>();
        //$NON-NLS-1$
        expectedPkgOrder.add("");
        //$NON-NLS-1$
        expectedPkgOrder.add("a");
        //$NON-NLS-1$
        expectedPkgOrder.add("a.b.c");
        final List<String> visit = new ArrayList<String>();
        ApiTypeContainerVisitor visitor = new ApiTypeContainerVisitor() {

            @Override
            public boolean visitPackage(String packageName) {
                visit.add(packageName);
                return false;
            }

            @Override
            public void visit(String packageName, IApiTypeRoot classFile) {
                assertTrue(//$NON-NLS-1$
                "Should not visit types", //$NON-NLS-1$
                false);
            }

            @Override
            public void endVisitPackage(String packageName) {
                assertTrue(//$NON-NLS-1$
                "Wrong end visit order", //$NON-NLS-1$
                visit.get(visit.size() - 1).equals(packageName));
            }

            @Override
            public void end(String packageName, IApiTypeRoot classFile) {
                assertTrue(//$NON-NLS-1$
                "Should not visit types", //$NON-NLS-1$
                false);
            }
        };
        container.accept(visitor);
        //$NON-NLS-1$
        assertEquals("Visited wrong number of packages", expectedPkgOrder.size(), visit.size());
        //$NON-NLS-1$
        assertEquals("Visit order incorrect", expectedPkgOrder, visit);
    }

    /**
	 * Tests visiting class files in an archive.
	 *
	 * @throws CoreException
	 */
    public void testArchiveVisitClassFiles() throws CoreException {
        doTestVisitClassFiles(buildArchiveContainer());
    }

    /**
	 * Tests visiting class files in a directory.
	 *
	 * @throws CoreException
	 */
    public void testDirectoryVisitClassFiles() throws CoreException {
        doTestVisitClassFiles(buildDirectoryContainer());
    }

    /**
	 * Test visiting class files
	 *
	 * @param container class file container
	 * @throws CoreException
	 */
    protected void doTestVisitClassFiles(IApiTypeContainer container) throws CoreException {
        final Map<String, List<String>> expectedTypes = new HashMap<String, List<String>>();
        final List<String> expectedPkgOrder = new ArrayList<String>();
        //$NON-NLS-1$
        expectedPkgOrder.add("");
        List<String> cf = new ArrayList<String>();
        //$NON-NLS-1$
        cf.add("DefA");
        //$NON-NLS-1$
        cf.add("DefB");
        //$NON-NLS-1$
        expectedTypes.put("", cf);
        //$NON-NLS-1$
        expectedPkgOrder.add("a");
        cf = new ArrayList<String>();
        //$NON-NLS-1$
        cf.add("a.ClassA");
        //$NON-NLS-1$
        cf.add("a.ClassB");
        //$NON-NLS-1$
        cf.add("a.ClassB$InsideB");
        //$NON-NLS-1$
        expectedTypes.put("a", cf);
        //$NON-NLS-1$
        expectedPkgOrder.add("a.b.c");
        cf = new ArrayList<String>();
        //$NON-NLS-1$
        cf.add("a.b.c.ClassC");
        //$NON-NLS-1$
        cf.add("a.b.c.ClassD");
        //$NON-NLS-1$
        cf.add("a.b.c.InterfaceC");
        //$NON-NLS-1$
        expectedTypes.put("a.b.c", cf);
        final List<String> visit = new ArrayList<String>();
        final Map<String, List<String>> visitTypes = new HashMap<String, List<String>>();
        ApiTypeContainerVisitor visitor = new ApiTypeContainerVisitor() {

            @Override
            public boolean visitPackage(String packageName) {
                visit.add(packageName);
                return true;
            }

            @Override
            public void visit(String packageName, IApiTypeRoot classFile) {
                assertTrue(//$NON-NLS-1$
                "Should not visit types", //$NON-NLS-1$
                visit.get(visit.size() - 1).equals(packageName));
                List<String> types = visitTypes.get(packageName);
                if (types == null) {
                    types = new ArrayList<String>();
                    visitTypes.put(packageName, types);
                }
                types.add(classFile.getTypeName());
            }

            @Override
            public void endVisitPackage(String packageName) {
                assertTrue(//$NON-NLS-1$
                "Wrong end visit order", //$NON-NLS-1$
                visit.get(visit.size() - 1).equals(packageName));
                assertEquals(//$NON-NLS-1$
                "Visited wrong types", //$NON-NLS-1$
                expectedTypes.get(packageName), //$NON-NLS-1$
                visitTypes.get(packageName));
            }

            @Override
            public void end(String packageName, IApiTypeRoot classFile) {
                List<String> types = visitTypes.get(packageName);
                assertTrue(//$NON-NLS-1$
                "Should not visit types", //$NON-NLS-1$
                types.get(types.size() - 1).equals(classFile.getTypeName()));
            }
        };
        container.accept(visitor);
        //$NON-NLS-1$
        assertEquals("Visited wrong number of packages", expectedPkgOrder.size(), visit.size());
        //$NON-NLS-1$
        assertEquals("Visit order incorrect", expectedPkgOrder, visit);
    }
}
