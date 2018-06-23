/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.model.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.core.Signature;
import org.eclipse.pde.api.tools.internal.provisional.Factory;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IComponentDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IFieldDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IMethodDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IPackageDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IReferenceTypeDescriptor;

/**
 * Tests for element descriptors.
 *
 * @since 1.0.0
 */
public class ElementDescriptorTests extends TestCase {

    public static Test suite() {
        return new TestSuite(ElementDescriptorTests.class);
    }

    public  ElementDescriptorTests() {
        super();
    }

    public  ElementDescriptorTests(String name) {
        super(name);
    }

    /**
	 * Tests equality of default package
	 */
    public void testDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        assertEquals("Default packages should be equal", pkg1, pkg2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", "<default package>", String.valueOf(pkg1));
    }

    /**
	 * Tests non-equality of different packages
	 */
    public void testPackageNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("d.e.f");
        //$NON-NLS-1$
        assertFalse("packages should be equal", pkg1.equals(pkg2));
    }

    /**
	 * Tests equality of non-default package
	 */
    public void testNonDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        assertEquals("a.b.c packages should be equal", pkg1, pkg2);
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("wrong value", "a.b.c", String.valueOf(pkg1));
    }

    /**
	 * Tests equality of types in the default package
	 */
    public void testTypeDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        assertEquals("Types in default package should be equal", type1, type2);
    }

    /**
	 * Tests equality of inner types in the default package
	 */
    public void testInnerTypeDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner1 = type1.getType("B");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner2 = type2.getType("B");
        //$NON-NLS-1$
        assertEquals("Types in default package should be equal", inner1, inner2);
    }

    /**
	 * Tests equality of inner types in non-default package
	 */
    public void testInnerTypeNonDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner1 = type1.getType("B");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner2 = type2.getType("B");
        //$NON-NLS-1$
        assertEquals("Types in default package should be equal", inner1, inner2);
    }

    /**
	 * Tests package retrieval
	 */
    public void testInnerTypePackage() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner1 = type1.getType("B");
        //$NON-NLS-1$
        assertEquals("Wrong package", pkg1, inner1.getPackage());
    }

    /**
	 * Tests non-equality of inner types in non-default package
	 */
    public void testInnerTypeNonDefaultPackageNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("d.e.f");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner1 = type1.getType("B");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner2 = type2.getType("B");
        //$NON-NLS-1$
        assertFalse("Types in different package should not be equal", inner1.equals(inner2));
    }

    /**
	 * Tests equality of inner types in non-default package
	 */
    public void testDeepInnerTypeNonDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor i1 = type1.getType("B");
        //$NON-NLS-1$
        IReferenceTypeDescriptor i2 = type2.getType("B");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner1 = i1.getType("C");
        //$NON-NLS-1$
        IReferenceTypeDescriptor inner2 = i2.getType("C");
        //$NON-NLS-1$
        assertEquals("Types in default package should be equal", inner1, inner2);
    }

    /**
	 * Tests non-equality of different types
	 */
    public void testTypeNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("d.e.f");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        assertFalse("Types in different package should not be equal", type1.equals(type2));
    }

    /**
	 * Tests equality of types in non-default package
	 */
    public void testTypeNonDefaultPackageEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        assertEquals("Types in default package should be equal", type1, type2);
    }

    /**
	 * Tests package retrieval
	 */
    public void testTypePackage() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        assertEquals("Wrong package", pkg1, type1.getPackage());
    }

    /**
	 * Tests equality of fields
	 */
    public void testFieldEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IFieldDescriptor field1 = type1.getField("name");
        //$NON-NLS-1$
        IFieldDescriptor field2 = type2.getField("name");
        //$NON-NLS-1$
        assertEquals("Fields should be equal", field1, field2);
    }

    /**
	 * Tests package retrieval
	 */
    public void testFieldPackage() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IFieldDescriptor field1 = type1.getField("name");
        //$NON-NLS-1$
        assertEquals("Wrong package", pkg1, field1.getPackage());
    }

    /**
	 * Tests non-equality of fields
	 */
    public void testFieldNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$
        IFieldDescriptor field1 = type1.getField("name");
        //$NON-NLS-1$
        IFieldDescriptor field2 = type2.getField("age");
        //$NON-NLS-1$
        assertFalse("Fields should not be equal", field1.equals(field2));
    }

    /**
	 * Tests equality of methods without parameters
	 */
    public void testMethodNoParamsEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        //$NON-NLS-1$ //$NON-NLS-2$
        IMethodDescriptor m1 = type1.getMethod("foo", "()V");
        //$NON-NLS-1$ //$NON-NLS-2$
        IMethodDescriptor m2 = type2.getMethod("foo", "()V");
        //$NON-NLS-1$
        assertEquals("Methods should be equal", m1, m2);
    }

    /**
	 * Tests equality of methods with parameters
	 */
    public void testMethodParamsEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        IMethodDescriptor m1 = type1.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT, type1.getSignature() }, //$NON-NLS-1$
        "V"));
        IMethodDescriptor m2 = type2.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT, type2.getSignature() }, //$NON-NLS-1$
        "V"));
        //$NON-NLS-1$
        assertEquals("Methods should be equal", m1, m2);
    }

    /**
	 * Tests non-equality of methods with parameters= types
	 */
    public void testMethodParamsNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        IMethodDescriptor m1 = type1.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT, type1.getSignature() }, //$NON-NLS-1$
        "V"));
        IMethodDescriptor m2 = type2.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT, Signature.SIG_BOOLEAN }, //$NON-NLS-1$
        "V"));
        //$NON-NLS-1$
        assertFalse("Methods should not be equal", m1.equals(m2));
    }

    /**
	 * Tests non-equality of methods with different number of parameters
	 */
    public void testMethodDiffParamsNonEq() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IPackageDescriptor pkg2 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = pkg2.getType("A");
        IMethodDescriptor m1 = type1.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT }, //$NON-NLS-1$
        "V"));
        IMethodDescriptor m2 = type2.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT, Signature.SIG_BOOLEAN }, //$NON-NLS-1$
        "V"));
        //$NON-NLS-1$
        assertFalse("Methods should not be equal", m1.equals(m2));
    }

    /**
	 * Tests package retrieval
	 */
    public void testMethodPackage() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("a.b.c");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("A");
        IMethodDescriptor m1 = type1.getMethod("foo", //$NON-NLS-1$
        Signature.createMethodSignature(new String[] { Signature.SIG_INT }, //$NON-NLS-1$
        "V"));
        //$NON-NLS-1$
        assertEquals("Wrong package", pkg1, m1.getPackage());
    }

    /**
	 * Tests reference type signature generation
	 */
    public void testTypeSignature() {
        //$NON-NLS-1$
        IPackageDescriptor pkg1 = Factory.packageDescriptor("java.lang");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type1 = pkg1.getType("Object");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong signature", "Ljava.lang.Object;", type1.getSignature());
    }

    public void testComponent() {
        //$NON-NLS-1$
        IComponentDescriptor descriptor = Factory.componentDescriptor("com.mycomponent");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong id", "com.mycomponent", descriptor.getId());
        //$NON-NLS-1$
        assertNull("Wrong value", descriptor.getPath());
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong id", "com.mycomponent", descriptor.toString());
        //$NON-NLS-1$
        assertEquals("Wrong element type", IElementDescriptor.COMPONENT, descriptor.getElementType());
    }

    public void testComponentVersion() {
        //$NON-NLS-1$ //$NON-NLS-2$
        IComponentDescriptor descriptor = Factory.componentDescriptor("com.mycomponent", "1.2.3");
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Wrong version", "1.2.3", descriptor.getVersion());
    }

    public void testComponentsEqual() {
        //$NON-NLS-1$ //$NON-NLS-2$
        IComponentDescriptor descriptor = Factory.componentDescriptor("com.mycomponent", "1.2.3");
        //$NON-NLS-1$ //$NON-NLS-2$
        IComponentDescriptor descriptor2 = Factory.componentDescriptor("com.mycomponent", "1.2.3");
        assertEquals(descriptor, descriptor2);
    }

    public void testComponentsNotEqual() {
        //$NON-NLS-1$ //$NON-NLS-2$
        IComponentDescriptor descriptor = Factory.componentDescriptor("com.mycomponent", "1.2.3");
        //$NON-NLS-1$ //$NON-NLS-2$
        IComponentDescriptor descriptor2 = Factory.componentDescriptor("com.mycomponent", "2.2.3");
        assertFalse(descriptor.equals(descriptor2));
    }
}
