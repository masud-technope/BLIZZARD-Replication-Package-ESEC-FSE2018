/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.util.tests;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.service.resolver.ResolverError;
import org.eclipse.pde.api.tools.internal.IApiCoreConstants;
import org.eclipse.pde.api.tools.internal.provisional.IApiDescription;
import org.eclipse.pde.api.tools.internal.provisional.IApiFilterStore;
import org.eclipse.pde.api.tools.internal.provisional.IRequiredComponentDescription;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.model.ApiTypeContainerVisitor;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiElement;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeContainer;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeRoot;
import org.eclipse.pde.api.tools.internal.search.IReferenceCollection;
import org.eclipse.pde.api.tools.internal.util.FilteredElements;
import org.eclipse.pde.api.tools.internal.util.SinceTagVersion;
import org.eclipse.pde.api.tools.internal.util.Util;
import org.eclipse.pde.api.tools.model.tests.TestSuiteHelper;
import junit.framework.TestCase;

/**
 * Tests the methods in our utility class: {@link Util}
 *
 * @since 1.0.0
 */
public class UtilTests extends TestCase {

    //$NON-NLS-1$
    private static final IPath SRC_LOC = TestSuiteHelper.getPluginDirectoryPath().append("test_source");

    //$NON-NLS-1$
    static final IPath SRC_LOC_SEARCH = TestSuiteHelper.getPluginDirectoryPath().append("test-search");

    /**
	 * Tests that passing in <code>null</code> to the getAllFiles(..) method
	 * will return all of the files in that directory
	 */
    public void getAllFilesNullFilter() {
        File root = new File(SRC_LOC.toOSString());
        //$NON-NLS-1$
        assertTrue("The test source  directory must exist", root.exists());
        //$NON-NLS-1$
        assertTrue("The source location should be a directory", root.isDirectory());
        File[] files = Util.getAllFiles(root, null);
        //$NON-NLS-1$
        assertTrue("There should be more than one file in the test source directory", files.length > 1);
    }

    /**
	 * Tests that passing an illegal argument when creating a new {@link SinceTagVersion} throws an exception
	 */
    public void testIllegalArgSinceTagVersion() {
        try {
            new SinceTagVersion(null);
            //$NON-NLS-1$
            fail("creating a since tag version with a null value should have thrown an exception");
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
	 * Tests that a filter passed to getALlFiles(..) returns only the files
	 * it is supposed to. In this test only files that end with <code>TestClass1.java</code>
	 * pass the filter (there is only one).
	 */
    public void getAllFilesSpecificFilter() {
        File root = new File(SRC_LOC.toOSString());
        //$NON-NLS-1$
        assertTrue("The test source  directory must exist", root.exists());
        //$NON-NLS-1$
        assertTrue("The source location should be a directory", root.isDirectory());
        File[] files = Util.getAllFiles(root, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return //$NON-NLS-1$
                pathname.getAbsolutePath().endsWith(//$NON-NLS-1$
                "TestClass1.java");
            }
        });
        //$NON-NLS-1$
        assertTrue("There should be only one file in the test source directory named 'TestClass1.java'", files.length == 1);
    }

