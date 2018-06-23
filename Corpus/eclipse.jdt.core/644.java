/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CompletionOnStringLiteral extends StringLiteral {

    public int contentStart;

    public int contentEnd;

    public  CompletionOnStringLiteral(char[] token, int s, int e, int cs, int ce, int lineNumber) {
        super(token, s, e, lineNumber);
        this.contentStart = cs;
        this.contentEnd = ce;
    }

    public  CompletionOnStringLiteral(int s, int e, int cs, int ce) {
        super(s, e);
        this.contentStart = cs;
        this.contentEnd = ce;
    }

    public TypeBinding resolveType(ClassScope scope) {
        throw new CompletionNodeFound(this, null, scope);
    }

    public TypeBinding resolveType(BlockScope scope) {
        throw new CompletionNodeFound(this, null, scope);
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        //$NON-NLS-1$
        output.append("<CompletionOnString:");
        output = super.printExpression(indent, output);
        return output.append('>');
    }
}
