/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import org.osgi.service.log.LogService;
import ch.ethz.iks.r_osgi.Remoting;
import ch.ethz.iks.r_osgi.URI;
import ch.ethz.iks.r_osgi.channels.ChannelEndpoint;
import ch.ethz.iks.r_osgi.channels.NetworkChannel;
import ch.ethz.iks.r_osgi.channels.NetworkChannelFactory;
import ch.ethz.iks.r_osgi.messages.RemoteOSGiMessage;
import ch.ethz.iks.util.SmartObjectInputStream;
import ch.ethz.iks.util.SmartObjectOutputStream;

/**
 * channel factory for (persistent) TCP transport. This is the default protocol.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.6
 */
final class TCPChannelFactory implements NetworkChannelFactory {

    //$NON-NLS-1$
    static final String PROTOCOL = "r-osgi";

    Remoting remoting;

    private TCPAcceptorThread thread;

    protected int listeningPort;

    /**
	 * get a new connection.
	 * 
	 * @param endpoint
	 *            the channel endpoint.
	 * @param endpointURI
	 *            the URI of the remote host.
	 * @return the transport channel.
	 * @throws IOException 
	 */
    public NetworkChannel getConnection(final ChannelEndpoint endpoint, final URI endpointURI) throws IOException {
        return new TCPChannel(endpoint, endpointURI);
    }

    /**
	 * Activate the factory. Is called by R-OSGi when the factory is discovered.
	 * 
	 * @see ch.ethz.iks.r_osgi.channels.NetworkChannelFactory#activate(ch.ethz.iks.r_osgi.Remoting)
	 */
    public void activate(final Remoting r) throws IOException {
        remoting = r;
        thread = new TCPAcceptorThread();
        thread.start();
    }

    /**
	 * Deactivate the factory.
	 * 
	 * @see ch.ethz.iks.r_osgi.channels.NetworkChannelFactory#deactivate(ch.ethz.iks.r_osgi.Remoting)
	 */
    public void deactivate(final Remoting r) throws IOException {
        if (thread != null) {
            thread.interrupt();
        }
        remoting = null;
    }

    /**
	 * get the listening port.
	 * 
	 * @see ch.ethz.iks.r_osgi.channels.NetworkChannelFactory#getListeningPort(java.lang.String)
	 */
    public int getListeningPort(final String protocol) {
        return listeningPort;
    }

    /**
	 * the inner class representing a channel with TCP transport. The TCP
	 * connection uses the TCP keepAlive option to reduce reconnection overhead.
	 * 
	 * @author Jan S. Rellermeyer, ETH Zurich
	 */
    private static final class TCPChannel implements NetworkChannel {

        /**
		 * the socket.
		 */
        Socket socket;

        /**
		 * the remote endpoint address.
		 */
        private final URI remoteEndpointAddress;

        /**
		 * the local endpoint address.
		 */
        private URI localEndpointAddress;

        /**
		 * the input stream.
		 */
        protected ObjectInputStream input;

        /**
		 * the output stream.
		 */
        protected ObjectOutputStream output;

        /**
		 * the channel endpoint.
		 */
        ChannelEndpoint endpoint;

        /**
		 * connected ?
		 */
        boolean connected = true;

        /**
		 * create a new TCPChannel.
		 * 
		 * @param endpoint
		 *            the channel endpoint.
		 * @param endpointAddress
		 *            the remote peer's URI.
		 * @throws IOException
		 *             in case of IO errors.
		 */
         TCPChannel(final ChannelEndpoint endpoint, final URI endpointAddress) throws IOException {
            int port = endpointAddress.getPort();
            if (port == -1) {
                port = 9278;
            }
            this.endpoint = endpoint;
            remoteEndpointAddress = endpointAddress;
            open(new Socket(endpointAddress.getHost(), port));
            new ReceiverThread().start();
        }

        /**
		 * create a new TCPChannel from an existing socket.
		 * 
		 * @param socket
		 *            the socket.
		 * @throws IOException
		 *             in case of IO errors.
		 */
        public  TCPChannel(final Socket socket) throws IOException {
            remoteEndpointAddress = URI.create(//$NON-NLS-1$
            getProtocol() + "://" + //$NON-NLS-1$
            socket.getInetAddress().getHostName() + //$NON-NLS-1$
            ":" + socket.getPort());
            open(socket);
        }

        /**
		 * bind the channel to a channel endpoint.
		 * 
		 * @param e
		 *            the channel endpoint.
		 * 
		 * @see ch.ethz.iks.r_osgi.channels.NetworkChannel#bind(ch.ethz.iks.r_osgi.channels.ChannelEndpoint)
		 */
        public void bind(final ChannelEndpoint e) {
            endpoint = e;
            new ReceiverThread().start();
        }

