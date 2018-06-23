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

import java.util.Dictionary;
import org.osgi.framework.ServiceRegistration;
import ch.ethz.iks.r_osgi.URI;
import ch.ethz.iks.r_osgi.messages.RemoteOSGiMessage;

/**
 * The endpoint of a channel to a connected remote peer.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.6
 */
public interface ChannelEndpoint {

    /**
	 * callback for NetworkChannels.
	 * 
	 * @param msg
	 *            the message.
	 * @since 0.6
	 */
    void receivedMessage(final RemoteOSGiMessage msg);

    /**
	 * invoke a method on the remote host. This function is used by all proxy
	 * bundles.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param methodSignature
	 *            the signature of the method.
	 * @param args
	 *            the method parameter.
	 * @return the result of the remote method invokation.
	 * @throws Throwable
	 *             can be either a local exception or an exception that occured
	 *             on the original service method.
	 * @since 0.6
	 */
    Object invokeMethod(final String service, final String methodSignature, final Object[] args) throws Throwable;

    /**
	 * get the attributes of a service. This function is used to simplify proxy
	 * bundle generation.
	 * 
	 * @param service
	 *            the URI of the remote service.
	 * @return the service attributes.
	 * @since 0.6
	 */
    Dictionary getProperties(final String service);

    /**
	 * get the attributes for the presentation of the service. This function is
	 * used by proxies that support ServiceUI presentations.
	 * 
	 * @param service
	 *            the URI of the remote service.
	 * @return the attributes for the presentation.
	 * @since 0.6
	 */
    Dictionary getPresentationProperties(final String service);

    /**
	 * register a proxied service registration to allow updates of the
	 * properties.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param reg
	 *            the service registration object.
	 */
    void trackRegistration(final String service, final ServiceRegistration reg);

    /**
	 * unregister a proxied service registration.
	 * 
	 * @param service
	 *            the URI of the service.
	 * @param reg
	 *            the service registration object.
	 */
    void untrackRegistration(final String service);

    /**
	 * get the URI of the remote channel endpoint.
	 * 
	 * @return the channel URI.
	 */
    URI getRemoteAddress();

    /**
	 * dispose of the channel endpoint.
	 */
    void dispose();
}
