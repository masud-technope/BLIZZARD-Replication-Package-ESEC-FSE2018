/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.sync.ui.resources.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ecf.internal.sync.resources.core.SyncResourcesCore;
import org.eclipse.ecf.sync.resources.core.preferences.PreferenceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ResourcesSynchronizationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final int LOCAL_RESOURCE_ADDITION_INDEX = 0;

    private static final int LOCAL_RESOURCE_CHANGE_INDEX = LOCAL_RESOURCE_ADDITION_INDEX + 1;

    private static final int LOCAL_RESOURCE_DELETION_INDEX = LOCAL_RESOURCE_CHANGE_INDEX + 1;

    private static final int REMOTE_RESOURCE_ADDITION_INDEX = LOCAL_RESOURCE_DELETION_INDEX + 1;

    private static final int REMOTE_RESOURCE_CHANGE_INDEX = REMOTE_RESOURCE_ADDITION_INDEX + 1;

    private static final int REMOTE_RESOURCE_DELETION_INDEX = REMOTE_RESOURCE_CHANGE_INDEX + 1;

    private List optionsList;

    private Button changePropagationButton;

    private Button ignoreConflictButton;

    private Button ignoreButton;

    private Text descriptionText;

    private int index = LOCAL_RESOURCE_ADDITION_INDEX;

    private boolean warnedLocal = false;

    private boolean warnedRemote = false;

    private final int[] originals = new int[6];

    private Preferences preferences;

    public  ResourcesSynchronizationPreferencePage() {
        setDescription("Modify the behaviour that the workspace should take in response to resource changes for shared projects. Please note that distributed changes may be slow to appear on remote clients due to latency issues. Clients are also free to ignore remote changes outright.");
        preferences = new InstanceScope().getNode(SyncResourcesCore.PLUGIN_ID);
        originals[0] = SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION);
        originals[1] = SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE);
        originals[2] = SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_DELETION);
        originals[3] = SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION);
        originals[4] = SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE);
        originals[5] = SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_DELETION);
    }

    public void init(IWorkbench workbench) {
    // nothing to do
    }

    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        optionsList = new List(composite, SWT.SINGLE | SWT.BORDER);
        optionsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 3));
        optionsList.setItems(new String[] { "Local resource addition", "Local resource change", "Local resource deletion", "Remote resource addition", "Remote resource change", "Remote resource deletion" });
        optionsList.select(LOCAL_RESOURCE_ADDITION_INDEX);
        changePropagationButton = new Button(composite, SWT.RADIO);
        changePropagationButton.setText("Distribute local changes");
        changePropagationButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        ignoreConflictButton = new Button(composite, SWT.RADIO);
        ignoreConflictButton.setText("Ignore conflicting remote changes");
        GridData data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.exclude = true;
        ignoreConflictButton.setLayoutData(data);
        ignoreConflictButton.setVisible(false);
        ignoreButton = new Button(composite, SWT.RADIO);
        ignoreButton.setText("Ignore local changes");
        ignoreButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
        Label label = new Label(composite, SWT.LEAD);
        label.setText("Description:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
        descriptionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
        addListeners();
        Dialog.applyDialogFont(composite);
        GC gc = new GC(composite);
        gc.setFont(composite.getFont());
        int height = gc.getFontMetrics().getHeight();
        gc.dispose();
        data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        data.heightHint = height * 3;
        descriptionText.setLayoutData(data);
        resetButtonSelection();
        return composite;
    }

    private void addListeners() {
        optionsList.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                index = optionsList.getSelectionIndex();
                switch(index) {
                    case LOCAL_RESOURCE_ADDITION_INDEX:
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION));
                        break;
                    case LOCAL_RESOURCE_CHANGE_INDEX:
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE));
                        break;
                    case LOCAL_RESOURCE_DELETION_INDEX:
                        changePropagationButton.setText("Distribute local changes");
                        ignoreButton.setText("Ignore local changes");
                        descriptionText.setText("");
                        ((GridData) ignoreConflictButton.getLayoutData()).exclude = true;
                        ignoreConflictButton.setVisible(false);
                        ignoreConflictButton.getParent().layout(false, false);
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_DELETION));
                        break;
                    case REMOTE_RESOURCE_ADDITION_INDEX:
                        descriptionText.setText("It is considered a conflict if a resource is added on a remote client when it already exists locally. Choosing to commit this change will cause the file to be overridden with the remote resource's contents.");
                        changePropagationButton.setText("Commit remote changes");
                        ignoreButton.setText("Ignore remote changes");
                        ((GridData) ignoreConflictButton.getLayoutData()).exclude = false;
                        ignoreConflictButton.setVisible(true);
                        ignoreConflictButton.getParent().layout(false, false);
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION));
                        break;
                    case REMOTE_RESOURCE_CHANGE_INDEX:
                        descriptionText.setText("It is considered a conflict if a resource is changed on a remote client when it does not exist locally. Choosing to commit this change will cause the resource to be created with the remote resource's contents.");
                        changePropagationButton.setText("Commit remote changes");
                        ignoreButton.setText("Ignore remote changes");
                        ((GridData) ignoreConflictButton.getLayoutData()).exclude = false;
                        ignoreConflictButton.setVisible(true);
                        ignoreConflictButton.getParent().layout(false, false);
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE));
                        break;
                    case REMOTE_RESOURCE_DELETION_INDEX:
                        changePropagationButton.setText("Commit remote changes");
                        descriptionText.setText("");
                        ignoreButton.setText("Ignore remote changes");
                        ((GridData) ignoreConflictButton.getLayoutData()).exclude = true;
                        ignoreConflictButton.setVisible(false);
                        ignoreConflictButton.getParent().layout(false, false);
                        updateRadioButtons(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_DELETION));
                        break;
                }
            }
        });
        changePropagationButton.addSelectionListener(new SelectionListenerImpl(changePropagationButton, PreferenceConstants.COMMIT_VALUE));
        ignoreConflictButton.addSelectionListener(new SelectionListenerImpl(ignoreConflictButton, PreferenceConstants.IGNORE_CONFLICTS_VALUE));
        ignoreButton.addSelectionListener(new SelectionListenerImpl(ignoreButton, PreferenceConstants.IGNORE_VALUE));
    }

    private void updateRadioButtons(int value) {
        switch(value) {
            case PreferenceConstants.COMMIT_VALUE:
                changePropagationButton.setSelection(true);
                ignoreConflictButton.setSelection(false);
                ignoreButton.setSelection(false);
                break;
            case PreferenceConstants.IGNORE_CONFLICTS_VALUE:
                changePropagationButton.setSelection(false);
                ignoreConflictButton.setSelection(true);
                ignoreButton.setSelection(false);
                break;
            case PreferenceConstants.IGNORE_VALUE:
                changePropagationButton.setSelection(false);
                ignoreConflictButton.setSelection(false);
                ignoreButton.setSelection(true);
                break;
        }
    }

    private boolean isIgnoring(String key) {
        return SyncResourcesCore.getInt(key) == PreferenceConstants.IGNORE_VALUE;
    }

    private boolean isIgnoringTwoLocalChanges() {
        if (isIgnoring(PreferenceConstants.LOCAL_RESOURCE_ADDITION)) {
            return isIgnoring(PreferenceConstants.LOCAL_RESOURCE_CHANGE) || isIgnoring(PreferenceConstants.LOCAL_RESOURCE_DELETION);
        } else {
            return isIgnoring(PreferenceConstants.LOCAL_RESOURCE_CHANGE) && isIgnoring(PreferenceConstants.LOCAL_RESOURCE_DELETION);
        }
    }

    private boolean isIgnoringTwoRemoteChanges() {
        if (isIgnoring(PreferenceConstants.REMOTE_RESOURCE_ADDITION)) {
            return isIgnoring(PreferenceConstants.REMOTE_RESOURCE_CHANGE) || isIgnoring(PreferenceConstants.REMOTE_RESOURCE_DELETION);
        } else {
            return isIgnoring(PreferenceConstants.REMOTE_RESOURCE_CHANGE) && isIgnoring(PreferenceConstants.REMOTE_RESOURCE_DELETION);
        }
    }

    private void setPreferenceValue(int value) {
        switch(index) {
            case LOCAL_RESOURCE_ADDITION_INDEX:
                preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION, value);
                break;
            case LOCAL_RESOURCE_CHANGE_INDEX:
                preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE, value);
                break;
            case LOCAL_RESOURCE_DELETION_INDEX:
                preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_DELETION, value);
                break;
            case REMOTE_RESOURCE_ADDITION_INDEX:
                preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION, value);
                break;
            case REMOTE_RESOURCE_CHANGE_INDEX:
                preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE, value);
                break;
            case REMOTE_RESOURCE_DELETION_INDEX:
                preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_DELETION, value);
                break;
        }
    }

    private void resetButtonSelection() {
        switch(index) {
            case LOCAL_RESOURCE_ADDITION_INDEX:
                resetButtonSelection(PreferenceConstants.LOCAL_RESOURCE_ADDITION);
                break;
            case LOCAL_RESOURCE_CHANGE_INDEX:
                resetButtonSelection(PreferenceConstants.LOCAL_RESOURCE_CHANGE);
                break;
            case LOCAL_RESOURCE_DELETION_INDEX:
                resetButtonSelection(PreferenceConstants.LOCAL_RESOURCE_DELETION);
                break;
            case REMOTE_RESOURCE_ADDITION_INDEX:
                resetButtonSelection(PreferenceConstants.REMOTE_RESOURCE_ADDITION);
                break;
            case REMOTE_RESOURCE_CHANGE_INDEX:
                resetButtonSelection(PreferenceConstants.REMOTE_RESOURCE_CHANGE);
                break;
            case REMOTE_RESOURCE_DELETION_INDEX:
                resetButtonSelection(PreferenceConstants.REMOTE_RESOURCE_DELETION);
                break;
        }
    }

    private void resetButtonSelection(String key) {
        switch(SyncResourcesCore.getInt(key)) {
            case PreferenceConstants.COMMIT_VALUE:
                changePropagationButton.setSelection(true);
                ignoreConflictButton.setSelection(false);
                ignoreButton.setSelection(false);
                break;
            case PreferenceConstants.IGNORE_CONFLICTS_VALUE:
                changePropagationButton.setSelection(false);
                ignoreConflictButton.setSelection(true);
                ignoreButton.setSelection(false);
                break;
            case PreferenceConstants.IGNORE_VALUE:
                changePropagationButton.setSelection(false);
                ignoreConflictButton.setSelection(false);
                ignoreButton.setSelection(true);
                break;
        }
    }

    public boolean performCancel() {
        if (super.performCancel()) {
            preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION, originals[0]);
            preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE, originals[1]);
            preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_DELETION, originals[2]);
            preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION, originals[3]);
            preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE, originals[4]);
            preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_DELETION, originals[5]);
            return true;
        }
        return false;
    }

    protected void performDefaults() {
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION, SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION));
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE, SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE));
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_DELETION, SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_DELETION));
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION, SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION));
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE, SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE));
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_DELETION, SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_DELETION));
        resetButtonSelection();
        super.performDefaults();
    }

    public boolean performOk() {
        if (super.performOk()) {
            try {
                preferences.flush();
            } catch (BackingStoreException e) {
                e.printStackTrace();
                MessageDialog.openError(getShell(), null, "Could not persist settings");
                return false;
            }
            return true;
        }
        return false;
    }

    class SelectionListenerImpl extends SelectionAdapter {

        private final Button button;

        private final int buttonIndex;

         SelectionListenerImpl(Button button, int buttonIndex) {
            this.button = button;
            this.buttonIndex = buttonIndex;
        }

        public void widgetSelected(SelectionEvent e) {
            if (!button.getSelection()) {
                // are not concerned with that button's state
                return;
            }
            // case
            switch(index) {
                case LOCAL_RESOURCE_ADDITION_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION) == buttonIndex) {
                        return;
                    }
                    break;
                case LOCAL_RESOURCE_CHANGE_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE) == buttonIndex) {
                        return;
                    }
                    break;
                case LOCAL_RESOURCE_DELETION_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.LOCAL_RESOURCE_DELETION) == buttonIndex) {
                        return;
                    }
                    break;
                case REMOTE_RESOURCE_ADDITION_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION) == buttonIndex) {
                        return;
                    }
                    break;
                case REMOTE_RESOURCE_CHANGE_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE) == buttonIndex) {
                        return;
                    }
                    break;
                case REMOTE_RESOURCE_DELETION_INDEX:
                    if (SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_DELETION) == buttonIndex) {
                        return;
                    }
                    break;
            }
            if (buttonIndex == PreferenceConstants.IGNORE_VALUE) {
                boolean commit = true;
                if (!warnedLocal && isIgnoringTwoLocalChanges()) {
                    commit = MessageDialog.openConfirm(ignoreButton.getShell(), null, "Are you sure that you do not wish to distribute any of your local changes to remote clients?");
                    if (commit) {
                        warnedLocal = true;
                    }
                } else if (!warnedRemote && isIgnoringTwoRemoteChanges()) {
                    commit = MessageDialog.openConfirm(ignoreButton.getShell(), null, "Are you sure that you do not wish to commit any remote changes from remote clients?");
                    if (commit) {
                        warnedRemote = true;
                    }
                }
                if (commit) {
                    setPreferenceValue(buttonIndex);
                } else {
                    resetButtonSelection();
                }
            } else {
                setPreferenceValue(buttonIndex);
            }
        }
    }
}
