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
package org.eclipse.ecf.presence.collab.ui.screencapture;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ScreenCaptureShareRosterContributionItem extends AbstractRosterContributionItem {

    public  ScreenCaptureShareRosterContributionItem() {
    // nothing
    }

    public  ScreenCaptureShareRosterContributionItem(String id) {
        super(id);
    }

    private IAction[] createActionAdd(final ID containerID, final IChannelContainerAdapter channelAdapter) {
        final IAction action = new Action() {

            public void run() {
                try {
                    ScreenCaptureShare.addScreenCaptureShare(containerID, channelAdapter);
                } catch (ECFException e) {
                    e.printStackTrace();
                }
            }
        };
        action.setText(Messages.ScreenCaptureShareRosterContributionItem_SCREENCAPTURESHARE_LISTENER_MENU_ADD_TEXT);
        action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
        return new IAction[] { action };
    }

    private IAction[] createActionRemove(final ID containerID, final ScreenCaptureShare screenCaptureShare) {
        final IAction action = new Action() {

            public void run() {
                ScreenCaptureShare.removeScreenCaptureShare(containerID);
                screenCaptureShare.dispose();
            }
        };
        action.setText(Messages.ScreenCaptureShareRosterContributionItem_SCREENCAPTURESHARE_LISTENER_MENU_REMOVE_TEXT);
        return new IAction[] { action };
    }

    protected IAction[] makeActions() {
        final IRoster roster = getSelectedRoster();
        if (roster != null) {
            // Roster is selected
            final IContainer c = getContainerForRoster(roster);
            if (c != null) {
                // Get existing ScreenCaptureShare for this container (if it exists)
                final ScreenCaptureShare screenCaptureShare = ScreenCaptureShare.getScreenCaptureShare(c.getID());
                // If it does exist already, then create action to remove
                if (screenCaptureShare != null)
                    return createActionRemove(c.getID(), screenCaptureShare);
                final IChannelContainerAdapter channelAdapter = (IChannelContainerAdapter) c.getAdapter(IChannelContainerAdapter.class);
                return (channelAdapter == null) ? null : createActionAdd(c.getID(), channelAdapter);
            }
        }
        return null;
    }
}
