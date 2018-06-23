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
 * This listener monitors the completion of hash checks for pieces.
 */
public interface IHashCheckListener {

    /**
	 * This method is called after the piece with the given number has completed
	 * its hash check. This is only called during the initial stage of the hash
	 * checking when the torrent has just been created or when
	 * {@link Torrent#performHashCheck()} has been called. Hash checks that are
	 * performed when files are being downloaded are notified via
	 * {@link ITorrentProgressListener}'s
	 * {@link ITorrentProgressListener#pieceCompleted(int)} method.
	 * 
	 * @param number
	 *            the number of the piece that has just finished its hash check
	 */
    public void hashChecked(int number);
}
