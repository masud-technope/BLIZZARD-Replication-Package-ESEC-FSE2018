/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.model.tests;

import junit.framework.TestCase;
import org.eclipse.pde.api.tools.internal.ApiDescription;
import org.eclipse.pde.api.tools.internal.CRCVisitor;
import org.eclipse.pde.api.tools.internal.provisional.Factory;
import org.eclipse.pde.api.tools.internal.provisional.RestrictionModifiers;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IFieldDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IMethodDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IReferenceTypeDescriptor;

/**
 * Tests CRC codes for API descriptions
 */
public class CRCTests extends TestCase {

    /**
	 * Test that a CRC code for a type's API description is the same for equivalent descriptions.
	 */
    public void testCRCEqual() {
        //$NON-NLS-1$
        ApiDescription description = new ApiDescription("test.component");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type = Factory.typeDescriptor("org.eclipse.debug.core.SomeClass");
        description.setVisibility(type, VisibilityModifiers.API);
        description.setRestrictions(type, RestrictionModifiers.NO_INSTANTIATE | RestrictionModifiers.NO_EXTEND);
        //$NON-NLS-1$ //$NON-NLS-2$
        IMethodDescriptor method = type.getMethod("someMethod", "(I)V");
        description.setRestrictions(method, RestrictionModifiers.NO_OVERRIDE);
        //$NON-NLS-1$
        IFieldDescriptor field = type.getField("fField");
        description.setRestrictions(field, RestrictionModifiers.NO_REFERENCE);
        CRCVisitor visitor = new CRCVisitor();
        description.accept(visitor, type, null);
        long crc1 = visitor.getValue();
        // test the same in another description with different order
        //$NON-NLS-1$
        ApiDescription description2 = new ApiDescription("test.component");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type2 = Factory.typeDescriptor("org.eclipse.debug.core.SomeClass");
        description2.setVisibility(type2, VisibilityModifiers.API);
        description2.setRestrictions(type2, RestrictionModifiers.NO_INSTANTIATE | RestrictionModifiers.NO_EXTEND);
        //$NON-NLS-1$
        IFieldDescriptor field2 = type2.getField("fField");
        description2.setRestrictions(field2, RestrictionModifiers.NO_REFERENCE);
        //$NON-NLS-1$ //$NON-NLS-2$
        IMethodDescriptor method2 = type2.getMethod("someMethod", "(I)V");
        description2.setRestrictions(method2, RestrictionModifiers.NO_OVERRIDE);
        CRCVisitor visitor2 = new CRCVisitor();
        description2.accept(visitor2, type2, null);
        long crc2 = visitor2.getValue();
        //$NON-NLS-1$
        assertEquals("CRC codes should be the same", crc1, crc2);
    }

    /**
	 * Test that a CRC changes when annotations for a method in a type have changed.
	 */
    public void testCRCModified() {
        //$NON-NLS-1$
        ApiDescription description = new ApiDescription("test.component");
        //$NON-NLS-1$
        IReferenceTypeDescriptor type = Factory.typeDescriptor("org.eclipse.debug.core.SomeClass");
        description.setVisibility(type, VisibilityModifiers.API);
        description.setRestrictions(type, RestrictionModifiers.NO_INSTANTIATE | RestrictionModifiers.NO_EXTEND);
        //$NON-NLS-1$ //$NON-NLS-2$
        IMethodDescriptor method = type.getMethod("someMethod", "(I)V");
        description.setRestrictions(method, RestrictionModifiers.NO_OVERRIDE);
        //$NON-NLS-1$
        IFieldDescriptor field = type.getField("fField");
        description.setRestrictions(field, RestrictionModifiers.NO_REFERENCE);
        CRCVisitor visitor = new CRCVisitor();
        description.accept(visitor, type, null);
        long crc1 = visitor.getValue();
        // modify the annotations, check different
        description.setRestrictions(method, RestrictionModifiers.NO_REFERENCE);
        CRCVisitor visitor2 = new CRCVisitor();
        description.accept(visitor2, type, null);
        long crc2 = visitor2.getValue();
        //$NON-NLS-1$
        assertFalse("CRC codes should be different", crc1 == crc2);
    }
}
