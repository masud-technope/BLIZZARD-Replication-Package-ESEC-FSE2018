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
package org.eclipse.ecf.internal.provider.bittorrent;

import java.io.IOException;
import java.util.Date;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IFileTransferPausable;
import org.eclipse.ecf.filetransfer.IFileTransferRateControl;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.protocol.bittorrent.IPieceProgressListener;
import org.eclipse.ecf.protocol.bittorrent.ITorrentStateListener;
import org.eclipse.ecf.protocol.bittorrent.Torrent;

final class TorrentFileTransfer implements IFileTransferPausable, IFileTransferRateControl, IIncomingFileTransfer, IOutgoingFileTransfer {

    private final ID id;

    private final IFileTransferListener listener;

    private final Torrent torrent;

    private final IPieceProgressListener pieceListener;

    private final ITorrentStateListener stateListener;

    private final double total;

    private Exception exception;

    private boolean paused = false;

     TorrentFileTransfer(ID id, IFileTransferListener listener, Torrent torrent) {
        this.id = id;
        this.listener = listener;
        this.torrent = torrent;
        total = torrent.getTorrentFile().getTotalLength();
        pieceListener = new IPieceProgressListener() {

            public void blockDownloaded(int piece, int index, int blockLength) {
                TorrentFileTransfer.this.listener.handleTransferEvent(new IIncomingFileTransferReceiveDataEvent() {

                    public IIncomingFileTransfer getSource() {
                        return TorrentFileTransfer.this;
                    }
                });
            }
        };
        stateListener = new ITorrentStateListener() {

            public void stateChanged(int state) {
                if (state == ITorrentStateListener.FINISHED) {
                    notifyCompletion(null);
                }
            }
        };
        torrent.addPieceProgressListener(pieceListener);
        torrent.addTorrentStateListener(stateListener);
        try {
            torrent.start();
        } catch (final IOException e) {
            notifyCompletion(e);
        }
    }

    private void notifyCompletion(Exception exception) {
        this.exception = exception;
        torrent.removePieceProgressListener(pieceListener);
        torrent.removeTorrentStateListener(stateListener);
        listener.handleTransferEvent(new IIncomingFileTransferReceiveDoneEvent() {

            public Exception getException() {
                return TorrentFileTransfer.this.exception;
            }

            public IIncomingFileTransfer getSource() {
                return TorrentFileTransfer.this;
            }
        });
    }

    public void cancel() {
        try {
            torrent.stop();
            notifyCompletion(new UserCancelledException());
        } catch (final IOException e) {
            notifyCompletion(e);
        }
    }

    public long getBytesSent() {
        return torrent.getUploaded();
    }

    public Exception getException() {
        return exception;
    }

    public double getPercentComplete() {
        return (total - torrent.getRemaining()) / total;
    }

    public Object getAdapter(Class adapter) {
        return adapter.isInstance(this) ? this : null;
    }

    public ID getID() {
        return id;
    }

    public long getBytesReceived() {
        return torrent.getDownloaded();
    }

    public boolean isDone() {
        return exception != null || getPercentComplete() == 1;
    }

    public boolean pause() {
        if (paused || isDone()) {
            return false;
        }
        try {
            torrent.stop();
            paused = true;
            return true;
        } catch (final IOException e) {
            notifyCompletion(e);
            return false;
        }
    }

    public boolean resume() {
        if (!paused || isDone()) {
            return false;
        }
        try {
            torrent.start();
            paused = false;
            return true;
        } catch (final IOException e) {
            notifyCompletion(e);
            return false;
        }
    }

    public void setMaxDownloadSpeed(long maxDownloadSpeed) {
        torrent.setMaxDownloadSpeed(maxDownloadSpeed);
    }

    public void setMaxUploadSpeed(long maxUploadSpeed) {
        torrent.setMaxUploadSpeed(maxUploadSpeed);
    }

    public boolean isPaused() {
        return paused;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getListener()
	 */
    public IFileTransferListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getFileRangeSpecification()
	 */
    public IFileRangeSpecification getFileRangeSpecification() {
        return null;
    }

    public long getFileLength() {
        return torrent.getTorrentFile().getTotalLength();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getRemoteFileName()
	 */
    public String getRemoteFileName() {
        if (torrent == null)
            return null;
        if (!torrent.getTorrentFile().isMultiFile()) {
            return torrent.getTorrentFile().getFilenames()[0];
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.IIncomingFileTransfer#getRemoteLastModified()
	 */
    public Date getRemoteLastModified() {
        // Not supported
        return null;
    }
}
