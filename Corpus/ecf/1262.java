/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Angelo ZERR <angelo.zerr@gmail.com>. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.springframework;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.identity.ID;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that creates ECF
 * container {@link IContainer} on host side (aka the 'server').
 * 
 */
public class HostContainerFactoryBean extends AbstractContainerFactoryBean {

    private ID containerId = null;

    public void setContainerId(ID identity) {
        this.containerId = identity;
    }

    protected IContainer createContainer() throws ContainerCreateException {
        if (containerId == null)
            return super.createBasicContainer();
        IContainerFactory containerFactory = getContainerFactory();
        String containerType = getContainerType();
        return containerFactory.createContainer(containerType, containerId);
    }

    public void destroy() throws Exception {
        super.destroy();
        this.containerId = null;
    }
}
