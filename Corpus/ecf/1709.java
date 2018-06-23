/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceID;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;

public class DnsSdServiceID extends ServiceID {

    private static final long serialVersionUID = -5265675009221335638L;

    protected  DnsSdServiceID(final Namespace namespace, final IServiceTypeID type, final URI anUri) {
        super(namespace, type, anUri);
    }

    Name getDnsName() throws TextParseException {
        return new Name(//$NON-NLS-1$ //$NON-NLS-2$
        "_" + type.getServices()[0] + "._" + type.getProtocols()[0]);
    }

    /**
	 * @return A relative SRV record independent of a given domain/zone
	 * @throws IOException
	 */
    Record toSRVRecord() throws IOException {
        //$NON-NLS-1$
        final Name name = new Name(getDnsName().toString(), new Name(type.getScopes()[0] + "."));
        final long ttl = serviceInfo.getTTL();
        final int priority = serviceInfo.getPriority();
        final int weight = serviceInfo.getWeight();
        final int port = serviceInfo.getLocation().getPort();
        //$NON-NLS-1$
        final Name target = Name.fromString(serviceInfo.getLocation().getHost() + ".");
        return new SRVRecord(name, DClass.IN, ttl, priority, weight, port, target);
    }

    /**
	 * @param aRecord The corresponding SRVRecord
	 * @return A relative TXT record independent of a given domain/zone
	 * @throws IOException
	 */
    Record[] toTXTRecords(final Record aRecord) throws IOException {
        final List result = new ArrayList();
        final IServiceProperties properties = serviceInfo.getServiceProperties();
        final Enumeration enumeration = properties.getPropertyNames();
        while (enumeration.hasMoreElements()) {
            final Object property = enumeration.nextElement();
            final String key = property.toString();
            final String value = (String) properties.getProperty(key).toString();
            final long ttl = serviceInfo.getTTL();
            //$NON-NLS-1$
            result.add(new TXTRecord(aRecord.getName(), DClass.IN, ttl, key + "=" + value));
        }
        return (Record[]) result.toArray(new Record[result.size()]);
    }
}
