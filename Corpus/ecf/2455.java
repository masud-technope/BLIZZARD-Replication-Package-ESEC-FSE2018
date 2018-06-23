/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.provider.local.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;

public class LocalSharedObjectConfig implements ISharedObjectConfig {

    static Map containerProperties = new HashMap(0);

    LocalRemoteServiceContainer container;

    private ID sharedObjectID;

    public  LocalSharedObjectConfig(LocalRemoteServiceContainer container, ID sharedObjectID) {
        this.container = container;
        this.sharedObjectID = sharedObjectID;
    }

    public ID getSharedObjectID() {
        return sharedObjectID;
    }

    public ID getHomeContainerID() {
        return container.getID();
    }

    public ISharedObjectContext getContext() {
        return new ISharedObjectContext() {

            public Object getAdapter(Class adapter) {
                return null;
            }

            public boolean isActive() {
                return true;
            }

            public ID getLocalContainerID() {
                return container.getID();
            }

            public ISharedObjectManager getSharedObjectManager() {
                return null;
            }

            public IQueueEnqueue getQueue() {
                return null;
            }

            public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
                container.connect(targetID, connectContext);
            }

            public void disconnect() {
                container.disconnect();
            }

            public ID getConnectedID() {
                return container.getConnectedID();
            }

            public boolean isGroupManager() {
                return false;
            }

            public ID[] getGroupMemberIDs() {
                return new ID[] { getLocalContainerID() };
            }

            /**
			 * @throws IOException  
			 */
            public void sendCreate(ID targetID, ReplicaSharedObjectDescription sd) throws IOException {
            // do nothing
            }

            /**
			 * @throws IOException  
			 */
            public void sendCreateResponse(ID targetID, Throwable throwable, long identifier) throws IOException {
            // do nothing
            }

            /**
			 * @throws IOException  
			 */
            public void sendDispose(ID targetID) throws IOException {
            // do nothing
            }

            /**
			 * @throws IOException  
			 */
            public void sendMessage(ID targetID, Object data) throws IOException {
            // do nothing
            }

            public Namespace getConnectNamespace() {
                return container.getConnectNamespace();
            }

            public Map getLocalContainerProperties() {
                return containerProperties;
            }
        };
    }

    public Map getProperties() {
        return containerProperties;
    }
}
