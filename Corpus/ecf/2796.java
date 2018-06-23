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

import java.util.EventListener;

/**
 * <p>
 * The RemoteServiceListener interface is used by applications to register for
 * remote service events. Has to be registered using the whiteboard pattern.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 1.0
 */
public interface RemoteServiceListener extends EventListener {

    /**
	 * Name of the property which denotes the service interfaces in which the
	 * listener is interested. Must be set to a <code>String</code> array of
	 * fully qualified names of the interfaces.
	 * 
	 * @since 0.6
	 */
    //$NON-NLS-1$
    String SERVICE_INTERFACES = "listener.service_interfaces";

    /**
	 * Name of the property which denotes the filter for the listener. Only
	 * events caused by services matching the filter are announced to the
	 * listener, if this property is set to a
	 * <code>org.osgi.framework.Filter</code> object.
	 * 
	 * @since 0.6
	 */
    //$NON-NLS-1$
    String FILTER = "listener.filter";

    /**
	 * <p>
	 * notify the application that a remote service matching the constraints has
	 * caused an event.
	 * </p>
	 * 
	 * @param event
	 *            the <code>RemoteServiceEvent</code>.
	 */
    void remoteServiceEvent(final RemoteServiceEvent event);
}
