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

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.ecf.internal.provider.xmpp.ui.Activator;
import org.eclipse.ecf.internal.provider.xmpp.ui.Messages;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class XMPPConnectWizardPage extends WizardPage {

    Combo connectText;

    Text passwordText;

    String usernameAtHost;

    Text serverText;

    // Note that this in many ways is more liberal than RCF5322
    static Pattern emailPattern = Pattern.compile(".+@[-a-z0-9]+(\\.[-a-z0-9]+)+(:[0-9]+)?");

    static Pattern serverPattern = Pattern.compile("([-a-z0-9]+(\\.[-a-z0-9]+)*)?");

     XMPPConnectWizardPage() {
        //$NON-NLS-1$
        super("");
        setTitle(Messages.XMPPConnectWizardPage_WIZARD_TITLE);
        setDescription(Messages.XMPPConnectWizardPage_WIZARD_DESCRIPTION);
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_CHAT_WIZARD));
        setPageComplete(false);
    }

     XMPPConnectWizardPage(String usernameAtHost) {
        this();
        this.usernameAtHost = usernameAtHost;
    }

    private void verify() {
        final String text = connectText.getText();
        if (//$NON-NLS-1$
        text.equals("")) {
            updateStatus(Messages.XMPPConnectWizardPage_WIZARD_STATUS);
        } else {
            final Matcher matcher = emailPattern.matcher(text);
            final Matcher hostMatcher = serverPattern.matcher(serverText.getText());
            if (!matcher.matches()) {
                updateStatus(Messages.XMPPConnectWizardPage_WIZARD_STATUS_INCOMPLETE);
            } else if (!hostMatcher.matches()) {
                updateStatus(Messages.XMPPConnectWizardPage_WIZARD_ALT_SERVER_INCOMPLETE);
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
        label.setText(Messages.XMPPConnectWizardPage_USERID_TEMPLATE);
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
        label.setText(Messages.XMPPConnectWizardPage_WIZARD_PASSWORD);
        passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
        passwordText.setLayoutData(fillData);
        restoreCombo();
        if (usernameAtHost != null) {
            connectText.setText(usernameAtHost);
            restorePassword(usernameAtHost);
            passwordText.setFocus();
        }
        if (connectText.getText().equals("")) {
            updateStatus(null);
            setPageComplete(false);
        } else if (isPageComplete())
            passwordText.setFocus();
        org.eclipse.jface.dialogs.Dialog.applyDialogFont(parent);
        setControl(parent);
    }

    protected void restorePassword(String username) {
        final PasswordCacheHelper pwStorage = new PasswordCacheHelper(username);
        final String pw = pwStorage.retrievePassword();
        if (pw != null) {
            passwordText.setText(pw);
            passwordText.setSelection(0, pw.length());
        }
    }

    String getConnectID() {
        String server = serverText.getText().trim();
        String userID = connectText.getText();
        // Check if the user typed in a hostname
        if ("".equals(server))
            return userID;
        else {
            // Now, did they add the port number part?
            int colonIdx = userID.lastIndexOf(':');
            if (// Yep, move the port to the end
            colonIdx > 0)
                return userID.substring(0, colonIdx) + ";" + server + userID.substring(colonIdx);
            else
                // Nah, nothing to move
                return userID + ";" + server;
        }
    }

    String getPassword() {
        return passwordText.getText();
    }

    protected void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    private static final String PAGE_SETTINGS = XMPPConnectWizardPage.class.getName();

    private static final int MAX_COMBO_VALUES = 40;

    private static final String COMBO_TEXT_KEY = "connectTextValue";

    private static final String COMBO_BOX_ITEMS_KEY = "comboValues";

    protected void saveComboText() {
        final IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null)
            pageSettings.put(COMBO_TEXT_KEY, connectText.getText());
    }

    protected void saveComboItems() {
        final IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null) {
            final String connectTextValue = connectText.getText();
            final List rawItems = Arrays.asList(connectText.getItems());
            // If existing text item is not in combo box then add it
            final List items = new ArrayList();
            if (!rawItems.contains(connectTextValue))
                items.add(connectTextValue);
            items.addAll(rawItems);
            int itemsToSaveLength = items.size();
            if (itemsToSaveLength > MAX_COMBO_VALUES)
                itemsToSaveLength = MAX_COMBO_VALUES;
            final String[] itemsToSave = new String[itemsToSaveLength];
            System.arraycopy(items.toArray(new String[] {}), 0, itemsToSave, 0, itemsToSaveLength);
            pageSettings.put(COMBO_BOX_ITEMS_KEY, itemsToSave);
        }
    }

    public IDialogSettings getDialogSettings() {
        return Activator.getDefault().getDialogSettings();
    }

    private IDialogSettings getPageSettings() {
        IDialogSettings pageSettings = null;
        final IDialogSettings dialogSettings = this.getDialogSettings();
        if (dialogSettings != null) {
            pageSettings = dialogSettings.getSection(PAGE_SETTINGS);
            if (pageSettings == null)
                pageSettings = dialogSettings.addNewSection(PAGE_SETTINGS);
            return pageSettings;
        }
        return null;
    }

    protected void restoreCombo() {
        final IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null) {
            final String[] items = pageSettings.getArray(COMBO_BOX_ITEMS_KEY);
            if (items != null)
                connectText.setItems(items);
            final String text = pageSettings.get(COMBO_TEXT_KEY);
            if (text != null)
                connectText.setText(text);
        }
    }
}
