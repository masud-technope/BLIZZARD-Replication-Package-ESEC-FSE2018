/*******************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.osgi.service.url.AbstractURLStreamHandlerService;

public final class BitTorrentURLStreamHandlerService extends AbstractURLStreamHandlerService {

    public URLConnection openConnection(URL u) throws IOException {
        return new BitTorrentConnection(u);
    }
}
