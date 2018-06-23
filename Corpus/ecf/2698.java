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

import java.util.Vector;

/**
 * A <code>PieceState</code> is a visual representation of the amount of data
 * that has been written and completed for a {@link Piece} at a given time. No
 * bytes of data are actually stored within this or the Blocks that it
 * contains. <code>PieceState</code>s are also used for recording the state
 * that a torrent is in so that resuming can begin quickly when the torrent has
 * restarted.
 */
public class PieceState {

    /**
	 * A <code>Vector</code> that contains all of the {@link Block}s being
	 * represented by this.
	 */
    private final Vector blocks;

    /**
	 * The number of the {@link Piece} that this is representing.
	 */
    private final int number;

    /**
	 * Create and return an array of length <code>pieces</code> containing
	 * initialized <code>PieceState</code> references
	 * 
	 * @param pieces
	 *            the length of the desired array
	 * @return the created array with instantiated <code>PieceState</code>
	 *         objects
	 */
    public static PieceState[] createStates(int pieces) {
        final PieceState[] statuses = new PieceState[pieces];
        for (int i = 0; i < statuses.length; i++) {
            statuses[i] = new PieceState(i);
        }
        return statuses;
    }

    /**
	 * Creates a new <code>PieceState</code> with the number of the
	 * {@link Piece} that it corresponds to.
	 * 
	 * @param number
	 *            the corresponding <code>Piece</code>'s number
	 */
    private  PieceState(int number) {
        this.number = number;
        blocks = new Vector();
    }

    /**
	 * Checks every {@link Block} stored within {@link #blocks} to look for
	 * matching pairs to merge them into one.
	 */
    private void updateState() {
        for (int i = 0; i < blocks.size(); i++) {
            final Block block1 = (Block) blocks.get(i);
            for (int j = i + 1; j < blocks.size(); j++) {
                final Block block2 = (Block) blocks.get(j);
                if (block1.isConnectedToStart(block2)) {
                    block1.prepend(block2);
                    blocks.remove(block2);
                    i--;
                    break;
                } else if (block1.isConnectedToEnd(block2)) {
                    block1.append(block2);
                    blocks.remove(block2);
                    i--;
                    break;
                }
            }
        }
    }

    /**
	 * Adds a block with <code>index</code> as its starting position and a
	 * length of <code>blockLength</code> to this or extend an existing block
	 * by <code>blockLength</code> if this block is connected to said existing
	 * block.
	 * 
	 * @param index
	 *            the starting index of this new block
	 * @param blockLength
	 *            the length of the new block
	 */
    synchronized void addDownloadedBlock(int index, int blockLength) {
        for (int i = 0; i < blocks.size(); i++) {
            final Block b = (Block) blocks.get(i);
            if (b.isConnectedToStart(index, blockLength)) {
                b.prepend(blockLength);
                updateState();
                return;
            } else if (b.isConnectedToEnd(index)) {
                b.append(blockLength);
                updateState();
                return;
            }
        }
        blocks.add(new Block(index, blockLength));
    }

    /**
	 * Retrieves the <code>Vector</code> that holds the {@link Block} objects
	 * contained within this.
	 * 
	 * @return a <code>Vector</code> that contains <code>Block</code>s
	 *         corresponding to this state
	 */
    Vector getBlocks() {
        return blocks;
    }

    /**
	 * Record the piece's state as being completed. All {@link Block}s will be
	 * removed and only one will be added starting at index zero and the length
	 * equal to <code>pieceLength</code>.
	 * 
	 * @param pieceLength
	 *            the length of the piece that this <code>PieceState</code>
	 *            represents
	 */
    void setAsComplete(int pieceLength) {
        synchronized (blocks) {
            // remove all of the original blocks
            blocks.clear();
            // simply add one block with a starting index of 0 with the
            // specified length
            blocks.add(new Block(0, pieceLength));
        }
    }

    /**
	 * Resets all state information corresponding so that it is as if no data
	 * has been downloaded thus far.
	 */
    void reset() {
        blocks.clear();
    }

    /**
	 * Parses a string to set the blocks' starting indices and lengths for this
	 * state. The string should be of the form <code>n:a-b:c-d:e-f</code>
	 * wherein <code>n</code> is this piece's number, and the letters within
	 * <code>a-b</code>, <code>c-d</code>, and <code>e-f</code> would
	 * correspond to a certain block's starting index and its length, in that
	 * order.
	 * 
	 * @param information
	 *            the string to parse
	 */
    public void parse(String information) {
        //$NON-NLS-1$
        String[] split = information.split(":", 2);
        if (split.length != 1) {
            //$NON-NLS-1$
            split = split[1].split(":");
            for (int i = 0; i < split.length; i++) {
                final String[] blockInfo = //$NON-NLS-1$
                split[i].split(//$NON-NLS-1$
                "-");
                final int index = Integer.parseInt(blockInfo[0]);
                final int blockLength = Integer.parseInt(blockInfo[1]) - index;
                addDownloadedBlock(index, blockLength);
            }
        }
    }

    /**
	 * Returns a string representation of this <code>PieceState</code> which
	 * is formatted in the same manner of what would be parsed in the argument
	 * to the {@link #parse(String)} method. A <code>PieceState</code> with a
	 * number of 5, and one single block starting at index 0 with a length of
	 * 16384 would return the string literal <code>5:0-16384</code>.
	 * 
	 * @return the string representation of this <code>PieceState</code> in a
	 *         format equal to what is parsed by <code>parse(String)</code>
	 */
    public String toString() {
        final StringBuffer buffer = new StringBuffer(Integer.toString(number));
        synchronized (buffer) {
            //$NON-NLS-1$
            buffer.append(":");
            for (int i = 0; i < blocks.size(); i++) {
                buffer.append(blocks.get(i));
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                ":");
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
}
