/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat - update to Mylyn 3.0 API
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.LocalContextStore;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.progress.UIJob;

public class CompoundContextActivationContributionItem extends CompoundContributionItem {

    static LinkedList tasks = new LinkedList();

    static Map contexts = new HashMap();

    private static ActivateTaskAction[] actions = new ActivateTaskAction[5];

    private Shell shell;

    static {
        for (int i = 0; i < 5; i++) {
            actions[i] = new ActivateTaskAction();
        }
    }

    public void fill(Menu menu, int index) {
        shell = menu.getShell();
        super.fill(menu, index);
    }

    protected IContributionItem[] getContributionItems() {
        int count = 0;
        for (Iterator it = tasks.iterator(); it.hasNext() && count != 5; ) {
            ITask task = (ITask) it.next();
            actions[count].setShell(shell);
            actions[count].setTask(task);
            count++;
        }
        IContributionItem[] array = null;
        if (count == 5 && tasks.size() != 5) {
            array = new IContributionItem[7];
            array[5] = new Separator();
            array[6] = new ActionContributionItem(new Action("Activate received task...") {

                public void run() {
                    ActivateReceivedContextHandler.open(shell);
                }
            });
        } else {
            array = new IContributionItem[count];
        }
        for (int i = 0; i < count; i++) {
            array[i] = new ActionContributionItem(actions[i]);
        }
        return array;
    }

    static void enqueue(ITask task, IInteractionContext context) {
        tasks.add(task);
        contexts.put(task, context);
    }

    static class ActivateTaskAction extends Action {

        private static final String TITLE_DIALOG = "Mylyn Information";

        private Shell shell;

        private ITask task;

        void setShell(Shell shell) {
            this.shell = shell;
        }

        void setTask(ITask task) {
            this.task = task;
            setText(task.getSummary());
        }

        public void run() {
            final IInteractionContext context = (IInteractionContext) contexts.get(task);
            final ITaskList taskList = TasksUiPlugin.getTaskList();
            if (taskList.getTask(task.getRepositoryUrl(), task.getTaskId()) != null) {
                boolean confirmed = MessageDialog.openConfirm(shell, TITLE_DIALOG, "The task '" + task.getSummary() + "' already exists. Do you want to override its context with the source?");
                if (confirmed) {
                    Job job = new Job("Import context") {

                        protected IStatus run(IProgressMonitor monitor) {
                            ((LocalContextStore) ContextCore.getContextStore()).importContext(context);
                            scheduleTaskActivationJob();
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();
                } else {
                    return;
                }
            } else {
                Job job = new Job("Import task") {

                    protected IStatus run(IProgressMonitor monitor) {
                        ((LocalContextStore) ContextCore.getContextStore()).importContext(context);
                        taskList.addTask(task);
                        scheduleTaskActivationJob();
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
            tasks.remove(task);
            contexts.remove(task);
        }

        private void scheduleTaskActivationJob() {
            UIJob job = new UIJob(shell.getDisplay(), "Activate imported task") {

                public IStatus runInUIThread(IProgressMonitor monitor) {
                    TasksUi.getTaskActivityManager().activateTask(task);
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }
    }
}
