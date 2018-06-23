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

import org.eclipse.core.filebuffers.ISynchronizationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.docshare2.DocShare;
import org.eclipse.ecf.docshare2.messages.StartMessage;
import org.eclipse.ecf.docshare2.messages.StopMessage;
import org.eclipse.ecf.sync.ui.resources.decorators.RemotelyOpenedEditorsDecorator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.progress.WorkbenchJob;

public class WorkbenchAwareDocShare extends DocShare {

    private Job decoratorJob;

    public  WorkbenchAwareDocShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
        decoratorJob = new DecoratorJob();
        decoratorJob.setPriority(Job.DECORATE);
    }

    protected void handleStartMessage(StartMessage message) throws CoreException {
        super.handleStartMessage(message);
        // starting to share a given file, request a decoration update
        RemotelyOpenedEditorsDecorator.set.add(message.getPath());
        decoratorJob.schedule();
    }

    protected void handleStopMessage(StopMessage message) {
        super.handleStopMessage(message);
        // no longer sharing a given file, request a decoration update
        RemotelyOpenedEditorsDecorator.set.remove(message.getPath());
        decoratorJob.schedule();
    }

    protected ISynchronizationContext getSynchronizationContext(String path) {
        // want to run code within a UI context
        if (editorIsOpen(path)) {
            return UI_CONTEXT;
        }
        return super.getSynchronizationContext(path);
    }

    protected void documentAboutToBeChanged(String path) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        if (file.exists()) {
            IEditorPart editor = findEditor(file);
            if (editor != null) {
                final StyledText control = (StyledText) editor.getAdapter(Control.class);
                if (control != null && !control.isDisposed()) {
                    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                        public void run() {
                            control.setEditable(false);
                        }
                    });
                }
            }
        }
    }

    protected void documentChanged(String path) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        if (file.exists()) {
            IEditorPart editor = findEditor(file);
            if (editor != null) {
                final StyledText control = (StyledText) editor.getAdapter(Control.class);
                if (control != null && !control.isDisposed()) {
                    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                        public void run() {
                            control.setEditable(true);
                        }
                    });
                }
            }
        }
    }

    private IEditorPart findEditor(final IFile file) {
        final IEditorPart[] editors = { null };
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

            public void run() {
                IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
                for (int i = 0; i < workbenchWindows.length; i++) {
                    IWorkbenchPage page = workbenchWindows[i].getActivePage();
                    if (page != null) {
                        IEditorPart editor = ResourceUtil.findEditor(page, file);
                        if (editor != null) {
                            editors[0] = editor;
                            return;
                        }
                    }
                }
            }
        });
        return editors[0];
    }

    private boolean editorIsOpen(String path) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        return file.exists() && findEditor(file) != null;
    }

    private static ISynchronizationContext UI_CONTEXT = new ISynchronizationContext() {

        public void run(Runnable runnable) {
            PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
        }
    };

    private static class DecoratorJob extends WorkbenchJob {

        public  DecoratorJob() {
            super(PlatformUI.getWorkbench().getDisplay(), "Decoration job");
        }

        public IStatus runInUIThread(IProgressMonitor monitor) {
            try {
                PlatformUI.getWorkbench().getDecoratorManager().update(RemotelyOpenedEditorsDecorator.DECORATOR_ID);
                return Status.OK_STATUS;
            } finally {
                monitor.done();
            }
        }
    }
}
