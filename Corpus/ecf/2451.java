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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The <code>TorrentFactory</code> class allows for the creation of
 * {@link Torrent}s based on a {@link TorrentFile} with the
 * {@link #createTorrent(TorrentFile)} method.
 */
public final class TorrentFactory {

    /**
	 * A <code>Map</code> that stores all of the created <code>Torrent</code>s
	 * so far.
	 */
    private static final Map CREATED_TORRENTS = new HashMap();

    /**
	 * A <code>FileFilter</code> that solely accepts files with a
	 * <code>.torrent</code> extension.
	 */
    private static final FileFilter TORRENTS_FILTER = new TorrentsFilter();

    private static Torrent createTorrent(TorrentFile file, Properties properties) throws IOException {
        if (TorrentConfiguration.statePath == null) {
            //$NON-NLS-1$
            throw new IllegalStateException("The folder to store configuration information has not been set yet");
        } else if (file == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The torrent cannot be null");
        }
        synchronized (CREATED_TORRENTS) {
            Torrent torrent = (Torrent) CREATED_TORRENTS.get(file.getInfoHash());
            if (torrent == null) {
                final File targetFile = file.getTargetFile();
                if (targetFile == null) {
                    throw new IllegalArgumentException("The target file or folder for this torrent has not been set yet");
                }
                torrent = new Torrent(file, properties);
                CREATED_TORRENTS.put(file.getInfoHash(), torrent);
            }
            return torrent;
        }
    }

    /**
	 * Creates a <code>Torrent</code> from the provided
	 * <code>TorrentFile</code>.
	 * 
	 * @param file
	 *            the <code>TorrentFile</code> to use for instantiation
	 * @return a <code>Torrent</code> that wraps around the provided file to
	 *         allow for exchanging of pieces with peers
	 * @throws IllegalArgumentException
	 *             If <code>file</code> is <code>null</code>
	 * @throws IllegalStateException
	 *             If a configuration path has not yet been set via the
	 *             {@link TorrentConfiguration#setConfigurationPath(File)}
	 *             method
	 * @throws IOException
	 *             If an I/O error error occurs while creating or hooking onto
	 *             the files specified by the <code>file</code>
	 */
    public static Torrent createTorrent(TorrentFile file) throws IllegalArgumentException, IllegalStateException, IOException {
        return createTorrent(file, null);
    }

    /**
	 * Gets all of the torrents that has been opened thus far based on where the
	 * state information has been saved as set by
	 * {@link TorrentConfiguration#setConfigurationPath(File)}. {@link Torrent}s
	 * can have its state information removed via the {@link Torrent#remove()}
	 * method, this will prevent it from being returned within this array.
	 * 
	 * @return an array of previously opened <code>Torrent</code>s
	 * @throws IllegalStateException
	 *             If the configuration path to search for torrents in has not
	 *             yet been set
	 * @throws IOException
	 *             If an I/O error occurs while reading the configuration
	 *             information or loading the torrent files
	 */
    public static Torrent[] getSavedTorrents() throws IllegalStateException, IOException {
        if (TorrentConfiguration.statePath == null) {
            //$NON-NLS-1$
            throw new IllegalStateException("The folder to store configuration information has not been set yet");
        }
        final File[] files = TorrentConfiguration.statePath.listFiles(TORRENTS_FILTER);
        final int count = files.length;
        final Torrent[] torrents = new Torrent[count];
        for (int i = 0; i < count; i++) {
            String path = files[i].getAbsolutePath();
            //$NON-NLS-1$ //$NON-NLS-2$
            path = path.substring(0, path.lastIndexOf(".")) + ".properties";
            final Properties properties = new Properties();
            properties.load(new FileInputStream(path));
            final TorrentFile file = new TorrentFile(files[i]);
            //$NON-NLS-1$
            file.setTargetFile(new File(properties.getProperty("target")));
            torrents[i] = createTorrent(file, properties);
        }
        return torrents;
    }

    /**
	 * A private constructor to prevent user instantiation.
	 */
    private  TorrentFactory() {
    // do nothing
    }

    /**
	 * A <code>FileFilter</code> implementation to only accept files with a
	 * <code>.torrent</code> extension.
	 */
    private static class TorrentsFilter implements FileFilter {

        /**
		 * Returns whether this file is actually a torrent file or not. This is
		 * determined by whether the file has a <code>.torrent</code>
		 * extension or not.
		 * @param pathname 
		 * 
		 * @return <code>true</code> if <code>pathname</code>'s name ends
		 *         with <code>.torrent</code>, <code>false</code> otherwise
		 */
        public boolean accept(File pathname) {
            //$NON-NLS-1$
            return pathname.getName().endsWith(".torrent");
        }
    }
}
