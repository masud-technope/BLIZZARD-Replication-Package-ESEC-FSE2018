/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.builder;

import junit.framework.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.tests.util.Util;

/**
 * Basic tests of the image builder.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CopyResourceTests extends BuilderTests {

    public  CopyResourceTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildTestSuite(CopyResourceTests.class);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=117302
    public void testFilteredResources() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        IPath src = env.addPackageFragmentRoot(projectPath, //$NON-NLS-1$
        "", //$NON-NLS-1$
        new IPath[] { new org.eclipse.core.runtime.Path("foo/;bar/") }, //$NON-NLS-1$
        new IPath[] { new org.eclipse.core.runtime.Path("foo/ignored/") }, //$NON-NLS-1$
        "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src, //$NON-NLS-1$ //$NON-NLS-2$
        "foo", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package foo;" + //$NON-NLS-1$
        "public class A extends bar.B {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src, //$NON-NLS-1$ //$NON-NLS-2$
        "bar", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "package bar;" + //$NON-NLS-1$
        "public class B {}");
        //$NON-NLS-1$
        env.addFolder(src, "foo/skip");
        //$NON-NLS-1$
        IPath ignored = env.addFolder(src, "foo/ignored");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        env.addFile(ignored, "test.txt", "test file");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        env.addFile(src.append("bar"), "test.txt", "test file");
        org.eclipse.jdt.core.IJavaProject p = env.getJavaProject("P");
        java.util.Map options = p.getOptions(true);
        //$NON-NLS-1$
        options.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "bar*");
        //$NON-NLS-1$
        options.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RECREATE_MODIFIED_CLASS_FILES_IN_OUTPUT_FOLDER, "enabled");
        p.setOptions(options);
        int max = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        try {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
            fullBuild();
            expectingNoProblems();
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/foo/skip/"));
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/foo/ignored/"));
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/bar/test.txt"));
            //$NON-NLS-1$
            env.removeFolder(projectPath.append("bin/bar"));
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            src, //$NON-NLS-1$ //$NON-NLS-2$
            "x", //$NON-NLS-1$ //$NON-NLS-2$
            "A", //$NON-NLS-1$
            "package x;" + "public class A extends bar.B {}");
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            env.addFile(src.append("bar"), "test.txt", "changed test file");
            incrementalBuild();
            expectingNoProblems();
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/foo/skip/"));
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/foo/ignored/"));
            //$NON-NLS-1$
            expectingNoPresenceOf(projectPath.append("bin/bar/test.txt"));
        } finally {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = max;
        }
    }

    public void testSimpleProject() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P1");
        //$NON-NLS-1$
        IPath src = env.getPackageFragmentRootPath(projectPath, "");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src, "z.txt", "");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$
        expectingPresenceOf(projectPath.append("z.txt"));
        //$NON-NLS-1$
        env.removeFile(src.append("z.txt"));
        //$NON-NLS-1$
        IPath p = env.addFolder(src, "p");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(p, "p.txt", "");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$
        expectingNoPresenceOf(projectPath.append("z.txt"));
        //$NON-NLS-1$
        expectingPresenceOf(p.append("p.txt"));
    }

    public void testProjectWithBin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P2");
        //$NON-NLS-1$
        IPath src = env.getPackageFragmentRootPath(projectPath, "");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src, "z.txt", "");
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("z.txt"), //$NON-NLS-1$
        projectPath.append("bin/z.txt") });
        //$NON-NLS-1$
        env.removeFile(src.append("z.txt"));
        //$NON-NLS-1$
        IPath p = env.addFolder(src, "p");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(p, "p.txt", "");
        incrementalBuild();
        expectingNoProblems();
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("z.txt"), //$NON-NLS-1$
        projectPath.append("bin/z.txt") });
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("p/p.txt"), //$NON-NLS-1$
        projectPath.append("bin/p/p.txt") });
    }

    public void testProjectWithSrcBin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P3");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath src = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src, "z.txt", "");
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src/z.txt"), //$NON-NLS-1$
        projectPath.append("bin/z.txt") });
        //$NON-NLS-1$
        env.removeFile(src.append("z.txt"));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src, "zz.txt", "");
        incrementalBuild();
        expectingNoProblems();
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src/z.txt"), //$NON-NLS-1$
        projectPath.append("bin/z.txt") });
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src/zz.txt"), //$NON-NLS-1$
        projectPath.append("bin/zz.txt") });
    }

    public void testProjectWith2SrcBin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P4");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath src1 = env.addPackageFragmentRoot(projectPath, "src1");
        //$NON-NLS-1$
        IPath src2 = env.addPackageFragmentRoot(projectPath, "src2");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src1, "z.txt", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src2, "zz.txt", "");
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src1/z.txt"), //$NON-NLS-1$
        projectPath.append("bin/z.txt"), //$NON-NLS-1$
        projectPath.append("src2/zz.txt"), //$NON-NLS-1$
        projectPath.append("bin/zz.txt") });
        //$NON-NLS-1$
        env.removeFile(src2.append("zz.txt"));
        //$NON-NLS-1$
        IPath p = env.addFolder(src2, "p");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(p, "p.txt", "");
        incrementalBuild();
        expectingNoProblems();
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src2/zz.txt"), //$NON-NLS-1$
        projectPath.append("bin/zz.txt") });
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src2/p/p.txt"), //$NON-NLS-1$
        projectPath.append("bin/p/p.txt") });
    }

    public void testProjectWith2SrcAsBin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P5");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath src1 = env.addPackageFragmentRoot(projectPath, "src1", null, "src1");
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath src2 = env.addPackageFragmentRoot(projectPath, "src2", null, "src2");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src1, "z.txt", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src2, "zz.txt", "");
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src1/z.txt"), //$NON-NLS-1$
        projectPath.append("src2/zz.txt") });
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("src2/z.txt"), //$NON-NLS-1$
        projectPath.append("bin") });
    }

    public void testProjectWith2Src2Bin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P6");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath src1 = env.addPackageFragmentRoot(projectPath, "src1", null, "bin1");
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath src2 = env.addPackageFragmentRoot(projectPath, "src2", null, "bin2");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin1");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src1, "z.txt", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(src2, "zz.txt", "");
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("bin1/z.txt"), //$NON-NLS-1$
        projectPath.append("bin2/zz.txt") });
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("bin1/zz.txt"), //$NON-NLS-1$
        projectPath.append("bin2/z.txt") });
    }

    public void test2ProjectWith1Bin() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P7");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        IPath bin = env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        IPath projectPath2 = env.addProject("P8");
        //$NON-NLS-1$
        IPath binLocation = env.getProject(projectPath).getFolder("bin").getLocation();
        //$NON-NLS-1$
        env.setExternalOutputFolder(projectPath2, "externalBin", binLocation);
        env.addExternalJars(projectPath2, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(projectPath2, "z.txt", "");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$
        expectingPresenceOf(bin.append("z.txt"));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=154693
    public void testBug154693() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P9");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath src = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        org.eclipse.jdt.core.IJavaProject p = env.getJavaProject("P9");
        java.util.Map options = p.getOptions(true);
        //$NON-NLS-1$
        options.put(org.eclipse.jdt.core.JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, ".svn/");
        p.setOptions(options);
        IPath folder = env.addFolder(src, "p");
        env.addFolder(folder, ".svn");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(folder, "A.java", "package p;\nclass A{}");
        fullBuild();
        expectingNoProblems();
        expectingNoPresenceOf(new IPath[] { //$NON-NLS-1$
        projectPath.append("bin/p/.svn") });
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=194420
    public void testBug194420() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("P");
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath src = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        IPath bin = env.setOutputFolder(projectPath, "bin");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        IPath folder = env.addFolder(src, "p");
        //$NON-NLS-1$
        String testContents = "incremental test contents";
        //$NON-NLS-1$
        IPath zPath = env.addFile(folder, "z.txt", testContents);
        IPath zBinPath = bin.append("p/z.txt");
        org.eclipse.core.resources.IFile zFile = env.getWorkspace().getRoot().getFile(zPath);
        fullBuild();
        expectingNoProblems();
        expectingPresenceOf(zBinPath);
        try {
            byte[] contents = new byte[testContents.length()];
            java.io.InputStream stream = zFile.getContents();
            stream.read(contents);
            stream.close();
            //$NON-NLS-1$
            assumeEquals("File was not copied", testContents, new String(contents));
        } catch (Exception e) {
            fail("File was not copied");
        }
        java.io.File file = new java.io.File(zFile.getLocation().toOSString());
        file.delete();
        fullBuild();
        expectingNoProblems();
        expectingNoPresenceOf(zBinPath);
        //$NON-NLS-1$
        testContents = "incremental test contents";
        //$NON-NLS-1$
        env.addFile(folder, "z.txt", testContents);
        incrementalBuild();
        expectingNoProblems();
        expectingPresenceOf(zBinPath);
        try {
            byte[] contents = new byte[testContents.length()];
            java.io.InputStream stream = zFile.getContents();
            stream.read(contents);
            stream.close();
            //$NON-NLS-1$
            assumeEquals("File was not copied", testContents, new String(contents));
        } catch (Exception e) {
            fail("File was not copied");
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addFile(folder, "z.txt", "about to be deleted");
        file.delete();
        incrementalBuild();
        expectingNoProblems();
        expectingNoPresenceOf(zBinPath);
    }
}
