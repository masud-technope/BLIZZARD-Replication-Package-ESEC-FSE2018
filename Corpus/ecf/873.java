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

import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.discovery.IDiscoveryContainerAdapter;

/**
 * 
 */
public class SingletonDiscoveryContainerInstantiator implements IContainerInstantiator {

    private static IContainer INSTANCE;

    private static synchronized IContainer getInstance(final String containerName) throws ContainerCreateException {
        if (INSTANCE == null) {
            final IContainerFactory factory = ContainerFactory.getDefault();
            final List list = factory.getDescriptions();
            for (final Iterator itr = list.iterator(); itr.hasNext(); ) {
                final ContainerTypeDescription ctd = (ContainerTypeDescription) itr.next();
                final String name = ctd.getName();
                if (name.equals(containerName)) {
                    final IContainer createContainer = factory.createContainer(ctd.getName());
                    INSTANCE = new SingletonDiscoveryContainer(createContainer);
                    return INSTANCE;
                }
            }
            if (INSTANCE == null) {
                throw new //$NON-NLS-1$
                ContainerCreateException(//$NON-NLS-1$
                "Unknown Container Name");
            }
        }
        return INSTANCE;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
    public IContainer createInstance(final ContainerTypeDescription description, final Object[] parameters) throws ContainerCreateException {
        if (parameters != null && parameters.length == 1 && parameters[0] instanceof String) {
            final String containerName = (String) parameters[0];
            return getInstance(containerName);
        }
        //$NON-NLS-1$
        throw new ContainerCreateException("Missing parameter");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(final ContainerTypeDescription description) {
        return new String[] { IDiscoveryContainerAdapter.class.getName() };
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(final ContainerTypeDescription description) {
        return new Class[][] { { String.class } };
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedIntents(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return null;
    }
}
