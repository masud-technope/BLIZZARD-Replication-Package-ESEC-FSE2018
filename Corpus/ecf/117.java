/*******************************************************************************
 * Copyright (c) 2006, 2008 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.bittorrent.internal.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import org.eclipse.ecf.protocol.bittorrent.TorrentConfiguration;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;
import org.eclipse.ecf.protocol.bittorrent.internal.encode.Decode;
import org.eclipse.ecf.protocol.bittorrent.internal.encode.Encode;
import org.eclipse.ecf.protocol.bittorrent.internal.torrent.Piece;

/**
 * An extension of the <code>Thread</code> class to manage a connection with a
 * peer.
 */
class PeerConnection extends Thread {

    private static final byte[] CHOKE = { 0x00, 0x00, 0x00, 0x01, 0x00 };

    private static final byte[] UNCHOKE = { 0x00, 0x00, 0x00, 0x01, 0x01 };

    private static final byte[] INTERESTED = { 0x00, 0x00, 0x00, 0x01, 0x02 };

    /**
	 * A byte array that is sent to indicate that the current user is not
	 * interested in any of the pieces that the connected peer possesses.
	 */
    private static final byte[] NOT_INTERESTED = { 0x00, 0x00, 0x00, 0x01, 0x03 };

    /**
	 * This is a specially formed string created by a byte array to represent
	 * the string literal "BitTorrent protocol" led by a value of '19' along
	 * with a string of eight zeros and is used during the handshaking process
	 * with a peer.
	 */
    private static final String PROTOCOL_STRING = new String(new byte[] { 19, 66, 105, 116, 84, 111, 114, 114, 101, 110, 116, 32, 112, 114, 111, 116, 111, 99, 111, 108, 0, 0, 0, 0, 0, 0, 0, 0 });

    /**
	 * The amount of space allocated for the <code>ByteBuffer</code>s. The
	 * value is 1024.
	 */
    private static final int BUFFER_MAXIMUM = 1024;

