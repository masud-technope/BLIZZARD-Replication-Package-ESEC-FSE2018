/****************************************************************************
 * Copyright (c) 2007, 2009 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *    Scott Lewis <slewis@composent.com>
 *****************************************************************************/
package org.eclipse.ecf.internal.irc.ui.wizards;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.irc.ui.*;
import org.eclipse.ecf.internal.provider.irc.identity.IRCID;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public final class IRCConnectWizard extends Wizard implements IConnectWizard, INewWizard {

    public static final String DEFAULT_GUEST_USER = "guest";

    private IRCConnectWizardPage page;

    private IContainer container;

    private ID targetID;

    private IConnectContext connectContext;

    private String authorityAndPath = null;

    public  IRCConnectWizard() {
        super();
    }

    public  IRCConnectWizard(String authorityAndPart) {
        super();
        this.authorityAndPath = authorityAndPart;
    }

    public void addPages() {
        page = new IRCConnectWizardPage(authorityAndPath);
        addPage(page);
    }

    public void init(IWorkbench workbench, IContainer container) {
        this.container = container;
        setWindowTitle(Messages.IRCConnectWizard_WIZARD_TITLE);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.container = null;
        try {
            this.container = ContainerFactory.getDefault().createContainer("ecf.irc.irclib");
        } catch (final ContainerCreateException e) {
        }
        setWindowTitle(Messages.IRCConnectWizard_WIZARD_TITLE);
    }

    public boolean performCancel() {
        container.dispose();
        IContainerManager containerManager = Activator.getDefault().getContainerManager();
        if (containerManager != null) {
            containerManager.removeContainer(container);
        }
        return super.performCancel();
    }

    public boolean performFinish() {
        final String connectID = page.getConnectID();
        final String password = page.getPassword();
        connectContext = ConnectContextFactory.createPasswordConnectContext(password);
        page.saveComboText();
        try {
            targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), connectID);
        } catch (final IDCreateException e) {
            new IDCreateErrorDialog(null, connectID, e).open();
            return false;
        }
        final IChatRoomManager manager = (IChatRoomManager) this.container.getAdapter(IChatRoomManager.class);
        final IRCUI ui = new IRCUI(this.container, manager, null);
        ui.showForTarget(targetID);
        // If it's not already connected, then we connect this new container
        if (!ui.isContainerConnected()) {
            page.saveComboItems();
            // bug 274613, we need to remove the extra autojoin channel bits
            IRCID id = (IRCID) targetID;
            // start with user, then host, then port, abc@irc.freenode.net:6667
            StringBuffer buffer = new StringBuffer(id.getUsername());
            buffer.append('@').append(id.getHost());
            buffer.append(':').append(id.getPort());
            // create a truncated ID instance for the container to connect to
            id = (IRCID) container.getConnectNamespace().createInstance(new Object[] { buffer.toString() });
            new AsynchContainerConnectAction(container, id, connectContext, null, new Runnable() {

                public void run() {
                    cachePassword(page.getPasswordKeyFromUserName(connectID), password);
                }
            }).run();
        }
        return true;
    }

    protected void cachePassword(final String connectID, String password) {
        if (password != null && !password.equals("")) {
            final PasswordCacheHelper pwStorage = new PasswordCacheHelper(connectID);
            pwStorage.savePassword(password);
        }
    }
}
