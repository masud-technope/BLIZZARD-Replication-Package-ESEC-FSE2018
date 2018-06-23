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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.eclipse.ecf.protocol.bittorrent.IHashCheckListener;
import org.eclipse.ecf.protocol.bittorrent.IPieceProgressListener;
import org.eclipse.ecf.protocol.bittorrent.ITorrentErrorListener;
import org.eclipse.ecf.protocol.bittorrent.ITorrentProgressListener;
import org.eclipse.ecf.protocol.bittorrent.ITorrentStateListener;
import org.eclipse.ecf.protocol.bittorrent.Torrent;
import org.eclipse.ecf.protocol.bittorrent.TorrentConfiguration;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;
import org.eclipse.ecf.protocol.bittorrent.TorrentServer;
import org.eclipse.ecf.protocol.bittorrent.internal.encode.BEncodedDictionary;
import org.eclipse.ecf.protocol.bittorrent.internal.encode.Decode;
import org.eclipse.ecf.protocol.bittorrent.internal.encode.Encode;
import org.eclipse.ecf.protocol.bittorrent.internal.torrent.DataFile;
import org.eclipse.ecf.protocol.bittorrent.internal.torrent.Piece;
import org.eclipse.ecf.protocol.bittorrent.internal.torrent.PieceState;

/**
 * The <code>TorrentManager</code> class is used to handle all of the
 * internals of what's being publicly exposed by {@link Torrent}.
 */
public class TorrentManager {

    //$NON-NLS-1$
    private static final String DOWN_SPEED_KEY = "down.speed";

    //$NON-NLS-1$
    private static final String UP_SPEED_KEY = "up.speed";

    private static MessageDigest shaDigest;

    private static File statePath;

    private final ConnectionPool connectionPool;

    /**
	 * An array of files that will be read and written to to exchange pieces
	 * with peers.
	 */
    private final DataFile[] files;

    private final Vector stateListeners;

    private final Vector progressListeners;

    private final Vector errorListeners;

    private final Vector pieceListeners;

    private final Vector hashCheckListeners;

    private final TorrentFile torrent;

    private final Properties properties;

    private final File propertiesFile;

    private final File targetFile;

    /**
	 * The file in which the amount of data that has been downloaded thus far
	 * for this torrent is stored. This allows the torrent to resume downloading
	 * or seeding very quickly.
	 */
    private final File torrentState;

    private final Vector pieces;

    /**
	 * A collection of {@link Piece}s that has had a portion of its bytes
	 * completed.
	 */
    private final Vector incompletePieces;

    /**
	 * An integer array that stores the number of peers that possesses a
	 * specific piece. This is used to determine which piece is a rare piece.
	 */
    private final int[] pieceAvailability;

    private final byte[] bitfield;

    /**
	 * A boolean array that indicates whether a specific piece has been
	 * downloaded successfully or not.
	 */
    private final boolean[] hasPiece;

    private final boolean[] priorityPieces;

    private final boolean[] interestedPieces;

    private final boolean[] uninterestedPieces;

    private final String infoHash;

    /**
	 * The URL of the tracker that this host should connect to.
	 */
    private final String tracker;

    /**
	 * A unique identification string is used to identify this client when
	 * talking with the tracker.
	 */
    //$NON-NLS-1$
    private final String peerID = "E088----" + createPeerID();

    private final String hexHash;

    /**
	 * An additional identification that is required by some trackers in the
	 * event that the client's IP changes.
	 */
    private final char key = createKey();

    /**
	 * The total number of bytes that is encompassed by the torrent file. This
	 * only takes files into accounts and ignores the file size of folders.
	 */
    private final long total;

    /**
	 * The length of a piece.
	 */
    private final int pieceLength;

    private TrackerThread trackerThread;

    private SpeedMonitoringThread speedMonitoringThread;

    private HashCheckThread hashCheckThread;

    private PieceState[] states;

    /**
	 * A unique string that may be returned by the connected tracker for
	 * identification purposes.
	 */
    private String trackerID;

    /**
	 * The amount of bytes that has been downloaded from other peers thus far.
	 */
    private long downloaded = 0;

    /**
	 * The amount of bytes that has been uploaded to other peers thus far.
	 */
    private long uploaded = 0;

    /**
	 * The speed in which the data is being downloaded from peers in bytes as
	 * calculated from a twenty second rolling average.
	 */
    private long downSpeed = 0;

    /**
	 * The speed in which the data is being uploaded to peers in bytes as
	 * calculated from a twenty second rolling average.
	 */
    private long upSpeed = 0;

    private long maxDownSpeed = -1;

    private long maxUpSpeed = -1;

    private long requestDownSpeed = -1;

    private long requestUpSpeed = -1;

    /**
	 * The amount of data that has been discarded thus far because of hash check
	 * failures.
	 */
    private long discarded = 0;

    /**
	 * The amount of data that still needs to be downloaded for completion.
	 */
    private long remaining;

    /**
	 * The number of peers to request from the tracker.
	 */
    private int request = 50;

    /**
	 * The total number of seeds on the torrent as returned by the tracker. If
	 * the value is -1, the tracker has either not returned this value or the
	 * tracker has not yet been queried.
	 */
    private int seeders = -1;

    /**
	 * The total number of peers on the torrent as returned by the tracker. If
	 * the value is -1, the tracker has either not returned this value or the
	 * tracker has not yet been queried.
	 */
    private int peers = -1;

    /**
	 * The amount of time to wait before querying the tracker again for peers.
	 * This is in milliseconds.
	 */
    private int timeout = 1800000;

    /**
	 * The number of completed pieces thus far. This is updated within
	 * {@link #write(int, int, byte[], int, int)} and is used to inform attached
	 * {@link ITorrentProgressListener}s via the
	 * {@link #firePieceCompletedEvent(int)} method.
	 */
    private int completedPieces = 0;

    private int state = ITorrentStateListener.STOPPED;

