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
package org.eclipse.ecf.internal.discovery;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

public class ServiceTypeComparator implements Comparator {

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(final Object object1, final Object object2) {
        Assert.isNotNull(object1);
        Assert.isNotNull(object2);
        if (object1 == object2) {
            return 0;
        }
        if (object1.equals(object2)) {
            return 0;
        }
        if (object1 instanceof IServiceTypeID && object2 instanceof IServiceTypeID) {
            final IServiceTypeID type1 = (IServiceTypeID) object1;
            final IServiceTypeID type2 = (IServiceTypeID) object2;
            final String name1 = type1.getNamingAuthority();
            final String name2 = type2.getNamingAuthority();
            if (//$NON-NLS-1$ //$NON-NLS-2$
            !name1.equals("*") && !name2.equals("*") && !name1.equals(name2)) {
                return -1;
            }
            final String[] services1 = type1.getServices();
            final String[] services2 = type2.getServices();
            if (//$NON-NLS-1$ //$NON-NLS-2$
            !services1[0].equals("*") && !services2[0].equals("*") && !Arrays.equals(services1, services2)) {
                return -1;
            }
            final String[] protocols1 = type1.getProtocols();
            final String[] protocols2 = type2.getProtocols();
            if (//$NON-NLS-1$ //$NON-NLS-2$
            !protocols1[0].equals("*") && !protocols2[0].equals("*") && !Arrays.equals(protocols1, protocols2)) {
                return -1;
            }
            final String[] scopes1 = type1.getScopes();
            final String[] scopes2 = type2.getScopes();
            if (//$NON-NLS-1$ //$NON-NLS-2$
            !scopes1[0].equals("*") && !scopes2[0].equals("*") && !Arrays.equals(scopes1, scopes2)) {
                return -1;
            }
            return 0;
        }
        return -1;
    }
}
