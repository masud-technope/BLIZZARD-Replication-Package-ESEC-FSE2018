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
package org.eclipse.ecf.provider.jslp.container;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.discovery.IDiscoveryContainerAdapter;

public class ContainerInstantiator implements IContainerInstantiator {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
    public IContainer createInstance(final ContainerTypeDescription description, final Object[] args) throws ContainerCreateException {
        try {
            final JSLPDiscoveryContainer container = new JSLPDiscoveryContainer();
            return container;
        } catch (IDCreateException e) {
            final ContainerCreateException excep = new ContainerCreateException("Could not create JSLPDiscoveryContainer", e);
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
        return new String[] { IDiscoveryContainerAdapter.class.getName() };
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(final ContainerTypeDescription description) {
        return new Class[0][0];
    }

    /**
	 * @param description description
	 * @return String[] supported intents
	 * @since 3.0
	 */
    public String[] getSupportedIntents(final ContainerTypeDescription description) {
        return null;
    }
}