    /**
	 * Used to indicate whether the torrent is currently running.
	 */
    private boolean running = false;

    /**
	 * Indicates that all the pieces have passed the SHA-1 hash check
	 * successfully.
	 */
    private boolean isCompleted = false;

    /**
	 * Indicates whether the user is deciding to not download some files.
	 */
    private boolean isSelective = false;

    private boolean isPrioritizing = false;

    private boolean isWaitingToStart = false;

    private boolean isHashChecking = false;

    static {
        try {
            //$NON-NLS-1$
            shaDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setStatePath(File path) {
        statePath = path;
    }

    private static String createPeerID() {
        char[] numbers = new char[12];
        for (int i = 0; i < 12; i++) {
            numbers[i] = (char) (48 + ConnectionPool.RANDOM.nextInt(10));
        }
        return new String(numbers);
    }

    private static char createKey() {
        char key = (char) (ConnectionPool.RANDOM.nextInt(75) + 48);
        while (!Character.isDigit(key) && !Character.isLetter(key)) {
            key = (char) (ConnectionPool.RANDOM.nextInt(75) + 48);
        }
        return key;
    }

    /**
	 * Creates a new <code>Host</code> to begin with exchanging pieces with
	 * other pieces for the given torrent.
	 * 
	 * @param torrent
	 *            the torrent to use
	 * @param properties
	 *            the <code>Properties</code> instance to use to store
	 *            information, if <code>null</code>, a new instance will be
	 *            created
	 * @throws IOException
	 *             If an I/O error occurs whilst creating or hooking up with the
	 *             files associated with the torrent
	 */
    public  TorrentManager(TorrentFile torrent, Properties properties) throws IOException {
        this.torrent = torrent;
        targetFile = torrent.getTargetFile();
        connectionPool = new ConnectionPool(this);
        tracker = torrent.getTracker();
        pieceLength = torrent.getPieceLength();
        infoHash = torrent.getInfoHash();
        hexHash = torrent.getHexHash();
        torrentState = new File(statePath, hexHash);
        total = torrent.getTotalLength();
        //$NON-NLS-1$
        torrent.save(new File(statePath, hexHash + ".torrent"));
        int numPieces = torrent.getNumPieces();
        pieces = new Vector(numPieces);
        trackerThread = new TrackerThread();
        states = PieceState.createStates(numPieces);
        for (int i = 0; i < numPieces; i++) {
            pieces.add(new Piece(states[i], i));
        }
        bitfield = new byte[numPieces % 8 != 0 ? (numPieces / 8) + 1 : (numPieces / 8)];
        hasPiece = new boolean[numPieces];
        pieceAvailability = new int[numPieces];
        priorityPieces = new boolean[numPieces];
        interestedPieces = new boolean[numPieces];
        uninterestedPieces = new boolean[numPieces];
        incompletePieces = new Vector();
        stateListeners = new Vector();
        errorListeners = new Vector();
        pieceListeners = new Vector();
        progressListeners = new Vector();
        hashCheckListeners = new Vector();
        //$NON-NLS-1$
        propertiesFile = new File(statePath, hexHash + ".properties");
        if (!propertiesFile.exists()) {
            properties = new Properties();
            //$NON-NLS-1$
            properties.setProperty("target", targetFile.getAbsolutePath());
        } else if (properties == null) {
            properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            restore(properties);
        } else {
            restore(properties);
        }
        this.properties = properties;
        store();
        String[] filenames = torrent.getFilenames();
        if (filenames.length != 1 && !targetFile.exists() && !targetFile.mkdirs()) {
            //$NON-NLS-1$
            throw new IOException("The folders needed by this torrent could not be created");
        }
        files = new DataFile[filenames.length];
        fileInitialization(filenames, targetFile);
        for (int i = 0; i < numPieces; i++) {
            Piece piece = (Piece) pieces.get(i);
            piece.setLength(pieceLength);
        }
        ((Piece) pieces.get(numPieces - 1)).setLength((int) (total % pieceLength));
        checkFile();
        setPieces();
    }

    private void checkFile() {
        if (torrentState.exists()) {
            int count = 0;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(torrentState));
                String input = reader.readLine();
                if (input != null && Long.parseLong(input) == targetFile.lastModified()) {
                    input = reader.readLine();
                    PieceState[] states = PieceState.createStates(torrent.getNumPieces());
                    while (input != null) {
                        states[count++].parse(input);
                        input = reader.readLine();
                    }
                    if (count != states.length) {
                        startHashCheck();
                    } else {
                        setPieces(states);
                    }
                } else {
                    startHashCheck();
                }
            } catch (IOException e) {
                startHashCheck();
            }
        } else {
            startHashCheck();
        }
    }

    public boolean performHashCheck() {
        switch(state) {
            case ITorrentStateListener.STOPPED:
                startHashCheck();
                return true;
            case ITorrentStateListener.HASH_CHECKING:
                return true;
            default:
                return false;
        }
    }

    private void startHashCheck() {
        if (hashCheckThread == null || !hashCheckThread.isAlive()) {
            hashCheckThread = new HashCheckThread();
            hashCheckThread.start();
            isHashChecking = true;
            fireStateChangedEvent(ITorrentStateListener.HASH_CHECKING);
        }
    }

