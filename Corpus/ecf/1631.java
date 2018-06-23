/****************************************************************************
 * Copyright (c) 20047 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui.url;

import java.net.URL;
import java.util.Hashtable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.collab.ui.AbstractCollabShare;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Send/receive requests to display an URL in the internal web browser.
 */
public class URLShare extends AbstractCollabShare {

    private static final Hashtable urlsharechannels = new Hashtable();

    public static URLShare getURLShare(ID containerID) {
        return (URLShare) urlsharechannels.get(containerID);
    }

    public static URLShare addURLShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        return (URLShare) urlsharechannels.put(containerID, new URLShare(channelAdapter));
    }

    public static URLShare removeURLShare(ID containerID) {
        return (URLShare) urlsharechannels.remove(containerID);
    }

    public  URLShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    private void showURL(final String user, final String url) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (MessageDialog.openQuestion(null, Messages.URLShare_RECEIVED_URL_TITLE, NLS.bind(Messages.URLShare_RECEIVED_URL_MESSAGE, user, url))) {
                    final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
                    IWebBrowser browser;
                    try {
                        browser = support.createBrowser(null);
                        browser.openURL(new URL(url));
                    } catch (final Exception e) {
                        logError(Messages.URLShare_EXCEPTION_LOG_BROWSER, e);
                    }
                }
            }
        });
    }

    public void sendURL(final String senderuser, final ID toID, final String theURL) {
        try {
            sendMessage(toID, serialize(new Object[] { senderuser, theURL }));
        } catch (final ECFException e) {
            logError(e.getStatus());
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_SEND, e);
        }
    }

    public void showDialogAndSendURL(final String senderuser, final ID toID) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                final InputDialog input = new InputDialog(null, Messages.URLShare_INPUT_URL_DIALOG_TITLE, Messages.URLShare_ENTER_URL_DIALOG_TEXT, Messages.URLShare_ENTER_URL_DEFAULT_URL, null);
                input.setBlockOnOpen(true);
                final int result = input.open();
                if (result == Window.OK) {
                    final String send = input.getValue();
                    if (send != null && !//$NON-NLS-1$
                    send.equals("")) {
                        try {
                            sendMessage(toID, serialize(new Object[] { senderuser, send }));
                        } catch (final ECFException e) {
                            logError(e.getStatus());
                        } catch (final Exception e) {
                            logError(Messages.Share_EXCEPTION_LOG_SEND, e);
                        }
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
            showURL((String) msg[0], (String) msg[1]);
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_MESSAGE, e);
        }
    }
}
