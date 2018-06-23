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
package org.eclipse.ecf.presence.collab.ui.view;

import java.util.Hashtable;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Activator;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.internal.presence.collab.ui.view.ShowViewDialogLabelProvider;
import org.eclipse.ecf.internal.presence.collab.ui.view.ShowViewDialogTreeContentProvider;
import org.eclipse.ecf.internal.presence.collab.ui.view.ShowViewDialogViewerFilter;
import org.eclipse.ecf.presence.collab.ui.AbstractCollabShare;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * Send/receive requests to share a specific view (identified by view ID).
 */
public class ViewShare extends AbstractCollabShare {

    private static final Map viewSharechannels = new Hashtable();

    public static ViewShare getViewShare(ID containerID) {
        return (ViewShare) viewSharechannels.get(containerID);
    }

    public static ViewShare addViewShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        return (ViewShare) viewSharechannels.put(containerID, new ViewShare(channelAdapter));
    }

    public static ViewShare removeViewShare(ID containerID) {
        return (ViewShare) viewSharechannels.remove(containerID);
    }

    public  ViewShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    private void handleOpenViewRequest(final String user, final String viewID, final String secondaryID, final int mode) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                // Ask user if they want to display view.
                if (MessageDialog.openQuestion(null, Messages.ViewShare_VIEWSHARE_RECEIVED_REQUEST_TITLE, NLS.bind(Messages.ViewShare_VIEWSHARE_RECEIVED_REQUEST_MESSAGE, user))) {
                    try {
                        final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        final IWorkbenchPage wp = ww.getActivePage();
                        if (wp == null)
                            throw new PartInitException(Messages.ViewShare_EXCEPTION_WORKBENCHPAGE_NULL);
                        // Actually show view requested
                        wp.showView(viewID, secondaryID, mode);
                    } catch (final Exception e) {
                        logError(Messages.ViewShare_VIEWSHARE_ERROR_LOG_MESSAGE, e);
                    }
                }
            }
        });
    }

    public void sendOpenViewRequest(final String senderuser, final ID toID) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                final IWorkbenchPage page = ww.getActivePage();
                if (page == null)
                    return;
                final ElementTreeSelectionDialog dlg = new ElementTreeSelectionDialog(null, new ShowViewDialogLabelProvider(), new ShowViewDialogTreeContentProvider());
                dlg.setTitle(Messages.ViewShare_VIEWSHARE_VIEW_REQUEST_DIALOG_TITLE);
                dlg.setMessage(Messages.ViewShare_VIEWSHARE_VIEW_REQUEST_DIALOG_MESSAGE);
                dlg.addFilter(new ShowViewDialogViewerFilter());
                dlg.setComparator(new ViewerComparator());
                dlg.setValidator(new ISelectionStatusValidator() {

                    public IStatus validate(Object[] selection) {
                        for (int i = 0; i < selection.length; ++i) if (!(selection[i] instanceof IViewDescriptor))
                            return new Status(IStatus.ERROR, //$NON-NLS-1$
                            Activator.PLUGIN_ID, 0, "", null);
                        return new //$NON-NLS-1$
                        Status(//$NON-NLS-1$
                        IStatus.OK, //$NON-NLS-1$
                        Activator.getDefault().getBundle().getSymbolicName(), //$NON-NLS-1$
                        0, //$NON-NLS-1$
                        "", //$NON-NLS-1$
                        null);
                    }
                });
                final IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
                dlg.setInput(reg);
                dlg.open();
                if (dlg.getReturnCode() == Window.CANCEL)
                    return;
                final Object[] descs = dlg.getResult();
                if (descs == null)
                    return;
                final String[] selectedIDs = new String[descs.length];
                for (int i = 0; i < descs.length; ++i) {
                    selectedIDs[i] = ((IViewDescriptor) descs[i]).getId();
                    try {
                        // Actually send messages to target remote user (toID),
                        // with selectedIDs (view IDs) to show
                        sendMessage(toID, serialize(new Object[] { senderuser, selectedIDs[i] }));
                    } catch (final ECFException e) {
                        logError(e.getStatus());
                    } catch (final Exception e) {
                        logError(Messages.Share_EXCEPTION_LOG_SEND, e);
                    }
                }
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.AbstractShare#handleChannelData(byte[])
	 */
    protected void handleMessage(ID fromContainerID, byte[] data) {
        try {
            final Object[] msg = (Object[]) deserialize(data);
            handleOpenViewRequest((String) msg[0], (String) msg[1], null, IWorkbenchPage.VIEW_ACTIVATE);
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_MESSAGE, e);
        }
    }
}
