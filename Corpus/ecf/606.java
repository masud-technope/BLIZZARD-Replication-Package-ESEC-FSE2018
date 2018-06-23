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
 * <p>
 * InvokeMethodMessage is used to invoke a method of a remote service.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class RemoteCallMessage extends RemoteOSGiMessage {

    /**
	 * the service ID.
	 */
    private String serviceID;

    /**
	 * the signature of the method that is requested to be invoked.
	 */
    private String methodSignature;

    /**
	 * the argument array of the method call.
	 */
    private Object[] arguments;

    /**
	 * creates a new InvokeMethodMessage.
	 */
    public  RemoteCallMessage() {
        super(REMOTE_CALL);
    }

    /**
	 * creates a new InvokeMethodMessage from network packet:
	 * 
	 * <pre>
	 *       0                   1                   2                   3
	 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |       R-OSGi header (function = InvokeMsg = 3)                |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |   length of &lt;serviceID&gt;     |    &lt;serviceID&gt; String       \
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |    length of &lt;MethodSignature&gt;     |     &lt;MethodSignature&gt; String       \
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |   number of param blocks      |     Param blocks (if any)     \
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * .
	 * 
	 * @param input
	 *            an <code>ObjectInputStream</code> that provides the body of a
	 *            R-OSGi network packet.
	 * @throws IOException
	 *             in case of IO failures.
	 * @throws ClassNotFoundException
	 */
     RemoteCallMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(REMOTE_CALL);
        serviceID = input.readUTF();
        methodSignature = input.readUTF();
        final short argLength = input.readShort();
        arguments = new Object[argLength];
        for (short i = 0; i < argLength; i++) {
            arguments[i] = input.readObject();
        }
    }

    /**
	 * write the body of the message to a stream.
	 * 
	 * @param out
	 *            the ObjectOutputStream.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        out.writeUTF(serviceID);
        out.writeUTF(methodSignature);
        out.writeShort(arguments.length);
        for (short i = 0; i < arguments.length; i++) {
            out.writeObject(arguments[i]);
        }
    }

    /**
	 * get the service ID.
	 * 
	 * @return the service ID.
	 */
    public String getServiceID() {
        return serviceID;
    }

    /**
	 * set the service ID.
	 * 
	 * @param serviceID
	 *            the service ID.
	 */
    public void setServiceID(final String serviceID) {
        this.serviceID = serviceID;
    }

    /**
	 * get the arguments for the invoked method.
	 * 
	 * @return the arguments.
	 */
    public Object[] getArgs() {
        return arguments;
    }

    /**
	 * set the arguments.
	 * 
	 * @param arguments
	 *            the arguments.
	 */
    public void setArgs(final Object[] arguments) {
        this.arguments = arguments;
    }

    /**
	 * get the method signature.
	 * 
	 * @return the method signature.
	 */
    public String getMethodSignature() {
        return methodSignature;
    }

    /**
	 * set the method signature.
	 * 
	 * @param methodSignature
	 *            the method signature.
	 */
    public void setMethodSignature(final String methodSignature) {
        this.methodSignature = methodSignature;
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
        buffer.append("[REMOTE_CALL] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", serviceID: ");
        buffer.append(serviceID);
        //$NON-NLS-1$
        buffer.append(", methodName: ");
        buffer.append(methodSignature);
        //$NON-NLS-1$
        buffer.append(", params: ");
        buffer.append(arguments == null ? "" : //$NON-NLS-1$
        Arrays.asList(arguments).toString());
        return buffer.toString();
    }
}
