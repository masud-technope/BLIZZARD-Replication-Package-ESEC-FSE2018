/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
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

public class CompletionOnQualifiedAllocationExpression extends QualifiedAllocationExpression {

    public TypeBinding resolveType(BlockScope scope) {
        this.argumentTypes = Binding.NO_PARAMETERS;
        if (this.arguments != null) {
            int argsLength = this.arguments.length;
            int length = this.arguments.length;
            this.argumentTypes = new TypeBinding[length];
            for (int a = argsLength; --a >= 0; ) {
                this.argumentTypes[a] = this.arguments[a].resolveType(scope);
            }
        }
        final boolean isDiamond = this.type != null && (this.type.bits & ASTNode.IsDiamond) != 0;
        if (this.enclosingInstance != null) {
            TypeBinding enclosingType = this.enclosingInstance.resolveType(scope);
            if (enclosingType == null) {
                // Eg.: new Test<>().new Test<>(#cursor#
                if (this.enclosingInstance instanceof AllocationExpression) {
                    TypeReference enclosingInstanceType = ((AllocationExpression) this.enclosingInstance).type;
                    if (enclosingInstanceType != null) {
                        enclosingType = enclosingInstanceType.resolvedType;
                    }
                }
            }
            if (enclosingType == null || !(enclosingType instanceof ReferenceBinding)) {
                throw new CompletionNodeFound();
            }
            this.resolvedType = ((SingleTypeReference) this.type).resolveTypeEnclosing(scope, (ReferenceBinding) enclosingType);
            if (isDiamond && (this.resolvedType instanceof ParameterizedTypeBinding)) {
                TypeBinding[] inferredTypes = inferElidedTypes(scope);
                if (inferredTypes != null) {
                    this.resolvedType = this.type.resolvedType = scope.environment().createParameterizedType(((ParameterizedTypeBinding) this.resolvedType).genericType(), inferredTypes, this.resolvedType.enclosingType());
                } else {
                    // inference failed. Resolved type will be of the form Test<>
                    this.bits |= ASTNode.IsDiamond;
                }
            }
            if (!(this.resolvedType instanceof ReferenceBinding))
                // no need to continue if its an array or base type
                throw new CompletionNodeFound();
            if (// handle the anonymous class definition case
            this.resolvedType.isInterface())
                this.resolvedType = scope.getJavaLangObject();
        } else {
            this.resolvedType = this.type.resolveType(scope, /* check bounds*/
            true);
            if (isDiamond && (this.resolvedType instanceof ParameterizedTypeBinding)) {
                TypeBinding[] inferredTypes = inferElidedTypes(scope);
                if (inferredTypes != null) {
                    this.resolvedType = this.type.resolvedType = scope.environment().createParameterizedType(((ParameterizedTypeBinding) this.resolvedType).genericType(), inferredTypes, this.resolvedType.enclosingType());
                } else {
                    // inference failed. Resolved type will be of the form Test<>
                    this.bits |= ASTNode.IsDiamond;
                }
            }
            if (!(this.resolvedType instanceof ReferenceBinding))
                // no need to continue if its an array or base type
                throw new CompletionNodeFound();
        }
        throw new CompletionNodeFound(this, this.resolvedType, scope);
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.enclosingInstance == null)
            //$NON-NLS-1$
            output.append("<CompleteOnAllocationExpression:");
        else
            //$NON-NLS-1$
            output.append("<CompleteOnQualifiedAllocationExpression:");
        return super.printExpression(indent, output).append('>');
    }
}
