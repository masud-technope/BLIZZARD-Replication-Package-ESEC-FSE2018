/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.ecf.internal.ui.wizards.IWizardRegistryConstants;
import org.eclipse.ecf.ui.ContainerConfigurationResult;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

/**
 * Dialog to show container connect wizard.
 */
public class ContainerConnectWizardDialog extends WizardDialog {

    public  ContainerConnectWizardDialog(Shell parentShell, IWorkbench workbench, ContainerConfigurationResult containerHolder) throws CoreException {
        super(parentShell, getWizard(workbench, containerHolder));
    }

    protected static IConnectWizard getWizard(IWorkbench workbench, ContainerConfigurationResult containerHolder) throws CoreException {
        IConnectWizard connectWizard = null;
        IConfigurationElement ce = findConnectWizardConfigurationElements(containerHolder)[0];
        connectWizard = (IConnectWizard) ce.createExecutableExtension(IWizardRegistryConstants.ATT_CLASS);
        connectWizard.init(workbench, containerHolder.getContainer());
        return connectWizard;
    }

    protected static IConfigurationElement[] findConnectWizardConfigurationElements(ContainerConfigurationResult containerHolder) {
        List result = new ArrayList();
        IExtensionRegistry reg = Activator.getDefault().getExtensionRegistry();
        if (reg != null) {
            IExtensionPoint extensionPoint = reg.getExtensionPoint(IWizardRegistryConstants.CONNECT_EPOINT_ID);
            if (extensionPoint == null) {
                return null;
            }
            IConfigurationElement[] ce = extensionPoint.getConfigurationElements();
            for (int i = 0; i < ce.length; i++) {
                String value = ce[i].getAttribute(IWizardRegistryConstants.ATT_CONTAINER_TYPE_NAME);
                if (value != null && value.equals(containerHolder.getContainerTypeDescription().getName()))
                    result.add(ce[i]);
            }
            return (IConfigurationElement[]) result.toArray(new IConfigurationElement[] {});
        }
        return new IConfigurationElement[0];
    }

    public boolean hasConnectWizard(ContainerConfigurationResult containerHolder) {
        return (findConnectWizardConfigurationElements(containerHolder).length > 0);
    }
}
