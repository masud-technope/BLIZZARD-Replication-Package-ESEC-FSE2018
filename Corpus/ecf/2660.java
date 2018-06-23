/****************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent.ui;

import java.io.File;
import java.io.IOException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.filetransfer.IFileTransfer;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.ui.FileTransfersView;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.dialogs.ContainerConnectErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

public class BitTorrentConnectWizard extends Wizard implements IConnectWizard, INewWizard {

    private IWorkbench workbench;

    private BitTorrentConnectWizardPage page;

    private IContainer container;

    private IFileID targetID;

    private IWorkbenchPage workbenchPage;

    private String torrentFile;

    public  BitTorrentConnectWizard() {
        super();
    }

    public  BitTorrentConnectWizard(String torrentFile) {
        this();
        this.torrentFile = torrentFile;
    }

    public void addPages() {
        page = new BitTorrentConnectWizardPage(torrentFile);
        addPage(page);
    }

    public void init(IWorkbench workbench, IContainer container) {
        this.workbench = workbench;
        this.container = container;
        setWindowTitle(Messages.getString("BitTorrentConnectWizardPage.File_Sharing.Title"));
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.container = null;
        try {
            this.container = ContainerFactory.getDefault().createContainer("ecf.filetransfer.bittorrent");
        } catch (final ContainerCreateException e) {
        }
        setWindowTitle(Messages.getString("BitTorrentConnectWizardPage.File_Sharing.Title"));
    }

    public boolean performFinish() {
        workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
        final IRetrieveFileTransferContainerAdapter irftca = (IRetrieveFileTransferContainerAdapter) container.getAdapter(IRetrieveFileTransferContainerAdapter.class);
        try {
            targetID = FileIDFactory.getDefault().createFileID(irftca.getRetrieveNamespace(), page.getTorrentName());
        } catch (final FileCreateException e) {
            new ContainerConnectErrorDialog(workbench.getActiveWorkbenchWindow().getShell(), 1, "The target ID to connect to could not be created", page.getTorrentName(), e).open();
            return true;
        }
        try {
            irftca.sendRetrieveRequest(targetID, new IFileTransferListener() {

                public void handleTransferEvent(final IFileTransferEvent e) {
                    if (e instanceof IIncomingFileTransferReceiveStartEvent) {
                        try {
                            final IFileTransfer ift = ((IIncomingFileTransferReceiveStartEvent) e).receive(new File(page.getTargetName()));
                            workbenchPage.getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {

                                public void run() {
                                    FileTransfersView.addTransfer(ift);
                                }
                            });
                        } catch (final IOException ioe) {
                            new ContainerConnectErrorDialog(workbench.getActiveWorkbenchWindow().getShell(), 1, "Could not write to " + page.getTargetName(), page.getTargetName(), null).open();
                        }
                    } else if (e instanceof IIncomingFileTransferEvent) {
                        final FileTransfersView ftv = (FileTransfersView) workbenchPage.findView(FileTransfersView.ID);
                        if (ftv != null) {
                            workbenchPage.getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {

                                public void run() {
                                    ftv.update(((IIncomingFileTransferEvent) e).getSource());
                                }
                            });
                        }
                    }
                }
            }, null);
        } catch (final IncomingFileTransferException e) {
            new ContainerConnectErrorDialog(workbench.getActiveWorkbenchWindow().getShell(), 1, "Could not send retrieval request.", targetID.getName(), e).open();
        }
        return true;
    }
}
