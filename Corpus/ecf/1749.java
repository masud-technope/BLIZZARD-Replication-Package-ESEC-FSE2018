/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
/**
 * 
 */
package org.eclipse.ecf.server.generic;

import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.provider.generic.SSLServerSOContainer;
import org.eclipse.ecf.provider.generic.SSLServerSOContainerGroup;

/**
 * 
 * @since 6.0
 *
 */
public class SSLGenericServerContainer extends SSLServerSOContainer {

    final SSLAbstractGenericServer abstractGenericServer;

    private IContainerListener departedListener = new IContainerListener() {

        public void handleEvent(IContainerEvent event) {
            if (event instanceof IContainerDisconnectedEvent) {
                IContainerDisconnectedEvent de = (IContainerDisconnectedEvent) event;
                SSLGenericServerContainer.this.abstractGenericServer.handleDisconnect(de.getTargetID());
            } else if (event instanceof IContainerEjectedEvent) {
                IContainerEjectedEvent de = (IContainerEjectedEvent) event;
                SSLGenericServerContainer.this.abstractGenericServer.handleEject(de.getTargetID());
            }
        }
    };

    public  SSLGenericServerContainer(SSLAbstractGenericServer abstractGenericServer, ISharedObjectContainerConfig config, SSLServerSOContainerGroup listener, String path, int keepAlive) {
        super(config, listener, path, keepAlive);
        this.abstractGenericServer = abstractGenericServer;
        addListener(departedListener);
    }

    public void dispose() {
        removeListener(departedListener);
        super.dispose();
    }
}
