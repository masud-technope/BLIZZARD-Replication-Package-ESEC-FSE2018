/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.*;

public abstract class NameReference extends Reference implements InvocationSite, BindingIds {

    //may be aTypeBinding-aFieldBinding-aLocalVariableBinding
    public Binding binding, codegenBinding;

    // raw receiver type
    public TypeBinding receiverType;

    // modified receiver type - actual one according to namelookup
    public TypeBinding actualReceiverType;

    //no changeClass in java.
    public  NameReference() {
        super();
        // restrictiveFlag
        bits |= TYPE | VARIABLE;
    }

    public FieldBinding fieldBinding() {
        return (FieldBinding) binding;
    }

    public boolean isSuperAccess() {
        return false;
    }

    public boolean isTypeAccess() {
        // null is acceptable when we are resolving the first part of a reference
        return binding == null || binding instanceof ReferenceBinding;
    }

    public boolean isTypeReference() {
        return binding instanceof ReferenceBinding;
    }

    public void setActualReceiverType(ReferenceBinding receiverType) {
        this.actualReceiverType = receiverType;
    }

    public void setDepth(int depth) {
        // flush previous depth if any			
        bits &= ~DepthMASK;
        if (depth > 0) {
            // encoded on 8 bits
            bits |= (depth & 0xFF) << DepthSHIFT;
        }
    }

    public void setFieldIndex(int index) {
    // ignored
    }

    public abstract String unboundReferenceErrorName();
}
