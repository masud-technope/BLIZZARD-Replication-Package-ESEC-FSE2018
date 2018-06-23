/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Stream request message.
 * 
 * @author Michael Duller, ETH Zurich.
 * 
 */
public final class StreamRequestMessage extends RemoteOSGiMessage {

    /**
	 * operation identifier for simple read operation on stream.
	 */
    public static final byte READ = 0;

    /**
	 * operation identifier for read operation reading more than one byte at
	 * once.
	 */
    public static final byte READ_ARRAY = 1;

    /**
	 * operation identifier for simple write operation on stream.
	 */
    public static final byte WRITE = 2;

    /**
	 * operation identifier for write operation writing more than one byte at
	 * once.
	 */
    public static final byte WRITE_ARRAY = 3;

    /**
	 * stream ID of the target stream.
	 */
    private short streamID;

    /**
	 * operation on the target stream.
	 */
    private byte op;

    /**
	 * length argument (read) or value (write).
	 */
    private int lenOrVal;

    /**
	 * array containing data to write.
	 */
    private byte[] b;

    /**
	 * creates a new StreamRequestMessage.
	 */
    public  StreamRequestMessage() {
        super(STREAM_REQUEST);
    }

    /**
	 * creates a new StreamRequestMessage from network packet:
	 * 
	 * <pre>
	 *       0                   1                   2                   3
	 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |    R-OSGi header (function = StreamRequestMsg = 10)           |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |              short            |       op      |   lenOrVal    |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |                lenOrVal (ctd.)                |       b       \
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * .
	 * 
	 * @param input
	 *            an <code>ObjectInputStream</code> that provides the body of a
	 *            R-OSGi network packet.
	 * @throws IOException
	 *             in case of IO failures.
	 */
     StreamRequestMessage(final ObjectInputStream input) throws IOException {
        super(STREAM_REQUEST);
        streamID = input.readShort();
        op = input.readByte();
        switch(op) {
            case READ:
                lenOrVal = 1;
                b = null;
                break;
            case READ_ARRAY:
            case WRITE:
                lenOrVal = input.readInt();
                b = null;
                break;
            case WRITE_ARRAY:
                lenOrVal = input.readInt();
                b = new byte[lenOrVal];
                int rem = lenOrVal;
                int read;
                while ((rem > 0) && ((read = input.read(b, lenOrVal - rem, rem)) > 0)) {
                    rem = rem - read;
                }
                if (rem > 0) {
                    throw new //$NON-NLS-1$
                    IOException(//$NON-NLS-1$
                    "Premature end of input stream.");
                }
                break;
            default:
                throw new IllegalArgumentException(//$NON-NLS-1$
                "op code not within valid range: " + //$NON-NLS-1$
                op);
        }
    }

    /**
	 * write the body of the message to a stream.
	 * 
	 * @param out
	 *            the ObjectOutputStream.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        out.writeShort(streamID);
        out.writeByte(op);
        if (op != READ) {
            out.writeInt(lenOrVal);
            if (op == WRITE_ARRAY) {
                out.write(b);
            }
        }
    }

    /**
	 * get the ID of the stream.
	 * 
	 * @return the ID of the stream.
	 */
    public short getStreamID() {
        return streamID;
    }

    /**
	 * set the ID of the stream.
	 * 
	 * @param streamID
	 *            the ID of the stream.
	 */
    public void setStreamID(final short streamID) {
        this.streamID = streamID;
    }

    /**
	 * get the operation code.
	 * 
	 * @return the operation code.
	 */
    public byte getOp() {
        return op;
    }

    /**
	 * set the operation code.
	 * 
	 * @param op
	 *            the operation code.
	 */
    public void setOp(final byte op) {
        this.op = op;
    }

    /**
	 * get the length (read op) or value (write op) field.
	 * 
	 * @return the length or value.
	 */
    public int getLenOrVal() {
        return lenOrVal;
    }

    /**
	 * set the length (read op) or value (write op) field.
	 * 
	 * @param lenOrVal
	 *            the length or value.
	 */
    public void setLenOrVal(final int lenOrVal) {
        this.lenOrVal = lenOrVal;
    }

    /**
	 * get the data array.
	 * 
	 * @return the data array.
	 */
    public byte[] getData() {
        return b;
    }

    /**
	 * set the data array.
	 * 
	 * @param b
	 *            the data array to store.
	 */
    public void setData(final byte[] b) {
        this.b = b;
    }

    /**
	 * String representation for debug outputs.
	 * 
	 * @return a string representation.
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("[STREAM_REQUEST] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", streamID: ");
        buffer.append(streamID);
        //$NON-NLS-1$
        buffer.append(", op: ");
        buffer.append(op);
        //$NON-NLS-1$
        buffer.append(", len: ");
        buffer.append(lenOrVal);
        return buffer.toString();
    }
}
