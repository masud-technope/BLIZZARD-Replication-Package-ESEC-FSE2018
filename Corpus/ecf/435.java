/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.discovery;

import java.net.URI;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.tests.discovery.ServiceInfoComparator;

public class CompositeServiceInfoComporator extends ServiceInfoComparator {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.ServiceInfoComparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(Object arg0, Object arg1) {
        if (arg0 instanceof IServiceInfo && arg1 instanceof IServiceInfo) {
            final IServiceInfo first = (IServiceInfo) arg0;
            final IServiceInfo second = (IServiceInfo) arg1;
            boolean priority = first.getPriority() == second.getPriority();
            boolean weight = first.getWeight() == second.getWeight();
            final URI uri1 = first.getLocation();
            final URI uri2 = second.getLocation();
            boolean port = uri1.getPort() == uri2.getPort();
            boolean host = uri1.getHost().equals(uri2.getHost());
            final IServiceID firstID = first.getServiceID();
            final IServiceID secondID = second.getServiceID();
            boolean serviceType = firstID.getServiceTypeID().equals(secondID.getServiceTypeID());
            boolean serviceName = firstID.getServiceName().equals(secondID.getServiceName());
            String firstName = firstID.getName();
            String secondName = secondID.getName();
            boolean name = firstName.equals(secondName);
            boolean serviceProperties = compareServiceProperties(first.getServiceProperties(), second.getServiceProperties());
            final boolean result = name && serviceType && serviceName && host && port && priority && weight && serviceProperties;
            if (result == true) {
                return 0;
            }
        }
        return -1;
    }
}
