/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Alexander Kuppe - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.discovery.ui;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.ui.views.properties.IPropertySource;

public class EObjectPropertySourceFactory implements IAdapterFactory {

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.equals(IPropertySource.class) && adaptableObject instanceof EObject) {
            EObject eObj = (EObject) adaptableObject;
            List adapters = eObj.eAdapters();
            for (Iterator itr = adapters.iterator(); itr.hasNext(); ) {
                Object adapter = itr.next();
                if (adapter instanceof IItemPropertySource) {
                    //TODO handle the case with multiple IItemPropertySource adapters for this EObject
                    return new PropertySource(adaptableObject, (IItemPropertySource) adapter);
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
    public Class[] getAdapterList() {
        return new Class[] { IPropertySource.class };
    }
}
