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

import java.util.Hashtable;
import junit.framework.Test;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.tests.util.Util;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IncrementalTests extends BuilderTests {

    public  IncrementalTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildTestSuite(IncrementalTests.class);
    }

    /*
	 * Ensures that the source range for a duplicate secondary type error is correct
	 * (regression test for https://bugs.eclipse.org/bugs/show_bug.cgi?id=77283)
	 */
    public void testAddDuplicateSecondaryType() throws JavaModelException {
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        env.removePackageFragmentRoot(projectPath, "");
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        env.setOutputFolder(projectPath, "bin");
        env.addClass(root, "p", "C", "package p;	\n" + "public class C {}	\n" + "class CC {}");
        fullBuild(projectPath);
        expectingNoProblems();
        IPath pathToD = env.addClass(root, "p", "D", "package p;	\n" + "public class D {}	\n" + "class CC {}");
        incrementalBuild(projectPath);
        expectingProblemsFor(pathToD, "Problem : The type CC is already defined [ resource : </Project/src/p/D.java> range : <37,39> category : <-1> severity : <2>]");
        expectingSpecificProblemsFor(pathToD, new Problem[] { new Problem("", "The type CC is already defined", pathToD, 37, 39, -1, IMarker.SEVERITY_ERROR) });
        env.removeProject(projectPath);
    }

    public void testDefaultPackage() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "public class A {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "public class B {}");
        fullBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "public class B {A a;}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testDefaultPackage2() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "public class A {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "public class B {}");
        fullBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "public class B {A a;}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testNewJCL() {
        //----------------------------
        //           Step 1
        //----------------------------
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        //$NON-NLS-1$
        IPath root = env.getPackageFragmentRootPath(projectPath, "");
        fullBuild();
        expectingNoProblems();
        //----------------------------
        //           Step 2
        //----------------------------
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "java.lang", //$NON-NLS-1$ //$NON-NLS-2$
        "Object", //$NON-NLS-1$
        "package java.lang;\n" + //$NON-NLS-1$
        "public class Object {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild();
        expectingNoProblems();
        //----------------------------
        //           Step 3
        //----------------------------
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "java.lang", //$NON-NLS-1$ //$NON-NLS-2$
        "Throwable", //$NON-NLS-1$
        "package java.lang;\n" + //$NON-NLS-1$
        "public class Throwable {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild();
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    /*
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=17329
	 */
    public void testRenameMainType() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        /* A.java */
        IPath pathToA = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class A {}");
        /* B.java */
        IPath pathToB = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class B extends A {}");
        /* C.java */
        IPath pathToC = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "C", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class C extends B {}");
        fullBuild(projectPath);
        expectingNoProblems();
        /* Touch both A and C, removing A main type */
        pathToA = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class _A {}");
        pathToC = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "C", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class C extends B { }");
        incrementalBuild(projectPath);
        expectingProblemsFor(new IPath[] { pathToA, pathToB, pathToC }, "Problem : A cannot be resolved to a type [ resource : </Project/src/p/B.java> range : <35,36> category : <40> severity : <2>]\n" + "Problem : The hierarchy of the type C is inconsistent [ resource : </Project/src/p/C.java> range : <25,26> category : <40> severity : <2>]\n" + "Problem : The public type _A must be defined in its own file [ resource : </Project/src/p/A.java> range : <25,27> category : <40> severity : <2>]");
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(pathToA, new Problem("_A", "The public type _A must be defined in its own file", pathToA, 25, 27, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(pathToB, new Problem("B", "A cannot be resolved to a type", pathToB, 35, 36, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(pathToC, new Problem("C", "The hierarchy of the type C is inconsistent", pathToC, 25, 26, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        /* Touch both A and C, removing A main type */
        pathToA = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class A {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    /*
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=17807
	 * case 1
	 */
    public void testRemoveSecondaryType() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AA {}	\n" + //$NON-NLS-1$
        "class AZ {}");
        IPath pathToAB = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AB extends AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "BB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class BB {	\n" + //$NON-NLS-1$
        "	void foo(){	\n" + "		System.out.println(new AB());	\n" + "		System.out.println(new ZA());	\n" + "	}	\n" + //$NON-NLS-1$
        "}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class ZZ {}	\n" + //$NON-NLS-1$
        "class ZA {}");
        fullBuild(projectPath);
        expectingNoProblems();
        /* Remove AZ and touch BB */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AA {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "BB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class BB {	\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		System.out.println(new AB());	\n" + "		System.out.println(new ZA());	\n" + "	}	\n" + //$NON-NLS-1$
        "}");
        incrementalBuild(projectPath);
        expectingProblemsFor(pathToAB, "Problem : AZ cannot be resolved to a type [ resource : </Project/src/p/AB.java> range : <36,38> category : <40> severity : <2>]");
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(pathToAB, new Problem("AB", "AZ cannot be resolved to a type", pathToAB, 36, 38, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AA {}	\n" + //$NON-NLS-1$
        "class AZ {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    /*
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=17807
	 * case 2
	 */
    public void testRemoveSecondaryType2() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AA {}	\n" + //$NON-NLS-1$
        "class AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class AB extends AZ {}");
        IPath pathToBB = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "BB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class BB {	\n" + //$NON-NLS-1$
        "	void foo(){	\n" + "		System.out.println(new AB());	\n" + "		System.out.println(new ZA());	\n" + "	}	\n" + //$NON-NLS-1$
        "}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class ZZ {}	\n" + //$NON-NLS-1$
        "class ZA {}");
        fullBuild(projectPath);
        expectingNoProblems();
        /* Remove ZA and touch BB */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class ZZ {}");
        pathToBB = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "BB", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class BB {	\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		System.out.println(new AB());	\n" + "		System.out.println(new ZA());	\n" + "	}	\n" + //$NON-NLS-1$
        "}");
        incrementalBuild(projectPath);
        expectingProblemsFor(pathToBB, "Problem : ZA cannot be resolved to a type [ resource : </Project/src/p/BB.java> range : <104,106> category : <40> severity : <2>]");
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(pathToBB, new Problem("BB.foo()", "ZA cannot be resolved to a type", pathToBB, 104, 106, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p;	\n" + //$NON-NLS-1$
        "public class ZZ {}	\n" + //$NON-NLS-1$
        "class ZA {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testMoveSecondaryType() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {} \n" + //$NON-NLS-1$
        "class AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AB", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AB extends AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {}");
        fullBuild(projectPath);
        expectingNoProblems();
        /* Move AZ from AA to ZZ */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {} \n" + //$NON-NLS-1$
        "class AZ {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        /* Move AZ from ZZ to AA */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {} \n" + //$NON-NLS-1$
        "class AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testMoveMemberType() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {} \n" + //$NON-NLS-1$
        "class AZ {static class M{}}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AB", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "import p.AZ.*; \n" + //$NON-NLS-1$
        "import p.ZA.*; \n" + //$NON-NLS-1$
        "public class AB extends M {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {} \n" + //$NON-NLS-1$
        "class ZA {}");
        fullBuild(projectPath);
        expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new Problem("", "The import p.ZA is never used", new Path("/Project/src/p/AB.java"), 35, 39, CategorizedProblem.CAT_UNNECESSARY_CODE, IMarker.SEVERITY_WARNING) });
        /* Move M from AA to ZZ */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {} \n" + //$NON-NLS-1$
        "class AZ {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {} \n" + //$NON-NLS-1$
        "class ZA {static class M{}}");
        incrementalBuild(projectPath);
        expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new Problem("", "The import p.AZ is never used", new Path("/Project/src/p/AB.java"), 19, 23, CategorizedProblem.CAT_UNNECESSARY_CODE, IMarker.SEVERITY_WARNING) });
        /* Move M from ZZ to AA */
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class AA {} \n" + //$NON-NLS-1$
        "class AZ {static class M{}}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "ZZ", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class ZZ {} \n" + //$NON-NLS-1$
        "class ZA {}");
        incrementalBuild(projectPath);
        expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new Problem("", "The import p.ZA is never used", new Path("/Project/src/p/AB.java"), 35, 39, CategorizedProblem.CAT_UNNECESSARY_CODE, IMarker.SEVERITY_WARNING) });
        env.removeProject(projectPath);
    }

    public void testMovePackage() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath[] exclusionPatterns = new Path[] { new Path("src2/") };
        //$NON-NLS-1$
        IPath src1 = env.addPackageFragmentRoot(projectPath, "src1", exclusionPatterns, null);
        //$NON-NLS-1$
        IPath src2 = env.addPackageFragmentRoot(projectPath, "src1/src2");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src1, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class A {}");
        fullBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$
        env.removePackage(src1, "p");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src2, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class A {}");
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testMovePackage2() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath src = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        IPath other = env.addFolder(projectPath, "other");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        IPath classA = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class A extends Missing {}");
        IPath classB = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        src, //$NON-NLS-1$ //$NON-NLS-2$
        "p.q", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "package p.q; \n" + //$NON-NLS-1$
        "public class B extends Missing {}");
        fullBuild(projectPath);
        expectingSpecificProblemFor(classA, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new Problem("", "Missing cannot be resolved to a type", new Path("/Project/src/p/A.java"), 35, 42, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        expectingSpecificProblemFor(classB, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new Problem("", "Missing cannot be resolved to a type", new Path("/Project/src/p/q/B.java"), 37, 44, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        try {
            IProject p = env.getProject(projectPath);
            IFolder pFolder = p.getWorkspace().getRoot().getFolder(classA.removeLastSegments(1));
            pFolder.move(other.append("p"), true, false, null);
        } catch (CoreException e) {
            env.handle(e);
        }
        incrementalBuild(projectPath);
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    public void testMemberTypeFromClassFile() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class A extends Z {M[] m;}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class B {A a; E e; \n" + //$NON-NLS-1$
        "void foo() { System.out.println(a.m); }}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "E", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class E extends Z { \n" + //$NON-NLS-1$
        "void foo() { System.out.println(new M()); }}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class Z {static class M {}}");
        fullBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "B", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class B {A a; E e; \n" + //$NON-NLS-1$
        "void foo( ) { System.out.println(a.m); }}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "E", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class E extends Z { \n" + //$NON-NLS-1$
        "void foo( ) { System.out.println(new M()); }}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p; \n" + //$NON-NLS-1$
        "public class Z { static class M {} }");
        int previous = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        // reduce the lot size
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
        incrementalBuild(projectPath);
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = previous;
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=372418
    public void testMemberTypeOfOtherProject() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath projectPath1 = env.addProject("Project1", "1.5");
        env.addExternalJars(projectPath1, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath projectPath2 = env.addProject("Project2", "1.5");
        env.addExternalJars(projectPath2, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath1, "");
        //$NON-NLS-1$
        IPath root1 = env.addPackageFragmentRoot(projectPath1, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath1, "bin");
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath2, "");
        //$NON-NLS-1$
        IPath root2 = env.addPackageFragmentRoot(projectPath2, "src");
        //$NON-NLS-1$
        IPath output2 = env.setOutputFolder(projectPath2, "bin");
        env.addClassFolder(projectPath1, output2, true);
        env.addRequiredProject(projectPath2, projectPath1);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root1, //$NON-NLS-1$ //$NON-NLS-2$
        "pB", //$NON-NLS-1$ //$NON-NLS-2$
        "BaseClass", //$NON-NLS-1$
        "package pB; \n" + //$NON-NLS-1$
        "public class BaseClass {\n" + //$NON-NLS-1$
        "  public static class Builder <T> {\n" + //$NON-NLS-1$
        "    public Builder(T t) {\n" + //$NON-NLS-1$
        "    }\n" + //$NON-NLS-1$
        "  }\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root1, //$NON-NLS-1$ //$NON-NLS-2$
        "pR", //$NON-NLS-1$ //$NON-NLS-2$
        "ReferencingClass", "package pR; \n" + "import pD.DerivedClass.Builder;\n" + //$NON-NLS-1$
        "public class ReferencingClass {\n" + //$NON-NLS-1$
        "   Builder<String> builder = new Builder<String>(null);\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root2, //$NON-NLS-1$ //$NON-NLS-2$
        "pD", //$NON-NLS-1$ //$NON-NLS-2$
        "DerivedClass", "package pD; \n" + //$NON-NLS-1$
        "public class DerivedClass extends pB.BaseClass {\n" + "  public static class Builder<T> extends pB.BaseClass.Builder <T> {\n" + //$NON-NLS-1$
        "    public Builder(T t) {\n" + "		super(t);\n" + //$NON-NLS-1$
        "    }\n" + //$NON-NLS-1$
        "  }\n" + //$NON-NLS-1$
        "}\n");
        int previous = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        fullBuild();
        // reduce the lot size
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
        cleanBuild();
        fullBuild();
        //$NON-NLS-1$
        cleanBuild("Project1");
        fullBuild(projectPath1);
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = previous;
        expectingNoProblems();
        env.removeProject(projectPath1);
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=377401
    public void test$InTypeName() throws JavaModelException {
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath projectPath1 = env.addProject("Project1", "1.5");
        env.addExternalJars(projectPath1, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        IPath projectPath2 = env.addProject("Project2", "1.5");
        env.addExternalJars(projectPath2, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath1, "");
        //$NON-NLS-1$
        IPath root1 = env.addPackageFragmentRoot(projectPath1, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath1, "bin");
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath2, "");
        //$NON-NLS-1$
        IPath root2 = env.addPackageFragmentRoot(projectPath2, "src");
        //$NON-NLS-1$
        IPath output2 = env.setOutputFolder(projectPath2, "bin");
        env.addClassFolder(projectPath1, output2, true);
        env.addRequiredProject(projectPath2, projectPath1);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root1, //$NON-NLS-1$ //$NON-NLS-2$
        "pB", //$NON-NLS-1$ //$NON-NLS-2$
        "Builder$a", //$NON-NLS-1$
        "package pB; \n" + //$NON-NLS-1$
        "public class Builder$a<T> {\n" + //$NON-NLS-1$
        "    public Builder$a(T t) {\n" + //$NON-NLS-1$
        "    }\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root1, //$NON-NLS-1$ //$NON-NLS-2$
        "pR", //$NON-NLS-1$ //$NON-NLS-2$
        "ReferencingClass", "package pR; \n" + "import pD.DerivedClass$a;\n" + //$NON-NLS-1$
        "public class ReferencingClass {\n" + //$NON-NLS-1$
        "   DerivedClass$a<String> builder = new DerivedClass$a<String>(null);\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root2, //$NON-NLS-1$ //$NON-NLS-2$
        "pD", //$NON-NLS-1$ //$NON-NLS-2$
        "DerivedClass$a", "package pD; \n" + //$NON-NLS-1$
        "public class DerivedClass$a<T> extends pB.Builder$a<T> {\n" + //$NON-NLS-1$
        "    public DerivedClass$a(T t) {\n" + "		super(t);\n" + //$NON-NLS-1$
        "    }\n" + //$NON-NLS-1$
        "}\n");
        int previous = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        fullBuild();
        // reduce the lot size
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
        cleanBuild();
        fullBuild();
        //$NON-NLS-1$
        cleanBuild("Project1");
        fullBuild(projectPath1);
        org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = previous;
        expectingNoProblems();
        env.removeProject(projectPath1);
    }

    // http://dev.eclipse.org/bugs/show_bug.cgi?id=27658
    public void testObjectWithSuperInterfaces() throws JavaModelException {
        try {
            //$NON-NLS-1$
            IPath projectPath = env.addProject("Project");
            env.addExternalJars(projectPath, Util.getJavaClassLibs());
            // remove old package fragment root so that names don't collide
            //$NON-NLS-1$
            env.removePackageFragmentRoot(projectPath, "");
            //$NON-NLS-1$
            IPath root = env.addPackageFragmentRoot(projectPath, "src");
            //$NON-NLS-1$
            env.setOutputFolder(projectPath, "bin");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "java.lang", //$NON-NLS-1$ //$NON-NLS-2$
            "Object", "package java.lang; \n" + "public class Object implements I {} \n" + //$NON-NLS-1$
            "interface I {}	\n");
            fullBuild(projectPath);
            expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new Problem("", "The type java.lang.Object cannot have a superclass or superinterfaces", new Path("/Project/src/java/lang/Object.java"), 33, 39, CategorizedProblem.CAT_INTERNAL, IMarker.SEVERITY_ERROR) });
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "p", //$NON-NLS-1$ //$NON-NLS-2$
            "X", "package p; \n" + //$NON-NLS-1$
            "public class X {}\n");
            incrementalBuild(projectPath);
            expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new Problem("", "The type java.lang.Object cannot have a superclass or superinterfaces", new Path("/Project/src/java/lang/Object.java"), 33, 39, CategorizedProblem.CAT_INTERNAL, IMarker.SEVERITY_ERROR) });
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "p", //$NON-NLS-1$ //$NON-NLS-2$
            "Y", "package p; \n" + //$NON-NLS-1$
            "public class Y extends X {}\n");
            incrementalBuild(projectPath);
            expectingOnlySpecificProblemsFor(root, new Problem[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new Problem("", "The type java.lang.Object cannot have a superclass or superinterfaces", new Path("/Project/src/java/lang/Object.java"), 33, 39, CategorizedProblem.CAT_INTERNAL, IMarker.SEVERITY_ERROR) });
            env.removeProject(projectPath);
        } catch (StackOverflowError e) {
            assertTrue("Infinite loop in cycle detection", false);
            e.printStackTrace();
        }
    }

    /**
	 * Bugs 6461
	 * TODO excluded test
	 */
    public void _testWrongCompilationUnitLocation() throws JavaModelException {
        //----------------------------
        //           Step 1
        //----------------------------
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        IPath bin = env.setOutputFolder(projectPath, "bin");
        IPath x = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$
        expectingPresenceOf(bin.append("X.class"));
        //----------------------------
        //           Step 2
        //----------------------------
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild();
        expectingProblemsFor(x, "???");
        //$NON-NLS-1$
        expectingNoPresenceOf(bin.append("X.class"));
        env.removeProject(projectPath);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=100631
    public void testMemberTypeCollisionWithBinary() throws JavaModelException {
        int max = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        try {
            //$NON-NLS-1$
            IPath projectPath = env.addProject("Project");
            env.addExternalJars(projectPath, Util.getJavaClassLibs());
            // remove old package fragment root so that names don't collide
            //$NON-NLS-1$
            env.removePackageFragmentRoot(projectPath, "");
            //$NON-NLS-1$
            IPath root = env.addPackageFragmentRoot(projectPath, "src");
            //$NON-NLS-1$
            env.setOutputFolder(projectPath, "bin");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "", //$NON-NLS-1$ //$NON-NLS-2$
            "A", "public class A {\n" + "	Object foo(B b) { return b.i; }\n" + //$NON-NLS-1$
            "}");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "", //$NON-NLS-1$ //$NON-NLS-2$
            "B", "public class B {\n" + "	I.InnerType i;\n" + //$NON-NLS-1$
            "}");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "", //$NON-NLS-1$ //$NON-NLS-2$
            "I", "public interface I {\n" + "	interface InnerType {}\n" + //$NON-NLS-1$
            "}");
            fullBuild(projectPath);
            expectingNoProblems();
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "", //$NON-NLS-1$ //$NON-NLS-2$
            "A", "public class A {\n" + "	Object foo(B b) { return b.i; }\n" + //$NON-NLS-1$
            "}");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            root, //$NON-NLS-1$ //$NON-NLS-2$
            "", //$NON-NLS-1$ //$NON-NLS-2$
            "I", "public interface I {\n" + "	interface InnerType {}\n" + //$NON-NLS-1$
            "}");
            incrementalBuild(projectPath);
            expectingNoProblems();
            env.removeProject(projectPath);
        } finally {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = max;
        }
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=191739
    public void testMemberTypeCollisionWithBinary2() throws JavaModelException {
        int max = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        try {
            //$NON-NLS-1$
            IPath projectPath = env.addProject("Project");
            env.addExternalJars(projectPath, Util.getJavaClassLibs());
            // remove old package fragment root so that names don't collide
            //$NON-NLS-1$
            env.removePackageFragmentRoot(projectPath, "");
            //$NON-NLS-1$
            IPath src1 = env.addPackageFragmentRoot(projectPath, "src1");
            //$NON-NLS-1$
            IPath bin1 = env.setOutputFolder(projectPath, "bin1");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            src1, //$NON-NLS-1$ //$NON-NLS-2$
            "p1", //$NON-NLS-1$ //$NON-NLS-2$
            "NoSource", "package p1;	\n" + "import p2.Foo;\n" + "public class NoSource {\n" + "	public NoSource(Foo.Bar b) {}\n" + //$NON-NLS-1$
            "}");
            //$NON-NLS-1$ //$NON-NLS-2$
            IPath src2 = env.addPackageFragmentRoot(projectPath, "src2", null, "bin2");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            src2, //$NON-NLS-1$ //$NON-NLS-2$
            "p2", //$NON-NLS-1$ //$NON-NLS-2$
            "Foo", "package p2; \n" + "public class Foo {\n" + "	public static class Bar {\n" + "		public static Bar LocalBar = new Bar();\n" + //$NON-NLS-1$
            "	}\n" + //$NON-NLS-1$
            "}");
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            src2, //$NON-NLS-1$ //$NON-NLS-2$
            "p2", //$NON-NLS-1$ //$NON-NLS-2$
            "Test", "package p2; \n" + "import p1.NoSource;\n" + "import p2.Foo.Bar;\n" + "public class Test {\n" + "	NoSource nosrc = new NoSource(Bar.LocalBar);\n" + //$NON-NLS-1$
            "}");
            fullBuild(projectPath);
            expectingNoProblems();
            //$NON-NLS-1$
            env.removePackageFragmentRoot(projectPath, "src1");
            env.addClassFolder(projectPath, bin1, false);
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
            //$NON-NLS-1$ //$NON-NLS-2$
            env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
            src2, //$NON-NLS-1$ //$NON-NLS-2$
            "p2", //$NON-NLS-1$ //$NON-NLS-2$
            "Test", "package p2; \n" + "import p1.NoSource;\n" + "import p2.Foo.Bar;\n" + "public class Test {\n" + "	NoSource nosrc = new NoSource(Bar.LocalBar);\n" + //$NON-NLS-1$
            "}");
            incrementalBuild(projectPath);
            expectingNoProblems();
            env.removeProject(projectPath);
        } finally {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = max;
        }
    }

    public void test129316() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "");
        IPath yPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", "package p;\n" + //$NON-NLS-1$
        "public class Y extends Z {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", "package p;\n" + //$NON-NLS-1$
        "public class Z {}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "X", "import p.Y;\n" + "public class X {\n" + "	boolean b(Object o) {\n" + "		return o instanceof Y;\n" + "    }\n" + //$NON-NLS-1$
        "}");
        fullBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", "package p;\n" + //$NON-NLS-1$
        "public class Y extends Zork {}");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "Zork cannot be resolved to a type", yPath, 34, 38, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        IPath xPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "X", "public class X {\n" + "	boolean b(Object o) {\n" + "		return o instanceof p.Y;\n" + "    }\n" + //$NON-NLS-1$
        "}");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "Zork cannot be resolved to a type", yPath, 34, 38, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        expectingNoProblemsFor(xPath);
        env.removeProject(projectPath);
    }

    public void testSecondaryType() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "AB", //$NON-NLS-1$
        "public class AB { AZ z = new AA();}");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$ //$NON-NLS-2$
        "AA", //$NON-NLS-1$
        "public class AA extends AZ {} \n" + //$NON-NLS-1$
        "class AZ {}");
        int max = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        try {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = 1;
            fullBuild(projectPath);
        } finally {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = max;
        }
        expectingNoProblems();
        env.removeProject(projectPath);
    }

    // http://dev.eclipse.org/bugs/show_bug.cgi?id=196200 - variation
    public void testMissingType001() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        fullBuild(projectPath);
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        IPath xPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + "	void foo(p2.Y y) {	\n" + "		y.bar(null);" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "	void X() {}\n" + //$NON-NLS-1$
        "}\n");
        IPath yPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "public class Y {\n" + //$NON-NLS-1$
        "	public void bar(Z z) {}\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "Z cannot be resolved to a type", yPath, 46, 47, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "public class Z {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        env.removeProject(projectPath);
    }

    // http://dev.eclipse.org/bugs/show_bug.cgi?id=196200 - variation
    public void testMissingType002() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        fullBuild(projectPath);
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        IPath yPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", "package p2;\n" + "public class Y {\n" + "	public void bar(Z z) {}\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "Z cannot be resolved to a type", yPath, 46, 47, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        IPath xPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + "	void foo(p2.Y y) {	\n" + "		y.bar(null);" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "	void X() {}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "Z cannot be resolved to a type", yPath, 46, 47, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "public class Z {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        env.removeProject(projectPath);
    }

    // http://dev.eclipse.org/bugs/show_bug.cgi?id=196200 - variation
    public void testMissingType003() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        fullBuild(projectPath);
        // remove old package fragment root so that names don't collide
        //$NON-NLS-1$
        env.removePackageFragmentRoot(projectPath, "");
        //$NON-NLS-1$
        IPath root = env.addPackageFragmentRoot(projectPath, "src");
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        IPath yPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", "package p2;\n" + "public class Y {\n" + "	public void bar(p1.Z z) {}\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "p1 cannot be resolved to a type", yPath, 46, 48, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        IPath xPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + "	void foo(p2.Y y) {	\n" + "		y.bar(null);" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "	void X() {}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(yPath, new Problem("Y", "p1.Z cannot be resolved to a type", yPath, 46, 50, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Z {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(xPath, new Problem("X", "This method has a constructor name", xPath, 73, 76, CategorizedProblem.CAT_CODE_STYLE, IMarker.SEVERITY_WARNING));
        env.removeProject(projectPath);
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=334377
    public void testBug334377() throws JavaModelException {
        int max = org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE;
        Hashtable options = null;
        try {
            options = JavaCore.getOptions();
            Hashtable newOptions = JavaCore.getOptions();
            newOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
            JavaCore.setOptions(newOptions);
            //$NON-NLS-1$
            IPath projectPath = env.addProject("Project");
            env.addExternalJars(projectPath, Util.getJavaClassLibs());
            // remove old package fragment root so that names don't collide
            //$NON-NLS-1$
            env.removePackageFragmentRoot(projectPath, "");
            //$NON-NLS-1$
            IPath root = env.addPackageFragmentRoot(projectPath, "src");
            //$NON-NLS-1$
            env.setOutputFolder(projectPath, "bin");
            env.addClass(root, "", "Upper", "public abstract class Upper<T>  {\n" + "    public static enum Mode {IN,OUT;}\n" + "}\n");
            env.addClass(root, "", "Lower", "public class Lower extends Upper<Lower> {};\n");
            env.addClass(root, "", "Bug", "public class Bug {\n" + "    Upper.Mode m1;\n" + "    void usage(){\n" + "        Lower.Mode m3;\n" + "        if (m1 == null){\n" + "            m3 = Lower.Mode.IN;\n" + "        } else {\n" + "            m3 = m1;\n" + "        }\n" + "        Lower.Mode m2 = (m1 == null ?  Lower.Mode.IN : m1);\n" + "        System.out.println(m2);\n" + "        System.out.println(m3);\n" + "    }\n" + "}\n");
            fullBuild(projectPath);
            expectingNoProblems();
            env.addClass(root, "", "Bug", "public class Bug {\n" + "    Upper.Mode m1;\n" + "    void usage(){\n" + "        Lower.Mode m3;\n" + "        if (m1 == null){\n" + "            m3 = Lower.Mode.IN;\n" + "        } else {\n" + "            m3 = m1;\n" + "        }\n" + "        Lower.Mode m2 = (m1 == null ?  Lower.Mode.IN : m1);\n" + "        System.out.println(m2);\n" + "        System.out.println(m3);\n" + "    }\n" + "}\n");
            incrementalBuild(projectPath);
            expectingNoProblems();
            env.removeProject(projectPath);
        } finally {
            org.eclipse.jdt.internal.core.builder.AbstractImageBuilder.MAX_AT_ONCE = max;
            JavaCore.setOptions(options);
        }
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=364450
    // Incremental build should not generate buildpath error
    // NOT generated by full build.
    public void testBug364450() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$
        env.setOutputFolder(projectPath, "bin");
        IPath wPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "w", //$NON-NLS-1$ //$NON-NLS-2$
        "W", "package w;\n" + "public class W {\n" + //$NON-NLS-1$
        "	private w.I i;}");
        IPath aPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "a", //$NON-NLS-1$ //$NON-NLS-2$
        "A", "package a;\n" + "import w.I;\n" + "import w.W;\n" + //$NON-NLS-1$
        "public class A {}");
        env.waitForManualRefresh();
        fullBuild(projectPath);
        env.waitForAutoBuild();
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(wPath, new Problem("W", "w.I cannot be resolved to a type", wPath, 37, 40, CategorizedProblem.CAT_TYPE, IMarker.SEVERITY_ERROR));
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(aPath, new Problem("A", "The import w.I cannot be resolved", aPath, 18, 21, CategorizedProblem.CAT_IMPORT, IMarker.SEVERITY_ERROR));
        aPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "a", //$NON-NLS-1$ //$NON-NLS-2$
        "A", "package a;\n" + "import w.I;\n" + "import w.W;\n" + //$NON-NLS-1$
        "public class A {}");
        env.waitForManualRefresh();
        incrementalBuild(projectPath);
        env.waitForAutoBuild();
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingSpecificProblemFor(aPath, new Problem("A", "The import w.I cannot be resolved", aPath, 18, 21, CategorizedProblem.CAT_IMPORT, IMarker.SEVERITY_ERROR));
        env.removeProject(projectPath);
    }
}
