/****************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.provider.remoteservice.container;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseRemoteServiceContainerInstantiator;

public class RSExampleContainerInstantiator extends BaseRemoteServiceContainerInstantiator {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        try {
            if (parameters != null && parameters.length > 0) {
                if (parameters[0] instanceof ID)
                    return new RSExampleContainer((ID) parameters[0]);
                if (parameters[0] instanceof String)
                    return new RSExampleContainer(IDFactory.getDefault().createStringID((String) parameters[0]));
            }
            return new RSExampleContainer();
        } catch (final IDCreateException e) {
            throw new ContainerCreateException("Exception creating ID for trivial container", e);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.BaseContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        // TODO Return String [] with adapter types supported for the given description
        return super.getSupportedAdapterTypes(description);
    }
}
