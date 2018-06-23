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
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.IContainerManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Abstract {@link org.springframework.beans.factory.FactoryBean} that creates
 * ECF container {@link IContainer}.
 * 
 */
public abstract class AbstractContainerFactoryBean implements FactoryBean, InitializingBean, DisposableBean {

    private IContainer container = null;

    private String containerType = null;

    private IContainerFactory containerFactory = null;

    public void setContainerManager(IContainerManager containerManager) {
        setContainerFactory(containerManager.getContainerFactory());
    }

    public void setContainerFactory(IContainerFactory containerFactory) {
        this.containerFactory = containerFactory;
    }

    protected IContainerFactory getContainerFactory() {
        return containerFactory;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    protected String getContainerType() {
        return containerType;
    }

    // public void setConnectContext(IConnectContext connectContext) {
    // this.connectContext = connectContext;
    // }
    public Object getObject() throws Exception {
        return this.container;
    }

    public Class getObjectType() {
        return (this.container != null ? this.container.getClass() : IContainer.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        if (containerFactory == null) {
            containerFactory = ContainerFactory.getDefault();
        }
        this.container = createContainer();
    }

    public void destroy() throws Exception {
        if (this.container != null) {
            this.container.disconnect();
            this.container.dispose();
        }
        this.container = null;
        this.containerType = null;
        this.containerFactory = null;
    }

    protected IContainer createBasicContainer() throws ContainerCreateException {
        return containerFactory.createContainer(this.containerType);
    }

    protected abstract IContainer createContainer() throws Exception;
}
