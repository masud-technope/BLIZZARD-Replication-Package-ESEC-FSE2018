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
package org.eclipse.jdt.internal.compiler.lookup;

/**
 * Context used during type inference for a generic method invocation
 */
public class InferenceContext {

    private TypeBinding[][][] collectedSubstitutes;

    MethodBinding genericMethod;

    int depth;

    int status;

    TypeBinding expectedType;

    // indicates whether the expectedType (if set) was explicit in code, or set by default
    boolean hasExplicitExpectedType;

    public boolean isUnchecked;

    TypeBinding[] substitutes;

    static final int FAILED = 1;

    public  InferenceContext(MethodBinding genericMethod) {
        this.genericMethod = genericMethod;
        TypeVariableBinding[] typeVariables = genericMethod.typeVariables;
        int varLength = typeVariables.length;
        this.collectedSubstitutes = new TypeBinding[varLength][3][];
        this.substitutes = new TypeBinding[varLength];
    }

    public TypeBinding[] getSubstitutes(TypeVariableBinding typeVariable, int constraint) {
        return this.collectedSubstitutes[typeVariable.rank][constraint];
    }

    /**
 * Returns true if any unresolved variable is detected, i.e. any variable is substituted with itself
 */
    public boolean hasUnresolvedTypeArgument() {
        for (int i = 0, varLength = this.substitutes.length; i < varLength; i++) {
            if (this.substitutes[i] == null) {
                return true;
            }
        }
        return false;
    }

    public void recordSubstitute(TypeVariableBinding typeVariable, TypeBinding actualType, int constraint) {
        TypeBinding[][] variableSubstitutes = this.collectedSubstitutes[typeVariable.rank];
        insertLoop: {
            TypeBinding[] constraintSubstitutes = variableSubstitutes[constraint];
            int length;
            if (constraintSubstitutes == null) {
                length = 0;
                constraintSubstitutes = new TypeBinding[1];
            } else {
                length = constraintSubstitutes.length;
                for (int i = 0; i < length; i++) {
                    TypeBinding substitute = constraintSubstitutes[i];
                    // already there //$IDENTITY-COMPARISON$
                    if (substitute == actualType)
                        return;
                    if (substitute == null) {
                        constraintSubstitutes[i] = actualType;
                        break insertLoop;
                    }
                }
                // no free spot found, need to grow by one
                System.arraycopy(constraintSubstitutes, 0, constraintSubstitutes = new TypeBinding[length + 1], 0, length);
            }
            constraintSubstitutes[length] = actualType;
            variableSubstitutes[constraint] = constraintSubstitutes;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(20);
        //$NON-NLS-1$
        buffer.append("InferenceContex for ");
        for (int i = 0, length = this.genericMethod.typeVariables.length; i < length; i++) {
            buffer.append(this.genericMethod.typeVariables[i]);
        }
        buffer.append(this.genericMethod);
        //$NON-NLS-1$
        buffer.append("\n\t[status=");
        switch(this.status) {
            case 0:
                //$NON-NLS-1$
                buffer.append("ok]");
                break;
            case FAILED:
                //$NON-NLS-1$
                buffer.append("failed]");
                break;
        }
        if (this.expectedType == null) {
            //$NON-NLS-1$
            buffer.append(" [expectedType=null]");
        } else {
            //$NON-NLS-1$
            buffer.append(" [expectedType=").append(this.expectedType.shortReadableName()).append(']');
        }
        //$NON-NLS-1$
        buffer.append(" [depth=").append(this.depth).append(']');
        //$NON-NLS-1$
        buffer.append("\n\t[collected={");
        for (int i = 0, length = this.collectedSubstitutes == null ? 0 : this.collectedSubstitutes.length; i < length; i++) {
            TypeBinding[][] collected = this.collectedSubstitutes[i];
            for (int j = TypeConstants.CONSTRAINT_EQUAL; j <= TypeConstants.CONSTRAINT_SUPER; j++) {
                TypeBinding[] constraintCollected = collected[j];
                if (constraintCollected != null) {
                    for (int k = 0, clength = constraintCollected.length; k < clength; k++) {
                        buffer.append("\n\t\t").append(//$NON-NLS-1$
                        this.genericMethod.typeVariables[i].sourceName);
                        switch(j) {
                            case TypeConstants.CONSTRAINT_EQUAL:
                                //$NON-NLS-1$
                                buffer.append("=");
                                break;
                            case TypeConstants.CONSTRAINT_EXTENDS:
                                //$NON-NLS-1$
                                buffer.append("<:");
                                break;
                            case TypeConstants.CONSTRAINT_SUPER:
                                //$NON-NLS-1$
                                buffer.append(">:");
                                break;
                        }
                        if (constraintCollected[k] != null) {
                            buffer.append(constraintCollected[k].shortReadableName());
                        }
                    }
                }
            }
        }
        //$NON-NLS-1$
        buffer.append("}]");
        //$NON-NLS-1$
        buffer.append("\n\t[inferred=");
        int count = 0;
        for (int i = 0, length = this.substitutes == null ? 0 : this.substitutes.length; i < length; i++) {
            if (this.substitutes[i] == null)
                continue;
            count++;
            buffer.append('{').append(this.genericMethod.typeVariables[i].sourceName);
            //$NON-NLS-1$
            buffer.append("=").append(this.substitutes[i].shortReadableName()).append('}');
        }
        //$NON-NLS-1$
        if (count == 0)
            buffer.append("{}");
        buffer.append(']');
        return buffer.toString();
    }
}
