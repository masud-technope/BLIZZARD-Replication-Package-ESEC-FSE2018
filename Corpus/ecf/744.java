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

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that creates ECF
 * container {@link IContainer} on consumer side (aka the 'client').
 * 
 */
public class ConsumerContainerFactoryBean extends AbstractContainerFactoryBean {

    private ID targetId = null;

    private IConnectContext connectContext;

    public void setTargetId(ID targetId) {
        this.targetId = targetId;
    }

    public void setConnectContext(IConnectContext connectContext) {
        this.connectContext = connectContext;
    }

    protected IContainer createContainer() throws ContainerCreateException, ContainerConnectException {
        IContainer container = super.createBasicContainer();
        if (targetId != null) {
            container.connect(targetId, connectContext);
        }
        return container;
    }

    public void destroy() throws Exception {
        super.destroy();
        this.targetId = null;
        this.connectContext = null;
    }
}
