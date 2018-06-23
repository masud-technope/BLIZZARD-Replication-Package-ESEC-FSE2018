/****************************************************************************
 * Copyright (c) 2006 Remy Suen, Composent, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.ui.wizards;

import java.net.URI;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.ecf.ui.wizards.AbstractConnectWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * @since 1.4
 */
public class GenericClientContainerConnectWizard extends Wizard implements IConnectWizard {

    protected static final int CONNECT_ERROR_CODE = 7777;

    private IContainer container;

    private AbstractConnectWizardPage wizardPage;

    private ID targetID;

    private IConnectContext connectContext;

    private URI uri;

    public  GenericClientContainerConnectWizard() {
        super();
    }

    public  GenericClientContainerConnectWizard(URI uri) {
        super();
        this.uri = uri;
    }

    public void addPages() {
        wizardPage = new GenericClientContainerConnectWizardPage(uri);
        addPage(wizardPage);
    }

    public void init(IWorkbench workbench, IContainer container) {
        this.container = container;
    }

    public ID getTargetID() {
        return targetID;
    }

    public IConnectContext getConnectContext() {
        return connectContext;
    }

    public boolean performFinish() {
        if (wizardPage.shouldRequestPassword()) {
            String password = wizardPage.getPassword();
            if (wizardPage.shouldRequestUsername()) {
                connectContext = ConnectContextFactory.createUsernamePasswordConnectContext(wizardPage.getUsername(), password);
            } else {
                connectContext = ConnectContextFactory.createPasswordConnectContext(password);
            }
        }
        try {
            targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), wizardPage.getConnectID());
        } catch (IDCreateException e) {
            new IDCreateErrorDialog(null, wizardPage.getConnectID(), e).open();
            return false;
        }
        new AsynchContainerConnectAction(this.container, this.targetID, this.connectContext).run();
        return true;
    }
}
