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

/**
 * A listener for monitoring what the associated torrent is currently doing.
 */
public interface ITorrentStateListener {

    /**
	 * Used to indicate that the torrent has finished the preprocessing setup
	 * and is about to start by communicating with the tracker.
	 */
    public static final int STARTED = 0;

    /**
	 * Used to indicate that the torrent is currently exchanging or waiting to
	 * exchange pieces with other peers.
	 */
    public static final int EXCHANGING = STARTED + 1;

    /**
	 * Used to indicate that the torrent is no longer connected.
	 * 
	 * @see Torrent#stop()
	 */
    public static final int STOPPED = EXCHANGING + 1;

    /**
	 * Used to indicate the torrent has been completed.
	 */
    public static final int FINISHED = STOPPED + 1;

    /**
	 * Used to indicate that the torrent is currently performing a hash check
	 * for file integrity for all of its pieces.
	 * 
	 * @see Torrent#performHashCheck()
	 */
    public static final int HASH_CHECKING = FINISHED + 1;

    /**
	 * This method is called to indicate a change in the torrent's current
	 * status.
	 * 
	 * @param state
	 *            the new state that the torrent is in
	 * @see #STARTED
	 * @see #EXCHANGING
	 * @see #STOPPED
	 * @see #FINISHED
	 * @see #HASH_CHECKING
	 */
    public void stateChanged(int state);
}
