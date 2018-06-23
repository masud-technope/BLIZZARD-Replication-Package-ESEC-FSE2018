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
package org.eclipse.ecf.internal.ui.wizards;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.model.AdaptableList;

public class WizardActivityFilter extends ViewerFilter {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        Object[] children = ((ITreeContentProvider) ((AbstractTreeViewer) viewer).getContentProvider()).getChildren(element);
        if (children.length > 0)
            return filter(viewer, element, children).length > 0;
        if (parentElement.getClass().equals(AdaptableList.class))
            return true;
        if (WorkbenchActivityHelper.filterItem(element))
            return false;
        return true;
    }
}
