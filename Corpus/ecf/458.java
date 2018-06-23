/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class WorkbenchAwareRemoteShare extends RemoteShare {

     WorkbenchAwareRemoteShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    protected boolean prompt(final ID fromId, String[] paths) {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < windows.length; i++) {
            final Shell shell = windows[i].getShell();
            if (shell != null && !shell.isDisposed()) {
                final boolean[] prompt = { false };
                final StringBuffer buffer = new StringBuffer(Text.DELIMITER);
                for (int j = 0; j < paths.length; j++) {
                    buffer.append(paths[j]).append(Text.DELIMITER);
                }
                PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                    public void run() {
                        prompt[0] = MessageDialog.openConfirm(shell, null, NLS.bind(Messages.WorkbenchAwareRemoteShare_PromptMessage, fromId.getName(), buffer.toString()));
                    }
                });
                return prompt[0];
            }
        }
        return false;
    }
}
