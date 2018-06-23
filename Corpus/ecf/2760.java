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
package org.eclipse.ecf.presence.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.presence.ui.Activator;
import org.eclipse.ecf.internal.presence.ui.dialogs.ReceiveAuthorizeRequestDialog;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A roster account appropriate for usage by a MultiRosterView. This class
 * provides a holder for an IContainer instance used by an
 * {@link IMultiRosterViewPart}. Subclasses may be created as desired.
 */
public class MultiRosterAccount {

    protected final MultiRosterView multiRosterView;

    protected IContainer container;

    protected IPresenceContainerAdapter adapter;

    IRosterListener updateListener = new IRosterListener() {

        public void handleRosterUpdate(final IRoster roster, final IRosterItem changedValue) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MultiRosterAccount.this.multiRosterView.refreshTreeViewer(changedValue, true);
                }
            });
        }

        public void handleRosterEntryAdd(final IRosterEntry entry) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MultiRosterAccount.this.multiRosterView.addEntryToTreeViewer(entry);
                }
            });
        }

        public void handleRosterEntryRemove(final IRosterEntry entry) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MultiRosterAccount.this.multiRosterView.removeEntryFromTreeViewer(entry);
                }
            });
        }
    };

    IContainerListener containerListener = new IContainerListener() {

        public void handleEvent(IContainerEvent event) {
            if (event instanceof IContainerDisconnectedEvent || event instanceof IContainerEjectedEvent) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        MultiRosterAccount.this.multiRosterView.rosterAccountDisconnected(MultiRosterAccount.this);
                    }
                });
            }
        }
    };

    IRosterSubscriptionListener subscriptionListener = new IRosterSubscriptionListener() {

        public void handleSubscribeRequest(final ID fromID) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    try {
                        Shell shell = MultiRosterAccount.this.multiRosterView.getViewSite().getShell();
                        ReceiveAuthorizeRequestDialog authRequest = new ReceiveAuthorizeRequestDialog(shell, fromID.getName(), MultiRosterAccount.this.getRoster().getUser().getID().getName());
                        authRequest.setBlockOnOpen(true);
                        authRequest.open();
                        int res = authRequest.getButtonPressed();
                        if (res == ReceiveAuthorizeRequestDialog.AUTHORIZE_ID) {
                            MultiRosterAccount.this.getRosterManager().getPresenceSender().sendPresenceUpdate(fromID, new Presence(IPresence.Type.SUBSCRIBED));
                        } else if (res == ReceiveAuthorizeRequestDialog.REFUSE_ID) {
                        // do nothing
                        } else {
                        // do nothing
                        }
                    } catch (ECFException e) {
                        Activator.getDefault().getLog().log(e.getStatus());
                    }
                }
            });
        }

        public void handleSubscribed(ID fromID) {
        // do nothing
        }

        public void handleUnsubscribed(final ID fromID) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MultiRosterAccount.this.multiRosterView.removeEntryFromTreeViewer(fromID);
                }
            });
        }
    };

    public  MultiRosterAccount(MultiRosterView multiRosterView, IContainer container, IPresenceContainerAdapter adapter) {
        this.multiRosterView = multiRosterView;
        Assert.isNotNull(container);
        Assert.isNotNull(adapter);
        this.container = container;
        this.adapter = adapter;
        this.container.addListener(containerListener);
        getRosterManager().addRosterListener(updateListener);
        getRosterManager().addRosterSubscriptionListener(subscriptionListener);
    }

    public IContainer getContainer() {
        return container;
    }

    public IPresenceContainerAdapter getPresenceContainerAdapter() {
        return adapter;
    }

    public IRosterManager getRosterManager() {
        return getPresenceContainerAdapter().getRosterManager();
    }

    public IRoster getRoster() {
        return getRosterManager().getRoster();
    }

    public void dispose() {
        getRosterManager().removeRosterSubscriptionListener(subscriptionListener);
        getRosterManager().removeRosterListener(updateListener);
        container.removeListener(containerListener);
    }
}
