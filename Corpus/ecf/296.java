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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.internal.example.collab.CollabClient;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.ecf.internal.example.collab.start.AccountStart;
import org.eclipse.ecf.internal.example.collab.start.ConnectionDetails;
import org.eclipse.ecf.ui.dialogs.ContainerConnectErrorDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class URIClientConnectAction implements IWorkbenchWindowActionDelegate {

    protected CollabClient client = null;

    protected String containerType = null;

    protected String uri = null;

    protected String nickname = null;

    protected String password = null;

    protected IResource project = null;

    protected String projectName = null;

    protected boolean autoLogin = false;

    public  URIClientConnectAction() {
        client = CollabClient.getDefault();
    }

    public  URIClientConnectAction(String containerType, String uri, String nickname, String password, IResource project, boolean autoLoginFlag) {
        this();
        this.containerType = containerType;
        this.uri = uri;
        this.nickname = nickname;
        this.password = password;
        this.autoLogin = autoLoginFlag;
        setProject(project);
    }

    public void setProject(IResource project) {
        if (project == null)
            project = ResourcesPlugin.getWorkspace().getRoot();
        this.project = project;
        projectName = CollabClient.getNameForResource(project);
    }

    public class ClientConnectJob extends Job {

        public  ClientConnectJob(String name) {
            super(name);
        }

        public IStatus run(IProgressMonitor pm) {
            try {
                client.createAndConnectClient(containerType, uri, nickname, password, project);
                if (autoLogin)
                    saveAutoLoginInfo();
            } catch (final ContainerConnectException e) {
                removeAutoLoginInfo();
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        new ContainerConnectErrorDialog(null, uri, e.getStatus()).open();
                    }
                });
            } catch (final Exception e) {
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        new ContainerConnectErrorDialog(null, uri, e).open();
                    }
                });
            }
            return Status.OK_STATUS;
        }

        private void saveAutoLoginInfo() {
            final AccountStart as = new AccountStart();
            as.addConnectionDetails(new ConnectionDetails(containerType, uri, nickname, password));
            as.saveConnectionDetailsToPreferenceStore();
        }

        private void removeAutoLoginInfo() {
            final AccountStart as = new AccountStart();
            as.removeConnectionDetails(new ConnectionDetails(containerType, uri, nickname, password));
        }
    }

    public void run(IAction action) {
        new ClientConnectJob(NLS.bind(Messages.URIClientConnectAction_CONNECT_JOB_NAME, projectName)).schedule();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }
}
