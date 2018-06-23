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

/**
 * ServiceTypeRequest message is used to find existing service types.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.6
 */
class ServiceTypeRequest extends RequestMessage {

    /**
	 * the naming authority.
	 */
    String namingAuthority;

    private static final String NA_ALL = "*";

    private static final String NA_DEFAULT = "";

    /**
	 * creates a new ServiceTypeRequest.
	 * 
	 * @param authority
	 *            the naming authority.
	 * @param scopes
	 *            a list of scopes to be included.
	 * @param theLocale
	 *            the Locale of the message.
	 */
     ServiceTypeRequest(final String authority, final List scopes, final Locale theLocale) {
        funcID = SRVTYPERQST;
        prevRespList = new ArrayList();
        namingAuthority = authority != null ? authority : NA_ALL;
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = new ArrayList();
            scopeList.add("default");
        }
        locale = theLocale == null ? SLPCore.DEFAULT_LOCALE : theLocale;
    }

    /**
	 * create a new ServiceTypeRequest from a DataInput streaming the bytes of a
	 * ServiceTypeRequest message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 */
     ServiceTypeRequest(final DataInputStream input) throws IOException {
        prevRespList = stringToList(input.readUTF(), ",");
        final int authLen = input.readUnsignedShort();
        if (authLen == 0xFFFF) {
            namingAuthority = NA_ALL;
        } else if (authLen == -1) {
            namingAuthority = NA_DEFAULT;
        } else {
            byte[] buf = new byte[authLen];
            input.readFully(buf);
            namingAuthority = new String(buf);
        }
        scopeList = stringToList(input.readUTF(), ",");
    }

    /**
	 * get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * <p>
	 * 
	 * <pre>
	 *   0                   1                   2                   3
	 *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |      Service Location header (function = SrvTypeRqst = 9)     |
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |        length of PRList       |        &lt;PRList&gt; String        \
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |   length of Naming Authority  |   &lt;Naming Authority String&gt;   \
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *  |     length of &lt;scope-list&gt;    |      &lt;scope-list&gt; String      \
	 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws IOException
	 * @throws ServiceLocationException
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeUTF(listToString(prevRespList, ","));
        if (namingAuthority.equals(NA_ALL)) {
            out.writeShort(0xFFFF);
        } else if (namingAuthority.equals(NA_DEFAULT)) {
            out.writeUTF("");
        } else {
            out.writeUTF(namingAuthority);
        }
        out.writeUTF(listToString(scopeList, ","));
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        int len = getHeaderSize() + 2 + listToString(prevRespList, ",").length();
        if (namingAuthority.equals(NA_DEFAULT) || namingAuthority.equals(NA_ALL)) {
            len += 2;
        } else {
            len += 2 + namingAuthority.length();
        }
        len += 2 + listToString(scopeList, ",").length();
        return len;
    }

    /**
	 * get a string representation of the ServiceTypeRequest message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", prevRespList: " + prevRespList);
        if (namingAuthority.equals(NA_ALL)) {
            buffer.append(", namingAuthority: ALL (NA_ALL)");
        } else if (namingAuthority.equals(NA_DEFAULT)) {
            buffer.append(", namingAuthority: IANA (NA_DEFAULT)");
        } else {
            buffer.append(", namingAuthority: " + namingAuthority);
        }
        buffer.append(", scopeList: " + scopeList);
        return buffer.toString();
    }
}
