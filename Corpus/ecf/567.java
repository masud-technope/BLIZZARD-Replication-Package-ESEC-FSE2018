/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IAbstractChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;

/**
 * MergeableChannel container entry point interface. This interface is an
 * adapter to allow providers to expose mergeable channels to clients. It may be
 * used in the following way:
 * <p>
 * 
 * <pre>
 *   IMergeableChannelContainerAdapter channelcontainer = (IMergeableChannelContainerAdapter) container.getAdapter(IMergeableChannelContainerAdapter.class);
 *   if (channelcontainer != null) {
 *      // use channelcontainer
 *      ...
 *   } else {
 *      // container does not support channel container functionality
 *   }
 * </pre>
 * 
 */
public interface IMergeableChannelContainerAdapter extends IAbstractChannelContainerAdapter {

    /**
	 * Create a mergeable channel within this container
	 * 
	 * @param channelID
	 *            the ID of the mergeable channel created. Should not be
	 *            <code>null</code>.
	 * @param listener
	 *            the channel listener associated with this channel to receive
	 *            asynchronous events. If <code>null</code>, no events will
	 *            be delivered.
	 * @param properties
	 *            a Map of properties to provide to the channel. May be
	 *            <code>null</code>.
	 * @return IMergeableChannel the mergeable channel created. Will not be
	 *         <code>null</code>.
	 * @throws ECFException
	 *             thrown if mergeable channel cannot be created
	 */
    public IMergeableChannel createMergeableChannel(ID channelID, IChannelListener listener, Map properties) throws ECFException;
}
