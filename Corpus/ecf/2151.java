/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.datashare;

import java.util.HashMap;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.BaseChannelConfig;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class DsClient2 {

    protected static final String CONTAINER_TYPE = "ecf.generic.channel";

    protected static final String TARGET_SERVER = "ecftcp://localhost:3282/server";

    IContainer container = null;

    IChannel channel = null;

    protected IChannel createChannel(IContainer container) throws ECFException {
        // Get IChannelContainerAdapter adapter
        IChannelContainerAdapter channelContainer = (IChannelContainerAdapter) container.getAdapter(IChannelContainerAdapter.class);
        // Check it's valid, throw if not
        if (channelContainer == null)
            throw new NullPointerException("cannot get channel container adapter");
        // Create channel ID with fixed name 'channel2'
        final ID channelID = IDFactory.getDefault().createID(channelContainer.getChannelNamespace(), "channel2");
        // Setup listener so then when channelmessageevents are received that
        // they present in UI
        final IChannelListener channelListener = new IChannelListener() {

            public void handleChannelEvent(final IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    IChannelMessageEvent msg = (IChannelMessageEvent) event;
                    showMessageInUI(new String(msg.getData()));
                } else
                    System.out.println("got channel event " + event);
            }
        };
        // Create channel config information
        IChannelConfig config = new BaseChannelConfig(channelID, channelListener, new HashMap());
        // Create and return new channel
        return channelContainer.createChannel(config);
    }

    protected void showMessageInUI(final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Display.getCurrent().beep();
                MessageDialog.openInformation(null, "channel message", message);
            }
        });
    }

    public void createAndConnect() throws ECFException {
        // create container instance from ECF ContainerFactory
        container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);
        // create channel
        channel = createChannel(container);
        // create target ID
        // connect container to target
        container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), TARGET_SERVER), null);
    }
}
