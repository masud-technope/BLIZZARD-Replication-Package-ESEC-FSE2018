/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * Super interface for {@link IChannelContainerAdapter}. Defines basic
 * semantics for IChannelContainerAdapters.
 * 
 */
public interface IAbstractChannelContainerAdapter extends IAdaptable {

    /**
	 * Add listener for IChannelContainerAdapter events.
	 * 
	 * @param listener
	 *            to be added. Must not be <code>null</code>.
	 */
    public void addListener(IChannelContainerListener listener);

    /**
	 * Remove listener for IChannelContainerAdapter events
	 * 
	 * @param listener
	 *            to be removed. Must not be <code>null</code>.
	 */
    public void removeListener(IChannelContainerListener listener);

    /**
	 * Get expected Namespace for channel ID creation
	 * 
	 * @return Namespace that can be used to create channel ID instances for
	 *         this channel container. Will not be <code>null</code>.
	 */
    public Namespace getChannelNamespace();

    /**
	 * Get IChannel with given channelID.
	 * 
	 * @param channelID
	 *            the ID of the channel to get. Must not be <code>null</code>.
	 * @return IChannel of channel within container with given ID. Returns
	 *         <code>null</code> if channel not found.
	 */
    public IChannel getChannel(ID channelID);

    /**
	 * Remove channel with given ID.  If the channel is found in the container, the 
	 * enclosing container will call {@link IChannel#dispose()} to dispose the channel 
	 * before returning from this method.
	 * 
	 * @param channelID
	 *            the ID of the channel to remove within this container. Must
	 *            not be <code>null</code>.
	 * @return true if channel found and Removed. False if channel not found
	 *         within container.
	 */
    public boolean removeChannel(ID channelID);
}
