/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.comparator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.pde.api.tools.internal.util.Util;

/**
 * Represents a type parameter inside a generic signature
 */
class TypeParameterDescriptor {

    //$NON-NLS-1$
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";

    String classBound;

    List<String> interfaceBounds;

    String name;

    public  TypeParameterDescriptor(String name) {
        this.name = name;
    }

    public void addInterfaceBound(String bound) {
        if (this.interfaceBounds == null) {
            this.interfaceBounds = new ArrayList();
        }
        this.interfaceBounds.add(bound);
    }

    public void setClassBound(String bound) {
        if (JAVA_LANG_OBJECT.equals(bound)) {
            // <E> is implicitly <E extends Object>
            return;
        }
        this.classBound = bound;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("type parameter ").append(this.name).append(" : ").append(Util.LINE_DELIMITER);
        if (this.classBound != null) {
            //$NON-NLS-1$
            buffer.append("class bound : ").append(this.classBound).append(Util.LINE_DELIMITER);
        }
        if (this.interfaceBounds != null) {
            //$NON-NLS-1$
            buffer.append("interface bounds : ");
            int i = 0;
            for (String string : this.interfaceBounds) {
                if (i > 0) {
                    buffer.append(',');
                }
                i++;
                buffer.append(string);
            }
            buffer.append(Util.LINE_DELIMITER);
        }
        return String.valueOf(buffer);
    }
}
