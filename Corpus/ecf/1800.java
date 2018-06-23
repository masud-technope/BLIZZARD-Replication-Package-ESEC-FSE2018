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
 * <p>
 * FetchServiceMessage is used to signal the service provider that a remote peer
 * wants to get a service that has been registered for remoting.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class RequestServiceMessage extends RemoteOSGiMessage {

    /**
	 * the service ID.
	 */
    private String serviceID;

    /**
	 * creates a new FetchServiceMessage from <code>ServiceURL</code>.
	 * 
	 */
    public  RequestServiceMessage() {
        super(REQUEST_SERVICE);
    }

    /**
	 * creates a new FetchServiceMessage from network packet.
	 * 
	 * <pre>
	 *     0                   1                   2                   3
	 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |       R-OSGi header (function = Fetch = 1)                    |
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    |   length of &lt;serviceID&gt;     |     &lt;serviceID&gt; String      \
	 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * @param input
	 *            an <code>ObjectInputStream</code> that provides the body of a
	 *            R-OSGi network packet.
	 * @throws IOException
	 *             if something goes wrong.
	 */
     RequestServiceMessage(final ObjectInputStream input) throws IOException {
        super(REQUEST_SERVICE);
        serviceID = input.readUTF();
    }

    /**
	 * gets the body of the message as raw bytes.
	 * 
	 * @param out
	 *            the <code>ObjectOutputStream</code>
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        out.writeUTF(serviceID);
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
	 * get the service ID.
	 * 
	 * @param serviceID
	 *            the service ID.
	 */
    public void setServiceID(final String serviceID) {
        this.serviceID = serviceID;
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
        buffer.append("[REQUEST_SERVICE]");
        //$NON-NLS-1$
        buffer.append("- XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", serviceID: ");
        buffer.append(serviceID);
        return buffer.toString();
    }
}
