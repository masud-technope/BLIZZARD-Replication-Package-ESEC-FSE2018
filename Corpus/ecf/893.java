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
package org.eclipse.ecf.protocol.bittorrent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import org.eclipse.ecf.protocol.bittorrent.internal.net.TorrentManager;

/**
 * The <code>Torrent</code> class is used for hooking onto a
 * {@link TorrentFile} so that pieces can be exchanged with peers.
 */
public class Torrent {

    /**
	 * The {@link TorrentManager} that is managing all of the backend functions
	 * of this torrent.
	 */
    private final TorrentManager manager;

    /**
	 * The 20-length SHA-1 hash of the torrent file's metainfo's
	 * <code>info</code> dictionary.
	 */
    private final String infoHash;

    /**
	 * Creates a new <code>Torrent</code> to begin with exchanging pieces with
	 * other pieces for the given torrent.
	 * 
	 * @param torrent
	 *            the torrent file to use
	 * @param properties
	 *            a <code>Properties</code> instance for storing information,
	 *            this can be <code>null</code>
	 * @throws IOException
	 *             If an I/O error occurs whilst creating or hooking up with the
	 *             files associated with the torrent
	 */
     Torrent(TorrentFile torrent, Properties properties) throws IOException {
        manager = new TorrentManager(torrent, properties);
        infoHash = torrent.getInfoHash();
    }

    /**
	 * Bridges the connection with the provided channel.
	 * 
	 * @param channel
	 *            the channel to connect to
	 * @throws UnsupportedEncodingException
	 *             If the <code>ISO-8859-1</code> encoding is not supported
	 */
    void connectTo(SocketChannel channel) throws UnsupportedEncodingException {
        manager.connectTo(channel);
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
        manager.start();
        TorrentServer.addTorrent(infoHash, this);
    }

    /**
	 * Stops downloading, seeding, or the hash checking process for this
	 * torrent. It is crucial that this method be called for termination as it
	 * will perform clean-up on lingering threads that are performing background
	 * operations.
	 * 
	 * @throws IOException
	 *             If an IOException occurred while informing the tracker that
	 *             the client is stopping
	 */
    public void stop() throws IOException {
        TorrentServer.remove(infoHash);
        manager.stop();
    }

    /**
	 * Removes all previously saved status and configuration information
	 * regarding the opened torrent. This will call {@link #stop()} prior to the
	 * deletion of the files.
	 */
    public void remove() {
        TorrentServer.remove(infoHash);
        manager.remove();
    }

    /**
	 * Removes all information pertaining to the opened torrent in addition to
	 * removing the target that has been set for downloading or seeding.
	 * 
	 * @return <code>true</code> if the target file or directory has been
	 *         deleted successfully, <code>false</code> otherwise
	 */
    public boolean delete() {
        TorrentServer.remove(infoHash);
        return manager.delete();
    }

    /**
	 * Performs a hash check on all pieces for this torrnet in a separate
	 * thread. This may not necessarily run if the torrent is currently
	 * performing other actions. The returned value will indicate whether the
	 * hash check is being performed or not.
	 * 
	 * @return <code>true</code> if the torrent will begin performing hash
	 *         check on its pieces on a separate thread or is already running a
	 *         hash check, </code>false</code> if the torrent is currently
	 *         performing other operations and is unable to run a hash check
	 */
    public boolean performHashCheck() {
        return manager.performHashCheck();
    }

