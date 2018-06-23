/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * Channel configuration to be used during createChannel to configure the newly
 * created IChannel implementation
 * 
 */
public interface IChannelConfig extends IAdaptable, IIdentifiable {

    /**
	 * Get listener for channel being created. Typically, provider will call
	 * this method during the implementation of createChannel. If this method
	 * returns a non-<code>null</code> IChannelListener instance, the newly
	 * created channel must notify the given listener when channel events occur.
	 * If this method returns <code>null</code>, then no listener will be
	 * notified of channel events
	 * 
	 * @return IChannelListener to use for notification of received channel
	 *         events. If <code>null</code>, then no listener will be
	 *         notified of channel events.
	 * 
	 */
    public IChannelListener getListener();

    /**
	 * Get properties for new channel creation
	 * 
	 * @return Map with properties for new channel
	 */
    public Map getProperties();
}
