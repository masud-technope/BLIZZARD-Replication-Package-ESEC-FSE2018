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
package org.eclipse.ecf.filetransfer.ui.actions;

import java.io.File;
import java.util.Map;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.ecf.filetransfer.*;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IOutgoingFileTransferSendDoneEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.filetransfer.ui.Activator;
import org.eclipse.ecf.internal.filetransfer.ui.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * Action super class for initiating outging file transfers. Subclasses should
 * be created to implement {@link #getOutgoingFileTransferAdapter()} and to
 * override any desired methods.
 */
public abstract class AbstractFileSendAction extends Action {

    protected IFileID targetReceiver;

    protected IFileTransferInfo fileTransferInfo;

    protected IFileTransferListener fileTransferListener;

    protected Map options;

    protected IExceptionHandler exceptionHandler = null;

    public void setTargetReceiver(IFileID targetReceiver) {
        this.targetReceiver = targetReceiver;
    }

    public IFileID getTargetReceiver() {
        return this.targetReceiver;
    }

    public void setFileTransferInfo(IFileTransferInfo info) {
        this.fileTransferInfo = info;
    }

    public IFileTransferInfo getFileTransferInfo() {
        return this.fileTransferInfo;
    }

    public void setFileToSend(File fileToSend) {
        // /this.fileToSend = fileToSend;
        this.fileTransferInfo = createFileTransferInfoFromFile(fileToSend);
    }

    /**
	 * @param fileToSend
	 * @return file transfer info for given file.
	 */
    private IFileTransferInfo createFileTransferInfoFromFile(final File fileToSend) {
        return new FileTransferInfo(fileToSend);
    }

    public File getFileToSend() {
        if (this.fileTransferInfo == null)
            return null;
        return this.fileTransferInfo.getFile();
    }

    public void setFileTransferListener(IFileTransferListener listener) {
        this.fileTransferListener = listener;
    }

    public IFileTransferListener getFileTransferListener() {
        return this.fileTransferListener;
    }

    public void setFileTransferOptions(Map options) {
        this.options = options;
    }

    public Map getFileTransferOptions() {
        return this.options;
    }

    public void setExceptionHandler(IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public IExceptionHandler getExceptionhandler() {
        return this.exceptionHandler;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
    public void run() {
        try {
            sendFileToTarget();
        } catch (Exception e) {
            if (exceptionHandler != null)
                exceptionHandler.handleException(e);
            else
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, NLS.bind(Messages.getString("AbstractFileSendAction.EXCEPTION_SENDING_TO_TARGET"), getTargetReceiver()), e));
        }
    }

    /**
	 * Get the container adapter for actually initiating the file transer
	 * request.
	 * 
	 * @return ISendFileTransferContainerAdapter to use for the action
	 *         {@link #run()}. Must not return <code>null</code>.
	 */
    protected abstract ISendFileTransferContainerAdapter getOutgoingFileTransferAdapter();

    protected void sendFileToTarget() throws Exception {
        IFileID target = getTargetReceiver();
        //$NON-NLS-1$
        Assert.isNotNull(target, Messages.getString("AbstractFileSendAction.RECEIVER_NOT_NULL"));
        ISendFileTransferContainerAdapter adapter = getOutgoingFileTransferAdapter();
        //$NON-NLS-1$
        Assert.isNotNull(adapter, Messages.getString("AbstractFileSendAction.ADAPTER_NOT_NULL"));
        IFileTransferListener listener = getFileTransferListener();
        if (listener == null)
            listener = createDefaultFileTransferListener();
        //$NON-NLS-1$
        Assert.isNotNull(listener, Messages.getString("AbstractFileSendAction.LISTENER_NOT_NULL"));
        IFileTransferInfo info = getFileTransferInfo();
        //$NON-NLS-1$
        Assert.isNotNull(info, Messages.getString("AbstractFileSendAction.FILE_NOT_NULL"));
        // Now call
        adapter.sendOutgoingRequest(target, info, listener, this.options);
    }

    /**
	 * @return IFileTransferListener to use as the default listener. Must not be
	 *         <code>null</code>.
	 */
    protected IFileTransferListener createDefaultFileTransferListener() {
        return new IFileTransferListener() {

            public void handleTransferEvent(final IFileTransferEvent event) {
                // then a custom IFileTransferListener should be provided.
                if (event instanceof IOutgoingFileTransferSendDoneEvent) {
                    final IOutgoingFileTransferSendDoneEvent oftsde = (IOutgoingFileTransferSendDoneEvent) event;
                    final Exception errorException = oftsde.getSource().getException();
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (errorException == null) {
                                MessageDialog.openInformation(null, Messages.getString("AbstractFileSendAction.TITLE_FILE_TRANSFER_SUCESSFUL"), NLS.bind(Messages.getString("AbstractFileSendAction.MESSAGE_FILE_TRANSFER_SUCCESSFUL"), getFileTransferInfo().getFile().getName()));
                            } else {
                                MessageDialog.openError(null, Messages.getString("AbstractFileSendAction.TITLE_FILE_TRANSFER_FAILED"), NLS.bind(Messages.getString("AbstractFileSendAction.MESSAGE_FILE_TRANSFER_FAILED"), errorException.getLocalizedMessage()));
                            }
                        }
                    });
                }
            }
        };
    }
}
