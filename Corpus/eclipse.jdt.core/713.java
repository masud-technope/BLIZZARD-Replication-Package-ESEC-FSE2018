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

public class StaticFinalTests extends BuilderTests {

    public  StaticFinalTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildTestSuite(StaticFinalTests.class);
    }

    public void testBoolean() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final boolean VAR = true; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "true", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final boolean VAR = false; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "false", "");
    }

    public void testByte() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final byte VAR = (byte) 0; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "0", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final byte VAR = (byte) 1; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "1", "");
    }

    public void testChar() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final String VAR = \"Hello\"; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "Hello", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final String VAR = \"Bye\"; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "Bye", "");
    }

    public void testDouble() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final double VAR = (double) 2; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "2", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final double VAR = (double) 3; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "3", "");
    }

    public void testFloat() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final float VAR = (float) 4; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "4", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final float VAR = (float) 5; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "5", "");
    }

    public void testInt() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final int VAR = (int) 6; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "6", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final int VAR = (int) 7; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "7", "");
    }

    public void testLong() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final long VAR = (long) 8; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "8", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final long VAR = (long) 9; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "9", "");
    }

    public void testShort() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final short VAR = (short) 10; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "10", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final short VAR = (short) 11; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "11", "");
    }

    public void testString() throws JavaModelException {
        //$NON-NLS-1$
        IPath projectPath = env.addProject("Project");
        env.addExternalJars(projectPath, Util.getJavaClassLibs());
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final String VAR = \"Hello\"; }");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "Main", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class Main {\n" + //$NON-NLS-1$
        "   public static void main(String args[]) {\n" + //$NON-NLS-1$
        "      System.out.println(A.VAR);\n" + //$NON-NLS-1$
        "   }\n" + //$NON-NLS-1$
        "}\n");
        fullBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "Hello", "");
        //$NON-NLS-1$ //$NON-NLS-2$
        env.addClass(//$NON-NLS-1$ //$NON-NLS-2$
        projectPath, //$NON-NLS-1$ //$NON-NLS-2$
        "p1", //$NON-NLS-1$ //$NON-NLS-2$
        "A", //$NON-NLS-1$
        "package p1;\n" + //$NON-NLS-1$
        "public class A { public static final String VAR = \"Bye\"; }");
        incrementalBuild();
        expectingNoProblems();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        executeClass(projectPath, "p1.Main", "Bye", "");
    }
}
