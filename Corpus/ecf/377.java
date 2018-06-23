/*******************************************************************************
 * Copyright (c) 2007, 2008 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.eclipse.ecf.protocol.bittorrent.Torrent;
import org.eclipse.ecf.protocol.bittorrent.TorrentFactory;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;

final class BitTorrentConnection extends URLConnection {

    private Torrent torrent;

     BitTorrentConnection(URL url) {
        super(url);
    }

    public void connect() throws IOException {
        if (torrent == null) {
            torrent = TorrentFactory.createTorrent(new TorrentFile(new File(url.getFile())));
            torrent.start();
        }
    }
}
