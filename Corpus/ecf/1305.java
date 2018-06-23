/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.*;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

class SelectTaskDialog extends Dialog {

    private ListViewer viewer;

    private ITask task;

    private Object input;

     SelectTaskDialog(Shell shell) {
        super(shell);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Task Selection");
    }

    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    public Control createDialogArea(Composite parent) {
        parent = (Composite) super.createDialogArea(parent);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, true));
        viewer = new ListViewer(parent);
        viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {

            public String getText(Object element) {
                String summary = ((ITask) element).getSummary();
                if (summary.length() > 30) {
                    return //$NON-NLS-1$
                    summary.substring(0, 30) + //$NON-NLS-1$
                    "...";
                } else {
                    return summary;
                }
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            }
        });
        viewer.addOpenListener(new IOpenListener() {

            public void open(OpenEvent e) {
                okPressed();
            }
        });
        viewer.setInput(input);
        return parent;
    }

    protected Point getInitialSize() {
        Point point = super.getInitialSize();
        return new Point(point.x, 400);
    }

    protected void okPressed() {
        task = (ITask) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        super.okPressed();
    }

    void setInput(Object input) {
        this.input = input;
    }

    ITask getTask() {
        return task;
    }
}
