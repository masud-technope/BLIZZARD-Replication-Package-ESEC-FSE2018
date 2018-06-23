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
package org.eclipse.ecf.presence.collab.ui.view;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ViewShareRosterEntryContributionItem extends AbstractRosterEntryContributionItem {

    public  ViewShareRosterEntryContributionItem() {
    // do nothing
    }

    public  ViewShareRosterEntryContributionItem(String id) {
        super(id);
    }

    protected IAction[] makeActions() {
        // Else check for Roster entry
        final IRosterEntry entry = getSelectedRosterEntry();
        final IContainer c = getContainerForRosterEntry(entry);
        // If roster entry is selected and it has a container
        if (entry != null && c != null) {
            final IChannelContainerAdapter channelAdapter = (IChannelContainerAdapter) c.getAdapter(IChannelContainerAdapter.class);
            // If the container has channel container adapter and is online/available
            if (channelAdapter != null && isAvailable(entry)) {
                final ViewShare tmp = ViewShare.getViewShare(c.getID());
                // If there is an URL share associated with this container
                if (tmp != null) {
                    final ViewShare viewshare = tmp;
                    final IAction action = new Action() {

                        public void run() {
                            viewshare.sendOpenViewRequest(entry.getRoster().getUser().getName(), entry.getUser().getID());
                        }
                    };
                    action.setText(Messages.ViewShareRosterEntryContributionItem_VIEWSHARE_MENU_TEXT);
                    action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
                    return new IAction[] { action };
                }
            }
        }
        return null;
    }
}
