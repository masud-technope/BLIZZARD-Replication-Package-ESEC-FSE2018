/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
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
 * Basic efficiency tests of the image builder.
 */
public class EfficiencyTests extends BuilderTests {

    public  EfficiencyTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildTestSuite(EfficiencyTests.class);
    }

    public void testProjectAsClassFolder() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath1 = env.addProject("Project1");
        env.addExternalJars(projectPath1, Util.getJavaClassLibs());
        //$NON-NLS-1$
        IPath projectPath2 = env.addProject("Project2");
        env.addExternalJars(projectPath2, Util.getJavaClassLibs());
        env.addClassFolder(projectPath2, projectPath1, false);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath2, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public abstract class X {}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath2, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "public class Y {}\n");
        fullBuild();
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath2, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {}\n");
        incrementalBuild(projectPath2);
        // if a full build happens instead of an incremental, then both types will be recompiled
        //$NON-NLS-1$
        expectingCompiledClasses(new String[] { "p1.X" });
    }

    public void testEfficiency() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Indicted", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public abstract class Indicted {\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Collaborator", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Collaborator extends Indicted{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Indicted", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public abstract class Indicted {\n" + //$NON-NLS-1$
        "   public abstract void foo();\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompiledClasses(new String[] { "p2.Collaborator", "p1.Indicted" });
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompilingOrder(new String[] { "p1.Indicted", "p2.Collaborator" });
    }

    public void testMethodAddition() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Y extends X{\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p3", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Z{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void bar(){}	\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompiledClasses(new String[] { "p1.X", "p2.Y" });
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompilingOrder(new String[] { "p1.X", "p2.Y" });
    }

    public void testLocalTypeAddition() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Y extends X{\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p3", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Z{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new Object(){	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompiledClasses(new String[] { "p1.X", "p1.X$1" });
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompilingOrder(new String[] { "p1.X", "p1.X$1" });
    }

    public void testLocalTypeAddition2() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new X(){	\n" + "			void bar(){}	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Y extends X{\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p3", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Z{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new Object(){	\n" + "		};	\n" + "		new X(){	\n" + "			void bar(){}	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        expectingCompiledClasses(new String[] { "p1.X", "p1.X$1", "p1.X$2" });
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        expectingCompilingOrder(new String[] { "p1.X", "p1.X$1", "p1.X$2" });
    }

    public void testLocalTypeRemoval() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new Object(){	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Y extends X{\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p3", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Z{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$
        expectingCompiledClasses(new String[] { "p1.X" });
        //$NON-NLS-1$
        expectingCompilingOrder(new String[] { "p1.X" });
    }

    public void testLocalTypeRemoval2() throws JavaModelException {
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new Object(){	\n" + "		};	\n" + "		new X(){	\n" + "			void bar(){}	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Y", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Y extends X{\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p3", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "import p1.*;\n" + //$NON-NLS-1$
        "public class Z{\n" + //$NON-NLS-1$
        "}\n");
        fullBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + //$NON-NLS-1$
        "	void foo() {	\n" + "		new X(){	\n" + "			void bar(){}	\n" + "		};	\n" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompiledClasses(new String[] { "p1.X", "p1.X$1" });
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompilingOrder(new String[] { "p1.X", "p1.X$1" });
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
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "X", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class X {\n" + "	void foo(p2.Y y) {	\n" + "		y.bar(null);" + //$NON-NLS-1$
        "	}\n" + //$NON-NLS-1$
        "}\n");
        //$NON-NLS-1$ //$NON-NLS-2$
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
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p2", //$NON-NLS-1$ //$NON-NLS-2$
        "Z", //$NON-NLS-1$
        "package p2;\n" + //$NON-NLS-1$
        "public class Z {\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompiledClasses(new String[] { "p1.X", "p2.Y", "p2.Z" });
        //$NON-NLS-1$ //$NON-NLS-2$
        expectingCompilingOrder(new String[] { "p2.Z", "p2.Y", "p1.X" });
    }
}
