/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bug 192762
 ******************************************************************************/
package org.eclipse.ecf.internal.irc.ui.actions;

import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.irc.ui.Activator;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.ui.chatroom.ChatRoomManagerView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

public abstract class AbstractActionDelegate implements IViewActionDelegate {

    private IStructuredSelection selection;

    private IChatRoomContainer chatRoomContainer;

    public  AbstractActionDelegate() {
        super();
    }

    protected abstract String getMessage(String username);

    protected String getUsername(IUser user) {
        String username = user.getName();
        if (//$NON-NLS-1$
        username.startsWith("@")) {
            username = username.substring(1);
        }
        return username;
    }

    public void run(IAction action) {
        if ((chatRoomContainer == null) || (selection == null)) {
            return;
        }
        try {
            Iterator iterator = selection.iterator();
            while (iterator.hasNext()) {
                IUser user = (IUser) iterator.next();
                String message = getMessage(getUsername(user));
                chatRoomContainer.getChatRoomMessageSender().sendMessage(message);
            }
        } catch (ECFException e) {
            StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        this.selection = (IStructuredSelection) selection;
    }

    public void init(IViewPart view) {
        chatRoomContainer = ((ChatRoomManagerView) view).getActiveChatRoomContainer();
    }
}
