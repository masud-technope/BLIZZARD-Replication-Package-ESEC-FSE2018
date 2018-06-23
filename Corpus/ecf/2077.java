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

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.IContainerInstantiator;

public class ContainerInstantiator implements IContainerInstantiator {

    private static final String ADVERTISER = Activator.DISCOVERY_CONTAINER_NAME_VALUE + Activator.ADVERTISER;

    private static final String LOCATOR = Activator.DISCOVERY_CONTAINER_NAME_VALUE + Activator.LOCATOR;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        if (description != null && ADVERTISER.equals(description.getName())) {
            return new DnsSdDiscoveryAdvertiser();
        } else if (description != null && LOCATOR.equals(description.getName())) {
            return new DnsSdDiscoveryLocator();
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedIntents(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedIntents(ContainerTypeDescription description) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        // TODO Auto-generated method stub
        return null;
    }
}