    /**
	 * The <code>ByteBuffer</code> that is used to read data from the peer.
	 */
    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_MAXIMUM);

    /**
	 * The <code>ByteBuffer</code> that is for sending data to the peer.
	 */
    private final ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_MAXIMUM);

    private final ConnectionPool pool;

    private final TorrentManager manager;

    private final long[] downloads = new long[20];

    private final long[] uploads = new long[20];

    private final byte[] handshake;

    private final byte[] have = { 0x00, 0x00, 0x00, 0x05, 0x04, 0x00, 0x00, 0x00, 0x00 };

    private final byte[] request = { 0x00, 0x00, 0x00, 0x0d, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x00 };

    private final byte[] blockInfo = { 0x00, 0x00, 0x00, 0x09, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    private final boolean[] haveMessages;

    private SocketChannel channel;

    private InetSocketAddress address;

    /**
	 * An array of <code>boolean</code>s that keeps track of what pieces this
	 * peer has.
	 */
    private boolean[] peerPieces;

    /**
	 * The name and version of the BitTorrent client that this peer is currently
	 * using or <code>"Unknown"</code> if it is not known.
	 */
    private String clientName;

    /**
	 * The peer's IP address.
	 */
    private String ip;

    /**
	 * The amount of bytes that has been downloaded from this peer.
	 */
    private long downloaded = 0;

    /**
	 * The amount of bytes that has been uploaded to this peer.
	 */
    private long uploaded = 0;

    private long lastDownloaded = 0;

    private long lastUploaded = 0;

    /**
	 * The port that this peer is listening on.
	 */
    private int port;

    /**
	 * A counter for {@link #downloads} and {@link #uploads} to store the amount
	 * of data that has been downloaded and uploaded per second.
	 */
    private int queuePosition = 0;

    /**
	 * Whether the client is currently choking this peer. This value is
	 * <code>true</code> in the beginning.
	 */
    private boolean isChoking = true;

    /**
	 * Whether this client is interested in a piece that this peer currently
	 * has. This value is <code>false</code> in the beginning.
	 */
    private boolean isInterested = false;

    /**
	 * Whether the peer is currently choking this client. This value is
	 * <code>true</code> in the beginning.
	 */
    private boolean peerIsChoking = true;

    /**
	 * Whether the peer is interested in a piece that this client currently has.
	 * This value is <code>false</code> in the beginning.
	 */
    private boolean peerIsInterested = false;

    /**
	 * Identifies whether this peer is a seed or not.
	 */
    private boolean peerIsSeed = false;

    private boolean initialized = true;

    /**
	 * Indicates whether a choke message should be sent to the peer.
	 */
    private boolean sendChoke = false;

    /**
	 * Indicates whether an unchoke message should be sent to the peer.
	 */
    private boolean sendUnchoke = false;

     PeerConnection(ConnectionPool pool, TorrentManager manager) throws UnsupportedEncodingException {
        this.pool = pool;
        this.manager = manager;
        TorrentFile torrent = manager.getTorrentFile();
        StringBuffer buffer = new StringBuffer(PROTOCOL_STRING);
        synchronized (buffer) {
            buffer.append(torrent.getInfoHash());
            buffer.append(manager.getPeerID());
        }
        //$NON-NLS-1$
        handshake = buffer.toString().getBytes("ISO-8859-1");
        peerPieces = new boolean[torrent.getNumPieces()];
        haveMessages = new boolean[peerPieces.length];
        Arrays.fill(peerPieces, false);
        Arrays.fill(haveMessages, false);
    }

    void setAddress(String ip, int port) {
        address = new InetSocketAddress(ip, port);
        this.ip = ip;
        this.port = port;
    }

    void setChannel(SocketChannel channel) {
        this.channel = channel;
        Socket socket = channel.socket();
        this.ip = socket.getLocalAddress().getHostAddress();
        this.port = socket.getLocalPort();
    }

    /**
	 * Connect to the peer and exchange handshakes as necessary. A new
	 * connection may be created depending on whether an incoming or outgoing
	 * connection is being established.
	 */
    private void connect() {
        try {
            if (channel == null) {
                call();
            } else {
                answer();
            }
        } catch (IOException e) {
            String message = e.getMessage();
            TorrentConfiguration.debug("The connection with " + ip + ":" + port + " has been closed" + (message == null ? "." : ": " + message));
        } catch (RuntimeException e) {
            close();
            throw e;
        }
        close();
    }

    private void call() throws IOException {
        try {
            channel = SocketChannel.open(address);
            address = null;
        } catch (SocketException e) {
            TorrentConfiguration.debug("Unable to connect to " + ip + ":" + port + " - " + e.getMessage());
            return;
        }
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Established outgoing connection with " + ip + ":" + //$NON-NLS-1$
        port);
        sendHandshake();
        int read = 0;
        int ret = 0;
        // bytes has been retrieved
        while (read < 68) {
            if (channel == null) {
                return;
            }
            ret = channel.read(buffer);
            if (ret == -1) {
                //$NON-NLS-1$ //$NON-NLS-2$
                TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                return;
            }
            read += ret;
        }
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Received [BT_HANDSHAKE] message from " + ip + ":" + //$NON-NLS-1$
        port);
        byte[] client = new byte[20];
        System.arraycopy(buffer.array(), 48, client, 0, 20);
        processClientName(new String(client));
        // check to see if data besides the return handshake was sent
        if (read != 68) {
            // keep reading until four additional bytes are read
            while (read < 72) {
                if (channel == null) {
                    return;
                }
                ret = channel.read(buffer);
                if (ret == -1) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                    return;
                }
                read += ret;
            }
            if (buffer.get(71) != 0) {
                while (read < 73) {
                    if (channel == null) {
                        return;
                    }
                    ret = channel.read(buffer);
                    if (ret == -1) {
                        //$NON-NLS-1$ //$NON-NLS-2$
                        TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                        return;
                    }
                    read += ret;
                }
                // check to see if part of what was read is a bitfield
                if (buffer.get(72) == 5) {
                    int length = 68 + 4 + buffer.get(71);
                    // keep reading until the entire bitfield has been read in
                    while (read < length) {
                        if (channel == null) {
                            return;
                        }
                        ret = channel.read(buffer);
                        if (ret == -1) {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                            return;
                        }
                        read += ret;
                    }
                    buffer.flip();
                    byte[] array = new byte[read];
                    buffer.get(array, 0, read);
                    processBitfield(array, 73, 72 + array[71]);
                    if (read == length) {
                        sendBitfield();
                    }
                } else if (buffer.get(71) == 1) {
                    byte[] bytes = null;
                    buffer.get(bytes, 0, read);
                    byte[] array = new byte[5];
                    System.arraycopy(bytes, 68, array, 0, 5);
                    if (!processMessage(array)) {
                        return;
                    }
                }
            } else if (buffer.get(78) == 0 && buffer.get(79) == 0 && buffer.get(80) == 0) {
                TorrentConfiguration.debug("Received [BT_KEEPALIVE] message from " + ip + //$NON-NLS-1$
                ":" + port);
            } else {
                TorrentConfiguration.debug("Received an unidentifiable message from " + ip + //$NON-NLS-1$
                ":" + port);
                return;
            }
        } else {
            sendBitfield();
        }
        buffer.clear();
        // enter the main loop
        query();
    }

    private void answer() throws IOException {
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Established incoming connection from " + ip + ":" + //$NON-NLS-1$
        port);
        sendHandshake();
        sendBitfield();
        query();
    }

    private void query() throws IOException {
        TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
        "Entering the main loop with " + ip + ":" + port);
        byte[] array = null;
        int read = 0;
        int ret = 0;
        while (channel != null && channel.isConnected()) {
            buffer.clear();
            sendQueuedMessages();
            if (read != 0) {
                byte[] bufferArray = buffer.array();
                byte[] bytes = new byte[bufferArray.length];
                System.arraycopy(bufferArray, read, bytes, 0, bufferArray.length - read);
                buffer.put(bytes);
                buffer.position(read);
            }
            int request = sendRequest(manager.request(peerPieces));
            int length = request == -1 ? 4 : request;
            resetBuffer();
            while (read < 4) {
                if (channel == null) {
                    return;
                }
                ret = channel.read(buffer);
                if (ret == -1) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                    return;
                }
                manager.updateDownloadRequestSpeed(ret);
                limitBuffer(manager.getDownloadRequestSpeed());
                read += ret;
            }
            buffer.limit(BUFFER_MAXIMUM);
            buffer.rewind();
            if (array == null) {
                array = new byte[read];
                buffer.get(array, 0, read);
            } else {
                byte[] bytes = new byte[array.length + read];
                System.arraycopy(array, 0, bytes, 0, array.length);
                buffer.get(bytes, array.length, read);
                array = bytes;
            }
            if (array[0] == 0 && array[1] == 0 && array[2] == 0 && array[3] == 0) {
                TorrentConfiguration.debug(//$NON-NLS-1$
                "Received [BT_KEEPALIVE] from " + //$NON-NLS-1$
                ip + //$NON-NLS-1$
                ":" + port);
                array = truncate(array, 4);
                read -= 4;
            }
            while (array != null && array.length > 4) {
                if (0 <= array[4] && array[4] <= 3) {
                    if (!processMessage(array)) {
                        return;
                    }
                    array = truncate(array, 5);
                    read -= 5;
                } else if (array[3] == 5 && array[4] == 4) {
                    if (array.length < 9) {
                        buffer.clear();
                        while (read < 9) {
                            if (channel == null) {
                                return;
                            }
                            ret = channel.read(buffer);
                            if (ret == -1) {
                                TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
                                "End of stream has been reached with " + ip + //$NON-NLS-1$ //$NON-NLS-2$
                                ":" + port);
                                return;
                            }
                            read += ret;
                        }
                        byte[] bytes = new byte[read];
                        System.arraycopy(array, 0, bytes, 0, array.length);
                        buffer.flip();
                        buffer.get(bytes, array.length, read - array.length);
                        array = bytes;
                    }
                    processHaveMessage(array);
                    array = truncate(array, 9);
                    read -= 9;
                } else if (array[4] == 5) {
                    length = 4 + array[3];
                    while (read < length) {
                        if (channel == null) {
                            return;
                        }
                        ret = channel.read(buffer);
                        if (ret == -1) {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                            return;
                        }
                        read += ret;
                    }
                    if (array.length < read) {
                        byte[] bytes = new byte[read];
                        System.arraycopy(array, 0, bytes, 0, array.length);
                        buffer.flip();
                        buffer.get(bytes, array.length, read - array.length);
                        array = bytes;
                    }
                    processBitfield(array, 5, length);
                    array = truncate(array, length);
                    read -= length;
                } else if (array[3] == 13 && array[4] == 6) {
                    if (array.length < 17) {
                        buffer.clear();
                        while (read < 17) {
                            if (channel == null) {
                                return;
                            }
                            ret = channel.read(buffer);
                            if (ret == -1) {
                                TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
                                "End of stream has been reached with " + ip + //$NON-NLS-1$ //$NON-NLS-2$
                                ":" + port);
                                return;
                            }
                            read += ret;
                        }
                        byte[] bytes = new byte[read];
                        System.arraycopy(array, 0, bytes, 0, array.length);
                        buffer.flip();
                        buffer.get(bytes, array.length, read - array.length);
                        array = bytes;
                    }
                    if (!processRequest(array)) {
                        // an improper request was sent
                        close();
                        return;
                    }
                    array = truncate(array, 17);
                    read -= 17;
                } else if (array[4] == 7) {
                    byte[] bufferArray = buffer.array();
                    length = Decode.decodeFourByteNumber(array, 0) + 4;
                    int offset = array.length;
                    if (offset < length) {
                        byte[] bytes = new byte[length];
                        System.arraycopy(array, 0, bytes, 0, offset);
                        array = bytes;
                    }
                    resetBuffer();
                    buffer.clear();
                    int start = read;
                    while (read < length) {
                        if (channel == null) {
                            return;
                        }
                        ret = channel.read(buffer);
                        if (ret == -1) {
                            //$NON-NLS-1$ //$NON-NLS-2$
                            TorrentConfiguration.debug("End of stream has been reached with " + ip + ":" + port);
                            return;
                        }
                        // expand the array's length if necessary
                        if (offset + ret > array.length) {
                            byte[] bytes = new byte[offset + ret];
                            System.arraycopy(array, 0, bytes, 0, array.length);
                            array = bytes;
                        }
                        System.arraycopy(bufferArray, 0, array, offset, ret);
                        manager.updateDownloadRequestSpeed(ret);
                        buffer.rewind();
                        limitBuffer(manager.getDownloadRequestSpeed());
                        offset += ret;
                        read += ret;
                    }
                    buffer.limit(BUFFER_MAXIMUM);
                    if (array.length < read) {
                        byte[] bytes = new byte[read];
                        System.arraycopy(array, 0, bytes, 0, array.length);
                        buffer.position(start);
                        buffer.get(bytes, array.length, read - array.length);
                        array = bytes;
                    }
                    processPiece(array);
                    array = truncate(array, length);
                    read -= length;
                } else if (array[4] == 8) {
                    // TODO: implement the processing of BT_CANCEL messages
                    array = truncate(array, 17);
                    read -= 17;
                } else if (array[4] == 9) {
                    // TODO: implement the processing of BT_PORT messages
                    array = truncate(array, 9);
                    read -= 9;
                } else {
                    TorrentConfiguration.debug(//$NON-NLS-1$
                    "An ID of " + //$NON-NLS-1$
                    array[4] + " has been encountered. Closing connection with " + //$NON-NLS-1$
                    ip + //$NON-NLS-1$
                    ":" + port);
                    return;
                }
            }
        }
    }

    private byte[] truncate(byte[] array, int length) {
        if (array.length == length) {
            return null;
        } else {
            byte[] bytes = new byte[array.length - length];
            System.arraycopy(array, length, bytes, 0, bytes.length);
            return bytes;
        }
    }

    private void resetBuffer() {
        long maximum = manager.getDownloadRequestSpeed();
        if (maximum == -1) {
            return;
        }
        buffer.limit(0);
        limitBuffer(maximum);
    }

    private void limitBuffer(long maximum) {
        if (maximum != 0) {
            int currentLimit = buffer.limit();
            if (maximum + currentLimit > BUFFER_MAXIMUM) {
                buffer.limit(BUFFER_MAXIMUM);
            } else {
                buffer.limit(currentLimit + (int) maximum);
            }
        } else {
            try {
                buffer.limit(0);
                // sleep for a short while here since if it's zero, chances are
                // that the next rotation will return a zero also, causing an
                // unnecessarily long loop
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private void processClientName(String peerID) {
        switch(peerID.charAt(0)) {
            case '-':
                String client = peerID.substring(1, 3);
                String version = //$NON-NLS-1$ //$NON-NLS-2$
                peerID.charAt(3) + "." + peerID.charAt(4) + "." + peerID.charAt(5) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(6);
                if (//$NON-NLS-1$
                client.equals("AR")) {
                    clientName = //$NON-NLS-1$
                    "Arctic " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("AX")) {
                    clientName = //$NON-NLS-1$
                    "BitPump " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("AZ")) {
                    clientName = //$NON-NLS-1$
                    "Azureus " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("BB")) {
                    clientName = //$NON-NLS-1$
                    "BitBuddy " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("BC")) {
                    clientName = //$NON-NLS-1$
                    "BitComet " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("BS")) {
                    clientName = //$NON-NLS-1$
                    "BTSlave " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("BX")) {
                    clientName = //$NON-NLS-1$
                    "Bittorrent X " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("CD")) {
                    clientName = //$NON-NLS-1$
                    "Enhanced CTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("CT")) {
                    clientName = //$NON-NLS-1$
                    "CTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("LP")) {
                    clientName = //$NON-NLS-1$
                    "Lphant " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("LT")) {
                    clientName = //$NON-NLS-1$
                    "libtorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("lt")) {
                    clientName = //$NON-NLS-1$
                    "libTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("MP")) {
                    clientName = //$NON-NLS-1$
                    "MooPolice " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("MT")) {
                    clientName = //$NON-NLS-1$
                    "MoonlightTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("QT")) {
                    clientName = //$NON-NLS-1$
                    "QT 4 Torrent example " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("RT")) {
                    clientName = //$NON-NLS-1$
                    "Retriever " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("SB")) {
                    clientName = //$NON-NLS-1$
                    "Swiftbit " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("SS")) {
                    clientName = //$NON-NLS-1$
                    "SwarmScope " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("SZ")) {
                    clientName = //$NON-NLS-1$
                    "Shareaza " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("TN")) {
                    clientName = //$NON-NLS-1$
                    "TorrentDotNet " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("TR")) {
                    clientName = //$NON-NLS-1$
                    "Transmission " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("TS")) {
                    clientName = //$NON-NLS-1$
                    "TorrentStorm " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("UT")) {
                    clientName = //$NON-NLS-1$
                    "µTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("XT")) {
                    clientName = //$NON-NLS-1$
                    "XanTorrent " + //$NON-NLS-1$
                    version;
                } else if (//$NON-NLS-1$
                client.equals("ZT")) {
                    clientName = //$NON-NLS-1$
                    "ZipTorrent " + //$NON-NLS-1$
                    version;
                } else {
                    //$NON-NLS-1$
                    clientName = //$NON-NLS-1$
                    "Unknown";
                }
                break;
            case 'A':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "ABC " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            case 'M':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "Mainline " + peerID.charAt(1) + "." + peerID.charAt(3) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(5);
                break;
            case 'O':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "Osprey Permaseed " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            case 'R':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "Tribler " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            case 'S':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "Shadow's client " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            case 'T':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "BitTornado " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            case 'U':
                clientName = //$NON-NLS-1$ //$NON-NLS-2$
                "UPnP NAT Bit Torrent " + peerID.charAt(1) + "." + peerID.charAt(2) + //$NON-NLS-1$
                "." + //$NON-NLS-1$
                peerID.charAt(3);
                break;
            default:
                //$NON-NLS-1$
                clientName = "Unknown";
                break;
        }
    }

    private boolean processMessage(byte[] array) throws IOException {
        switch(array[4]) {
            case 0:
                TorrentConfiguration.debug(//$NON-NLS-1$
                "Received [BT_CHOKE] message from " + ip + //$NON-NLS-1$
                ":" + //$NON-NLS-1$
                port);
                peerIsChoking = true;
                break;
            case 1:
                TorrentConfiguration.debug(//$NON-NLS-1$
                "Received [BT_UNCHOKE] message from " + ip + //$NON-NLS-1$
                ":" + //$NON-NLS-1$
                port);
                peerIsChoking = false;
                break;
            case 2:
                TorrentConfiguration.debug(//$NON-NLS-1$
                "Received [BT_INTERESTED] message from " + ip + //$NON-NLS-1$
                ":" + //$NON-NLS-1$
                port);
                if (peerIsInterested) {
                    break;
                }
                peerIsInterested = true;
                if (pool.checkUnchoke()) {
                    sendUnchoke();
                }
                break;
            case 3:
                TorrentConfiguration.debug("Received [BT_NOT_INTERESTED] message from " + ip + //$NON-NLS-1$
                ":" + port);
                if (!peerIsInterested) {
                    break;
                }
                peerIsInterested = false;
                if (!isChoking) {
                    pool.unchokedPeerCleared();
                }
                sendChoke();
                break;
            default:
                return false;
        }
        return true;
    }

    private void processBitfield(byte[] array, int offset, int end) {
        boolean[] hasPiece = new boolean[(end - offset) * 8];
        int count = 0;
        // this peer has
        for (int i = offset; i < end; i++) {
            int bit = Decode.decodeSignedByte(array[i]);
            hasPiece[count++] = (bit & 1) != 0;
            hasPiece[count++] = (bit & 2) != 0;
            hasPiece[count++] = (bit & 4) != 0;
            hasPiece[count++] = (bit & 8) != 0;
            hasPiece[count++] = (bit & 16) != 0;
            hasPiece[count++] = (bit & 32) != 0;
            hasPiece[count++] = (bit & 64) != 0;
            hasPiece[count++] = (bit & 128) != 0;
        }
        System.arraycopy(hasPiece, 0, peerPieces, 0, peerPieces.length);
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Received [BT_BITFIELD] message from " + ip + ":" + //$NON-NLS-1$
        port);
        manager.addPieceAvailability(peerPieces);
        for (int i = 0; i < peerPieces.length; i++) {
            if (!peerPieces[i]) {
                return;
            }
        }
        peerIsSeed = true;
    }

    private void processHaveMessage(byte[] array) {
        int piece = Decode.decodeFourByteNumber(array, 5);
        if (!peerPieces[piece]) {
            peerPieces[piece] = true;
            manager.updatePieceAvailability(piece);
        }
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Received [BT_HAVE piece #" + piece + "] message from " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
        port);
        for (int i = 0; i < peerPieces.length; i++) {
            if (!peerPieces[i]) {
                return;
            }
        }
        peerIsSeed = true;
    }

    private void processPiece(byte[] array) throws IOException {
        int piece = Decode.decodeFourByteNumber(array, 5);
        int index = Decode.decodeFourByteNumber(array, 9);
        int length = Decode.decodeFourByteNumber(array, 0) - 9;
        manager.write(piece, index, array, 13, length);
        downloaded += length;
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Received [BT_PIECE data for #" + piece + ": " + index + "->" + //$NON-NLS-1$ //$NON-NLS-2$
        (length + index - 1) + "] message from " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
        port);
    }

    private boolean processRequest(byte[] array) throws IOException {
        int piece = Decode.decodeFourByteNumber(array, 5);
        int index = Decode.decodeFourByteNumber(array, 9);
        int length = Decode.decodeFourByteNumber(array, 13);
        if (isChoking) {
            TorrentConfiguration.debug(//$NON-NLS-1$
            "Ignoring [BT_REQUEST piece #" + piece + ": " + index + "->" + //$NON-NLS-1$ //$NON-NLS-2$
            (index + length - 1) + "] message from " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
            port + " as this peer is currently choked");
            return true;
        }
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Received [BT_REQUEST piece #" + piece + ": " + index + "->" + //$NON-NLS-1$ //$NON-NLS-2$
        (index + length - 1) + "] message from " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
        port);
        if (length > 131072) {
            TorrentConfiguration.debug(//$NON-NLS-1$
            "The requesting of " + length + " bytes violates the standard maximum amount of 131072, the connection to " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
            port + " will be closed.");
            return false;
        }
        byte[] block = manager.getPieceData(piece, index, length);
        if (block == null) {
            return false;
        }
        Encode.putIntegerAsFourBytes(blockInfo, length + 9, 0);
        Encode.putIntegerAsFourBytes(blockInfo, piece, 5);
        Encode.putIntegerAsFourBytes(blockInfo, index, 9);
        sendBuffer.put(blockInfo);
        int blockLength = block.length;
        int offset = 0;
        int remaining = sendBuffer.remaining();
        long write = manager.getUploadRequestSpeed();
        if (write != 0) {
            if (write == -1) {
                // since no limit has been set, simply set the amount to write
                // as the length of the block
                write = blockLength;
            } else {
                write = write < blockLength ? write : blockLength;
            }
            write = remaining < write ? remaining : write;
        }
        while (offset < blockLength) {
            if (write == 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            } else {
                sendBuffer.put(block, offset, (int) write);
                sendBuffer.flip();
                channel.write(sendBuffer);
                sendBuffer.clear();
                offset += write;
                manager.updateUploadRequestSpeed((int) write);
                uploaded += write;
                manager.addToUploaded(write);
            }
            write = manager.getUploadRequestSpeed();
            if (write != 0) {
                remaining = blockLength - offset;
                if (write == -1) {
                    write = remaining;
                } else {
                    write = write < remaining ? write : remaining;
                }
                remaining = sendBuffer.remaining();
                write = remaining < write ? remaining : write;
            }
        }
        TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
        "Sent [BT_PIECE data for #" + piece + ": " + index + "->" + (length + index - 1) + "] message to " + //$NON-NLS-1$ //$NON-NLS-2$
        ip + ":" + //$NON-NLS-1$
        port);
        return true;
    }

    private void sendHandshake() throws IOException {
        sendBuffer.put(handshake);
        sendBuffer.flip();
        channel.write(sendBuffer);
        sendBuffer.clear();
        TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
        "Sent [BT_HANDSHAKE] message to " + ip + ":" + port);
    }

    private void sendBitfield() throws IOException {
        byte[] bitfield = manager.getBitfield();
        boolean hasPiece = false;
        for (int i = 0; i < bitfield.length; i++) {
            if (bitfield[i] != 0) {
                hasPiece = true;
                break;
            }
        }
        // to the peer
        if (!hasPiece) {
            return;
        }
        byte[] header = { 0x00, 0x00, 0x00, 0x00, 0x05 };
        Encode.putIntegerAsFourBytes(header, bitfield.length + 1, 0);
        sendBuffer.put(header);
        sendBuffer.put(manager.getBitfield());
        sendBuffer.flip();
        channel.write(sendBuffer);
        sendBuffer.clear();
        TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
        "Sent [BT_BITFIELD] message to " + ip + ":" + port);
    }

    private int sendRequest(Piece piece) throws IOException {
        if (piece == null) {
            sendNotInterested();
            return -1;
        } else if (peerIsChoking) {
            sendInterested();
            return -1;
        }
        int[] information = piece.getRequestInformation();
        while (information == null) {
            piece = manager.request(peerPieces);
            if (piece == null) {
                sendNotInterested();
                return -1;
            }
            information = piece.getRequestInformation();
        }
        Encode.placeRequestInformation(request, information);
        sendBuffer.put(request);
        sendBuffer.flip();
        channel.write(sendBuffer);
        sendBuffer.clear();
        TorrentConfiguration.debug(//$NON-NLS-1$
        "Sent [BT_REQUEST piece #" + information[0] + ": " + information[1] + //$NON-NLS-1$ //$NON-NLS-2$
        "->" + (information[1] + information[2] - 1) + //$NON-NLS-1$
        "] message to " + //$NON-NLS-1$
        ip + ":" + //$NON-NLS-1$
        port);
        return information[2] + 13;
    }

    /**
	 * Sends the queued up messages to the connected peer to inform.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while sending the messages to the peer
	 */
    private synchronized void sendQueuedMessages() throws IOException {
        for (int i = 0; i < haveMessages.length; i++) {
            if (haveMessages[i]) {
                Encode.putIntegerAsFourBytes(have, i, 5);
                sendBuffer.put(have);
                sendBuffer.flip();
                channel.write(sendBuffer);
                sendBuffer.clear();
                TorrentConfiguration.debug(//$NON-NLS-1$
                "Sent [BT_HAVE PIECE #" + //$NON-NLS-1$
                i + "] message to " + ip + ":" + //$NON-NLS-1$ //$NON-NLS-2$
                port);
                haveMessages[i] = false;
            }
        }
        if (sendChoke) {
            sendChoke();
            sendChoke = false;
        } else if (sendUnchoke) {
            sendUnchoke();
            sendUnchoke = false;
        }
    }

    /**
	 * Sends a message to the peer that this client is interested in something
	 * that the peer has to offer.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while sending the message
	 */
    private void sendInterested() throws IOException {
        if (!isInterested) {
            sendBuffer.put(INTERESTED);
            sendBuffer.flip();
            channel.write(sendBuffer);
            sendBuffer.clear();
            isInterested = true;
            TorrentConfiguration.debug(//$NON-NLS-1$
            "Sent [BT_INTERESTED] message to " + ip + //$NON-NLS-1$
            ":" + //$NON-NLS-1$
            port);
        }
    }

    /**
	 * Sends a message to the peer that this client is not interested in
	 * anything that the peer currently has to offer.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while sending the message
	 */
    private void sendNotInterested() throws IOException {
        if (isInterested) {
            sendBuffer.put(NOT_INTERESTED);
            sendBuffer.flip();
            channel.write(sendBuffer);
            sendBuffer.clear();
            isInterested = false;
            TorrentConfiguration.debug(//$NON-NLS-1$
            "Sent [BT_NOT_INTERESTED] message to " + ip + //$NON-NLS-1$
            ":" + //$NON-NLS-1$
            port);
        }
    }

    /**
	 * Sends a choke message to the peer which indicates to them that any piece
	 * requests will be ignored and discarded.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while writing the message to the peer
	 */
    private void sendChoke() throws IOException {
        if (!isChoking) {
            sendBuffer.put(CHOKE);
            sendBuffer.flip();
            channel.write(sendBuffer);
            sendBuffer.clear();
            isChoking = true;
            TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
            "Sent [BT_CHOKE] message to " + ip + ":" + port);
        }
    }

    /**
	 * Sends an unchoke message to the peer to inform them that piece requests
	 * will now be honoured.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs whilst writing the message to the peer
	 */
    private void sendUnchoke() throws IOException {
        if (isChoking) {
            sendBuffer.put(UNCHOKE);
            sendBuffer.flip();
            channel.write(sendBuffer);
            sendBuffer.clear();
            isChoking = false;
            TorrentConfiguration.debug(//$NON-NLS-1$
            "Sent [BT_UNCHOKE] message to " + ip + //$NON-NLS-1$
            ":" + //$NON-NLS-1$
            port);
        }
    }

    /**
	 * Resets instance values so that a new connection that is bridged by this
	 * thread will not have remnants of information from the last connection.
	 */
    private void reset() {
        queuePosition = 0;
        downloaded = 0;
        uploaded = 0;
        Arrays.fill(downloads, 0);
        Arrays.fill(uploads, 0);
        isInterested = false;
        isChoking = true;
        peerIsInterested = false;
        peerIsChoking = true;
        peerIsSeed = false;
        Arrays.fill(peerPieces, false);
        sendChoke = false;
        sendUnchoke = false;
        Arrays.fill(haveMessages, false);
        //$NON-NLS-1$
        clientName = "Unknown";
    }

    private void cleanup() {
        pool.connectionClosed();
        if (!isChoking) {
            pool.unchokedPeerCleared();
        }
        initialized = false;
    }

    /**
	 * Closes this connection. Any <code>IOException</code>s that may be
	 * thrown will closing the connection with the peer will be ignored.
	 */
    void close() {
        if (channel != null) {
            reset();
            try {
                channel.close();
            } catch (IOException e) {
            }
            channel = null;
            manager.removePieceAvailability(peerPieces);
        }
    }

    boolean isChoking() {
        return isChoking;
    }

    boolean isConnectedTo(String ip, int port) {
        return port == this.port && ip.equals(this.ip);
    }

    /**
	 * Returns whether the connected peer is a seed or not. This is used to
	 * identify whether this connection should be cut after a download has
	 * completed since there is no need for a seed to be connected to another
	 * seed.
	 * 
	 * @return <code>true</code> if the connected peer is a seed,
	 *         <code>false</code> otherwise
	 */
    boolean isSeed() {
        return peerIsSeed;
    }

    /**
	 * Queues up the specified piece as needing a corresponding HAVE message to
	 * be sent to the connected peer.
	 * 
	 * @param number
	 *            the number of the piece that has just been completed
	 * @throws IllegalArgumentException
	 *             If a negative piece number or a piece number that is over the
	 *             number of available pieces has been set
	 */
    void queueHaveMessage(int number) throws IllegalArgumentException {
        if (number < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The piece number cannot be negative");
        } else if (number >= peerPieces.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The piece number is greater than the number of pieces");
        }
        haveMessages[number] = true;
    }

    void queueChokeMessage() {
        sendUnchoke = true;
    }

    void queueUnchokeMessage() {
        sendChoke = true;
    }

    long getDownloaded() {
        return downloaded;
    }

    long getUploaded() {
        return uploaded;
    }

    double getDownSpeed() {
        double totalDown = 0;
        for (int j = 0; j < 20; j++) {
            totalDown += downloads[j];
        }
        return totalDown / 20;
    }

    double getUpSpeed() {
        double totalUp = 0;
        for (int j = 0; j < 20; j++) {
            totalUp += uploads[j];
        }
        return totalUp / 20;
    }

    void queueSpeeds() {
        if (queuePosition == 20) {
            queuePosition = 0;
        }
        downloads[queuePosition] = downloaded - lastDownloaded;
        uploads[queuePosition] = uploaded - lastUploaded;
        lastDownloaded = downloaded;
        lastUploaded = uploaded;
        queuePosition++;
    }

    String getClientName() {
        return clientName;
    }

    boolean isInitialized() {
        return initialized;
    }

    protected void finalize() {
        close();
    }

    public void run() {
        ConnectionInfo info = pool.dequeue();
        if (info == null) {
            pool.connectionDestroyed(this);
            return;
        }
        while (true) {
            try {
                initialized = true;
                if (info.isChannel()) {
                    setChannel(info.getChannel());
                } else {
                    setAddress(info.getIP(), info.getPort());
                }
                pool.connectionCreated();
                connect();
                cleanup();
                if (!pool.isConnected()) {
                    return;
                }
                try {
                    synchronized (pool) {
                        pool.wait();
                    }
                } catch (InterruptedException e) {
                    pool.connectionDestroyed(this);
                    return;
                }
                info = pool.dequeue();
                if (info == null) {
                    pool.connectionDestroyed(this);
                    return;
                }
            } catch (RuntimeException e) {
                cleanup();
                pool.connectionDestroyed(this);
                throw e;
            }
        }
    }
}
