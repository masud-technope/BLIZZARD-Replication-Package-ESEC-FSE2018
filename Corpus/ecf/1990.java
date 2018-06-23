/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.presence.collab.ui.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.IViewDescriptor;

public class ShowViewDialogViewerFilter extends ViewerFilter {

    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IViewDescriptor && //$NON-NLS-1$
        "org.eclipse.ui.internal.introview".equals(((IViewDescriptor) element).getId()))
            return false;
        return true;
    }
}
