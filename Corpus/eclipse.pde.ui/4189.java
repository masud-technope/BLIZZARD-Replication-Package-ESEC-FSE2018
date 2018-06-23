/*******************************************************************************
 *  Copyright (c) 2003, 2007 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import org.eclipse.pde.internal.ui.templates.PDETemplateSection;

public abstract class BaseEditorTemplate extends PDETemplateSection {

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.editors";
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$
        return new String[] { "icons/" };
    }
}
