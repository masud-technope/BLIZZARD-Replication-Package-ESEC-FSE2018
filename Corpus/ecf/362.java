/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core;

import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;

/**
 * Container factory contract {@link ContainerFactory} for default
 * implementation.
 */
public interface IContainerFactory {

    /**
	 * Add a ContainerTypeDescription to the set of known ContainerDescriptions.
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to add to this factory. Must not
	 *            be <code>null</code>.
	 * @return ContainerTypeDescription the old description of the same name,
	 *         null if none found
	 */
    public ContainerTypeDescription addDescription(ContainerTypeDescription containerTypeDescription);

    /**
	 * Get a collection of the ContainerDescriptions currently known to this
	 * factory. This allows clients to query the factory to determine what if
	 * any other ContainerDescriptions are currently registered with the
	 * factory, and if so, what they are.
	 * 
	 * @return List of ContainerTypeDescription instances
	 */
    public List getDescriptions();

    /**
	 * Check to see if a given named description is already contained by this
	 * factory
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to look for
	 * @return true if description is already known to factory, false otherwise
	 */
    public boolean containsDescription(ContainerTypeDescription containerTypeDescription);

    /**
	 * Get the known ContainerTypeDescription given it's name.
	 * 
	 * @param containerTypeDescriptionName
	 *            the name to use as key to find ContainerTypeDescription.  Must not be <code>null</code>.
	 * @return ContainerTypeDescription found. Null if not found.
	 */
    public ContainerTypeDescription getDescriptionByName(String containerTypeDescriptionName);

    /**
	 * Remove given description from set known to this factory.
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to remove
	 * @return the removed ContainerTypeDescription, null if nothing removed
	 */
    public ContainerTypeDescription removeDescription(ContainerTypeDescription containerTypeDescription);

    /**
	 * Get container type descriptions that support the given containerAdapter
	 * 
	 * @param containerAdapter the container adapter.  Must not be null.
	 * @return ContainerTypeDescription[] of descriptions that support the given container adapter.  If no 
	 * ContainerTypeDescriptions found that support the given adapter, an empty array will be returned.
	 */
    public ContainerTypeDescription[] getDescriptionsForContainerAdapter(Class containerAdapter);

    /**
	 * Make a base IContainer instance.
	 * 
	 * @return IContainer instance.  A non-<code>null</code> instance will be returned.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer() throws ContainerCreateException;

    /**
	 * Create a new container.
	 * 
	 * @param containerID the container's new ID.  Must not be <code>null</code>.
	 * @return IContainer instance.  A non-<code>null</code>. instance will be returned.
	 * @throws ContainerCreateException if some problem creating a base IContainer instance.
	 */
    public IContainer createContainer(ID containerID) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use. Must not be <code>null</code>.
	 * @return a valid instance of IContainer. Will not be <code>null</code>.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup. Must not be <code>null</code>.
	 * @return a valid instance of IContainer. Will not be <code>null</code>.
	 * @throws ContainerCreateException if cannot create container of given name
	 */
    public IContainer createContainer(String containerTypeDescriptionName) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance. Must not be <code>null</code>.
	 * @param parameters
	 *            an Object [] of parameters passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer. A non-<code>null</code> instance will be returned.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup. Must not be <code>null</code>.
	 * @param parameters
	 *            the Object [] of parameters passed to the
	 *            IContainerInstantiator.createInstance method.  May be <code>null</code>.
	 * @return a valid instance of IContainer. Will not be <code>null</code>.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(String containerTypeDescriptionName, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to use to create the instance.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to use to create the instance.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            an Object [] of parameters passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            an Object [] of parameters passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param containerID the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            an Object [] of parameters passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup. Must not be <code>null</code>.
	 * @param containerID the new container's id.  May be <code>null</code>.
	 * @param parameters
	 *            the Object [] of parameters passed to the
	 *            IContainerInstantiator.createInstance method.  May be <code>null</code>.
	 * @return a valid instance of IContainer. Will not be <code>null</code>.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID, Object[] parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to lookup.  Must not be <code>null</code>.
	 * @param containerID the new container's id.  May be <code>null</code>.
	 * @return a valid instance of IContainer.  Will not be <code>null</code>.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup.  Must not be <code>null</code>.
	 * @param containerID the new container's id.  May be <code>null</code>.
	 * @return a valid instance of IContainer.  Will not be <code>null</code>.
	 * @throws ContainerCreateException if some problem creating the instance.
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param containerID the container's new ID.  Must not be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID, Map parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId, Map parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup.  Must not be <code>null</code>.
	 * @param containerID the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID, Map parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup.  Must not be <code>null</code>.
	 * @param containerId the container's new ID.  May be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId, Map parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescription
	 *            the ContainerTypeDescription to use to create the instance.  Must not be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, Map parameters) throws ContainerCreateException;

    /**
	 * Create a new container. 
	 * 
	 * @param containerTypeDescriptionName
	 *            the ContainerTypeDescription name to lookup.  Must not be <code>null</code>.
	 * @param parameters
	 *            a Map of parameters (name/value pairs) passed to the createInstance method
	 *            of the IContainerInstantiator. May be <code>null</code>.
	 * @return a valid instance of IContainer
	 * @throws ContainerCreateException if some problem creating the instance.
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, Map parameters) throws ContainerCreateException;
}
