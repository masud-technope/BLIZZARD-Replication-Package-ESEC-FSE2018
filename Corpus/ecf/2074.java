/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Angelo ZERR <angelo.zerr@gmail.com>. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.springframework.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Abstract {@link org.springframework.beans.factory.FactoryBean} that creates
 * ECF identity {@link ID}.
 * 
 */
public abstract class AbstractIDFactoryBean implements FactoryBean, InitializingBean, DisposableBean {

    private ID identity = null;

    private IIDFactory idFactory;

    public void setIdFactory(IIDFactory idFactory) {
        this.idFactory = idFactory;
    }

    protected IIDFactory getIdFactory() {
        return idFactory;
    }

    public Object getObject() throws Exception {
        return identity;
    }

    public Class getObjectType() {
        return (this.identity != null ? this.identity.getClass() : ID.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        if (idFactory == null) {
            idFactory = IDFactory.getDefault();
        }
        this.identity = createIdentity();
    }

    public void destroy() throws Exception {
        this.idFactory = null;
    }

    protected abstract ID createIdentity() throws IDCreateException;
}
