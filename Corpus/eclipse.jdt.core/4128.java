/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.select;

import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SelectionOnQualifiedSuperReference extends QualifiedSuperReference {

    public  SelectionOnQualifiedSuperReference(TypeReference name, int pos, int sourceEnd) {
        super(name, pos, sourceEnd);
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        //$NON-NLS-1$
        output.append("<SelectOnQualifiedSuper:");
        return super.printExpression(0, output).append('>');
    }

    public TypeBinding resolveType(BlockScope scope) {
        TypeBinding binding = super.resolveType(scope);
        if (binding == null || !binding.isValidBinding())
            throw new SelectionNodeFound();
        else
            throw new SelectionNodeFound(binding);
    }
}
