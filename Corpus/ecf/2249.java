/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.sharedobject.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.ecf.core.util.reflection.ClassUtil;

public class ClassUtilTest extends TestCase {

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetPrimitiveMethodWithPrimitive() {
        testGetMethod(new Class[] { int.class }, new Class[] { int.class }, new Object[] { new Integer(1) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetPrimitiveMethodWithObject() {
        testGetMethod(new Class[] { Integer.class }, new Class[] { int.class }, new Object[] { new Integer(1) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetObjectMethodWithObject() {
        testGetMethod(new Class[] { Long.class }, new Class[] { Long.class }, new Object[] { new Long(1L) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetObjectMethodWithPrimitive() {
        testGetMethod(new Class[] { long.class }, new Class[] { Long.class }, new Object[] { new Long(1L) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetObjectMethodWhenBoth() {
        testGetMethod(new Class[] { Boolean.class }, new Class[] { Boolean.class }, new Object[] { new Boolean(true) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetPrimitiveMethodWhenBoth() {
        testGetMethod(new Class[] { boolean.class }, new Class[] { boolean.class }, new Object[] { new Boolean(true) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetMethodWithoutParams() {
        testGetMethod(new Class[] {}, new Class[] {}, null);
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetObjectMethodFromSuperclassWithPrimitive() {
        testGetMethod(new Class[] { float.class }, new Class[] { Float.class }, new Object[] { new Float(1.0) });
    }

    /**
	 * Test method for {@link org.eclipse.ecf.core.util.reflection.ClassUtil#getMethod(java.lang.Class, java.lang.String, java.lang.Class[])}.
	 */
    public void testGetPrimitiveMethodFromSuperclassWithObject() {
        testGetMethod(new Class[] { Float.class }, new Class[] { Float.class }, new Object[] { new Float(1.0) });
    }

    // helper
    private void testGetMethod(Class[] searchParameterTypes, Class[] expectedParameterTypes, Object[] params) {
        Method method = null;
        try {
            method = ClassUtil.getMethod(TestClass.class, "foo", searchParameterTypes);
        } catch (NoSuchMethodException e) {
            fail("failed to match expected the method: " + e.getMessage());
        }
        final Class[] someParameterTypes = method.getParameterTypes();
        assertTrue("Parameters don't match", Arrays.equals(expectedParameterTypes, someParameterTypes));
        try {
            assertNotNull("executed method from superclass", method.invoke(new TestClass(), params));
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    // helper class 
    class TestClass extends AbstractTestClass {

        public String foo() {
            return "";
        }

        public String foo(final int i) {
            return "";
        }

        public String foo(final Long i) {
            return "";
        }

        public String foo(final boolean b) {
            return "";
        }

        public String foo(final Boolean b) {
            return "";
        }
    }

    abstract class AbstractTestClass {

        public String foo(final Float f) {
            return "";
        }

        public String foo() {
            throw new UnsupportedOperationException();
        }

        public String foo(final int i) {
            throw new UnsupportedOperationException();
        }

        public String foo(final Long i) {
            throw new UnsupportedOperationException();
        }

        public String foo(final boolean b) {
            throw new UnsupportedOperationException();
        }

        ;

        public String foo(final Boolean b) {
            throw new UnsupportedOperationException();
        }

        ;
    }
}
