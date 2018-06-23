/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Angelo ZERR <angelo.zerr@gmail.com>. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.springframework.security;

import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * {@link FactoryBean} that creates ECF connect context {@link IConnectContext}.
 * 
 */
public class ConnectContextFactoryBean implements FactoryBean, InitializingBean, DisposableBean {

    private IConnectContext connectContext = null;

    private String username;

    private Object password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public Object getObject() throws Exception {
        return this.connectContext;
    }

    public Class getObjectType() {
        return (this.connectContext != null ? this.connectContext.getClass() : IConnectContext.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        if (username == null) {
            this.connectContext = ConnectContextFactory.createPasswordConnectContext(password.toString());
        } else {
            this.connectContext = ConnectContextFactory.createUsernamePasswordConnectContext(username, password);
        }
    }

    public void destroy() throws Exception {
        this.connectContext = null;
        this.username = null;
        this.password = null;
    }
}
