/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;

/**
 * Factory contract for {@link SharedObjectContainerFactory}
 * 
 */
public interface ISharedObjectContainerFactory {

    /**
	 * Make ISharedObjectContainer instance.
	 * 
	 * @param desc
	 *            the ContainerTypeDescription to use to create the instance
	 * @param args
	 *            an Object [] of arguments passed to the createInstance method
	 *            of the IContainerInstantiator
	 * @return a valid instance of ISharedObjectContainer
	 * @throws ContainerCreateException
	 */
    public ISharedObjectContainer createSharedObjectContainer(ContainerTypeDescription desc, Object[] args) throws ContainerCreateException;

    /**
	 * Make ISharedObjectContainer instance.
	 * 
	 * @param descriptionName
	 *            the ContainerTypeDescription name to lookup
	 * @return a valid instance of ISharedObjectContainer
	 * @throws ContainerCreateException
	 */
    public ISharedObjectContainer createSharedObjectContainer(String descriptionName) throws ContainerCreateException;

    /**
	 * Make ISharedObjectContainer instance.
	 * 
	 * @param descriptionName
	 *            the ContainerTypeDescription name to lookup
	 * @param args
	 *            the Object [] of arguments passed to the
	 *            IContainerInstantiator.createInstance method
	 * @return a valid instance of ISharedObjectContainer
	 * @throws ContainerCreateException
	 */
    public ISharedObjectContainer createSharedObjectContainer(String descriptionName, Object[] args) throws ContainerCreateException;
}
