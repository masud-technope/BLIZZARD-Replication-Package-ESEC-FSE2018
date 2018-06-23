/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.ui;

import java.util.List;
import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for multiple users viewer. This content provider implements
 * an ITreeContentProvider suitable for use by tree viewers that accepts
 * ITreeContentProviders as input. This class may be subclassed in order to
 * customize the behavior/display of other content providers.
 * @since 2.0
 * 
 */
public class UserSearchContentProvider implements ITreeContentProvider {

    protected static Object[] EMPTY_ARRAY = new Object[0];

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IResultList) {
            return ((IResultList) parentElement).getResults().toArray();
        }
        return EMPTY_ARRAY;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
    public Object getParent(Object element) {
        if (element == null) {
            return null;
        }
        return element;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
    public boolean hasChildren(Object element) {
        if (element instanceof IResultList) {
            IResultList list = (IResultList) element;
            if (list.getResults().size() > 0)
                return true;
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List)
            return ((List) inputElement).toArray();
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
    public void dispose() {
    // do nothing
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // do nothing
    }
}
