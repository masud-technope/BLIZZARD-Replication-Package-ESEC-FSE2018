/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.filetransfer.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.filetransfer.events.*;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.ui.FileTransfersView;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class GetFileHandler extends AbstractHandler {

    //$NON-NLS-1$
    private static final String SCP_JOB_FAMILY = "scp";

    FileTransfersView fileTransfersView;

    void addTransferToView(Shell shell, final IIncomingFileTransfer incoming, final String localFileName) {
        shell.getDisplay().asyncExec(new Runnable() {

            public void run() {
                fileTransfersView = FileTransfersView.addTransfer(incoming, localFileName);
            }
        });
    }

    void updateTransferInView(Shell shell, final IIncomingFileTransfer incoming) {
        shell.getDisplay().asyncExec(new Runnable() {

            public void run() {
                if (fileTransfersView != null) {
                    fileTransfersView.update(incoming);
                }
            }
        });
    }

    void completeTransferInView(Shell shell, final IIncomingFileTransfer incoming) {
        updateTransferInView(shell, incoming);
    }

    public void openStartFileDownloadDialog(final Shell shell, String url) {
        StartFileDownloadDialog dialog = new StartFileDownloadDialog(shell, url);
        if (dialog.open() == Window.OK) {
            final String scp = dialog.getValue();
            final String userid = dialog.userid;
            final String passwd = dialog.passwd;
            final String fileName = dialog.filename;
            new Job(SCP_JOB_FAMILY) {

                protected IStatus run(final IProgressMonitor monitor) {
                    try {
                        final IContainer container = ContainerFactory.getDefault().createContainer();
                        IRetrieveFileTransferContainerAdapter adapter = (IRetrieveFileTransferContainerAdapter) container.getAdapter(IRetrieveFileTransferContainerAdapter.class);
                        final FileOutputStream out = new FileOutputStream(fileName);
                        IFileTransferListener listener = new IFileTransferListener() {

                            IIncomingFileTransfer incoming = null;

                            public void handleTransferEvent(IFileTransferEvent event) {
                                if (event instanceof IIncomingFileTransferReceiveStartEvent) {
                                    IIncomingFileTransferReceiveStartEvent rse = (IIncomingFileTransferReceiveStartEvent) event;
                                    try {
                                        incoming = rse.receive(out);
                                        addTransferToView(shell, incoming, fileName);
                                    } catch (IOException e) {
                                        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "failed to set output file", e));
                                    }
                                } else if (event instanceof IIncomingFileTransferReceiveDataEvent) {
                                    updateTransferInView(shell, incoming);
                                } else if (event instanceof IIncomingFileTransferReceiveDoneEvent) {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "failed to close output file", e));
                                    }
                                    completeTransferInView(shell, incoming);
                                }
                            }
                        };
                        adapter.setConnectContextForAuthentication(ConnectContextFactory.createUsernamePasswordConnectContext(userid, passwd));
                        monitor.beginTask(SCP_JOB_FAMILY, IProgressMonitor.UNKNOWN);
                        adapter.sendRetrieveRequest(FileIDFactory.getDefault().createFileID(adapter.getRetrieveNamespace(), scp), listener, null);
                    } catch (Exception e) {
                        monitor.done();
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "get file failed", e);
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
    public Object execute(ExecutionEvent o) throws ExecutionException {
        openStartFileDownloadDialog(HandlerUtil.getActiveShellChecked(o), null);
        return null;
    }
}
