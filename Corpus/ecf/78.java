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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.team.internal.ecf.core.TeamSynchronization;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.team.internal.ecf.ui.subscriber.RemoteSubscriberParticipant;
import org.eclipse.team.internal.ecf.ui.wizards.RemotePeerSynchronizeWizard;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.HandlerUtil;

class SynchronizeWithHandler extends AbstractRosterMenuHandler {

     SynchronizeWithHandler(IRosterEntry entry) {
        super(entry);
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IRosterEntry selectedEntry = getRosterEntry();
        IRoster roster = selectedEntry.getRoster();
        final IUser remoteUser = roster.getUser();
        final ID localId = remoteUser.getID();
        final ID remoteId = selectedEntry.getUser().getID();
        IContainer container = (IContainer) roster.getPresenceContainerAdapter().getAdapter(IContainer.class);
        final IResource[] resources = getResources(event);
        final RemoteShare share = TeamSynchronization.getShare(container.getID());
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        final IWorkbenchPartSite site = part == null ? null : part.getSite();
        final Shell shell = HandlerUtil.getActiveShellChecked(event);
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
        final boolean[] response = { true };
        IRunnableWithProgress runnable = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                if (resources.length == 1) {
                    monitor.beginTask(NLS.bind(Messages.SynchronizeWithHandler_SynchronizeResourceTaskName, resources[0].getName()), IProgressMonitor.UNKNOWN);
                } else {
                    monitor.beginTask(Messages.SynchronizeWithHandler_SynchronizeResourcesTaskName, IProgressMonitor.UNKNOWN);
                }
                try {
                    if (share.sendShareRequest(localId, remoteId, resources, monitor)) {
                        scheduleRefreshJob(share, localId, remoteId, resources, remoteUser, site);
                    } else {
                        response[0] = false;
                    }
                } catch (ECFException e) {
                    throw new InvocationTargetException(e);
                } catch (OperationCanceledException e) {
                    if (!monitor.isCanceled()) {
                        throw e;
                    }
                }
            }
        };
        try {
            dialog.run(true, true, runnable);
            if (!response[0]) {
                MessageDialog.openInformation(shell, null, Messages.SynchronizeWithHandler_SynchronizeRequestDenial);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ECFException) {
                MessageDialog.openError(shell, null, Messages.SynchronizeWithHandler_SynchronizeRequestError);
            }
            TeamSynchronization.log("Failed to contact remote peer", cause);
        } catch (InterruptedException e) {
            Thread.interrupted();
            MessageDialog.openError(shell, null, Messages.SynchronizeWithHandler_SynchronizeRequestInterrupted);
            TeamSynchronization.log("Synchronization request operation was interrupted", e);
        }
        return null;
    }

    void scheduleRefreshJob(RemoteShare share, ID localId, ID remoteId, IResource[] resources, IUser remoteUser, IWorkbenchPartSite site) {
        RemoteSubscriberParticipant participant = RemotePeerSynchronizeWizard.getSubscriberParticipant(share, localId, remoteId);
        participant.setResources(resources);
        TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] { participant });
        if (resources.length == 1) {
            participant.refresh(resources, NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationTaskName, remoteUser.getNickname()), NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationResourceDescription, resources[0].getName(), remoteUser.getNickname()), site);
        } else {
            participant.refresh(resources, NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationTaskName, remoteUser.getNickname()), NLS.bind(Messages.SynchronizeWithHandler_RemoteSynchronizationResourcesDescription, remoteUser.getNickname()), site);
        }
    }

    public IResource[] getResources(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection) {
            List workspaceSelection = new ArrayList(((IStructuredSelection) selection).toList());
            for (int i = 0; i < workspaceSelection.size(); i++) {
                IResource resourceOne = (IResource) ((IAdaptable) workspaceSelection.get(i)).getAdapter(IResource.class);
                IPath pathOne = resourceOne.getFullPath();
                for (int j = i + 1; j < workspaceSelection.size(); j++) {
                    IResource resourceTwo = (IResource) ((IAdaptable) workspaceSelection.get(j)).getAdapter(IResource.class);
                    IPath pathTwo = resourceTwo.getFullPath();
                    if (pathOne.isPrefixOf(pathTwo)) {
                        workspaceSelection.remove(j);
                        i--;
                        break;
                    } else if (pathTwo.isPrefixOf(pathOne)) {
                        workspaceSelection.remove(i);
                        i--;
                        break;
                    }
                }
            }
            IResource[] resources = new IResource[workspaceSelection.size()];
            for (int i = 0; i < resources.length; i++) {
                resources[i] = (IResource) ((IAdaptable) workspaceSelection.get(i)).getAdapter(IResource.class);
            }
            return resources;
        }
        IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        return file == null ? new IResource[0] : new IResource[] { file };
    }
}
