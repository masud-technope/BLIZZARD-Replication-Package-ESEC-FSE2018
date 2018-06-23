/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import org.eclipse.core.commands.*;
import org.eclipse.ecf.internal.mylyn.ui.CompoundContextActivationContributionItem.ActivateTaskAction;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ActivateReceivedContextHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveShell(event);
        if (shell != null) {
            open(shell);
        }
        return null;
    }

    static void open(Shell shell) {
        ElementListSelectionDialog elsd = new ElementListSelectionDialog(shell, new LabelProvider() {

            public String getText(Object element) {
                String summary = ((ITask) element).getSummary();
                return summary;
            }
        });
        elsd.setElements(CompoundContextActivationContributionItem.tasks.toArray());
        if (Window.OK == elsd.open()) {
            ActivateTaskAction action = new CompoundContextActivationContributionItem.ActivateTaskAction();
            action.setShell(shell);
            action.setTask((ITask) elsd.getFirstResult());
            action.run();
        }
    }
}
