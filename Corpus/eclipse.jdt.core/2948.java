/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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

public class CompletionOnSingleNameReference extends SingleNameReference {

    public char[][] possibleKeywords;

    public boolean canBeExplicitConstructor;

    public boolean isInsideAnnotationAttribute;

    public boolean isPrecededByModifiers;

    public  CompletionOnSingleNameReference(char[] source, long pos, boolean isInsideAnnotationAttribute) {
        this(source, pos, null, false, isInsideAnnotationAttribute);
    }

    public  CompletionOnSingleNameReference(char[] source, long pos, char[][] possibleKeywords, boolean canBeExplicitConstructor, boolean isInsideAnnotationAttribute) {
        super(source, pos);
        this.possibleKeywords = possibleKeywords;
        this.canBeExplicitConstructor = canBeExplicitConstructor;
        this.isInsideAnnotationAttribute = isInsideAnnotationAttribute;
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        //$NON-NLS-1$
        output.append("<CompleteOnName:");
        return super.printExpression(0, output).append('>');
    }

    public TypeBinding resolveType(BlockScope scope) {
        if (scope instanceof MethodScope) {
            throw new CompletionNodeFound(this, scope, ((MethodScope) scope).insideTypeAnnotation);
        }
        throw new CompletionNodeFound(this, scope);
    }
}
