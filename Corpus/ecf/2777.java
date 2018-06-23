/****************************************************************************
 * Copyright (c) 2007, 2008 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.msn.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

final class MSNConnectWizardPage extends WizardPage {

    private Combo emailText;

    private Text passwordText;

    private String username;

    //$NON-NLS-1$
    private static Pattern emailPattern = Pattern.compile(".+@.+.[a-z]+");

     MSNConnectWizardPage() {
        super(MSNConnectWizardPage.class.getName());
        setTitle(Messages.MSNConnectWizardPage_Title);
        setDescription(Messages.MSNConnectWizardPage_WIZARD_PAGE_DESCRIPTION);
        setPageComplete(false);
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_CHAT_WIZARD));
    }

     MSNConnectWizardPage(String username) {
        this();
        this.username = username;
    }

    /**
	 * Verifies the user's input to the wizard. Optionally sets the password for
	 * the specified email if one has been stored and is recognized.
	 * 
	 * @param restorePassword
	 * 		<tt>true</tt> if the password field should be set if a password can be
	 * 		found
	 */
    private void verify(boolean restorePassword) {
        String email = emailText.getText().trim();
        if (//$NON-NLS-1$
        email.equals("")) {
            setErrorMessage(Messages.MSNConnectWizardPage_EmailAddressRequired);
        } else {
            Matcher matcher = emailPattern.matcher(email);
            if (!matcher.matches()) {
                setErrorMessage(Messages.MSNConnectWizardPage_EmailAddressInvalid);
            } else {
                if (restorePassword) {
                    restorePassword(email);
                }
                if (//$NON-NLS-1$
                passwordText.getText().equals("")) {
                    setErrorMessage(Messages.MSNConnectWizardPage_PasswordRequired);
                } else {
                    setErrorMessage(null);
                }
            }
        }
    }

    private void restorePassword(String username) {
        PasswordCacheHelper pwStorage = new PasswordCacheHelper(username);
        String pw = pwStorage.retrievePassword();
        if (pw != null) {
            passwordText.setText(pw);
        }
    }

    private void addListeners() {
        emailText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verify(true);
            }
        });
        emailText.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                verify(true);
            }

            public void widgetSelected(SelectionEvent e) {
                verify(true);
            }
        });
        passwordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                verify(false);
            }
        });
    }

    public void createControl(Composite parent) {
        parent = new Composite(parent, SWT.NONE);
        parent.setLayout(new GridLayout(2, false));
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        Label label = new Label(parent, SWT.LEFT);
        label.setText(Messages.MSNConnectWizardPage_EmailAddressLabel);
        emailText = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
        emailText.setLayoutData(data);
        label = new Label(parent, SWT.LEFT);
        label.setText(Messages.MSNConnectWizardPage_PasswordLabel);
        passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
        passwordText.setLayoutData(data);
        addListeners();
        restoreCombo();
        if (username != null) {
            emailText.setText(username);
            restorePassword(username);
            passwordText.setFocus();
        }
        Dialog.applyDialogFont(parent);
        setControl(parent);
    }

    String getEmail() {
        return emailText.getText();
    }

    String getPassword() {
        return passwordText.getText();
    }

    public void setErrorMessage(String message) {
        super.setErrorMessage(message);
        setPageComplete(message == null);
    }

    private static final String PAGE_SETTINGS = MSNConnectWizardPage.class.getName();

    private static final int MAX_COMBO_VALUES = 40;

    //$NON-NLS-1$
    private static final String COMBO_TEXT_KEY = "connectTextValue";

    //$NON-NLS-1$
    private static final String COMBO_BOX_ITEMS_KEY = "comboValues";

    protected void saveComboText() {
        IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null)
            pageSettings.put(COMBO_TEXT_KEY, emailText.getText());
    }

    protected void saveComboItems() {
        IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null) {
            String connectTextValue = emailText.getText();
            List rawItems = Arrays.asList(emailText.getItems());
            // If existing text item is not in combo box then add it
            List items = new ArrayList();
            if (!rawItems.contains(connectTextValue))
                items.add(connectTextValue);
            items.addAll(rawItems);
            int itemsToSaveLength = items.size();
            if (itemsToSaveLength > MAX_COMBO_VALUES)
                itemsToSaveLength = MAX_COMBO_VALUES;
            String[] itemsToSave = new String[itemsToSaveLength];
            System.arraycopy(items.toArray(new String[] {}), 0, itemsToSave, 0, itemsToSaveLength);
            pageSettings.put(COMBO_BOX_ITEMS_KEY, itemsToSave);
        }
    }

    public IDialogSettings getDialogSettings() {
        return Activator.getDefault().getDialogSettings();
    }

    private IDialogSettings getPageSettings() {
        IDialogSettings pageSettings = null;
        IDialogSettings dialogSettings = this.getDialogSettings();
        if (dialogSettings != null) {
            pageSettings = dialogSettings.getSection(PAGE_SETTINGS);
            if (pageSettings == null)
                pageSettings = dialogSettings.addNewSection(PAGE_SETTINGS);
            return pageSettings;
        }
        return null;
    }

    protected void restoreCombo() {
        IDialogSettings pageSettings = getPageSettings();
        if (pageSettings != null) {
            String[] items = pageSettings.getArray(COMBO_BOX_ITEMS_KEY);
            if (items != null)
                emailText.setItems(items);
            String text = pageSettings.get(COMBO_TEXT_KEY);
            if (text != null)
                emailText.setText(text);
        }
    }
}
