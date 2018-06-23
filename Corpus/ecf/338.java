/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientEntry;
import org.eclipse.ecf.internal.example.collab.CollabClient;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class OpenSharedEditorAction implements IObjectActionDelegate {

    private IWorkbenchPart targetPart;

    private IFile file;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    protected ClientEntry isConnected(IResource res) {
        if (res == null)
            return null;
        final CollabClient client = CollabClient.getDefault();
        final ClientEntry entry = client.isConnected(res, CollabClient.GENERIC_CONTAINER_CLIENT_NAME);
        return entry;
    }

    public void run(IAction action) {
        if (file == null) {
            return;
        }
        final IProject project = file.getProject();
        final ClientEntry entry = isConnected(project);
        if (entry == null) {
            MessageDialog.openInformation(targetPart.getSite().getWorkbenchWindow().getShell(), Messages.OpenSharedEditorAction_DIALOG_NOT_CONNECTED_TITLE, NLS.bind(Messages.OpenSharedEditorAction_DIALOG_NOT_CONNECTED_TEXT, project.getName()));
            return;
        }
        final EclipseCollabSharedObject collabsharedobject = entry.getSharedObject();
        if (collabsharedobject != null) {
            collabsharedobject.sendLaunchEditorForFile(null, file.getProjectRelativePath().toString());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        action.setEnabled(false);
        file = null;
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection ss = (IStructuredSelection) selection;
            final Object obj = ss.getFirstElement();
            // now try to set relevant file
            if (obj instanceof IFile) {
                file = (IFile) obj;
                action.setEnabled(true);
            } else if (obj instanceof IAdaptable) {
                file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
                if (file != null) {
                    action.setEnabled(true);
                }
            }
        }
    }
}
