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
 * Stream result message.
 * 
 * @author Michael Duller, ETH Zurich.
 */
public final class StreamResultMessage extends RemoteOSGiMessage {

    /**
	 * result indicates array.
	 */
    public static final short RESULT_ARRAY = -2;

    /**
	 * result indicates exception.
	 */
    public static final short RESULT_EXCEPTION = -3;

    /**
	 * result indicates write operation completed successfully.
	 */
    public static final short RESULT_WRITE_OK = -4;

    /**
	 * result of the operation.
	 */
    private short result;

    /**
	 * array containing read data.
	 */
    private byte[] b;

    /**
	 * length of b.
	 */
    private int len;

    /**
	 * the exception.
	 */
    private IOException exception;

    /**
	 * creates a new StreamResultMessage.
	 */
    public  StreamResultMessage() {
        super(STREAM_RESULT);
    }

    /**
	 * creates a new StreamResultMessage from network packet:
	 * 
	 * <pre>
	 *       0                   1                   2                   3
	 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |    R-OSGi header (function = StreamResultMsg = 11)            |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |            result             | result == -2: len, -3: excep. \
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      | result == -2 &amp;&amp; len &gt; 0: b                                    |
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
	 * @throws ClassNotFoundException
	 */
     StreamResultMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(STREAM_RESULT);
        result = input.readShort();
        switch(result) {
            case RESULT_ARRAY:
                len = input.readInt();
                if (len > 0) {
                    b = new byte[len];
                    int rem = len;
                    int read;
                    while ((rem > 0) && ((read = input.read(b, len - rem, rem)) > 0)) {
                        rem = rem - read;
                    }
                    if (rem > 0) {
                        throw new IOException("Premature end of input stream.");
                    }
                }
                break;
            case RESULT_EXCEPTION:
                exception = (IOException) input.readObject();
                break;
            case RESULT_WRITE_OK:
                break;
            default:
                if (// -1 indicates EOF -> valid
                (result < -1) || (result > 255)) {
                    throw new IllegalArgumentException(//$NON-NLS-1$
                    "result not within valid range: " + //$NON-NLS-1$
                    result);
                }
                break;
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
        out.writeShort(result);
        if (result == RESULT_ARRAY) {
            out.writeInt(len);
            if (len > 0) {
                out.write(b, 0, len);
            }
        } else if (result == RESULT_EXCEPTION) {
            out.writeObject(exception);
        }
    }

    /**
	 * did the stream operation cause an exception ?
	 * 
	 * @return <code>true</code>, if an exception has been thrown on the remote
	 *         side. In this case, the exception can be retrieved through the
	 *         <code>getException</code> method.
	 */
    public boolean causedException() {
        return (result == RESULT_EXCEPTION);
    }

    /**
	 * get the result value.
	 * 
	 * @return the return value of the invoked operation.
	 */
    public short getResult() {
        return result;
    }

    /**
	 * set the result value.
	 * 
	 * @param result
	 *            the result.
	 */
    public void setResult(final short result) {
        this.result = result;
    }

    /**
	 * get the data array.
	 * 
	 * @return the array containing the read data.
	 */
    public byte[] getData() {
        return b;
    }

    /**
	 * set the data array.
	 * 
	 * @param b
	 *            the array containing the read data.
	 */
    public void setData(final byte[] b) {
        this.b = b;
    }

    /**
	 * get the length.
	 * 
	 * @return the number of bytes read.
	 */
    public int getLen() {
        return len;
    }

    /**
	 * set the length.
	 * 
	 * @param len
	 *            the number of bytes read.
	 */
    public void setLen(final int len) {
        this.len = len;
    }

    /**
	 * get the exception.
	 * 
	 * @return the exception or <code>null</code> if none was thrown.
	 */
    public IOException getException() {
        return exception;
    }

    /**
	 * set the exception.
	 * 
	 * @param exception
	 *            the exception that was thrown.
	 */
    public void setException(final IOException exception) {
        this.exception = exception;
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
        buffer.append("[STREAM_RESULT] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", result: ");
        buffer.append(result);
        if (causedException()) {
            //$NON-NLS-1$
            buffer.append(", exception: ");
            buffer.append(exception.getMessage());
        }
        return buffer.toString();
    }
}
