/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;

/**
 * Generic server container group.  Instances of this type are returned from using the {@link IGenericServerContainerGroupFactory}
 * service.
 * 
 * @since 4.0
 */
public interface IGenericServerContainerGroup {

    public static final int DEFAULT_KEEPALIVE = 30000;

    /**
	 * Get the URI for the group endpoint.  For
	 * @return uri that is the group endpoint.  For example uri='ecftcp://localhost:3282'
	 */
    public URI getGroupEndpoint();

    /**
	 * Create a shared object container within this container group, given a path, a keepAlive value, and a Map of properties
	 * that will be provided to the created container.
	 * 
	 * @param path the uri path suffix for defining the container's identity.  For example, if {@link #getGroupEndpoint()}
	 * returns 'ecftcp://localhost:3282', and a path of '/server' is used to create a new container, then the container's id
	 * will be 'ecftcp://localhost:3282/server'.  Must not be <code>null</code>.
	 * @param keepAlive a value (in milliseconds) that defines the keepAlive for the resulting container.
	 * @param properties to be associated to the returned container upon instantiation.
	 * @return shared object container.  Will not be <code>null</code>.
	 * @throws ContainerCreateException if container with given path, keepAlive, and properties could not be created.
	 */
    public ISharedObjectContainer createContainer(String path, int keepAlive, Map properties) throws ContainerCreateException;

    /**
	 * Create a shared object container within this container group, given a path, a keepAlive value, and a Map of properties
	 * that will be provided to the created container.
	 * 
	 * @param path the uri path suffix for defining the container's identity.  For example, if {@link #getGroupEndpoint()}
	 * returns 'ecftcp://localhost:3282', and a path of '/server' is used to create a new container, then the container's id
	 * will be 'ecftcp://localhost:3282/server'.  Must not be <code>null</code>.
	 * @param keepAlive a value (in milliseconds) that defines the keepAlive for the resulting container.
	 * @return shared object container.  Will not be <code>null</code>.
	 * @throws ContainerCreateException if container with given path, keepAlive, and properties could not be created.
	 * @since 5.0
	 */
    public ISharedObjectContainer createContainer(String path, int keepAlive) throws ContainerCreateException;

    /**
	 * Create a shared object container within this container group, given a path, a keepAlive value, and a Map of properties
	 * that will be provided to the created container.
	 * 
	 * @param path the uri path suffix for defining the container's identity.  For example, if {@link #getGroupEndpoint()}
	 * returns 'ecftcp://localhost:3282', and a path of '/server' is used to create a new container, then the container's id
	 * will be 'ecftcp://localhost:3282/server'.  Must not be <code>null</code>.
	 * @return shared object container.  Will not be <code>null</code>.
	 * @throws ContainerCreateException if container with given path, keepAlive, and properties could not be created.
	 * @since 5.0
	 */
    public ISharedObjectContainer createContainer(String path) throws ContainerCreateException;

    /**
	 * Get the container instance associated with the given path. 
	 * 
	 * @param path of the container to return.  Must not be <code>null</code>.
	 * @return the previously created shared object container associated with the given path.
	 */
    public ISharedObjectContainer getContainer(String path);

    /**
	 * Get a map (String->ISharedObjectContainer) of the path->containers previously created.
	 * @return map of the path->container map for this generic server container group.  Will not return <code>null</code>, but
	 * may return a Map of size==0.
	 */
    public Map getContainers();

    /**
	 * Remove previously created container with given path.
	 * 
	 * @param path of the shared object container to remove.
	 * @return shared object container removed.  If no shared object container was previously created
	 * with the given path, then <code>null</code> will be returned.
	 */
    public ISharedObjectContainer removeContainer(String path);

    /**
	 * Start listening on the port given to this generic server container group upon creation.
	 * 
	 * @throws IOException if the server port cannot be opened for listening.
	 * 
	 * @see IGenericServerContainerGroupFactory#createContainerGroup(String, int, Map)
	 */
    public void startListening() throws IOException;

    /**
	 * Returns <code>true</code> if this container group previously started listening via a successful call to {@link #startListening()}).
	 * 
	 * @return <code>true</code> if currently listening, <code>false</code> otherwise.
	 */
    public boolean isListening();

    /**
	 * Stop listening on port given to this generic server container group upon creation.
	 */
    public void stopListening();

    /**
	 * Close this generic server container group.  This method will:
	 * <ol>
	 * <li>iterate through all containers created via {@link #createContainer(String, int, Map)} within this group, and for each one:</li>
	 * <ol>
	 *   <li>remove it from the set of containers within this group</li>
	 *   <li>call {@link IContainer#dispose()}</li>
	 * </ol>
	 * <li>stop listening on the port given to this generic server container group upon creation.</li>
	 * </ol>
	 */
    public void close();

    /**
	 * @since 6.0
	 */
    public boolean isSSLTransport();
}
