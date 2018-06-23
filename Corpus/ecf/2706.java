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
package ch.ethz.iks.r_osgi.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Request bundle dependencies.
 * 
 * @author Jan S. Rellermeyer
 * 
 */
public class RequestDependenciesMessage extends RemoteOSGiMessage {

    /**
	 * the packages.
	 */
    private String[] packages;

    /**
	 * create a new message.
	 */
    public  RequestDependenciesMessage() {
        super(RemoteOSGiMessage.REQUEST_DEPENDENCIES);
    }

    /**
	 * create a new message from the wire.
	 * 
	 * @param input
	 *            the input stream.
	 * @throws IOException
	 *             in case of IO problems.
	 */
    public  RequestDependenciesMessage(final ObjectInputStream input) throws IOException {
        super(RemoteOSGiMessage.REQUEST_DEPENDENCIES);
        packages = readStringArray(input);
    }

    /**
	 * write the body of the message to the wire.
	 * @param output 
	 * @throws IOException 
	 */
    public void writeBody(final ObjectOutputStream output) throws IOException {
        writeStringArray(output, packages);
    }

    /**
	 * get the packages that have to be resolved.
	 * 
	 * @return the packages.
	 */
    public String[] getPackages() {
        return packages;
    }

    /**
	 * set the packages that have to be resolved.
	 * 
	 * @param packages
	 *            the packages.
	 */
    public void setPackages(final String[] packages) {
        this.packages = packages;
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
        buffer.append("[REQUEST_DEPENDENCIES]");
        //$NON-NLS-1$
        buffer.append("- XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", packages: ");
        buffer.append(Arrays.asList(packages));
        return buffer.toString();
    }
}
