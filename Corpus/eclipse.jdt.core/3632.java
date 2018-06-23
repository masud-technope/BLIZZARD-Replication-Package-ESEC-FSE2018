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

import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class LocalVariableBinding extends VariableBinding {

    public boolean isArgument;

    // for code generation (position in method context)
    public int resolvedPosition;

    public static final int UNUSED = 0;

    public static final int USED = 1;

    public static final int FAKE_USED = 2;

    // for flow analysis (default is UNUSED)
    public int useFlag;

    // back-pointer to its declaring scope
    public BlockScope declaringScope;

    // for source-positions
    public LocalDeclaration declaration;

    public int[] initializationPCs;

    public int initializationCount = 0;

    // note that the name of a variable should be chosen so as not to conflict with user ones (usually starting with a space char is all needed)
    public  LocalVariableBinding(char[] name, TypeBinding type, int modifiers, boolean isArgument) {
        super(name, type, modifiers, isArgument ? Constant.NotAConstant : null);
        this.isArgument = isArgument;
    }

    // regular local variable or argument
    public  LocalVariableBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, boolean isArgument) {
        this(declaration.name, type, modifiers, isArgument);
        this.declaration = declaration;
    }

    /* API
	* Answer the receiver's binding type from Binding.BindingID.
	*/
    public final int bindingType() {
        return LOCAL;
    }

    // Answer whether the variable binding is a secret variable added for code gen purposes
    public boolean isSecret() {
        return declaration == null && !isArgument;
    }

    public void recordInitializationEndPC(int pc) {
        if (initializationPCs[((initializationCount - 1) << 1) + 1] == -1)
            initializationPCs[((initializationCount - 1) << 1) + 1] = pc;
    }

    public void recordInitializationStartPC(int pc) {
        if (initializationPCs == null)
            return;
        // optimize cases where reopening a contiguous interval
        if ((initializationCount > 0) && (initializationPCs[((initializationCount - 1) << 1) + 1] == pc)) {
            // reuse previous interval (its range will be augmented)
            initializationPCs[((initializationCount - 1) << 1) + 1] = -1;
        } else {
            int index = initializationCount << 1;
            if (index == initializationPCs.length) {
                System.arraycopy(initializationPCs, 0, (initializationPCs = new int[initializationCount << 2]), 0, index);
            }
            initializationPCs[index] = pc;
            initializationPCs[index + 1] = -1;
            initializationCount++;
        }
    }

    public String toString() {
        String s = super.toString();
        switch(useFlag) {
            case USED:
                //$NON-NLS-2$ //$NON-NLS-1$
                s += "[pos: " + String.valueOf(resolvedPosition) + "]";
                break;
            case UNUSED:
                s += "[pos: unused]";
                break;
            case FAKE_USED:
                s += "[pos: fake_used]";
                break;
        }
        //$NON-NLS-2$ //$NON-NLS-1$
        s += "[id:" + String.valueOf(id) + "]";
        if (initializationCount > 0) {
            //$NON-NLS-1$
            s += "[pc: ";
            for (int i = 0; i < initializationCount; i++) {
                if (i > 0)
                    //$NON-NLS-1$
                    s += ", ";
                //$NON-NLS-2$ //$NON-NLS-1$
                s += String.valueOf(initializationPCs[i << 1]) + "-" + ((initializationPCs[(i << 1) + 1] == -1) ? "?" : String.valueOf(initializationPCs[(i << 1) + 1]));
            }
            //$NON-NLS-1$
            s += "]";
        }
        return s;
    }
}