    private void fileInitialization(String[] filenames, File targetFile) throws IOException {
        if (files.length == 1) {
            long length = torrent.getLengths()[0];
            files[0] = new DataFile(targetFile, length);
            int[] pieces = new int[torrent.getPieces().length];
            for (int i = 0; i < pieces.length; i++) {
                pieces[i] = i;
            }
            files[0].setPieces(pieces, pieceLength, pieceLength);
            remaining = length;
        } else {
            int count = 0;
            int piece = 0;
            int currentLength = pieceLength;
            for (int i = 0; i < filenames.length; i++) {
                File file = new File(targetFile + File.separator + filenames[i]);
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("The folders needed by this torrent could not be created");
                }
                long fileLength = torrent.getLengths()[i];
                files[i] = new DataFile(file, fileLength);
                if (currentLength > fileLength) {
                    files[i].setPieces(new int[] { count }, (int) fileLength, (int) fileLength);
                    currentLength -= fileLength;
                    continue;
                } else if (currentLength == fileLength) {
                    files[i].setPieces(new int[] { count }, (int) fileLength, (int) fileLength);
                    currentLength = pieceLength;
                    count++;
                    piece++;
                    continue;
                } else if (currentLength != pieceLength && currentLength < fileLength) {
                    fileLength -= currentLength;
                    count++;
                }
                while (fileLength >= pieceLength) {
                    count++;
                    fileLength -= pieceLength;
                }
                count++;
                int[] pieces = new int[count - piece];
                for (int j = piece; j < count; j++) {
                    pieces[j - piece] = j;
                }
                if (fileLength == 0) {
                    files[i].setPieces(pieces, pieceLength, pieceLength);
                } else {
                    files[i].setPieces(pieces, currentLength, pieceLength);
                    currentLength = (int) (pieceLength - fileLength);
                }
                count--;
                piece = count;
            }
            remaining = total;
        }
    }

    private void setPieces() {
        int count = 0;
        int pieceLen = pieceLength;
        for (int i = 0; i < files.length; i++) {
            long length = files[i].length();
            if (pieceLen == 0) {
                pieceLen = pieceLength;
            }
            while (pieceLen < length) {
                Piece piece = (Piece) pieces.get(count);
                piece.addFile(files[i], pieceLen);
                count++;
                length -= pieceLen;
                if (pieceLen < pieceLength) {
                    pieceLen = pieceLength;
                }
            }
            pieceLen -= length;
            ((Piece) pieces.get(count)).addFile(files[i], (int) length);
        }
    }

    private void restore(Properties properties) {
        String value = properties.getProperty(DOWN_SPEED_KEY);
        requestDownSpeed = value != null ? Long.parseLong(value) : -1;
        value = properties.getProperty(UP_SPEED_KEY);
        requestUpSpeed = value != null ? Long.parseLong(value) : -1;
    }

    private void store() throws IOException {
        properties.setProperty(DOWN_SPEED_KEY, Long.toString(requestDownSpeed));
        properties.setProperty(UP_SPEED_KEY, Long.toString(requestUpSpeed));
        properties.store(new FileOutputStream(propertiesFile), null);
    }

    private void updateBitfield() {
        int count = 0;
        int size = hasPiece.length;
        // iterate over all the pieces by multiples of 8
        char[] bits = new char[8];
        for (int i = 0; i < size; i += 8) {
            Arrays.fill(bits, '0');
            // iterate over the 8 (or less) pieces within this segment
            for (int j = i; j < (i + 8) && j < size; j++) {
                // if we have this piece, flag it so
                if (hasPiece[j]) {
                    bits[j - i] = '1';
                }
            }
            // encode the binary string into the bitfield
            bitfield[count] = Encode.encodeForBitfield(bits);
            count++;
        }
    }

    private void updateBitfield(int piece) {
        int offset = piece / 8;
        char[] bits = new char[8];
        Arrays.fill(bits, '0');
        for (int i = offset; i < (offset + 8) && i < hasPiece.length; i++) {
            if (hasPiece[i]) {
                bits[i - offset] = '1';
            }
        }
        bitfield[offset] = Encode.encodeForBitfield(bits);
    }

    /**
	 * Contacts the tracker to begin exchanging pieces with any peers that are
	 * found.
	 * 
	 * @throws IOException
	 *             If an error occurs while querying the tracker or connecting
	 *             to one of the provided peers
	 */
    public void start() throws IOException {
        if (hashCheckThread != null && hashCheckThread.isAlive() && isHashChecking) {
            isWaitingToStart = true;
            return;
        } else if (!torrentState.exists()) {
            startHashCheck();
            isWaitingToStart = true;
            return;
        } else if (running) {
            return;
        }
        isWaitingToStart = false;
        running = true;
        speedMonitoringThread = new SpeedMonitoringThread();
        trackerThread = new TrackerThread();
        speedMonitoringThread.start();
        trackerThread.start();
        fireStateChangedEvent(ITorrentStateListener.STARTED);
        //$NON-NLS-1$
        queryTracker("started");
        fireStateChangedEvent(ITorrentStateListener.EXCHANGING);
    }

    /**
	 * Stops downloading or seeding the torrent.
	 * 
	 * @throws IOException
	 *             If an IOException occurred while informing the tracker that
	 *             the client is stopping
	 */
    public void stop() throws IOException {
        isWaitingToStart = false;
        if (hashCheckThread != null && hashCheckThread.isAlive()) {
            hashCheckThread.interrupt();
            isHashChecking = false;
            hashCheckThread = null;
            fireStateChangedEvent(ITorrentStateListener.STOPPED);
            return;
        } else if (!running) {
            return;
        }
        state = ITorrentStateListener.STOPPED;
        trackerThread.interrupt();
        speedMonitoringThread.interrupt();
        trackerThread = null;
        speedMonitoringThread = null;
        running = false;
        connectionPool.close();
        //$NON-NLS-1$
        queryTracker("stopped");
        fireStateChangedEvent(ITorrentStateListener.STOPPED);
        store();
    }

    /**
	 * Removes all previously saved status and configuration information
	 * regarding the opened torrent. This will call {@link #stop()} prior to the
	 * deletion of the files.
	 */
    public void remove() {
        try {
            stop();
        } catch (IOException e) {
        }
        File[] files = statePath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(hexHash)) {
                files[i].delete();
            }
        }
        remaining = total;
        seeders = -1;
        peers = -1;
        Arrays.fill(priorityPieces, false);
        Arrays.fill(interestedPieces, false);
        Arrays.fill(uninterestedPieces, false);
        for (int i = 0; i < pieces.size(); i++) {
            ((Piece) pieces.get(i)).reset();
        }
    }

    public boolean delete() {
        remove();
        return targetFile.delete();
    }

    private void queryTracker(String event) throws IOException {
        String link = tracker + //$NON-NLS-1$
        "?info_hash=" + //$NON-NLS-1$ //$NON-NLS-2$
        URLEncoder.encode(infoHash, "ISO-8859-1").replaceAll(//$NON-NLS-1$ //$NON-NLS-2$
        "\\+", //$NON-NLS-1$
        "%20") + //$NON-NLS-1$
        "&peer_id=" + //$NON-NLS-1$ //$NON-NLS-2$
        URLEncoder.encode(peerID, "ISO-8859-1").replaceAll(//$NON-NLS-1$ //$NON-NLS-2$
        "\\+", "%20") + "&port=" + //$NON-NLS-1$ //$NON-NLS-2$
        TorrentServer.getPort() + "&uploaded=" + uploaded + "&downloaded=" + //$NON-NLS-1$ //$NON-NLS-2$
        downloaded + //$NON-NLS-1$
        "&left=" + //$NON-NLS-1$
        remaining + (event == null ? "" : "&event=" + event) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "&numwant=" + request + "&compact=1" + "&key=" + //$NON-NLS-1$ //$NON-NLS-2$
        key + //$NON-NLS-1$ //$NON-NLS-2$
        (trackerID != null ? "&trackerid=" + trackerID : "");
        //$NON-NLS-1$
        TorrentConfiguration.debug("Querying the tracker at " + link);
        URL url = new URL(link);
        BEncodedDictionary dictionary = Decode.bDecode(url.openStream());
        if (//$NON-NLS-1$
        event != null && event.equals("stopped")) {
            return;
        }
        //$NON-NLS-1$
        String failure = (String) dictionary.get("failure reason");
        if (failure != null) {
            fireTrackerErrorEvent(failure);
            //$NON-NLS-1$
            TorrentConfiguration.debug("The client could not connect to the tracker, the reason provided was - " + failure);
            return;
        }
        //$NON-NLS-1$
        timeout = ((Long) dictionary.get("interval")).intValue() * 1000;
        if (trackerID == null) {
            //$NON-NLS-1$
            trackerID = (String) dictionary.get("tracker id");
        }
        //$NON-NLS-1$
        Long number = (Long) dictionary.get("completed");
        seeders = number != null ? number.intValue() : -1;
        //$NON-NLS-1$
        number = (Long) dictionary.get("incompleted");
        peers = number != null ? number.intValue() : -1;
        //$NON-NLS-1$
        Object peersList = dictionary.get("peers");
        if (peersList instanceof List) {
            //$NON-NLS-1$
            TorrentConfiguration.debug("No peers were returned");
            return;
        }
        String string = (String) peersList;
        //$NON-NLS-1$
        byte[] bytes = string.getBytes("ISO-8859-1");
        for (int i = 0; i < string.length(); i += 6) {
            String ip = //$NON-NLS-1$ //$NON-NLS-2$
            (bytes[i] & 0xff) + "." + (bytes[i + 1] & 0xff) + "." + (bytes[i + 2] & 0xff) + "." + (//$NON-NLS-1$
            bytes[i + 3] & //$NON-NLS-1$
            0xff);
            int port = Integer.parseInt(Integer.toHexString(bytes[i + 4] & 0xff) + Integer.toHexString(bytes[i + 5] & 0xff), 16);
            connectionPool.connectTo(ip, port);
        }
    }

    public void connectTo(SocketChannel channel) throws UnsupportedEncodingException {
        connectionPool.connectTo(channel);
    }

    /**
	 * Retrieves the block of data that corresponds to the specified piece.
	 * 
	 * @param piece
	 *            the interested piece's number
	 * @return an array of bytes corresponding to the data that is encompassed
	 *         by the specified piece, or <code>null</code> if no data is
	 *         available
	 * @throws IllegalArgumentException
	 *             If <code>piece</code> is negative or if it is not one of
	 *             the piece numbers
	 * @throws IOException
	 *             If an I/O/ error occurs while reading the data from the local
	 *             files
	 */
    private byte[] getPiece(int piece) throws IOException {
        if (piece < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The piece number cannot be negative");
        } else if (piece >= hasPiece.length) {
            throw new IllegalArgumentException(//$NON-NLS-1$
            "The piece number " + piece + " does not exist");
        }
        byte[][] data = new byte[files.length][0];
        Arrays.fill(data, null);
        for (int i = 0; i < files.length; i++) {
            if (files[i].containsPiece(piece)) {
                data[i] = files[i].getData(piece);
            }
        }
        boolean empty = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null && data[i].length != 0) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return null;
        }
        byte[] bytes = new byte[((Piece) pieces.get(piece)).getLength()];
        int offset = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                System.arraycopy(data[i], 0, bytes, offset, data[i].length);
                offset += data[i].length;
            }
        }
        return bytes;
    }

    byte[] getPieceData(int number, int offset, int length) throws IllegalArgumentException, IOException {
        byte[] piece = getPiece(number);
        if (piece == null) {
            return null;
        }
        if (offset + length > piece.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The block of data that is being requested goes beyond the range of the requested piece");
        }
        byte[] block = new byte[length];
        System.arraycopy(piece, offset, block, 0, length);
        return block;
    }

    /**
	 * Performs a hash check on the specified piece number to see whether the
	 * data is corrupt or not.
	 * 
	 * @param piece
	 *            the number of the piece to check
	 * @return <code>true</code> if the SHA-1 hash of the particular piece's
	 *         data matches the torrent file's hash, <code>false</code>
	 *         otherwise
	 * @throws IllegalArgumentException
	 *             If the specified piece has not been completed
	 * @throws IOException
	 *             If an I/O error occurs while retrieving the piece's data from
	 *             a particular file
	 */
    private boolean hashCheck(int piece) throws IllegalArgumentException, IOException {
        byte[] data = getPiece(piece);
        return data == null ? false : torrent.getPieces()[piece].equals(//$NON-NLS-1$
        new String(shaDigest.digest(data), "ISO-8859-1"));
    }

    private void saveState() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(torrentState, false));
        writer.write(Long.toString(targetFile.lastModified()));
        writer.newLine();
        for (int i = 0; i < states.length; i++) {
            writer.write(states[i].toString());
            writer.newLine();
        }
        writer.flush();
    }

    /**
	 * Writes the data that has been received from a peer onto the local files.
	 * 
	 * @param number
	 *            the piece's number that this data corresponds to
	 * @param index
	 *            the position within the piece that the retrieved data starts
	 *            from
	 * @param data
	 *            the data sent from the peer
	 * @param offset
	 *            the offset within <code>data</code> that the actual bytes
	 *            for the files start at
	 * @param length
	 *            the amount of bytes of data that has been received
	 * @throws IOException
	 *             If an I/O error occurs while writing to a file, saving the
	 *             state information, performing a hash check, or querying the
	 *             tracker
	 */
    synchronized void write(int number, int index, byte[] data, int offset, int length) throws IOException {
        Piece piece = (Piece) pieces.get(number);
        if (!hasPiece[number] && piece.write(index, data, offset, length)) {
            remaining -= length;
            downloaded += length;
            saveState();
            fireBlockDownloadedEvent(number, index, length);
            if (!incompletePieces.contains(piece)) {
                incompletePieces.add(piece);
            }
            if (piece.isComplete()) {
                incompletePieces.remove(piece);
                checkCompletedPiece(piece, number);
            }
        }
    }

    private void checkCompletedPiece(Piece piece, int number) throws IOException {
        if (hashCheck(number)) {
            TorrentConfiguration.debug(//$NON-NLS-1$ //$NON-NLS-2$
            "Piece " + number + " passed hash check");
            hasPiece[number] = true;
            updateBitfield(number);
            firePieceCompletedEvent(++completedPieces);
            connectionPool.queueHaveMessage(number);
            for (int i = 0; i < hasPiece.length; i++) {
                if (!hasPiece[i]) {
                    return;
                }
            }
            isCompleted = true;
            fireStateChangedEvent(ITorrentStateListener.FINISHED);
            connectionPool.disconnectSeeds();
            // let the tracker know that the download has completed
            //$NON-NLS-1$
            queryTracker("completed");
        } else {
            TorrentConfiguration.debug(//$NON-NLS-1$
            "Piece " + number + " has failed the hash check");
            piece.reset();
            int pieceLength = piece.getLength();
            discarded += pieceLength;
            remaining += (remaining == total) ? 0 : pieceLength;
            firePieceDiscardEvent(number, pieceLength);
        }
    }

    synchronized Piece request(boolean[] peerPieces) {
        if (isCompleted) {
            return null;
        }
        Piece request = null;
        if (!isSelective) {
            request = request(hasPiece, peerPieces);
        } else if (isPrioritizing) {
            request = request(priorityPieces, peerPieces);
            if (request == null) {
                request = request(interestedPieces, peerPieces);
            }
        } else {
            request = request(interestedPieces, peerPieces);
        }
        return request;
    }

    private Piece request(boolean[] compare, boolean[] peerPieces) {
        boolean isInterested = false;
        if (compare == hasPiece) {
            for (int i = 0; i < peerPieces.length; i++) {
                if (!hasPiece[i] && peerPieces[i]) {
                    isInterested = true;
                    break;
                }
            }
        } else {
            for (int i = 0; i < peerPieces.length; i++) {
                if (compare[i] && peerPieces[i]) {
                    isInterested = true;
                    break;
                }
            }
        }
        if (!isInterested) {
            return null;
        }
        boolean hasIncompletePiece = false;
        for (int i = 0; i < incompletePieces.size(); i++) {
            Piece piece = (Piece) incompletePieces.get(i);
            if (peerPieces[piece.getNumber()]) {
                hasIncompletePiece = true;
                break;
            }
        }
        if (hasIncompletePiece) {
            if (incompletePieces.size() == 0) {
                return hasPiece == compare ? getRarePiece(peerPieces) : getRarePiece(compare, peerPieces);
            }
            Piece piece = (Piece) incompletePieces.get(ConnectionPool.RANDOM.nextInt(incompletePieces.size()));
            while (!peerPieces[piece.getNumber()]) {
                if (isCompleted) {
                    return null;
                } else if (incompletePieces.size() == 0) {
                    return hasPiece == compare ? getRarePiece(peerPieces) : getRarePiece(compare, peerPieces);
                }
                piece = (Piece) incompletePieces.get(ConnectionPool.RANDOM.nextInt(incompletePieces.size()));
            }
            return piece;
        } else {
            return hasPiece == compare ? getRarePiece(peerPieces) : getRarePiece(compare, peerPieces);
        }
    }

    private Piece getRarePiece(boolean[] peerPieces) {
        int min = pieceAvailability[0];
        for (int i = 1; i < peerPieces.length; i++) {
            if (!hasPiece[i] && peerPieces[i] && min > pieceAvailability[i]) {
                min = pieceAvailability[i];
            }
        }
        int size = pieces.size();
        int pieceNumber = ConnectionPool.RANDOM.nextInt(size);
        while (hasPiece[pieceNumber] || pieceAvailability[pieceNumber] != min || !peerPieces[pieceNumber]) {
            if (isCompleted) {
                return null;
            }
            pieceNumber = ConnectionPool.RANDOM.nextInt(size);
        }
        return (Piece) pieces.get(pieceNumber);
    }

    private Piece getRarePiece(boolean[] compare, boolean[] peerPieces) {
        int min = pieceAvailability[0];
        for (int i = 1; i < peerPieces.length; i++) {
            if (!hasPiece[i] && compare[i] && peerPieces[i] && min > pieceAvailability[i]) {
                min = pieceAvailability[i];
            }
        }
        int size = pieces.size();
        int pieceNumber = ConnectionPool.RANDOM.nextInt(size);
        while (hasPiece[pieceNumber] || pieceAvailability[pieceNumber] != min || !compare[pieceNumber] || !peerPieces[pieceNumber]) {
            if (isCompleted) {
                return null;
            }
            pieceNumber = ConnectionPool.RANDOM.nextInt(size);
        }
        return (Piece) pieces.get(pieceNumber);
    }

    String getPeerID() {
        return peerID;
    }

    byte[] getBitfield() {
        return bitfield;
    }

    /**
	 * Used to indicate that the specified piece now has another user that has
	 * it. This information is for identifying which pieces are rare amongst the
	 * list of connected peers.
	 * 
	 * @param piece
	 *            the number of the piece
	 */
    void updatePieceAvailability(int piece) {
        pieceAvailability[piece]++;
    }

    void addPieceAvailability(boolean[] peerPieces) {
        if (peerPieces.length != pieceAvailability.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The length of the array is not " + pieceAvailability.length);
        }
        for (int i = 0; i < pieceAvailability.length; i++) {
            if (peerPieces[i]) {
                pieceAvailability[i]++;
            }
        }
    }

    void removePieceAvailability(boolean[] peerPieces) {
        if (peerPieces.length != pieceAvailability.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The length of the array is not " + pieceAvailability.length);
        }
        for (int i = 0; i < pieceAvailability.length; i++) {
            if (peerPieces[i]) {
                pieceAvailability[i]--;
            }
        }
    }

    void addToUploaded(long length) {
        uploaded += length;
    }

    /**
	 * Sets the maximum number of connections that the host should attempt to
	 * connect to. The default value is set to 50 although 30 peers should
	 * already be plenty. This value should not be heightened unless there is a
	 * good reason to do so as it will likely cause network congestions.
	 * 
	 * @param maxConnections
	 *            the maximum number of connections that should be used
	 */
    public void setMaxConnections(int maxConnections) {
        connectionPool.setMaxConnections(maxConnections);
    }

    /**
	 * Setup file download priority levels and whether a file should even be
	 * downloaded at all.
	 * 
	 * @param downloadChoices
	 *            an integer array which stores a value greater than zero if the
	 *            file should have a high priority, a value equal to zero if it
	 *            should have a regular priority, or less than zero if it should
	 *            not be downloaded at all, the values should correspond to the
	 *            files returned from {@link TorrentFile}'s
	 *            {@link TorrentFile#getFilenames()} method
	 */
    public synchronized void setFilesToDownload(int[] downloadChoices) {
        if (files.length != downloadChoices.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The provided array should be of length " + files.length);
        }
        for (int i = 0; i < downloadChoices.length; i++) {
            int[] pieces = files[i].getPieces();
            for (int j = 0; j < pieces.length; j++) {
                if (downloadChoices[i] > 0) {
                    priorityPieces[j] = true;
                } else if (downloadChoices[i] == 0) {
                    interestedPieces[j] = true;
                } else {
                    uninterestedPieces[j] = uninterestedPieces[j] || false;
                }
            }
        }
        if (isSelective) {
            return;
        }
        for (int i = 0; i < downloadChoices.length; i++) {
            if (downloadChoices[i] > 0) {
                isPrioritizing = true;
                break;
            }
        }
        for (int i = 0; i < downloadChoices.length; i++) {
            for (int j = i + 1; j < downloadChoices.length; j++) {
                if (downloadChoices[i] != downloadChoices[j]) {
                    isSelective = true;
                    return;
                }
            }
        }
        isSelective = false;
    }

    public void setMaxDownloadSpeed(long maximum) {
        if (maximum < 1) {
            maxDownSpeed = -1;
            requestDownSpeed = -1;
        } else {
            maxDownSpeed = maximum;
            requestDownSpeed = maximum;
        }
    }

    public void setMaxUploadSpeed(long maximum) {
        if (maximum < 1) {
            maxUpSpeed = -1;
            requestDownSpeed = -1;
        } else {
            maxUpSpeed = maximum;
            requestUpSpeed = maximum;
        }
    }

    long getMaxDownloadSpeed() {
        return maxDownSpeed;
    }

    long getMaxUploadSpeed() {
        return maxUpSpeed;
    }

    long getDownloadRequestSpeed() {
        return requestDownSpeed;
    }

    long getUploadRequestSpeed() {
        return requestUpSpeed;
    }

    void updateDownloadRequestSpeed(int amount) {
        if (requestDownSpeed == -1) {
            return;
        }
        requestDownSpeed -= requestDownSpeed > amount ? amount : requestDownSpeed;
    }

    void updateUploadRequestSpeed(int amount) {
        if (requestUpSpeed == -1) {
            return;
        }
        requestUpSpeed -= requestUpSpeed > amount ? amount : requestUpSpeed;
    }

    /**
	 * Retrieves the amount that has been downloaded thus far since the original
	 * call to {@link #start()}.
	 * 
	 * @return the amount of bytes that has been downloaded from peers
	 */
    public long getDownloaded() {
        return downloaded;
    }

    /**
	 * Returns the number of bytes that has been uploaded to peers thus far
	 * since calling {@link #start()}.
	 * 
	 * @return the amount of bytes that has been uploaded to peers
	 */
    public long getUploaded() {
        return uploaded;
    }

    /**
	 * Retreives the number of bytes that are required to complete the download.
	 * 
	 * @return the number of bytes left to complete the download
	 */
    public long getRemaining() {
        return remaining;
    }

    /**
	 * Gets the downloading speed as calculated over a twenty second rolling
	 * average.
	 * 
	 * @return the speed at which bytes are being downloaded from peers
	 */
    public long getDownSpeed() {
        return downSpeed;
    }

    /**
	 * Retrieves the uploading speed per calculations over a twenty second
	 * rolling average.
	 * 
	 * @return the speed at which bytes are being uploaded to peers
	 */
    public long getUpSpeed() {
        return upSpeed;
    }

    public long getTimeRemaining() {
        return isCompleted ? 0 : downSpeed == 0 ? -1 : Math.round(remaining / downSpeed);
    }

    /**
	 * Retrieves the amount of data that has been discarded thus far. This is
	 * caused by pieces that has failed the integrity hash check.
	 * 
	 * @return the amount of bytes that has been discarded
	 */
    public long getDiscarded() {
        return discarded;
    }

    /**
	 * Retrieves the number of peers that connections have been created for thus
	 * far.
	 * 
	 * @return the number of connected peers
	 */
    public int getConnectedPeers() {
        return connectionPool.getConnected();
    }

    /**
	 * Returns the number of seeds that are currently assisting with the
	 * distribution.
	 * 
	 * @return the number of connected seeds, if the value is <code>-1</code>,
	 *         the tracker does not support the distribution of this information
	 * @see #getPeers()
	 */
    public int getSeeds() {
        return seeders;
    }

    /**
	 * Returns the total number of peers that are downloading the torrent that
	 * this <code>Host</code> is associated with.
	 * 
	 * @return the total number of connected peers on the torrent, if the value
	 *         is <code>-1</code>, the tracker does not support the
	 *         distribution of this information
	 * @see #getSeeds()
	 */
    public int getPeers() {
        return peers;
    }

    /**
	 * Retrieves the torrent that was used to create this <code>Host</code>.
	 * 
	 * @return the <code>Torrent</code> associated with this
	 */
    public TorrentFile getTorrentFile() {
        return torrent;
    }

    /**
	 * Retrieves the current state in which the host is currently in. This could
	 * be any one of the states provided by the {@link ITorrentStateListener}
	 * interface.
	 * 
	 * @return the state that the host is currently in
	 * @see ITorrentStateListener#STARTED
	 * @see ITorrentStateListener#EXCHANGING
	 * @see ITorrentStateListener#STOPPED
	 * @see ITorrentStateListener#FINISHED
	 */
    public int getState() {
        return state;
    }

    /**
	 * Adds the specified listener to the collection of listeners within this
	 * host if it is not already contained. The listener will be notified of the
	 * changes of the current state of the torrent's activity. The event's state
	 * will correspond to the value returned from {@link #getState()}.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentStateListener(ITorrentStateListener listener) throws IllegalArgumentException {
        synchronized (stateListeners) {
            if (!stateListeners.contains(listener)) {
                stateListeners.add(listener);
            }
        }
    }

    /**
	 * Adds the specified listener to the collection of listeners within this
	 * host if it is not already contained. The listener will be notified when
	 * another piece has been completed by verifying it against a hash sum.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentProgressListener(ITorrentProgressListener listener) throws IllegalArgumentException {
        synchronized (progressListeners) {
            if (!progressListeners.contains(listener)) {
                progressListeners.add(listener);
            }
        }
    }

    /**
	 * Adds the specified listener to the collection of listeners within this
	 * host if it is not already contained. The listener will be notified when a
	 * piece has downloaded some amount of additional bytes.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addPieceProgressListener(IPieceProgressListener listener) throws IllegalArgumentException {
        synchronized (pieceListeners) {
            if (!pieceListeners.contains(listener)) {
                pieceListeners.add(listener);
            }
        }
    }

    /**
	 * Adds the specified listener to the collection of listeners within this
	 * host if it is not already contained. The listener will be notified when
	 * errors such as tracker failures or hash check failures occurs.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentErrorListener(ITorrentErrorListener listener) throws IllegalArgumentException {
        synchronized (errorListeners) {
            if (!errorListeners.contains(listener)) {
                errorListeners.add(listener);
            }
        }
    }

    public void addHashCheckListener(IHashCheckListener listener) {
        synchronized (hashCheckListeners) {
            if (!hashCheckListeners.contains(listener)) {
                hashCheckListeners.add(listener);
            }
        }
    }

    public boolean removeTorrentStateListener(ITorrentStateListener listener) {
        synchronized (stateListeners) {
            return stateListeners.remove(listener);
        }
    }

    public boolean removeTorrentProgressListener(ITorrentProgressListener listener) {
        synchronized (progressListeners) {
            return progressListeners.remove(listener);
        }
    }

    public boolean removePieceProgressListener(IPieceProgressListener listener) {
        synchronized (pieceListeners) {
            return pieceListeners.remove(listener);
        }
    }

    public boolean removeTorrentErrorListener(ITorrentErrorListener listener) {
        synchronized (errorListeners) {
            return errorListeners.remove(listener);
        }
    }

    public boolean removeHashCheckListener(IHashCheckListener listener) {
        synchronized (hashCheckListeners) {
            return hashCheckListeners.remove(listener);
        }
    }

    private synchronized void fireStateChangedEvent(int state) {
        this.state = state;
        for (int i = 0; i < stateListeners.size(); i++) {
            ((ITorrentStateListener) stateListeners.get(i)).stateChanged(state);
        }
    }

    private void fireBlockDownloadedEvent(int piece, int index, int blockLength) {
        synchronized (pieceListeners) {
            for (int i = 0; i < pieceListeners.size(); i++) {
                ((IPieceProgressListener) pieceListeners.get(i)).blockDownloaded(piece, index, blockLength);
            }
        }
    }

    private void firePieceCompletedEvent(int completed) {
        synchronized (progressListeners) {
            for (int i = 0; i < progressListeners.size(); i++) {
                ((ITorrentProgressListener) progressListeners.get(i)).pieceCompleted(completed);
            }
        }
    }

    private void fireTrackerErrorEvent(String message) {
        synchronized (errorListeners) {
            for (int i = 0; i < errorListeners.size(); i++) {
                ((ITorrentErrorListener) errorListeners.get(i)).trackerError(message);
            }
        }
    }

    private void firePieceDiscardEvent(int piece, int pieceLength) {
        synchronized (errorListeners) {
            for (int i = 0; i < errorListeners.size(); i++) {
                ((ITorrentErrorListener) errorListeners.get(i)).pieceDiscarded(piece, pieceLength);
            }
        }
    }

    private void fireHashCheckedEvent(int piece) {
        synchronized (hashCheckListeners) {
            for (int i = 0; i < hashCheckListeners.size(); i++) {
                ((IHashCheckListener) hashCheckListeners.get(i)).hashChecked(piece);
            }
        }
    }

    /**
	 * Loads the stored information regarding each piece's status information
	 * and updates the torrent as such.
	 * 
	 * @param states
	 *            a specification of how far and how much each piece has
	 *            downloaded
	 * @throws IllegalArgumentException
	 *             if the provided array's length is not equal to the number of
	 *             pieces
	 */
    private void setPieces(PieceState[] states) {
        if (this.states.length != states.length) {
            throw new IllegalArgumentException(//$NON-NLS-1$
            "The array's size should be " + this.states.length);
        }
        this.states = states;
        for (int i = 0; i < states.length; i++) {
            Piece piece = (Piece) pieces.get(i);
            if (piece.isComplete()) {
                completedPieces--;
                hasPiece[i] = false;
            }
            remaining += piece.getWritten();
            piece.setState(states[i]);
            int written = piece.getWritten();
            remaining -= written;
            if (written == piece.getLength()) {
                completedPieces++;
                hasPiece[i] = true;
                incompletePieces.remove(piece);
            } else if (written != 0 && !incompletePieces.contains(piece)) {
                incompletePieces.add(piece);
            }
        }
        updateBitfield();
        for (int i = 0; i < hasPiece.length; i++) {
            if (!hasPiece[i]) {
                return;
            }
        }
        isCompleted = true;
    }

    private class HashCheckThread extends Thread {

        private  HashCheckThread() {
            //$NON-NLS-1$
            super("Hash Check Thread - " + torrent.getName());
        }

        private void cleanup() {
            isHashChecking = false;
            fireStateChangedEvent(ITorrentStateListener.STOPPED);
        }

        public void run() {
            try {
                int read = 0;
                int count = 0;
                ByteBuffer buffer = ByteBuffer.allocate(pieceLength);
                for (int i = 0; i < files.length; i++) {
                    FileChannel channel = files[i].getChannel();
                    while ((read += channel.read(buffer)) == pieceLength) {
                        if (isInterrupted()) {
                            cleanup();
                            return;
                        }
                        Piece piece = (Piece) pieces.get(count);
                        if (piece.isComplete()) {
                            completedPieces--;
                            hasPiece[i] = false;
                        }
                        remaining += piece.getWritten();
                        buffer.rewind();
                        if (torrent.getPieces()[count].equals(new String(shaDigest.digest(buffer.array()), "ISO-8859-1"))) {
                            piece.setAsCompleted();
                            hasPiece[count] = true;
                            completedPieces++;
                            remaining -= piece.getLength();
                        } else {
                            piece.reset();
                        }
                        incompletePieces.remove(piece);
                        fireHashCheckedEvent(count);
                        count++;
                        read = 0;
                    }
                }
                if (read > 0) {
                    if (isInterrupted()) {
                        cleanup();
                        return;
                    }
                    Piece piece = (Piece) pieces.get(count);
                    if (piece.isComplete()) {
                        completedPieces--;
                        hasPiece[count] = false;
                    }
                    remaining += piece.getWritten();
                    buffer.rewind();
                    shaDigest.update(buffer.array(), 0, read);
                    if (torrent.getPieces()[count].equals(new String(shaDigest.digest(), //$NON-NLS-1$
                    "ISO-8859-1"))) {
                        hasPiece[count] = true;
                        piece.setAsCompleted();
                        completedPieces++;
                        remaining -= piece.getLength();
                    } else {
                        piece.reset();
                    }
                    incompletePieces.remove(piece);
                    fireHashCheckedEvent(count);
                }
                updateBitfield();
                saveState();
                if (isInterrupted()) {
                    cleanup();
                    return;
                }
                isHashChecking = false;
                if (isWaitingToStart) {
                    try {
                        TorrentManager.this.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (ClosedByInterruptException e) {
                cleanup();
                try {
                    saveState();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                cleanup();
                throw new RuntimeException(e);
            }
        }
    }

    private class TrackerThread extends Thread {

        private  TrackerThread() {
            //$NON-NLS-1$
            super("Tracker Thread - " + torrent.getName());
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(timeout);
                    queryTracker(null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private class SpeedMonitoringThread extends Thread {

        private  SpeedMonitoringThread() {
            //$NON-NLS-1$
            super("Speed Monitoring Thread - " + torrent.getName());
        }

        public void run() {
            long totalDown;
            long totalUp;
            long lastDownloaded = downloaded;
            long lastUploaded = uploaded;
            long[] downloads = new long[20];
            long[] uploads = new long[20];
            while (true) {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    downloads[i] = downloaded - lastDownloaded;
                    uploads[i] = uploaded - lastUploaded;
                    totalDown = 0;
                    totalUp = 0;
                    for (int j = 0; j < 20; j++) {
                        totalDown += downloads[j];
                        totalUp += uploads[j];
                    }
                    downSpeed = totalDown / 20;
                    upSpeed = totalUp / 20;
                    lastDownloaded = downloaded;
                    lastUploaded = uploaded;
                    requestDownSpeed = maxDownSpeed;
                    requestUpSpeed = maxUpSpeed;
                }
            }
        }
    }
}
