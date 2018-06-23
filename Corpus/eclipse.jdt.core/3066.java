/*******************************************************************************
 * Copyright (c) 2004, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for
 *								Bug 429958 - [1.8][null] evaluate new DefaultLocation attribute of @NonNullByDefault
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CompletionOnParameterizedQualifiedTypeReference extends ParameterizedQualifiedTypeReference {

    public static final int K_TYPE = 0;

    public static final int K_CLASS = 1;

    public static final int K_INTERFACE = 2;

    public static final int K_EXCEPTION = 3;

    private int kind = K_TYPE;

    public char[] completionIdentifier;

    /**
	 * @param tokens
	 * @param typeArguments
	 * @param positions
	 */
    public  CompletionOnParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, char[] completionIdentifier, long[] positions) {
        this(tokens, typeArguments, completionIdentifier, positions, K_TYPE);
    }

    /**
	 * @param tokens
	 * @param typeArguments
	 * @param positions
	 * @param kind
	 */
    public  CompletionOnParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, char[] completionIdentifier, long[] positions, int kind) {
        super(tokens, typeArguments, 0, positions);
        this.completionIdentifier = completionIdentifier;
        this.kind = kind;
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

    public TypeBinding resolveType(BlockScope scope, boolean checkBounds, int location) {
        super.resolveType(scope, checkBounds, location);
        throw new CompletionNodeFound(this, this.resolvedType, scope);
    }

    public TypeBinding resolveType(ClassScope scope, int location) {
        super.resolveType(scope, location);
        throw new CompletionNodeFound(this, this.resolvedType, scope);
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        switch(this.kind) {
            case K_CLASS:
                output.append("<CompleteOnClass:");
                break;
            case K_INTERFACE:
                output.append("<CompleteOnInterface:");
                break;
            case K_EXCEPTION:
                output.append("<CompleteOnException:");
                break;
            default:
                output.append("<CompleteOnType:");
                break;
        }
        int length = this.tokens.length;
        for (int i = 0; i < length - 1; i++) {
            output.append(this.tokens[i]);
            TypeReference[] typeArgument = this.typeArguments[i];
            if (typeArgument != null) {
                output.append('<');
                int max = typeArgument.length - 1;
                for (int j = 0; j < max; j++) {
                    typeArgument[j].print(0, output);
                    //$NON-NLS-1$
                    output.append(//$NON-NLS-1$
                    ", ");
                }
                typeArgument[max].print(0, output);
                output.append('>');
            }
            output.append('.');
        }
        output.append(this.tokens[length - 1]);
        TypeReference[] typeArgument = this.typeArguments[length - 1];
        if (typeArgument != null) {
            output.append('<');
            int max = typeArgument.length - 1;
            for (int j = 0; j < max; j++) {
                typeArgument[j].print(0, output);
                //$NON-NLS-1$
                output.append(//$NON-NLS-1$
                ", ");
            }
            typeArgument[max].print(0, output);
            output.append('>');
        }
        output.append('.').append(this.completionIdentifier).append('>');
        return output;
    }
}
