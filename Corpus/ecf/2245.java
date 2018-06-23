/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.provider;

import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectCreateException;
import org.eclipse.ecf.core.sharedobject.SharedObjectTypeDescription;

/**
 * Interface that must be implemented by extensions of the sharedObjectFactory
 * extension point
 * 
 */
public interface ISharedObjectInstantiator {

    /**
	 * Create instance of ISharedObject. This is the interface that plugin
	 * implementations must implement for the sharedObjectFactory extension
	 * point. The caller may optionally specify both argument types and
	 * arguments that will be passed into this method (and therefore to the
	 * provider implementation implementing this method). For example:
	 * <p>
	 * </p>
	 * <p>
	 * <b>
	 * SharedObjectFactory.getDefault().createSharedObject("foosharedobject",new
	 * String [] { java.lang.String }, new Object { "hello" });</b>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param typeDescription
	 *            the SharedObjectTypeDescription associated with the registered
	 *            shared object provider implementation plugin
	 * @param args
	 *            arguments specified by the caller. May be null if no arguments
	 *            are passed in by caller to
	 *            SharedObjectFactory.getDefault().createSharedObject(...)
	 * @return ISharedObject instance. The provider implementation must return a
	 *         valid object implementing ISharedObject OR throw a
	 *         SharedObjectCreateException
	 * @throws SharedObjectCreateException
	 *             if shared object instance cannot be created
	 */
    public ISharedObject createInstance(SharedObjectTypeDescription typeDescription, Object[] args) throws SharedObjectCreateException;
}
