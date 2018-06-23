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
package org.eclipse.ecf.internal.provider.msn.ui;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.ecf.presence.im.ITypingMessageEvent;
import org.eclipse.ecf.presence.im.ITypingMessageSender;
import org.eclipse.ecf.presence.ui.MessagesView;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.actions.AsynchContainerConnectAction;
import org.eclipse.ecf.ui.dialogs.IDCreateErrorDialog;
import org.eclipse.ecf.ui.util.PasswordCacheHelper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class MSNConnectWizard extends Wizard implements IConnectWizard, INewWizard {

    private IWorkbench workbench;

    private MSNConnectWizardPage page;

    private IContainer container;

    private IChatMessageSender icms;

    private ITypingMessageSender itms;

    private ID targetID;

    private IConnectContext connectContext;

    private String username;

    public  MSNConnectWizard() {
        super();
    }

    public  MSNConnectWizard(String username) {
        this();
        this.username = username;
    }

    public void addPages() {
        page = new MSNConnectWizardPage(username);
        addPage(page);
    }

    public void init(IWorkbench workbench, IContainer container) {
        this.workbench = workbench;
        this.container = container;
        setWindowTitle(Messages.MSNConnectWizard_Title);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.container = null;
        try {
            this.container = ContainerFactory.getDefault().createContainer("ecf.msn.msnp");
        } catch (final ContainerCreateException e) {
        }
        setWindowTitle(Messages.MSNConnectWizard_Title);
    }

    private void openView() {
        try {
            MultiRosterView view = (MultiRosterView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MultiRosterView.VIEW_ID);
            if (view == null) {
                view = (MultiRosterView) workbench.getActiveWorkbenchWindow().getActivePage().showView(MultiRosterView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
            }
            view.addContainer(container);
            final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            if (!page.isPartVisible(view)) {
                final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
                service.warnOfContentChange();
            }
        } catch (final PartInitException e) {
            e.printStackTrace();
        }
    }

    private void displayMessage(IChatMessageEvent e) {
        final IChatMessage message = e.getChatMessage();
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                MessagesView view = (MessagesView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MessagesView.VIEW_ID);
                if (view != null) {
                    final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
                    view.openTab(icms, itms, targetID, message.getFromID());
                    view.showMessage(message);
                    service.warnOfContentChange();
                } else {
                    try {
                        final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                        view = (MessagesView) page.showView(MessagesView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
                        if (!page.isPartVisible(view)) {
                            final IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
                            service.warnOfContentChange();
                        }
                        view.openTab(icms, itms, targetID, message.getFromID());
                        view.showMessage(message);
                    } catch (final PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayTypingNotification(final ITypingMessageEvent e) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                final MessagesView view = (MessagesView) workbench.getActiveWorkbenchWindow().getActivePage().findView(MessagesView.VIEW_ID);
                if (view != null) {
                    view.displayTypingNotification(e);
                }
            }
        });
    }

    public boolean performFinish() {
        final String connectID = page.getEmail();
        final String password = page.getPassword();
        // Save combo text even if we don't successfully login
        page.saveComboText();
        connectContext = ConnectContextFactory.createPasswordConnectContext(password);
        try {
            targetID = container.getConnectNamespace().createInstance(new Object[] { connectID });
        } catch (final IDCreateException e) {
            new IDCreateErrorDialog(null, connectID, e).open();
            return false;
        }
        page.saveComboItems();
        final IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);
        container.addListener(new IContainerListener() {

            public void handleEvent(IContainerEvent event) {
                if (event instanceof IContainerConnectedEvent) {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            openView();
                        }
                    });
                }
            }
        });
        final IChatManager icm = adapter.getChatManager();
        icms = icm.getChatMessageSender();
        itms = icm.getTypingMessageSender();
        icm.addMessageListener(new IIMMessageListener() {

            public void handleMessageEvent(IIMMessageEvent e) {
                if (e instanceof IChatMessageEvent) {
                    displayMessage((IChatMessageEvent) e);
                } else if (e instanceof ITypingMessageEvent) {
                    displayTypingNotification((ITypingMessageEvent) e);
                }
            }
        });
        new AsynchContainerConnectAction(container, targetID, connectContext, null, new Runnable() {

            public void run() {
                cachePassword(connectID, password);
            }
        }).run();
        return true;
    }

    private void cachePassword(final String connectID, String password) {
        if (password != null && !password.equals("")) {
            final PasswordCacheHelper pwStorage = new PasswordCacheHelper(connectID);
            pwStorage.savePassword(password);
        }
    }
}