        /**
		 * open the channel.
		 * 
		 * @param socket
		 *            the socket.
		 * @throws IOException
		 *             if something goes wrong.
		 */
        private void open(final Socket s) throws IOException {
            socket = s;
            localEndpointAddress = URI.create(//$NON-NLS-1$
            getProtocol() + "://" + //$NON-NLS-1$
            socket.getLocalAddress().getHostName() + //$NON-NLS-1$
            ":" + socket.getLocalPort());
            try {
                socket.setKeepAlive(true);
            } catch (final Throwable t) {
            }
            socket.setTcpNoDelay(true);
            output = new SmartObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            output.flush();
            input = new SmartObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        /**
		 * get the String representation of the channel.
		 * 
		 * @return the ID. *
		 * @see java.lang.Object#toString()
		 */
        public String toString() {
            //$NON-NLS-1$ //$NON-NLS-2$
            return "TCPChannel (" + getRemoteAddress() + ")";
        }

        /**
		 * close the channel.
		 * @throws IOException 
		 */
        public void close() throws IOException {
            socket.close();
            // receiver.interrupt();
            connected = false;
        }

        /**
		 * get the protocol that is implemented by the channel.
		 * 
		 * @return the protocol.
		 * @see ch.ethz.iks.r_osgi.channels.NetworkChannel#getProtocol()
		 */
        public String getProtocol() {
            return PROTOCOL;
        }

        /**
		 * get the remote address.
		 * 
		 * @see ch.ethz.iks.r_osgi.channels.NetworkChannel#getRemoteAddress()
		 */
        public URI getRemoteAddress() {
            return remoteEndpointAddress;
        }

        /**
		 * get the local address.
		 * 
		 * @see ch.ethz.iks.r_osgi.channels.NetworkChannel#getLocalAddress()
		 */
        public URI getLocalAddress() {
            return localEndpointAddress;
        }

        /**
		 * send a message through the channel.
		 * 
		 * @param message
		 *            the message.
		 * @throws IOException
		 *             in case of IO errors.
		 */
        public void sendMessage(final RemoteOSGiMessage message) throws IOException {
            if (RemoteOSGiServiceImpl.MSG_DEBUG) {
                RemoteOSGiServiceImpl.log.log(LogService.LOG_DEBUG, //$NON-NLS-1$
                "{TCP Channel} sending " + //$NON-NLS-1$
                message);
            }
            message.send(output);
        }

        /**
		 * the receiver thread continuously tries to receive messages from the
		 * other endpoint.
		 * 
		 * @author Jan S. Rellermeyer, ETH Zurich
		 * @since 0.6
		 */
        class ReceiverThread extends Thread {

             ReceiverThread() {
                setName(//$NON-NLS-1$
                "TCPChannel:ReceiverThread:" + getRemoteAddress());
                setDaemon(true);
            }

            public void run() {
                while (connected) {
                    try {
                        final RemoteOSGiMessage msg = RemoteOSGiMessage.parse(input);
                        if (RemoteOSGiServiceImpl.MSG_DEBUG) {
                            RemoteOSGiServiceImpl.log.log(LogService.LOG_DEBUG, //$NON-NLS-1$
                            "{TCP Channel} received " + msg);
                        }
                        endpoint.receivedMessage(msg);
                    } catch (final IOException ioe) {
                        connected = false;
                        try {
                            socket.close();
                        } catch (final IOException e1) {
                        }
                        endpoint.receivedMessage(null);
                        return;
                    } catch (final Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    /**
	 * TCPThread, handles incoming tcp messages.
	 */
    protected final class TCPAcceptorThread extends Thread {

        private ServerSocket socket;

        /**
		 * creates and starts a new TCPThread.
		 * 
		 * @throws IOException
		 *             if the server socket cannot be opened.
		 */
         TCPAcceptorThread() throws IOException {
            //$NON-NLS-1$
            setName("TCPChannel:TCPAcceptorThread");
            setDaemon(true);
            int e = 0;
            while (true) {
                try {
                    listeningPort = RemoteOSGiServiceImpl.R_OSGI_PORT + e;
                    socket = new ServerSocket(listeningPort);
                    if (e != 0) {
                        System.err.println(//$NON-NLS-1$
                        "WARNING: Port " + RemoteOSGiServiceImpl.R_OSGI_PORT + " already in use. This instance of R-OSGi is running on port " + listeningPort);
                    }
                    RemoteOSGiServiceImpl.R_OSGI_PORT = listeningPort;
                    return;
                } catch (final BindException b) {
                    e++;
                }
            }
        }

        /**
		 * thread loop.
		 * 
		 * @see java.lang.Thread#run()
		 */
        public void run() {
            while (!isInterrupted()) {
                try {
                    // accept incoming connections and build channel endpoints
                    // for them
                    remoting.createEndpoint(new TCPChannel(socket.accept()));
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
