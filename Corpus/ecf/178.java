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
package org.eclipse.ecf.sync.ui.resources;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.sync.resources.core.ResourcesShare;
import org.eclipse.ecf.internal.sync.resources.core.SyncResourcesCore;
import org.eclipse.ecf.internal.sync.ui.resources.SyncResourcesUI;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

class ResourcesShareHandler extends AbstractRosterMenuHandler {

     ResourcesShareHandler(IRosterEntry entry) {
        super(entry);
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection iss = (IStructuredSelection) selection;
        Object o = iss.getFirstElement();
        IResource resource = null;
        if (o instanceof IAdaptable) {
            resource = (IResource) ((IAdaptable) o).getAdapter(IResource.class);
        } else {
            return null;
        }
        IPresenceContainerAdapter ipca = getRosterEntry().getRoster().getPresenceContainerAdapter();
        IContainer container = (IContainer) ipca.getAdapter(IContainer.class);
        ResourcesShare sender = SyncResourcesCore.getResourcesShare(container.getID());
        IProject project = resource.getProject();
        String projectName = project.getName();
        if (!SyncResourcesCore.isSharing(projectName)) {
            run(HandlerUtil.getActiveShellChecked(event), sender, projectName);
        } else {
            sender.stopSharing(projectName);
        }
        return null;
    }

    private void run(Shell shell, final ResourcesShare share, final String projectName) {
        final boolean[] ret = { false };
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
            dialog.open();
            dialog.run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Sharing " + projectName, IProgressMonitor.UNKNOWN);
                        monitor.subTask("Sending request...");
                        share.startShare(getRosterEntry().getRoster().getUser().getID(), getRosterEntry().getUser().getID(), projectName);
                        monitor.subTask("Waiting for acknowledgement...");
                        while (true) {
                            if (monitor.isCanceled()) {
                                throw new InterruptedException();
                            }
                            Thread.sleep(50);
                            Boolean response = share.getResponse();
                            if (response != null) {
                                ret[0] = response.booleanValue();
                                return;
                            }
                        }
                    } catch (ECFException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException e) {
            IStatus status = new Status(IStatus.ERROR, SyncResourcesUI.PLUGIN_ID, "Could not send share request", e.getCause());
            StatusManager.getManager().handle(status);
            SyncResourcesUI.log(status);
        } catch (InterruptedException e) {
            share.stopSharing(projectName);
            return;
        }
        if (ret[0]) {
            IRosterManager manager = getRosterEntry().getRoster().getPresenceContainerAdapter().getRosterManager();
            manager.addRosterListener(new RosterListener(share, projectName, getRosterEntry()));
        } else {
            MessageDialog.openInformation(shell, null, "Sharing request denied.");
        }
    }
}
