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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

/**
 * a ServiceRegistation message is sent to register a service with all DAs in
 * the scopes.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.1
 */
class ServiceRegistration extends SLPMessage {

    /**
	 * the ServiceURL of the service.
	 */
    ServiceURL url;

    /**
	 * the ServiceType of the service.
	 */
    ServiceType serviceType;

    /**
	 * the List of scopes in which the service will be registered.
	 */
    List scopeList;

    /**
	 * the List of attributes to be registered with the service.
	 */
    List attList;

    /**
	 * an Array of AuthenticationBlocks.
	 */
    AuthenticationBlock[] authBlocks;

    /**
	 * creates a new ServiceRegistration message.
	 * 
	 * @param serviceURL
	 *            the ServiceURL of the service.
	 * @param type
	 *            the ServiceType of the service.
	 * @param scopes
	 *            a List of scopes.
	 * @param attributes
	 *            a List of attributes in
	 * 
	 * <pre>
	 * (key = value)
	 * </pre>
	 * 
	 * format.
	 * @param theLocale
	 *            the locale.
	 */
     ServiceRegistration(final ServiceURL serviceURL, final ServiceType type, final List scopes, final List attributes, final Locale theLocale) {
        funcID = SRVREG;
        locale = theLocale;
        if (serviceURL == null) {
            throw new IllegalArgumentException("serviceURL must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("serviceType must not be null");
        }
        url = serviceURL;
        serviceType = type;
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = Arrays.asList(new String[] { "default" });
        }
        attList = attributes;
        if (attList == null) {
            attList = new ArrayList();
        }
        authBlocks = new AuthenticationBlock[0];
    }

    /**
	 * create a new ServiceRegistration from a DataInput streaming the bytes of
	 * an ServiceRegistration message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 * @throws IOException
	 */
     ServiceRegistration(final DataInputStream input) throws ServiceLocationException, IOException {
        funcID = SRVREG;
        locale = SLPCore.DEFAULT_LOCALE;
        url = ServiceURL.fromBytes(input);
        serviceType = new ServiceType(input.readUTF());
        scopeList = stringToList(input.readUTF(), ",");
        attList = attributeStringToList(input.readUTF());
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
	 *         0                   1                   2                   3
	 *         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |         Service Location header (function = SrvReg = 3)       |
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |                          &lt;URL-Entry&gt;                          \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        | length of service type string |        &lt;service-type&gt;         \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |     length of &lt;scope-list&gt;    |         &lt;scope-list&gt;          \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of attr-list string   |          &lt;attr-list&gt;          \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |# of AttrAuths |(if present) Attribute Authentication Blocks...\
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        url.writeTo(out);
        out.writeUTF(serviceType.toString());
        out.writeUTF(listToString(scopeList, ","));
        out.writeUTF(listToString(attList, ","));
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
        int len = getHeaderSize() + url.getLength() + 2 + serviceType.toString().length() + 2 + listToString(scopeList, ",").length() + 2 + listToString(attList, ",").length() + 1;
        for (int i = 0; i < authBlocks.length; i++) {
            len += authBlocks[i].getLength();
        }
        return len;
    }

    /**
	 * get a string representation of the AttributeReply message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", url: " + url);
        buffer.append(", serviceType: " + serviceType);
        buffer.append(", scopeList: " + scopeList);
        buffer.append(", attList: " + attList);
        return buffer.toString();
    }

    /**
	 * sign this ServiceRegistration.
	 * 
	 * @param spiList
	 *            the List of SPIs.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    void sign(final List spiList) throws ServiceLocationException {
        url.sign(spiList);
        authBlocks = new AuthenticationBlock[spiList.size()];
        for (int k = 0; k < spiList.size(); k++) {
            int timestamp = SLPUtils.getTimestamp();
            String spi = (String) spiList.get(k);
            byte[] data = getAuthData(spi, timestamp);
            authBlocks[k] = new AuthenticationBlock(AuthenticationBlock.BSD_DSA, spi, timestamp, data, null);
        }
    }

    /**
	 * verify this ServiceRegistration.
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
	 * @param spi
	 *            the SPI.
	 * @param timestamp
	 *            the timestamp.
	 * @return the auth data.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    private byte[] getAuthData(final String spi, final int timestamp) throws ServiceLocationException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(spi);
            dos.writeUTF(listToString(attList, ","));
            dos.writeInt(timestamp);
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, ioe.getMessage());
        }
    }
}
