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

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

public class CompletionOnMethodName extends MethodDeclaration {

    public int selectorEnd;

    public  CompletionOnMethodName(CompilationResult compilationResult) {
        super(compilationResult);
    }

    public StringBuffer print(int indent, StringBuffer output) {
        printIndent(indent, output);
        //$NON-NLS-1$
        output.append("<CompletionOnMethodName:");
        printModifiers(this.modifiers, output);
        printReturnType(0, output);
        output.append(this.selector).append('(');
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; i++) {
                if (i > 0)
                    //$NON-NLS-1$
                    output.append(//$NON-NLS-1$
                    ", ");
                this.arguments[i].print(0, output);
            }
        }
        output.append(')');
        if (this.thrownExceptions != null) {
            //$NON-NLS-1$
            output.append(" throws ");
            for (int i = 0; i < this.thrownExceptions.length; i++) {
                if (i > 0)
                    //$NON-NLS-1$
                    output.append(//$NON-NLS-1$
                    ", ");
                this.thrownExceptions[i].print(0, output);
            }
        }
        return output.append('>');
    }

    public void resolve(ClassScope upperScope) {
        super.resolve(upperScope);
        throw new CompletionNodeFound(this, upperScope);
    }
}
