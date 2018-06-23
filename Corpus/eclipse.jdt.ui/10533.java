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
package org.eclipse.ltk.internal.ui.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ltk.ui.refactoring.IChangePreviewViewer;

public class ChangePreviewViewerDescriptor extends AbstractDescriptor {

    //$NON-NLS-1$
    private static final String EXT_ID = "changePreviewViewers";

    private static DescriptorManager fgDescriptions = new //$NON-NLS-1$
    DescriptorManager(//$NON-NLS-1$
    EXT_ID, //$NON-NLS-1$
    "change") {

        @Override
        protected AbstractDescriptor createDescriptor(IConfigurationElement element) {
            return new ChangePreviewViewerDescriptor(element);
        }
    };

    public static ChangePreviewViewerDescriptor get(Object element) throws CoreException {
        return (ChangePreviewViewerDescriptor) fgDescriptions.getDescriptor(element);
    }

    public  ChangePreviewViewerDescriptor(IConfigurationElement element) {
        super(element);
    }

    public IChangePreviewViewer createViewer() throws CoreException {
        return (IChangePreviewViewer) fConfigurationElement.createExecutableExtension(CLASS);
    }
}
