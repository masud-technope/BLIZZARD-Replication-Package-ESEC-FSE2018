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

/**
 * <p>
 * MethodResultMessage is used to return the result of a method invocation to
 * the invoking remote peer.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class RemoteCallResultMessage extends RemoteOSGiMessage {

    /**
	 * the error flag.
	 */
    private byte errorFlag;

    /**
	 * the return value.
	 */
    private Object result;

    /**
	 * the exception.
	 */
    private Throwable exception;

    /**
	 * creates a new MethodResultMessage from InvokeMethodMessage and set the
	 * exception.
	 */
    public  RemoteCallResultMessage() {
        super(REMOTE_CALL_RESULT);
    }

    /**
	 * creates a new MethodResultMessage from network packet:
	 * 
	 * <pre>
	 *       0                   1                   2                   3
	 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |       R-OSGi header (function = Service = 2)                  |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |  error flag   | result or Exception                           \
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
     RemoteCallResultMessage(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        super(REMOTE_CALL_RESULT);
        errorFlag = input.readByte();
        if (errorFlag == 0) {
            result = input.readObject();
            exception = null;
        } else {
            exception = (Throwable) input.readObject();
            result = null;
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
        if (exception == null) {
            out.writeByte(0);
            out.writeObject(result);
        } else {
            out.writeByte(1);
            out.writeObject(exception);
        }
    }

    /**
	 * did the method invocation cause an exception ?
	 * 
	 * @return <code>true</code>, if an exception has been thrown on the remote
	 *         side. In this case, the exception can be retrieved through the
	 *         <code>getException</code> method.
	 */
    public boolean causedException() {
        return (errorFlag == 1);
    }

    /**
	 * get the result object.
	 * 
	 * @return the return value of the invoked message.
	 */
    public Object getResult() {
        return result;
    }

    /**
	 * set the result.
	 * 
	 * @param result
	 *            the result.
	 */
    public void setResult(final Object result) {
        this.result = result;
        errorFlag = 0;
    }

    /**
	 * get the exception.
	 * 
	 * @return the exception or <code>null</code> if non was thrown.
	 */
    public Throwable getException() {
        return exception;
    }

    /**
	 * set the exception.
	 * 
	 * @param t
	 *            the exception.
	 */
    public void setException(final Throwable t) {
        exception = t;
        errorFlag = 1;
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
        buffer.append("[REMOTE_CALL_RESULT] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append(", errorFlag: ");
        buffer.append(errorFlag);
        if (causedException()) {
            //$NON-NLS-1$
            buffer.append(", exception: ");
            buffer.append(exception.getMessage());
        } else {
            //$NON-NLS-1$
            buffer.append(", result: ");
            buffer.append(result);
        }
        return buffer.toString();
    }
}
