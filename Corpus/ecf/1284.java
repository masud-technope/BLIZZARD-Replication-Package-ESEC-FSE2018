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
package org.eclipse.ecf.protocol.bittorrent.internal.torrent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * A <code>Piece</code> is a section of data specified by the torrent
 * metainfo. Each piece has a corresponding SHA-1 hash which is used to verify
 * the integrity of the data that has been received from peers.
 */
public class Piece {

    /**
	 * The amount of data to request when asking peers for data. The value is
	 * set at 16384, which is equal to 2^14.
	 */
    private static final int BLOCK_REQUEST_SIZE = 16384;

    /**
	 * An <code>ArrayList</code> that contains {@link DataFile}(s) that this
	 * piece corresponds to.
	 */
    private final ArrayList files;

    /**
	 * An <code>ArrayList</code> that contains <code>Integer</code>(s)
	 * which corresponsd to the length of data of each {@link DataFile} within
	 * {@link #files} that this piece represents.
	 */
    private final ArrayList fileLengths;

    /**
	 * This piece's number.
	 */
    private final int number;

    /**
	 * This piece's {@link PieceState} which stores information pertaining to
	 * the amount of data that has been written for each block.
	 */
    private PieceState state;

    /**
	 * An array that corresponds to the number of bytes that has been written
	 * for a specific block.
	 */
    private int[] writtenBlocks;

    /**
	 * An array that indicates whether a specific block has been requested from
	 * a peer or not.
	 */
    private boolean[] requested;

    /**
	 * An array that indicates whether a specific block has been completed or
	 * not.
	 */
    private boolean[] completed;

    /**
	 * The length of this piece.
	 */
    private int length = -1;

    /**
	 * The number of {@link Block}s that this piece contains.
	 */
    private int blocks;

    /**
	 * Indicates whether this piece is the final piece specified by the
	 * torrent's metainfo.
	 */
    private boolean isLastPiece;

    /**
	 * Creates a new <code>Piece</code> with the provided {@link PieceState}
	 * to store information in and the specified number to represent.
	 * 
	 * @param state
	 *            the <code>PieceStaet</code> to use to store information
	 * @param number
	 *            the number of this piece
	 * @throws IllegalArgumentException
	 *             If <code>number < 0 </code> returns <code>true</code>
	 */
    public  Piece(PieceState state, int number) throws IllegalArgumentException {
        if (number < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("A piece number cannot be negative");
        }
        this.state = state;
        this.number = number;
        files = new ArrayList();
        fileLengths = new ArrayList();
    }

    /**
	 * Sets the length of this piece. The length of a piece is the amount of
	 * bytes that it represents.
	 * 
	 * @param length
	 *            the length of this piece
	 * @throws IllegalArgumentException
	 *             If the specified length is negative
	 */
    public void setLength(int length) throws IllegalArgumentException {
        if (length < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("A piece's length cannot be negative");
        }
        this.length = length;
        isLastPiece = length % BLOCK_REQUEST_SIZE != 0;
        blocks = (length / BLOCK_REQUEST_SIZE) + (isLastPiece ? 1 : 0);
        requested = new boolean[blocks];
        completed = new boolean[blocks];
        writtenBlocks = new int[blocks];
    }

    /**
	 * Retrieves this piece's length.
	 * 
	 * @return the length of this piece
	 */
    public int getLength() {
        return length;
    }

    /**
	 * Removes all information regarding the data that has been written for this
	 * piece. This changes the state of this piece such that it is as if no data
	 * has been written and no blocks are currently being requested.
	 */
    public void reset() {
        Arrays.fill(requested, false);
        Arrays.fill(completed, false);
        Arrays.fill(writtenBlocks, 0);
        state.reset();
    }

