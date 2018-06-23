/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.discovery.ui.statusline;

import org.eclipse.ecf.discovery.ui.model.IItemStatusLineProvider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.*;

public class AdapterFactoryStatuslineProvider implements ISelectionChangedListener {

    private ComposedAdapterFactory adapterFactory;

    private IStatusLineManager statusline;

    /**
	 * @param aStatusline
	 * @param adapterFactory
	 */
    public  AdapterFactoryStatuslineProvider(ComposedAdapterFactory anAdapterFactory, IStatusLineManager aStatusline) {
        adapterFactory = anAdapterFactory;
        statusline = aStatusline;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            EObject object = (EObject) ss.getFirstElement();
            if (// do we really have a selection?
            object != null) {
                IItemStatusLineProvider itemStatusLineProvider = (IItemStatusLineProvider) adapterFactory.adapt(object, IItemStatusLineProvider.class);
                if (itemStatusLineProvider != null) {
                    statusline.setMessage(itemStatusLineProvider.getStatusLineText(object));
                } else {
                    // fallback to IItemLabelProvider.getText(..)
                    IItemLabelProvider itemLabelProvider = (IItemLabelProvider) adapterFactory.adapt(object, IItemLabelProvider.class);
                    if (itemLabelProvider != null) {
                        statusline.setMessage(itemLabelProvider.getText(object));
                    }
                }
            }
        } else {
            //$NON-NLS-1$
            statusline.setMessage("");
        }
    }
}
