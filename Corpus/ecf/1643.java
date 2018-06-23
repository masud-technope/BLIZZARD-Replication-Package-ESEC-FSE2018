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

import java.net.InetAddress;
import java.util.Map;
import org.eclipse.ecf.core.IContainer;

/**
 * Generic server container group factory service.  This service interface defines
 * the contract for dynamically creating ECF generic server container groups for a given hostname and port.  A container group
 * is a set of {@link IContainer} instances...all of which are associated with a single
 * hostname and port combination.
 * 
 * @since 4.0
 */
public interface IGenericServerContainerGroupFactory {

    public int DEFAULT_PORT = 3282;

    /**
	 * @since 6.0
	 */
    public int DEFAULT_SECURE_PORT = 4282;

    //$NON-NLS-1$
    public static final String SSLTRANSPORT_CONTAINER_PROP = "org.eclipse.ecf.server.generic.containerProp.sslTransport";

    /**
	 * Create a new container group given a hostname, port, and a Map of default container properties.
	 * @param hostname the hostname associated with the new container group.  Must not be <code>null</code>.
	 * @param port the port that the new container group will listen on (once {@link IGenericServerContainerGroup#startListening()}
	 * is called).  Should be a valid tcp port, openable for listening by this process via {@link IGenericServerContainerGroup#startListening()}.
	 * @param bindAddress an InetAddress specifying what the resulting container group will bind the ServerSocket to when {@link IGenericServerContainerGroup#startListening()}
	 * is called.   May be <code>null</code>.  If <code>null</code> then the ServerSocket binding will occur for any/all available addresses
	 * @param defaultContainerProperties a Map of default properties passed to any IContainer instances created within the resulting group.
	 * @return new generic server container group.  Will not return <code>null</code>.
	 * @throws GenericServerContainerGroupCreateException if a container group exists for the given hostname and port combination.
	 * 
	 * @see IGenericServerContainerGroup
	 * @since 7.0
	 */
    public IGenericServerContainerGroup createContainerGroup(String hostname, int port, InetAddress bindAddress, Map defaultContainerProperties) throws GenericServerContainerGroupCreateException;

    /**
	 * Create a new container group given a hostname, port, and a Map of default container properties.
	 * @param hostname the hostname associated with the new container group.  Must not be <code>null</code>.
	 * @param port the port that the new container group will listen on (once {@link IGenericServerContainerGroup#startListening()}
	 * is called).  Should be a valid tcp port, openable for listening by this process via {@link IGenericServerContainerGroup#startListening()}.
	 * @param defaultContainerProperties a Map of default properties passed to any IContainer instances created within the resulting group.
	 * @return new generic server container group.  Will not return <code>null</code>.
	 * @throws GenericServerContainerGroupCreateException if a container group exists for the given hostname and port combination.
	 * 
	 * @see IGenericServerContainerGroup
	 */
    public IGenericServerContainerGroup createContainerGroup(String hostname, int port, Map defaultContainerProperties) throws GenericServerContainerGroupCreateException;

    /**
	 * Create a new container group given a hostname, and port.
	 * @param hostname the hostname associated with the new container group.  Must not be <code>null</code>.
	 * @param port the port that the new container group will listen on (once {@link IGenericServerContainerGroup#startListening()}
	 * is called).  Should be a valid tcp port, openable for listening by this process via {@link IGenericServerContainerGroup#startListening()}.
	 * @return new generic server container group.  Will not return <code>null</code>.
	 * @throws GenericServerContainerGroupCreateException if a container group exists for the given hostname and port combination.
	 * 
	 * @see IGenericServerContainerGroup
	 */
    public IGenericServerContainerGroup createContainerGroup(String hostname, int port) throws GenericServerContainerGroupCreateException;

    /**
	 * Create a new container group given a hostname using the {@link #DEFAULT_PORT}.
	 * @param hostname the hostname associated with the new container group.  Must not be <code>null</code>.
	 * @return new generic server container group.  Will not return <code>null</code>.
	 * @throws GenericServerContainerGroupCreateException if a container group exists for the given hostname and port combination.
	 * 
	 * @see IGenericServerContainerGroup
	 */
    public IGenericServerContainerGroup createContainerGroup(String hostname) throws GenericServerContainerGroupCreateException;

    /**
	 * Get the container group associated with the given hostname and port.
	 * @param hostname the hostname associated with the new container group.  Must not be <code>null</code>.
	 * @param port the port of the desired container group.
	 * @return the existing generic server container group associated with the given hostname and port.  If no container group
	 * exists with the given hostname and port, <code>null</code> will be returned.
	 * 
	 */
    public IGenericServerContainerGroup getContainerGroup(String hostname, int port);

    /**
	 * Get all the container groups created by this container group factory.
	 * @return array of generic server container groups.  Will not return <code>null</code>, but 
	 * may return empty array if no generic server container groups have been created by this factory.
	 */
    public IGenericServerContainerGroup[] getContainerGroups();

    /**
	 * Remove the container group with the given hostname and port.
	 * 
	 * @param hostname the hostname of the container group to remove.  Must not be <code>null</code>.
	 * @param port the port of the desired container group.
	 * @return generic server container group removed.  If no container group exists for this factory, then
	 * nothing was actually removed, and <code>null</code> will be returned.  
	 * 
	 * @see #getContainerGroup(String, int)
	 */
    public IGenericServerContainerGroup removeContainerGroup(String hostname, int port);
}
