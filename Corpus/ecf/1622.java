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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * deregister a service from a DA.
 * 
 * @author Jan S. Rellermeyer, ETH Z�rich
 * @since 0.1
 */
class ServiceDeregistration extends SLPMessage {

    /**
	 * the service url.
	 */
    ServiceURL url;

    /**
	 * the scopes.
	 */
    List scopeList;

    /**
	 * the attributes.
	 */
    List attList;

    /**
	 * create a new ServiceDeregistration.
	 * 
	 * @param serviceURL
	 *            the ServiceURL.
	 * @param scopes
	 *            a List of scopes.
	 * @param attributes
	 *            the attributes.
	 * @param theLocale
	 *            the locale.
	 */
     ServiceDeregistration(final ServiceURL serviceURL, final List scopes, final List attributes, final Locale theLocale) {
        funcID = SRVDEREG;
        locale = theLocale;
        if (serviceURL == null) {
            throw new IllegalArgumentException("serviceURL must not be null");
        }
        url = serviceURL;
        scopeList = scopes;
        if (scopeList == null) {
            scopeList = Arrays.asList(new String[] { "default" });
        }
        attList = attributes;
        if (attList == null) {
            attList = new ArrayList();
        }
    }

    /**
	 * parse a ServiceDeregistration from an input stream.
	 * 
	 * @param input
	 *            the stream.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 */
    public  ServiceDeregistration(final DataInputStream input) throws ServiceLocationException, IOException {
        scopeList = stringToList(input.readUTF(), ",");
        url = ServiceURL.fromBytes(input);
        attList = attributeStringToList(input.readUTF());
        if (SLPCore.CONFIG.getSecurityEnabled()) {
            if (!verify()) {
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED, "Authentication failed for " + toString());
            }
        }
    }

    /**
	 * get the bytes from a ServiceDeregistration:
	 * <p>
	 * 
	 * <pre>
	 *          0                   1                   2                   3
	 *          0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         |         Service Location header (function = SrvDeReg = 4)     |
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         |    Length of &lt;scope-list&gt;     |         &lt;scope-list&gt;          \
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         |                           URL Entry                           \
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *         |      Length of &lt;tag-list&gt;     |            &lt;tag-list&gt;         \
	 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return the bytes.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    protected void writeTo(final DataOutputStream out) throws IOException {
        out.writeUTF(listToString(scopeList, ","));
        url.writeTo(out);
        out.writeUTF(listToString(attList, ","));
    }

    /**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
    protected int getSize() {
        return getHeaderSize() + 2 + listToString(scopeList, ",").length() + url.getLength() + 2 + listToString(attList, ",").length();
    }

    /**
	 * sign this ServiceDeregistration.
	 * 
	 * @param spiList
	 *            a List of SPIs.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    void sign(final List spiList) throws ServiceLocationException {
        url.sign(spiList);
    }

    /**
	 * verify the ServiceDeregistration.
	 * 
	 * @return true if it could be verified.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
    boolean verify() throws ServiceLocationException {
        if (!url.verify()) {
            return false;
        }
        return true;
    }
}
