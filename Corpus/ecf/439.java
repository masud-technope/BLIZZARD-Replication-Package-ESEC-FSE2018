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
package ch.ethz.iks.r_osgi;

/**
 * <p>
 * The remote service interface. Is the equivalent of the service interface on
 * the local OSGi framework. Service properties are synchronized with the remote
 * service, as long as the peer is connected.
 * <p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * 
 */
public interface RemoteServiceReference {

    /**
	 * get the service interface class names.
	 * 
	 * @return the interface class names as a string array.
	 */
    String[] getServiceInterfaces();

    /**
	 * get the URI of the remote service.
	 * 
	 * @return the URI of the remote service.
	 */
    URI getURI();

    /**
	 * get a property of the remote service.
	 * 
	 * @param key
	 *            the key.
	 * @return the property belonging to the given key, or <code>null</code> if
	 *         there is no such property set.
	 */
    Object getProperty(String key);

    /**
	 * get all property keys.
	 * 
	 * @return the key array.
	 */
    String[] getPropertyKeys();

    /**
	 * Is the service active?
	 * 
	 * @return true if there is a connection to the host and the service is
	 *         still active and reachable.
	 */
    boolean isActive();
}
