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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * A <code>DataFile</code> is a representation of a file that will be
 * downloaded by a torrent.
 */
public class DataFile {

    /**
	 * One of the files being downloaded by the torrent.
	 */
    private RandomAccessFile file;

    /**
	 * An array of integers that indicates the pieces of a torrent file that
	 * this file represents.
	 */
    private int[] pieces;

    /**
	 * The number of bytes that occupy a given piece
	 */
    private int[] pieceLengths;

    /**
	 * The size of the file.
	 */
    private long length;

    /**
	 * Constructs a <code>DataFile</code> to handle the reading and writing of
	 * pieces and blocks.
	 * 
	 * @param aFile
	 *            the file to wrap around
	 * @param length
	 *            the length that the file should be, as specified by the
	 *            metainfo stored within a <i>.torrent</i> file
	 * @throws IOException
	 *             If an I/O error occurs while creating the wrapper around the
	 *             file and specifying its length
	 */
    public  DataFile(File aFile, long length) throws IOException {
        //$NON-NLS-1$
        file = new RandomAccessFile(aFile, "rw");
        if (aFile.length() > length) {
            aFile.delete();
        }
        if (aFile.length() != length) {
            file.seek(length - 1);
            file.write(0);
        }
        this.length = length;
    }

    /**
	 * Sets the piece numbers that this file represents and the first length of
	 * the piece and the length of subsequent pieces excluding the last.
	 * 
	 * @param pieces
	 *            an array of integers that specifies the pieces that this file
	 *            is a part of
	 * @param initialLength
	 *            the length of the first piece
	 * @param length
	 *            the length of pieces after the second, excluding the final
	 *            piece
	 * @throws IllegalArgumentException
	 *             If <code>initialLength</code> is greater than
	 *             <code>length</code> when there are more than two pieces
	 */
    public void setPieces(int[] pieces, int initialLength, int length) throws IllegalArgumentException {
        if (pieces.length > 2 && initialLength > length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The first piece's length cannot be larger than a regular piece's length");
        }
        this.pieces = pieces;
        int numPieces = pieces.length;
        pieceLengths = new int[numPieces];
        pieceLengths[0] = 0;
        // need to set anymore additional values
        if (numPieces == 1) {
            return;
        }
        pieceLengths[1] = initialLength;
        for (int i = 2; i < numPieces; i++) {
            pieceLengths[i] = pieceLengths[i - 1] + length;
        }
    }

    /**
	 * Writes data retrieved from a peer onto this file.
	 * 
	 * @param piece
	 *            the piece's number
	 * @param offset
	 *            the offset for the given piece
	 * @param block
	 *            the data that has been retrieved from the peer of which the
	 *            contents may be written onto this file
	 * @param data
	 *            an integer array with information on how to write the amount
	 *            of information stored within <code>block</code>, the first
	 *            value represents the amount written thus far, the second value
	 *            represents the amount of available data to write, and the
	 *            third value is the index within the piece itself
	 * @return the information to use for the next file that needs writing to,
	 *         if applicable, if the returned value is <code>null</code>, no
	 *         other files needs data written to
	 * @throws IllegalArgumentException
	 *             If <code>piece</code> is not a part of this file, or if the
	 *             provided offset to seek to to write to this file exceeds this
	 *             file's length
	 * @throws IOException
	 *             If an I/O error occurs while attempting to write to the file
	 */
    int[] write(int piece, int offset, byte[] block, int[] data) throws IllegalArgumentException, IOException {
        if (offset == -1) {
            return data;
        }
        int index = indexOf(piece);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        int seek = pieceLengths[index] + offset;
        if (seek >= this.length) {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The seeking position cannot be greater than this file's length");
        }
        synchronized (file) {
            // move to the specified position and write to the file
            file.seek(seek);
            // large this file actually holds
            if (seek + data[1] > this.length) {
                // since there is an excessive amount of data, just take the
                // difference
                int write = (int) (this.length - seek);
                file.write(block, data[0], write);
                data[0] += write;
                data[1] -= write;
                data[2] = write;
                return data;
            } else {
                file.write(block, data[0], data[1]);
                return null;
            }
        }
    }

    /**
	 * Retrieves the data that a particular piece represents within this file. A
	 * piece can potentially be split between multiple files, so the array
	 * returned here may or may not be equal to the piece's length.
	 * 
	 * @param piece
	 *            the number of the interested piece
	 * @return the block of data that is represented by the interested piece, or
	 *         <code>null</code> if this file does not contain the specified
	 *         piece
	 * @throws IOException
	 *             If an I/O error occurs while attempting to read the data from
	 *             this file
	 */
    public byte[] getData(int piece) throws IOException {
        // check to see whether this file contains this piece
        int index = indexOf(piece);
        if (index == -1) {
            return null;
        }
        int dataLength = -1;
        // length of this file and the starting length of the last piece
        if (pieceLengths.length - 1 == index) {
            dataLength = (int) (length - pieceLengths[index]);
        } else {
            // get the length by decrementing the length of the piece after it
            // with the current piece
            dataLength = pieceLengths[index + 1] - pieceLengths[index];
        }
        // create a new byte array to store the data so that it can be returned
        byte[] data = new byte[dataLength];
        synchronized (file) {
            file.seek(pieceLengths[index]);
            file.read(data, 0, dataLength);
        }
        return data;
    }

    /**
	 * Retrieves the length of this file.
	 * 
	 * @return this file's length
	 */
    public long length() {
        return length;
    }

    /**
	 * Checks to see if the piece is part of this file.
	 * 
	 * @param piece
	 *            the piece to check
	 * @return <code>true</code> if this file contains this piece,
	 *         <code>false</code> otherwise
	 */
    public boolean containsPiece(int piece) {
        if (pieces.length == 1) {
            return pieces[0] == piece;
        }
        return pieces[0] <= piece && pieces[pieces.length - 1] >= piece;
    }

    /**
	 * Gets the position of the specified piece within {@link #pieces}.
	 * 
	 * @param piece
	 *            the piece's number
	 * @return the index of the provided piece's number in <code>pieces</code>,
	 *         or <code>-1</code> if it could not be found
	 */
    private int indexOf(int piece) {
        if (!containsPiece(piece)) {
            return -1;
        }
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i] == piece) {
                return i;
            }
        }
        return -1;
    }

    /**
	 * Retrieves the <code>FileChannel</code> associated with the file being
	 * wrapped.
	 * 
	 * @return this file's <code>FileChannel</code>
	 */
    public FileChannel getChannel() {
        return file.getChannel();
    }

    /**
	 * Gets an integer array that stores the numbers of all the pieces that are
	 * a part of this file.
	 * 
	 * @return an array of integers with all of the piece's numbers that this
	 *         file contains
	 */
    public int[] getPieces() {
        return pieces;
    }
}
