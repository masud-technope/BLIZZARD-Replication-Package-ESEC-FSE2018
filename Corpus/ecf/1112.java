/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.tutorial.Activator;
import org.eclipse.ecf.tutorial.scribbleshare.ScribbleClient;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class StartClientAction implements IWorkbenchWindowActionDelegate {

    IWorkbenchWindow window = null;

    public void run(IAction action) {
        ClientConnectJob job = new ClientConnectJob("ECF connect job");
        job.schedule();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public class ClientConnectJob extends Job {

        public  ClientConnectJob(String name) {
            super(name);
        }

        public IStatus run(IProgressMonitor pm) {
            try {
                createAndConnectClient();
                return new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), 15000, "Connected", null);
            } catch (Exception e) {
                return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), 15555, "Could not connect\n\n" + e.getMessage() + "\nSee stack trace in Error Log", e);
            }
        }
    }

    protected void createAndConnectClient() throws ECFException {
        // Client1 client = new Client1();
        // Client2 client = new Client2();
        // Client3 client = new Client4();
        // Client4 client = new Client4();
        // DsClient1 client = new DsClient1();
        // DsClient2 client = new DsClient2();
        ScribbleClient client = new ScribbleClient();
        client.createAndConnect();
    }
}
