/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.internal.server.generic.Activator;
import org.eclipse.ecf.provider.generic.*;

/**
 * @since 6.0
 */
public class SSLGenericServerContainerGroup implements IGenericServerContainerGroup {

    private SSLGenericServerSOContainerGroup serverGroup;

    private Map defaultContainerProperties;

    class SSLGenericServerSOContainerGroup extends SSLServerSOContainerGroup {

        public  SSLGenericServerSOContainerGroup(String name, int port, InetAddress bindAddress) {
            super(name, null, port, bindAddress);
        }

        public Map getMap() {
            return map;
        }
    }

    /**
	 * @since 7.0
	 */
    public  SSLGenericServerContainerGroup(String hostname, int port, InetAddress bindAddress, Map defaultContainerProperties) {
        this.serverGroup = new SSLGenericServerSOContainerGroup(hostname, port, bindAddress);
        this.defaultContainerProperties = defaultContainerProperties;
    }

    public  SSLGenericServerContainerGroup(String hostname, int port, Map defaultContainerProperties) {
        this(hostname, port, null, defaultContainerProperties);
    }

    private String getHost() {
        return serverGroup.getName();
    }

    private int getPort() {
        return serverGroup.getPort();
    }

    public Map getContainers() {
        Map result = new HashMap();
        Map lock = serverGroup.getMap();
        synchronized (lock) {
            for (Iterator i = lock.keySet().iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                result.put(key, lock.get(key));
            }
        }
        return result;
    }

    public ISharedObjectContainer createContainer(String path, int keepAlive, Map properties) throws ContainerCreateException {
        if (path == null)
            //$NON-NLS-1$
            throw new ContainerCreateException("Path for new container cannot be null");
        Map lock = serverGroup.getMap();
        ISharedObjectContainer newContainer = null;
        synchronized (lock) {
            SSLServerSOContainer existing = (SSLServerSOContainer) lock.get(path);
            if (existing != null)
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new ContainerCreateException("Container with path=" + path + " already exists");
            // create container
            newContainer = createGenericServerContainer(path, keepAlive, properties);
            // add To container manager
            addNewContainerToContainerManager(newContainer);
        }
        return newContainer;
    }

    public ISharedObjectContainer createContainer(String path, int keepAlive) throws ContainerCreateException {
        return createContainer(path, keepAlive, null);
    }

    private void addNewContainerToContainerManager(ISharedObjectContainer container) {
        IContainerManager containerManager = Activator.getDefault().getContainerManager();
        if (containerManager != null) {
            //$NON-NLS-1$
            ContainerTypeDescription ctd = containerManager.getContainerFactory().getDescriptionByName("ecf.generic.server");
            containerManager.addContainer(container, ctd);
        }
    }

    private void removeContainerFromContainerManager(ISharedObjectContainer container) {
        IContainerManager containerManager = Activator.getDefault().getContainerManager();
        if (containerManager != null) {
            containerManager.removeContainer(container);
        }
    }

    public ISharedObjectContainer createContainer(String path) throws ContainerCreateException {
        return createContainer(path, IGenericServerContainerGroup.DEFAULT_KEEPALIVE);
    }

    /**
	 * @since 5.1
	 */
    protected SSLServerSOContainerGroup getServerGroup() {
        return serverGroup;
    }

    protected SSLServerSOContainer createGenericServerContainer(String path, int keepAlive, Map properties) throws ContainerCreateException {
        try {
            return new SSLServerSOContainer(new SOContainerConfig(createGenericServerID(path, properties), createGenericServerProperties(path, properties)), getServerGroup(), path, keepAlive);
        } catch (Exception e) {
            throw new ContainerCreateException("Unexpected exception creating generic server container path=" + path, e);
        }
    }

    protected Map createGenericServerProperties(String path, Map properties) {
        return (properties == null) ? defaultContainerProperties : properties;
    }

    protected ID createGenericServerID(String path, Map properties) throws ContainerCreateException {
        if (//$NON-NLS-1$
        !path.startsWith("/"))
            //$NON-NLS-1$
            throw new ContainerCreateException("Path must start with '/'");
        String serverIDPrefix = createGenericServerIDPrefix();
        return IDFactory.getDefault().createStringID(serverIDPrefix + path);
    }

    private String createGenericServerIDPrefix() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return SSLServerSOContainer.DEFAULT_PROTOCOL + "://" + getHost() + ":" + getPort();
    }

    public ISharedObjectContainer getContainer(String path) {
        return serverGroup.get(path);
    }

    public ISharedObjectContainer removeContainer(String path) {
        return serverGroup.remove(path);
    }

    public void startListening() throws IOException {
        Map lock = serverGroup.getMap();
        synchronized (lock) {
            serverGroup.putOnTheAir();
        }
    }

    public boolean isListening() {
        return serverGroup.isOnTheAir();
    }

    public void stopListening() {
        Map lock = serverGroup.getMap();
        synchronized (lock) {
            serverGroup.takeOffTheAir();
        }
    }

    public void close() {
        Map lock = serverGroup.getMap();
        synchronized (lock) {
            for (Iterator i = lock.keySet().iterator(); i.hasNext(); ) {
                SSLServerSOContainer container = (SSLServerSOContainer) removeContainer((String) i.next());
                removeContainerFromContainerManager(container);
                if (container != null) {
                    container.dispose();
                }
            }
            serverGroup.takeOffTheAir();
        }
    }

    public URI getGroupEndpoint() {
        try {
            return new URI(createGenericServerIDPrefix());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public boolean isSSLTransport() {
        return true;
    }
}
