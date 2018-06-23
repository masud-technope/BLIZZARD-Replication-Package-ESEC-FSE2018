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
package org.eclipse.ecf.tests.provider.dnssd;

import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.tests.discovery.ServiceInfoComparator;

public class DnsSdDiscoveryComparator extends ServiceInfoComparator {

    /* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(Object arg0, Object arg1) {
        if (arg0 instanceof IServiceInfo && arg1 instanceof IServiceInfo) {
            final IServiceInfo first = (IServiceInfo) arg0;
            final IServiceInfo second = (IServiceInfo) arg1;
            final IServiceID firstServiceId = first.getServiceID();
            final IServiceID secondServiceId = second.getServiceID();
            boolean idsSame = firstServiceId.equals(secondServiceId);
            boolean prioSame = first.getPriority() == second.getPriority();
            boolean weightSame = first.getWeight() == second.getWeight();
            boolean servicePropertiesSame = compareServiceProperties(first.getServiceProperties(), second.getServiceProperties());
            // <= due to the fact that we might get a cache hit during testing which ttl has already decreased
            boolean ttlSame = first.getTTL() <= second.getTTL();
            final boolean result = (idsSame && prioSame && weightSame && servicePropertiesSame && ttlSame);
            if (result == true) {
                return 0;
            }
        }
        return -1;
    }
}
