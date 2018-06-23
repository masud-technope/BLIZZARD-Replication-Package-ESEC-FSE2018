/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * This ServiceType represents the special RRs defined in 
 * chapter 12. Discovery of Browsing and Registration Domains
 * 
 * see http://files.dns-sd.org/draft-cheshire-dnsext-dns-sd.txt
 */
public class BnRDnsSdServiceTypeID extends DnsSdServiceTypeID {

    private static final long serialVersionUID = -466458565598238072L;

    /**
  	 * A list of domains recommended for browsing
  	 */
    //$NON-NLS-1$
    static final String BROWSE_DOMAINS = "b._dns-sd";

    /**
	 * A single recommended default domain for browsing
	 */
    //$NON-NLS-1$
    static final String DEFAULT_BROWSE_DOMAIN = "db._dns-sd";

    /**
	 * A list of domains recommended for registering services using Dynamic Update
	 */
    //$NON-NLS-1$
    static final String REG_DOMAINS = "r._dns-sd";

    /**
	 * A single recommended default domain for registering services.
	 */
    //$NON-NLS-1$
    static final String DEFAULT_REG_DOMAIN = "dr._dns-sd";

     BnRDnsSdServiceTypeID(IServiceTypeID aServiceType, String aService) {
        super(aServiceType.getNamespace(), aServiceType);
        services = new String[] { aService };
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.provider.dnssd.DnsSdServiceTypeID#getInternalQueries()
	 */
    Lookup[] getInternalQueries() {
        List result = new ArrayList();
        for (int i = 0; i < scopes.length; i++) {
            String scope = scopes[i];
            // remove dangling "."
            if (//$NON-NLS-1$
            scope.endsWith(".")) {
                scope = scope.substring(0, scope.length() - 1);
            }
            Lookup query;
            try {
                query = new //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Lookup(//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                services[0] + "._udp" + "." + scope + ".", Type.PTR);
            } catch (TextParseException e) {
                continue;
            }
            result.add(query);
        }
        return (Lookup[]) result.toArray(new Lookup[result.size()]);
    }

    void setScope(String target) {
        if (//$NON-NLS-1$
        target.endsWith(".")) {
            target = target.substring(0, target.length() - 1);
        }
        scopes = new String[] { target };
        createType();
    }

    Collection getScopesAsZones() {
        final List res = new ArrayList(scopes.length);
        for (int i = 0; i < scopes.length; i++) {
            String scope = scopes[i];
            //$NON-NLS-1$
            res.add(scope + ".");
        }
        return res;
    }
}
