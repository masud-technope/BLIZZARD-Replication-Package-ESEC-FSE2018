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
package org.eclipse.ecf.provider.datashare.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisposeEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelContainerListener;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelActivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelDeactivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerEvent;

/**
 * A datashare channel container implementation that creates channels that uses
 * NIO for sending and receiving messages.
 * <p>
 * The channel container facilitates communication at a socket-level to a
 * corresponding {@link NIOChannel}. When a request has been received from a
 * remote peer to establish a channel connection, the request can be honoured
 * via calling the {@link #enqueue(SocketAddress)} method with the remote peer's
 * corresponding socket address as the parameter.
 * </p>
 * <p>
 * Subclasses must implement the following:
 * <ul>
 * <li>For channel creation:
 * <ul>
 * <li>{@link #createNIOChannel(ID, IChannelListener, Map)}</li>
 * </ul>
 * <ul>
 * <li>{@link #createNIOChannel(IChannelConfig)}</li>
 * </ul>
 * </li>
 * <li>To facilitate the logging of statuses:
 * <ul>
 * <li>{@link #log(IStatus)}</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public abstract class NIODatashareContainer implements IChannelContainerAdapter {

    /**
	 * A thread for establishing a connection to remote clients.
	 */
    private Thread connectionThread;

    /**
	 * A list of IP addresses that should be connected to.
	 */
    private LinkedList pendingConnections;

    /**
	 * A list of socket channels that needs to be processed for handshaking with
	 * the remote peer.
	 */
    private List pendingSockets;

    /**
	 * A map of datashare channels owned by this container mapped by their ids.
	 */
    private Map channels;

    /**
	 * The parent container of this datashare container.
	 */
    private IContainer container;

    /**
	 * List of IChannelContainerListeners attached to this datashare container.
	 */
    private ListenerList listenerList;

    /**
	 * Instantiates a new datashare container that will connect to remote
	 * clients using NIO functionality.
	 * 
	 * @param container
	 *            the parent container of this datashare container, must not be
	 *            <code>null</code>
	 */
    public  NIODatashareContainer(IContainer container) {
        //$NON-NLS-1$
        Assert.isNotNull(container, "Container cannot be null");
        this.container = container;
        container.addListener(new IContainerListener() {

            public void handleEvent(IContainerEvent event) {
                if (event instanceof IContainerConnectedEvent) {
                    ID id = ((IContainerConnectedEvent) event).getTargetID();
                    fireChannelConnectedEvent(id);
                } else if (event instanceof IContainerDisconnectedEvent) {
                    ID id = ((IContainerDisconnectedEvent) event).getTargetID();
                    fireChannelDisconnectedEvent(id);
                    disconnect();
                } else if (event instanceof IContainerDisposeEvent) {
                    // also invoke disconnect() here in case a disconnection
                    // event was never fired
                    disconnect();
                }
            }
        });
        channels = new HashMap();
        pendingConnections = new LinkedList();
        pendingSockets = new ArrayList();
        listenerList = new ListenerList();
    }

    /**
	 * Fires a channel connected event to all of this channel container's
	 * channels notifying that the parent container has connected to the
	 * specified target id.
	 * 
	 * @param containerTargetId
	 *            the target id that the parent container has connected to
	 */
    private void fireChannelConnectedEvent(ID containerTargetId) {
        synchronized (channels) {
            for (Iterator it = channels.values().iterator(); it.hasNext(); ) {
                NIOChannel channel = (NIOChannel) it.next();
                channel.fireChannelConnectEvent(containerTargetId);
            }
        }
    }

    /**
	 * Fires a channel disconnected event to all of this channel container's
	 * channels notifying that the parent container has disconnected from the
	 * specified target id.
	 * 
	 * @param containerTargetId
	 *            the target id that the parent container has disconnected from
	 */
    private void fireChannelDisconnectedEvent(ID id) {
        synchronized (channels) {
            for (Iterator it = channels.values().iterator(); it.hasNext(); ) {
                NIOChannel channel = (NIOChannel) it.next();
                channel.fireChannelDisconnectEvent(id);
            }
        }
    }

    /**
	 * Notifies the specified listener about the event.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @param event
	 *            the event to notify the listener of
	 */
    private void fireChannelContainerEvent(final IChannelContainerListener listener, final IChannelContainerEvent event) {
        // use a SafeRunner to send out the notification to ensure that
        // client-side failures do not cause the channel to die
        SafeRunner.run(new ISafeRunnable() {

            public void run() throws Exception {
                listener.handleChannelContainerEvent(event);
            }

            public void handleException(Throwable t) {
                log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Error handling channel container event", t));
            }
        });
    }

    private void fireChannelContainerActivatedEvent(final ID channelId) {
        Object[] listeners = listenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            IChannelContainerListener listener = (IChannelContainerListener) listeners[i];
            fireChannelContainerEvent(listener, new IChannelContainerChannelActivatedEvent() {

                public ID getChannelID() {
                    return channelId;
                }

                public ID getChannelContainerID() {
                    return container.getID();
                }

                public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("IChannelContainerChannelActivatedEvent[");
                    //$NON-NLS-1$
                    buffer.append(//$NON-NLS-1$
                    "container=").append(container.getID());
                    //$NON-NLS-1$
                    buffer.append(",channel=").append(//$NON-NLS-1$
                    channelId);
                    buffer.append(']');
                    return buffer.toString();
                }
            });
        }
    }

    void fireChannelContainerDeactivatedEvent(final ID channelId) {
        Object[] listeners = listenerList.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            IChannelContainerListener listener = (IChannelContainerListener) listeners[i];
            fireChannelContainerEvent(listener, new IChannelContainerChannelDeactivatedEvent() {

                public ID getChannelID() {
                    return channelId;
                }

                public ID getChannelContainerID() {
                    return container.getID();
                }

                public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("IChannelContainerChannelDeactivatedEvent[");
                    //$NON-NLS-1$
                    buffer.append(//$NON-NLS-1$
                    "container=").append(container.getID());
                    //$NON-NLS-1$
                    buffer.append(",channel=").append(//$NON-NLS-1$
                    channelId);
                    buffer.append(']');
                    return buffer.toString();
                }
            });
        }
    }

    protected abstract void log(IStatus status);

    /**
	 * Stores the specified channel based on the given ID into this container.
	 * 
	 * @param channel
	 *            the channel to store
	 */
    private void storeChannel(IChannel channel) {
        channels.put(channel.getID(), channel);
    }

    private void disconnect() {
        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;
        }
        synchronized (pendingConnections) {
            pendingConnections.clear();
        }
        synchronized (pendingSockets) {
            for (int i = 0; i < pendingSockets.size(); i++) {
                SocketChannel channel = (SocketChannel) pendingSockets.get(i);
                Util.closeChannel(channel);
            }
            pendingSockets.clear();
        }
        synchronized (channels) {
            for (Iterator it = channels.values().iterator(); it.hasNext(); ) {
                final IChannel channel = (IChannel) it.next();
                // dispose the channel in a SafeRunner so exceptions don't
                // prevent us from disposing other channels
                SafeRunner.run(new ISafeRunnable() {

                    public void run() throws Exception {
                        channel.dispose();
                    }

                    public void handleException(Throwable t) {
                        log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Error disposing channel: " + channel, t));
                    }
                });
            }
            channels.clear();
        }
    }

    /**
	 * Attempts to connect to a remote address that has been enqueued to this
	 * channel container for processing via the {@link #enqueue(SocketAddress)}
	 * method.
	 * 
	 * @param buffer
	 *            the buffer to use for reading and writing data
	 * @throws IOException
	 *             if an IO error occurs while attempting to contact the peer
	 */
    private void connect(ByteBuffer buffer) throws IOException {
        while (!pendingConnections.isEmpty()) {
            // retrieve an IP address to connect to
            SocketAddress remote = (SocketAddress) pendingConnections.removeFirst();
            // open a socket channel to the remote address
            SocketChannel socketChannel = SocketChannel.open(remote);
            byte[] bytes = Util.serialize(container.getConnectedID());
            if (bytes == null) {
                // serialization failed, close the socket
                Util.closeChannel(socketChannel);
                return;
            }
            socketChannel.configureBlocking(false);
            Util.write(socketChannel, buffer, bytes);
            pendingSockets.add(socketChannel);
        }
    }

    /**
	 * Enqueues the specified address to be connected to. This should be invoked
	 * after a request has been received from a remote user.
	 * 
	 * @param address
	 *            the address to connect to, cannot be <code>null</code>
	 * @see NIOChannel#sendRequest(ID)
	 */
    public void enqueue(SocketAddress address) {
        //$NON-NLS-1$
        Assert.isNotNull(address, "Socket address cannot be null");
        if (connectionThread == null) {
            connectionThread = new Thread(new ConnectionRunnable(), getClass().getName() + "Thread-" + //$NON-NLS-1$
            container.getID().toString());
            connectionThread.start();
        }
        pendingConnections.add(address);
    }

    /**
	 * Performs a handshake operation with the remote peer.
	 * 
	 * @param socketChannel
	 *            the socket channel to handshake with
	 * @param data
	 *            the data sent from the channel thus far
	 * @throws ClassNotFoundException
	 *             if a deserialization error occurs
	 * @throws IOException
	 *             if an IO error occurs while reading or writing data
	 */
    private void handshake(SocketChannel socketChannel, ChannelData data) throws ClassNotFoundException, IOException {
        // retrieve the data that was sent
        byte[] message = data.getData();
        // read in the response
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        ObjectInputStream ois = new ObjectInputStream(bais);
        // first response should be the channel id
        ID channelId = (ID) ois.readObject();
        synchronized (channels) {
            // retrieve the channel that corresponds to that id
            IChannel channel = getChannel(channelId);
            if (channel == null) {
                // can't find a channel that corresponds to the id, close the
                // socket
                Util.closeChannel(socketChannel);
            } else {
                // open another ObjectInputStream because each object is
                // serialized separately
                ois = new ObjectInputStream(bais);
                // next id is the id of the remote user
                ID peerId = (ID) ois.readObject();
                // store the peer id and the corresponding socket in the
                // retrieved NIO channel
                NIOChannel datashare = (NIOChannel) channel;
                datashare.put(peerId, socketChannel);
                // check if we have any bytes left to read
                int available = bais.available();
                if (available != 0) {
                    // if there are extra bytes that means this is data that
                    // the sender has sent to us, we must process these messages
                    byte[] received = new byte[available];
                    // copy the remaining information
                    System.arraycopy(message, message.length - available, received, 0, available);
                    // process the received data
                    datashare.processIncomingMessage(socketChannel, received);
                }
            }
        }
    }

    /**
	 * Processes any pending sockets that are currently queued up in this
	 * channel container and is waiting to initiate the handshake process with
	 * the remote peer.
	 * 
	 * @param buffer
	 *            the buffer to use for reading and writing data
	 * @throws ClassNotFoundException
	 *             if a serialization or deserialization operation encountered
	 *             errors
	 * @throws IOException
	 *             if an IO error occurs while reading or writing data
	 */
    private void processPendingSockets(ByteBuffer buffer) throws ClassNotFoundException, IOException {
        for (int i = 0; i < pendingSockets.size(); i++) {
            SocketChannel socketChannel = (SocketChannel) pendingSockets.get(i);
            buffer.clear();
            // read in the response
            ChannelData data = Util.read(socketChannel, buffer);
            if (!data.isOpen()) {
                // the channel isn't open, we should close it on our end also
                Util.closeChannel(socketChannel);
                // remove this channel
                pendingSockets.remove(i);
                i--;
            } else if (data.getData() != null) {
                try {
                    handshake(socketChannel, data);
                } finally {
                    // this socket has been processed, remove it
                    pendingSockets.remove(i);
                    i--;
                }
            }
        }
    }

    /**
	 * Creates a new NIO-capable channel within this container.
	 * 
	 * @param channelId
	 *            the ID of the channel, must not be <code>null</code>
	 * @param listener
	 *            the listener for receiving notifications pertaining to the
	 *            created channel, may be <code>null</code> if no listener needs
	 *            to be notified
	 * @param properties
	 *            a map of properties to provide to this channel, may be
	 *            <code>null</code>
	 * @return the created NIOChannel instance
	 * @throws ECFException
	 *             if an error occurred while creating the channel
	 * @see #createChannel(ID, IChannelListener, Map)
	 */
    protected abstract NIOChannel createNIOChannel(ID channelId, IChannelListener listener, Map properties) throws ECFException;

    /**
	 * Creates a new NIO-capable channel within this container.
	 * 
	 * @param newChannelConfig
	 *            the configuration for the newly created channel, must not be
	 *            <code>null</code>
	 * @return the created NIOChannel instance
	 * @throws ECFException
	 *             if an error occurred while creating the channel
	 * @see #createChannel(IChannelConfig)
	 */
    protected abstract NIOChannel createNIOChannel(IChannelConfig newChannelConfig) throws ECFException;

    public final IChannel createChannel(ID channelId, IChannelListener listener, Map properties) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(channelId, "Channel id cannot be null");
        IChannel channel = createNIOChannel(channelId, listener, properties);
        if (channel != null) {
            storeChannel(channel);
            fireChannelContainerActivatedEvent(channelId);
        }
        return channel;
    }

    public final IChannel createChannel(IChannelConfig newChannelConfig) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(newChannelConfig, "Channel config cannot be null");
        Assert.isNotNull(newChannelConfig.getID(), //$NON-NLS-1$
        "Channel config id cannot be null");
        IChannel channel = createNIOChannel(newChannelConfig);
        if (channel != null) {
            storeChannel(channel);
            fireChannelContainerActivatedEvent(newChannelConfig.getID());
        }
        return channel;
    }

    public void addListener(IChannelContainerListener listener) {
        listenerList.add(listener);
    }

    public IChannel getChannel(ID channelId) {
        //$NON-NLS-1$
        Assert.isNotNull(channelId, "Channel id cannot be null");
        return (IChannel) channels.get(channelId);
    }

    public boolean removeChannel(ID channelId) {
        IChannel channel = (IChannel) channels.remove(channelId);
        if (channel == null) {
            return false;
        } else {
            channel.dispose();
            return true;
        }
    }

    public void removeListener(IChannelContainerListener listener) {
        listenerList.remove(listener);
    }

    public Object getAdapter(Class adapter) {
        if (adapter == null) {
            return null;
        } else if (adapter.isInstance(this)) {
            return this;
        } else if (adapter == IContainer.class) {
            return container;
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        //$NON-NLS-1$
        buffer.append("[parentContainer=").append(container).append(']');
        return buffer.toString();
    }

    private class ConnectionRunnable implements Runnable {

        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                try {
                    buffer.clear();
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    synchronized (pendingConnections) {
                        if (!pendingConnections.isEmpty()) {
                            connect(buffer);
                        }
                    }
                    processPendingSockets(buffer);
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    return;
                } catch (ClassNotFoundException e) {
                    log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Could not deserialize", e));
                } catch (IOException e) {
                    log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "An IO error occurred", e));
                } catch (RuntimeException e) {
                    log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "A runtime error occurred", e));
                }
            }
        }
    }
}
