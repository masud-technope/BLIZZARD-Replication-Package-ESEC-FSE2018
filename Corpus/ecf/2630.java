/*******************************************************************************
 * Copyright (c) 2009 Versant Corp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.userinput;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.discovery.IDiscoveryLocator;

public class UserInputDiscoveryContainerInstantiator implements IContainerInstantiator {

    //$NON-NLS-1$
    public static final String NAME = "ecf.discovery.userinput.locator";

    private static class SingletonHolder {

        public static IContainer instance = new UserInputDiscoveryLocator();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        return SingletonHolder.instance;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return new String[] { IDiscoveryLocator.class.getName() };
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedIntents(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        return new Class[0][0];
    }
}
