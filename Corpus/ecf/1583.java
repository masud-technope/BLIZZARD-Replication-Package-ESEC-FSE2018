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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.internal.provider.irc.Activator;
import org.eclipse.ecf.internal.provider.irc.identity.IRCID;
import org.eclipse.ecf.presence.chatroom.IChatRoomMessageSender;
import org.eclipse.ecf.presence.im.IChatID;
import org.eclipse.ecf.provider.datashare.nio.NIOChannel;
import org.eclipse.ecf.provider.datashare.nio.NIODatashareContainer;

class IRCDatashareChannel extends NIOChannel {

    private static final String BIND_IP_PROPERTY = "org.eclipse.ecf.provider.irc.bindIP";

    private static final String BIND_PORT_PROPERTY = "org.eclipse.ecf.provider.irc.bindPort";

    /**
	 * The key value to a system property that can be used for specifying the IP
	 * that remote peers will use to connect to for establishing a connection
	 * with the local machine. If this is not set, the IP is retrieved from the
	 * IRC server directly via the <code>USERHOST</code> command.
	 * 
	 * @see #setIP(String)
	 */
    private static final String LOCAL_IP_PROPERTY = "org.eclipse.ecf.provider.irc.localIP";

    /**
	 * A namespace for instantiating IDs for message identification.
	 */
    private Namespace receiverNamespace;

    /**
	 * Our own container's target ID. This will be used for instantiating an ID
	 * that corresponds to the remote peer.
	 * 
	 * @see #receiverNamespace
	 * @see #sendMessage(ID, byte[])
	 */
    private IRCID userId;

    /**
	 * A message sender to send the connection request to.
	 */
    private IChatRoomMessageSender sender;

    /**
	 * The IP that will be sent to the remote peer for establishing a
	 * connection.
	 */
    private String ip;

     IRCDatashareChannel(NIODatashareContainer datashareContainer, Namespace receiverNamespace, ID userId, IChatRoomMessageSender sender, ID id, IChannelListener listener) throws ECFException {
        super(datashareContainer, userId, id, listener);
        this.receiverNamespace = receiverNamespace;
        this.userId = (IRCID) userId;
        this.sender = sender;
    }

    protected void log(IStatus status) {
        Activator.getDefault().log(status);
    }

    protected SocketAddress getBindAddress() {
        String bindIP = System.getProperty(BIND_IP_PROPERTY);
        String bindPort = System.getProperty(BIND_PORT_PROPERTY);
        if (bindIP == null) {
            if (bindPort == null) {
                return super.getBindAddress();
            } else {
                try {
                    return new InetSocketAddress(Integer.parseInt(bindPort));
                } catch (NumberFormatException e) {
                    Activator.log("Invalid bind property value (" + BIND_PORT_PROPERTY + ") specified: " + bindPort);
                    return super.getBindAddress();
                }
            }
        } else {
            if (bindPort == null) {
                return new InetSocketAddress(bindIP, 0);
            } else {
                try {
                    return new InetSocketAddress(bindIP, Integer.parseInt(bindPort));
                } catch (NumberFormatException e) {
                    Activator.log("Invalid bind property value (" + BIND_PORT_PROPERTY + ") specified: " + bindPort);
                    return new InetSocketAddress(bindIP, 0);
                }
            }
        }
    }

    /**
	 * Sets the IP that should be sent to the remote peer for connecting with
	 * the local computer.
	 * <p>
	 * <b>Note:</b> The provided IP address may be ignored if the
	 * <code>org.eclipse.ecf.provider.irc.localIP</code> system property has
	 * been set.
	 * </p>
	 * 
	 * @param ip
	 *            the local computer's IP
	 */
    void setIP(String ip) {
        String propertyIP = System.getProperty(LOCAL_IP_PROPERTY);
        if (propertyIP == null) {
            this.ip = ip;
        } else {
            this.ip = propertyIP;
        }
    }

    protected void sendRequest(ID receiver) throws ECFException {
        String name = ((IChatID) receiver).getUsername();
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("/msg ").append(name);
        //$NON-NLS-1$
        buffer.append(" \01ECF ");
        buffer.append(ip).append(':').append(getLocalPort());
        buffer.append('\01');
        sender.sendMessage(buffer.toString());
    }

    public void sendMessage(ID receiver, byte[] message) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(receiver, "A receiver must be specified");
        // retrieve the target's name
        String name = receiver instanceof IChatID ? ((IChatID) receiver).getUsername() : receiver.getName();
        // now create a string that is similar to our own ID, using the form
        // username@irc.freenode.net:6667
        StringBuffer buffer = new StringBuffer(name);
        buffer.append('@').append(userId.getHost());
        buffer.append(':').append(userId.getPort());
        // now create a new ID
        ID modifiedId = receiverNamespace.createInstance(new Object[] { buffer.toString() });
        // send the message with the new ID
        super.sendMessage(modifiedId, message);
    }
}
