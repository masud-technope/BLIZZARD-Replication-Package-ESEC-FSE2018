/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.riena.container;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

public class RienaContainerInstantiator extends BaseContainerInstantiator {

    public  RienaContainerInstantiator() {
    // Nothing to do
    }

    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        try {
            if (parameters != null && parameters.length > 0) {
                if (parameters[0] instanceof ID)
                    return new RienaContainer((ID) parameters[0]);
                if (parameters[0] instanceof String)
                    return new RienaContainer(IDFactory.getDefault().createStringID((String) parameters[0]));
            }
            return new RienaContainer(IDFactory.getDefault().createGUID());
        } catch (final IDCreateException e) {
            throw new ContainerCreateException("Exception creating ID for riena container", e);
        }
    }

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return new String[] { IRemoteServiceContainerAdapter.class.getName(), IContainer.class.getName() };
    }

    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        return new Class[][] { {}, { ID.class } };
    }
}
