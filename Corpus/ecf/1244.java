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
import ch.ethz.iks.slp.ServiceURL;

/**
 * a AttributeRequest Message is sent to discover the attributes of a service.
 * 
 * @author Jan S. Rellermeyer
 * @since 0.1
 */
class AttributeRequest extends RequestMessage {

    /**
	 * the url of the service.
	 */
    String url;

    /**
	 * a list of tags that are requested if they exist.
	 */
    List tagList;

    /**
	 * the spi string.
	 */
    String spi;

    /**
	 * create an AttributeRequest message for a ServiceURL.
	 * 
	 * @param serviceURL
	 *            the ServiceURL
	 * @param scopes
	 *            a list of scopes that are included.
	 * @param tags
	 *            a list of tags that are requested. If omitted, all attribute
	 *            values will be returned.
	 * @param theLocale
	 *            the Locale of the message.
	 */
     AttributeRequest(final ServiceURL serviceURL, final List scopes, final List tags, final Locale theLocale) {
        funcID = ATTRRQST;
        url = serviceURL.toString();
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = new ArrayList();
            scopeList.add("default");
        }
        tagList = tags;
        if (tagList == null) {
            tagList = new ArrayList();
        }
        locale = theLocale == null ? SLPCore.DEFAULT_LOCALE : theLocale;
        spi = SLPCore.CONFIG.getSecurityEnabled() ? SLPCore.CONFIG.getSPI() : "";
    }

    /**
	 * create an AttributeRequest message for a ServiceType.
	 * 
	 * @param type
	 *            the ServiceType.
	 * @param scopes
	 *            a list of scopes that are included.
	 * @param tags
	 *            a list of tags that are requested. If omitted, all attribute
	 *            values will be returned.
	 * @param theLocale
	 *            the Locale of the message.
	 */
     AttributeRequest(final ServiceType type, final List scopes, final List tags, final Locale theLocale) {
        funcID = ATTRRQST;
        url = type.toString();
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = new ArrayList();
            scopeList.add("default");
        }
        tagList = tags;
        if (tagList == null) {
            tagList = new ArrayList();
        }
        locale = theLocale == null ? SLPCore.DEFAULT_LOCALE : theLocale;
        spi = SLPCore.CONFIG.getSecurityEnabled() ? SLPCore.CONFIG.getSPI() : "";
    }

    /**
	 * create a new AttributeRequest from a DataInput streaming the bytes of an
	 * AttributeReply message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             if IO Exceptions occure.
	 */
     AttributeRequest(final DataInputStream input) throws IOException {
        prevRespList = stringToList(input.readUTF(), ",");
        url = input.readUTF();
        scopeList = stringToList(input.readUTF(), ",");
        tagList = stringToList(input.readUTF(), ",");
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
	 *        |       Service Location header (function = AttrRqst = 6)       |
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |       length of PRList        |        &lt;PRList&gt; String        \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |         length of URL         |              URL              \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |    length of &lt;scope-list&gt;     |      &lt;scope-list&gt; string      \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of &lt;tag-list&gt; string  |       &lt;tag-list&gt; string       \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |   length of &lt;SLP SPI&gt; string  |        &lt;SLP SPI&gt; string       \
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
        out.writeUTF(url);
        out.writeUTF(listToString(scopeList, ","));
        out.writeUTF(listToString(tagList, ","));
        out.writeUTF(spi);
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        return getHeaderSize() + 2 + listToString(prevRespList, ",").length() + 2 + url.length() + 2 + listToString(scopeList, ",").length() + 2 + listToString(tagList, ",").length() + 2 + spi.length();
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
        buffer.append(", URL: " + url);
        buffer.append(", scopeList: " + scopeList);
        buffer.append(", tag-list: " + tagList);
        buffer.append(", slpSpi: " + spi);
        return buffer.toString();
    }
}
