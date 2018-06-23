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

public class ServicePropertySection extends AbstractPropertySection {

    private Text serviceName;

    private Text serviceID;

    private Text serviceIDNamespace;

    private Text servicePriority;

    private Text serviceWeight;

    private Text location;

    private IServiceInfo serviceInfo;

    public  ServicePropertySection() {
    // nothing
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
        Composite composite = getWidgetFactory().createFlatFormComposite(parent);
        FormData data;
        // Service Name
        //$NON-NLS-1$
        serviceName = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, 0);
        serviceName.setLayoutData(data);
        //$NON-NLS-1$
        CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Name:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceName, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceName, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceID 
        //$NON-NLS-1$
        serviceID = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceName, 0);
        serviceID.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "ID:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceID, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceID, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceID namespace
        //$NON-NLS-1$
        serviceIDNamespace = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceID, 0);
        serviceIDNamespace.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Namespace:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceIDNamespace, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceIDNamespace, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceID priority
        //$NON-NLS-1$
        servicePriority = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceIDNamespace, 0);
        servicePriority.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Priority:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(servicePriority, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(servicePriority, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // ServiceID weight
        //$NON-NLS-1$
        serviceWeight = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(servicePriority, 0);
        serviceWeight.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Weight:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(serviceWeight, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(serviceWeight, 0, SWT.CENTER);
        labelLabel.setLayoutData(data);
        // Location
        //$NON-NLS-1$
        location = getWidgetFactory().createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(serviceWeight, 0);
        location.setLayoutData(data);
        //$NON-NLS-1$
        labelLabel = getWidgetFactory().createCLabel(composite, "Location:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(location, -ITabbedPropertyConstants.HSPACE);
        data.top = new FormAttachment(location, 0, SWT.CENTER);
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
            serviceName.setText(serviceInfo.getServiceID().getEcfServiceName());
            serviceName.setEditable(false);
            serviceID.setText(serviceInfo.getEcfServiceInfo().getServiceID().getName());
            serviceID.setEditable(false);
            serviceIDNamespace.setText(serviceInfo.getEcfServiceInfo().getServiceID().getNamespace().getName());
            serviceIDNamespace.setEditable(false);
            //$NON-NLS-1$
            servicePriority.setText(serviceInfo.getEcfPriority() + "");
            servicePriority.setEditable(false);
            //$NON-NLS-1$
            serviceWeight.setText(serviceInfo.getEcfWeight() + "");
            serviceWeight.setEditable(false);
            location.setText(serviceInfo.getEcfLocation().toString());
            location.setEditable(false);
        }
    }
}
