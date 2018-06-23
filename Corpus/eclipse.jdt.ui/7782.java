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

public final class NullType extends TType {

    protected  NullType(TypeEnvironment environment) {
        //$NON-NLS-1$
        super(environment, "N");
    }

    @Override
    public int getKind() {
        return NULL_TYPE;
    }

    @Override
    public TType[] getSubTypes() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doEquals(TType type) {
        return true;
    }

    @Override
    public int hashCode() {
        return 1234;
    }

    @Override
    public String getName() {
        //$NON-NLS-1$
        return "null";
    }

    @Override
    protected String getPlainPrettySignature() {
        return getName();
    }

    @Override
    protected boolean doCanAssignTo(TType lhs) {
        int kind = lhs.getKind();
        return kind != PRIMITIVE_TYPE && kind != VOID_TYPE;
    }
}
