/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.irc.datashare;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.internal.provider.irc.Activator;
import org.eclipse.ecf.internal.provider.irc.container.IRCRootContainer;
import org.eclipse.ecf.provider.datashare.nio.NIOChannel;
import org.eclipse.ecf.provider.datashare.nio.NIODatashareContainer;

public class IRCDatashareContainer extends NIODatashareContainer implements IIRCDatashareContainer {

    private List channels = new ArrayList();

    private IRCRootContainer container;

    public  IRCDatashareContainer(IRCRootContainer container) {
        super(container);
        this.container = container;
        container.addListener(new IContainerListener() {

            public void handleEvent(IContainerEvent event) {
                if (event instanceof IContainerDisconnectedEvent || event instanceof IContainerDisposeEvent) {
                    setIP(null);
                    channels.clear();
                }
            }
        });
    }

    protected void log(IStatus status) {
        Activator.getDefault().log(status);
    }

    public void setIP(String ip) {
        for (int i = 0; i < channels.size(); i++) {
            IRCDatashareChannel channel = (IRCDatashareChannel) channels.get(i);
            channel.setIP(ip);
        }
    }

    protected NIOChannel createNIOChannel(ID channelId, IChannelListener listener, Map properties) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(channelId, "Channel id cannot be null");
        NIOChannel channel = new IRCDatashareChannel(this, container.getConnectNamespace(), container.getConnectedID(), container.getChatRoomMessageSender(), channelId, listener);
        channels.add(channel);
        return channel;
    }

    protected NIOChannel createNIOChannel(IChannelConfig newChannelConfig) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(newChannelConfig, "Channel config cannot be null");
        return createNIOChannel(newChannelConfig.getID(), newChannelConfig.getListener(), newChannelConfig.getProperties());
    }

    public Namespace getChannelNamespace() {
        return IDFactory.getDefault().getNamespaceByName(StringID.class.getName());
    }
}
