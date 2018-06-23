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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ReceiveAuthorizeRequestDialog extends Dialog {

    //$NON-NLS-1$
    private String targetName = "target";

    //$NON-NLS-1$
    private String requesterName = "requester";

    private Label target_username;

    private Label requester_userid;

    public static final int REFUSE_ID = IDialogConstants.CLIENT_ID + 3;

    public static final int AUTHORIZE_ID = IDialogConstants.CLIENT_ID + 1;

    int buttonPressed = 0;

    public  ReceiveAuthorizeRequestDialog(Shell parentShell) {
        super(parentShell);
    }

    public  ReceiveAuthorizeRequestDialog(Shell parentShell, String fromName, String toName) {
        super(parentShell);
        this.requesterName = fromName;
        this.targetName = toName;
    }

    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final Composite composite = new Composite(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.numColumns = 4;
        composite.setLayout(gridLayout);
        final GridData gridData_1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL);
        gridData_1.heightHint = 52;
        gridData_1.verticalSpan = 2;
        composite.setLayoutData(gridData_1);
        requester_userid = new Label(composite, SWT.NONE);
        final GridData gridData_2 = new GridData(GridData.GRAB_VERTICAL);
        requester_userid.setLayoutData(gridData_2);
        requester_userid.setText(requesterName);
        final Label label_4 = new Label(composite, SWT.NONE);
        label_4.setText(Messages.ReceiveAuthorizeRequestDialog_WOULD_LIKE_TO_ADD);
        target_username = new Label(composite, SWT.NONE);
        target_username.setLayoutData(new GridData());
        target_username.setText(targetName);
        final Label label_5 = new Label(composite, SWT.NONE);
        label_5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        label_5.setText(Messages.ReceiveAuthorizeRequestDialog_TO_BUDDY_LIST);
        final Composite composite_1 = new Composite(container, SWT.NONE);
        composite_1.setLayout(new GridLayout());
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 73;
        composite_1.setLayoutData(gridData);
        final Label label = new Label(composite_1, SWT.WRAP);
        final GridData gridData_4 = new GridData();
        gridData_4.heightHint = 56;
        gridData_4.widthHint = 462;
        label.setLayoutData(gridData_4);
        label.setText(Messages.ReceiveAuthorizeRequestDialog_MESSAGE);
        final Label label_3 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gridData_3 = new GridData(GridData.FILL_HORIZONTAL);
        gridData_3.heightHint = 6;
        gridData_3.widthHint = 469;
        label_3.setLayoutData(gridData_3);
        //
        applyDialogFont(container);
        return container;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, AUTHORIZE_ID, Messages.ReceiveAuthorizeRequestDialog_BUTTON_AUTHORIZE_ONLY, false);
        createButton(parent, REFUSE_ID, Messages.ReceiveAuthorizeRequestDialog_BUTTON_REFUSE, true);
    }

    protected Point getInitialSize() {
        return new Point(486, 223);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ReceiveAuthorizeRequestDialog_AUTH_REQUEST_FROM + requesterName);
    }

    public int getButtonPressed() {
        return buttonPressed;
    }

    protected void buttonPressed(int button) {
        buttonPressed = button;
        this.close();
    }
}
