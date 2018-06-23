/****************************************************************************
 * Copyright (c) 2008 Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.ui.wizards;

import java.util.regex.Matcher;
import org.eclipse.ecf.internal.provider.xmpp.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

final class EclipseXMPPSConnectWizardPage extends XMPPConnectWizardPage {

    private static final String ECLIPSE_XMPP_HOST = (String) System.getProperty("org.eclipse.ecf.provider.xmpp.ui.eclipseXMPPServer", "xmpp.eclipse.org");

     EclipseXMPPSConnectWizardPage() {
        super();
        setTitle("Eclipse IM Connection Wizard");
        setDescription("Eclipse Bugzilla Account - Please login using your Eclipse Bugzilla username and password");
        setPageComplete(false);
    }

     EclipseXMPPSConnectWizardPage(String usernameAtHost) {
        this();
        this.usernameAtHost = usernameAtHost;
    }

    private void verify() {
        final String text = connectText.getText();
        if (//$NON-NLS-1$
        text.equals("")) {
            updateStatus("A valid Bugzilla account must be specified");
        } else {
            final Matcher matcher = emailPattern.matcher(text);
            if (!matcher.matches()) {
                updateStatus("Invalid Bugzilla account information");
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
        label.setText("Bugzilla Username:");
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
        label.setText("<email address>");
        label.setLayoutData(endData);
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.XMPPSConnectWizardPage_WIZARD_PAGE_PASSWORD);
        passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
        passwordText.setLayoutData(fillData);
        restoreCombo();
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

    String getConnectID() {
        final String bugzillaAccount = connectText.getText();
        if (!bugzillaAccount.endsWith(ECLIPSE_XMPP_HOST)) {
            return bugzillaAccount + "@" + ECLIPSE_XMPP_HOST;
        }
        return bugzillaAccount;
    }
}
