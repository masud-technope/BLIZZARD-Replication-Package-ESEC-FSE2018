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

public class CompletionOnQualifiedTypeReference extends QualifiedTypeReference {

    public static final int K_TYPE = 0;

    public static final int K_CLASS = 1;

    public static final int K_INTERFACE = 2;

    public static final int K_EXCEPTION = 3;

    private int kind = K_TYPE;

    public char[] completionIdentifier;

    public boolean isConstructorType;

    public  CompletionOnQualifiedTypeReference(char[][] previousIdentifiers, char[] completionIdentifier, long[] positions) {
        this(previousIdentifiers, completionIdentifier, positions, K_TYPE);
    }

    public  CompletionOnQualifiedTypeReference(char[][] previousIdentifiers, char[] completionIdentifier, long[] positions, int kind) {
        super(previousIdentifiers, positions);
        this.completionIdentifier = completionIdentifier;
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
        // it can be a package, type or member type
        // step up from the ClassScope
        Binding binding = scope.parent.getTypeOrPackage(this.tokens);
        if (!binding.isValidBinding()) {
            scope.problemReporter().invalidType(this, (TypeBinding) binding);
            if (binding.problemId() == ProblemReasons.NotFound) {
                throw new CompletionNodeFound(this, binding, scope);
            }
            throw new CompletionNodeFound();
        }
        throw new CompletionNodeFound(this, binding, scope);
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

    public void setKind(int kind) {
        this.kind = kind;
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
        for (int i = 0; i < this.tokens.length; i++) {
            output.append(this.tokens[i]);
            output.append('.');
        }
        output.append(this.completionIdentifier).append('>');
        return output;
    }
}
