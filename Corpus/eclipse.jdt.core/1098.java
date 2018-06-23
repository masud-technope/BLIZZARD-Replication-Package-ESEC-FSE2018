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
package org.eclipse.jdt.internal.core.search.matching;

import org.eclipse.jdt.internal.core.index.*;

public class PackageDeclarationPattern extends JavaSearchPattern {

    protected char[] pkgName;

    public  PackageDeclarationPattern(char[] pkgName, int matchRule) {
        super(PKG_DECL_PATTERN, matchRule);
        this.pkgName = pkgName;
    }

    public EntryResult[] queryIn(Index index) {
        // package declarations are not indexed
        return null;
    }

    protected StringBuffer print(StringBuffer output) {
        //$NON-NLS-1$
        output.append("PackageDeclarationPattern: <");
        if (this.pkgName != null)
            output.append(this.pkgName);
        else
            //$NON-NLS-1$
            output.append("*");
        //$NON-NLS-1$
        output.append(">");
        return super.print(output);
    }
}
