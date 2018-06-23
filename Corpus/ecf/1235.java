/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.ui.actions;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.ecf.internal.ui.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.osgi.util.NLS;

/**
 * Action class for invoking {@link IContainer#connect(ID, IConnectContext)} as
 * separate job.
 */
public class AsynchContainerConnectAction extends SynchContainerConnectAction {

    public  AsynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext, IExceptionHandler exceptionHandler, Runnable successBlock) {
        super(container, targetID, connectContext, exceptionHandler, successBlock);
    }

    public  AsynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext, IExceptionHandler exceptionHandler) {
        super(container, targetID, connectContext, exceptionHandler);
    }

    public  AsynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext) {
        this(container, targetID, connectContext, null);
    }

    public void dispose() {
        this.container = null;
        this.targetID = null;
        this.connectContext = null;
        this.window = null;
    }

    protected IStatus handleException(Throwable e) {
        if (exceptionHandler != null)
            return exceptionHandler.handleException(e);
        else if (e instanceof ECFException) {
            return new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, new IStatus[] { getStatusForECFException((ECFException) e) }, //$NON-NLS-1$
            NLS.bind(//$NON-NLS-1$
            "Connect to {0} failed.", targetID.getName()), null);
        } else
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), null);
    }

    protected IStatus getStatusForECFException(ECFException exception) {
        IStatus status = exception.getStatus();
        Throwable cause = status.getException();
        if (cause instanceof ECFException) {
            return getStatusForECFException((ECFException) cause);
        }
        return status;
    }

    class ContainerMutex implements ISchedulingRule {

        IContainer c;

        public  ContainerMutex(IContainer container) {
            this.c = container;
        }

        protected boolean isSameContainer(IContainer other) {
            if (other != null && c.getID().equals(other.getID()))
                return true;
            return false;
        }

        protected IContainer getContainer() {
            return AsynchContainerConnectAction.this.getContainer();
        }

        public boolean isConflicting(ISchedulingRule rule) {
            if (rule == this)
                return true;
            else if (rule instanceof ContainerMutex && isSameContainer(((ContainerMutex) rule).getContainer()))
                return true;
            else
                return false;
        }

        public boolean contains(ISchedulingRule rule) {
            return (rule == this);
        }
    }

    class AsynchActionJob extends Job {

        public  AsynchActionJob() {
            super(Messages.AsynchContainerConnectAction_JOB_NAME);
            setRule(new ContainerMutex(getContainer()));
        }

        public IStatus run(IProgressMonitor monitor) {
            //$NON-NLS-1$
            monitor.beginTask(NLS.bind(Messages.AsynchContainerConnectAction_MONITOR_BEGIN_TASK, (targetID == null) ? "" : targetID.getName()), 100);
            monitor.worked(30);
            try {
                container.connect(targetID, connectContext);
                if (monitor.isCanceled()) {
                    container.disconnect();
                    return Status.CANCEL_STATUS;
                }
                if (successBlock != null) {
                    successBlock.run();
                }
                monitor.worked(60);
                return Status.OK_STATUS;
            } catch (ContainerConnectException e) {
                return handleException(e);
            } finally {
                monitor.done();
            }
        }
    }

    public void run(IAction action) {
        new AsynchActionJob().schedule();
    }

    public void run() {
        this.run(null);
    }
}
