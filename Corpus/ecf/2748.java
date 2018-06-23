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
package org.eclipse.ecf.ui.wizards;

import org.eclipse.ecf.internal.ui.Messages;
import org.eclipse.ecf.ui.ContainerConfigurationResult;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard to allow the selection and display of a configuration.  This wizard shows
 * the available IConfigurationWizards in a list, and when selected opens up the
 * selected configuration wizard.
 */
public class ConfigurationWizardSelectionWizard extends Wizard {

    private IWorkbench workbench;

    private IStructuredSelection selection;

    protected ConfigurationWizardSelectionPage createContainerWizardPage;

    protected ContainerConfigurationResult containerHolder;

    public boolean performFinish() {
        if (createContainerWizardPage != null)
            this.containerHolder = createContainerWizardPage.getContainerResult();
        return (this.containerHolder != null);
    }

    public ContainerConfigurationResult getContainerConfigurationResult() {
        return this.containerHolder;
    }

    public void addPages() {
        setForcePreviousAndNextButtons(true);
        createContainerWizardPage = new ConfigurationWizardSelectionPage(this.workbench, this.selection);
        addPage(createContainerWizardPage);
    }

    /**
	 * Initializes the wizard.
	 * 
	 * @param aWorkbench
	 *            the workbench
	 * @param currentSelection
	 *            the current selection
	 */
    public void init(IWorkbench aWorkbench, IStructuredSelection currentSelection) {
        this.workbench = aWorkbench;
        this.selection = currentSelection;
        setWindowTitle(Messages.ConfigurationWizard_title);
        setNeedsProgressMonitor(true);
    }
}
