/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic.app;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.generic.SSLServerSOContainer;

/**
 * @since 6.0
 */
public abstract class SSLAbstractGenericClientApplication {

    protected String connectTarget;

    protected ISharedObjectContainer clientContainer;

    protected int waitTime = 40000;

    /**
	 * @since 5.1
	 */
    protected String clientId = null;

    /**
	 * @since 5.1
	 */
    protected String password = null;

    protected abstract ISharedObjectContainer createContainer() throws ContainerCreateException;

    protected void processArguments(String[] args) {
        connectTarget = SSLServerSOContainer.getDefaultServerURL();
        for (int i = 0; i < args.length; i++) {
            if (//$NON-NLS-1$
            args[i].equals("-connectTarget")) {
                connectTarget = args[i + 1];
                i++;
            }
            if (//$NON-NLS-1$
            args[i].equals("-waitTime")) {
                waitTime = new Integer(args[i + 1]).intValue();
                i++;
            }
            if (//$NON-NLS-1$
            args[i].equals("-clientId")) {
                clientId = args[i + 1];
            }
            if (//$NON-NLS-1$
            args[i].equals("-connectPassword")) {
                password = args[i + 1];
            }
        }
    }

    protected void initialize() throws ContainerCreateException {
        clientContainer = createContainer();
    }

    protected void connect() throws ContainerConnectException {
        clientContainer.connect(IDFactory.getDefault().createStringID(connectTarget), null);
    }

    protected void dispose() {
        if (clientContainer != null) {
            clientContainer.dispose();
            clientContainer = null;
        }
    }
}
