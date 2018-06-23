/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types;

public final class SuperWildcardType extends WildcardType {

    protected  SuperWildcardType(TypeEnvironment environment) {
        super(environment);
    }

    @Override
    public TType getErasure() {
        return getEnvironment().getJavaLangObject();
    }

    @Override
    public int getKind() {
        return SUPER_WILDCARD_TYPE;
    }

    @Override
    protected boolean doCanAssignTo(TType lhs) {
        switch(lhs.getKind()) {
            case STANDARD_TYPE:
                return ((StandardType) lhs).isJavaLangObject();
            case UNBOUND_WILDCARD_TYPE:
                return true;
            case EXTENDS_WILDCARD_TYPE:
                return ((ExtendsWildcardType) lhs).getBound().isJavaLangObject();
            case SUPER_WILDCARD_TYPE:
                return ((SuperWildcardType) lhs).getBound().canAssignTo(this.getBound());
            case TYPE_VARIABLE:
                return ((TypeVariable) lhs).isUnbounded();
            case CAPTURE_TYPE:
                return ((CaptureType) lhs).checkLowerBound(this);
            default:
                return false;
        }
    }

    @Override
    protected boolean checkTypeArgument(TType rhs) {
        switch(rhs.getKind()) {
            case ARRAY_TYPE:
            case STANDARD_TYPE:
            case PARAMETERIZED_TYPE:
            case RAW_TYPE:
                return getBound().canAssignTo(rhs);
            case UNBOUND_WILDCARD_TYPE:
                return false;
            case EXTENDS_WILDCARD_TYPE:
                return false;
            case SUPER_WILDCARD_TYPE:
                return getBound().canAssignTo(((SuperWildcardType) rhs).getBound());
            case TYPE_VARIABLE:
                return getBound().canAssignTo(rhs);
            case CAPTURE_TYPE:
                return checkTypeArgument(((CaptureType) rhs).getWildcard());
            default:
                return false;
        }
    }

    @Override
    protected boolean checkAssignmentBound(TType rhs) {
        // Number.
        return rhs.canAssignTo(getBound());
    }

    @Override
    public String getName() {
        //$NON-NLS-1$
        return internalGetName("super");
    }

    @Override
    protected String getPlainPrettySignature() {
        //$NON-NLS-1$
        return internalGetPrettySignature("super");
    }
}
