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

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.*;

public class CompletionOnFieldType extends FieldDeclaration {

    public boolean isLocalVariable;

    public  CompletionOnFieldType(TypeReference type, boolean isLocalVariable) {
        super();
        this.sourceStart = type.sourceStart;
        this.sourceEnd = type.sourceEnd;
        this.type = type;
        this.name = CharOperation.NO_CHAR;
        this.isLocalVariable = isLocalVariable;
        if (type instanceof CompletionOnSingleTypeReference) {
            ((CompletionOnSingleTypeReference) type).fieldTypeCompletionNode = this;
        }
    }

    public StringBuffer printStatement(int tab, StringBuffer output) {
        return this.type.print(tab, output).append(';');
    }
}
