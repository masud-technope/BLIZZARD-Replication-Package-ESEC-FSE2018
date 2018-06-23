/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Institute for Pervasive Computing, ETH Zurich.
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

public class DeliverBundlesMessage extends RemoteOSGiMessage {

    /**
	 * the bytes of the bundles
	 */
    private byte[][] bytes;

    public  DeliverBundlesMessage() {
        super(RemoteOSGiMessage.DELIVER_BUNDLES);
    }

    /**
	 * create a new message from the wire.
	 * 
	 * @param input
	 *            the input stream.
	 * @throws IOException
	 *             in case of IO problems.
	 */
    public  DeliverBundlesMessage(final ObjectInputStream input) throws IOException {
        super(RemoteOSGiMessage.DELIVER_BUNDLES);
        final int bundleCount = input.readInt();
        bytes = new byte[bundleCount][];
        for (int i = 0; i < bundleCount; i++) {
            bytes[i] = readBytes(input);
        }
    }

    /**
	 * write the body of this message to the wire.
	 */
    protected void writeBody(final ObjectOutputStream output) throws IOException {
        output.writeInt(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            writeBytes(output, bytes[i]);
        }
    }

    /**
	 * get the bytes of the dependency bundles.
	 * 
	 * @return the bundle bytes.
	 */
    public byte[][] getDependencies() {
        return bytes;
    }

    /**
	 * set the bytes of the dependency bundles.
	 * 
	 * @param bytes
	 */
    public void setDependencies(final byte[][] bytes) {
        this.bytes = bytes;
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
        buffer.append("[DELIVER_BUNDLES]");
        //$NON-NLS-1$
        buffer.append("- XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", ... ");
        return buffer.toString();
    }
}
