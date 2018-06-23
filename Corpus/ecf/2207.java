/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.discovery.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.internal.discovery.ui.views.DiscoveryView;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class TabbedPropertyAdapterFactory implements IAdapterFactory, ITabbedPropertySheetPageContributor {

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IPropertySheetPage.class)
            return new TabbedPropertySheetPage(this);
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
    public Class[] getAdapterList() {
        return new Class[] { IPropertySheetPage.class };
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
    public String getContributorId() {
        return DiscoveryView.ID;
    }
}
