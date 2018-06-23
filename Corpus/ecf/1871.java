/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic.app;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.generic.SSLGenericContainerInstantiator;

/**
 * @since 6.0
 */
public class SSLGenericClientJavaApplication extends AbstractGenericClientApplication {

    //$NON-NLS-1$
    protected static final String GENERIC_SSL_CLIENT = "ecf.generic.ssl.client";

    protected ISharedObjectContainer createContainer() throws ContainerCreateException {
        IContainerFactory containerFactory = ContainerFactory.getDefault();
        containerFactory.addDescription(new ContainerTypeDescription(GENERIC_SSL_CLIENT, new SSLGenericContainerInstantiator(), null));
        return (ISharedObjectContainer) containerFactory.createContainer(GENERIC_SSL_CLIENT);
    }

    public static void main(String[] args) throws Exception {
        SSLGenericClientJavaApplication app = new SSLGenericClientJavaApplication();
        app.processArguments(args);
        app.initialize();
        app.connect();
        // wait for waitTime
        try {
            synchronized (app) {
                app.wait(app.waitTime);
            }
        } catch (InterruptedException e) {
        }
        app.dispose();
    }
}
