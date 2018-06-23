/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.text.tests.performance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import org.eclipse.test.performance.PerformanceMeter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
* @since 3.3
*/
public class PerfTestSuite extends TestSuite {

    public  PerfTestSuite() {
        super();
    }

    public  PerfTestSuite(Class<?> theClass, String name) {
        super(name);
        try {
            // Avoid generating multiple error messages
            getTestConstructor(theClass);
        } catch (NoSuchMethodException e) {
            addTest(warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()"));
            return;
        }
        if (!Modifier.isPublic(theClass.getModifiers())) {
            //$NON-NLS-1$//$NON-NLS-2$
            addTest(warning("Class " + theClass.getName() + " is not public"));
            return;
        }
        Class<?> superClass = theClass;
        Vector<String> names = new Vector();
        while (Test.class.isAssignableFrom(superClass)) {
            Method[] methods = superClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                addPerformanceTestMethod(methods[i], names, theClass);
            }
            superClass = superClass.getSuperclass();
        }
    }

    private void addPerformanceTestMethod(Method m, Vector<String> names, Class<?> theClass) {
        String name = m.getName();
        if (names.contains(name))
            return;
        if (!isPublicMeasureMethod(m)) {
            if (isMeasureMethod(m))
                addTest(//$NON-NLS-1$
                warning("Test method isn't public: " + m.getName()));
            return;
        }
        names.addElement(name);
        addTest(createPerformanceTest(theClass, name));
    }

    private boolean isPublicMeasureMethod(Method m) {
        return isMeasureMethod(m) && Modifier.isPublic(m.getModifiers());
    }

    private boolean isMeasureMethod(Method m) {
        String name = m.getName();
        Class<?>[] parameters = m.getParameterTypes();
        Class<?> returnType = m.getReturnType();
        //$NON-NLS-1$
        return parameters.length == 1 && name.startsWith("measure") && returnType.equals(Void.TYPE) && PerformanceMeter.class.isAssignableFrom(parameters[0]);
    }

    public  PerfTestSuite(Class<?> theClass) {
        this(theClass, theClass.getName());
    }

    public  PerfTestSuite(String name) {
        super(name);
    }

    /**
	 * ... buried deep in the mountains in a lonely camp of the Swiss army...
	 *
	 * @param theClass the class to be scanned for <code>measure</code> methods
	 * @param name hte name of the test
	 * @return a new performance test
	 */
    public static Test createPerformanceTest(Class<?> theClass, String name) {
        Constructor<?> constructor;
        try {
            constructor = getTestConstructor(theClass);
        } catch (NoSuchMethodException e) {
            return warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()");
        }
        Object test;
        try {
            if (constructor.getParameterTypes().length == 0) {
                test = constructor.newInstance(new Object[0]);
                if (test instanceof TestCase)
                    ((TestCase) test).setName(name);
            } else {
                test = constructor.newInstance(new Object[] { name });
            }
        } catch (InstantiationException e) {
            return (warning("Cannot instantiate test case: " + name + " (" + exceptionToString(e) + ")"));
        } catch (InvocationTargetException e) {
            return (warning("Exception in constructor: " + name + " (" + exceptionToString(e.getTargetException()) + ")"));
        } catch (IllegalAccessException e) {
            return (warning("Cannot access test case: " + name + " (" + exceptionToString(e) + ")"));
        }
        return (Test) test;
    }

    private static String exceptionToString(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        return stringWriter.toString();
    }

    public static Test warning(final String message) {
        return new //$NON-NLS-1$
        TestCase(//$NON-NLS-1$
        "warning") {

            @Override
            protected void runTest() {
                fail(message);
            }
        };
    }
}
