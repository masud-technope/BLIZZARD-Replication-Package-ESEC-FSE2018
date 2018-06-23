/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.collab.ui.console;

import java.util.Hashtable;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.collab.ui.AbstractCollabShare;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;

/**
 * Send/receive requests to share a specific view (identified by view ID).
 */
public class ConsoleShare extends AbstractCollabShare {

    private static final Map consoleSharechannels = new Hashtable();

    static TextSelection selection = null;

    static boolean initialized = false;

    static final ISelectionListener selectionListener = new ISelectionListener() {

        public void selectionChanged(IWorkbenchPart part, ISelection sel) {
            if (part instanceof IConsoleView && sel instanceof TextSelection) {
                TextSelection s = (TextSelection) sel;
                if (s == null || s.getLength() == 0)
                    ConsoleShare.selection = null;
                else
                    ConsoleShare.selection = (TextSelection) sel;
            }
        }
    };

    public static ConsoleShare getStackShare(ID containerID) {
        return (ConsoleShare) consoleSharechannels.get(containerID);
    }

    public static ConsoleShare addStackShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        initialize();
        return (ConsoleShare) consoleSharechannels.put(containerID, new ConsoleShare(channelAdapter));
    }

    private static void initialize() {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (!initialized) {
                    final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    final IWorkbenchPage page = ww.getActivePage();
                    page.addSelectionListener(selectionListener);
                    initialized = true;
                }
            }
        });
    }

    public static TextSelection getSelection() {
        return selection;
    }

    public static ConsoleShare removeStackShare(ID containerID) {
        return (ConsoleShare) consoleSharechannels.remove(containerID);
    }

    public  ConsoleShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    private void handleShowConsoleSelection(final String user, final String consoleSelection) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    if (ww == null)
                        throw new PartInitException(Messages.ConsoleShare_EXCEPTION_WW_NOT_AVAILABLE);
                    final IWorkbenchPage wp = ww.getActivePage();
                    if (wp == null)
                        throw new PartInitException(Messages.ConsoleShare_EXCEPTION_WP_NOT_AVAILABLE);
                    wp.showView(IConsoleConstants.ID_CONSOLE_VIEW);
                    final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
                    final IConsole[] consoles = consoleManager.getConsoles();
                    if (consoles.length == 0) {
                        MessageDialog.openInformation(null, NLS.bind(Messages.ConsoleShare_STACK_TRACE_FROM_TITLE, user), NLS.bind(Messages.ConsoleShare_STACK_TRACE_FROM_MESSAGE, user));
                        return;
                    }
                    for (int i = 0; i < consoles.length; i++) {
                        final String consoleType = consoles[i].getType();
                        if (consoleType != null && consoleType.equals("javaStackTraceConsole")) {
                            final TextConsole textConsole = (TextConsole) consoles[i];
                            textConsole.activate();
                            final IDocument document = textConsole.getDocument();
                            final String text = document.get() + getConsoleSelectionToShow(user, consoleSelection);
                            document.set(text);
                        }
                    }
                } catch (final Exception e) {
                    logError(Messages.ConsoleShare_STACKSHARE_ERROR_LOG_MESSAGE, e);
                }
            }
        });
    }

    String getConsoleSelectionToShow(String user, String stackTrace) {
        return NLS.bind(Messages.ConsoleShare_STACK_TRACE_CONTENT, user, stackTrace);
    }

    public void sendShareConsoleSelection(final String senderuser, final ID toID, final String consoleSelection) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    sendMessage(toID, serialize(new Object[] { senderuser, consoleSelection }));
                } catch (final ECFException e) {
                    logError(e.getStatus());
                } catch (final Exception e) {
                    logError(Messages.Share_EXCEPTION_LOG_SEND, e);
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
            handleShowConsoleSelection((String) msg[0], (String) msg[1]);
        } catch (final Exception e) {
            logError(Messages.Share_EXCEPTION_LOG_MESSAGE, e);
        }
    }
}
