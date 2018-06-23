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
package org.eclipse.ecf.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.ui.ContainerConfigurationResult;
import org.eclipse.ecf.ui.IConfigurationWizard;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;

public class ConfigurationWizardNode extends WizardNode {

    protected ContainerTypeDescription typeDescription;

    protected ContainerConfigurationResult containerHolder = null;

    public  ConfigurationWizardNode(IWorkbench workbench, WizardPage wizardPage, WorkbenchWizardElement wizardElement, ContainerTypeDescription containerTypeDescription) {
        super(workbench, wizardPage, wizardElement);
        this.typeDescription = containerTypeDescription;
    }

    public IWizard createWizard() throws CoreException {
        IConfigurationWizard configWizard = ((IConfigurationWizard) getWizardElement().createWizardForNode());
        configWizard.init(getWorkbench(), typeDescription);
        return configWizard;
    }

    public ContainerConfigurationResult getConfigurationResult() {
        if (containerHolder != null)
            return containerHolder;
        return ((IConfigurationWizard) getWizard()).getConfigurationResult();
    }
}
