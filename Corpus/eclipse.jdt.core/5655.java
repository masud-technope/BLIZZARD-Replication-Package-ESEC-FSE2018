/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.apt.core.internal.type;

import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.util.TypeVisitor;
import org.eclipse.jdt.apt.core.internal.declaration.EclipseMirrorType;
import org.eclipse.jdt.apt.core.internal.env.BaseProcessorEnv;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class PrimitiveTypeImpl implements PrimitiveType, EclipseMirrorType {

    private final ITypeBinding _binding;

    public  PrimitiveTypeImpl(ITypeBinding binding) {
        assert binding != null;
        _binding = binding;
    }

    public void accept(TypeVisitor visitor) {
        visitor.visitPrimitiveType(this);
    }

    public PrimitiveType.Kind getKind() {
        final String name = getTypeBinding().getName();
        if (//$NON-NLS-1$
        "int".equals(name))
            return PrimitiveType.Kind.INT;
        else if (//$NON-NLS-1$
        "byte".equals(name))
            return PrimitiveType.Kind.BYTE;
        else if (//$NON-NLS-1$
        "short".equals(name))
            return PrimitiveType.Kind.SHORT;
        else if (//$NON-NLS-1$
        "char".equals(name))
            return PrimitiveType.Kind.CHAR;
        else if (//$NON-NLS-1$
        "long".equals(name))
            return PrimitiveType.Kind.LONG;
        else if (//$NON-NLS-1$
        "float".equals(name))
            return PrimitiveType.Kind.FLOAT;
        else if (//$NON-NLS-1$
        "double".equals(name))
            return PrimitiveType.Kind.DOUBLE;
        else if (//$NON-NLS-1$
        "boolean".equals(name))
            return PrimitiveType.Kind.BOOLEAN;
        else
            //$NON-NLS-1$
            throw new IllegalStateException("unrecognized primitive type " + _binding);
    }

    public String toString() {
        return _binding.getName();
    }

    public ITypeBinding getTypeBinding() {
        return _binding;
    }

    public MirrorKind kind() {
        return MirrorKind.TYPE_PRIMITIVE;
    }

    public boolean equals(final Object obj) {
        try {
            return this._binding.isEqualTo(((PrimitiveTypeImpl) obj)._binding);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public BaseProcessorEnv getEnvironment() {
        return null;
    }

    public boolean isAssignmentCompatible(EclipseMirrorType left) {
        return getTypeBinding().isAssignmentCompatible(left.getTypeBinding());
    }

    public boolean isSubTypeCompatible(EclipseMirrorType type) {
        return getTypeBinding().isSubTypeCompatible(type.getTypeBinding());
    }
}
