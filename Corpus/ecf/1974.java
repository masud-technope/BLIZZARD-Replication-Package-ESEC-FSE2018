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
import java.net.SocketException;
import ch.ethz.iks.r_osgi.RemoteOSGiException;

/**
 * <p>
 * Abstract base class for all Messages.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public abstract class RemoteOSGiMessage {

    /**
	 * type code for lease messages.
	 */
    public static final short LEASE = 1;

    /**
	 * type code for fetch service messages.
	 */
    public static final short REQUEST_SERVICE = 2;

    /**
	 * type code for deliver service messages.
	 */
    public static final short DELIVER_SERVICE = 3;

    /**
	 * type code for deliver bundle messages.
	 * 
	 * @deprecated
	 */
    public static final short DELIVER_BUNDLE = 4;

    /**
	 * type code for invoke method messages.
	 */
    public static final short REMOTE_CALL = 5;

    /**
	 * type code for method result messages.
	 */
    public static final short REMOTE_CALL_RESULT = 6;

    /**
	 * type code for remote event messages.
	 */
    public static final short REMOTE_EVENT = 7;

    /**
	 * type code for time offset messages.
	 */
    public static final short TIME_OFFSET = 8;

    /**
	 * type code for service attribute updates.
	 */
    public static final short LEASE_UPDATE = 9;

    /**
	 * type code for stream request messages.
	 */
    public static final short STREAM_REQUEST = 10;

    /**
	 * type code for stream result messages.
	 */
    public static final short STREAM_RESULT = 11;

    /**
	 * type code for request dependency message.
	 */
    public static final short REQUEST_DEPENDENCIES = 12;

    /**
	 * type code for request bundle message
	 */
    public static final short REQUEST_BUNDLE = 13;

    /**
	 * type code for deliver bundles message
	 */
    public static final short DELIVER_BUNDLES = 14;

    /**
	 * the type code or functionID in SLP notation.
	 */
    private short funcID;

    /**
	 * the transaction id.
	 */
    protected int xid;

    /**
	 * hides the default constructor.
	 */
     RemoteOSGiMessage(final short funcID) {
        this.funcID = funcID;
    }

    /**
	 * get the transaction ID.
	 * 
	 * @return the xid.
	 * @since 0.6
	 */
    public final int getXID() {
        return xid;
    }

    /**
	 * set the xid.
	 * 
	 * @param xid
	 *            set the xid.
	 */
    public void setXID(final int xid) {
        this.xid = xid;
    }

    /**
	 * Get the function ID (type code) of the message.
	 * 
	 * @return the type code.
	 * @since 0.6
	 */
    public final short getFuncID() {
        return funcID;
    }

    /**
	 * reads in a network packet and constructs the corresponding subtype of
	 * RemoteOSGiMessage from it. The header is:
	 * 
	 * <pre>
	 *   0                   1                   2                   3
	 *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |    Version    |         Function-ID           |     XID       |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |    XID cntd.  | 
	 *  +-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * the body is processed by the subtype class.
	 * 
	 * @param input
	 *            the DataInput providing the network packet.
	 * @return the RemoteOSGiMessage.
	 * @throws IOException 
	 * @throws ClassNotFoundException
	 * @throws SocketException
	 *             if something goes wrong.
	 */
    public static RemoteOSGiMessage parse(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        // version, currently unused
        input.readByte();
        final short funcID = input.readByte();
        final int xid = input.readInt();
        RemoteOSGiMessage msg;
        switch(funcID) {
            case LEASE:
                msg = new LeaseMessage(input);
                break;
            case REQUEST_SERVICE:
                msg = new RequestServiceMessage(input);
                break;
            case DELIVER_SERVICE:
                msg = new DeliverServiceMessage(input);
                break;
            case REMOTE_CALL:
                msg = new RemoteCallMessage(input);
                break;
            case REMOTE_CALL_RESULT:
                msg = new RemoteCallResultMessage(input);
                break;
            case REMOTE_EVENT:
                msg = new RemoteEventMessage(input);
                break;
            case TIME_OFFSET:
                msg = new TimeOffsetMessage(input);
                break;
            case LEASE_UPDATE:
                msg = new LeaseUpdateMessage(input);
                break;
            case STREAM_REQUEST:
                msg = new StreamRequestMessage(input);
                break;
            case STREAM_RESULT:
                msg = new StreamResultMessage(input);
                break;
            case REQUEST_DEPENDENCIES:
                msg = new RequestDependenciesMessage(input);
                break;
            case REQUEST_BUNDLE:
                msg = new RequestBundleMessage(input);
                break;
            case DELIVER_BUNDLES:
                msg = new DeliverBundlesMessage(input);
                break;
            default:
                throw new RemoteOSGiException(//$NON-NLS-1$
                "funcID " + funcID + " not supported.");
        }
        msg.funcID = funcID;
        msg.xid = xid;
        return msg;
    }

    /**
	 * write the RemoteOSGiMessage to an output stream.
	 * 
	 * @param out
	 *            the ObjectOutputStream.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public final void send(final ObjectOutputStream out) throws IOException {
        synchronized (out) {
            out.write(1);
            out.write(funcID);
            out.writeInt(xid);
            writeBody(out);
            out.reset();
            out.flush();
        }
    }

    /**
	 * write the body of a RemoteOSGiMessage.
	 * 
	 * @param output
	 *            the output stream.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    protected abstract void writeBody(final ObjectOutputStream output) throws IOException;

    /**
	 * reads the bytes encoded as SLP string.
	 * 
	 * @param input
	 *            the DataInput.
	 * @return the byte array.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    protected static byte[] readBytes(final ObjectInputStream input) throws IOException {
        final int length = input.readInt();
        final byte[] buffer = new byte[length];
        input.readFully(buffer);
        return buffer;
    }

    /**
	 * writes a byte array.
	 * 
	 * @param out
	 *            the output stream.
	 * @param bytes
	 *            the bytes.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    protected static void writeBytes(final ObjectOutputStream out, final byte[] bytes) throws IOException {
        out.writeInt(bytes.length);
        if (bytes.length > 0) {
            out.write(bytes);
        }
    }

    /**
	 * write a string array.
	 * 
	 * @param out
	 *            the output stream.
	 * @param strings
	 *            the string array.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    protected static void writeStringArray(final ObjectOutputStream out, final String[] strings) throws IOException {
        final short length = (short) strings.length;
        out.writeShort(length);
        for (short i = 0; i < length; i++) {
            out.writeUTF(strings[i]);
        }
    }

    /**
	 * read a string array.
	 * 
	 * @param in
	 *            the input stream
	 * @return the read string array.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    protected static String[] readStringArray(final ObjectInputStream in) throws IOException {
        final short length = in.readShort();
        final String[] result = new String[length];
        for (short i = 0; i < length; i++) {
            result[i] = in.readUTF();
        }
        return result;
    }
}
