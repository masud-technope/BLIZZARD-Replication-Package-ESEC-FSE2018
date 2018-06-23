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
package org.eclipse.ecf.internal.example.collab.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientEntry;
import org.eclipse.ecf.internal.example.collab.CollabClient;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class SetSharedEditorSelectionAction implements IEditorActionDelegate {

    ITextEditor editor = null;

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        action.setEnabled(false);
        if (targetEditor instanceof ITextEditor) {
            // Got one
            editor = (ITextEditor) targetEditor;
            action.setEnabled(true);
        }
    }

    protected IFile getFileForPart(ITextEditor editor) {
        final IEditorInput input = editor.getEditorInput();
        if (input instanceof FileEditorInput) {
            final FileEditorInput fei = (FileEditorInput) input;
            return fei.getFile();
        }
        return null;
    }

    protected IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    protected ClientEntry isConnected(IResource res) {
        if (res == null)
            return null;
        final CollabClient client = CollabClient.getDefault();
        final ClientEntry entry = client.isConnected(res, CollabClient.GENERIC_CONTAINER_CLIENT_NAME);
        return entry;
    }

    public void run(IAction action) {
        if (editor == null)
            return;
        final ISelection s = editor.getSelectionProvider().getSelection();
        ITextSelection textSelection = null;
        if (s instanceof ITextSelection) {
            textSelection = (ITextSelection) s;
        }
        if (textSelection == null)
            return;
        final IFile file = getFileForPart(editor);
        if (file == null)
            return;
        final IProject project = file.getProject();
        final ClientEntry entry = isConnected(project.getWorkspace().getRoot());
        if (entry == null) {
            MessageDialog.openInformation(getWorkbench().getDisplay().getActiveShell(), Messages.SetSharedEditorSelectionAction_DIALOG_NOT_CONNECTED_TITLE, Messages.SetSharedEditorSelectionAction_DIALOG_NOT_CONNECTED_TEXT);
            return;
        }
        final EclipseCollabSharedObject collabsharedobject = entry.getSharedObject();
        if (collabsharedobject != null) {
            //$NON-NLS-1$
            collabsharedobject.sendOpenAndSelectForFile(null, project.getName() + "/" + file.getProjectRelativePath().toString(), textSelection.getOffset(), textSelection.getLength());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
