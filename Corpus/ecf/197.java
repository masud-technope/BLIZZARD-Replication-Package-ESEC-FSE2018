/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core;

/**
 * Container manager listener. Instances of this interface may be registered via
 * calls to {@link IContainerManager#addListener(IContainerManagerListener)}.
 * When subsequent additions to the {@link IContainerManager} occur, the
 * {@link #containerAdded(IContainer)} method will be called. When container
 * removals occur, {@link #containerRemoved(IContainer)}. Note that these
 * methods will be called by arbitrary threads.
 * 
 */
public interface IContainerManagerListener {

    /**
	 * Container added to the implementing IContainerManager.
	 * 
	 * @param container
	 *            the {@link IContainer} added. Will not be <code>null</code>.
	 */
    public void containerAdded(IContainer container);

    /**
	 * Container removed from the implementing IContainerManager.
	 * 
	 * @param container
	 *            the {@link IContainer} removed. Will not be <code>null</code>.
	 */
    public void containerRemoved(IContainer container);
}
