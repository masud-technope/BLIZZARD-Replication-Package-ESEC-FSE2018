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
import java.util.Dictionary;

/**
 * <p>
 * RemoteEventMessage transfers RemoteEvents via multicast to all listening
 * peers.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zürich
 * @since 0.2
 */
public final class RemoteEventMessage extends RemoteOSGiMessage {

    /**
	 * the event property contains the sender's uri.
	 */
    //$NON-NLS-1$
    public static final String EVENT_SENDER_URI = "sender.uri";

    /**
	 * 
	 * the event topic. E.g. <code>ch/ethz/iks/SampleEvent</code>.
	 */
    private String topic;

    /**
	 * the properties of the event.
	 */
    private Dictionary properties;

    /**
	 * creates a new RemoteEventMessage from RemoteEvent.
	 * 
	 */
    public  RemoteEventMessage() {
        super(REMOTE_EVENT);
    }

    /**
	 * creates a new RemoteEventMessage from network packet:
	 * 
	 * <pre>
	 *          0                   1                   2                   3
	 *          0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         |       R-OSGi header (function = RemoteEvent = 5)              |
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         | length of &lt;topic&gt;           |   &lt;topic&gt; String                \
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         | Properties                                                    \
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
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
     RemoteEventMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(REMOTE_EVENT);
        topic = input.readUTF();
        properties = (Dictionary) input.readObject();
    }

    /**
	 * get the topic.
	 * 
	 * @return the topic.
	 */
    public String getTopic() {
        return topic;
    }

    /**
	 * set the topic.
	 * 
	 * @param topic
	 */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
	 * get the properties.
	 * 
	 * @return the properties.
	 */
    public Dictionary getProperties() {
        return properties;
    }

    /**
	 * set the properties.
	 * 
	 * @param properties
	 *            the properties.
	 */
    public void setProperties(final Dictionary properties) {
        this.properties = properties;
    }

    /**
	 * convenience method to get the sender URI.
	 * 
	 * @return the sender URI as string.
	 */
    String getSender() {
        return (String) properties.get(EVENT_SENDER_URI);
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
        out.writeUTF(topic);
        out.writeObject(properties);
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
        buffer.append("[REMOTE_EVENT] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append("topic: ");
        buffer.append(topic);
        return buffer.toString();
    }
}
