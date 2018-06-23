/*******************************************************************************
 * Copyright (c) 2005, 2011 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mkaufman@bea.com - initial API and implementation
 *    
 *******************************************************************************/
package org.eclipse.jdt.apt.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.tests.builder.Problem;
import org.eclipse.jdt.core.tests.builder.BuilderTests;

public class PerfTests extends BuilderTests {

    private IPath projectPath;

    public  PerfTests(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(PerfTests.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        IWorkspace ws = env.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();
        IPath path = root.getLocation();
        File destRoot = path.toFile();
        //$NON-NLS-1$//$NON-NLS-2$
        URL platformURL = Platform.getBundle("org.eclipse.jdt.core.tests.binaries").getEntry("/");
        File f = new File(FileLocator.toFileURL(platformURL).getFile());
        //$NON-NLS-1$
        f = new File(f, "perf-test-project.zip");
        InputStream in = new FileInputStream(f);
        ZipInputStream zipIn = new ZipInputStream(in);
        try {
            TestUtil.unzip(zipIn, destRoot);
        } finally {
            zipIn.close();
        }
        // project will be deleted by super-class's tearDown() method
        //$NON-NLS-1$ //$NON-NLS-2$
        projectPath = env.addProject("org.eclipse.jdt.core", "1.4");
        //$NON-NLS-1$
        System.out.println("Performing full build...");
        fullBuild(projectPath);
        //$NON-NLS-1$
        System.out.println("Completed build.");
        assertNoUnexpectedProblems();
    }

    /**
	 * JDT Core has one warning on the use of IWorkingCopy, and a number
	 * of TODOs, XXXs and FIXMEs.
	 */
    private void assertNoUnexpectedProblems() {
        Problem[] problems = env.getProblems();
        for (Problem problem : problems) {
            if (problem.getMessage().startsWith("TODO") || problem.getMessage().startsWith("XXX") || problem.getMessage().startsWith("FIXME")) {
                continue;
            } else {
                if (problem.getMessage().equals("The type IWorkingCopy is deprecated"))
                    continue;
            }
            fail("Found unexpected problem: " + problem);
        }
    }

    public static String getProjectName() {
        //$NON-NLS-1$
        return PerfTests.class.getName() + "Project";
    }

    public IPath getSourcePath() {
        IProject project = env.getProject(getProjectName());
        //$NON-NLS-1$
        IFolder srcFolder = project.getFolder("src");
        IPath srcRoot = srcFolder.getFullPath();
        return srcRoot;
    }

    public void testBuilding() throws Throwable {
        IProject proj = env.getProject(projectPath);
        // doesn't actually create anything
        IJavaProject jproj = JavaCore.create(proj);
        assertNoUnexpectedProblems();
        // Start with APT turned off
        AptConfig.setEnabled(jproj, false);
        proj.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
        assertNoUnexpectedProblems();
        System.out.println("Performing full build without apt...");
        long start = System.currentTimeMillis();
        proj.build(IncrementalProjectBuilder.FULL_BUILD, null);
        long totalWithoutAPT = System.currentTimeMillis() - start;
        System.out.println("Completed full build without APT in " + totalWithoutAPT + "ms.");
        assertNoUnexpectedProblems();
        // Now turn on APT
        AptConfig.setEnabled(jproj, true);
        proj.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
        assertNoUnexpectedProblems();
        System.out.println("Performing full build with apt...");
        start = System.currentTimeMillis();
        proj.build(IncrementalProjectBuilder.FULL_BUILD, null);
        long totalWithAPT = System.currentTimeMillis() - start;
        System.out.println("Completed full build with APT in " + totalWithAPT + "ms.");
        assertNoUnexpectedProblems();
        if (totalWithAPT > totalWithoutAPT * 1.15) {
            fail("APT performance degradation greater than 15%");
        }
    }
}
