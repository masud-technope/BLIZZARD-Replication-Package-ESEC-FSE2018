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

import ch.ethz.iks.r_osgi.RemoteOSGiException;
import ch.ethz.iks.r_osgi.URI;
import ch.ethz.iks.r_osgi.types.Timestamp;

/**
 * Channel endpoint manager. Can be used to add redundant service bindings to
 * alternative endpoints and define policies. Additionally, it provides access
 * to the timestamp transformation.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public interface ChannelEndpointManager {

    /**
	 * 
	 */
    public static final int NO_POLICY = 0;

    /**
	 * 
	 */
    public static final int FAILOVER_REDUNDANCY_POLICY = 1;

    /**
	 * 
	 */
    static final int LOADBALANCING_ANY_POLICY = 2;

    /**
	 * 
	 */
    static final int LOADBALANCING_ONE_POLICY = 3;

    /**
	 * add a reduntant binding for a service. <i>EXPERIMENTAL</i>.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param redundant
	 *            the URI of the redundant service.
	 */
    public void addRedundantEndpoint(final URI service, final URI redundant);

    /**
	 * remove a redundant binding from a service. <i>EXPERIMENTAL</i>.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param redundant
	 *            the URI of the redundant service.
	 */
    public void removeRedundantEndpoint(final URI service, final URI redundant);

    /**
	 * set a policy for making use of redundant service bindings.
	 * <i>EXPERIMENTAL</i>.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param policy
	 *            the policy to be used.
	 */
    public void setEndpointPolicy(final URI service, final int policy);

    /**
	 * get the local address of the managed channel endpoint.
	 * 
	 * @return the local URI.
	 */
    public URI getLocalAddress();

    /**
	 * transform a timestamp into the peer's local time.
	 * 
	 * @param timestamp
	 *            the Timestamp.
	 * @return the transformed timestamp.
	 * @throws RemoteOSGiException
	 *             if the transformation fails.
	 * @since 0.2
	 */
    public Timestamp transformTimestamp(final Timestamp timestamp) throws RemoteOSGiException;
}
