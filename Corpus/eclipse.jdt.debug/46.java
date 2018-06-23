/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.actions;

import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.IClasspathViewer;
import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * Abstract action that opens a dialog. Contains a prefix for dialog preference
 * settings.
 */
public abstract class OpenDialogAction extends RuntimeClasspathAction {

    /**
	 * Attribute name for the last path used to open a file/directory chooser
	 * dialog.
	 */
    //$NON-NLS-1$
    protected static final String LAST_PATH_SETTING = "LAST_PATH_SETTING";

    /**
	 * Dialog settings prefix/qualifier
	 */
    private String fPrefix = null;

    /**
	 * Constructs an action that opens a dialog.
	 */
    public  OpenDialogAction(String label, IClasspathViewer viewer, String dialogSettingsPrefix) {
        super(label, viewer);
        fPrefix = dialogSettingsPrefix;
    }

    /**
	 * Returns the prefix of the identifier used to store dialog settings for
	 * this action.
	 */
    protected String getDialogSettingsPrefix() {
        return fPrefix;
    }

    /**
	 * Returns the value of the dialog setting, associated with the given
	 * settingName, resolved by the dialog setting prefix associated with this
	 * action.
	 * 
	 * @param settingName unqualified setting name
	 * @return value or <code>null</code> if none
	 */
    protected String getDialogSetting(String settingName) {
        //$NON-NLS-1$
        return getDialogSettings().get(getDialogSettingsPrefix() + "." + settingName);
    }

    /**
	 * Sets the value of the dialog setting, associated with the given
	 * settingName, resolved by the dialog setting prefix associated with this
	 * action.
	 * 
	 * @param settingName unqualified setting name
	 * @return value or <code>null</code> if none
	 */
    protected void setDialogSetting(String settingName, String value) {
        //$NON-NLS-1$
        getDialogSettings().put(getDialogSettingsPrefix() + "." + settingName, value);
    }

    /**
	 * Returns this plug-in's dialog settings.
	 * 
	 * @return IDialogSettings
	 */
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = JDIDebugUIPlugin.getDefault().getDialogSettings();
        return settings;
    }

    @Override
    protected int getActionType() {
        return ADD;
    }
}