    /**
	 * Retrieves the index offset position within a file at position
	 * <code>pos</code> within {@link #files} requires writing to.
	 * 
	 * @param pos
	 *            the position of the {@link DataFile} within <code>files</code>
	 * @param index
	 *            the index within this piece that data is available for writing
	 *            to
	 * @return the additional offset index that this piece corresponds to within
	 *         the <code>DataFile</code> or <code>-1</code> if the offset
	 *         does not pertain to that file
	 */
    private int getFileOffset(int pos, int index) {
        if (files.size() == 1) {
            return index;
        }
        int count = 0;
        int offset = 0;
        do {
            offset += ((Integer) fileLengths.get(count++)).intValue();
        } while (count <= pos);
        if (index > offset) {
            return -1;
        } else {
            pos = offset - ((Integer) fileLengths.get(count - 1)).intValue();
            if (pos <= index && index < offset) {
                return index - pos;
            } else {
                return -1;
            }
        }
    }

    /**
	 * Writes the bytes received from peers onto the corresponding files on the
	 * local file system.
	 * 
	 * @param pieceIndex
	 *            the index within this piece that the block of data received
	 *            starts at
	 * @param block
	 *            an array with a subsection that holds data received from the
	 *            peer
	 * @param offset
	 *            the starting offset index within <code>block</code> that the
	 *            data for the files begins at
	 * @param length
	 *            the length of bytes that has been received from the peer
	 * @return <code>true</code> if the writing has been performed,
	 *         <code>false</code> otherwise
	 * @throws IOException
	 *             If an I/O error occurs while attempting to write the data to
	 *             the files
	 */
    public boolean write(int pieceIndex, byte[] block, int offset, int length) throws IOException {
        int blockIndex = pieceIndex / BLOCK_REQUEST_SIZE;
        if (completed[blockIndex]) {
            return false;
        }
        state.addDownloadedBlock(pieceIndex, length);
        int[] ret = null;
        // check to see if we're writing to this piece's last block
        if (blockIndex == blocks - 1) {
            int limit = isLastPiece ? this.length % BLOCK_REQUEST_SIZE : BLOCK_REQUEST_SIZE;
            ret = new int[] { offset, length, 0 };
            for (int i = 0; i < files.size(); i++) {
                ret = ((DataFile) files.get(i)).write(number, getFileOffset(i, pieceIndex), block, ret);
                if (ret == null) {
                    break;
                }
                pieceIndex += ret[2];
            }
            writtenBlocks[blockIndex] += length;
            if (writtenBlocks[blockIndex] == limit) {
                completed[blockIndex] = true;
            }
        } else {
            ret = new int[] { offset, length, 0 };
            for (int i = 0; i < files.size(); i++) {
                ret = ((DataFile) files.get(i)).write(number, getFileOffset(i, pieceIndex), block, ret);
                if (ret == null) {
                    break;
                }
                pieceIndex += ret[2];
            }
            writtenBlocks[blockIndex] += length;
            // if the entire block has been written, note this fact
            if (writtenBlocks[blockIndex] == BLOCK_REQUEST_SIZE) {
                completed[blockIndex] = true;
            }
        }
        return true;
    }

    /**
	 * Retrieves the amount of bytes that has been written thus far for this
	 * piece.
	 * 
	 * @return the amount of data written so far
	 */
    public int getWritten() {
        int total = 0;
        for (int i = 0; i < writtenBlocks.length; i++) {
            total += writtenBlocks[i];
        }
        return total;
    }

