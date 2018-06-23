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

import java.util.EventObject;

/**
 * Remote service event class.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public final class RemoteServiceEvent extends EventObject {

    /**
	 * Type of service lifecycle change.
	 */
    private final transient int type;

    /**
	 * The service got registered.
	 */
    public static final int REGISTERED = 0x00000001;

    /**
	 * The service got modified.
	 */
    public static final int MODIFIED = 0x00000002;

    /**
	 * The service is unregistering.
	 */
    public static final int UNREGISTERING = 0x00000004;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * create a new remote service event.
	 * 
	 * @param type
	 *            the type of the event.
	 * @param remoteRef
	 *            the remote service reference.
	 */
    public  RemoteServiceEvent(final int type, final RemoteServiceReference remoteRef) {
        super(remoteRef);
        this.type = type;
    }

    /**
	 * get the type of the event.
	 * 
	 * @return the type.
	 */
    public int getType() {
        return (type);
    }

    /**
	 * get the remote service reference.
	 * 
	 * @return the remote service reference.
	 */
    public RemoteServiceReference getRemoteReference() {
        return (RemoteServiceReference) getSource();
    }
}
