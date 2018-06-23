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
import java.util.Dictionary;

/**
 * Lease message. Is exchanged when a channel is established. Leases are the
 * implementations of the statements of supply and demand.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.6
 */
public final class LeaseMessage extends RemoteOSGiMessage {

    /**
	 * the service IDs.
	 */
    private String[] serviceIDs;

    /**
	 * the services that the peer offers.
	 */
    private String[][] serviceInterfaces;

    /**
	 * the service properties.
	 */
    private Dictionary[] serviceProperties;

    /**
	 * the event topics that the peer is interested in.
	 */
    private String[] topics;

    /**
	 * create a new lease message.
	 * 
	 */
    public  LeaseMessage() {
        super(LEASE);
    }

    /**
	 * creates a new LeaseMessage from network packet.
	 * 
	 * <pre>
	 *   0                   1                   2                   3
	 *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |       R-OSGi header (function = Lease = 1)                    |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |  Array of service info (Fragment#, Interface[], properties    \
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |  Array of topic strings                                       \
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * @param input
	 *            an <code>ObjectInputStream</code> that provides the body of a
	 *            R-OSGi network packet.
	 * @throws IOException
	 *             if something goes wrong.
	 * @throws ClassNotFoundException
	 */
     LeaseMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(LEASE);
        final int serviceCount = input.readShort();
        serviceIDs = new String[serviceCount];
        serviceInterfaces = new String[serviceCount][];
        serviceProperties = new Dictionary[serviceCount];
        for (short i = 0; i < serviceCount; i++) {
            serviceIDs[i] = input.readUTF();
            serviceInterfaces[i] = readStringArray(input);
            serviceProperties[i] = (Dictionary) input.readObject();
        }
        topics = readStringArray(input);
    }

    /**
	 * get the service interfaces.
	 * 
	 * @return the service interfaces.
	 */
    public String[][] getServiceInterfaces() {
        return serviceInterfaces;
    }

    /**
	 * set the service interfaces.
	 * 
	 * @param serviceInterfaces
	 *            the service interfaces.
	 */
    public void setServiceInterfaces(final String[][] serviceInterfaces) {
        this.serviceInterfaces = serviceInterfaces;
    }

    /**
	 * get the service IDs.
	 * 
	 * @return the service IDs.
	 */
    public String[] getServiceIDs() {
        return serviceIDs;
    }

    /**
	 * set the service IDs.
	 * 
	 * @param serviceIDs
	 *            the service IDs.
	 */
    public void setServiceIDs(final String[] serviceIDs) {
        this.serviceIDs = serviceIDs;
    }

    /**
	 * get the service properties.
	 * 
	 * @return the service properties.
	 */
    public Dictionary[] getServiceProperties() {
        return serviceProperties;
    }

    /**
	 * set the service properties.
	 * 
	 * @param serviceProperties
	 *            the service properties.
	 */
    public void setServiceProperties(final Dictionary[] serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    /**
	 * get the topics.
	 * 
	 * @return the topics.
	 */
    public String[] getTopics() {
        return topics;
    }

    /**
	 * set the topics.
	 * 
	 * @param topics
	 *            the topics.
	 */
    public void setTopics(final String[] topics) {
        this.topics = topics;
    }

    /**
	 * write the bytes of the message.
	 * 
	 * @param out
	 *            the output stream
	 * @throws IOException
	 *             in case of IO errors.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        final int slen = serviceInterfaces.length;
        out.writeShort(slen);
        for (short i = 0; i < slen; i++) {
            out.writeUTF(serviceIDs[i]);
            writeStringArray(out, serviceInterfaces[i]);
            out.writeObject(serviceProperties[i]);
        }
        writeStringArray(out, topics);
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
        buffer.append("[LEASE] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", services: ");
        for (int i = 0; i < serviceInterfaces.length; i++) {
            buffer.append(Arrays.asList(serviceInterfaces[i]));
            //$NON-NLS-1$
            buffer.append("-");
            buffer.append(serviceIDs[i]);
            if (i < serviceInterfaces.length) {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        //$NON-NLS-1$
        buffer.append(", topics: ");
        buffer.append(Arrays.asList(topics));
        return buffer.toString();
    }
}
