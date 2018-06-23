/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Markus Alexander Kuppe - https://bugs.eclipse.org/256603
 *****************************************************************************/
package org.eclipse.ecf.internal.discovery.ui;

import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;

public class ServicePropertiesLabelProvider extends LabelProvider {

    /* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
    public String getText(Object element) {
        if (element instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) element;
            Object selected = ss.getFirstElement();
            if (selected instanceof IServiceInfo) {
                IServiceInfo si = (IServiceInfo) selected;
                return si.getServiceID().getEcfServiceName();
            }
        }
        return null;
    }
}
