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
package org.eclipse.jdt.internal.codeassist.select;

import org.eclipse.jdt.internal.compiler.ast.ImportReference;

public class SelectionOnImportReference extends ImportReference {

    public  SelectionOnImportReference(char[][] tokens, long[] positions, int modifiers) {
        super(tokens, positions, false, modifiers);
    }

    public StringBuffer print(int indent, StringBuffer output, boolean withOnDemand) {
        //$NON-NLS-1$
        printIndent(indent, output).append("<SelectOnImport:");
        for (int i = 0; i < this.tokens.length; i++) {
            if (i > 0)
                output.append('.');
            output.append(this.tokens[i]);
        }
        return output.append('>');
    }
}
