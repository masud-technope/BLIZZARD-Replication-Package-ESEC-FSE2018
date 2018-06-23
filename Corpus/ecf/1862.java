/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.provider;

import org.eclipse.ecf.core.*;

/**
 * Interface that must be implemented by ECF provider implementations.
 * 
 */
public interface IContainerInstantiator {

    /**
	 * Create instance of IContainer. This is the interface that container
	 * provider implementations must implement for the containerFactory
	 * extension point. The caller may optionally specify both argument types
	 * and arguments that will be passed into this method (and therefore to the
	 * provider implementation implementing this method). For example:
	 * <p>
	 * <b> ContainerFactory.getDefault().createContainer("foocontainer",new
	 * Object { "hello" });</b>
	 * <p>
	 * @param description
	 *            the ContainerTypeDescription associated with the registered
	 *            container provider implementation
	 * @param parameters
	 *            parameters specified by the caller. May be null if no
	 *            parameters are passed in by caller to
	 *            ContainerFactory.getDefault().createContainer(...)
	 * @return IContainer instance. The provider implementation must return a
	 *         valid object implementing IContainer OR throw a
	 *         ContainerCreateException. Null will not be returned.
	 * @throws ContainerCreateException thrown if instance cannot be created
	 */
    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException;

    /**
	 * Get array of supported adapters for the given container type description.
	 * Providers implement this method to allow clients to inspect the adapter
	 * types exposed by the container described by the given description.
	 * 
	 * The returned array entries will be the fully qualified names of the
	 * adapter classes.
	 * 
	 * Note that the returned types do not guarantee that a subsequent call to
	 * {@link IContainer#getAdapter(Class)} with the same type name as a
	 * returned value will return a non-<code>null</code> result. In other
	 * words, even if the class name is in the returned array, subsequent calls
	 * to {@link IContainer#getAdapter(Class)} may still return
	 * <code>null</code>.
	 * 
	 * @param description
	 *            the ContainerTypeDescription to report adapters for. Must not
	 *            be <code>null</code>.
	 * @return String[] of supported adapters. The entries in the returned array
	 *         will be the fully qualified class names of adapters supported by
	 *         the given description. <code>null</code> may be returned by
	 *         the provider if no adapters are supported for this description.
	 */
    public String[] getSupportedAdapterTypes(ContainerTypeDescription description);

    /**
	 * Get array of parameter types for given container type description.
	 * Providers implement this method to allow clients to inspect the available
	 * set of parameter types understood for calls to
	 * {@link #createInstance(ContainerTypeDescription, Object[])}.
	 * <p>
	 * Each of the rows of the returned array specifies a Class[] of parameter
	 * types. These parameter types correspond to the types of Object[] that can
	 * be passed into the second parameter of
	 * {@link #createInstance(ContainerTypeDescription, Object[])}.
	 * <p>
	 * Consider the following example:
	 * <pre>
	 * public Class[][] getSupportedParameterTypes() {
	 * 	return new Class[][] { { String.class }, { String.class, String.class } };
	 * }
	 * </pre>
	 * 
	 * The above means that there are two acceptable values for the Object []
	 * passed into {@link #createInstance(ContainerTypeDescription, Object[])}:
	 * 1) a single String, and 2) two Strings. These would therefore be
	 * acceptable as input to createInstance:
	 * 
	 * <pre>
	 * IContainer container = ContainerFactory.getDefault().createContainer(
	 * 		description, new Object[] { &quot;Hello&quot; });
	 * 
	 * IContainer container2 = ContainerFactory.getDefault().createContainer(
	 * 		description, new Object[] { &quot;Hello&quot; });
	 * </pre>
	 * <p>
	 * @param description
	 *            the ContainerTypeDescription to return parameter types for
	 * @return Class[][] array of Class[]s. Each row in the table corresponds to
	 *         a Class[] that describes the types of Objects in Object[] for
	 *         second parameter to
	 *         {@link #createInstance(ContainerTypeDescription, Object[])}.
	 *         <code>null</code> returned if no parameter types supported for
	 *         given description.
	 */
    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description);

    public String[] getSupportedIntents(ContainerTypeDescription description);
}
