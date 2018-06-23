/****************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.ui.wizards;

import java.util.regex.Matcher;
import org.eclipse.ecf.internal.provider.xmpp.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

final class XMPPSConnectWizardPage extends XMPPConnectWizardPage {

    private Text keystorePasswordText;

     XMPPSConnectWizardPage() {
        super();
        setTitle(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_TITLE);
        setDescription(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_DESCRIPTION);
        setPageComplete(false);
    }

     XMPPSConnectWizardPage(String usernameAtHost) {
        this();
        this.usernameAtHost = usernameAtHost;
    }

    private void verify() {
        final String text = connectText.getText();
        if (//$NON-NLS-1$
        text.equals("")) {
            updateStatus(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_STATUS);
        } else {
            final Matcher matcher = emailPattern.matcher(text);
            if (!matcher.matches()) {
                updateStatus(Messages.XMPPConnectWizardPage_WIZARD_STATUS_INCOMPLETE);
            } else {
                restorePassword(text);
                updateStatus(null);
            }
        }
    }

    public void createControl(Composite parent) {
        parent = new Composite(parent, SWT.NONE);
        parent.setLayout(new GridLayout());
        final GridData fillData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        final GridData endData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        Label label = new Label(parent, SWT.LEFT);
        label.setText(Messages.XMPPConnectWizardPage_LABEL_USERID);
        connectText = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
        connectText.setLayoutData(fillData);
        connectText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verify();
            }
        });
        connectText.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                verify();
            }

            public void widgetSelected(SelectionEvent e) {
                verify();
            }
        });
        label = new Label(parent, SWT.RIGHT);
        label.setText(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_TEMPLATE);
        label.setLayoutData(endData);
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.XMPPConnectWizardPage_WIZARD_ALT_SERVER);
        serverText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        serverText.setLayoutData(fillData);
        serverText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verify();
            }
        });
        serverText.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                verify();
            }

            public void widgetSelected(SelectionEvent e) {
                verify();
            }
        });
        label = new Label(parent, SWT.RIGHT);
        label.setText(Messages.XMPPConnectWizardPage_WIZARD_ALT_SERVER_TEXT);
        label.setLayoutData(endData);
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_PASSWORD);
        passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
        passwordText.setLayoutData(fillData);
        restoreCombo();
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_KEYSTORE_PASSWORD);
        keystorePasswordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
        keystorePasswordText.setLayoutData(fillData);
        if (usernameAtHost != null) {
            connectText.setText(usernameAtHost);
            restorePassword(usernameAtHost);
            passwordText.setFocus();
        }
        verify();
        if (connectText.getText().equals("")) {
            updateStatus(null);
            setPageComplete(false);
        } else if (isPageComplete())
            passwordText.setFocus();
        org.eclipse.jface.dialogs.Dialog.applyDialogFont(parent);
        setControl(parent);
    }

    String getKeystorePassword() {
        return keystorePasswordText.getText();
    }
}
