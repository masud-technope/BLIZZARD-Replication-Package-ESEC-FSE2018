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
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.internal.presence.collab.ui.Messages;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterEntryContributionItem;
import org.eclipse.ecf.ui.screencapture.IImageSender;
import org.eclipse.ecf.ui.screencapture.ScreenCaptureJob;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ScreenCaptureShareRosterEntryContributionItem extends AbstractRosterEntryContributionItem {

    public static final long SCREEN_CAPTURE_DELAY = 5000;

    public  ScreenCaptureShareRosterEntryContributionItem() {
    // nothing
    }

    public  ScreenCaptureShareRosterEntryContributionItem(String id) {
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
                final ScreenCaptureShare tmp = ScreenCaptureShare.getScreenCaptureShare(c.getID());
                // If there is an URL share associated with this container
                if (tmp != null) {
                    final ScreenCaptureShare screencaptureshare = tmp;
                    final IAction action = new Action() {

                        public void run() {
                            MessageDialog dialog = new MessageDialog(null, Messages.ScreenCaptureShareRosterEntryContributionItem_SCREEN_CAPTURE_MESSAGEBOX_TITLE, Window.getDefaultImage(), Messages.ScreenCaptureShareRosterEntryContributionItem_SCREEN_CAPTURE_MESSAGEBOX_MESSAGE, MessageDialog.QUESTION, new String[] { NLS.bind(Messages.ScreenCaptureShareRosterEntryContributionItem_VERIFY_SEND_BUTTON_TEXT, entry.getName()), Messages.ScreenCaptureShareRosterEntryContributionItem_VERIFY_CANCEL_BUTTON_TEXT }, 0);
                            if (dialog.open() == Window.OK) {
                                ScreenCaptureJob screenCaptureJob = new ScreenCaptureJob(Display.getCurrent(), entry.getUser().getID(), entry.getUser().getName(), new IImageSender() {

                                    public void sendImage(ID targetID, ImageData imageData) {
                                        screencaptureshare.sendImage(entry.getRoster().getUser().getID(), entry.getRoster().getUser().getName(), targetID, imageData);
                                    }
                                });
                                screenCaptureJob.schedule(SCREEN_CAPTURE_DELAY);
                            }
                        }
                    };
                    action.setText(Messages.ScreenCaptureShareRosterEntryContributionItem_SCREEN_CAPTURE_MENU);
                    action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
                    return new IAction[] { action };
                }
            }
        }
        return null;
    }
}
