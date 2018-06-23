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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelConnectEvent;
import org.eclipse.ecf.datashare.events.IChannelDisconnectEvent;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;

/**
 * An abstract implementation of <code>IChannel</code> that uses Java 1.4 NIO
 * APIs for sending and retrieving data.
 * <p>
 * This channel will inherently spawn multiple socket connections as messages
 * are sent to different remote clients via {@link #sendMessage(ID, byte[])}.
 * Please note that the current implementation does not handle repeated
 * invocations to that method well. Please refer to its javadoc for further
 * information.
 * </p>
 * <p>
 * Subclasses must implement the following:
 * <ul>
 * <li>For communicating local information for establishing a socket connection:
 * <ul>
 * <li>{@link #sendRequest(ID)}</li>
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
public abstract class NIOChannel implements IChannel {

    private NIODatashareContainer datashareContainer;

    /**
	 * The id of the originating owner container of the datashare container that
	 * created this channel.
	 */
    private final ID containerId;

    /**
	 * The ID of this channel.
	 */
    private final ID id;

    /**
	 * The server socket for listening to incoming connections. This channel is
	 * non-blocking.
	 */
    private ServerSocketChannel serverSocketChannel;

    /**
	 * The port that the server socket is listening on for incoming connections.
	 * 
	 * @see #serverSocketChannel
	 * @see #getLocalPort()
	 */
    private final int localPort;

    /**
	 * A map of <code>ID</code>s to their corresponding
	 * <code>SocketChannel</code>s.
	 */
    private Map connectedSockets;

    /**
	 * A list of sockets that is waiting to handshake with remote peers.
	 */
    private List pendingSockets;

    /**
	 * A queue of messages that needs to be sent to remote clients.
	 */
    private LinkedList messages;

    /**
	 * This channel's listener. May be <code>null</code>.
	 */
    private IChannelListener listener;

    /**
	 * The thread responsible for processing incoming messages and sending
	 * messages to remote peers.
	 */
    private Thread processingThread;

    /**
	 * Instantiates a new channel for sending and receiving messages in a
	 * non-blocking manner via sockets.
	 * 
	 * @param datashareContainer
	 *            the source NIODatashareContainer that created this channel,
	 *            cannot be <code>null</code>
	 * @param containerId
	 *            the id of the originating owner container, this should
	 *            <b>not</b> be the id of the datashare container that created
	 *            this channel but the parent container of the datashare
	 *            container, may not be <code>null</code>
	 * @param id
	 *            the id of this channel, may not be <code>null</code>
	 * @param listener
	 *            the channel listener for this channel, may be
	 *            <code>null</code> if no notification is required
	 * @throws ECFException
	 *             if an error occurred while creating this channel
	 */
    public  NIOChannel(NIODatashareContainer datashareContainer, ID containerId, ID id, IChannelListener listener) throws ECFException {
        Assert.isNotNull(datashareContainer, //$NON-NLS-1$
        "Datashare container cannot be null");
        //$NON-NLS-1$
        Assert.isNotNull(containerId, "Container id cannot be null");
        //$NON-NLS-1$
        Assert.isNotNull(id, "Channel id cannot be null");
        this.datashareContainer = datashareContainer;
        this.containerId = containerId;
        this.id = id;
        this.listener = listener;
        try {
            // open a server socket
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new ECFException(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Could not create server socket", e));
        }
        try {
            // bind to a local port
            ServerSocket socket = serverSocketChannel.socket();
            socket.bind(getBindAddress(), getBackLog());
        } catch (IOException e) {
            throw new ECFException(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Could not bind server socket", e));
        }
        localPort = serverSocketChannel.socket().getLocalPort();
        connectedSockets = new HashMap();
        pendingSockets = new ArrayList();
        messages = new LinkedList();
        processingThread = new Thread(new ProcessingRunnable(), getClass().getName() + "Thread-" + //$NON-NLS-1$
        id.toString());
        processingThread.start();
    }

    /**
	 * Fires a channel connected event to this channel's listener if there is
	 * one attached.
	 * 
	 * @param containerId
	 *            the target ID of the container has connected to
	 */
    void fireChannelConnectEvent(final ID containerId) {
        IChannelListener listener = getListener();
        if (listener != null) {
            fireChannelEvent(listener, new IChannelConnectEvent() {

                public ID getChannelID() {
                    return id;
                }

                public ID getTargetID() {
                    return containerId;
                }

                public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("IChannelConnectEvent[");
                    //$NON-NLS-1$
                    buffer.append("channel=").append(//$NON-NLS-1$
                    id);
                    //$NON-NLS-1$
                    buffer.append(",target=").append(containerId).append(//$NON-NLS-1$
                    ']');
                    return buffer.toString();
                }
            });
        }
    }

    /**
	 * Fires a channel disconnected event to this channel's listener if there is
	 * one attached.
	 * 
	 * @param containerId
	 *            the target ID of the container has disconnected from
	 */
    void fireChannelDisconnectEvent(final ID containerId) {
        IChannelListener listener = getListener();
        if (listener != null) {
            fireChannelEvent(listener, new IChannelDisconnectEvent() {

                public ID getChannelID() {
                    return id;
                }

                public ID getTargetID() {
                    return containerId;
                }

                public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("IChannelDisconnectEvent[");
                    //$NON-NLS-1$
                    buffer.append("channel=").append(//$NON-NLS-1$
                    id);
                    //$NON-NLS-1$
                    buffer.append(",target=").append(containerId).append(//$NON-NLS-1$
                    ']');
                    return buffer.toString();
                }
            });
        }
    }

    protected abstract void log(IStatus status);

    /**
	 * Returns the address that this channel's server socket should bind to. If
	 * <code>null</code>, a default port and valid local address will be used.
	 * 
	 * @return this channel's server socket's bind address, may be
	 *         <code>null</code> if a default should be used
	 */
    protected SocketAddress getBindAddress() {
        return null;
    }

    /**
	 * Retrieves the listen backlog length of this channel's server socket. If
	 * the value is less than or equal to zero, the default length is used.
	 * 
	 * @return this channel's server socket's listen backlog length
	 */
    protected int getBackLog() {
        return 0;
    }

    /**
	 * Sends any pending messages we may have queued up.
	 */
    private void sendPendingMessages() {
        Collection deadSockets = null;
        Collection processedMessages = null;
        for (Iterator it = messages.iterator(); it.hasNext(); ) {
            ChannelMessage message = (ChannelMessage) it.next();
            ID id = message.getId();
            SocketChannel channel = (SocketChannel) connectedSockets.get(id);
            // check if we have a socket for the target of this message
            if (channel != null) {
                byte[] data = message.getData();
                try {
                    // flush the data directly with regular IO, this method
                    // saves us the extra work of having to constantly flip and
                    // clear a ByteBuffer and in a way ensures the message is
                    // sent in one piece instead of chunks
                    channel.configureBlocking(true);
                    channel.socket().getOutputStream().write(data);
                    channel.socket().getOutputStream().flush();
                    // turn off blocking
                    channel.configureBlocking(false);
                } catch (IOException e) {
                    log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Error occurred while sending message", e));
                    if (deadSockets == null) {
                        deadSockets = new HashSet();
                    }
                    deadSockets.add(id);
                }
                if (processedMessages == null) {
                    processedMessages = new LinkedList();
                }
                // store the processed message
                processedMessages.add(message);
            }
        }
        // remove all messages that have been processed
        if (processedMessages != null) {
            messages.removeAll(processedMessages);
        }
        if (deadSockets != null) {
            for (Iterator it = deadSockets.iterator(); it.hasNext(); ) {
                ID id = (ID) it.next();
                SocketChannel channel = (SocketChannel) connectedSockets.remove(id);
                Util.closeChannel(channel);
            }
        }
    }

    /**
	 * Reads in any incoming messages from remote clients.
	 * 
	 * @param buffer
	 *            the buffer to use for reading the socket
	 * @throws IOException
	 *             if an error occurs while reading from the socket
	 */
    private void processIncomingMessages(ByteBuffer buffer) throws IOException {
        Collection deadSockets = null;
        for (Iterator it = connectedSockets.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            SocketChannel socketChannel = (SocketChannel) entry.getValue();
            try {
                if (!processIncomingMessages(socketChannel, buffer)) {
                    if (deadSockets == null) {
                        deadSockets = new HashSet();
                    }
                    deadSockets.add(entry.getKey());
                }
            } catch (IOException e) {
                log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Error occurred while sending message", e));
                if (deadSockets == null) {
                    deadSockets = new HashSet();
                }
                deadSockets.add(entry.getKey());
            }
        }
        if (deadSockets != null) {
            for (Iterator it = deadSockets.iterator(); it.hasNext(); ) {
                ID id = (ID) it.next();
                SocketChannel channel = (SocketChannel) connectedSockets.remove(id);
                Util.closeChannel(channel);
            }
        }
    }

    /**
	 * Processes any incoming messages from the specified channel by reading it
	 * into the specified buffer and returns whether the channel has reached
	 * end-of-stream.
	 * 
	 * @param socketChannel
	 *            the channel to read messages from
	 * @param buffer
	 *            the buffer to use to read the messages into
	 * @return <code>true</code> if the channel is still active,
	 *         <code>false</code> has reached end-of-stream
	 * @throws IOException
	 *             if an error occurred while trying to read from the channel
	 */
    private boolean processIncomingMessages(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        ChannelData channelData = Util.read(socketChannel, buffer);
        byte[] message = channelData.getData();
        if (message != null) {
            processIncomingMessage(socketChannel, message);
        }
        return channelData.isOpen();
    }

    /**
	 * Processes the message that has been received from the specified channel.
	 * 
	 * @param socketChannel
	 *            the channel that the message was from
	 * @param message
	 *            the message that was received
	 */
    void processIncomingMessage(SocketChannel socketChannel, byte[] message) {
        // we read something, need to notify
        IChannelListener listener = getListener();
        if (listener != null) {
            // we have a listener, convert our data and then notify
            byte[][] messages = convert(message);
            if (messages != null) {
                fireMessageEvents(listener, socketChannel, messages);
            }
        }
    }

    /**
	 * Converts the data that has been read from the socket into separate byte[]
	 * instances.
	 * 
	 * @param message
	 *            the data read from the socket
	 * @return a byte[][] containing the individual messages
	 */
    private byte[][] convert(byte[] message) {
        try {
            // back the read in data with a ByteArrayInputStream
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            // instantiate an ObjectInputStream and read the individual
            // byte[]
            byte[] bytes = (byte[]) new ObjectInputStream(bais).readObject();
            if (bais.available() == 0) {
                return new byte[][] { bytes };
            }
            Collection c = new ArrayList();
            c.add(bytes);
            while (bais.available() != 0) {
                // instantiate an ObjectInputStream and read the individual
                // byte[]
                bytes = (byte[]) new ObjectInputStream(bais).readObject();
                // store it
                c.add(bytes);
            }
            // return all the individual byte[]s
            return (byte[][]) c.toArray(new byte[c.size()][]);
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
	 * Fires message events to the specified listener for each of the message
	 * that was received.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @param socketChannel
	 *            the socket that the message was read from
	 * @param messages
	 *            the messages that have been received
	 */
    private void fireMessageEvents(IChannelListener listener, SocketChannel socketChannel, byte[][] messages) {
        for (int i = 0; i < messages.length; i++) {
            IChannelEvent event = createMessageEvent(socketChannel, messages[i]);
            if (event != null) {
                fireChannelEvent(listener, event);
            }
        }
    }

    /**
	 * Notifies the specified listener of the given channel event. The code is
	 * run within a SafeRunner to ensure that the program flow is not affected
	 * in the event of errors during notification.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @param event
	 *            the event to fire
	 */
    private void fireChannelEvent(final IChannelListener listener, final IChannelEvent event) {
        // use a SafeRunner to send out the notification to ensure that
        // client-side failures do not cause the channel to die
        SafeRunner.run(new ISafeRunnable() {

            public void run() throws Exception {
                listener.handleChannelEvent(event);
            }

            public void handleException(Throwable t) {
                log(new Status(IStatus.ERROR, Util.PLUGIN_ID, "Error handling channel event", t));
            }
        });
    }

    /**
	 * Creates and returns a message event corresponding to the specified
	 * channel and the data that was read.
	 * 
	 * @param channel
	 *            the socket channel that the message was from
	 * @param data
	 *            the message from the remote peer
	 * @return a message event describing the received message, may be
	 *         <code>null</code> if the channel could not be identified
	 */
    private IChannelEvent createMessageEvent(SocketChannel channel, final byte[] data) {
        // search for the id of the corresponding channel
        for (Iterator it = connectedSockets.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            if (channel == entry.getValue()) {
                final ID fromId = (ID) entry.getKey();
                return new IChannelMessageEvent() {

                    public byte[] getData() {
                        return data;
                    }

                    public ID getFromContainerID() {
                        return fromId;
                    }

                    public ID getChannelID() {
                        return id;
                    }

                    public String toString() {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("IChannelMessageEvent[");
                        //$NON-NLS-1$
                        buffer.append("container=").append(//$NON-NLS-1$
                        fromId);
                        //$NON-NLS-1$
                        buffer.append(",channel=").append(//$NON-NLS-1$
                        id);
                        //$NON-NLS-1$
                        buffer.append(",data=").append(data).append(//$NON-NLS-1$
                        ']');
                        return buffer.toString();
                    }
                };
            }
        }
        return null;
    }

    /**
	 * Stores the specified ID with its corresponding socket into this channel.
	 * The socket will now be actively used for reading and sending messages.
	 * 
	 * @param id
	 *            the target that the socket is connected with
	 * @param socketChannel
	 *            the socket channel to be stored
	 */
    void put(ID id, SocketChannel socketChannel) {
        connectedSockets.put(id, socketChannel);
    }

    /**
	 * Accept the socket as a potential client and attempt to handshake with it.
	 * 
	 * @param socketChannel
	 *            the socket to establish a connection with
	 * @throws ClassNotFoundException
	 *             if the class of an object being deserialized could not be
	 *             found
	 * @throws IOException
	 *             if a networking error occurs with the socket while
	 *             reading/sending messages
	 */
    // private boolean accept2(SocketChannel socketChannel, ByteBuffer buffer)
    // throws ClassNotFoundException, IOException {
    // int read = socketChannel.read(buffer);
    // buffer.flip();
    // byte[] bytes = new byte[read];
    // buffer.get(bytes);
    // buffer.clear();
    //
    // ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
    // bytes));
    // Object object = ois.readObject();
    //
    // if (object instanceof ID) {
    // bytes = serialize(id);
    // socketChannel.socket().getOutputStream().write(bytes);
    //
    // bytes = serialize(containerId);
    // socketChannel.socket().getOutputStream().write(bytes);
    // socketChannel.socket().getOutputStream().flush();
    //
    // socketChannel.configureBlocking(false);
    // put((ID) object, socketChannel);
    // }
    // return true;
    // }
    /**
	 * Performs a handshake with a remote peer via the provided socket channel
	 * and returns whether <code>true</code> if no further attempts are
	 * required. Note that <code>true</code> does not indicate that the
	 * handshake has been successful.
	 * <p>
	 * For example, if the remote peer has closed this channel then
	 * <code>true</code> would be returned as no further attempts should be
	 * attempted.
	 * </p>
	 * 
	 * @param socketChannel
	 *            the channel to use to handshake with the remote peer
	 * @param buffer
	 *            the buffer to use for reading and writing data from the
	 *            channel
	 * @return <code>true</code> if no further handshake attempts are required,
	 *         <code>false</code> otherwise
	 * @throws ClassNotFoundException
	 *             if deserialization failed during the handshake
	 * @throws IOException
	 *             if an IO error occurred while performing the handshake
	 */
    private boolean handshake(SocketChannel socketChannel, ByteBuffer buffer) throws ClassNotFoundException, IOException {
        ChannelData data = Util.read(socketChannel, buffer);
        if (!data.isOpen()) {
            // this channel is dead, close it
            Util.closeChannel(socketChannel);
            return true;
        }
        byte[] bytes = data.getData();
        if (bytes == null) {
            return false;
        }
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object object = ois.readObject();
        if (object instanceof ID) {
            socketChannel.configureBlocking(true);
            byte[] one = Util.serialize(id);
            byte[] two = Util.serialize(containerId);
            bytes = new byte[one.length + two.length];
            System.arraycopy(one, 0, bytes, 0, one.length);
            System.arraycopy(two, 0, bytes, one.length, two.length);
            socketChannel.socket().getOutputStream().write(bytes);
            socketChannel.socket().getOutputStream().flush();
            socketChannel.configureBlocking(false);
            put((ID) object, socketChannel);
        }
        return true;
    }

    /**
	 * Returns the port that is currently open for incoming socket connections.
	 * 
	 * @return the open port for socket connections
	 * @see #sendRequest(ID)
	 * @see NIODatashareContainer#enqueue(SocketAddress)
	 */
    protected final int getLocalPort() {
        return localPort;
    }

    /**
	 * Sends a request to the receiver to notify them that a socket is open and
	 * waiting for incoming connections to establish a channel connection. It is
	 * up to the client to decide how this request should be sent as the
	 * communication channel between one client and another is entirely
	 * dependent on the underlying provider's networking protocol.
	 * <p>
	 * This method will be invoked when a socket corresponding to the receiver's
	 * ID cannot be found.
	 * </p>
	 * 
	 * @param receiver
	 *            the receiver to contact, will not be <code>null</code>
	 * @throws ECFException
	 *             if an error occurred while attempting to send the request
	 * @see #getLocalPort()
	 * @see NIODatashareContainer#enqueue(SocketAddress)
	 */
    protected abstract void sendRequest(ID receiver) throws ECFException;

    public void sendMessage(byte[] message) throws ECFException {
        throw new ECFException(new Status(IStatus.ERROR, Util.PLUGIN_ID, "A receiver must be specified, see sendMessage(ID, byte[])"));
    }

    /**
	 * Sends a message to a remote instance of this channel of the target peer.
	 * <p>
	 * <b>Note:</b> The current implementation does not handle repeated
	 * invocations of this method in succession prior to a socket connection
	 * established. For optimal performance and some assurance of success, there
	 * needs to be a time lag between the first message that is sent and the
	 * ones that follow it. This lag should hopefully allow the provider
	 * sufficient time for establishing a socket connection with the remote
	 * peer. Otherwise, there may be multiple invocations of
	 * {@link #sendRequest(ID)} and clients are responsible for handling this
	 * individually.
	 * </p>
	 * 
	 * @param receiver
	 *            the receiver to send the message to, must not be
	 *            <code>null</code>
	 * @param message
	 *            the message to send, must not be <code>null</code>
	 */
    public void sendMessage(ID receiver, byte[] message) throws ECFException {
        //$NON-NLS-1$
        Assert.isNotNull(receiver, "A receiver must be specified");
        //$NON-NLS-1$
        Assert.isNotNull(message, "Message cannot be null");
        // check if we already have a socket for this receiver
        if (!connectedSockets.containsKey(receiver)) {
            // send a request to the receiver for establishing a socket
            // connection
            sendRequest(receiver);
        }
        synchronized (messages) {
            // enqueue the message for processing
            messages.add(new ChannelMessage(receiver, message));
        }
    }

    /**
	 * Disposes of this channel. Clients may extend to perform additional
	 * clean-up but <b>must</b> call <code>super.dispose()</code> before the
	 * method returns.
	 */
    public void dispose() {
        processingThread.interrupt();
        try {
            // turn off the server to prevent and deny incoming connections
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
                serverSocketChannel = null;
            }
        } catch (IOException e) {
            serverSocketChannel = null;
        }
        synchronized (connectedSockets) {
            // close all connections
            for (Iterator it = connectedSockets.values().iterator(); it.hasNext(); ) {
                SocketChannel socket = (SocketChannel) it.next();
                Util.closeChannel(socket);
            }
            connectedSockets.clear();
        }
        datashareContainer.fireChannelContainerDeactivatedEvent(id);
    }

    public IChannelListener getListener() {
        return listener;
    }

    public IChannelListener setListener(IChannelListener listener) {
        IChannelListener previous = this.listener;
        this.listener = listener;
        return previous;
    }

    public Object getAdapter(Class adapter) {
        if (adapter != null && adapter.isInstance(this)) {
            return this;
        }
        return null;
    }

    public ID getID() {
        return id;
    }

    private final class ProcessingRunnable implements Runnable {

        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                try {
                    Thread.sleep(50);
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    // perform handshaking for any pending sockets
                    for (int i = 0; i < pendingSockets.size(); i++) {
                        SocketChannel channel = (SocketChannel) pendingSockets.get(i);
                        if (handshake(channel, buffer)) {
                            // remove if handled
                            pendingSockets.remove(i);
                            i--;
                        }
                    }
                    processIncomingMessages(buffer);
                    // check if we have pending messages to send
                    synchronized (messages) {
                        if (!messages.isEmpty()) {
                            sendPendingMessages();
                        }
                    }
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    if (socketChannel != null) {
                        socketChannel.configureBlocking(false);
                        pendingSockets.add(socketChannel);
                    }
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

    private final class ChannelMessage {

        private ID fromId;

        private byte[] data;

         ChannelMessage(ID fromId, byte[] data) throws ECFException {
            this.fromId = fromId;
            this.data = convert(data);
        }

        private byte[] convert(byte[] data) throws ECFException {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(data);
                return baos.toByteArray();
            } catch (IOException e) {
                throw new ECFException(e);
            }
        }

        public ID getId() {
            return fromId;
        }

        public byte[] getData() {
            return data;
        }
    }
}
