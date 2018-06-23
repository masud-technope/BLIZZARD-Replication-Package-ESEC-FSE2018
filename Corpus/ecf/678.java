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
import java.util.List;
import java.util.Locale;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.impl.filter.Filter;

/**
 * ServiceRequest message is used to find services in the network.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
class ServiceRequest extends RequestMessage {

    /**
	 * the ServiceType.
	 */
    ServiceType serviceType;

    /**
	 * a RFC 1960 compliant filter predicate.
	 */
    Filter predicate;

    /**
	 * the spi String.
	 */
    String spi;

    /**
	 * creates a new ServiceRequest for a ServiceType.
	 * 
	 * @param type
	 *            the ServiceType.
	 * @param scopes
	 *            a list of scopes to be included.
	 * @param filterStr
	 *            a filter String, RFC 1960 compliant.
	 * @param theLocale
	 *            the Locale of the message.
	 * @throws InvalidSyntaxException
	 *             if the filter is not well-formed.
	 */
     ServiceRequest(final ServiceType type, final List scopes, String filterStr, final Locale theLocale) throws IllegalArgumentException {
        funcID = SRVRQST;
        prevRespList = new ArrayList();
        serviceType = type;
        predicate = filterStr == null ? null : SLPCore.platform.createFilter(filterStr);
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = new ArrayList();
            scopeList.add("default");
        }
        locale = theLocale == null ? SLPCore.DEFAULT_LOCALE : theLocale;
        spi = SLPCore.CONFIG.getSecurityEnabled() ? SLPCore.CONFIG.getSPI() : "";
    }

    /**
	 * create a new ServiceRequest from a DataInput streaming the bytes of a
	 * ServiceRequest message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 * @throws IOException
	 */
    protected  ServiceRequest(final DataInputStream input) throws IOException {
        prevRespList = stringToList(input.readUTF(), ",");
        serviceType = new ServiceType(input.readUTF());
        scopeList = stringToList(input.readUTF(), ",");
        try {
            final String filterStr = input.readUTF();
            predicate = "".equals(filterStr) ? null : SLPCore.platform.createFilter(filterStr);
        } catch (IllegalArgumentException ise) {
            SLPCore.platform.logError("Invalid filter in incoming message " + xid, ise);
        }
        spi = input.readUTF();
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
	 *        |       Service Location header (function = SrvRqst = 1)        |
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |      length of &lt;PRList&gt;       |        &lt;PRList&gt; String        \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |   length of &lt;service-type&gt;    |    &lt;service-type&gt; String      \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |    length of &lt;scope-list&gt;     |     &lt;scope-list&gt; String       \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of predicate string   |  Service Request &lt;predicate&gt;  \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of &lt;SLP SPI&gt; string   |       &lt;SLP SPI&gt; String        \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeUTF(listToString(prevRespList, ","));
        out.writeUTF(serviceType.toString());
        out.writeUTF(listToString(scopeList, ","));
        out.writeUTF(predicate == null ? "" : predicate.toString());
        out.writeUTF(spi);
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        return getHeaderSize() + 2 + listToString(prevRespList, ",").length() + 2 + serviceType.toString().length() + 2 + listToString(scopeList, ",").length() + 2 + (predicate == null ? 0 : predicate.toString().length()) + 2 + spi.length();
    }

    /**
	 * get a string representation of the AttributeReply message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", prevRespList: " + prevRespList);
        buffer.append(", serviceType: " + serviceType);
        buffer.append(", scopeList: " + scopeList);
        buffer.append(", predicate: " + (predicate == null ? "" : predicate.toString()));
        buffer.append(", slpSpi: " + spi);
        return buffer.toString();
    }
}
