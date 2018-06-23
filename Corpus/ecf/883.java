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
package org.eclipse.ecf.provider.discovery;

import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;

public class CompositeDiscoveryContainerInstantiator implements IContainerInstantiator {

    //$NON-NLS-1$
    private static final String ADVERTISER = ".advertiser";

    //$NON-NLS-1$
    private static final String LOCATOR = ".locator";

    /*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
    public IContainer createInstance(final ContainerTypeDescription description, final Object[] parameters) throws ContainerCreateException {
        try {
            final IContainerFactory factory = ContainerFactory.getDefault();
            final Set containers = new HashSet();
            final List list = factory.getDescriptions();
            for (final Iterator itr = list.iterator(); itr.hasNext(); ) {
                final ContainerTypeDescription ctd = (ContainerTypeDescription) itr.next();
                final String name = ctd.getName();
                // do not call ourself
                if (name.startsWith(CompositeDiscoveryContainer.NAME)) {
                    continue;
                }
                if (//$NON-NLS-1$
                name.startsWith("ecf.discovery.")) {
                    final String ccName = description.getName();
                    // composite is configured to be generic container, thus use all discovery providers
                    if (CompositeDiscoveryContainer.NAME.equals(ccName)) {
                        final IContainer container = factory.createContainer(ctd.getName());
                        containers.add(container);
                    // if composite is set to be a locator only, use only discovery locators
                    } else if (ccName.endsWith(LOCATOR) && name.endsWith(LOCATOR)) {
                        final IContainer container = factory.createContainer(ctd.getName());
                        containers.add(container);
                    // if composite is set to be a advertiser only, use only discovery advertiser
                    } else if (ccName.endsWith(ADVERTISER) && name.endsWith(ADVERTISER)) {
                        final IContainer container = factory.createContainer(ctd.getName());
                        containers.add(container);
                    }
                }
            }
            return new CompositeDiscoveryContainer(containers);
        } catch (final IDCreateException e) {
            final ContainerCreateException excep = new ContainerCreateException(e.getMessage());
            excep.setStackTrace(e.getStackTrace());
            throw excep;
        } catch (final ContainerCreateException e) {
            final ContainerCreateException excep = new ContainerCreateException(e.getMessage());
            excep.setStackTrace(e.getStackTrace());
            throw excep;
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(final ContainerTypeDescription description) {
        return new String[] { IDiscoveryAdvertiser.class.getName(), IDiscoveryLocator.class.getName() };
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(final ContainerTypeDescription description) {
        return new Class[0][0];
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedIntents(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return null;
    }
}
