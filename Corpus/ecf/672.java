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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationException;

/**
 * a ServiceReply Message is sent as reaction of a ServiceRequest.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.6
 */
class ServiceTypeReply extends ReplyMessage {

    /**
	 * a List of ServiceURLs.
	 */
    List serviceTypes;

    /**
	 * create a new ServiceTypeReply from a list of ServiceTypes.
	 * 
	 * @param req
	 *            the request to reply to.
	 * @param types
	 *            the ServiceTypes.
	 */
     ServiceTypeReply(final ServiceTypeRequest req, final List types) {
        this.funcID = SRVTYPERPLY;
        this.locale = req.locale;
        this.xid = req.xid;
        this.address = req.address;
        this.port = req.port;
        this.errorCode = 0;
        this.serviceTypes = types;
    }

    /**
	 * create a new ServiceTypeReply from a DataInput streaming the bytes of an
	 * ServiceTypeReply message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 */
     ServiceTypeReply(final DataInputStream input) throws IOException {
        errorCode = input.readShort();
        serviceTypes = stringToList(input.readUTF(), ",");
    }

    /**
	 * get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * <p>
	 * 
	 * <pre>
	 *    0                   1                   2                   3
	 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |      Service Location header (function = SrvTypeRply = 10)    |
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |           Error Code          |    length of &lt;srvType-list&gt;   |
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |                       &lt;srvtype--list&gt;                         \
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeShort(errorCode);
        out.writeUTF(listToString(serviceTypes, ","));
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        return getHeaderSize() + 2 + 2 + listToString(serviceTypes, ",").length();
    }

    /**
	 * get the result of the reply message.
	 * 
	 * @return the <code>List</code> of results.
	 * @see ch.ethz.iks.slp.impl.ReplyMessage#getResult()
	 */
    List getResult() {
        return serviceTypes;
    }

    /**
	 * get a string representation of the ServiceTypeReply message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", errorCode " + errorCode);
        buffer.append(", ServiceTypeCount " + serviceTypes.size());
        buffer.append(", ServiceTypes " + serviceTypes);
        return buffer.toString();
    }
}
