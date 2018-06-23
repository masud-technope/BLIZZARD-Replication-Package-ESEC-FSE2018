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

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.*;

public class ServiceTypePropertySection extends AbstractPropertySection {

    private Text serviceTypeID;

    private Text serviceTypeIDInternal;

    private Text serviceTypeIDNamespace;

    private IServiceInfo serviceInfo;

    public  ServiceTypePropertySection() {
    // nothing
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
        Composite composite = getWidgetFactory().createFlatFormComposite(parent);
        FormData data;
        // ServiceTypeID
        //$NON-NLS-1$
        serviceTypeID = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, 0);
        serviceTypeID.setLayoutData(data);
        //$NON-NLS-1$
        CLabel labelLabel = getWidgetFactory().createCLabel(composite, "TypeID:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceTypeID, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceTypeID, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceTypeID internal
        //$NON-NLS-1$
        serviceTypeIDInternal = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceTypeID, 0);
        serviceTypeIDInternal.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Internal:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceTypeIDInternal, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceTypeIDInternal, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceTypeID namespace
        //$NON-NLS-1$
        serviceTypeIDNamespace = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceTypeIDInternal, 0);
        serviceTypeIDNamespace.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Namespace:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceTypeIDNamespace, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceTypeIDNamespace, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
    public void dispose() {
        super.dispose();
        serviceInfo = null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        if (input instanceof IServiceInfo) {
            serviceInfo = (IServiceInfo) input;
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
    public void refresh() {
        if (serviceInfo != null) {
            serviceTypeID.setText(serviceInfo.getServiceID().getEcfServiceID().getServiceTypeID().getName());
            serviceTypeID.setEditable(false);
            serviceTypeIDInternal.setText(serviceInfo.getServiceID().getEcfServiceID().getServiceTypeID().getInternal());
            serviceTypeIDInternal.setEditable(false);
            serviceTypeIDNamespace.setText(serviceInfo.getServiceID().getEcfServiceID().getServiceTypeID().getNamespace().getName());
            serviceTypeIDNamespace.setEditable(false);
        }
    }
}
