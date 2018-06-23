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
package org.eclipse.ecf.tests.provider.jslp;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

/**
 * Used for testing equality
 */
public class JSLPTestComparator implements Comparator {

    /* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
    public int compare(final Object arg0, final Object arg1) {
        if (arg0 instanceof IServiceInfo && arg1 instanceof IServiceInfo) {
            final IServiceInfo first = (IServiceInfo) arg0;
            final IServiceInfo second = (IServiceInfo) arg1;
            final IServiceID firstID = first.getServiceID();
            final IServiceID secondID = second.getServiceID();
            final IServiceTypeID firstTypeID = firstID.getServiceTypeID();
            final IServiceTypeID secondTypeID = secondID.getServiceTypeID();
            final boolean protocolsSame = Arrays.equals(firstTypeID.getProtocols(), secondTypeID.getProtocols());
            final boolean weightSame = first.getWeight() == second.getWeight();
            final boolean prioSame = first.getPriority() == second.getPriority();
            final String firstName = firstID.getName();
            final String secondName = secondID.getName();
            final boolean nameSame = firstName.equals(secondName);
            final String[] firstServices = firstTypeID.getServices();
            final String[] secondServices = secondTypeID.getServices();
            final boolean serviceSame = Arrays.equals(firstServices, secondServices);
            final Namespace firstNamespace = firstID.getNamespace();
            final Namespace secondNamespace = secondID.getNamespace();
            final boolean namespaceSame = firstNamespace.equals(secondNamespace);
            final String firstNA = firstTypeID.getNamingAuthority();
            final String secondsSA = secondTypeID.getNamingAuthority();
            final boolean naSame = firstNA.equals(secondsSA);
            final URI firstLocation = first.getLocation();
            final URI secondLocation = second.getLocation();
            final boolean locationSame = firstLocation.equals(secondLocation);
            final boolean scopesSame = Arrays.equals(firstTypeID.getScopes(), secondTypeID.getScopes());
            final IServiceProperties firstProperty = first.getServiceProperties();
            final IServiceProperties secondProperty = second.getServiceProperties();
            final boolean propertySame = firstProperty.equals(secondProperty);
            final boolean result = protocolsSame && weightSame && prioSame && nameSame && namespaceSame && serviceSame && naSame && locationSame && scopesSame && propertySame;
            if (result == true) {
                return 0;
            }
        }
        return -1;
    }
}
