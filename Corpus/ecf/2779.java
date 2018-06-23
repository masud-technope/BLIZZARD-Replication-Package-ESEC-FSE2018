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
import java.util.Arrays;

/**
 * Lease update message. Sent whenever the information expressed in the original
 * lease message has changed. This can be that either a service has been
 * added/modified/removed, or that the topic space has changed.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public final class LeaseUpdateMessage extends RemoteOSGiMessage {

    /**
	 * the update is a topic update.
	 */
    public static final short TOPIC_UPDATE = 0;

    /**
	 * a service has been added.
	 */
    public static final short SERVICE_ADDED = 1;

    /**
	 * a service has been modified.
	 */
    public static final short SERVICE_MODIFIED = 2;

    /**
	 * a service has been removed.
	 */
    public static final short SERVICE_REMOVED = 3;

    /**
	 * the type of the message.
	 */
    private short type;

    /**
	 * the service ID.
	 */
    private String serviceID;

    /**
	 * the payload of the message.
	 */
    private Object[] payload;

    /**
	 * creates a new LeaseUpdateMessage for topic updates.
	 */
    public  LeaseUpdateMessage() {
        super(LEASE_UPDATE);
    }

    /**
	 * creates a new LeaseUpdateMessage from a network packet:
	 * 
	 * <pre>
	 *     0                   1                   2                   3
	 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |       R-OSGi header (function = InvokeMsg = 3)                |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |   update type  |  length of &lt;url&gt;   |  &lt;url&gt; String  \
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |   service information or url or topic array                      \
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
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
     LeaseUpdateMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(LEASE_UPDATE);
        type = input.readShort();
        serviceID = input.readUTF();
        payload = (Object[]) input.readObject();
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
        out.writeShort(type);
        out.writeUTF(serviceID);
        out.writeObject(payload);
    }

    /**
	 * get the type of the message.
	 * 
	 * @return the type.
	 */
    public short getType() {
        return type;
    }

    /**
	 * set the type of the message.
	 * 
	 * @param type
	 *            the type.
	 */
    public void setType(final short type) {
        this.type = type;
    }

    /**
	 * get the service ID.
	 * 
	 * @return the service ID.
	 */
    public String getServiceID() {
        return serviceID;
    }

    /**
	 * set the service ID.
	 * 
	 * @param serviceID
	 *            the service ID.
	 */
    public void setServiceID(final String serviceID) {
        this.serviceID = serviceID;
    }

    /**
	 * get the payload of the message.
	 * 
	 * @return the payload.
	 */
    public Object[] getPayload() {
        return payload;
    }

    /**
	 * set the payload of the message.
	 * 
	 * @param payload
	 *            the payload.
	 */
    public void setPayload(final Object[] payload) {
        this.payload = payload;
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
        buffer.append("[STATE_UPDATE] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", service ");
        //$NON-NLS-1$
        buffer.append("#" + serviceID);
        //$NON-NLS-1$
        buffer.append(", type ");
        buffer.append(type);
        if (type == TOPIC_UPDATE) {
            //$NON-NLS-1$
            buffer.append(", topics added: ");
            buffer.append(payload[0] == null ? "" : //$NON-NLS-1$
            Arrays.asList((String[]) payload[0]).toString());
            //$NON-NLS-1$
            buffer.append(", topics removed: ");
            buffer.append(payload[1] == null ? "" : //$NON-NLS-1$
            Arrays.asList((String[]) payload[1]).toString());
        } else {
            //$NON-NLS-1$
            buffer.append(", service interfaces: ");
            buffer.append(payload[0] == null ? "" : //$NON-NLS-1$
            Arrays.asList((String[]) payload[0]).toString());
            //$NON-NLS-1$
            buffer.append(", properties: ");
            buffer.append(payload[1]);
        }
        return buffer.toString();
    }
}
