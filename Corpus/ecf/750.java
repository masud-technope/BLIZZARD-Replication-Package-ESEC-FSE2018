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
package org.eclipse.ecf.internal.presence.ui.dialogs;

import org.eclipse.ecf.internal.presence.ui.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChangePasswordDialog extends Dialog {

    private Text password1;

    private Text password2;

    private int result = Window.CANCEL;

    //$NON-NLS-1$
    private String pass1 = "";

    private String pass2 = Messages.ChangePasswordDialog_1;

    private Button okButton;

    private String accountName;

    public  ChangePasswordDialog(Shell parentShell, String accountName) {
        super(parentShell);
        this.accountName = accountName;
    }

    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        container.setLayout(gridLayout);
        final Composite composite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        composite.setLayout(gridLayout_2);
        Label l = new Label(composite, SWT.NONE);
        l.setText(accountName);
        new Label(composite, SWT.NONE);
        final Label label_3 = new Label(composite, SWT.NONE);
        label_3.setText(Messages.ChangePasswordDialog_NEW_PASSWORD_LABEL);
        password1 = new Text(composite, SWT.BORDER);
        password1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        password1.setEchoChar('*');
        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setText(Messages.ChangePasswordDialog_REENTER_PASSWORD_LABEL);
        password2 = new Text(composite, SWT.BORDER);
        final GridData gridData_1 = new GridData(GridData.FILL_HORIZONTAL);
        gridData_1.widthHint = 192;
        password2.setLayoutData(gridData_1);
        password2.setEchoChar('*');
        //
        applyDialogFont(container);
        return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
        okButton = getButton(IDialogConstants.OK_ID);
        okButton.setEnabled(true);
    }

    public String getNewPassword() {
        return pass1;
    }

    protected Point getInitialSize() {
        return new Point(330, 157);
    }

    public void buttonPressed(int button) {
        result = button;
        if (button == Window.OK) {
            pass1 = password1.getText();
            pass2 = password2.getText();
            if (!pass1.equals(pass2)) {
                // message box that passwords do not match
                MessageDialog.openError(getShell(), Messages.ChangePasswordDialog_PASSWORDS_NO_MATCH_TITLE, Messages.ChangePasswordDialog_PASSWORDS_NO_MATCH_MESSAGE);
                //$NON-NLS-1$
                password1.setText(//$NON-NLS-1$
                "");
                //$NON-NLS-1$
                password2.setText(//$NON-NLS-1$
                "");
                password1.setFocus();
                return;
            }
        }
        close();
    }

    public int getResult() {
        return result;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ChangePasswordDialog_CHANGE_PASSWORD);
    }
}
