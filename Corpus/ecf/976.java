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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationException;

/**
 * a AttributeReply Message is sent as reaction to a AttributeRequest message.
 * 
 * @author Jan S. Rellermeyer, Systems Group, ETH Z�rich
 * @since 0.1
 */
class AttributeReply extends ReplyMessage {

    /**
	 * a List of attributes that have been retrieved.
	 */
    List attributes;

    /**
	 * an array of AuthenticationBlocks. Used to verify that the reply has not
	 * been modified during transport.
	 */
    AuthenticationBlock[] authBlocks;

    /**
	 * create a new AttributeReply from a list of attributes.
	 * 
	 * @param attList
	 *            a list of attributes in
	 * 
	 * <verbatim> (key=value) </verbatim>
	 * 
	 * format.
	 */
     AttributeReply(final AttributeRequest req, final List attList) {
        funcID = ATTRRPLY;
        errorCode = 0;
        attributes = attList;
        locale = req.locale;
        xid = req.xid;
        address = req.address;
        port = req.port;
        authBlocks = new AuthenticationBlock[0];
    }

    /**
	 * create a new AttributeReply from a DataInput streaming the bytes of an
	 * AttributeReply message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 */
     AttributeReply(final DataInputStream input) throws IOException, ServiceLocationException {
        errorCode = input.readShort();
        attributes = attributeStringToListLiberal(input.readUTF());
        authBlocks = AuthenticationBlock.parse(input);
        if (SLPCore.CONFIG.getSecurityEnabled()) {
            if (!verify()) {
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED, "Authentication failed for " + toString());
            }
        }
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
	 *   |       Service Location header (function = AttrRply = 7)       |
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |         Error Code            |      length of &lt;attr-list&gt;    |
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |                         &lt;attr-list&gt;                           \
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |# of AttrAuths |  Attribute Authentication Block (if present)  \
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 * @throws IOException 
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeShort(errorCode);
        out.writeUTF(listToString(attributes, ","));
        out.write(authBlocks.length);
        for (int i = 0; i < authBlocks.length; i++) {
            authBlocks[i].write(out);
        }
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        int len = getHeaderSize() + 4 + listToString(attributes, ",").length() + 1;
        for (int i = 0; i < authBlocks.length; i++) {
            len += authBlocks[i].getLength();
        }
        return len;
    }

    /**
	 * get the result.
	 * 
	 * @see ch.ethz.iks.slp.impl.ReplyMessage#getResult()
	 * @return the <code>List</code> of results.
	 */
    List getResult() {
        return attributes;
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
        buffer.append(", attrCount " + attributes.size());
        buffer.append(", attributes " + attributes);
        return buffer.toString();
    }

    /**
	 * sign this AttributeReply.
	 * 
	 * @param spiStr
	 *            a String of SPIs.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    void sign(final String spiStr) throws ServiceLocationException {
        List spiList = stringToList(spiStr, ",");
        authBlocks = new AuthenticationBlock[spiList.size()];
        for (int k = 0; k < spiList.size(); k++) {
            int timestamp = SLPUtils.getTimestamp();
            String spi = (String) spiList.get(k);
            byte[] data = getAuthData(spi, timestamp);
            authBlocks[k] = new AuthenticationBlock(AuthenticationBlock.BSD_DSA, spi, timestamp, data, null);
        }
    }

    /**
	 * verify this AttributeReply.
	 * 
	 * @return true if verification suceeds.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    boolean verify() throws ServiceLocationException {
        for (int i = 0; i < authBlocks.length; i++) {
            if (authBlocks[i].verify(getAuthData(authBlocks[i].getSPI(), authBlocks[i].getTimestamp()))) {
                return true;
            }
        }
        return false;
    }

    /**
	 * get the authentication data.
	 * 
	 * @param spiStr
	 *            the SPI.
	 * @param timestamp
	 *            the timestamp.
	 * @return the auth data.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    private byte[] getAuthData(final String spiStr, final int timestamp) throws ServiceLocationException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(spiStr);
            dos.writeUTF(listToString(attributes, ","));
            dos.writeInt(timestamp);
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, ioe.getMessage());
        }
    }
}
