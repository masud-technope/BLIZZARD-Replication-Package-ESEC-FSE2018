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
 * This listener reports on the overall progress of the current download.
 */
public interface ITorrentProgressListener {

    /**
	 * This method is called when a piece has been identified as being completed
	 * after a hash check verification has completed.
	 * 
	 * @param completed
	 *            the number of pieces completed thus far
	 */
    public void pieceCompleted(int completed);
}
