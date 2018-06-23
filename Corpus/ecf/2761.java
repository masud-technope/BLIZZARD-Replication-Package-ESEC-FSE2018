/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui.handlers;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.team.internal.ecf.core.TeamSynchronization;
import org.eclipse.team.internal.ecf.core.variants.RemoteResourceVariantTreeSubscriber;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.team.ui.synchronize.SyncInfoCompareInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.WorkbenchJob;

class CompareWithHandler extends AbstractRosterMenuHandler {

     CompareWithHandler(IRosterEntry entry) {
        super(entry);
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IRosterEntry selectedEntry = getRosterEntry();
        IRoster roster = selectedEntry.getRoster();
        final IUser remoteUser = roster.getUser();
        ID localId = remoteUser.getID();
        ID remoteId = selectedEntry.getUser().getID();
        IContainer container = (IContainer) roster.getPresenceContainerAdapter().getAdapter(IContainer.class);
        final IResource resource = getResource(event);
        if (resource == null) {
            MessageDialog.openInformation(HandlerUtil.getActiveShell(event), null, Messages.CompareWithHandler_FileNotSelectedError);
            return null;
        }
        RemoteShare share = TeamSynchronization.getShare(container.getID());
        final RemoteResourceVariantTreeSubscriber subscriber = new RemoteResourceVariantTreeSubscriber(share, localId, remoteId);
        Job job = new Job(Messages.CompareWithHandler_ResourceComparisonJobTitle) {

            protected IStatus run(IProgressMonitor monitor) {
                try {
                    openCompareEditor(subscriber.getSyncInfo(resource, monitor), remoteUser);
                    return Status.OK_STATUS;
                } catch (TeamException e) {
                    return e.getStatus();
                }
            }
        };
        job.setUser(true);
        job.schedule();
        return null;
    }

    void openCompareEditor(SyncInfo syncInfo, IUser user) {
        final SyncInfoCompareInput input = new SyncInfoCompareInput(NLS.bind(Messages.CompareWithHandler_CompareInputDescription, user.getNickname()), syncInfo);
        WorkbenchJob job = new WorkbenchJob(Messages.CompareWithHandler_CompareEditorWorkbenchJobTitle) {

            public IStatus runInUIThread(IProgressMonitor monitor) {
                CompareUI.openCompareEditor(input, true);
                return Status.OK_STATUS;
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private IResource getResource(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection iss = (IStructuredSelection) selection;
            Object element = iss.getFirstElement();
            if (element instanceof IResource) {
                return (IResource) element;
            } else if (element instanceof IAdaptable) {
                return (IResource) ((IAdaptable) element).getAdapter(IResource.class);
            } else {
                return null;
            }
        }
        IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
        return (IFile) editor.getEditorInput().getAdapter(IFile.class);
    }
}