    /**
	 * Adds a {@link DataFile} as being a part of this piece with the specified
	 * length as the length of data within the file that this piece holds.
	 * 
	 * @param file
	 *            the <code>DataFile</code> to be added as being a part of
	 *            this piece
	 * @param length
	 *            a length of bytes within <code>file</code> that is being
	 *            contained by this piece
	 * @throws IllegalArgumentException
	 *             If <code>(file == null || length < 0)</code> returns
	 *             <code>true</code>
	 */
    public void addFile(DataFile file, int length) throws IllegalArgumentException {
        if (file == null) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The file cannot be null");
        } else if (length < 0) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The length cannot be a negative number");
        }
        files.add(file);
        fileLengths.add(new Integer(length));
    }

    /**
	 * Returns whether the amount of data that this piece has written is equal
	 * to its length. Note that this method does not perform a hash check to see
	 * whether the data is corrupt or not.
	 * 
	 * @return <code>true</code> if this piece has written data equal to its
	 *         length, <code>false</code> otherwise
	 */
    public boolean isComplete() {
        synchronized (completed) {
            for (int i = 0; i < blocks; i++) {
                if (!completed[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
	 * Sets this piece's state as being completed. All blocks are set as being
	 * completed and all the corresponding data as being written.
	 */
    public void setAsCompleted() {
        Arrays.fill(requested, true);
        Arrays.fill(completed, true);
        if (isLastPiece) {
            for (int i = 0; i < blocks - 1; i++) {
                writtenBlocks[i] = BLOCK_REQUEST_SIZE;
            }
            writtenBlocks[blocks - 1] = length % BLOCK_REQUEST_SIZE;
        } else {
            for (int i = 0; i < blocks; i++) {
                writtenBlocks[i] = BLOCK_REQUEST_SIZE;
            }
        }
        state.setAsComplete(length);
    }

    /**
	 * Sets the state of this piece to a new state. This may set blocks within
	 * this piece as having been requested and completed.
	 * 
	 * @param state
	 *            the new state to set to
	 */
    public void setState(PieceState state) {
        this.state = state;
        Vector blocks = state.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = (Block) blocks.get(i);
            int index = block.getIndex() / BLOCK_REQUEST_SIZE;
            if (isLastPiece && block.getIndex() % BLOCK_REQUEST_SIZE != 0) {
                index++;
            }
            int length = block.getBlockLength();
            int remainder = length % BLOCK_REQUEST_SIZE;
            if (remainder == 0) {
                for (int j = 0; j < length; j += BLOCK_REQUEST_SIZE) {
                    writtenBlocks[index] = BLOCK_REQUEST_SIZE;
                    requested[index] = true;
                    completed[index] = true;
                    index++;
                }
            } else {
                for (int j = 0; j < length - BLOCK_REQUEST_SIZE; j += BLOCK_REQUEST_SIZE) {
                    writtenBlocks[index] = BLOCK_REQUEST_SIZE;
                    requested[index] = true;
                    completed[index] = true;
                    index++;
                }
                writtenBlocks[index] = remainder;
            }
        }
    }

    /**
	 * Retrieves this piece's number as specified by the torrent metadata file.
	 * 
	 * @return this piece's number
	 */
    public int getNumber() {
        return number;
    }

    /**
	 * Returns an array of size three with information about the next block of
	 * data that should be requested from a peer to complete this piece.
	 * 
	 * @return an array of size three with the first index containing this
	 *         piece's identification number, the second index with the starting
	 *         index of this piece in which the data received should begin from,
	 *         and the third index with the length of data that should be sent,
	 *         if this array is <code>null</code>, this block has been
	 *         completed and does not need to have anything requested
	 * @throws IllegalStateException
	 *             If the length of this piece has not been set yet with
	 *             {@link #setLength(int)}
	 */
    public synchronized int[] getRequestInformation() throws IllegalStateException {
        if (length == -1) {
            //$NON-NLS-1$
            throw new IllegalStateException("The length has not been set yet for this piece");
        } else if (isComplete()) {
            return null;
        }
        boolean allRequested = true;
        for (int i = 0; i < blocks; i++) {
            if (!completed[i] && !requested[i]) {
                allRequested = false;
                break;
            }
        }
        int random = -1;
        if (!allRequested) {
            random = (int) (Math.random() * blocks);
            while (requested[random]) {
                if (isComplete()) {
                    return null;
                }
                random = (int) (Math.random() * blocks);
            }
            requested[random] = true;
        } else {
            random = (int) (Math.random() * blocks);
            while (completed[random]) {
                if (isComplete()) {
                    return null;
                }
                random = (int) (Math.random() * blocks);
            }
        }
        return new int[] { number, writtenBlocks[random] + random * BLOCK_REQUEST_SIZE, (random == blocks - 1) ? (isLastPiece ? length % BLOCK_REQUEST_SIZE : BLOCK_REQUEST_SIZE) : BLOCK_REQUEST_SIZE };
    }
}
