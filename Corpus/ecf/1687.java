/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.ecf.internal.example.collab.actions.URIClientConnectAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;

public class JoinGroupWizard extends Wizard {

    protected static final String PAGE_TITLE = Messages.JoinGroupWizard_CONNECT;

    private static final String DIALOG_SETTINGS = JoinGroupWizard.class.getName();

    JoinGroupWizardPage mainPage;

    private final IResource resource;

    private String connectID;

    public  JoinGroupWizard(IResource resource, IWorkbench workbench) {
        super();
        this.resource = resource;
        setWindowTitle(PAGE_TITLE);
        final IDialogSettings dialogSettings = ClientPlugin.getDefault().getDialogSettings();
        IDialogSettings wizardSettings = dialogSettings.getSection(DIALOG_SETTINGS);
        if (wizardSettings == null)
            wizardSettings = dialogSettings.addNewSection(DIALOG_SETTINGS);
        setDialogSettings(wizardSettings);
    }

    public  JoinGroupWizard(IResource resource, IWorkbench workbench, String connectID) {
        this(resource, workbench);
        this.connectID = connectID;
    }

    protected ISchedulingRule getSchedulingRule() {
        return resource;
    }

    public void addPages() {
        super.addPages();
        mainPage = new JoinGroupWizardPage(connectID);
        addPage(mainPage);
    }

    public boolean performFinish() {
        try {
            finishPage(new NullProgressMonitor());
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void finishPage(final IProgressMonitor monitor) throws InterruptedException, CoreException {
        mainPage.saveDialogSettings();
        URIClientConnectAction client = null;
        final String groupName = mainPage.getJoinGroupText();
        final String nickName = mainPage.getNicknameText();
        final String containerType = mainPage.getContainerType();
        final boolean autoLogin = mainPage.getAutoLoginFlag();
        try {
            //$NON-NLS-1$
            client = new URIClientConnectAction(containerType, groupName, nickName, "", resource, autoLogin);
            client.run(null);
        } catch (final Exception e) {
            final String id = ClientPlugin.getDefault().getBundle().getSymbolicName();
            throw new CoreException(new Status(Status.ERROR, id, IStatus.ERROR, NLS.bind(Messages.JoinGroupWizard_COULD_NOT_CONNECT, groupName), e));
        }
    }
}
