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
package ch.ethz.iks.r_osgi.streams;

import java.io.IOException;
import java.io.OutputStream;
import ch.ethz.iks.r_osgi.impl.ChannelEndpointImpl;

/**
 * Output stream proxy.
 * 
 * @author Michael Duller, ETH Zurich.
 */
public class OutputStreamProxy extends OutputStream {

    /**
	 * the stream ID.
	 */
    private final short streamID;

    /**
	 * the channel endpoint.
	 */
    private final ChannelEndpointImpl endpoint;

    /**
	 * create a new output stream proxy.
	 * 
	 * @param streamID
	 *            the stream ID.
	 * @param endpoint
	 *            the endpoint.
	 */
    public  OutputStreamProxy(final short streamID, final ChannelEndpointImpl endpoint) {
        this.streamID = streamID;
        this.endpoint = endpoint;
    }

    /**
	 * write to the stream.
	 * 
	 * @param b
	 *            the value.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public void write(final int b) throws IOException {
        endpoint.writeStream(streamID, b);
    }

    /**
	 * write to the stream.
	 * @param b 
	 * 
	 * @param off
	 *            the offset.
	 * @param len
	 *            the length.
	 * @throws IOException 
	 */
    public void write(final byte[] b, final int off, final int len) throws IOException {
        endpoint.writeStream(streamID, b, off, len);
    }
}