    /**
	 * Tests that the isClassFile method works as expected when passed a valid name (*.class)
	 */
    public void testIsClassfile() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.class is a class file", Util.isClassFile("Test.class"));
    }

    /**
	 * Tests that the isClassFile method works as expected when passed an invalid name (not *.class)
	 */
    public void testIsNotClassfile() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.notclass is not a classfile", !Util.isClassFile("Test.notclass"));
    }

    /**
	 * Tests that the isArchive method works as expected when passed a valid archive
	 * name (*.zip or *.jar)
	 */
    public void testIsArchive() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.zip is an archive", Util.isArchive("Test.zip"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.jar is an archive", Util.isArchive("Test.jar"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.tar.gz"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isTGZFile("Test.tgz"));
    }

    /**
	 * Tests that the isTGZFile method works as expected
	 */
    public void testIsTar() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.tar.gz"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.TAR.GZ"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.Tar.Gz"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.tar.GZ"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isTGZFile("Test.TAR.gz"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isTGZFile("Test.tgz"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isTGZFile("Test.TGZ"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isTGZFile("Test.Tgz"));
    }

    /**
	 * Tests that the isZipJarFile method works as expected
	 */
    public void testIsJar() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isZipJarFile("Test.zip"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isZipJarFile("Test.ZIP"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tar.gz is an archive", Util.isZipJarFile("Test.Zip"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isZipJarFile("Test.jar"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isZipJarFile("Test.JAR"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.tgz is an archive", Util.isZipJarFile("Test.Jar"));
    }

    /**
	 * Tests that the isArchive method works as expected when passed an invalid archive
	 * name (*.notzip)
	 */
    public void testisNotArchive() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("Test.notzip is not an archive", !Util.isArchive("Test.notzip"));
    }

    /*
	 * Test org.eclipse.pde.api.tools.internal.util.Util.getFragmentNumber(String)
	 * org.eclipse.pde.api.tools.internal.util.Util.isGreatherVersion(String, String)
	 */
    public void testGetFragmentNumber() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", 2, Util.getFragmentNumber("org.eclipse.core.filesystem 1.0"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", 2, Util.getFragmentNumber("1.0"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", 3, Util.getFragmentNumber("1.0.0"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", 1, Util.getFragmentNumber("1"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", 4, Util.getFragmentNumber("org.test 1.0.0.0"));
        try {
            Util.getFragmentNumber((String) null);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", -1, Util.getFragmentNumber("org.test "));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", -1, Util.getFragmentNumber("org.test"));
    }

    /*
	 * Test org.eclipse.pde.api.tools.internal.util.Util.isGreatherVersion(String, String)
	 */
    public void testIsGreatherVersion() {
        //$NON-NLS-1$
        assertEquals("wrong value", 1, 1);
    }

    public void testSinceTagVersion() {
        try {
            new SinceTagVersion(null);
            //$NON-NLS-1$
            assertTrue("Should not reach there", false);
        } catch (IllegalArgumentException e) {
        }
        //$NON-NLS-1$
        SinceTagVersion sinceTagVersion = new SinceTagVersion(" org.eclipse.jdt.core 3.4. test plugin");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", " org.eclipse.jdt.core ", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " test plugin", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("org.eclipse.jdt.core 3.4.0.");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", "org.eclipse.jdt.core ", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("org.eclipse.jdt.core 3.4.0.qualifier");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.qualifier", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", "org.eclipse.jdt.core ", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("org.eclipse.jdt.core 3.4.0.qualifier postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.qualifier", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", "org.eclipse.jdt.core ", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0.");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0.qualifier");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.qualifier", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$
        assertNull("wrong postfix string", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0.qualifier postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.qualifier", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0. postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4.0 postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.0", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4. postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4 postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3. postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3 postfix");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " postfix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("prefix");
        //$NON-NLS-1$
        assertNull("wrong version string", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", "prefix", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("test 3.4 protected (was added in 2.1 as private class)");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", "test ", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " protected (was added in 2.1 as private class)", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4 protected (was added in 2.1 as private class)");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " protected (was added in 2.1 as private class)", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("abc1.0");
        //$NON-NLS-1$
        assertNull("Wrong version string", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("Wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong postfix string", "abc1.0", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("3.4, was added in 3.1 as private method");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.4", sinceTagVersion.getVersionString());
        //$NON-NLS-1$
        assertNull("wrong prefix string", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", ", was added in 3.1 as private method", sinceTagVersion.postfixString());
        //$NON-NLS-1$
        sinceTagVersion = new SinceTagVersion("abc1.0, was added in 3.1 as private method");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong version string", "3.1", sinceTagVersion.getVersionString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong prefix string", "abc1.0, was added in ", sinceTagVersion.prefixString());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong postfix string", " as private method", sinceTagVersion.postfixString());
    }

    public void testRegexExcludeList() {
        //$NON-NLS-1$
        String line = "R:org\\.eclipse\\.swt[a-zA-Z_0-9\\.]*";
        class LocalApiComponent implements IApiComponent {

            String symbolicName;

            public  LocalApiComponent(String symbolicName) {
                this.symbolicName = symbolicName;
            }

            @Override
            public String[] getPackageNames() throws CoreException {
                return null;
            }

            @Override
            public IApiTypeRoot findTypeRoot(String qualifiedName) throws CoreException {
                return null;
            }

            @Override
            public IApiTypeRoot findTypeRoot(String qualifiedName, String id) throws CoreException {
                return null;
            }

            @Override
            public void accept(ApiTypeContainerVisitor visitor) throws CoreException {
            }

            @Override
            public void close() throws CoreException {
            }

            @Override
            public int getContainerType() {
                return 0;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public int getType() {
                return 0;
            }

            @Override
            public IApiElement getParent() {
                return null;
            }

            @Override
            public IApiElement getAncestor(int ancestorType) {
                return null;
            }

            @Override
            public IApiComponent getApiComponent() {
                return null;
            }

            @Override
            public String getSymbolicName() {
                return this.symbolicName;
            }

            @Override
            public IApiDescription getApiDescription() throws CoreException {
                return null;
            }

            @Override
            public boolean hasApiDescription() {
                return false;
            }

            @Override
            public String getVersion() {
                return null;
            }

            @Override
            public String[] getExecutionEnvironments() throws CoreException {
                return null;
            }

            @Override
            public IApiTypeContainer[] getApiTypeContainers() throws CoreException {
                return null;
            }

            @Override
            public IApiTypeContainer[] getApiTypeContainers(String id) throws CoreException {
                return null;
            }

            @Override
            public IRequiredComponentDescription[] getRequiredComponents() throws CoreException {
                return null;
            }

            @Override
            public String getLocation() {
                return null;
            }

            @Override
            public boolean isSystemComponent() {
                return false;
            }

            @Override
            public boolean isSourceComponent() throws CoreException {
                return false;
            }

            @Override
            public void dispose() {
            }

            @Override
            public IApiBaseline getBaseline() throws CoreException {
                return null;
            }

            @Override
            public IApiFilterStore getFilterStore() throws CoreException {
                return null;
            }

            @Override
            public boolean isFragment() throws CoreException {
                return false;
            }

            @Override
            public IApiComponent getHost() throws CoreException {
                return null;
            }

            @Override
            public boolean hasFragments() throws CoreException {
                return false;
            }

            @Override
            public String[] getLowestEEs() throws CoreException {
                return null;
            }

            @Override
            public ResolverError[] getErrors() throws CoreException {
                return null;
            }

            @Override
            public IElementDescriptor getHandle() {
                return null;
            }

            @Override
            public IReferenceCollection getExternalDependencies() {
                return null;
            }
        }
        List<IApiComponent> allComponents = new ArrayList<IApiComponent>();
        String[] componentNames = new String[] { "org.eclipse.swt", "org.eclipse.equinox.simpleconfigurator.manipulator", "org.eclipse.team.ui", "org.eclipse.ecf", "org.eclipse.core.commands", "org.eclipse.equinox.util", "org.eclipse.equinox.p2.jarprocessor", "org.eclipse.equinox.security", "org.eclipse.sdk", "org.eclipse.help.ui", "org.eclipse.jdt.doc.isv", "org.eclipse.equinox.p2.core", "org.eclipse.debug.ui", "org.eclipse.ui.navigator", "org.eclipse.update.core", "javax.servlet.jsp", "org.eclipse.ui.workbench", "org.eclipse.equinox.event", "org.eclipse.jdt.core", //$NON-NLS-1$
        "JavaSE-1.7", "org.apache.commons.codec", "org.apache.commons.logging", "org.objectweb.asm", "org.eclipse.core.filebuffers", "org.eclipse.jsch.ui", "org.eclipse.platform", "org.eclipse.pde.ua.core", "org.eclipse.help", "org.eclipse.ecf.provider.filetransfer", "org.eclipse.equinox.preferences", "org.eclipse.equinox.p2.reconciler.dropins", "org.eclipse.team.cvs.ui", "org.eclipse.equinox.p2.metadata.generator", "org.eclipse.equinox.registry", "org.eclipse.update.ui", "org.eclipse.swt", "org.eclipse.ui.console", //$NON-NLS-1$
        "org.junit4", "org.eclipse.ui.views.log", "org.eclipse.equinox.p2.touchpoint.natives", "org.eclipse.equinox.ds", "org.eclipse.help.base", "org.eclipse.equinox.frameworkadmin.equinox", "org.eclipse.jdt", "org.eclipse.osgi.util", "org.sat4j.pb", "org.hamcrest.core", "org.eclipse.jdt.junit4.runtime", "org.eclipse.equinox.p2.artifact.repository", "org.eclipse.core.databinding.property", "org.eclipse.core.databinding", "org.eclipse.equinox.concurrent", "org.eclipse.pde.ua.ui", "org.eclipse.ui.navigator.resources", "org.eclipse.equinox.http.servlet", "org.eclipse.jsch.core", "javax.servlet", "org.eclipse.jface", "org.eclipse.equinox.p2.updatesite", "org.eclipse.jface.databinding", "org.eclipse.ui.browser", "org.eclipse.ui", "org.eclipse.core.databinding.beans", "org.eclipse.search", "org.eclipse.equinox.jsp.jasper.registry", "org.eclipse.jdt.debug", "org.eclipse.ecf.provider.filetransfer.ssl", "org.eclipse.platform.doc.isv", "org.eclipse.update.core.win32", "org.eclipse.pde.api.tools", "org.eclipse.ui.ide.application", "org.eclipse.equinox.p2.metadata", "org.eclipse.equinox.security.win32.x86", "org.eclipse.core.contenttype", "org.eclipse.equinox.p2.ui.sdk", "org.eclipse.core.resources", "org.eclipse.pde.launching", "org.eclipse.ui.externaltools", "org.eclipse.cvs", "org.eclipse.equinox.p2.repository", "org.eclipse.core.resources.win32.x86", "org.eclipse.pde.ui", "org.eclipse.core.databinding.observable", "org.eclipse.pde.doc.user", "org.eclipse.ui.editors", "org.eclipse.jdt.compiler.tool", "org.eclipse.jdt.apt.ui", "org.eclipse.rcp", "org.eclipse.ui.presentations.r21", "org.eclipse.pde.runtime", "org.eclipse.equinox.security.ui", "org.eclipse.core.jobs", "org.eclipse.update.configurator", "org.eclipse.equinox.http.jetty", "org.eclipse.pde.ds.ui", "org.apache.lucene.analysis", "org.eclipse.ui.views", "org.eclipse.equinox.common", "org.apache.lucene", "org.eclipse.ecf.identity", "org.eclipse.ui.workbench.texteditor", "org.eclipse.equinox.p2.ui", "org.eclipse.core.runtime.compatibility.auth", "org.eclipse.ltk.core.refactoring", "org.eclipse.ant.core", "org.eclipse.ant.launching", "com.jcraft.jsch", "org.eclipse.ui.win32", "org.eclipse.pde.core", "org.eclipse.pde.build", "org.eclipse.ltk.ui.refactoring", "org.eclipse.jface.text", "org.apache.commons.el", "org.eclipse.compare.win32", "org.eclipse.core.runtime", "org.eclipse.jdt.ui", "org.eclipse.compare", "org.eclipse.ui.forms", "org.eclipse.equinox.p2.extensionlocation", "org.mortbay.jetty.util", "org.eclipse.equinox.p2.director", "org.eclipse.core.filesystem", "org.eclipse.jdt.junit.core", "org.eclipse.jdt.junit.runtime", "org.eclipse.team.cvs.ssh2", "org.eclipse.core.variables", "org.eclipse.platform.doc.user", "org.eclipse.equinox.p2.operations", "org.eclipse.core.externaltools", "org.eclipse.equinox.simpleconfigurator", "org.eclipse.equinox.p2.touchpoint.eclipse", "org.eclipse.equinox.p2.metadata.repository", "org.eclipse.pde.ds.core", "org.eclipse.jdt.apt.pluggable.core", "org.eclipse.team.cvs.core", "org.mortbay.jetty.server", "org.eclipse.text", "org.eclipse.jdt.compiler.apt", "org.eclipse.equinox.p2.director.app", "org.eclipse.jdt.debug.ui", "org.eclipse.equinox.p2.repository.tools", "org.apache.commons.httpclient", "org.eclipse.equinox.p2.garbagecollector", "org.eclipse.ui.ide", "org.eclipse.equinox.p2.engine", "org.apache.ant", "org.eclipse.jdt.junit", "org.eclipse.ecf.filetransfer", "org.eclipse.core.filesystem.win32.x86", "org.eclipse.core.net", "org.eclipse.equinox.jsp.jasper", "org.eclipse.equinox.p2.directorywatcher", "org.eclipse.equinox.http.registry", //$NON-NLS-1$
        "org.junit", "org.eclipse.pde.junit.runtime", "org.eclipse.equinox.launcher", "org.eclipse.jdt.launching", "org.eclipse.core.expressions", "org.eclipse.ui.intro", "org.eclipse.team.core", "org.eclipse.ui.intro.universal", "org.eclipse.swt.win32.win32.x86", "org.eclipse.osgi.services", "org.eclipse.pde", "org.eclipse.ui.views.properties.tabbed", "org.eclipse.core.runtime.compatibility", "org.eclipse.ant.ui", "org.eclipse.ecf.provider.filetransfer.httpclient.ssl", "org.eclipse.equinox.launcher.win32.win32.x86", "org.eclipse.core.boot", "org.apache.jasper", "org.eclipse.help.webapp", "org.sat4j.core", "org.eclipse.pde.api.tools.ui", "org.eclipse.equinox.p2.ui.sdk.scheduler", "org.eclipse.debug.core", "org.eclipse.jdt.core.manipulation", "org.eclipse.osgi", "org.eclipse.update.scheduler", "org.eclipse.equinox.p2.updatechecker", "org.eclipse.equinox.p2.console", "org.eclipse.equinox.frameworkadmin", "org.eclipse.compare.core", "org.eclipse.jdt.apt.core", "org.eclipse.help.appserver", "org.eclipse.pde.ui.templates", "org.eclipse.ecf.ssl", "org.eclipse.ui.cheatsheets", //$NON-NLS-1$
        "com.ibm.icu", "org.eclipse.core.net.win32.x86", "org.eclipse.jdt.doc.user", "org.eclipse.equinox.app", "org.eclipse.ui.net", "org.eclipse.equinox.p2.publisher", "org.eclipse.ecf.provider.filetransfer.httpclient" };
        for (int i = 0, max = componentNames.length; i < max; i++) {
            allComponents.add(new LocalApiComponent(componentNames[i]));
        }
        IApiComponent[] components = new IApiComponent[allComponents.size()];
        allComponents.toArray(components);
        FilteredElements excludedElements = new FilteredElements();
        try {
            Util.collectRegexIds(line, excludedElements, components, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //$NON-NLS-1$
        assertEquals("Wrong size", 2, excludedElements.getPartialMatches().size());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("Wrong result", excludedElements.containsPartialMatch("org.eclipse.jdt.core"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("Wrong result", excludedElements.containsExactMatch("org.eclipse.jdt.core"));
    }

    public void testRegexExcludeList2() {
        FilteredElements excludedElements = new FilteredElements();
        //$NON-NLS-1$
        assertEquals("Wrong size", 0, excludedElements.getPartialMatches().size());
        //$NON-NLS-1$
        assertEquals("Wrong size", 0, excludedElements.getExactMatches().size());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("Wrong result", excludedElements.containsPartialMatch("org.eclipse.jdt.core"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("Wrong result", excludedElements.containsExactMatch("org.eclipse.jdt.core"));
    }

    public void testPluginXmlDecoding() {
        //$NON-NLS-1$
        InputStream stream = UtilTests.class.getResourceAsStream("plugin.xml.zip");
        ZipInputStream inputStream = new ZipInputStream(new BufferedInputStream(stream));
        String s = null;
        try {
            ZipEntry zEntry;
            while ((zEntry = inputStream.getNextEntry()) != null) {
                // if it is empty directory, continue
                if (//$NON-NLS-1$
                zEntry.isDirectory() || !zEntry.getName().endsWith(".xml")) {
                    continue;
                }
                s = new String(Util.getInputStreamAsCharArray(inputStream, -1, IApiCoreConstants.UTF_8));
            }
        } catch (IOException e) {
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ioe) {
            }
        }
        //$NON-NLS-1$
        assertNotNull("Should not be null", s);
        try {
            Util.parseDocument(s);
        } catch (CoreException ce) {
            assertTrue("Should not happen", false);
        }
    }

    /**
	 * Tests that the utility method for reading in include/exclude regex tests throws an exception
	 * when the file doesn't exist.
	 *
	 * The regex parsing is tested more extensively in {@link org.eclipse.pde.api.tools.search.tests.SearchEngineTests}
	 */
    public void testInitializeRegexFilterList() {
        //$NON-NLS-1$
        File bogus = new File(SRC_LOC.toFile(), "DOES_NOT_EXIST");
        try {
            Util.initializeRegexFilterList(bogus.getAbsolutePath(), null, false);
            //$NON-NLS-1$
            fail("Util.initializeRegexFilterList should throw Core Exception for missing file");
        } catch (CoreException e) {
        }
    }
}
