/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.events;

import org.eclipse.ecf.core.sharedobject.ISharedObjectConnector;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;

/**
 * Shared object manager connection event. Instances implementing this interface
 * are sent to IContainerListeners when the
 * {@link ISharedObjectManager#connectSharedObjects(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.identity.ID[])}
 * or
 * {@link ISharedObjectManager#disconnectSharedObjects(ISharedObjectConnector)}
 * is called.
 * 
 */
public interface ISharedObjectManagerConnectionEvent extends ISharedObjectManagerEvent {

    /**
	 * Get the {@link ISharedObjectConnector} associated with this event
	 * 
	 * @return ISharedObjectConnector associated with this new connection
	 */
    public ISharedObjectConnector getConnector();
}
