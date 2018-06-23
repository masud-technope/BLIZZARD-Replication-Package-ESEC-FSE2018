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
package org.eclipse.ecf.internal.sync.ui.resources;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.docshare2.DocShare;
import org.eclipse.ecf.internal.sync.resources.core.FileChangeMessage;
import org.eclipse.ecf.internal.sync.resources.core.ResourcesShare;
import org.eclipse.ecf.internal.sync.resources.core.StartMessage;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

public class WorkbenchAwareResourcesShare extends ResourcesShare {

     WorkbenchAwareResourcesShare(ID containerID, IChannelContainerAdapter adapter) throws ECFException {
        super(containerID, adapter);
    }

    protected void handleStartMessage(final StartMessage msg) {
        final Display display = SyncResourcesUI.getDefault().getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                NotificationDialog dialog = new NotificationDialog(window.getShell(), msg.getFromId(), msg.getProjectName(), new IHyperlinkListener() {

                    public void linkExited(HyperlinkEvent e) {
                    }

                    public void linkEntered(HyperlinkEvent e) {
                    }

                    public void linkActivated(HyperlinkEvent e) {
                        boolean b = MessageDialog.openConfirm(window.getShell(), null, "Are you sure you wish to share the '" + msg.getProjectName() + "' project with " + msg.getFromId() + "?");
                        if (b) {
                            WorkbenchAwareResourcesShare.this.sendResponse(true, msg.getProjectName());
                            WorkbenchAwareResourcesShare.super.handleStartMessage(msg);
                        } else {
                            WorkbenchAwareResourcesShare.this.sendResponse(false, msg.getProjectName());
                        }
                    }
                });
                dialog.open();
            }
        });
    }

    protected void lock(IModelChange[] remoteChanges) {
        DocShare docShare = SyncResourcesUI.getDocShare(getContainerID());
        if (docShare != null) {
            List paths = new ArrayList(remoteChanges.length);
            for (int i = 0; i < remoteChanges.length; i++) {
                if (remoteChanges[i] instanceof FileChangeMessage) {
                    String path = ((FileChangeMessage) remoteChanges[i]).getPath();
                    paths.add(path);
                }
            }
            docShare.lock((String[]) paths.toArray(new String[paths.size()]));
        }
        super.lock(remoteChanges);
    }

    protected void unlock(IModelChange[] remoteChanges) {
        super.unlock(remoteChanges);
        DocShare docShare = SyncResourcesUI.getDocShare(getContainerID());
        if (docShare != null) {
            List paths = new ArrayList(remoteChanges.length);
            for (int i = 0; i < remoteChanges.length; i++) {
                if (remoteChanges[i] instanceof FileChangeMessage) {
                    String path = ((FileChangeMessage) remoteChanges[i]).getPath();
                    paths.add(path);
                }
            }
            docShare.unlock((String[]) paths.toArray(new String[paths.size()]));
        }
    }
}
