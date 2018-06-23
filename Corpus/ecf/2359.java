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
package ch.ethz.iks.r_osgi.service_discovery;

import ch.ethz.iks.r_osgi.URI;

public interface ServiceDiscoveryListener {

    //$NON-NLS-1$
    public static final String SERVICE_INTERFACES_PROPERTY = "service.interfaces";

    //$NON-NLS-1$
    public static final String FILTER_PROPERTY = "filter";

    /**
	 * if this property is set (to anything), the service is automatically
	 * fetched before the listener is called.
	 * 
	 * @since 0.5
	 */
    //$NON-NLS-1$
    public static final String AUTO_FETCH_PROPERTY = "listener.auto_fetch";

    /**
	 * a remote service has been discovered.
	 * 
	 * @param serviceInterface
	 *            the service interface that matched
	 * @param uri
	 *            the URI of the remote service.
	 */
    void announceService(final String serviceInterface, final URI uri);

    /**
	 * a previously discovered remote service is no longer reachable.
	 * 
	 * @param serviceInterface
	 *            the service interface.
	 * @param uri
	 *            the URI of the remote service.
	 */
    void discardService(final String serviceInterface, final URI uri);
}
