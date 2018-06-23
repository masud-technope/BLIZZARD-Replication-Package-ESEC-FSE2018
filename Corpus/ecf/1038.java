/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class JoinGroupWizardPage extends WizardPage {

    protected static final String CLASSNAME = JoinGroupWizardPage.class.getName();

    //$NON-NLS-1$
    protected static final String USER_NAME_SYSTEM_PROPERTY = "user.name";

    protected static final String PAGE_DESCRIPTION = Messages.JoinGroupWizardPage_COMPLETE_ACCOUNT_INFO;

    protected static final String JOINGROUP_FIELDNAME = Messages.JoinGroupWizardPage_GROUPID;

    protected static final String NICKNAME_FIELDNAME = Messages.JoinGroupWizardPage_NICKNAME;

    protected static final String ECF_DEFAULT_URL = Messages.JoinGroupWizardPage_DEFAULT_SERVER;

    protected static final String ECF_TEMPLATE_URL = Messages.JoinGroupWizardPage_TEMPLATE;

    protected static final String PAGE_TITLE = Messages.JoinGroupWizardPage_CONNECT_GENERIC_TITLE;

    //$NON-NLS-1$
    protected static final String DEFAULT_CLIENT = "ecf.generic.client";

    private static final String DIALOG_SETTINGS = CLASSNAME;

    private String connectID = null;

    public  JoinGroupWizardPage() {
        //$NON-NLS-1$
        super("wizardPage");
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_COLLABORATION_WIZARD));
    }

    public  JoinGroupWizardPage(String connectID) {
        //$NON-NLS-1$
        super("wizardPage");
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_COLLABORATION_WIZARD));
        this.connectID = connectID;
    }

    protected String template_url = ECF_TEMPLATE_URL;

    protected String default_url = ECF_DEFAULT_URL;

    protected Text nicknameText;

    protected Text joinGroupText;

    private Text text;

    private ContainerTypeDescription desc;

    //$NON-NLS-1$
    protected String urlPrefix = "";

    // private Button autoLogin = null;
    private final boolean autoLoginFlag = false;

    public boolean getAutoLoginFlag() {
        return autoLoginFlag;
    }

    protected void fillCombo() {
        desc = ContainerFactory.getDefault().getDescriptionByName(DEFAULT_CLIENT);
        if (desc != null) {
            final String name = desc.getName();
            final String description = desc.getDescription();
            //$NON-NLS-1$
            text.setText(description + " - " + name);
        }
    }

    public void createControl(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout(2, false);
        container.setLayout(gridLayout);
        setControl(container);
        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setText(Messages.JoinGroupWizardPage_PROTOCOL);
        text = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        final GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        text.setLayoutData(data);
        final Label groupIDLabel = new Label(container, SWT.NONE);
        groupIDLabel.setText(JOINGROUP_FIELDNAME);
        joinGroupText = new Text(container, SWT.BORDER);
        joinGroupText.setFocus();
        joinGroupText.setText(default_url);
        joinGroupText.setLayoutData(data);
        final Label exampleLabel = new Label(container, SWT.NONE);
        exampleLabel.setText(template_url);
        exampleLabel.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false, 2, 1));
        joinGroupText.setLayoutData(data);
        joinGroupText.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                joinGroupText.selectAll();
            }
        });
        final Label nicknameLabel = new Label(container, SWT.NONE);
        nicknameLabel.setLayoutData(new GridData());
        nicknameLabel.setText(NICKNAME_FIELDNAME);
        nicknameText = new Text(container, SWT.BORDER);
        nicknameText.setLayoutData(data);
        nicknameText.setText(System.getProperty(USER_NAME_SYSTEM_PROPERTY));
        nicknameText.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                nicknameText.selectAll();
            }
        });
        nicknameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                validateNicknameText();
            }
        });
        fillCombo();
        restoreDialogSettings();
        if (connectID != null) {
            joinGroupText.setText(connectID);
        }
        org.eclipse.jface.dialogs.Dialog.applyDialogFont(parent);
    }

    private void validateNicknameText() {
        final String nickname = nicknameText.getText();
        if (//$NON-NLS-1$
        "".equals(nickname)) {
            setErrorMessage(Messages.JoinGroupWizardPage_NICKNAME_CANNOT_BE_EMPTY);
            setPageComplete(false);
        } else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

    private void restoreDialogSettings() {
        final IDialogSettings dialogSettings = getDialogSettings();
        if (dialogSettings != null) {
            final IDialogSettings pageSettings = dialogSettings.getSection(DIALOG_SETTINGS);
            if (pageSettings != null) {
                String strVal = //$NON-NLS-1$
                pageSettings.get(//$NON-NLS-1$
                "url");
                if (strVal != null)
                    joinGroupText.setText(strVal);
                strVal = //$NON-NLS-1$
                pageSettings.get(//$NON-NLS-1$
                "nickname");
                if (strVal != null)
                    nicknameText.setText(strVal);
            }
        }
    }

    public void saveDialogSettings() {
        final IDialogSettings dialogSettings = getDialogSettings();
        if (dialogSettings != null) {
            IDialogSettings pageSettings = dialogSettings.getSection(DIALOG_SETTINGS);
            if (pageSettings == null)
                pageSettings = dialogSettings.addNewSection(DIALOG_SETTINGS);
            //$NON-NLS-1$
            pageSettings.put("url", joinGroupText.getText());
            //$NON-NLS-1$
            pageSettings.put("nickname", nicknameText.getText());
            //$NON-NLS-1$
            pageSettings.put("provider", text.getText());
        }
    }

    public String getJoinGroupText() {
        String textValue = joinGroupText.getText().trim();
        if (//$NON-NLS-1$
        !urlPrefix.equals("") && !textValue.startsWith(urlPrefix)) {
            textValue = urlPrefix + textValue;
        }
        return textValue;
    }

    public String getNicknameText() {
        if (nicknameText == null)
            return null;
        return nicknameText.getText().trim();
    }

    public String getContainerType() {
        return desc == null ? null : desc.getName();
    }
}
