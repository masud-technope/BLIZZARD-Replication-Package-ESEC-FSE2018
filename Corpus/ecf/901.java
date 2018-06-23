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
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ClientPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
    protected void performDefaults() {
        super.performDefaults();
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_USE_CHAT_WINDOW, false);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_DISPLAY_TIMESTAMP, true);
        // this.getPreferenceStore().setDefault(ClientPlugin.PREF_CHAT_FONT,
        // "");
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_CONFIRM_FILE_SEND, true);
        // this.getPreferenceStore().setDefault(ClientPlugin.PREF_CONFIRM_FILE_RECEIVE,
        // true);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_CONFIRM_REMOTE_VIEW, true);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_START_SERVER, false);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_REGISTER_SERVER, false);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_SHAREDEDITOR_PLAY_EVENTS_IMMEDIATELY, true);
        this.getPreferenceStore().setDefault(ClientPlugin.PREF_SHAREDEDITOR_ASK_RECEIVER, true);
    }

    public  ClientPreferencePage() {
        super(GRID);
        setPreferenceStore(ClientPlugin.getDefault().getPreferenceStore());
    }

    BooleanFieldEditor playImmediate = null;

    BooleanFieldEditor ask = null;

    Composite askParent = null;

    public void createFieldEditors() {
        addField(new BooleanFieldEditor(ClientPlugin.PREF_USE_CHAT_WINDOW, Messages.ClientPreferencePage_USE_CHAT_WINDOW_FIELD_TEXT, getFieldEditorParent()));
        addField(new BooleanFieldEditor(ClientPlugin.PREF_DISPLAY_TIMESTAMP, Messages.ClientPreferencePage_SHOW_TIME_FOR_CHAT_FIELD, getFieldEditorParent()));
        addField(new FontFieldEditor(ClientPlugin.PREF_CHAT_FONT, Messages.ClientPreferencePage_CHAT_WINDOW_FONT_FIELD, getFieldEditorParent()));
        // addField(new
        // BooleanFieldEditor(ClientPlugin.PREF_CONFIRM_FILE_RECEIVE, "Confirm
        // before receiving file.", getFieldEditorParent()));
        // addField(new SpacerFieldEditor(
        // getFieldEditorParent()));
        addField(new ColorFieldEditor(ClientPlugin.PREF_ME_TEXT_COLOR, Messages.ClientPreferencePage_CHAT_COLOR_FOR_ME_FIELD, getFieldEditorParent()));
        addField(new ColorFieldEditor(ClientPlugin.PREF_OTHER_TEXT_COLOR, Messages.ClientPreferencePage_CHAT_TEXT_COLOR_FOR_OTHER_FIELD, getFieldEditorParent()));
        addField(new ColorFieldEditor(ClientPlugin.PREF_SYSTEM_TEXT_COLOR, Messages.ClientPreferencePage_CHAT_COLOR_FOR_SYSTEM_FIELD, getFieldEditorParent()));
        addField(new SpacerFieldEditor(getFieldEditorParent()));
        playImmediate = new BooleanFieldEditor(ClientPlugin.PREF_SHAREDEDITOR_PLAY_EVENTS_IMMEDIATELY, Messages.ClientPreferencePage_PLAY_EDITOR_EVENTS_IMMEDIATELY, getFieldEditorParent());
        addField(playImmediate);
        askParent = getFieldEditorParent();
        ask = new BooleanFieldEditor(ClientPlugin.PREF_SHAREDEDITOR_ASK_RECEIVER, Messages.ClientPreferencePage_ASK_RECEIVER_FOR_PERMISSION, askParent);
        addField(ask);
        boolean val = getPreferenceStore().getBoolean(ClientPlugin.PREF_SHAREDEDITOR_PLAY_EVENTS_IMMEDIATELY);
        ask.setEnabled(val, askParent);
    }

    public void propertyChange(PropertyChangeEvent event) {
        Object field = event.getSource();
        if (field.equals(playImmediate)) {
            Boolean oldValue = (Boolean) event.getNewValue();
            ask.setEnabled(oldValue.booleanValue(), askParent);
        } else {
            super.propertyChange(event);
        }
    }

    public void init(IWorkbench workbench) {
    }
}
