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
package org.eclipse.ecf.internal.example.collab.start;

import java.util.Collection;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class AutoLoginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected Collection contents = null;

    protected ListViewer list = null;

    protected Button delete = null;

    public  AutoLoginPreferencePage() {
        super(GRID);
        setPreferenceStore(ClientPlugin.getDefault().getPreferenceStore());
    }

    public void init(IWorkbench workbench) {
    }

    protected void createFieldEditors() {
        addField(new //$NON-NLS-1$
        URLListFieldEditor(//$NON-NLS-1$
        "urilisteditor", Messages.AutoLoginPreferencePage_URILISTEDITOR_TEXT, getFieldEditorParent()));
    }
}
