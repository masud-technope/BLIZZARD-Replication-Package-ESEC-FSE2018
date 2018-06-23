/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Abner Ballardo <modlost@modlost.net> - bug 192756
 ******************************************************************************/
package org.eclipse.ecf.internal.presence.ui.preferences;

import org.eclipse.ecf.internal.presence.ui.Activator;
import org.eclipse.ecf.internal.presence.ui.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ChatRoomPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public  ChatRoomPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(PreferenceConstants.CHATROOM_SHOW_USER_PRESENCE, Messages.ChatRoomPreferencePage_CHATROOM_SHOW_USER_PRESENCE_TEXT, getFieldEditorParent()));
        addField(new BooleanFieldEditor(PreferenceConstants.PREFERENCES_SCROLLONINPUT, Messages.ChatRoomPreferencePage_SCROLL_OUTPUT_ON_INPUT, getFieldEditorParent()));
    }

    /*
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
    public void init(IWorkbench workbench) {
    // do nothing
    }
}
