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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * a ServiceReply Message is sent as reaction to a ServiceRequest.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.1
 */
class ServiceReply extends ReplyMessage {

    /**
	 * a List of ServiceURLs.
	 */
    List urlList;

    /**
	 * create a new ServiceReply from a list of ServiceURLs.
	 * 
	 * @param req
	 *            the ServiceRequest to reply to.
	 * @param urls
	 *            the result URLs.
	 */
     ServiceReply(final ServiceRequest req, final List urls) {
        this.funcID = SRVRPLY;
        this.xid = req.xid;
        this.locale = req.locale;
        this.address = req.address;
        this.port = req.port;
        this.errorCode = 0;
        this.urlList = urls;
    }

    /**
	 * create a new ServiceReply from a DataInput streaming the bytes of an
	 * ServiceReply message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 * @throws IOException
	 */
     ServiceReply(final DataInputStream input) throws ServiceLocationException, IOException {
        errorCode = input.readShort();
        short entryCount = input.readShort();
        urlList = new ArrayList();
        for (int i = 0; i < entryCount; i++) {
            urlList.add(ServiceURL.fromBytes(input));
        }
        if (SLPCore.CONFIG.getSecurityEnabled()) {
            if (!verify())
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED, toString());
        }
    }

    /**
	 * get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * <p>
	 * 
	 * <pre>
	 *      0                   1                   2                   3
	 *      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |        Service Location header (function = SrvRply = 2)       |
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |        Error Code             |        URL Entry count        |
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |       &lt;URL Entry 1&gt;          ...       &lt;URL Entry N&gt;          \
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeShort(errorCode);
        out.writeShort(urlList.size());
        for (int i = 0; i < urlList.size(); i++) {
            ((ServiceURL) urlList.get(i)).writeTo(out);
        }
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        int len = getHeaderSize() + 2 + 2;
        for (int i = 0; i < urlList.size(); i++) {
            len += ((ServiceURL) urlList.get(i)).getLength();
        }
        return len;
    }

    List getResult() {
        return urlList;
    }

    /**
	 * get a string representation of the AttributeReply message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", errorCode " + errorCode);
        buffer.append(", URLCount " + urlList.size());
        buffer.append(", URLs " + urlList);
        return buffer.toString();
    }

    /**
	 * sign the ServiceReply.
	 * 
	 * @param spiStr
	 *            the SPI String.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    void sign(final String spiStr) throws ServiceLocationException {
        List spiList = stringToList(spiStr, ",");
        for (Iterator urlIter = urlList.iterator(); urlIter.hasNext(); ) {
            ServiceURL url = (ServiceURL) urlIter.next();
            url.sign(spiList);
        }
    }

    /**
	 * verify the ServiceReply.
	 * 
	 * @return true if it could be verified.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    boolean verify() throws ServiceLocationException {
        for (Iterator urlIter = urlList.iterator(); urlIter.hasNext(); ) {
            ServiceURL url = (ServiceURL) urlIter.next();
            if (!url.verify()) {
                return false;
            }
        }
        return true;
    }
}
