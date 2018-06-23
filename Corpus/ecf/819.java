/****************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.ui;

import java.io.File;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferResponseEvent;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class XMPPCompoundContributionItem extends AbstractRosterEntryContributionItem {

    protected IAction[] makeActions() {
        return null;
    }

    protected IContributionItem[] getContributionItems() {
        final Object selection = getSelection();
        if (!(selection instanceof IRosterEntry)) {
            return EMPTY_ARRAY;
        }
        final IRosterEntry entry = (IRosterEntry) selection;
        final IContainer container = getContainerForRosterEntry(entry);
        if (container instanceof XMPPContainer) {
            final IContributionItem[] contributions = new IContributionItem[1];
            final ISendFileTransferContainerAdapter ioftca = (ISendFileTransferContainerAdapter) container.getAdapter(ISendFileTransferContainerAdapter.class);
            if (!(ioftca != null && isAvailable(entry)))
                return EMPTY_ARRAY;
            final IAction fileSendAction = new Action() {

                public void run() {
                    sendFileToTarget(ioftca, entry.getUser().getID());
                }
            };
            fileSendAction.setText(Messages.XMPPCompoundContributionItem_SEND_FILE);
            fileSendAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
            contributions[0] = new ActionContributionItem(fileSendAction);
            return contributions;
        } else {
            return EMPTY_ARRAY;
        }
    }

    private void sendFileToTarget(ISendFileTransferContainerAdapter fileTransfer, final ID targetID) {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final FileDialog fd = new FileDialog(shell, SWT.OPEN);
        // XXX this should be some default path set by preferences
        //$NON-NLS-1$
        fd.setFilterPath(System.getProperty("user.home"));
        fd.setText(NLS.bind(Messages.XMPPCompoundContributionItem_CHOOSE_FILE, targetID.getName()));
        final String res = fd.open();
        if (res != null) {
            final File aFile = new File(res);
            try {
                final IFileID targetFileID = FileIDFactory.getDefault().createFileID(fileTransfer.getOutgoingNamespace(), new Object[] { targetID, res });
                fileTransfer.sendOutgoingRequest(targetFileID, aFile, new IFileTransferListener() {

                    public void handleTransferEvent(final IFileTransferEvent event) {
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {
                                // bar?)
                                if (event instanceof IOutgoingFileTransferResponseEvent) {
                                    if (!((IOutgoingFileTransferResponseEvent) event).requestAccepted())
                                        MessageDialog.openInformation(shell, Messages.XMPPCompoundContributionItem_FILE_SEND_REFUSED_TITLE, NLS.bind(Messages.XMPPCompoundContributionItem_FILE_SEND_REFUSED_MESSAGE, res, targetID.getName()));
                                }
                            }
                        });
                    }
                }, null);
            } catch (final Exception e) {
                MessageDialog.openError(shell, Messages.XMPPCompoundContributionItem_SEND_ERROR_TITLE, NLS.bind(Messages.XMPPCompoundContributionItem_SEND_ERROR_MESSAGE, res, e.getLocalizedMessage()));
            }
        }
    }
}
