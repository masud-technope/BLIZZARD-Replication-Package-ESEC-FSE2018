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
import java.util.Calendar;
import java.util.Date;
import org.eclipse.ecf.protocol.bittorrent.internal.net.TorrentManager;

/**
 * A class used to define properties and configurations such as specifying where
 * a torrent's state information should be stored and how debugging messages
 * should be displayed.
 */
public final class TorrentConfiguration {

    /**
	 * A boolean flag to specify whether debugging output should be printed or
	 * not.
	 */
    public static boolean DEBUG = false;

    /**
	 * The folder in which all state and configuration information should be
	 * saved.
	 */
    static File statePath = null;

    /**
	 * The shared <code>Calendar</code> instance that will be used to retrieve
	 * the current time for debugging output.
	 * 
	 * @see #DEBUG
	 * @see #debug(String)
	 */
    private static final Calendar calendar = Calendar.getInstance();

    /**
	 * The implementation of {@link IDebugListener} that's currently monitoring
	 * debug messages.
	 */
    private static IDebugListener debugListener;

    /**
	 * This method is called by classes that wishes to log a message to the
	 * {@link IDebugListener} that was set with
	 * {@link #setDebugListener(org.eclipse.ecf.protocol.bittorrent.TorrentConfiguration.IDebugListener)}.
	 * 
	 * @param message
	 *            the message to print out
	 */
    public static void debug(String message) {
        if (DEBUG && debugListener != null) {
            Date date = new Date(System.currentTimeMillis());
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String hourString = (//$NON-NLS-1$
            hour < 10 ? //$NON-NLS-1$
            "0" + hour : Integer.toString(hour));
            String minuteString = (//$NON-NLS-1$
            minute < 10 ? //$NON-NLS-1$
            "0" + minute : Integer.toString(minute));
            String secondString = //$NON-NLS-1$
            second < 10 ? //$NON-NLS-1$
            "0" + second : Integer.toString(second);
            debugListener.print(//$NON-NLS-1$ //$NON-NLS-2$
            hourString + ":" + minuteString + ":" + secondString + //$NON-NLS-1$
            " " + //$NON-NLS-1$
            message);
        }
    }

    /**
	 * Sets the listener that will be notified of debugging messages.
	 * 
	 * @param listener
	 *            the listener to use, or <code>null</code> if the previously
	 *            set listener should be discarded
	 */
    public static void setDebugListener(IDebugListener listener) {
        debugListener = listener;
    }

    /**
	 * Sets the directory to use to save configuration and status information
	 * when starting up torrents. Note that the configuration path cannot be
	 * modified again with a second invocation of this method.
	 * 
	 * @param directory
	 *            the directory to save the states into
	 * @throws IllegalArgumentException
	 *             If <code>directory</code> is <code>null</code>, a file
	 *             and not a directory, cannot be written, or could not be
	 *             created
	 */
    public static void setConfigurationPath(File directory) {
        if (statePath != null) {
            return;
        } else if (directory == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The provided directory cannot be null");
        } else if (directory.isFile()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + //$NON-NLS-1$
            " is a file");
        } else if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalArgumentException(//$NON-NLS-1$
            "The directory " + directory.getAbsolutePath() + " could not be made");
        } else if (!directory.canWrite()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " cannot be written to");
        }
        statePath = directory;
        TorrentManager.setStatePath(statePath);
    }

    /**
	 * Removes saved information regarding a torrent's progress and status based
	 * on its hexadecimal hash value.
	 * 
	 * @param hexHash
	 *            the hexadecimal hash value of the torrent
	 * @throws IllegalArgumentException
	 *             If <code>hexHash</code> is <code>null</code>
	 */
    public static void remove(String hexHash) {
        if (hexHash == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The hash cannot be null");
        }
        Torrent torrent = TorrentServer.remove(hexHash);
        if (torrent != null) {
            torrent.remove();
        } else {
            File[] files = statePath.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().startsWith(hexHash)) {
                    files[i].delete();
                }
            }
        }
    }

    /**
	 * A private constructor to prevent user instantiation.
	 */
    private  TorrentConfiguration() {
    // do nothing
    }

    /**
	 * An interface to setup an outlet for debugging messages. Instances of this
	 * class should be set with the
	 * {@link TorrentConfiguration#setDebugListener(TorrentConfiguration.IDebugListener)}
	 * method.
	 */
    public interface IDebugListener {

        /**
		 * This method is called when a debugging message has been relayed.
		 * Messages will not be sent unless {@link TorrentConfiguration#DEBUG}
		 * is set to <code>true</code>.
		 * 
		 * @param message
		 *            the debugging message
		 */
        public void print(String message);
    }
}
