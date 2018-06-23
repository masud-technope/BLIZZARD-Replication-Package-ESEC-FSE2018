/*******************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.events;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;

/**
 * Event delivered to an IChannelListener when the parent container of a channel
 * connects successfully to a target ID via the {@link IContainer#connect(ID, org.eclipse.ecf.core.security.IConnectContext) connect(ID, IConnectContext)} method.
 */
public interface IChannelConnectEvent extends IChannelEvent {

    /**
	 * Get ID of target IContainer that connected.
	 * 
	 * @return ID of IContainer that has connected. Will not be <code>null</code>.
	 */
    public ID getTargetID();
}
