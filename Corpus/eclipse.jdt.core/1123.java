/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class CompletionOnSingleTypeReference extends SingleTypeReference {

    public static final int K_TYPE = 0;

    public static final int K_CLASS = 1;

    public static final int K_INTERFACE = 2;

    public static final int K_EXCEPTION = 3;

    private int kind = K_TYPE;

    public boolean isCompletionNode;

    public boolean isConstructorType;

    public CompletionOnFieldType fieldTypeCompletionNode;

    public  CompletionOnSingleTypeReference(char[] source, long pos) {
        this(source, pos, K_TYPE);
    }

    public  CompletionOnSingleTypeReference(char[] source, long pos, int kind) {
        super(source, pos);
        this.isCompletionNode = true;
        this.kind = kind;
    }

    public void aboutToResolve(Scope scope) {
        getTypeBinding(scope);
    }

    /*
 * No expansion of the completion reference into an array one
 */
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        return this;
    }

    protected TypeBinding getTypeBinding(Scope scope) {
        if (this.fieldTypeCompletionNode != null) {
            throw new CompletionNodeFound(this.fieldTypeCompletionNode, scope);
        }
        if (this.isCompletionNode) {
            throw new CompletionNodeFound(this, scope);
        } else {
            return super.getTypeBinding(scope);
        }
    }

    public boolean isClass() {
        return this.kind == K_CLASS;
    }

    public boolean isInterface() {
        return this.kind == K_INTERFACE;
    }

    public boolean isException() {
        return this.kind == K_EXCEPTION;
    }

    public boolean isSuperType() {
        return this.kind == K_CLASS || this.kind == K_INTERFACE;
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        switch(this.kind) {
            case K_CLASS:
                //$NON-NLS-1$
                output.append("<CompleteOnClass:");
                break;
            case K_INTERFACE:
                //$NON-NLS-1$
                output.append("<CompleteOnInterface:");
                break;
            case K_EXCEPTION:
                //$NON-NLS-1$
                output.append("<CompleteOnException:");
                break;
            default:
                //$NON-NLS-1$
                output.append("<CompleteOnType:");
                break;
        }
        return output.append(this.token).append('>');
    }

    public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
        if (this.fieldTypeCompletionNode != null) {
            throw new CompletionNodeFound(this.fieldTypeCompletionNode, scope);
        }
        if (this.isCompletionNode) {
            throw new CompletionNodeFound(this, enclosingType, scope);
        } else {
            return super.resolveTypeEnclosing(scope, enclosingType);
        }
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
