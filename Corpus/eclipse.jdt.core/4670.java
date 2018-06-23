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
 * Basic execution tests of the image builder.
 */
public class ExecutionTests extends BuilderTests {

    public  ExecutionTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildTestSuite(ExecutionTests.class);
    }

    public void testSuccess() throws JavaModelException {
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
        "Hello", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Hello {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.print(\"Hello world\");\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        incrementalBuild(projectPath);
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Hello", "Hello world", "");
    }

    public void testFailure() throws JavaModelException {
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
        IPath helloPath = //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        root, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Hello", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Hello {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(\"Hello world\")\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        // public static void main(String args[]) {
        //    System.out.println("Hello world") <-- missing ";"
        // }
        incrementalBuild(projectPath);
        expectingOnlyProblemsFor(helloPath);
        executeClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1.Hello", //$NON-NLS-1$ //$NON-NLS-2$
        "", //$NON-NLS-1$
        "java.lang.Error: Unresolved compilation problem: \n" + "	Syntax error, insert \";\" to complete BlockStatements\n");
    }
}
