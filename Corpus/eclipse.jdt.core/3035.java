/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
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

public class CompletionOnClassLiteralAccess extends ClassLiteralAccess {

    public char[] completionIdentifier;

    public int classStart;

    public  CompletionOnClassLiteralAccess(long pos, TypeReference t) {
        super((int) pos, t);
        this.classStart = (int) (pos >>> 32);
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        //$NON-NLS-1$
        output.append("<CompleteOnClassLiteralAccess:");
        return this.type.print(0, output).append('.').append(this.completionIdentifier).append('>');
    }

    public TypeBinding resolveType(BlockScope scope) {
        if (super.resolveType(scope) == null)
            throw new CompletionNodeFound();
        else
            throw new CompletionNodeFound(this, this.targetType, scope);
    }
}
