/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationException;

/**
 * a ServiceAcknowledgement is sent by a DA as reaction to a ServiceRegistration
 * or ServiceDeregistration.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.1
 */
class ServiceAcknowledgement extends ReplyMessage {

    /**
	 * create a new ServiceAcknowledgement from a DataInput streaming the bytes
	 * of an ServiceAcknowledgement message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 * @throws IOException
	 */
     ServiceAcknowledgement(final DataInput input) throws IOException {
        errorCode = input.readShort();
    }

    /**
	 * create a new ServiceAcknowledgement.
	 * 
	 * @param msg
	 *            the SLPMessage to acknowledge.
	 * @param error
	 *            the error code.
	 */
     ServiceAcknowledgement(final SLPMessage msg, final int error) {
        funcID = SRVACK;
        xid = msg.xid;
        locale = msg.locale;
        address = msg.address;
        port = msg.port;
        errorCode = error;
    }

    /**
	 * get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * <p>
	 * 
	 * <pre>
	 *       0                   1                   2                   3
	 *       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |          Service Location header (function = SrvAck = 5)      |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *      |          Error Code           |
	 *      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeShort(errorCode);
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        return getHeaderSize() + 2;
    }

    /**
	 * get the result.
	 * 
	 * @see ch.ethz.iks.slp.impl.ReplyMessage#getResult()
	 * @return the <code>List</code> of results.
	 */
    List getResult() {
        return null;
    }

    /**
	 * get a string representation of the ServiceAcknowledgement message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        return super.toString() + ", errorCode " + errorCode;
    }
}
