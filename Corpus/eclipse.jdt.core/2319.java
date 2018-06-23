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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class SyntheticArgumentBinding extends LocalVariableBinding {

    {
        this.isArgument = true;
        this.useFlag = USED;
    }

    // if the argument is mapping to an outer local variable, this denotes the outer actual variable
    public LocalVariableBinding actualOuterLocalVariable;

    // if the argument has a matching synthetic field
    public FieldBinding matchingField;

    static final char[] OuterLocalPrefix = { 'v', 'a', 'l', '$' };

    static final char[] EnclosingInstancePrefix = { 't', 'h', 'i', 's', '$' };

    public  SyntheticArgumentBinding(LocalVariableBinding actualOuterLocalVariable) {
        super(CharOperation.concat(OuterLocalPrefix, actualOuterLocalVariable.name), actualOuterLocalVariable.type, AccFinal, true);
        this.actualOuterLocalVariable = actualOuterLocalVariable;
    }

    public  SyntheticArgumentBinding(ReferenceBinding enclosingType) {
        super(CharOperation.concat(SyntheticArgumentBinding.EnclosingInstancePrefix, String.valueOf(enclosingType.depth()).toCharArray()), enclosingType, AccFinal, true);
    }
}
