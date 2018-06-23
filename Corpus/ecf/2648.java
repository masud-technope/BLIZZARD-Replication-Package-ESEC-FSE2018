/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.ui;

import java.util.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Content provider for multiple roster viewer. This content provider implements
 * an IMultiRosterContentProvider suitable for use by tree viewers that accepts
 * ITreeContentProviders as input. This class may be subclassed in order to
 * customize the behavior/display of other content providers.
 * 
 */
public class MultiRosterContentProvider implements ITreeContentProvider {

    protected List rosters = Collections.synchronizedList(new ArrayList());

    protected Object input;

    protected IWorkbenchAdapter getAdapter(Object element) {
        IWorkbenchAdapter adapter = null;
        if (element instanceof IAdaptable)
            adapter = (IWorkbenchAdapter) ((IAdaptable) element).getAdapter(IWorkbenchAdapter.class);
        if (element != null && adapter == null)
            adapter = (IWorkbenchAdapter) Platform.getAdapterManager().loadAdapter(element, IWorkbenchAdapter.class.getName());
        return adapter;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
    public Object[] getChildren(Object parentElement) {
        IWorkbenchAdapter adapter = getAdapter(parentElement);
        return adapter == null ? new Object[0] : adapter.getChildren(parentElement);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
    public Object getParent(Object element) {
        IWorkbenchAdapter adapter = getAdapter(element);
        return adapter != null ? adapter.getParent(element) : input;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            input = inputElement;
            Object[] elements = ((List) inputElement).toArray();
            for (int i = 0; i < elements.length; i++) {
                MultiRosterAccount account = (MultiRosterAccount) elements[i];
                elements[i] = account.getRoster();
            }
            return elements;
        }
        return new Object[0];
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
