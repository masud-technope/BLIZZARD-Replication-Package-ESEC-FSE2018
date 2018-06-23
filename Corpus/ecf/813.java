/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare;

import java.util.HashMap;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ScribbleClient {

    protected static final String CONTAINER_TYPE = "ecf.generic.channel";

    protected static final String TARGET_SERVER = "ecftcp://localhost:3282/server";

    protected static final String CHANNEL_ID = "scribble";

    IContainer container = null;

    ScribbleView scribbleView = null;

    protected void createChannel() throws ECFException {
        // Get IChannelContainerAdapter adapter
        IChannelContainerAdapter channelContainer = (IChannelContainerAdapter) container.getAdapter(IChannelContainerAdapter.class);
        // Create channel ID with fixed name 'channel2'
        final ID channelID = IDFactory.getDefault().createID(channelContainer.getChannelNamespace(), CHANNEL_ID);
        // Setup listener so then when channelmessageevents are received that
        // they present in UI
        final IChannelListener channelListener = new IChannelListener() {

            public void handleChannelEvent(final IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    IChannelMessageEvent msg = (IChannelMessageEvent) event;
                    scribbleView.handleDrawLine(msg.getData());
                }
            }
        };
        // Create new channel
        IChannel channel = channelContainer.createChannel(channelID, channelListener, new HashMap());
        // Set the view to use the given channel (for sending)
        scribbleView.setChannel(channel);
    }

    protected void openScribbleView() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                try {
                    IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    IWorkbenchPage wp = ww.getActivePage();
                    IViewPart view = wp.showView("org.eclipse.ecf.tutorial.paintshare");
                    // setup member variable with view
                    scribbleView = (ScribbleView) view;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void createAndConnect() throws ECFException {
        // create container instance from ECF ContainerFactory
        container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);
        // open scribble view
        openScribbleView();
        // create channel
        createChannel();
        // create target ID
        // connect container to target
        container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), TARGET_SERVER), null);
    }
}
