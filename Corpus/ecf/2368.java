/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import org.eclipse.ecf.core.IReliableContainer;
import org.eclipse.ecf.core.sharedobject.util.ISharedObjectMessageSerializer;

/**
 * Core interface that must be implemented by all ECF container instances.
 * Instances are typically created via {@link SharedObjectContainerFactory}
 * 
 * @see ISharedObject
 * @see ISharedObjectManager
 */
public interface ISharedObjectContainer extends IReliableContainer {

    /**
	 * Get SharedObjectManager for this container
	 * 
	 * @return ISharedObjectManager for this container instance
	 */
    public ISharedObjectManager getSharedObjectManager();

    /**
	 * <p>
	 * Set the shared object message serializer.  The serializer should not be <code>null</code>.
	 * </p>
	 * <p>
	 * Note that users of this method should guarantee that their are not pending messages
	 * to be sent/received...typically by <b>only</b> calling this method prior to connecting/being
	 * connected to from remote containers.
	 * </p>
	 * <p>
	 * Note also that the serializer must perform well when it's methods are called, to prevent
	 * negatively impacting overall container messaging performance.
	 * </p>
	 * @since 2.0
	 */
    public void setSharedObjectMessageSerializer(ISharedObjectMessageSerializer serializer);
}
