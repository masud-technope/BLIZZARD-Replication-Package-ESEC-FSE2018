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
package ch.ethz.iks.r_osgi.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import ch.ethz.iks.r_osgi.RemoteServiceReference;
import ch.ethz.iks.r_osgi.URI;

/**
 * Service reference to remote services.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 1.0
 */
final class RemoteServiceReferenceImpl implements RemoteServiceReference {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1340731053890522970L;

    /**
	 * the service interfaces.
	 */
    private final String[] serviceInterfaces;

    /**
	 * the service properties.
	 */
    private Dictionary properties;

    /**
	 * the URI of the remote service.
	 */
    private final URI uri;

    /**
	 * the channel endpoint.
	 */
    private final ChannelEndpointImpl channel;

    /**
	 * create a new remote service reference.
	 * 
	 * @param serviceInterfaces
	 *            the service interfaces.
	 * @param serviceID
	 *            the service id.
	 * @param properties
	 *            the service properties.
	 * @param channel
	 *            the channel endpoint.
	 */
     RemoteServiceReferenceImpl(final String[] serviceInterfaces, final String serviceID, final Dictionary properties, final ChannelEndpointImpl channel) {
        this.serviceInterfaces = serviceInterfaces;
        this.properties = properties;
        //$NON-NLS-1$
        this.uri = channel.getRemoteAddress().resolve("#" + serviceID);
        this.properties = properties;
        this.channel = channel;
    }

    /**
	 * get a service property.
	 * 
	 * @param key
	 *            the key.
	 * @return the value, or null, if not set.
	 */
    public Object getProperty(final String key) {
        return properties.get(key);
    }

    /**
	 * get the property keys.
	 * 
	 * @return the property keys.
	 */
    public String[] getPropertyKeys() {
        final ArrayList result = new ArrayList(properties.size());
        for (final Enumeration e = properties.keys(); e.hasMoreElements(); result.add(e.nextElement())) {
        }
        return (String[]) result.toArray(new String[properties.size()]);
    }

    /**
	 * get the service interfaces.
	 * 
	 * @return the service interfaces.
	 */
    public String[] getServiceInterfaces() {
        return serviceInterfaces;
    }

    /**
	 * get the channel.
	 * 
	 * @return the channel.
	 */
    ChannelEndpointImpl getChannel() {
        return channel;
    }

    /**
	 * get the service properties.
	 * 
	 * @return the properties.
	 */
    Dictionary getProperties() {
        return properties;
    }

    /**
	 * get the service URI.
	 * @return the URI.
	 */
    public URI getURI() {
        return uri;
    }

    /**
	 * set the service properties.
	 * 
	 * @param newProps
	 */
    void setProperties(final Dictionary newProps) {
        properties = newProps;
    }

    /**
	 * is this URL active? It is if the host has a connected channel to the
	 * service provider host.
	 * 
	 * @return true if active.
	 */
    public boolean isActive() {
        return channel.isConnected() && channel.isActive(uri.toString());
    }

    /**
	 * get a string representation.
	 * 
	 * @return a string representation.
	 */
    public String toString() {
        return //$NON-NLS-1$ //$NON-NLS-2$
        "RemoteServiceReference{" + uri + "-" + //$NON-NLS-1$
        Arrays.asList(serviceInterfaces) + //$NON-NLS-1$
        "}";
    }
}
