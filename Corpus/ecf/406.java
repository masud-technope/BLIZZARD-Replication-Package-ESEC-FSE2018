/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery;

import java.util.Comparator;
import java.util.Enumeration;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * Used for testing equality 
 */
public class ServiceInfoComparator implements Comparator {

    protected boolean compareServiceProperties(IServiceProperties p1, IServiceProperties p2) {
        if (p1.size() != p2.size())
            return false;
        for (final Enumeration e = p1.getPropertyNames(); e.hasMoreElements(); ) {
            final String key = (String) e.nextElement();
            Object o1 = p1.getProperty(key);
            Object o2 = p2.getProperty(key);
            if (p1.getPropertyBytes(key) != null) {
                o1 = p1.getPropertyBytes(key);
            }
            if (p2.getPropertyBytes(key) != null) {
                o2 = p2.getPropertyBytes(key);
            }
            if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
                final byte[] b1 = (byte[]) o1;
                final byte[] b2 = (byte[]) o2;
                for (int i = 0; i < b1.length; i++) if (b1[i] != b2[i])
                    return false;
            } else if (!o1.equals(o2))
                return false;
        }
        return true;
    }

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
            boolean ttlSame = first.getTTL() == second.getTTL();
            final boolean result = (idsSame && prioSame && weightSame && servicePropertiesSame && ttlSame);
            if (result == true) {
                return 0;
            }
        }
        return -1;
    }
}
