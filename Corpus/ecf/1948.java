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
package ch.ethz.iks.r_osgi.channels;

import java.io.IOException;
import ch.ethz.iks.r_osgi.Remoting;
import ch.ethz.iks.r_osgi.URI;

/**
 * Interface for services that create transport channel implementations. Must
 * not necessarily be an OSGi service factory, since this service is only used
 * by the R-OSGi bundle.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.6
 */
public interface NetworkChannelFactory {

    /**
	 * this constant should be set in the properties. The <code>String</code>
	 * set to this property is matched with the protocol argument that client
	 * bundles requesting for connections. Can also be a <code>String[]</code>.
	 */
    //$NON-NLS-1$
    String PROTOCOL_PROPERTY = "protocol";

    /**
	 * get a new connection to a remote OSGi framework.
	 * 
	 * @param endpoint
	 *            the channel endpoint.
	 * @param endpointURI
	 *            the endpoint to connect to.
	 * @return a new transport channel that is typically already connected to
	 *         the endpoint and ready to send messages.
	 * @throws IOException
	 *             in case of connection errors.
	 */
    NetworkChannel getConnection(final ChannelEndpoint endpoint, final URI endpointURI) throws IOException;

    /**
	 * activate the network channel factory. Called by R-OSGi.
	 * 
	 * @param remoting
	 * @throws IOException
	 */
    void activate(final Remoting remoting) throws IOException;

    /**
	 * deactivate the network channel factory. Called by R-OSGi.
	 * 
	 * @param remoting
	 * @throws IOException
	 */
    void deactivate(final Remoting remoting) throws IOException;

    /**
	 * return the port number on which the factory listens for incoming
	 * connections.
	 * 
	 * @param protocol
	 *            the protocol, important if the factory can create channels for
	 *            multiple protocols, e.g., "http" and "https".
	 * @return the port number.
	 */
    int getListeningPort(final String protocol);
}
