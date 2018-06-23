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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceTypeID;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsSdServiceTypeID extends ServiceTypeID implements IServiceTypeID {

    private static final long serialVersionUID = 1247933069737880365L;

     DnsSdServiceTypeID() {
        super(new DnsSdNamespace());
        scopes = DEFAULT_SCOPE;
        protocols = DEFAULT_PROTO;
        namingAuthority = DEFAULT_NA;
        //$NON-NLS-1$
        services = new String[] { "" };
    }

    public  DnsSdServiceTypeID(Namespace ns, IServiceTypeID id) {
        super(ns, id);
    }

    public  DnsSdServiceTypeID(Namespace namespace, String aType) throws IDCreateException {
        super(namespace, aType);
    }

     DnsSdServiceTypeID(Namespace namespace, Name aName) {
        super(namespace, aName.toString());
    }

    Lookup[] getInternalQueries() {
        String[] protos = protocols;
        int type = Type.SRV;
        String service = null;
        if (services == null || services.length == 0 || //$NON-NLS-1$
        (services.length == 1 && services[0].equals(""))) {
            // if no service is set, create a non service specific query
            //$NON-NLS-1$
            service = "_services._dns-sd._";
            // and set proto to "udp" irregardless what has been set
            //$NON-NLS-1$
            protos = new String[] { "udp" };
            // and query for PTR records
            type = Type.PTR;
        } else {
            //$NON-NLS-1$
            service = "_";
            for (int i = 0; i < services.length; i++) {
                service += services[i];
                //$NON-NLS-1$
                service += //$NON-NLS-1$
                "._";
            }
        }
        List result = new ArrayList();
        for (int i = 0; i < scopes.length; i++) {
            String scope = scopes[i];
            // remove dangling "."
            if (//$NON-NLS-1$
            scope.endsWith(".")) {
                scope = scope.substring(0, scope.length() - 1);
            }
            for (int j = 0; j < protos.length; j++) {
                Lookup query;
                try {
                    query = new //$NON-NLS-1$ //$NON-NLS-2$
                    Lookup(//$NON-NLS-1$ //$NON-NLS-2$
                    service + protos[j] + "." + scope + ".", type);
                } catch (TextParseException e) {
                    continue;
                }
                result.add(query);
            }
        }
        return (Lookup[]) result.toArray(new Lookup[result.size()]);
    }

    /**
	 * @param searchPaths Sets the default search path
	 */
    void setSearchPath(Name[] searchPaths) {
        String[] s = new String[searchPaths.length];
        for (int i = 0; i < searchPaths.length; i++) {
            s[i] = searchPaths[i].toString();
        }
        setSearchPath(s);
    }

    /**
	 * @param searchPaths Sets the default search path
	 */
    void setSearchPath(String[] searchPaths) {
        scopes = searchPaths;
    }

    /**
	 * @return Search scopes used during discovery
	 */
    String[] getSearchPath() {
        return scopes;
    }

    /**
	 * @param additionalSearchPaths Adds the given array to the existing search paths
	 */
    void addSearchPath(String[] additionalSearchPaths) {
        //convert arrays to collections (lists)
        Collection coll1 = Arrays.asList(scopes);
        Collection coll2 = Arrays.asList(additionalSearchPaths);
        // remove dupes
        Set s = new HashSet();
        s.addAll(coll1);
        s.addAll(coll2);
        // convert into new scopes
        scopes = (String[]) s.toArray(new String[s.size()]);
    }
}