    /**
	 * Adds the specified listener to the collection of listeners for this
	 * torrent if it is not already contained. The listener will be notified of
	 * the changes of the current state of the torrent's activity. The event's
	 * state will correspond to the value returned from {@link #getState()}.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentStateListener(ITorrentStateListener listener) throws IllegalArgumentException {
        if (listener == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The listener cannot be null");
        }
        manager.addTorrentStateListener(listener);
    }

    /**
	 * Adds the specified listener to the collection of listeners for this
	 * torrent if it is not already contained. The listener will be notified
	 * when another piece has been completed by verifying it against a hash sum.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentProgressListener(ITorrentProgressListener listener) throws IllegalArgumentException {
        if (listener == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The listener cannot be null");
        }
        manager.addTorrentProgressListener(listener);
    }

    /**
	 * Adds the specified listener to the collection of listeners for this
	 * torrent if it is not already contained. The listener will be notified
	 * when a piece has downloaded some amount of additional bytes.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addPieceProgressListener(IPieceProgressListener listener) throws IllegalArgumentException {
        if (listener == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The listener cannot be null");
        }
        manager.addPieceProgressListener(listener);
    }

    /**
	 * Adds the specified listener to the collection of listeners for this
	 * torrent if it is not already contained. The listener will be notified
	 * when a piece has successfully completed its hash check.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addHashCheckListener(IHashCheckListener listener) {
        if (listener == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The listener cannot be null");
        }
        manager.addHashCheckListener(listener);
    }

    /**
	 * Adds the specified listener to the collection of listeners for this
	 * torrent if it is not already contained. The listener will be notified
	 * when errors such as tracker failures or hash check failures occurs.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @throws IllegalArgumentException
	 *             If <code>listener</code> is <code>null</code>
	 */
    public void addTorrentErrorListener(ITorrentErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The listener cannot be null");
        }
        manager.addTorrentErrorListener(listener);
    }

    /**
	 * Removes the specified listener so that it will no longer be notified of
	 * state updates.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if one such listener was removed,
	 *         <code>false</code> otherwise
	 */
    public boolean removeTorrentStateListener(ITorrentStateListener listener) {
        return manager.removeTorrentStateListener(listener);
    }

    /**
	 * Removes the specified listener so that it will no longer be notified when
	 * pieces has been completed.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if one such listener was removed,
	 *         <code>false</code> otherwise
	 */
    public boolean removeTorrentProgressListener(ITorrentProgressListener listener) {
        return manager.removeTorrentProgressListener(listener);
    }

    /**
	 * Removes the specified listener so that it will no longer be notified of a
	 * piece having downloaded some additional bytes.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if one such listener was removed,
	 *         <code>false</code> otherwise
	 */
    public boolean removePieceProgressListener(IPieceProgressListener listener) {
        return manager.removePieceProgressListener(listener);
    }

    /**
	 * Removes the specified listener so that it will no longer be notified of
	 * pieces have completed the hash checking process.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if one such listener was removed,
	 *         <code>false</code> otherwise
	 */
    public boolean removeHashCheckListener(IHashCheckListener listener) {
        return manager.removeHashCheckListener(listener);
    }

    /**
	 * Removes the specified listener so that it will no longer be notified of
	 * errors.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return <code>true</code> if one such listener was removed,
	 *         <code>false</code> otherwise
	 */
    public boolean removeTorrentErrorListener(ITorrentErrorListener listener) {
        return manager.removeTorrentErrorListener(listener);
    }

    /**
	 * Sets the maximum number of connections that this torrent should attempt
	 * to connect to. The default value is set to 50 although 30 peers should
	 * already be plenty. This value should not be heightened unless there is a
	 * good reason to do so as it will likely cause network congestions.
	 * 
	 * @param maxConnections
	 *            the maximum number of connections that should be used
	 * @throws IllegalArgumentException
	 *             If <code>maxConnections</code> is negative
	 */
    public void setMaxConnections(int maxConnections) throws IllegalArgumentException {
        if (maxConnections < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The maximum number of connections cannot be negative");
        }
        manager.setMaxConnections(maxConnections);
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
	 * @throws IllegalArgumentException
	 *             If <code>downloadChoices</code> is <code>null</code> or
	 *             if the length of the array is not equal to the number of
	 *             files for this torrent
	 */
    public void setFilesToDownload(int[] downloadChoices) {
        if (downloadChoices == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The array cannot be null");
        }
        manager.setFilesToDownload(downloadChoices);
    }

    /**
	 * Retrieves the amount that has been downloaded thus far since the original
	 * call to {@link #start()}.
	 * 
	 * @return the amount of bytes that has been downloaded from peers
	 */
    public long getDownloaded() {
        return manager.getDownloaded();
    }

    /**
	 * Returns the number of bytes that has been uploaded to peers thus far
	 * since calling {@link #start()}.
	 * 
	 * @return the amount of bytes that has been uploaded to peers
	 */
    public long getUploaded() {
        return manager.getUploaded();
    }

    /**
	 * Retreives the number of bytes that are required to complete the download.
	 * 
	 * @return the number of bytes left to complete the download
	 */
    public long getRemaining() {
        return manager.getRemaining();
    }

    /**
	 * Gets the downloading speed as calculated over a twenty second rolling
	 * average.
	 * 
	 * @return the speed at which bytes are being downloaded from peers
	 */
    public long getDownSpeed() {
        return manager.getDownSpeed();
    }

    /**
	 * Retrieves the uploading speed per calculations over a twenty second
	 * rolling average.
	 * 
	 * @return the speed at which bytes are being uploaded to peers
	 */
    public long getUpSpeed() {
        return manager.getUpSpeed();
    }

    /**
	 * Sets the maximum number of bytes to download per second from peers. If
	 * <code>maximum</code> is zero or negative, the speed capping limit will
	 * be lifted.
	 * 
	 * @param maximum
	 *            the maximum number of bytes that should be downloaded from
	 *            peers per second
	 */
    public void setMaxDownloadSpeed(long maximum) {
        manager.setMaxDownloadSpeed(maximum);
    }

    /**
	 * Sets the maximum number of bytes to upload per second to peers. If
	 * <code>maximum</code> is zero or negative, the speed capping limit will
	 * be lifted.
	 * 
	 * @param maximum
	 *            the maximum number of bytes that should be uploaded to peers
	 *            per second
	 */
    public void setMaxUploadSpeed(long maximum) {
        manager.setMaxUploadSpeed(maximum);
    }

    /**
	 * Retrieves the time in seconds that are remaining for this download to
	 * complete. If the returned value is <code>-1</code>, the time is
	 * unknown. This will be returned when the downloading speed is at 0.
	 * 
	 * @return the time remaining in seconds for the download to complete or
	 *         <code>-1</code> if the value is not known
	 */
    public long getTimeRemaining() {
        return manager.getTimeRemaining();
    }

    /**
	 * Retrieves the amount of data that has been discarded thus far. This is
	 * caused by pieces that has failed the integrity hash check.
	 * 
	 * @return the amount of bytes that has been discarded
	 */
    public long getDiscarded() {
        return manager.getDiscarded();
    }

    /**
	 * Retrieves the number of peers that connections have been created for thus
	 * far.
	 * 
	 * @return the number of connected peers
	 */
    public int getConnectedPeers() {
        return manager.getConnectedPeers();
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
        return manager.getSeeds();
    }

    /**
	 * Returns the total number of peers that are attempting to download this
	 * torrent.
	 * 
	 * @return the total number of connected peers on the torrent, if the value
	 *         is <code>-1</code>, the tracker does not support the
	 *         distribution of this information
	 * @see #getSeeds()
	 */
    public int getPeers() {
        return manager.getPeers();
    }

    /**
	 * Retrieves the torrent file that was used to create this torrent.
	 * 
	 * @return the {@link TorrentFile} that initialized this
	 */
    public TorrentFile getTorrentFile() {
        return manager.getTorrentFile();
    }

    /**
	 * Retrieves the current state in which this torrent is currently in. This
	 * could be any one of the states provided by the
	 * {@link ITorrentStateListener} interface.
	 * 
	 * @return the state that this torrent is currently in
	 * @see ITorrentStateListener#STARTED
	 * @see ITorrentStateListener#EXCHANGING
	 * @see ITorrentStateListener#STOPPED
	 * @see ITorrentStateListener#FINISHED
	 * @see ITorrentStateListener#HASH_CHECKING
	 */
    public int getState() {
        return manager.getState();
    }
}
