/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.ui.wizards;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard node represents a "potential" wizard. Wizard nodes are used by
 * wizard selection pages to allow the user to pick from several available
 * nested wizards.
 * <p>
 * <b>Subclasses</b> simply need to override method <code>createWizard()</code>,
 * which is responsible for creating an instance of the wizard it represents AND
 * ensuring that this wizard is the "right" type of wizard (e.g.- New, Import,
 * etc.).
 * </p>
 */
public abstract class WizardNode implements IWizardNode, IPluginContribution {

    protected IWizard wizard;

    protected IWorkbench workbench;

    protected WorkbenchWizardElement wizardElement;

    protected WizardPage parentWizardPage;

    public  WizardNode(IWorkbench workbench, WizardPage wizardPage, WorkbenchWizardElement wizardElement) {
        super();
        this.workbench = workbench;
        this.parentWizardPage = wizardPage;
        this.wizardElement = wizardElement;
    }

    protected IWorkbench getWorkbench() {
        return this.workbench;
    }

    protected WorkbenchWizardElement getWizardElement() {
        return wizardElement;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizardNode#dispose()
	 */
    public void dispose() {
    // Do nothing since the wizard wasn't created via reflection.
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
    public Point getExtent() {
        return new Point(-1, -1);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPluginContribution#getLocalId()
	 */
    public String getLocalId() {
        IPluginContribution contribution = (IPluginContribution) wizardElement.getAdapter(IPluginContribution.class);
        if (contribution != null)
            return contribution.getLocalId();
        return wizardElement.getId();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPluginContribution#getPluginId()
	 */
    public String getPluginId() {
        IPluginContribution contribution = (IPluginContribution) wizardElement.getAdapter(IPluginContribution.class);
        if (contribution != null)
            return contribution.getLocalId();
        return null;
    }

    public abstract IWizard createWizard() throws CoreException;

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizardNode#getWizard()
	 */
    public IWizard getWizard() {
        if (wizard != null)
            // we've already created it
            return wizard;
        final IWizard[] newWizard = new IWizard[1];
        final IStatus statuses[] = new IStatus[1];
        // Start busy indicator.
        BusyIndicator.showWhile(parentWizardPage.getShell().getDisplay(), new Runnable() {

            public void run() {
                SafeRunner.run(new SafeRunnable() {

                    /**
					 * Add the exception details to status is one
					 * happens.
					 */
                    public void handleException(Throwable e) {
                        IPluginContribution contribution = (IPluginContribution) wizardElement.getAdapter(IPluginContribution.class);
                        statuses[0] = new Status(IStatus.ERROR, contribution != null ? contribution.getPluginId() : Activator.PLUGIN_ID, IStatus.OK, //$NON-NLS-1$,
                        e.getMessage() == null ? //$NON-NLS-1$,
                        "" : //$NON-NLS-1$,
                        e.getMessage(), e);
                    }

                    public void run() {
                        try {
                            newWizard[0] = createWizard();
                        // create instance of target wizard
                        } catch (CoreException e) {
                            statuses[0] = e.getStatus();
                        }
                    }
                });
            }
        });
        if (statuses[0] != null) {
            //$NON-NLS-1$
            parentWizardPage.setErrorMessage("The selected wizard could not be started.");
            //$NON-NLS-1$
            ErrorDialog.openError(//$NON-NLS-1$
            parentWizardPage.getShell(), //$NON-NLS-1$
            "Problem Opening Wizard", //$NON-NLS-1$
            "The selected wizard could not be started.", //$NON-NLS-1$
            statuses[0]);
            return null;
        }
        wizard = newWizard[0];
        return wizard;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizardNode#isContentCreated()
	 */
    public boolean isContentCreated() {
        return wizard != null;
    }
}
